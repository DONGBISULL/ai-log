package com.demo.ailog.embed.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
//@Table(name = "vector_log")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class VectorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rawId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)  // 임베딩 차원 수
    private float[] embedding;
}
