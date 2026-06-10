package com.parking.ai.analysis.repository;

import com.parking.ai.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Analysis> findByVehicleIdAndUserIdOrderByCreatedAtDesc(Long vehicleId, Long userId);
    Optional<Analysis> findByIdAndUserId(Long id, Long userId);
}
