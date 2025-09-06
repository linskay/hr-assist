package com.example.hr_assistant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI/Swagger документации
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HR Assistant API")
                .description("API для системы проведения интервью с кандидатами с использованием AI")
                .version("1.0.0")
                .contact(new Contact()
                    .name("HR Assistant Team")
                    .email("support@hr-assistant.com")
                    .url("https://hr-assistant.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api/v1")
                    .description("Локальный сервер разработки"),
                new Server()
                    .url("https://api.hr-assistant.com/api/v1")
                    .description("Продакшн сервер")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT токен авторизации")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
