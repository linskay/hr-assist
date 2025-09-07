package com.example.hr_assistant;

import com.example.hr_assistant.config.LlmProperties;
import com.example.hr_assistant.service.ml.ModelManager;
import com.example.hr_assistant.service.storage.MediaStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Главный класс приложения HR Assistant
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class
})
@RestController
@EnableConfigurationProperties(LlmProperties.class)
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class BackendApplication {

    private final ModelManager modelManager;
    private final MediaStorageService mediaStorageService;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "HR Assistant API is running!";
    }

    @Bean
    public CommandLineRunner commandLineRunner(LlmProperties properties) {
        return args -> {
            log.info("============================================================");
            log.info("HR Assistant Backend Application Started");
            log.info("Active LLM Provider: {}", properties.getProvider());
            log.info("OpenAI Model: {}", properties.getOpenai().getModel());
            log.info("============================================================");
            
            // Инициализация компонентов
            try {
                log.info("Инициализация медиа хранилища...");
                mediaStorageService.initializeBucket();
                log.info("MinIO bucket успешно инициализирован");
            } catch (Exception e) {
                log.error("Ошибка при инициализации MinIO bucket: {}", e.getMessage());
                log.warn("Продолжаем работу без MinIO (режим разработки)");
            }
            
            try {
                log.info("Инициализация ML моделей...");
                modelManager.initializeModels();
                log.info("Все ML модели успешно загружены");
            } catch (Exception e) {
                log.error("Ошибка при инициализации ML моделей: {}", e.getMessage());
                log.warn("Продолжаем работу без ML моделей (режим разработки)");
            }
        };
    }
}
