package com.demo.ailog.common.exception;

public class LLMResponseException extends RuntimeException {

    public LLMResponseException(String message) {
        super(message);
    }

    public LLMResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}
