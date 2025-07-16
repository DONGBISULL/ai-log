package com.demo.ailog.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class LogParsingException extends RuntimeException  {

    public LogParsingException(String message) {
        super(message);
    }

}
