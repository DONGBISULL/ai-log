package com.demo.ailog.processor.consumer.repository;

import com.demo.ailog.processor.consumer.entity.RawLogEntity;
import com.demo.ailog.common.enums.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RawLogRepository extends JpaRepository<RawLogEntity, Long> {

    List<RawLogEntity> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(
            value = "SELECT * FROM raw_log WHERE processed = false AND log_level IN (:logLevels) LIMIT :limit",
            nativeQuery = true
    )
    List<RawLogEntity> findByProcessedFalseAndLogLevelIn(@Param("logLevels") List<LogLevel> logLevels, @Param("limit") int limit);

    @Modifying
    @Query("""
                UPDATE RawLogEntity l 
                SET l.processed = true
                WHERE l.id = :id
            """)
    void updateProcessed(@Param("id") Long id);

    @Modifying
    @Query("""
                UPDATE RawLogEntity l 
                SET l.alerted = true 
                WHERE l.id = :id
            """)
    void updateAlerted(@Param("id") Long id);
}
