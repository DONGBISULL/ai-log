package com.demo.ailog.processor.consumer.parser;

import com.demo.ailog.common.enums.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SpringLogLevelExtractor implements LogLevelExtractor {

    private static final Pattern LEVEL_PATTERN = Pattern.compile(LogLevel.getAllKeywordsRegex(), Pattern.CASE_INSENSITIVE);

    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\[([^\\]]+)\\]", Pattern.CASE_INSENSITIVE);

    private static final Pattern COLON_PATTERN = Pattern.compile("\\s*([^:]+):\\s*", Pattern.CASE_INSENSITIVE);

    @Override
    public String getAppType() {
        return "spring";
    }

    @Override
    public LogLevel extractLogLevel(String logLine) {
        if (!StringUtils.hasText(logLine)) {
            return LogLevel.UNKNOWN;
        }

        LogLevel levelFromBracket = extractFromBrackets(logLine);
        if (levelFromBracket != LogLevel.UNKNOWN) return levelFromBracket;

        LogLevel levelFromColons = extractFromColons(logLine);
        if (levelFromColons != LogLevel.UNKNOWN) return levelFromColons;

        return extractFromAllLine(logLine);
    }

    private LogLevel extractFromAllLine(String logLine) {
        return findLevelInText(logLine);
    }

    private LogLevel extractFromColons(String logLine) {
        Matcher matcher = COLON_PATTERN.matcher(logLine);
        while (matcher.find()) {
            String content = matcher.group(1);
            if (content != null) {
                LogLevel level = findLevelInText(content);
                if (level != LogLevel.UNKNOWN) return level;
            }
        }
        return LogLevel.UNKNOWN;
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
