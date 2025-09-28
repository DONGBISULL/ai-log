package com.demo.ailog.processor.consumer;

import com.demo.ailog.common.exception.ParsingException;
import com.demo.ailog.common.exception.LogPersistenceException;
import com.demo.ailog.processor.consumer.service.LogProcessingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogKafkaListener {

    private final LogProcessingService service;

    /**
     * 카프카에 적재되는 로그 배치 처리
     * - 대용량 로그 처리를 위한 배치 처리 방식
     * - 성능 최적화를 위해 로그 출력 최소화
     */
    @KafkaListener(topics = {"raw-logs.spring", "raw-logs.nginx"}, 
                   containerFactory = "batchKafkaListenerContainerFactory",
                   properties = {
                    "auto.offset.reset=latest",
                })
    @Async("logProcessingExecutor")
    public CompletableFuture<Void> processBatchLogs(
            @Payload List<ConsumerRecord<String, String>> records,
            Acknowledgment ack) {
        
        try {
            log.debug("배치 로그 처리 시작: {} 건", records.size());
            
            // ConsumerRecord를 LogBatchItem으로 변환
            List<LogProcessingService.LogBatchItem> batchItems = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                String topic = record.topic(); // 각 레코드에서 직접 토픽 가져오기
                String[] topicParts = topic.split("\\.");
                String appType = topicParts.length > 1 ? topicParts[1] : "unknown";
                
                batchItems.add(new LogProcessingService.LogBatchItem(
                        null, // 트레이스 ID - 배치에서는 개별 헤더 추출 어려움
                        null, // 스팬 ID
                        appType,
                        record.value()
                ));
            }
            
            // 배치 처리 실행
            LogProcessingService.BatchProcessResult result = service.processBatch(batchItems);
            log.info("배치 로그 처리 완료: {}", result);
            
            ack.acknowledge(); // 배치 처리 완료 후 일괄 커밋
            
        } catch (Exception e) {
            log.error("배치 처리 중 심각한 오류 발생: {}", e.getMessage(), e);
            // 배치 전체 실패 시 재처리를 위해 ack하지 않음
            throw new RuntimeException("배치 처리 실패", e);
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 단일 메시지 처리 (기존 호환성 유지용)
     * 긴급 처리가 필요한 특별한 토픽용
     */
    @KafkaListener(topics = {"urgent-logs"})
    @Transactional
    public void processUrgentLog(ConsumerRecord<String, String> record, 
                                @Header(name = "traceId", required = false) String traceId, 
                                @Header(name = "spanId", required = false) String spanId, 
                                Acknowledgment ack) {
        try {
            String value = record.value();
            String topic = record.topic();
            String[] topicParts = topic.split("\\.");
            String appType = topicParts.length > 1 ? topicParts[1] : "urgent";
            
            log.warn("긴급 로그 수신: topic >> {}", topic);
            service.process(traceId, spanId, appType, value);
            ack.acknowledge();
            
        } catch (ParsingException e) {
            log.error("긴급 로그 파싱 실패: {}", e.getMessage());
            ack.acknowledge();
        } catch (LogPersistenceException e) {
            log.warn("긴급 로그 저장 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("긴급 로그 처리 런타임 오류: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
