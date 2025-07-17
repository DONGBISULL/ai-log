package com.demo.ailog.processor.schedular;

import com.demo.ailog.processor.consumer.entity.FailedTask;
import com.demo.ailog.processor.consumer.service.FailedTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedTaskRetryScheduler {

    private final FailedTaskService service;

    @Scheduled(fixedDelay = 1000 * 60 * 2) // 2분마다 실행
    public void retryFailedTasks() {
        List<FailedTask> retryableTasks = service.getRetryableTasks();
        log.info("Retrying tasks  {} ", retryableTasks.size());
        for (FailedTask task : retryableTasks) {
            try {
                log.info("Retrying failed task: taskId={}, logId={}, taskType={}",
                        task.getId(), task.getLogId(), task.getTaskType());

                boolean success = service.retryTask(task);

                if (success) {
                    service.updateStatusComplete(task.getId());
                    log.info("Failed task completed successfully: taskId={}", task.getId());
                } else {
                    service.incrementRetryCount(task.getId(), "Task execution failed");
                }

            } catch (Exception e) {
                log.error("Error during task retry: taskId={}", task.getId(), e);
                service.incrementRetryCount(task.getId(), e.getMessage());
            }
        }
    }

}
