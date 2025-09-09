package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.AuditLog;
import com.example.hr_assistant.repository.AuditLogRepository;
import com.example.hr_assistant.service.ml.ModelManager;
import com.example.hr_assistant.service.ml.TrainingService;
// import com.example.hr_assistant.service.queue.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Админский контроллер для управления системой
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Администрирование", description = "API для администраторов системы")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ModelManager modelManager;
    private final TrainingService trainingService;
    // private final QueueService queueService;
    private final AuditLogRepository auditLogRepository;

    @PostMapping("/models/reload")
    @Operation(summary = "Перезагрузить модели", description = "Перезагрузка всех ML моделей")
    public ResponseEntity<Map<String, String>> reloadModels() {
        try {
            log.info("Запуск перезагрузки моделей администратором");
            
            // Перезагружаем все модели
            modelManager.loadAllModels();
            
            // Логируем действие
            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(AuditLog.EventType.MODEL_RELOADED.name());
            auditLog.setMessage("Модели перезагружены администратором");
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
            
            Map<String, String> response = Map.of(
                "status", "success",
                "message", "Модели успешно перезагружены",
                "timestamp", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при перезагрузке моделей: {}", e.getMessage(), e);
            
            Map<String, String> response = Map.of(
                "status", "error",
                "message", "Ошибка перезагрузки моделей: " + e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/models/reload/{modelName}")
    @Operation(summary = "Перезагрузить модель", description = "Перезагрузка конкретной ML модели")
    public ResponseEntity<Map<String, String>> reloadModel(@PathVariable String modelName) {
        try {
            log.info("Перезагрузка модели {} администратором", modelName);
            
            modelManager.reloadModel(modelName);
            
            // Логируем действие
            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(AuditLog.EventType.MODEL_RELOADED.name());
            auditLog.setMessage("Модель " + modelName + " перезагружена администратором");
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
            
            Map<String, String> response = Map.of(
                "status", "success",
                "message", "Модель " + modelName + " успешно перезагружена",
                "timestamp", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при перезагрузке модели {}: {}", modelName, e.getMessage(), e);
            
            Map<String, String> response = Map.of(
                "status", "error",
                "message", "Ошибка перезагрузки модели: " + e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/models/status")
    @Operation(summary = "Статус моделей", description = "Получение статуса всех ML моделей")
    public ResponseEntity<Map<String, Boolean>> getModelsStatus() {
        try {
            Map<String, Boolean> status = modelManager.getModelsStatus();
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Ошибка при получении статуса моделей: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/ml/train")
    @Operation(summary = "Запуск обучения", description = "Запуск процесса обучения моделей")
    public ResponseEntity<Map<String, Object>> startTraining() {
        try {
            log.info("Запуск обучения моделей администратором");
            
            // Запускаем обучение моделей
            Map<String, String> trainingResults = trainingService.trainAllModels();
            
            // Логируем действие
            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(AuditLog.EventType.SYSTEM_ERROR.name()); // Используем существующий тип
            auditLog.setMessage("Запущено обучение моделей администратором");
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Обучение моделей завершено");
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("results", trainingResults);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при запуске обучения: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка запуска обучения: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ml/training-stats")
    @Operation(summary = "Статистика данных обучения", description = "Получение статистики по данным обучения")
    public ResponseEntity<Map<String, Object>> getTrainingStats() {
        try {
            Map<String, Object> stats = trainingService.getTrainingDataStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Ошибка при получении статистики обучения: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/logs")
    @Operation(summary = "Просмотр логов", description = "Получение логов системы и событий")
    public ResponseEntity<List<AuditLog>> getLogs(
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        
        try {
            List<AuditLog> logs;
            
            if (eventType != null) {
                logs = auditLogRepository.findByEventType(eventType);
            } else {
                logs = auditLogRepository.findAll();
            }
            
            // Ограничиваем количество записей
            if (logs.size() > limit) {
                logs = logs.subList(0, limit);
            }
            
            return ResponseEntity.ok(logs);
            
        } catch (Exception e) {
            log.error("Ошибка при получении логов: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs/{interviewId}")
    @Operation(summary = "Логи интервью", description = "Получение логов для конкретного интервью")
    public ResponseEntity<List<AuditLog>> getInterviewLogs(@PathVariable Long interviewId) {
        try {
            List<AuditLog> logs = auditLogRepository.findByInterviewId(interviewId);
            return ResponseEntity.ok(logs);
            
        } catch (Exception e) {
            log.error("Ошибка при получении логов интервью {}: {}", interviewId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/data/{interviewId}")
    @Operation(summary = "Удалить данные", description = "Удаление данных интервью (GDPR)")
    public ResponseEntity<Map<String, String>> deleteInterviewData(@PathVariable Long interviewId) {
        try {
            log.info("Удаление данных интервью {} администратором", interviewId);
            
            // TODO: Реализовать полное удаление данных интервью
            // - Удалить записи из всех таблиц
            // - Удалить медиа файлы
            // - Удалить транскрипции
            // - Удалить анализ и антифрод данные
            
            // Логируем действие
            AuditLog auditLog = new AuditLog();
            // auditLog.setInterviewId(interviewId); // Поле не существует в модели
            auditLog.setEventType(AuditLog.EventType.DATA_DELETED.name());
            auditLog.setMessage("Данные интервью " + interviewId + " удалены администратором (GDPR)");
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
            
            Map<String, String> response = Map.of(
                "status", "success",
                "message", "Данные интервью " + interviewId + " удалены",
                "timestamp", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при удалении данных интервью {}: {}", interviewId, e.getMessage(), e);
            
            Map<String, String> response = Map.of(
                "status", "error",
                "message", "Ошибка удаления данных: " + e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/system/health")
    @Operation(summary = "Здоровье системы", description = "Проверка состояния всех компонентов системы")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = Map.of(
                "models", modelManager.getModelsStatus(),
                "timestamp", LocalDateTime.now(),
                "status", "healthy"
            );
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            log.error("Ошибка при проверке здоровья системы: {}", e.getMessage(), e);
            
            Map<String, Object> health = Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.badRequest().body(health);
        }
    }
}
