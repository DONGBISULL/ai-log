package com.demo.ailog.common.exception;

import com.demo.ailog.processor.consumer.domain.LogParseDTO;

public class LogPersistenceException extends RuntimeException {

    private final LogParseDTO dto;

    public LogPersistenceException(String message, LogParseDTO dto) {
        super(message);
        this.dto = dto;
    }
 
}
