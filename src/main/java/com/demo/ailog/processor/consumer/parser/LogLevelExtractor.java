package com.demo.ailog.processor.consumer.parser;

import com.demo.ailog.common.enums.LogLevel;

public interface LogLevelExtractor {

    LogLevel extractLogLevel(String logLine);

    default boolean isErrorLog(String logLine) {
        return LogLevel.isMonitored(extractLogLevel(logLine));
    }

    String getAppType();

}
