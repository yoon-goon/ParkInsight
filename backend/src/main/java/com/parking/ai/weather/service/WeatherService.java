package com.parking.ai.weather.service;

import com.parking.ai.weather.dto.WeatherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient webClient;

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.base-url}")
    private String baseUrl;

    @Value("${weather.lat}")
    private double defaultLat;

    @Value("${weather.lon}")
    private double defaultLon;

    public WeatherInfo getCurrentWeather(double lat, double lon) {
        try {
            Map<?, ?> forecast = webClient.get()
                    .uri(baseUrl + "/forecast?lat={lat}&lon={lon}&appid={key}&units=metric&cnt=8&lang=kr",
                            lat, lon, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<?, ?> airPollution = webClient.get()
                    .uri(baseUrl + "/air_pollution?lat={lat}&lon={lon}&appid={key}", lat, lon, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseWeather(forecast, airPollution);
        } catch (Exception e) {
            log.warn("날씨 API 호출 실패, 기본값 사용: {}", e.getMessage());
            return WeatherInfo.builder()
                    .temperature(20.0)
                    .description("날씨 정보 없음")
                    .rainProbability(0)
                    .fineDust("보통")
                    .ultraFineDust("보통")
                    .recommendation("날씨 정보를 가져올 수 없습니다.")
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherInfo parseWeather(Map<?, ?> forecast, Map<?, ?> airPollution) {
        List<Map<?, ?>> list = (List<Map<?, ?>>) forecast.get("list");
        Map<?, ?> current = list.get(0);

        double temp = ((Number) ((Map<?, ?>) current.get("main")).get("temp")).doubleValue();
        String desc = ((List<Map<?, ?>>) current.get("weather")).get(0).get("description").toString();

        double maxPop = list.stream()
                .mapToDouble(item -> item.containsKey("pop") ? ((Number) item.get("pop")).doubleValue() : 0)
                .max().orElse(0);

        String aqi2 = "";
        try {
            Map<?, ?> firstEntry = ((List<Map<?, ?>>) airPollution.get("list")).get(0);
            int aqiVal = ((Number) ((Map<?, ?>) firstEntry.get("main")).get("aqi")).intValue();
            aqi2 = switch (aqiVal) {
                case 1 -> "좋음";
                case 2 -> "보통";
                case 3 -> "나쁨";
                case 4, 5 -> "매우나쁨";
                default -> "보통";
            };
        } catch (Exception e) {
            aqi2 = "보통";
        }

        String recommendation = buildRecommendation(maxPop * 100, aqi2);

        return WeatherInfo.builder()
                .temperature(temp)
                .description(desc)
                .rainProbability(Math.round(maxPop * 100))
                .fineDust(aqi2)
                .ultraFineDust(aqi2)
                .recommendation(recommendation)
                .build();
    }

    private String buildRecommendation(double rainProb, String dust) {
        if (rainProb >= 60) return "24시간 내 비 예보로 세차를 연기하세요.";
        if ("매우나쁨".equals(dust)) return "미세먼지가 매우 나쁩니다. 세차 후 실내 주차를 권장합니다.";
        if ("나쁨".equals(dust)) return "미세먼지가 나쁩니다. 세차 후 즉시 실내 주차하세요.";
        if (rainProb >= 30) return "비 가능성이 있습니다. 세차를 잠시 미루는 것을 권장합니다.";
        return "세차하기 좋은 날씨입니다.";
    }
}
