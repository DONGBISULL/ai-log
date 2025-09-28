package com.demo.ailog.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper ObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 타입 처리를 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        // 알 수 없는 JSON 속성이 있어도 파싱 실패하지 않음
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 빈 배열을 null로 처리
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        // 빈 문자열을 null로 처리
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // null 값을 가진 필드는 JSON 직렬화에서 제외
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Map에서 null 값을 가진 엔트리는 직렬화하지 않음
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // 알 수 없는 enum 값은 null로 처리
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        // 단일 값을 배열로 처리 가능하도록 설정 (예: "value" -> ["value"])
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // 날짜/시간 타임스탬프를 나노초 단위로 읽지 않음
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }

    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

}
