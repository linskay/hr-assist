package com.example.hr_assistant.service.ml;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import com.example.hr_assistant.config.MlModelsConfig;
import com.example.hr_assistant.model.ModelVersion;
import com.example.hr_assistant.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер для загрузки и управления ML моделями
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModelManager {

    private final MlModelsConfig mlModelsConfig;
    private final ModelVersionRepository modelVersionRepository;
    
    private final Map<String, ZooModel<?, ?>> loadedModels = new ConcurrentHashMap<>();
    private final Map<String, Predictor<?, ?>> predictors = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeModels() {
        log.info("Инициализация ML моделей...");
        loadAllModels();
    }

    @PreDestroy
    public void cleanup() {
        log.info("Очистка ресурсов ML моделей...");
        predictors.values().forEach(Predictor::close);
        loadedModels.values().forEach(ZooModel::close);
    }

    /**
     * Загружает все модели из конфигурации
     */
    public void loadAllModels() {
        try {
            // Загружаем модель только если имя файла задано (иначе используем внешний сервис или пропускаем)
            if (mlModelsConfig.getWhisper() != null && !mlModelsConfig.getWhisper().isBlank()) {
                loadModel("whisper", mlModelsConfig.getWhisper());
            } else {
                log.info("Пропускаем загрузку локальной Whisper модели (используется WhisperX сервис)");
            }

            if (mlModelsConfig.getEmbeddings() != null && !mlModelsConfig.getEmbeddings().isBlank()) {
                loadModel("embeddings", mlModelsConfig.getEmbeddings());
            } else {
                log.info("Пропускаем загрузку модели embeddings (используется внешний SBERT сервис)");
            }

            if (mlModelsConfig.getClassifier() != null && !mlModelsConfig.getClassifier().isBlank()) {
                loadModel("classifier", mlModelsConfig.getClassifier());
            } else {
                log.info("Пропускаем загрузку модели classifier (используется внешний LLM/сервис)");
            }

            if (mlModelsConfig.getVoiceVerification() != null && !mlModelsConfig.getVoiceVerification().isBlank()) {
                loadModel("voice-verification", mlModelsConfig.getVoiceVerification());
            } else {
                log.info("Пропускаем загрузку модели voice-verification (используется внешний сервис)");
            }

            if (mlModelsConfig.getFaceRecognition() != null && !mlModelsConfig.getFaceRecognition().isBlank()) {
                loadModel("face-recognition", mlModelsConfig.getFaceRecognition());
            } else {
                log.info("Пропускаем загрузку модели face-recognition (используется внешний видео-антифрод сервис)");
            }

            if (mlModelsConfig.getLiveness() != null && !mlModelsConfig.getLiveness().isBlank()) {
                loadModel("liveness", mlModelsConfig.getLiveness());
            } else {
                log.info("Пропускаем загрузку модели liveness (используется внешний видео-антифрод сервис)");
            }

            if (mlModelsConfig.getAiDetector() != null && !mlModelsConfig.getAiDetector().isBlank()) {
                loadModel("ai-detector", mlModelsConfig.getAiDetector());
            } else {
                log.info("Пропускаем загрузку модели ai-detector (используется DetectGPT сервис)");
            }
            
            log.info("Все модели успешно загружены");
        } catch (Exception e) {
            log.error("Ошибка при загрузке моделей: {}", e.getMessage(), e);
        }
    }

    /**
     * Загружает конкретную модель
     */
    public void loadModel(String modelName, String modelFileName) {
        try {
            Path modelPath = Paths.get(mlModelsConfig.getPath(), modelName, modelFileName);
            
            if (!Files.exists(modelPath)) {
                log.warn("Модель не найдена: {}", modelPath);
                return;
            }

            Criteria<?, ?> criteria = Criteria.builder()
                .setTypes(Object.class, Object.class)
                .optModelPath(modelPath)
                .optEngine("OnnxRuntime")
                .build();

            ZooModel<?, ?> model = ModelZoo.loadModel(criteria);
            loadedModels.put(modelName, model);
            
            // Создаем предиктор для модели
            Predictor<?, ?> predictor = model.newPredictor();
            predictors.put(modelName, predictor);
            
            // Сохраняем информацию о модели в БД
            saveModelVersion(modelName, modelFileName, modelPath.toString());
            
            log.info("Модель {} успешно загружена", modelName);
            
        } catch (Exception e) {
            log.error("Ошибка при загрузке модели {}: {}", modelName, e.getMessage(), e);
        }
    }

    /**
     * Получает предиктор для модели
     */
    @SuppressWarnings("unchecked")
    public <I, O> Predictor<I, O> getPredictor(String modelName) {
        return (Predictor<I, O>) predictors.get(modelName);
    }

    /**
     * Проверяет, загружена ли модель
     */
    public boolean isModelLoaded(String modelName) {
        return loadedModels.containsKey(modelName) && predictors.containsKey(modelName);
    }

    /**
     * Перезагружает модель
     */
    public void reloadModel(String modelName) {
        log.info("Перезагрузка модели: {}", modelName);
        
        // Закрываем старые ресурсы
        Predictor<?, ?> oldPredictor = predictors.remove(modelName);
        if (oldPredictor != null) {
            oldPredictor.close();
        }
        
        ZooModel<?, ?> oldModel = loadedModels.remove(modelName);
        if (oldModel != null) {
            oldModel.close();
        }
        
        // Загружаем модель заново
        String modelFileName = getModelFileName(modelName);
        loadModel(modelName, modelFileName);
    }

    /**
     * Получает имя файла модели по имени модели
     */
    private String getModelFileName(String modelName) {
        return switch (modelName) {
            case "whisper" -> mlModelsConfig.getWhisper();
            case "embeddings" -> mlModelsConfig.getEmbeddings();
            case "classifier" -> mlModelsConfig.getClassifier();
            case "voice-verification" -> mlModelsConfig.getVoiceVerification();
            case "face-recognition" -> mlModelsConfig.getFaceRecognition();
            case "liveness" -> mlModelsConfig.getLiveness();
            case "ai-detector" -> mlModelsConfig.getAiDetector();
            default -> throw new IllegalArgumentException("Неизвестная модель: " + modelName);
        };
    }

    /**
     * Сохраняет информацию о версии модели в БД
     */
    private void saveModelVersion(String modelName, String version, String modelPath) {
        try {
            ModelVersion modelVersion = new ModelVersion();
            modelVersion.setModelName(modelName);
            modelVersion.setVersion(version);
            modelVersion.setModelPath(modelPath);
            modelVersion.setIsActive(true);
            modelVersion.setLoadedAt(java.time.LocalDateTime.now());
            
            // Деактивируем предыдущие версии
            modelVersionRepository.findActiveByModelName(modelName)
                .ifPresent(active -> {
                    active.setIsActive(false);
                    modelVersionRepository.save(active);
                });
            
            modelVersionRepository.save(modelVersion);
            
        } catch (Exception e) {
            log.error("Ошибка при сохранении версии модели {}: {}", modelName, e.getMessage());
        }
    }

    /**
     * Получает статус всех моделей
     */
    public Map<String, Boolean> getModelsStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("whisper", isModelLoaded("whisper"));
        status.put("embeddings", isModelLoaded("embeddings"));
        status.put("classifier", isModelLoaded("classifier"));
        status.put("voice-verification", isModelLoaded("voice-verification"));
        status.put("face-recognition", isModelLoaded("face-recognition"));
        status.put("liveness", isModelLoaded("liveness"));
        status.put("ai-detector", isModelLoaded("ai-detector"));
        return status;
    }
}
