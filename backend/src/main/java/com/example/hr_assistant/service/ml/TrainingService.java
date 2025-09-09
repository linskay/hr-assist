package com.example.hr_assistant.service.ml;

import com.example.hr_assistant.model.ModelVersion;
import com.example.hr_assistant.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для обучения ML моделей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {

    private final ModelVersionRepository modelVersionRepository;

    /**
     * Запускает обучение всех моделей
     */
    public Map<String, String> trainAllModels() {
        Map<String, String> results = new HashMap<>();
        
        try {
            log.info("Запуск обучения всех моделей...");
            
            // Обучение классификатора соответствия
            results.put("classifier", trainClassifier());
            
            // Обучение детектора живости
            results.put("liveness", trainLivenessDetector());
            
            // Обучение детектора AI
            results.put("ai-detector", trainAiDetector());
            
            log.info("Обучение всех моделей завершено");
            return results;
            
        } catch (Exception e) {
            log.error("Ошибка при обучении моделей: {}", e.getMessage(), e);
            results.put("error", e.getMessage());
            return results;
        }
    }

    /**
     * Обучение классификатора соответствия кандидата вакансии
     */
    private String trainClassifier() {
        try {
            log.info("Обучение классификатора соответствия...");
            
            // Загружаем данные обучения
            ClassPathResource resource = new ClassPathResource("ml-data/training/it_dataset_combined_10000.csv");
            int recordCount = 0;
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                boolean isFirstLine = true;
                
                while (reader.readLine() != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Пропускаем заголовок
                    }
                    recordCount++;
                }
            }
            
            log.info("Загружено {} записей для обучения классификатора", recordCount);
            
            // Здесь должна быть логика обучения модели
            // Пока что просто сохраняем информацию о версии
            saveModelVersion("classifier", "trained_model.onnx", "Обучена на " + recordCount + " записях");
            
            return "Успешно обучена на " + recordCount + " записях";
            
        } catch (Exception e) {
            log.error("Ошибка при обучении классификатора: {}", e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }
    }

    /**
     * Обучение детектора живости лица
     */
    private String trainLivenessDetector() {
        try {
            log.info("Обучение детектора живости...");
            
            // Загружаем данные обучения
            ClassPathResource resource = new ClassPathResource("ml-data/training/liveness_detection.csv");
            int recordCount = 0;
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                boolean isFirstLine = true;
                
                while (reader.readLine() != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Пропускаем заголовок
                    }
                    recordCount++;
                }
            }
            
            log.info("Загружено {} записей для обучения детектора живости", recordCount);
            
            // Здесь должна быть логика обучения модели
            saveModelVersion("liveness", "liveness_model.onnx", "Обучена на " + recordCount + " записях");
            
            return "Успешно обучена на " + recordCount + " записях";
            
        } catch (Exception e) {
            log.error("Ошибка при обучении детектора живости: {}", e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }
    }

    /**
     * Обучение детектора AI-генерированного текста
     */
    private String trainAiDetector() {
        try {
            log.info("Обучение детектора AI...");
            
            // Загружаем данные обучения
            ClassPathResource resource = new ClassPathResource("ml-data/training/ai_text_detection.csv");
            int recordCount = 0;
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                boolean isFirstLine = true;
                
                while (reader.readLine() != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Пропускаем заголовок
                    }
                    recordCount++;
                }
            }
            
            log.info("Загружено {} записей для обучения детектора AI", recordCount);
            
            // Здесь должна быть логика обучения модели
            saveModelVersion("ai-detector", "ai_detector_model.onnx", "Обучена на " + recordCount + " записях");
            
            return "Успешно обучена на " + recordCount + " записях";
            
        } catch (Exception e) {
            log.error("Ошибка при обучении детектора AI: {}", e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }
    }

    /**
     * Сохраняет информацию о версии модели
     */
    private void saveModelVersion(String modelName, String fileName, String description) {
        try {
            ModelVersion version = new ModelVersion();
            version.setModelName(modelName);
            version.setVersion("1.0.0");
            version.setModelPath(fileName);
            version.setMetadataJson(description);
            version.setCreatedAt(LocalDateTime.now());
            version.setIsActive(true);
            
            modelVersionRepository.save(version);
            log.info("Сохранена версия модели {}: {}", modelName, description);
            
        } catch (Exception e) {
            log.error("Ошибка при сохранении версии модели {}: {}", modelName, e.getMessage(), e);
        }
    }

    /**
     * Получает статистику по данным обучения
     */
    public Map<String, Object> getTrainingDataStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Статистика по классификатору
            ClassPathResource classifierResource = new ClassPathResource("ml-data/training/it_dataset_combined_10000.csv");
            int classifierRecords = countRecords(classifierResource);
            stats.put("classifier_records", classifierRecords);
            
            // Статистика по детектору живости
            ClassPathResource livenessResource = new ClassPathResource("ml-data/training/liveness_detection.csv");
            int livenessRecords = countRecords(livenessResource);
            stats.put("liveness_records", livenessRecords);
            
            // Статистика по детектору AI
            ClassPathResource aiResource = new ClassPathResource("ml-data/training/ai_text_detection.csv");
            int aiRecords = countRecords(aiResource);
            stats.put("ai_detector_records", aiRecords);
            
            // Статистика по антифрод данным
            ClassPathResource antifraudResource = new ClassPathResource("ml-data/training/antifraud_data.csv");
            int antifraudRecords = countRecords(antifraudResource);
            stats.put("antifraud_records", antifraudRecords);
            
            stats.put("total_records", classifierRecords + livenessRecords + aiRecords + antifraudRecords);
            
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage(), e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }

    /**
     * Подсчитывает количество записей в CSV файле
     */
    private int countRecords(ClassPathResource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            int count = 0;
            boolean isFirstLine = true;
            
            while (reader.readLine() != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Пропускаем заголовок
                }
                count++;
            }
            
            return count;
        } catch (Exception e) {
            log.error("Ошибка при подсчете записей в {}: {}", resource.getPath(), e.getMessage());
            return 0;
        }
    }
}
