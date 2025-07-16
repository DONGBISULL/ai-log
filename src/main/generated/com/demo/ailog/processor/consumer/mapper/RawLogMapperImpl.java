package com.demo.ailog.processor.consumer.mapper;

import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-16T11:50:07+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class RawLogMapperImpl implements RawLogMapper {

    @Override
    public RawLogEntity toEntity(LogParseDTO rawLogEntity) {
        if ( rawLogEntity == null ) {
            return null;
        }

        RawLogEntity.RawLogEntityBuilder rawLogEntity1 = RawLogEntity.builder();

        rawLogEntity1.appType( rawLogEntity.getAppType() );
        rawLogEntity1.hostname( rawLogEntity.getHostname() );
        rawLogEntity1.sourceFile( rawLogEntity.getSourceFile() );
        rawLogEntity1.timestamp( rawLogEntity.getTimestamp() );
        rawLogEntity1.logLevel( rawLogEntity.getLogLevel() );
        rawLogEntity1.serviceName( rawLogEntity.getServiceName() );
        rawLogEntity1.message( rawLogEntity.getMessage() );
        rawLogEntity1.logHash( rawLogEntity.getLogHash() );
        rawLogEntity1.metadata( rawLogEntity.getMetadata() );

        return rawLogEntity1.build();
    }
}
