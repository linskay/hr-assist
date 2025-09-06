package com.example.hr_assistant.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос на создание интервью
 */
@Data
public class InterviewCreateRequest {
    
    @NotNull(message = "ID вакансии обязателен")
    private Long vacancyId;
    
    @NotBlank(message = "Имя кандидата обязательно")
    private String candidateName;
    
    @Email(message = "Некорректный формат email")
    private String candidateEmail;
    
    private String candidatePhone;
}
