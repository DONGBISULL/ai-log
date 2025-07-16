package com.demo.ailog.processor.consumer.mapper;


import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RawLogMapper {

    RawLogEntity toEntity(LogParseDTO rawLogEntity);

}
