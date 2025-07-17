package com.demo.ailog.processor.consumer.domain;

import lombok.Data;

import java.util.List;

@Data
public class GeminiEmbeddingDTO {

    private Embedding embedding;

    @Data
    public static class Embedding {
        private List<Float> values;  // 실제 숫자 벡터
    }

}
