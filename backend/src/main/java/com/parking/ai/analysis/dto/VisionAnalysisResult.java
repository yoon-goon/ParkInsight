package com.parking.ai.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisionAnalysisResult {
    private int parkingScore = 70;
    private int washScore = 70;
    private String doorDentRisk = "MEDIUM";
    private String contamination = "분석 중";
    private String scratch = "분석 중";
    private String alignment = "분석 중";
    private String driverPattern = "분석 중";
}
