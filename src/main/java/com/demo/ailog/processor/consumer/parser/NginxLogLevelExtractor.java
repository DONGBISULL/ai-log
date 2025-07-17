package com.demo.ailog.processor.consumer.parser;

import com.demo.ailog.common.enums.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class NginxLogLevelExtractor implements LogLevelExtractor {

    private static final Pattern LEVEL_PATTERN = Pattern.compile(LogLevel.getAllKeywordsRegex(), Pattern.CASE_INSENSITIVE);

    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\[([^\\]]+)\\]", Pattern.CASE_INSENSITIVE);

    // 상태 코드 추출 패턴들
    private static final Pattern NGINX_STATUS_PATTERN = Pattern.compile("^(\\d+\\.?){4} - - \\[.*\\] \\\"(?<method>GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH)\\s(?<path>.*?)\\s(?<protocol>.*?)\\\" (?<statuscode>[1-5][0-9][0-9])");

    @Override
    public LogLevel extractLogLevel(String logLine) {
        if (!StringUtils.hasText(logLine)) {
            return LogLevel.UNKNOWN;
        }
        LogLevel logLevelFromStatus = extractFromStatus(logLine);
        if (logLevelFromStatus != LogLevel.UNKNOWN) return logLevelFromStatus;

        LogLevel levelFromBracket = extractFromBrackets(logLine);
        if (levelFromBracket != LogLevel.UNKNOWN) return levelFromBracket;

        return LogLevel.UNKNOWN;
    }

    private LogLevel extractFromStatus(String logLine) {
        Matcher matcher = NGINX_STATUS_PATTERN.matcher(logLine);
        if (matcher.find()) {
            String statusCode = matcher.group("statuscode");
            System.out.println("Status Code: " + statusCode);
            LogLevel logLevel = LogLevel.fromStatusCode(Integer.parseInt(statusCode));
            log.info(logLevel.toString());
            return LogLevel.fromStatusCode(Integer.parseInt(statusCode));
        } else {
            System.out.println("No match found");
        }

        return LogLevel.UNKNOWN;
    }

    @Override
    public String getAppType() {
        return "nginx";
    }

    private LogLevel extractFromBrackets(String logLine) {
        Matcher matcher = BRACKET_PATTERN.matcher(logLine);

        while (matcher.find()) {
            String content = matcher.group(1);
            if (content != null) {
                LogLevel level = findLevelInText(content);
                if (level != LogLevel.UNKNOWN) return level;
            }
        }

        return LogLevel.UNKNOWN;
    }

    private LogLevel findLevelInText(String content) {
        if (!StringUtils.hasText(content)) {
            return LogLevel.UNKNOWN;
        }
        Matcher matcher = LEVEL_PATTERN.matcher(content);
        if (matcher.find()) {
            String keyword = matcher.group(1);
            if (keyword != null) {
                return LogLevel.fromKeyword(keyword);
            }
        }
        return LogLevel.UNKNOWN;
    }
}
