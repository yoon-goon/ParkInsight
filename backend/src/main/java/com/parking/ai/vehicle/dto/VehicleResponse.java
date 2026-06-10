package com.parking.ai.vehicle.dto;

import com.parking.ai.vehicle.entity.Vehicle;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VehicleResponse {
    private final Long id;
    private final String model;
    private final Integer year;
    private final String color;
    private final Integer mileage;
    private final LocalDateTime createdAt;

    private VehicleResponse(Vehicle v) {
        this.id = v.getId();
        this.model = v.getModel();
        this.year = v.getYear();
        this.color = v.getColor();
        this.mileage = v.getMileage();
        this.createdAt = v.getCreatedAt();
    }

    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(v);
    }
}
