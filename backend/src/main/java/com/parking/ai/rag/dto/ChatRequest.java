package com.parking.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    private Long vehicleId;

    @NotBlank(message = "질문을 입력해주세요.")
    private String question;
}
