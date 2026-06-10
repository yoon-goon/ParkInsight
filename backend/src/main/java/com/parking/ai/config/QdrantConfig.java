package com.parking.ai.config;

import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class QdrantConfig {

    @Value("${qdrant.host}")
    private String host;

    @Value("${qdrant.port}")
    private int port;

    @Value("${qdrant.api-key:}")
    private String apiKey;

    @Value("${qdrant.tls:false}")
    private boolean tls;

    @Value("${qdrant.collection-name}")
    private String collectionName;

    @Value("${qdrant.dimension}")
    private int dimension;

    private QdrantGrpcClient buildGrpcClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, tls);
        if (apiKey != null && !apiKey.isBlank()) {
            builder.withApiKey(apiKey);
        }
        return builder.build();
    }

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(buildGrpcClient());
    }

    @Bean
    public QdrantEmbeddingStore qdrantEmbeddingStore(QdrantClient client) {
        return QdrantEmbeddingStore.builder()
                .client(client)
                .collectionName(collectionName)
                .build();
    }

    @PostConstruct
    public void initCollection() {
        try (QdrantClient client = new QdrantClient(buildGrpcClient())) {

            boolean exists = client.listCollectionsAsync().get()
                    .stream().anyMatch(c -> c.equals(collectionName));

            if (!exists) {
                client.createCollectionAsync(collectionName,
                        VectorParams.newBuilder()
                                .setSize(dimension)
                                .setDistance(Distance.Cosine)
                                .build()
                ).get();
                log.info("Qdrant 컬렉션 생성: {}", collectionName);
            }
        } catch (Exception e) {
            log.warn("Qdrant 초기화 실패 (연결 확인 필요): {}", e.getMessage());
        }
    }
}
