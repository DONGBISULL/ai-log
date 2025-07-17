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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    @Value("${log-processing.limit-size}")
    private int limitSize;

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

    public List<RawLogEntity> findByProcessedFalseAndLogLevelIn(List<LogLevel> logLevels) {
        return repository.findByProcessedFalseAndLogLevelIn(logLevels, limitSize);
    }

    @Transactional
    public void updateProcessed(Long id) {
        log.info("========updateProcessed========");
        repository.updateProcessed(id); // processed=true 저장
    }

    @Transactional
    public void updateAlerted(Long id) {
        repository.updateAlerted(id); // 재시도 가능
    }
}
