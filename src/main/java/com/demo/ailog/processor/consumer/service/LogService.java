package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.common.enums.LogLevel;
import com.demo.ailog.common.exception.LogPersistenceException;
import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.processor.consumer.mapper.RawLogMapper;
import com.demo.ailog.processor.consumer.repository.RawLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final RawLogMapper mapper;

    private final RawLogRepository repository;

    @Transactional
    public RawLogEntity save(LogParseDTO dto) {
        try {
            RawLogEntity entity = mapper.toEntity(dto);
            return repository.save(entity);
        } catch (Exception e) {
            throw new LogPersistenceException("Failed to save log " + e.getMessage(), dto);
        }
    }

    public List<RawLogEntity> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByTimestampBetween(startDate, endDate);
    }

    public List<RawLogEntity> findTop100ByProcessedFalseAndLogLevelIn(List<LogLevel> logLevels) {
        return repository.findTop100ByProcessedFalseAndLogLevelIn(logLevels);
    }

    @Transactional
    public void updateProcessed(Long id) {
        log.info("========updateProcessedAndSummary========");
        repository.updateProcessed(id); // processed=true, summary 저장
    }

    // 재시도 로직이 포함된 업데이트 메서드 추가
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateProcessedAndSummaryWithRetry(Long id, String summary) {
        try {
            log.info("========updateProcessedAndSummaryWithRetry========");
            repository.updateProcessedAndSummary(id, summary);
        } catch (Exception e) {
            log.error("DB 업데이트 실패, 재시도 중... id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void updateAlerted(Long id) {
        repository.updateAlerted(id); // 재시도 가능
    }
}
