package com.parking.ai.rag.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final QdrantEmbeddingStore embeddingStore;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.embedding-model}")
    private String embeddingModelName;

    public String retrieveContext(String query) {
        try {
            EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName(embeddingModelName)
                    .build();

            Embedding queryEmbedding;
            try {
                var response = embeddingModel.embed(query);
                queryEmbedding = response.content();
                log.info("임베딩 완료: 차원={}, 모델={}", queryEmbedding == null ? "null" : queryEmbedding.vector().length, embeddingModelName);
            } catch (Exception e) {
                log.warn("임베딩 API 호출 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                return "";
            }

            if (queryEmbedding == null || queryEmbedding.vector().length == 0) {
                log.warn("임베딩 결과가 비어있습니다. 모델: {}", embeddingModelName);
                return "";
            }

            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, 3);

            return matches.stream()
                    .map(m -> m.embedded().text())
                    .collect(Collectors.joining("\n\n---\n\n"));
        } catch (Exception e) {
            log.warn("RAG 검색 실패: {}", e.getMessage());
            return "";
        }
    }

    public EmbeddingModel getEmbeddingModel() {
        return GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(embeddingModelName)
                .build();
    }
}
