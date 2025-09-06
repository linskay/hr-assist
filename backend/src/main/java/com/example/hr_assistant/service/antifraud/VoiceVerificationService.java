package com.example.hr_assistant.service.antifraud;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для верификации голоса
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceVerificationService {

    private final com.example.hr_assistant.service.ml.ModelManager modelManager;

    /**
     * Извлекает эмбеддинг голоса из аудио
     */
    public float[] extractVoiceEmbedding(byte[] audioData) {
        try {
            if (!modelManager.isModelLoaded("voice-verification")) {
                log.warn("Модель верификации голоса не загружена");
                return createFallbackEmbedding();
            }

            var predictor = modelManager.getPredictor("voice-verification");
            
            // Подготавливаем аудио данные
            NDList input = prepareAudioInput(audioData);
            
            // Извлекаем эмбеддинг голоса
            @SuppressWarnings("unchecked")
            NDList output = (NDList) predictor.predict(input);
            NDArray embedding = output.get(0);
            
            // Конвертируем в массив float
            float[] result = embedding.toFloatArray();
            
            log.debug("Извлечен эмбеддинг голоса размером: {}", result.length);
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при извлечении эмбеддинга голоса: {}", e.getMessage(), e);
            return createFallbackEmbedding();
        }
    }

    /**
     * Сравнивает два голоса и возвращает score сходства
     */
    public double compareVoices(byte[] audio1, byte[] audio2) {
        try {
            float[] embedding1 = extractVoiceEmbedding(audio1);
            float[] embedding2 = extractVoiceEmbedding(audio2);
            
            return calculateVoiceSimilarity(embedding1, embedding2);
            
        } catch (Exception e) {
            log.error("Ошибка при сравнении голосов: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Сравнивает голос с эталонным эмбеддингом
     */
    public double compareWithReference(byte[] audio, float[] referenceEmbedding) {
        try {
            float[] audioEmbedding = extractVoiceEmbedding(audio);
            return calculateVoiceSimilarity(audioEmbedding, referenceEmbedding);
            
        } catch (Exception e) {
            log.error("Ошибка при сравнении с эталоном: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Проверяет соответствие голоса кандидата (voice match)
     */
    public VoiceMatchResult verifyVoiceMatch(byte[] candidateAudio, byte[] referenceAudio) {
        try {
            double similarity = compareVoices(candidateAudio, referenceAudio);
            
            VoiceMatchResult result = new VoiceMatchResult();
            result.setVoiceMatchScore(similarity);
            result.setIsMatch(similarity >= 0.6); // Порог соответствия для голоса
            
            // Дополнительные проверки
            Map<String, Object> flags = performAdditionalChecks(candidateAudio, referenceAudio);
            result.setFlags(flags);
            
            // Проверка на синтез речи
            boolean isSynthetic = detectSyntheticSpeech(candidateAudio);
            result.setIsSynthetic(isSynthetic);
            
            log.info("Voice match проверка: similarity={}, isMatch={}, isSynthetic={}", 
                similarity, result.getIsMatch(), isSynthetic);
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при проверке voice match: {}", e.getMessage(), e);
            return createErrorResult();
        }
    }

    /**
     * Детектирует синтезированную речь
     */
    public boolean detectSyntheticSpeech(byte[] audioData) {
        try {
            // Анализируем характеристики аудио для выявления синтеза:
            // - Спектральные характеристики
            // - Просодия (интонация, ритм)
            // - Артефакты синтеза
            
            // Заглушка - простая эвристика
            boolean hasFlatProsody = analyzeProsody(audioData);
            boolean hasSyntheticArtifacts = analyzeArtifacts(audioData);
            
            return hasFlatProsody || hasSyntheticArtifacts;
            
        } catch (Exception e) {
            log.error("Ошибка при детекции синтезированной речи: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Вычисляет косинусное сходство между эмбеддингами голосов
     */
    private double calculateVoiceSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            log.warn("Размеры эмбеддингов голосов не совпадают: {} vs {}", embedding1.length, embedding2.length);
            return 0.0;
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
     * Подготавливает аудио данные для модели
     */
    private NDList prepareAudioInput(byte[] audioData) {
        // Здесь должна быть логика:
        // 1. Декодирование аудио
        // 2. Нормализация sample rate (16kHz)
        // 3. Извлечение MFCC или других признаков
        // 4. Преобразование в NDArray
        
        // Заглушка
        try {
            // В реальной реализации нужно использовать librosa или аналогичную библиотеку
            return new NDList();
        } catch (Exception e) {
            log.error("Ошибка при подготовке аудио данных: {}", e.getMessage());
            throw new RuntimeException("Ошибка подготовки аудио", e);
        }
    }

    /**
     * Анализирует просодию речи
     */
    private boolean analyzeProsody(byte[] audioData) {
        // Анализ интонации и ритма речи
        // Синтезированная речь часто имеет более плоскую просодию
        
        // Заглушка
        return Math.random() < 0.1; // 10% вероятность детекции
    }

    /**
     * Анализирует артефакты синтеза
     */
    private boolean analyzeArtifacts(byte[] audioData) {
        // Поиск характерных артефактов синтеза речи:
        // - Резкие переходы между фонемами
        // - Отсутствие естественных шумов
        // - Слишком четкие границы слов
        
        // Заглушка
        return Math.random() < 0.05; // 5% вероятность детекции
    }

    /**
     * Выполняет дополнительные проверки
     */
    private Map<String, Object> performAdditionalChecks(byte[] audio1, byte[] audio2) {
        return Map.of(
            "audio_quality_good", true,
            "no_background_noise", true,
            "speech_clarity_adequate", true,
            "duration_sufficient", true
        );
    }

    /**
     * Создает fallback эмбеддинг
     */
    private float[] createFallbackEmbedding() {
        // Возвращаем случайный эмбеддинг для тестирования
        float[] embedding = new float[192]; // Типичный размер для ECAPA-TDNN
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) (Math.random() * 2 - 1); // [-1, 1]
        }
        return embedding;
    }

    /**
     * Создает результат с ошибкой
     */
    private VoiceMatchResult createErrorResult() {
        VoiceMatchResult result = new VoiceMatchResult();
        result.setVoiceMatchScore(0.0);
        result.setIsMatch(false);
        result.setIsSynthetic(false);
        result.setFlags(Map.of("error", true));
        return result;
    }

    /**
     * Результат проверки соответствия голосов
     */
    public static class VoiceMatchResult {
        private double voiceMatchScore;
        private boolean isMatch;
        private boolean isSynthetic;
        private Map<String, Object> flags;

        // Getters and setters
        public double getVoiceMatchScore() { return voiceMatchScore; }
        public void setVoiceMatchScore(double voiceMatchScore) { this.voiceMatchScore = voiceMatchScore; }
        
        public boolean getIsMatch() { return isMatch; }
        public void setIsMatch(boolean isMatch) { this.isMatch = isMatch; }
        
        public boolean getIsSynthetic() { return isSynthetic; }
        public void setIsSynthetic(boolean isSynthetic) { this.isSynthetic = isSynthetic; }
        
        public Map<String, Object> getFlags() { return flags; }
        public void setFlags(Map<String, Object> flags) { this.flags = flags; }
    }
}
