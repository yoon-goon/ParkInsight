package com.parking.ai.analysis.dto;

import com.parking.ai.analysis.entity.Analysis;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnalysisResponse {
    private final Long id;
    private final Long vehicleId;
    private final String frontUrl;
    private final String rearUrl;
    private final String leftUrl;
    private final String rightUrl;
    private final Integer parkingScore;
    private final Integer washScore;
    private final String doorDentRisk;
    private final String weatherSnapshot;
    private final String reportText;
    private final LocalDateTime createdAt;

    private AnalysisResponse(Analysis a) {
        this.id = a.getId();
        this.vehicleId = a.getVehicle().getId();
        this.frontUrl = a.getFrontUrl();
        this.rearUrl = a.getRearUrl();
        this.leftUrl = a.getLeftUrl();
        this.rightUrl = a.getRightUrl();
        this.parkingScore = a.getParkingScore();
        this.washScore = a.getWashScore();
        this.doorDentRisk = a.getDoorDentRisk();
        this.weatherSnapshot = a.getWeatherSnapshot();
        this.reportText = a.getReportText();
        this.createdAt = a.getCreatedAt();
    }

    public static AnalysisResponse from(Analysis a) {
        return new AnalysisResponse(a);
    }
}
