package com.parking.ai.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.ai.analysis.dto.AnalysisResponse;
import com.parking.ai.analysis.dto.VisionAnalysisResult;
import com.parking.ai.analysis.entity.Analysis;
import com.parking.ai.analysis.repository.AnalysisRepository;
import com.parking.ai.auth.entity.User;
import com.parking.ai.auth.repository.UserRepository;
import com.parking.ai.common.BusinessException;
import com.parking.ai.common.ErrorCode;
import com.parking.ai.rag.service.RagService;
import com.parking.ai.report.service.ReportService;
import com.parking.ai.vehicle.entity.Vehicle;
import com.parking.ai.vehicle.repository.VehicleRepository;
import com.parking.ai.weather.dto.WeatherInfo;
import com.parking.ai.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final GeminiVisionService geminiVisionService;
    private final WeatherService weatherService;
    private final RagService ragService;
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    @Transactional
    public AnalysisResponse analyze(Long userId, Long vehicleId,
                                     MultipartFile front, MultipartFile rear,
                                     MultipartFile left, MultipartFile right) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        String frontUrl = cloudinaryService.upload(front, "front");
        String rearUrl = cloudinaryService.upload(rear, "rear");
        String leftUrl = cloudinaryService.upload(left, "left");
        String rightUrl = cloudinaryService.upload(right, "right");

        VisionAnalysisResult vision = geminiVisionService.analyze(
                List.of(frontUrl, rearUrl, leftUrl, rightUrl));

        WeatherInfo weather = weatherService.getCurrentWeather();
        String weatherJson = serializeWeather(weather);

        String ragQuery = String.format("%s %d년식 오염:%s 스크래치:%s 주차:%s",
                vehicle.getModel(), vehicle.getYear(),
                vision.getContamination(), vision.getScratch(), vision.getAlignment());
        String ragContext = ragService.retrieveContext(ragQuery);

        String report = reportService.generateReport(vehicle, vision, weather, ragContext);

        Analysis analysis = Analysis.builder()
                .vehicle(vehicle)
                .user(user)
                .frontUrl(frontUrl)
                .rearUrl(rearUrl)
                .leftUrl(leftUrl)
                .rightUrl(rightUrl)
                .parkingScore(vision.getParkingScore())
                .washScore(vision.getWashScore())
                .doorDentRisk(vision.getDoorDentRisk())
                .weatherSnapshot(weatherJson)
                .reportText(report)
                .build();

        return AnalysisResponse.from(analysisRepository.save(analysis));
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(Long userId, Long analysisId) {
        return AnalysisResponse.from(
                analysisRepository.findByIdAndUserId(analysisId, userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ANALYSIS_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponse> getHistory(Long userId) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(AnalysisResponse::from).toList();
    }

    private String serializeWeather(WeatherInfo weather) {
        try {
            return objectMapper.writeValueAsString(weather);
        } catch (Exception e) {
            return "{}";
        }
    }
}
