package com.parking.ai.rag.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Value("${qdrant.host}")
    private String qdrantHost;

    @Value("${qdrant.api-key:}")
    private String qdrantApiKey;

    @Value("${qdrant.tls:false}")
    private boolean qdrantTls;

    @Value("${qdrant.collection-name}")
    private String collectionName;

    public String retrieveContext(String query) {
        try {
            EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName(embeddingModelName)
                    .build();

            Embedding queryEmbedding;
            try {
                queryEmbedding = embeddingModel.embed(query).content();
                log.info("임베딩 완료: 차원={}, 모델={}", queryEmbedding == null ? "null" : queryEmbedding.vector().length, embeddingModelName);
            } catch (Exception e) {
                log.warn("임베딩 API 호출 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                return "";
            }

            if (queryEmbedding == null || queryEmbedding.vector().length == 0) {
                log.warn("임베딩 결과가 비어있습니다. 모델: {}", embeddingModelName);
                return "";
            }

            return searchViaRest(queryEmbedding.vector());

        } catch (Exception e) {
            log.warn("RAG 검색 실패: {}", e.getMessage());
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private String searchViaRest(float[] vector) {
        try {
            String scheme = qdrantTls ? "https" : "http";
            String baseUrl = String.format("%s://%s:6333", scheme, qdrantHost);

            List<Float> vectorList = new ArrayList<>(vector.length);
            for (float v : vector) {
                vectorList.add(v);
            }

            Map<String, Object> searchRequest = Map.of(
                    "vector", vectorList,
                    "limit", 3,
                    "with_payload", true
            );

            WebClient client = WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("api-key", qdrantApiKey)
                    .build();

            Map<?, ?> response = client.post()
                    .uri("/collections/" + collectionName + "/points/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<?, ?>> results = (List<Map<?, ?>>) response.get("result");
            if (results == null || results.isEmpty()) {
                log.info("RAG 검색 결과 없음");
                return "";
            }

            log.info("RAG 검색 완료: {}개 결과", results.size());

            return results.stream()
                    .map(r -> {
                        Map<?, ?> payload = (Map<?, ?>) r.get("payload");
                        Object text = payload.get("text_segment");
                        if (text == null) text = payload.get("text");
                        return text != null ? text.toString() : "";
                    })
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.joining("\n\n---\n\n"));

        } catch (Exception e) {
            log.warn("Qdrant REST 검색 실패: {}", e.getMessage());
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
