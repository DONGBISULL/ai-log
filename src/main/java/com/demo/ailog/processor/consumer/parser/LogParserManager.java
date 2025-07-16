package com.demo.ailog.processor.consumer.parser;

import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.common.enums.LogLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class LogParserManager {

    private final ObjectMapper objectMapper;

    public LogParseDTO parse(String appType, String rawLog) throws JsonProcessingException, NoSuchAlgorithmException {
        LogParseDTO target = null;
        JsonNode jsonNode = objectMapper.readTree(rawLog);

        if (jsonNode != null) {
            String message = jsonNode.path("message").asText();
            LogLevel logLevel = LogLevelExtractor.extractLogLevel(message);

            HashMap<String, Object> metadataMap = new HashMap<>();
            metadataMap.put("agent", jsonNode.get("agent"));
            metadataMap.put("host", jsonNode.get("host"));
            metadataMap.put("@metadata", jsonNode.get("@metadata"));
            metadataMap.put("log", jsonNode.get("log"));
            String metadata = objectMapper.writeValueAsString(metadataMap);

            Instant logTime = Instant.parse(jsonNode.get("@timestamp").asText());
            target = LogParseDTO.builder()
                    .timestamp(LocalDateTime.ofInstant(logTime, ZoneId.of("Asia/Seoul")))
                    .hostname(jsonNode.get("hostname").asText())
                    .sourceFile(jsonNode.path("log")
                            .path("file")
                            .path("path")
                            .asText()
                    )
                    .logHash(LogHashGenerator.generateLogHash(message))
                    .message(message)
                    .appType(appType)
                    .metadata(metadata)
                    .logLevel(logLevel)
                    .build();
        }

        return target;
    }

    public ErrorAnalysisDTO parse(String jsonLog) {
        try {
            jsonLog = extractPureJson(jsonLog);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            return objectMapper.readValue(jsonLog, ErrorAnalysisDTO.class);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            // 기본값 반환 또는 예외 발생
            return createDefaultErrorAnalysis();
        }
    }

    public String extractPureJson(String raw) {
        String cleaned = raw
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)```\\s*$", "")
                .trim();

        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "")
                    .trim();
        }
        return cleaned;
    }


    private ErrorAnalysisDTO createDefaultErrorAnalysis() {
        return ErrorAnalysisDTO.builder()
                .errorCategory("UNKNOWN")
                .errorType("PARSING_FAILED")
                .errorSummary("JSON 파싱 실패")
                .normalizedPattern("UNKNOWN")
                .rootCause("UNKNOWN")
                .technicalDetails(ErrorAnalysisDTO.TechnicalDetailsDTO.builder()
                        .primaryIdentifier("UNKNOWN")
                        .locationInfo("UNKNOWN")
                        .contextInfo("UNKNOWN")
                        .systemLayer("UNKNOWN")
                        .build())
                .affectedComponent("UNKNOWN")
                .fixApproach("UNKNOWN")
                .build();
    }

}
