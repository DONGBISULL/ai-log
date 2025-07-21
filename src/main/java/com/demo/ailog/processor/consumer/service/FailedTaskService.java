package com.demo.ailog.processor.consumer.service;

import com.demo.ailog.analogy.service.AnalogyService;
import com.demo.ailog.processor.consumer.domain.ErrorAnalysisDTO;
import com.demo.ailog.processor.consumer.entity.FailedTask;
import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.common.enums.TaskStatus;
import com.demo.ailog.common.enums.TaskType;
import com.demo.ailog.processor.consumer.repository.FailedTaskRepository;
import com.demo.ailog.processor.consumer.repository.RawLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedTaskService {

    private final FailedTaskRepository repository;

    private final RawLogRepository logRepository;

    private final AnalogyService analogyService;

    /**
     * 실패 로직
     * - 재시도 프로세스를 태우기 위한 기록 용도
     * - 트랜잭션 에러 나지 않도록 매번 새로 생성할 수 있도록 수정
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveFailedTask(Long logId, TaskType taskType, String errorMessage) {
        FailedTask failedTask = FailedTask.builder()
                .rawLogId(logId)
                .taskType(taskType)
                .retryCount(0)
                .maxRetries(3)
                .errorMessage(errorMessage)
                .createdAt(LocalDateTime.now())
                .nextRetryAt(LocalDateTime.now().plusMinutes(5)) // 5분 후 재시도
                .status(TaskStatus.PENDING)
                .build();

        repository.save(failedTask);
        log.warn("Failed task saved: logId={}, taskType={}, error={}",
                logId, taskType, errorMessage);
    }

    public List<FailedTask> getRetryableTasks() {
        LocalDateTime target = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0);
        return repository.findByStatusAndNextRetryAtBefore(
                TaskStatus.PENDING, target);
    }

    @Transactional
    public void incrementRetryCount(Long taskId, String errorMessage) {
        FailedTask task = repository.findById(taskId).orElseThrow();
        task.setRetryCount(task.getRetryCount() + 1);
        task.setErrorMessage(errorMessage);

        if (task.getRetryCount() >= task.getMaxRetries()) {
            task.setStatus(TaskStatus.FAILED);
        } else {
            int delayMinutes = 5 * (int) Math.pow(2, task.getRetryCount() - 1);
            task.setNextRetryAt(LocalDateTime.now().plusMinutes(delayMinutes));
        }

        repository.save(task);
    }

    @Transactional
    public void updateStatusComplete(Long taskId) {
        repository.updateStatus(taskId, TaskStatus.COMPLETED);
    }

    /**
     * 실패한 테스트 처리 프로세스
     * - AI 요약 처리
     */
    @Transactional
    public boolean retryTask(FailedTask task) {
        RawLogEntity target = logRepository.findById(task.getRawLogId()).orElse(null);
        if (target == null) {
            log.warn("Log not found for task retry: rwaLogId={}", task.getRawLogId());
            return false;
        }
        try {
            switch (task.getTaskType()) {
                case AI_SUMMARY:
                    analogyService.analysisErrors(target);
                    return true;
                case SLACK_NOTIFICATION:
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Task execution failed: taskType={}, logId={}",
                    task.getTaskType(), task.getRawLogId(), e);
            return false;
        }
    }

}
