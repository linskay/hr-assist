package com.example.hr_assistant.service.ml;

 
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import com.example.hr_assistant.config.MlModelsConfig;
import com.example.hr_assistant.model.ModelVersion;
import com.example.hr_assistant.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
@ConditionalOnProperty(name = "ml.init.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ModelManager {

    private final MlModelsConfig mlModelsConfig;
    private final ModelVersionRepository modelVersionRepository;
    
    private final Map<String, ZooModel<?, ?>> loadedModels = new ConcurrentHashMap<>();
    private final Map<String, Predictor<?, ?>> predictors = new ConcurrentHashMap<>();

    @Value("${ml.init.enabled:true}")
    private boolean mlInitEnabled;

    @PostConstruct
    public void initializeModels() {
        if (!mlInitEnabled) {
            log.info("Инициализация ML моделей отключена свойством ml.init.enabled=false");
            return;
        }
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
            loadModel("whisper", mlModelsConfig.getWhisper());
            loadModel("embeddings", mlModelsConfig.getEmbeddings());
            loadModel("classifier", mlModelsConfig.getClassifier());
            loadModel("voice-verification", mlModelsConfig.getVoiceVerification());
            loadModel("face-recognition", mlModelsConfig.getFaceRecognition());
            loadModel("liveness", mlModelsConfig.getLiveness());
            loadModel("ai-detector", mlModelsConfig.getAiDetector());
            
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
            Path basePath = Paths.get(mlModelsConfig.getPath());
            Path modelPath = basePath.resolve(Paths.get(modelName, modelFileName));
            // Fallback: файл может лежать прямо в корне каталога моделей
            if (!Files.exists(modelPath)) {
                Path fallbackPath = basePath.resolve(modelFileName);
                if (Files.exists(fallbackPath)) {
                    modelPath = fallbackPath;
                } else {
                    log.warn("Модель не найдена: {} или {}", modelPath, fallbackPath);
                    return;
                }
            }

            Criteria<?, ?> criteria = Criteria.builder()
                .setTypes(NDList.class, NDList.class)
                .optModelPath(modelPath)
                .optEngine("OnnxRuntime")
                .build();

            ZooModel<?, ?> model = ModelZoo.loadModel(criteria);
            loadedModels.put(modelName, model);
            
            // Создаем предиктор для модели
            Predictor<NDList, NDList> predictor = ((ZooModel<NDList, NDList>) model).newPredictor();
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
