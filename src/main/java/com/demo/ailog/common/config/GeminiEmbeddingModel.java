package com.demo.ailog.common.config;


import com.demo.ailog.processor.consumer.domain.GeminiEmbeddingDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeminiEmbeddingModel implements EmbeddingModel {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final Integer dimensions;
    private final ObjectMapper objectMapper;

    public GeminiEmbeddingModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.embedding.base-url}") String baseUrl,
            @Value("${spring.ai.openai.embedding.options.model}") String model,
            @Value("${spring.ai.vectorstore.pgvector.dimensions}") Integer dimensions, ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimensions = dimensions;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * 문자열 임배딩하여 반환 값
     */
    @Override
    public float[] embed(String text) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "content", Map.of("parts", List.of(Map.of("text", text))),
                "output_dimensionality", dimensions,
                "taskType", "RETRIEVAL_DOCUMENT"
        );

        GeminiEmbeddingDTO response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:embedContent")
                        .build(model))
                .header("x-goog-api-key", apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error(" >>>>>>>>>{}", errorBody);
                                return Mono.error(new RuntimeException(errorBody));
                            });
                })
                .bodyToMono(GeminiEmbeddingDTO.class)
                .block();

        log.info(" >>>>> {} ", response.getEmbedding());

        List<Float> list = response.getEmbedding().getValues();
        float[] values = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            values[i] = list.get(i);
        }
        return values;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Document의 내용 추출하여 처리
     */
    @Override
    public float[] embed(Document document) {

        if (document == null) {
            log.error("Document가 null입니다");
            throw new IllegalArgumentException("Document가 null입니다");
        }

        String text = document.getText();

        if (text == null || text.trim().isEmpty()) {
            log.error("Document text 없음");
            throw new IllegalArgumentException("Document text 없음");
        }

        Map<String, Object> metadata = document.getMetadata();
        if (metadata != null && metadata.containsKey("filename")) {
            log.debug("임베딩 처리 중: {}", metadata.get("filename"));
        }

        return embed(text);
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = request
                .getInstructions()
                .stream()
                .map(text -> new Embedding(embed(text), 0))
                .collect(Collectors.toList());
        return new EmbeddingResponse(embeddings);
    }

}
