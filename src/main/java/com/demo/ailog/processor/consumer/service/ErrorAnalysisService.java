package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.ErrorAnalysis;
import com.demo.ailog.processor.consumer.mapper.ErrorAnalysisMapper;
import com.demo.ailog.processor.consumer.repository.ErrorAnalysisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ErrorAnalysisService {

    private final ErrorAnalysisMapper mapper;

    private final ErrorAnalysisRepository repository;

    @Transactional
    public ErrorAnalysisDTO add(ErrorAnalysisDTO dto) {
        ErrorAnalysis entity = mapper.toEntity(dto);
        ErrorAnalysis saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    public boolean existsByRawId(Long id) {
        return repository.existsByRawLogId(id);
    }

    public ErrorAnalysisDTO findByRawId(Long id) {
        return mapper.toDTO(get(id));
    }

    public ErrorAnalysis get(Long id) {
        return repository.findByRawLogId(id).orElseThrow(NoSuchElementException::new);
    }


    public List<ErrorAnalysisDTO> listInRawId(List<Long> rawIds) {
        List<ErrorAnalysis> list = repository.findAllByRawLogIdIn(rawIds);
        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

}
