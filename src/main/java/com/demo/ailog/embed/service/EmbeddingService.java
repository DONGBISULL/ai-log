package com.demo.ailog.embed.service;

import com.demo.ailog.common.config.GeminiEmbeddingModel;
import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.ErrorAnalysis;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.processor.consumer.service.ErrorAnalysisService;
import com.demo.ailog.processor.consumer.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmbeddingService {

    private final VectorStore vectorStore;

    public void addVectorLog(String message, ErrorAnalysisDTO dto) {
        Document document = new Document(message);
        document.getMetadata().put("rawLogId", dto.getRawLogId());
        document.getMetadata().put("createAt", LocalDateTime.now());
        vectorStore.add(List.of(document));
    }

    public Document getDocument(String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.builder()
                .query(message)
                .topK(2)
                .similarityThreshold(0.95f)
                .build());

        return similarDocuments != null && similarDocuments.size() > 0 ? similarDocuments.get(0) : null;
    }

}
