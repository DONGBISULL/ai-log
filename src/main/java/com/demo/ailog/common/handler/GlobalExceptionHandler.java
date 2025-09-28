package com.demo.ailog.common.handler;

import com.demo.ailog.common.exception.LLMResponseException;
import com.demo.ailog.common.exception.ParsingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<String> handleParsingException() {
        return new ResponseEntity<>("error Message", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LLMResponseException.class)
    public ResponseEntity<String> handleLLMResponseException() {
        return new ResponseEntity<>("error Message", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
