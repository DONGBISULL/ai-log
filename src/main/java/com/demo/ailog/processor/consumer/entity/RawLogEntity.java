package com.demo.ailog.processor.consumer.entity;

import com.demo.ailog.common.enums.LogLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "raw_logs", indexes = {
        @Index(name = "idx_app_type_timestamp", columnList = "appType,timestamp"),
        @Index(name = "idx_log_level", columnList = "logLevel"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class RawLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String appType;           // spring, nginx, postgres 등

    @Column(nullable = false, length = 50)
    private String hostname;

    @Column(nullable = false, length = 500)
    private String sourceFile;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;          // ERROR, WARN, INFO, DEBUG

    @Column(length = 100)
    private String serviceName; // 추후 vm 이든 구분을 위한 키값이 들어가야할듯

    @Column(columnDefinition = "TEXT")
    private String message;           // 실제 로그 메시지

    @Column(columnDefinition = "TEXT")
    private String summary;         // 요약 메세지

    @Column
    private String logHash;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;          // 추가 메타데이터 (JSON 형태)

    @Builder.Default
    @Column(nullable = false)
    private Boolean processed = false; // AI 분석 처리 여부

    /*  알림 발송 여부 */
    /* 추후에 스케줄러로 쪼개서 에러 레벨 높은 항목 처리 추가 */
    @Builder.Default
    @Column(nullable = false)
    private Boolean alerted = false;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;


}
