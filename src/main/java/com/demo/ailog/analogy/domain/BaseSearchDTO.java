package com.demo.ailog.analogy.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseSearchDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /* 추후 연결 예정 */
    private String serviceId;
    private String modelId;
}
