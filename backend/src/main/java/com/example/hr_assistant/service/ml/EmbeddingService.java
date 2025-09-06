package com.example.hr_assistant.service.ml;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с эмбеддингами текста
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final ModelManager modelManager;

    /**
     * Создает эмбеддинг для текста
     */
    public float[] createEmbedding(String text) {
        try {
            if (!modelManager.isModelLoaded("embeddings")) {
                throw new IllegalStateException("Модель эмбеддингов не загружена");
            }

            var predictor = modelManager.getPredictor("embeddings");
            
            // Подготавливаем входные данные
            NDList input = prepareInput(text);
            
            // Получаем эмбеддинг
            @SuppressWarnings("unchecked")
            NDList output = (NDList) predictor.predict(input);
            NDArray embedding = output.get(0);
            
            // Конвертируем в массив float
            float[] result = embedding.toFloatArray();
            
            log.debug("Создан эмбеддинг размером {} для текста: {}", result.length, text.substring(0, Math.min(50, text.length())));
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при создании эмбеддинга: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания эмбеддинга", e);
        }
    }

    /**
     * Создает эмбеддинги для списка текстов
     */
    public List<float[]> createEmbeddings(List<String> texts) {
        return texts.stream()
            .map(this::createEmbedding)
            .collect(Collectors.toList());
    }

    /**
     * Вычисляет косинусное сходство между двумя эмбеддингами
     */
    public double cosineSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Размеры эмбеддингов должны совпадать");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Вычисляет сходство между текстом и списком эталонных текстов
     */
    public double calculateTextSimilarity(String text, List<String> referenceTexts) {
        float[] textEmbedding = createEmbedding(text);
        
        double maxSimilarity = 0.0;
        for (String reference : referenceTexts) {
            float[] referenceEmbedding = createEmbedding(reference);
            double similarity = cosineSimilarity(textEmbedding, referenceEmbedding);
            maxSimilarity = Math.max(maxSimilarity, similarity);
        }
        
        return maxSimilarity;
    }

    /**
     * Подготавливает входные данные для модели эмбеддингов
     */
    private NDList prepareInput(String text) {
        // Здесь должна быть логика токенизации и подготовки данных
        // Это упрощенная версия
        try (NDManager manager = NDManager.newBaseManager()) {
            // В реальной реализации нужно токенизировать текст
            // и преобразовать в NDArray
            NDArray input = manager.create(new float[]{1.0f, 2.0f, 3.0f}); // Заглушка
            return new NDList(input);
        }
    }

    /**
     * Нормализует эмбеддинг
     */
    public float[] normalizeEmbedding(float[] embedding) {
        double norm = 0.0;
        for (float value : embedding) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);
        
        if (norm == 0.0) {
            return embedding;
        }
        
        float[] normalized = new float[embedding.length];
        for (int i = 0; i < embedding.length; i++) {
            normalized[i] = (float) (embedding[i] / norm);
        }
        
        return normalized;
    }
}
