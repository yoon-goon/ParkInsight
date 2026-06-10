package com.parking.ai.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VehicleRequest {

    @NotBlank(message = "차종을 입력해주세요.")
    private String model;

    @NotNull(message = "연식을 입력해주세요.")
    private Integer year;

    @NotBlank(message = "색상을 입력해주세요.")
    private String color;

    @NotNull(message = "주행거리를 입력해주세요.")
    @Positive(message = "주행거리는 0보다 커야 합니다.")
    private Integer mileage;
}
