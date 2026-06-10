package com.parking.ai.analysis.controller;

import com.parking.ai.analysis.dto.AnalysisResponse;
import com.parking.ai.analysis.service.AnalysisService;
import com.parking.ai.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AnalysisResponse>> analyze(
            @RequestParam("vehicleId") Long vehicleId,
            @RequestParam(value = "lat", defaultValue = "37.5665") double lat,
            @RequestParam(value = "lon", defaultValue = "126.9780") double lon,
            @RequestPart("front") MultipartFile front,
            @RequestPart("rear") MultipartFile rear,
            @RequestPart("left") MultipartFile left,
            @RequestPart("right") MultipartFile right,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(
                analysisService.analyze(userId, vehicleId, lat, lon, front, rear, left, right)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisResponse>> getAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(analysisService.getAnalysis(userId, id)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AnalysisResponse>>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(analysisService.getHistory(userId)));
    }
}
