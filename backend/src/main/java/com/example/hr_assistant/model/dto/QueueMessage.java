package com.example.hr_assistant.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * Сообщение для очереди задач
 */
@Data
public class QueueMessage {
    
    private MessageType type;
    private Long interviewId;
    private Long recordingId;
    private String fileUrl;
    private String transcriptText;
    private String taskType;
    private Object data;
    private String notificationType;
    private String message;
    private Integer priority;
    private Long timestamp;
    
    public enum MessageType {
        TRANSCRIPTION,
        ANALYSIS,
        ANTIFRAUD,
        NOTIFICATION,
        TRAINING
    }
}
