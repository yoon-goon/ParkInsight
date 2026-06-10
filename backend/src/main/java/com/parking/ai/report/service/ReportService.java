package com.parking.ai.report.service;

import com.parking.ai.analysis.dto.VisionAnalysisResult;
import com.parking.ai.vehicle.entity.Vehicle;
import com.parking.ai.weather.dto.WeatherInfo;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.chat-model}")
    private String chatModel;

    public String generateReport(Vehicle vehicle, VisionAnalysisResult vision,
                                  WeatherInfo weather, String ragContext) {
        try {
            GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(chatModel)
                    .build();

            String prompt = buildPrompt(vehicle, vision, weather, ragContext);
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(prompt))
                    .build();
            ChatResponse response = model.chat(request);
            return response.aiMessage().text();
        } catch (Exception e) {
            log.error("리포트 생성 실패: {}", e.getMessage());
            return buildFallbackReport(vehicle, vision, weather);
        }
    }

    private String buildPrompt(Vehicle vehicle, VisionAnalysisResult vision,
                                WeatherInfo weather, String ragContext) {
        return String.format("""
                다음 정보를 바탕으로 차량 관리 리포트를 한국어로 작성해주세요. 500자 이내로 친절하고 실용적으로 작성하세요.

                [차량 정보]
                차종: %s %d년식 / 색상: %s / 주행거리: %,dkm

                [Vision 분석 결과]
                주차 점수: %d점 / 세차 필요도: %d%% / 문콕 위험: %s
                오염도: %s / 스크래치: %s / 주차 정렬: %s / 운전 성향: %s

                [날씨 정보]
                기온: %.1f°C / 날씨: %s / 강수확률: %.0f%% / 미세먼지: %s
                날씨 권고: %s

                [관련 차량 관리 지식]
                %s

                위 정보를 종합하여 현재 차량 상태 요약, 즉각 조치 사항, 세차 및 관리 권고사항을 포함한 리포트를 작성해주세요.
                """,
                vehicle.getModel(), vehicle.getYear(), vehicle.getColor(), vehicle.getMileage(),
                vision.getParkingScore(), vision.getWashScore(), vision.getDoorDentRisk(),
                vision.getContamination(), vision.getScratch(), vision.getAlignment(), vision.getDriverPattern(),
                weather.getTemperature(), weather.getDescription(),
                weather.getRainProbability(), weather.getFineDust(), weather.getRecommendation(),
                ragContext.isEmpty() ? "관련 정보 없음" : ragContext
        );
    }

    private String buildFallbackReport(Vehicle vehicle, VisionAnalysisResult vision, WeatherInfo weather) {
        return String.format(
                "%s %d년식 분석 결과: 주차 점수 %d점, 세차 필요도 %d%%, 문콕 위험 %s. 날씨: %s, 강수확률 %.0f%%. %s",
                vehicle.getModel(), vehicle.getYear(),
                vision.getParkingScore(), vision.getWashScore(), vision.getDoorDentRisk(),
                weather.getDescription(), weather.getRainProbability(), weather.getRecommendation()
        );
    }
}
