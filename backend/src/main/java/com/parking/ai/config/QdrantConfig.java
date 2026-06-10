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

    @Value("${qdrant.collection-name}")
    private String collectionName;

    @Value("${qdrant.dimension}")
    private int dimension;

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(host, port, false).build()
        );
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
        try (QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(host, port, false).build())) {

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
