package com.demo.ailog.processor.consumer.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_analysis")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class ErrorAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long rawLogId;        // Raw 로그 ID 참조

    @Column(length = 100) //
    private String serviceName;

    @Column(nullable = false, length = 50)
    private String errorCategory; // JAVA_ERROR, DB_ERROR, NETWORK_ERROR

    @Column(nullable = false, length = 100)
    private String errorType;     // ARRAY_INDEX_OUT_OF_BOUNDS

    @Column(columnDefinition = "TEXT")
    private String errorSummary;  // LLM 요약

    @Column(columnDefinition = "TEXT")
    private String normalizedPattern; // 정규화된 메시지

    @Column(length = 64)
    private String patternHash;   // 패턴 해시 (중복 체크용)

    @Column(columnDefinition = "TEXT")
    private String rootCause;     // 근본 원인

    @Embedded
    LogTechnicalDetails technicalDetails;

    @Column(columnDefinition = "TEXT")
    private String affectedComponent; // 영향받는_기능_추정

    @Column(columnDefinition = "TEXT")
    private String fixApproach; // 수정_방향_제시

    @Column
    private LocalDateTime analyzedAt = LocalDateTime.now();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

}
