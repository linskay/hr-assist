package com.example.hr_assistant.model.dto;

import lombok.Data;

/**
 * Запрос heartbeat от клиента
 */
@Data
public class HeartbeatRequest {
    
    private Long timestamp;
    private String userAgent;
    private Integer windowWidth;
    private Integer windowHeight;
    private Boolean isFullscreen;
    private Boolean isVisible;
    private String browserEvents;
}
