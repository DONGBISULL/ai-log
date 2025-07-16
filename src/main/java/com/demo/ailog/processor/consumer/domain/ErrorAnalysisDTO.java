package com.demo.ailog.processor.consumer.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAnalysisDTO {

    private Long id;
    private Long rawLogId;        // Raw 로그 ID 참조
    private String serviceName;
    private String errorCategory; // JAVA_ERROR, DB_ERROR, NETWORK_ERROR
    private String errorType;     // ARRAY_INDEX_OUT_OF_BOUNDS
    private String errorSummary;  // LLM 요약
    private String normalizedPattern; // 정규화된 메시지
    private String patternHash;   // 패턴 해시 (중복 체크용)
    private String rootCause;     // 근본 원인
    private String affectedComponent; // 영향받는_기술컴포넌트
    private String fixApproach; // 수정_방향_제시

    private TechnicalDetailsDTO technicalDetails;
    private LocalDateTime createdAt;

    private Long errorCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnicalDetailsDTO {
        private String primaryIdentifier;
        private String locationInfo;
        private String contextInfo;
        private String systemLayer;
    }

}
