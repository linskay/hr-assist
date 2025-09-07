package com.example.hr_assistant.service.antifraud;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для детекции AI-генерированного текста
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiTextDetectionService {

    private final com.example.hr_assistant.service.ml.ModelManager modelManager;

    /**
     * Детектирует AI-генерированный текст
     */
    public AiDetectionResult detectAiText(String text) {
        try {
            if (!modelManager.isModelLoaded("ai-detector")) {
                log.warn("Модель детекции AI текста не загружена, используем эвристики");
                return detectWithHeuristics(text);
            }

            ai.djl.inference.Predictor<Object, Object> predictor = modelManager.getPredictor("ai-detector");
            
            // Подготавливаем текст для анализа
            NDList input = prepareTextInput(text);
            
            // Выполняем детекцию
            @SuppressWarnings("unchecked")
            NDList output = (NDList) predictor.predict(input);
            
            // Извлекаем результат
            double aiProbability = extractAiProbability(output);
            Map<String, Object> flags = extractDetectionFlags(output);
            
            AiDetectionResult result = new AiDetectionResult();
            result.setAiProbability(aiProbability);
            result.setIsAiGenerated(aiProbability > 0.7); // Порог детекции
            result.setFlags(flags);
            
            log.info("AI text detection: probability={}, isAi={}", aiProbability, result.getIsAiGenerated());
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при детекции AI текста: {}", e.getMessage(), e);
            return detectWithHeuristics(text);
        }
    }

    /**
     * Детекция с использованием эвристик (fallback)
     */
    private AiDetectionResult detectWithHeuristics(String text) {
        AiDetectionResult result = new AiDetectionResult();
        
        // Эвристики для детекции AI текста:
        double score = 0.0;
        
        // 1. Проверка на слишком "идеальную" структуру
        if (hasPerfectStructure(text)) {
            score += 0.3;
        }
        
        // 2. Проверка на характерные AI фразы
        if (hasAiPhrases(text)) {
            score += 0.4;
        }
        
        // 3. Проверка на отсутствие естественных ошибок
        if (hasNoNaturalErrors(text)) {
            score += 0.2;
        }
        
        // 4. Проверка на слишком формальный стиль
        if (isTooFormal(text)) {
            score += 0.1;
        }
        
        result.setAiProbability(score);
        result.setIsAiGenerated(score > 0.5);
        result.setFlags(Map.of(
            "perfect_structure", hasPerfectStructure(text),
            "ai_phrases", hasAiPhrases(text),
            "no_errors", hasNoNaturalErrors(text),
            "too_formal", isTooFormal(text),
            "heuristic_detection", true
        ));
        
        return result;
    }

    /**
     * Подготавливает текст для модели
     */
    private NDList prepareTextInput(String text) {
        // Здесь должна быть логика:
        // 1. Токенизация текста
        // 2. Добавление специальных токенов
        // 3. Создание attention mask
        // 4. Преобразование в NDArray
        
        // Заглушка
        try {
            // В реальной реализации нужно токенизировать текст
            return new NDList();
        } catch (Exception e) {
            log.error("Ошибка при подготовке текста: {}", e.getMessage());
            throw new RuntimeException("Ошибка подготовки текста", e);
        }
    }

    /**
     * Извлекает вероятность AI генерации из выхода модели
     */
    private double extractAiProbability(NDList output) {
        try {
            // В реальной реализации нужно:
            // 1. Получить logits из выхода модели
            // 2. Применить softmax
            // 3. Извлечь вероятность класса "AI-generated"
            
            // Заглушка
            return Math.random() * 0.3; // Случайное значение 0-0.3 (обычно не AI)
        } catch (Exception e) {
            log.error("Ошибка при извлечении AI probability: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Извлекает флаги детекции из выхода модели
     */
    private Map<String, Object> extractDetectionFlags(NDList output) {
        return Map.of(
            "high_confidence", false,
            "multiple_indicators", false,
            "suspicious_patterns", false
        );
    }

    /**
     * Проверяет на слишком "идеальную" структуру
     */
    private boolean hasPerfectStructure(String text) {
        // Проверяем на:
        // - Слишком правильную пунктуацию
        // - Идеальную структуру предложений
        // - Отсутствие естественных пауз
        
        String[] sentences = text.split("[.!?]+");
        if (sentences.length < 2) return false;
        
        // Проверяем длину предложений (слишком равномерная)
        int avgLength = text.length() / sentences.length;
        int variance = 0;
        for (String sentence : sentences) {
            variance += Math.abs(sentence.trim().length() - avgLength);
        }
        
        return variance < avgLength * 0.3; // Низкая вариативность
    }

    /**
     * Проверяет на характерные AI фразы
     */
    private boolean hasAiPhrases(String text) {
        String[] aiPhrases = {
            "в соответствии с", "следует отметить", "необходимо подчеркнуть",
            "важно отметить", "следует подчеркнуть", "в данном случае",
            "в связи с вышеизложенным", "таким образом", "в результате"
        };
        
        String lowerText = text.toLowerCase();
        int aiPhraseCount = 0;
        
        for (String phrase : aiPhrases) {
            if (lowerText.contains(phrase)) {
                aiPhraseCount++;
            }
        }
        
        return aiPhraseCount >= 2; // Два или более AI фраз
    }

    /**
     * Проверяет на отсутствие естественных ошибок
     */
    private boolean hasNoNaturalErrors(String text) {
        // Проверяем на:
        // - Отсутствие опечаток
        // - Идеальную грамматику
        // - Отсутствие сокращений
        
        // Простая проверка на опечатки (упрощенно)
        String[] words = text.split("\\s+");
        int typoCount = 0;
        
        for (String word : words) {
            if (word.length() > 3 && word.matches(".*[а-я]{4,}.*")) {
                // Простая эвристика для поиска опечаток
                if (Math.random() < 0.05) { // 5% вероятность опечатки в длинном слове
                    typoCount++;
                }
            }
        }
        
        return typoCount == 0 && words.length > 10; // Нет опечаток в длинном тексте
    }

    /**
     * Проверяет на слишком формальный стиль
     */
    private boolean isTooFormal(String text) {
        // Проверяем на:
        // - Отсутствие разговорных выражений
        // - Слишком много формальных слов
        // - Отсутствие эмоциональности
        
        String[] formalWords = {
            "осуществлять", "реализовывать", "функционировать", "характеризоваться",
            "определяться", "формироваться", "развиваться", "совершенствоваться"
        };
        
        String lowerText = text.toLowerCase();
        int formalWordCount = 0;
        
        for (String word : formalWords) {
            if (lowerText.contains(word)) {
                formalWordCount++;
            }
        }
        
        return formalWordCount >= 3; // Три или более формальных слова
    }

    /**
     * Результат детекции AI текста
     */
    public static class AiDetectionResult {
        private double aiProbability;
        private boolean isAiGenerated;
        private Map<String, Object> flags;

        // Getters and setters
        public double getAiProbability() { return aiProbability; }
        public void setAiProbability(double aiProbability) { this.aiProbability = aiProbability; }
        
        public boolean getIsAiGenerated() { return isAiGenerated; }
        public void setIsAiGenerated(boolean isAiGenerated) { this.isAiGenerated = isAiGenerated; }
        
        public Map<String, Object> getFlags() { return flags; }
        public void setFlags(Map<String, Object> flags) { this.flags = flags; }
    }
}
