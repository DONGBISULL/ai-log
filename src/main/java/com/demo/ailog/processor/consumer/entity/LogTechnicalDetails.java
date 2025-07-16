package com.demo.ailog.processor.consumer.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class LogTechnicalDetails {

    /* 주요 식별자 */
    private String primaryIdentifier;
    /* 위치_정보 */
    private String locationInfo;
    /* 컨텍스트_정보 */
    private String contextInfo;
    /* 시스템_레이어 */
    private String systemLayer;

}
