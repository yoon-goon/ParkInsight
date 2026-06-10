package com.parking.ai.rag.controller;

import com.parking.ai.common.ApiResponse;
import com.parking.ai.rag.dto.ChatRequest;
import com.parking.ai.rag.dto.ChatResponse;
import com.parking.ai.rag.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.chat(request)));
    }
}
