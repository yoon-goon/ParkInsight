package com.parking.ai.batch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.ai.rag.service.RagService;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("ingest")
@RequiredArgsConstructor
public class RagIngestionRunner implements ApplicationRunner {

    private final RagService ragService;
    private final QdrantEmbeddingStore embeddingStore;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("RAG 문서 ingestion 시작...");

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> documents = mapper.readValue(
                new ClassPathResource("data/rag_documents.json").getInputStream(),
                new TypeReference<>() {}
        );

        EmbeddingModel embeddingModel = ragService.getEmbeddingModel();
        int count = 0;

        for (Map<String, Object> doc : documents) {
            String text = doc.get("text").toString();
            TextSegment segment = TextSegment.from(text, Metadata.from("id", doc.get("id").toString()));
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
            count++;
            log.info("문서 {} / {} 처리 완료", count, documents.size());
        }

        log.info("RAG ingestion 완료: {}개 문서", count);
        System.exit(0);
    }
}
