package com.demo.ailog.processor.consumer.repository;

import com.demo.ailog.processor.consumer.entity.FailedTask;
import com.demo.ailog.common.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FailedTaskRepository extends JpaRepository<FailedTask, Long> {

    List<FailedTask> findByStatusAndNextRetryAtBefore(TaskStatus type, LocalDateTime target);

    @Modifying
    @Query("""
                UPDATE FailedTask t
                SET t.status = :staus 
                WHERE t.id = :id
            """)
    void updateStatus(@Param("id") Long id, @Param("staus") TaskStatus staus);
}
