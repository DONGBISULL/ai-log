package com.demo.ailog.common.config;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.pgvector.index-type}")
    private String indexType;

    @Value("${spring.ai.vectorstore.pgvector.distance-type}")
    private String distanceType;

    @Value("${spring.ai.vectorstore.pgvector.dimensions}")
    private Integer dimensions;

    @Value("${spring.ai.vectorstore.pgvector.max-document-batch-size}")
    private Integer maxDocumentBatchSize;

    @Value("${spring.ai.vectorstore.pgvector.initialize-schema}")
    private Boolean initializeSchema;

    @Value("${spring.ai.vectorstore.pgvector.vector-table-name}")
    private String vectorTableName;

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, GeminiEmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(dimensions)                    // Optional: defaults to model dimensions or 1536
                .distanceType(PgVectorStore.PgDistanceType.valueOf(distanceType))  // Optional: defaults to COSINE_DISTANCE
                .indexType(PgVectorStore.PgIndexType.valueOf(indexType))           // Optional: defaults to HNSW
                .initializeSchema(initializeSchema)             // Optional: defaults to false
                .vectorTableName(vectorTableName)      // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(maxDocumentBatchSize) // Optional: defaults to 10000
                .build();
    }

}
