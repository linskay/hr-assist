package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с логами аудита
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByInterviewId(Long interviewId);
    
    List<AuditLog> findByUserId(Long userId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.eventType = :eventType")
    List<AuditLog> findByEventType(@Param("eventType") String eventType);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);
}
