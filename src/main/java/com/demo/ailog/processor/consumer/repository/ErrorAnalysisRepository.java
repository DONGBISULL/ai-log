package com.demo.ailog.processor.consumer.repository;

import com.demo.ailog.processor.consumer.entity.ErrorAnalysis;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorAnalysisRepository extends JpaRepository<ErrorAnalysis, Long> {

    List<ErrorAnalysis> findAllByRawLogIdIn(List<Long> rawLogIds);

    boolean existsByRawLogId(Long rawLogId);
}
