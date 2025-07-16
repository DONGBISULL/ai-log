package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.common.enums.LogLevel;
import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogProcessingService {

    private final LogService service;

    private final LogParseService parseService;

    @Transactional
    public void process(String traceId, String spanId, String appType, String rawLog) {
        LogParseDTO parsed = parseService.parse(appType, rawLog);
        if (parsed == null || !LogLevel.isMonitored(parsed.getLogLevel())) {
            log.debug("Log filtered out: level={}", parsed.getLogLevel());
            return;
        }
        /* 고객사 ID 추출 및 세팅 */
        parsed.setTraceId(traceId);
        parsed.setSpanId(spanId);
        RawLogEntity saved = service.save(parsed);
    }

}
