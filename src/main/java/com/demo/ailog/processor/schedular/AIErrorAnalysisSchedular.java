package com.demo.ailog.processor.schedular;

import com.demo.ailog.analogy.service.AnalogyService;
import com.demo.ailog.common.enums.LogLevel;
import com.demo.ailog.common.enums.TaskType;
import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.processor.consumer.parser.LogHashGenerator;
import com.demo.ailog.processor.consumer.service.ErrorAnalysisService;
import com.demo.ailog.processor.consumer.service.FailedTaskService;
import com.demo.ailog.processor.consumer.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIErrorAnalysisSchedular {

    private final LogService service;

    private final AnalogyService analogyService;

    private final ErrorAnalysisService errorAnalysisService;

    private final FailedTaskService failedTaskService;

    //    @Scheduled(fixedDelay = 60000 * 2)
    public void summarizeLogs() {
        List<RawLogEntity> targets = service.findTop100ByProcessedFalseAndLogLevelIn(
                List.of(LogLevel.ERROR, LogLevel.WARN)
        );
        int successCount = 0;
        int failCount = 0;
        for (RawLogEntity entity : targets) {
            try {
                analogyService.analysisErrors(entity);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failedTaskService.saveFailedTask(entity.getId(), TaskType.AI_SUMMARY, e.getMessage());
            }
        }
        log.info("로그 처리 완료 - 성공: {}, 실패: {}", successCount, failCount);
    }


}
