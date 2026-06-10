package com.parking.ai.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    DUPLICATE_EMAIL(400, "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),

    // Vehicle
    VEHICLE_NOT_FOUND(404, "차량을 찾을 수 없습니다."),

    // Analysis
    ANALYSIS_NOT_FOUND(404, "분석 결과를 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(500, "이미지 업로드에 실패했습니다."),
    ANALYSIS_FAILED(500, "차량 분석에 실패했습니다."),

    // Common
    INVALID_INPUT(400, "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(500, "서버 오류가 발생했습니다.");

    private final int status;
    private final String message;
}
