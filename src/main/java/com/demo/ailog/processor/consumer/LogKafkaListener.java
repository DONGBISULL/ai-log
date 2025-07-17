package com.demo.ailog.processor.consumer;

import com.demo.ailog.common.exception.LogParsingException;
import com.demo.ailog.common.exception.LogPersistenceException;
import com.demo.ailog.processor.consumer.service.LogProcessingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogKafkaListener {

    private final LogProcessingService service;

    /**
     * 카프카에 적재되는 로그 처리
     * - 에러 로그의 필터링의 경우 파일비츠에서 어떤 로그까지 적재할지 지정해놓는게 나을거같음
     *
     * @param record
     * @param ack    카프카에서 로그 다 읽었다는걸 확인 시키기 위해 사용
     */
    @KafkaListener(topics = {"raw-logs.spring", "raw-logs.nginx"})
    @Transactional
    public void springListen(ConsumerRecord<String, String> record, @Header(name = "traceId", required = false) String traceId, @Header(name = "spanId", required = false) String spanId, Acknowledgment ack) {
        try {
            String value = record.value();
            String topic = record.topic();
            String[] topicParts = topic.split("\\.");
            String appType = topicParts.length > 1 ? topicParts[1] : "unknown";
            log.info("Kafka spring 로그 수신: topic >> {}", topic);
            log.info("Kafka spring 로그 수신: value >>{}", value);
            service.process(traceId, spanId, appType, value);
            ack.acknowledge(); // 정상 처리 후에만 커밋
        } catch (LogParsingException e) {
            // 데이터 자체 문제
            log.error("파싱 실패 (재시도 안함): {}", e.getMessage());
            ack.acknowledge();
        } catch (LogPersistenceException e) {
            log.warn("저장 프로세스 오류 {} ", e.getMessage());
        } catch (Exception e) {
            log.error("런타임  오류 {} ", e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
