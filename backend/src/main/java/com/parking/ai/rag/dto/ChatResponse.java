package com.parking.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatResponse {
    private String question;
    private String answer;
}
