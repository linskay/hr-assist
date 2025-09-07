package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Лог аудита для отслеживания действий пользователей
 */
@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "meta_json", columnDefinition = "TEXT")
    private String metaJson;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum EventType {
        INTERVIEW_STARTED,
        INTERVIEW_COMPLETED,
        INTERVIEW_CANCELLED,
        MODEL_RELOADED,
        DATA_DELETED,
        USER_LOGIN,
        USER_LOGOUT,
        ACCESS_DENIED,
        FRAUD_DETECTED,
        SYSTEM_ERROR
    }
}
