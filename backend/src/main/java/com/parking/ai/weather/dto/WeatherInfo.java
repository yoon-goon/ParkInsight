package com.parking.ai.weather.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherInfo {
    private double temperature;
    private String description;
    private double rainProbability;
    private String fineDust;
    private String ultraFineDust;
    private String recommendation;
}
