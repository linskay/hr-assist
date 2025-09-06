package com.example.hr_assistant.service.ml;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для классификации соответствия ответов требованиям
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassificationService {

    private final ModelManager modelManager;
    private final EmbeddingService embeddingService;

    /**
     * Классифицирует соответствие ответа требованиям (0/0.5/1)
     */
    public Map<String, Double> classifyCompliance(String answer, Map<String, String> requirements) {
        Map<String, Double> results = new HashMap<>();
        
        try {
            if (!modelManager.isModelLoaded("classifier")) {
                log.warn("Модель классификатора не загружена, используем эмбеддинги");
                return classifyWithEmbeddings(answer, requirements);
            }

            var predictor = modelManager.getPredictor("classifier");
            
            for (Map.Entry<String, String> requirement : requirements.entrySet()) {
                String competency = requirement.getKey();
                String requirementText = requirement.getValue();
                
                // Подготавливаем данные для классификации
                NDList input = prepareClassificationInput(answer, requirementText);
                
                // Выполняем классификацию
                @SuppressWarnings("unchecked")
                NDList output = (NDList) predictor.predict(input);
                
                // Извлекаем результат (0, 0.5, 1)
                double score = extractClassificationScore(output);
                results.put(competency, score);
                
                log.debug("Классификация для {}: {}", competency, score);
            }
            
        } catch (Exception e) {
            log.error("Ошибка при классификации: {}", e.getMessage(), e);
            // Fallback к эмбеддингам
            return classifyWithEmbeddings(answer, requirements);
        }
        
        return results;
    }

    /**
     * Классификация с использованием эмбеддингов (fallback)
     */
    private Map<String, Double> classifyWithEmbeddings(String answer, Map<String, String> requirements) {
        Map<String, Double> results = new HashMap<>();
        
        try {
            float[] answerEmbedding = embeddingService.createEmbedding(answer);
            
            for (Map.Entry<String, String> requirement : requirements.entrySet()) {
                String competency = requirement.getKey();
                String requirementText = requirement.getValue();
                
                float[] requirementEmbedding = embeddingService.createEmbedding(requirementText);
                double similarity = embeddingService.cosineSimilarity(answerEmbedding, requirementEmbedding);
                
                // Преобразуем similarity в score (0, 0.5, 1)
                double score = convertSimilarityToScore(similarity);
                results.put(competency, score);
                
                log.debug("Эмбеддинг классификация для {}: similarity={}, score={}", 
                    competency, similarity, score);
            }
            
        } catch (Exception e) {
            log.error("Ошибка при классификации с эмбеддингами: {}", e.getMessage(), e);
            // Возвращаем нулевые оценки
            requirements.keySet().forEach(competency -> results.put(competency, 0.0));
        }
        
        return results;
    }

    /**
     * Подготавливает входные данные для классификатора
     */
    private NDList prepareClassificationInput(String answer, String requirement) {
        // Здесь должна быть логика подготовки данных для BERT-подобной модели
        // Это упрощенная версия
        try {
            // В реальной реализации нужно:
            // 1. Токенизировать текст
            // 2. Добавить специальные токены [CLS], [SEP]
            // 3. Создать attention mask
            // 4. Преобразовать в NDArray
            
            // Заглушка
            return new NDList();
        } catch (Exception e) {
            log.error("Ошибка при подготовке входных данных: {}", e.getMessage());
            throw new RuntimeException("Ошибка подготовки данных", e);
        }
    }

    /**
     * Извлекает оценку классификации из выхода модели
     */
    private double extractClassificationScore(NDList output) {
        try {
            // В реальной реализации нужно:
            // 1. Получить logits из выхода модели
            // 2. Применить softmax
            // 3. Выбрать класс с максимальной вероятностью
            // 4. Преобразовать в score (0, 0.5, 1)
            
            // Заглушка - случайная оценка
            double random = Math.random();
            if (random < 0.3) return 0.0;
            if (random < 0.7) return 0.5;
            return 1.0;
            
        } catch (Exception e) {
            log.error("Ошибка при извлечении оценки: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Преобразует similarity в score (0, 0.5, 1)
     */
    private double convertSimilarityToScore(double similarity) {
        if (similarity >= 0.8) return 1.0;
        if (similarity >= 0.5) return 0.5;
        return 0.0;
    }

    /**
     * Вычисляет общую оценку соответствия
     */
    public double calculateOverallScore(Map<String, Double> competencyScores, Map<String, Double> weights) {
        if (competencyScores.isEmpty()) return 0.0;
        
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (Map.Entry<String, Double> entry : competencyScores.entrySet()) {
            String competency = entry.getKey();
            double score = entry.getValue();
            double weight = weights.getOrDefault(competency, 1.0);
            
            totalWeightedScore += score * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }
}
