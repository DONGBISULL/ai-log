package com.demo.ailog.processor.consumer.mapper;

import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.ErrorAnalysis;
import com.demo.ailog.processor.consumer.entity.LogTechnicalDetails;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-16T11:50:07+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class ErrorAnalysisMapperImpl implements ErrorAnalysisMapper {

    @Override
    public ErrorAnalysis toEntity(ErrorAnalysisDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ErrorAnalysis errorAnalysis = new ErrorAnalysis();

        errorAnalysis.setId( dto.getId() );
        errorAnalysis.setRawLogId( dto.getRawLogId() );
        errorAnalysis.setServiceName( dto.getServiceName() );
        errorAnalysis.setErrorCategory( dto.getErrorCategory() );
        errorAnalysis.setErrorType( dto.getErrorType() );
        errorAnalysis.setErrorSummary( dto.getErrorSummary() );
        errorAnalysis.setNormalizedPattern( dto.getNormalizedPattern() );
        errorAnalysis.setPatternHash( dto.getPatternHash() );
        errorAnalysis.setRootCause( dto.getRootCause() );
        errorAnalysis.setTechnicalDetails( technicalDetailsDTOToLogTechnicalDetails( dto.getTechnicalDetails() ) );
        errorAnalysis.setAffectedComponent( dto.getAffectedComponent() );
        errorAnalysis.setFixApproach( dto.getFixApproach() );
        errorAnalysis.setCreatedAt( dto.getCreatedAt() );

        return errorAnalysis;
    }

    @Override
    public ErrorAnalysisDTO toDTO(ErrorAnalysis entity) {
        if ( entity == null ) {
            return null;
        }

        ErrorAnalysisDTO.ErrorAnalysisDTOBuilder errorAnalysisDTO = ErrorAnalysisDTO.builder();

        errorAnalysisDTO.id( entity.getId() );
        errorAnalysisDTO.rawLogId( entity.getRawLogId() );
        errorAnalysisDTO.serviceName( entity.getServiceName() );
        errorAnalysisDTO.errorCategory( entity.getErrorCategory() );
        errorAnalysisDTO.errorType( entity.getErrorType() );
        errorAnalysisDTO.errorSummary( entity.getErrorSummary() );
        errorAnalysisDTO.normalizedPattern( entity.getNormalizedPattern() );
        errorAnalysisDTO.patternHash( entity.getPatternHash() );
        errorAnalysisDTO.rootCause( entity.getRootCause() );
        errorAnalysisDTO.affectedComponent( entity.getAffectedComponent() );
        errorAnalysisDTO.fixApproach( entity.getFixApproach() );
        errorAnalysisDTO.technicalDetails( logTechnicalDetailsToTechnicalDetailsDTO( entity.getTechnicalDetails() ) );
        errorAnalysisDTO.createdAt( entity.getCreatedAt() );

        return errorAnalysisDTO.build();
    }

    protected LogTechnicalDetails technicalDetailsDTOToLogTechnicalDetails(ErrorAnalysisDTO.TechnicalDetailsDTO technicalDetailsDTO) {
        if ( technicalDetailsDTO == null ) {
            return null;
        }

        LogTechnicalDetails logTechnicalDetails = new LogTechnicalDetails();

        logTechnicalDetails.setPrimaryIdentifier( technicalDetailsDTO.getPrimaryIdentifier() );
        logTechnicalDetails.setLocationInfo( technicalDetailsDTO.getLocationInfo() );
        logTechnicalDetails.setContextInfo( technicalDetailsDTO.getContextInfo() );
        logTechnicalDetails.setSystemLayer( technicalDetailsDTO.getSystemLayer() );

        return logTechnicalDetails;
    }

    protected ErrorAnalysisDTO.TechnicalDetailsDTO logTechnicalDetailsToTechnicalDetailsDTO(LogTechnicalDetails logTechnicalDetails) {
        if ( logTechnicalDetails == null ) {
            return null;
        }

        ErrorAnalysisDTO.TechnicalDetailsDTO.TechnicalDetailsDTOBuilder technicalDetailsDTO = ErrorAnalysisDTO.TechnicalDetailsDTO.builder();

        technicalDetailsDTO.primaryIdentifier( logTechnicalDetails.getPrimaryIdentifier() );
        technicalDetailsDTO.locationInfo( logTechnicalDetails.getLocationInfo() );
        technicalDetailsDTO.contextInfo( logTechnicalDetails.getContextInfo() );
        technicalDetailsDTO.systemLayer( logTechnicalDetails.getSystemLayer() );

        return technicalDetailsDTO.build();
    }
}
