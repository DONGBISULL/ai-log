package com.demo.ailog.processor.consumer.domain;

import com.demo.ailog.common.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LogParseDTO {

    LocalDateTime timestamp; // 로그 찍힌 시간
    String hostname;
    String sourceFile;
    LogLevel logLevel;      // ERROR, WARN, INFO, DEBUG
    String serviceName;     // 서비스명
    String message;         // 파싱된 메시지 (원본과 다를 수 있음)
    String traceId;         // 분산 추적 ID
    String spanId;          // 스팬 ID
    String appType;
    String metadata;
    String logHash;
    Boolean processed;

    /**
     * 공통 필드 유효성 검증
     */
    public boolean isValid() {
        return timestamp != null &&
                logLevel != null &&
                logLevel != null;
    }
}
