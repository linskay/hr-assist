package com.example.hr_assistant.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Запрос на авторизацию
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @NotBlank(message = "Пароль обязателен")
    private String password;
}
