package com.parking.ai.vehicle.service;

import com.parking.ai.auth.entity.User;
import com.parking.ai.auth.repository.UserRepository;
import com.parking.ai.common.BusinessException;
import com.parking.ai.common.ErrorCode;
import com.parking.ai.vehicle.dto.VehicleRequest;
import com.parking.ai.vehicle.dto.VehicleResponse;
import com.parking.ai.vehicle.entity.Vehicle;
import com.parking.ai.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehicles(Long userId) {
        return vehicleRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(VehicleResponse::from).toList();
    }

    @Transactional
    public VehicleResponse createVehicle(Long userId, VehicleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .mileage(request.getMileage())
                .build();

        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long userId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));
        vehicleRepository.delete(vehicle);
    }
}
