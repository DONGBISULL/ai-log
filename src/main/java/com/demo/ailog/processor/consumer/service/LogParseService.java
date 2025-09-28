package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.common.exception.ParsingException;
import com.demo.ailog.processor.consumer.domain.LogParseDTO;
import com.demo.ailog.processor.consumer.parser.LogParserManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class LogParseService {

    private final LogParserManager parserManager;

    public LogParseDTO parse(String appType, String rawLog) {
        try {
            return parserManager.parse(appType, rawLog);
        } catch (JsonProcessingException e) {
            throw new ParsingException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new ParsingException(e.getMessage());
        }
    }

}
