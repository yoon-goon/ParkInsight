package com.parking.ai.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.ai.analysis.dto.VisionAnalysisResult;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GeminiVisionService {

    private static final String ANALYSIS_PROMPT = """
            다음 차량 사진들(전면/후면/좌측/우측)을 분석하여 아래 JSON 형식으로만 응답해주세요. JSON 외 다른 텍스트는 포함하지 마세요.
            {
              "parkingScore": (0-100, 주차 상태 점수),
              "washScore": (0-100, 세차 필요도. 높을수록 세차 필요),
              "doorDentRisk": ("HIGH" | "MEDIUM" | "LOW"),
              "contamination": "오염도 한 줄 설명",
              "scratch": "스크래치 한 줄 설명",
              "alignment": "주차 정렬 한 줄 설명",
              "driverPattern": "운전 성향 한 줄 설명"
            }
            """;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.chat-model}")
    private String chatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public VisionAnalysisResult analyze(List<String> imageUrls) {
        try {
            GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(chatModel)
                    .build();

            List<dev.langchain4j.data.message.Content> contents = new ArrayList<>();
            for (String url : imageUrls) {
                contents.add(ImageContent.from(url));
            }
            contents.add(TextContent.from(ANALYSIS_PROMPT));

            UserMessage message = UserMessage.from(contents);
            ChatRequest request = ChatRequest.builder().messages(message).build();
            ChatResponse response = model.chat(request);
            String raw = response.aiMessage().text();

            return objectMapper.readValue(extractJson(raw), VisionAnalysisResult.class);
        } catch (Exception e) {
            log.error("Gemini Vision 분석 실패: {}", e.getMessage());
            return new VisionAnalysisResult();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        return (start >= 0 && end > start) ? text.substring(start, end + 1) : "{}";
    }
}
