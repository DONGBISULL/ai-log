package com.demo.ailog.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum LogLevel {

    TRACE(0, "TRACE", Arrays.asList("trace", "verbose", "debug")),
    DEBUG(1, "DEBUG", Arrays.asList("debug", "dbg")),
    INFO(2, "INFO", Arrays.asList("info", "information", "notice")),
    WARN(3, "WARN", Arrays.asList("warn", "warning", "caution")),
    ERROR(4, "ERROR", Arrays.asList("error", "err", "exception", "fatal")),
    FATAL(5, "FATAL", Arrays.asList("fatal", "critical", "emergency")),
    UNKNOWN(5, "UNKNOWN", Arrays.asList());

    private final int level;
    private final String levelName;
    private final List<String> keywords;

    private static final Map<String, LogLevel> KEYWORD_LEVEL_MAP = new HashMap<>();
    private static final String ALL_KEYWORDS_REGEX;

    static {
        for (LogLevel level : LogLevel.values()) {
            if (level != UNKNOWN) {
                KEYWORD_LEVEL_MAP.put(level.name().toLowerCase(), level);
                for (String keyword : level.keywords) {
                    KEYWORD_LEVEL_MAP.put(keyword.toLowerCase(), level);
                }

            }
        }
        String keywordPattern = KEYWORD_LEVEL_MAP.keySet().stream().map(Pattern::quote).collect(Collectors.joining("|"));
        System.out.println("keywordPattern " + keywordPattern);
        ALL_KEYWORDS_REGEX = "(?:^|\\s|\\]|\\(|\\))+(" + keywordPattern + ")(?=\\s|\\[|\\]|\\(|\\)|$)";
        System.out.println("ALL_KEYWORDS_REGEX " + ALL_KEYWORDS_REGEX);
    }

    public static String getAllKeywordsRegex() {
        return ALL_KEYWORDS_REGEX;
    }

    public static LogLevel fromKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return UNKNOWN;
        }
        keyword = keyword.trim();
//        LogLevel logLevel = KEYWORD_LEVEL_MAP.get(keyword.toLowerCase());
//        System.out.println(KEYWORD_LEVEL_MAP.containsKey("error"));
        return KEYWORD_LEVEL_MAP.getOrDefault(keyword.toLowerCase(), UNKNOWN);
    }

    public static Map<String, LogLevel> getKeywordLevelMap(String rawLog) {
        return new HashMap<>(KEYWORD_LEVEL_MAP);
    }

    public static LogLevel fromLevelName(String levelName) {
        for (LogLevel level : LogLevel.values()) {
            if (level.name().equalsIgnoreCase(levelName)) {
                return level;
            }
        }
        return UNKNOWN;
    }

    public static boolean isMonitored(LogLevel level) {
        return level.getLevel() >= 3;
    }

}
