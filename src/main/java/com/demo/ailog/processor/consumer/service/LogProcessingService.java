package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.common.enums.LogLevel;
import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogProcessingService {

    private final LogService service;

    private final LogParseService parseService;

    /**
     * 단일 로그 처리 (기존 방식)
     */
    @Transactional
    public void process(String traceId, String spanId, String appType, String rawLog) {
        LogParseDTO parsed = parseService.parse(appType, rawLog);
        if (parsed == null || !LogLevel.isMonitored(parsed.getLogLevel())) {
            log.debug("Log filtered out: level={}", parsed != null ? parsed.getLogLevel() : "null");
            return;
        }
        parsed.setTraceId(traceId);
        parsed.setSpanId(spanId);
        RawLogEntity saved = service.save(parsed);
    }

    /**
     * 배치 로그 처리 (대용량 처리용)
     * @param logBatch 처리할 로그 배치 정보 리스트
     * @return 처리 결과 통계
     */
    @Transactional
    public BatchProcessResult processBatch(List<LogBatchItem> logBatch) {
        List<LogParseDTO> validLogs = new ArrayList<>();
        int filteredCount = 0;
        int parseErrorCount = 0;

        // 1단계: 파싱 및 필터링
        for (LogBatchItem item : logBatch) {
            try {
                LogParseDTO parsed = parseService.parse(item.getAppType(), item.getRawLog());
                if (parsed == null || !LogLevel.isMonitored(parsed.getLogLevel())) {
                    filteredCount++;
                    continue;
                }
                parsed.setTraceId(item.getTraceId());
                parsed.setSpanId(item.getSpanId());
                validLogs.add(parsed);
            } catch (Exception e) {
                log.debug("Parse error for log: {}", e.getMessage());
                parseErrorCount++;
            }
        }

        // 2단계: 배치 저장
        int savedCount = 0;
        if (!validLogs.isEmpty()) {
            try {
                savedCount = service.saveBatch(validLogs);
            } catch (Exception e) {
                log.error("Batch save failed: {}", e.getMessage());
                // 실패 시 개별 저장 시도
                for (LogParseDTO logDto : validLogs) {
                    try {
                        service.save(logDto);
                        savedCount++;
                    } catch (Exception ex) {
                        log.debug("Individual save failed: {}", ex.getMessage());
                    }
                }
            }
        }

        return new BatchProcessResult(
                logBatch.size(),
                savedCount,
                filteredCount,
                parseErrorCount,
                logBatch.size() - savedCount - filteredCount - parseErrorCount
        );
    }

    /**
     * 로그 배치 아이템
     */
    public static class LogBatchItem {
        private final String traceId;
        private final String spanId;
        private final String appType;
        private final String rawLog;

        public LogBatchItem(String traceId, String spanId, String appType, String rawLog) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.appType = appType;
            this.rawLog = rawLog;
        }

        public String getTraceId() { return traceId; }
        public String getSpanId() { return spanId; }
        public String getAppType() { return appType; }
        public String getRawLog() { return rawLog; }
    }

    /**
     * 배치 처리 결과
     */
    public static class BatchProcessResult {
        private final int totalCount;
        private final int savedCount;
        private final int filteredCount;
        private final int parseErrorCount;
        private final int saveErrorCount;

        public BatchProcessResult(int totalCount, int savedCount, int filteredCount, 
                                int parseErrorCount, int saveErrorCount) {
            this.totalCount = totalCount;
            this.savedCount = savedCount;
            this.filteredCount = filteredCount;
            this.parseErrorCount = parseErrorCount;
            this.saveErrorCount = saveErrorCount;
        }

        public int getTotalCount() { return totalCount; }
        public int getSavedCount() { return savedCount; }
        public int getFilteredCount() { return filteredCount; }
        public int getParseErrorCount() { return parseErrorCount; }
        public int getSaveErrorCount() { return saveErrorCount; }

        @Override
        public String toString() {
            return String.format("BatchResult[total=%d, saved=%d, filtered=%d, parseError=%d, saveError=%d]",
                    totalCount, savedCount, filteredCount, parseErrorCount, saveErrorCount);
        }
    }
}
