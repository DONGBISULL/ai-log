package com.demo.ailog.processor.consumer.mapper;

import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.ErrorAnalysis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErrorAnalysisMapper {

    ErrorAnalysis toEntity(ErrorAnalysisDTO dto);

    ErrorAnalysisDTO toDTO(ErrorAnalysis entity);

}
