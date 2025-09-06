package com.example.hr_assistant.model.dto;

import lombok.Data;

/**
 * Ответ на загрузку медиа файла
 */
@Data
public class MediaUploadResponse {
    
    private String fileUrl;
    private String presignedUrl;
    private Long fileSize;
    private String contentType;
    private String uploadId;
    private Boolean success;
    private String message;
}
