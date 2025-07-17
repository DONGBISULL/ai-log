package com.demo.ailog.processor.consumer.parser;

import com.demo.ailog.common.enums.LogLevel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LogLevelExtractorManager {

    private final Map<String, LogLevelExtractor> extractors;

    private final List<LogLevelExtractor> fallbackOrder;

    public LogLevelExtractorManager(List<LogLevelExtractor> extractorList) {
        this.extractors = extractorList.stream()
                .collect(Collectors.toMap(
                        LogLevelExtractor::getAppType,
                        extractor -> extractor
                ));

        this.fallbackOrder = Arrays.asList(
                extractors.get("spring"),    // Spring 로그가 가장 일반적
                extractors.get("nginx"),     // 그 다음 Nginx
                extractors.get("postgresql") // 그 외
        ).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public LogLevel extractLogLevel(String appType, String logLine) {
        LogLevelExtractor extractor = extractors.get(appType);
        if (extractor != null) {
            LogLevel result = extractor.extractLogLevel(logLine);
            if (result != LogLevel.UNKNOWN || containsLevelKeyword(logLine)) {
                return result; // 의미있는 결과면 바로 반환
            }
        }
        // 기본 처리기 또는 에러
        return extractWithDefaultExtractor(logLine);
    }

    public boolean isErrorLog(String appType, String logLine) {
        LogLevelExtractor extractor = extractors.get(appType);
        if (extractor != null) {
            return extractor.isErrorLog(logLine);
        }
        return false; // 기본값
    }

    public Set<String> getSupportedAppTypes() {
        return extractors.keySet();
    }

    private LogLevel extractWithDefaultExtractor(String logLine) {
        Optional<LogLevel> target = fallbackOrder.stream().map(extractor -> {
            try {
                return extractor.extractLogLevel(logLine);
            } catch (Exception e) {
                return LogLevel.UNKNOWN;
            }
        }).filter(level -> level != LogLevel.UNKNOWN || containsLevelKeyword(logLine)).findFirst();

        if (target.isPresent()) {
            return target.get();
        }
        return LogLevel.UNKNOWN;
    }

    private boolean containsLevelKeyword(String logLine) {
        // 로그 라인에 실제로 레벨 키워드가 포함되어 있는지 확인
        String upperLine = logLine.toUpperCase();
        return Arrays.stream(LogLevel.values())
                .anyMatch(level -> upperLine.contains(level.name()));
    }
}
