package com.parking.ai.rag.service;

import com.parking.ai.rag.dto.ChatRequest;
import com.parking.ai.rag.dto.ChatResponse;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RagService ragService;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.chat-model}")
    private String chatModel;

    public ChatResponse chat(ChatRequest request) {
        String context = ragService.retrieveContext(request.getQuestion());
        String prompt = buildPrompt(request.getQuestion(), context);

        try {
            GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(chatModel)
                    .build();

            dev.langchain4j.model.chat.request.ChatRequest lc4jRequest =
                    dev.langchain4j.model.chat.request.ChatRequest.builder()
                            .messages(UserMessage.from(prompt))
                            .build();

            String answer = model.chat(lc4jRequest).aiMessage().text();
            return new ChatResponse(request.getQuestion(), answer);
        } catch (Exception e) {
            log.error("채팅 응답 생성 실패: {}", e.getMessage());
            return new ChatResponse(request.getQuestion(),
                    "죄송합니다. 현재 응답을 생성할 수 없습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private String buildPrompt(String question, String context) {
        if (context.isBlank()) {
            return "당신은 차량 관리 전문가 AI입니다. 아래 질문에 한국어로 친절하고 실용적으로 답변해주세요.\n\n질문: " + question;
        }
        return String.format("""
                당신은 차량 관리 전문가 AI입니다. 아래 참고 자료를 활용하여 질문에 한국어로 친절하고 실용적으로 답변해주세요.

                [참고 자료]
                %s

                질문: %s
                """, context, question);
    }
}
