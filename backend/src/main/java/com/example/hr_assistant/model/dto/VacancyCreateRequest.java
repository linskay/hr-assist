package com.example.hr_assistant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * Запрос на создание вакансии
 */
@Data
public class VacancyCreateRequest {
    
    @NotBlank(message = "Название вакансии обязательно")
    private String title;
    
    @NotBlank(message = "Грейд обязателен")
    private String grade;
    
    @NotBlank(message = "Описание обязательно")
    private String description;
    
    @NotNull(message = "Требования обязательны")
    private Map<String, Double> requirements;
}
