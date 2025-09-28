package com.demo.ailog.analogy.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.List;

/**
 * 에러 로그 분석 결과를 담는 DTO
 * 프롬프트 출력 JSON을 이 구조에 그대로 매핑
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorAnalysisReportDTO {

    /** 에러 발생 통계 */
    private StatsSection errorStatistics;

    /** 심각도별 분류 */
    private SeveritySection severityBreakdown;

    /** 패턴 분석 */
    private PatternSection patternAnalysis;

    /** 컴포넌트 건강도 */
    private ComponentHealthSection componentHealth;

    /** 종합 결론 */
    private ConclusionSection conclusion;

    // ---------- 1) 에러 발생 통계 ----------
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsSection {
        /** 총 에러 건수 */
        private Long totalErrors;
        /** 유니크 패턴 수 (patternHash 기준) */
        private Long uniquePatternCount;
        /** 가장 빈번한 에러 Top 5 */
        private List<TopPatternItem> topFrequentErrors;
        /** 컴포넌트별 에러 분포 */
        private List<ComponentDistribution> componentDistributions;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TopPatternItem {
        /** 패턴 해시 */
        private String patternHash;
        /** 발생 건수 */
        private Long occurrences;
        /** 에러 타입 */
        private String errorType;
        /** 예외 클래스 */
        private String exceptionClass;
        /** 에러 요약 */
        private String summary;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ComponentDistribution {
        /** 컴포넌트명 */
        private String component;
        /** 에러 건수 */
        private Long count;
        /** 비율(%) */
        private Double ratioPercent; // null 허용
    }

    // ---------- 2) 심각도별 분류 ----------
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SeveritySection {
        @JsonProperty("CRITICAL")
        private List<FindingItem> CRITICAL;
        @JsonProperty("HIGH")
        private List<FindingItem> HIGH;
        @JsonProperty("MEDIUM")
        private List<FindingItem> MEDIUM;
        @JsonProperty("LOW")
        private List<FindingItem> LOW;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class FindingItem {
        /** 제목 */
        private String title;
        /** 발생 시간대 또는 패턴 */
        private String timePattern;
        /** 에러 타입 */
        private String errorType;
        /** 예외 클래스 */
        private String exceptionClass;
        /** 영향받은 컴포넌트 목록 */
        private List<String> affectedComponents;
        /** 패턴해시 기준 발생 빈도 */
        private Long frequencyByPatternHash;
        /** 심각도 */
        private String severity;
        /** 근본 원인 */
        private String rootCause;
        /** 권장 조치 */
        private String recommendation;
        /** 패턴 해시 */
        private String patternHash;
    }

    // ---------- 3) 패턴 분석 ----------
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PatternSection {
        /** 반복되는 패턴 */
        private List<PatternTrendItem> recurringPatterns;
        /** 새로운 패턴 */
        private List<PatternTrendItem> newPatterns;
        /** 급증하는 패턴 */
        private List<PatternTrendItem> surgingPatterns;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PatternTrendItem {
        /** 패턴 해시 */
        private String patternHash;
        /** 에러 타입 */
        private String errorType;
        /** 예외 클래스 */
        private String exceptionClass;
        /** 발생 건수 */
        private Long count;
        /** 증감율(%) */
        private Double changeRatePercent;
        /** 해당 패턴이 발생한 컴포넌트 목록 */
        private List<String> components;
    }

    // ---------- 4) 컴포넌트 건강도 ----------
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ComponentHealthSection {
        /** 컴포넌트별 에러율 */
        private List<ComponentErrorRate> errorRates;
        /** 문제가 있는 컴포넌트 목록 */
        private List<String> problematicComponents;
        /** 안정적인 컴포넌트 목록 */
        private List<String> stableComponents;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ComponentErrorRate {
        /** 컴포넌트명 */
        private String component;
        /** 에러율 */
        private Double errorRate; // null 허용
        /** 에러 건수 */
        private Long errorCount;
        /** 전체 요청 수 */
        private Long totalRequests; // null 허용
    }

    // ---------- 5) 종합 결론 ----------
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConclusionSection {
        /** 시스템 건강 상태 */
        private String systemHealth;
        /** 개발팀 우선순위 항목 */
        private List<String> devPriorities;
        /** 운영팀 모니터링 항목 */
        private List<String> opsMonitoring;
        /** 즉시 대응 필요 여부 */
        private Boolean immediateAction;
    }
}