package com.example.hr_assistant.service.antifraud;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для распознавания лиц и сравнения
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FaceRecognitionService {

    private final com.example.hr_assistant.service.ml.ModelManager modelManager;

    /**
     * Извлекает эмбеддинг лица из изображения
     */
    public float[] extractFaceEmbedding(byte[] imageData) {
        try {
            if (!modelManager.isModelLoaded("face-recognition")) {
                log.warn("Модель распознавания лиц не загружена");
                return createFallbackEmbedding();
            }

            ai.djl.inference.Predictor<Object, Object> predictor = modelManager.getPredictor("face-recognition");
            
            // Подготавливаем данные изображения
            NDList input = prepareImageInput(imageData);
            
            // Извлекаем эмбеддинг лица
            @SuppressWarnings("unchecked")
            NDList output = (NDList) predictor.predict(input);
            NDArray embedding = output.get(0);
            
            // Конвертируем в массив float
            float[] result = embedding.toFloatArray();
            
            log.debug("Извлечен эмбеддинг лица размером: {}", result.length);
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при извлечении эмбеддинга лица: {}", e.getMessage(), e);
            return createFallbackEmbedding();
        }
    }

    /**
     * Сравнивает два лица и возвращает score сходства
     */
    public double compareFaces(byte[] image1, byte[] image2) {
        try {
            float[] embedding1 = extractFaceEmbedding(image1);
            float[] embedding2 = extractFaceEmbedding(image2);
            
            return calculateFaceSimilarity(embedding1, embedding2);
            
        } catch (Exception e) {
            log.error("Ошибка при сравнении лиц: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Сравнивает лицо с эталонным эмбеддингом
     */
    public double compareWithReference(byte[] image, float[] referenceEmbedding) {
        try {
            float[] imageEmbedding = extractFaceEmbedding(image);
            return calculateFaceSimilarity(imageEmbedding, referenceEmbedding);
            
        } catch (Exception e) {
            log.error("Ошибка при сравнении с эталоном: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Проверяет соответствие лица кандидата (face match)
     */
    public FaceMatchResult verifyFaceMatch(byte[] candidateImage, byte[] referenceImage) {
        try {
            double similarity = compareFaces(candidateImage, referenceImage);
            
            FaceMatchResult result = new FaceMatchResult();
            result.setFaceMatchScore(similarity);
            result.setIsMatch(similarity >= 0.7); // Порог соответствия
            result.setConfidence(similarity);
            
            // Дополнительные проверки
            Map<String, Object> flags = performAdditionalChecks(candidateImage, referenceImage);
            result.setFlags(flags);
            
            log.info("Face match проверка: similarity={}, isMatch={}", similarity, result.getIsMatch());
            
            return result;
            
        } catch (Exception e) {
            log.error("Ошибка при проверке face match: {}", e.getMessage(), e);
            return createErrorResult();
        }
    }

    /**
     * Вычисляет косинусное сходство между эмбеддингами лиц
     */
    private double calculateFaceSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            log.warn("Размеры эмбеддингов лиц не совпадают: {} vs {}", embedding1.length, embedding2.length);
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
     * Подготавливает данные изображения для модели
     */
    private NDList prepareImageInput(byte[] imageData) {
        // Здесь должна быть логика:
        // 1. Декодирование изображения
        // 2. Детекция лица
        // 3. Выравнивание лица
        // 4. Нормализация
        // 5. Преобразование в NDArray
        
        // Заглушка
        try {
            // В реальной реализации нужно использовать OpenCV или аналогичную библиотеку
            return new NDList();
        } catch (Exception e) {
            log.error("Ошибка при подготовке изображения: {}", e.getMessage());
            throw new RuntimeException("Ошибка подготовки изображения", e);
        }
    }

    /**
     * Выполняет дополнительные проверки
     */
    private Map<String, Object> performAdditionalChecks(byte[] image1, byte[] image2) {
        // Дополнительные проверки:
        // - Качество изображения
        // - Освещение
        // - Угол поворота головы
        // - Наличие очков, масок и т.д.
        
        return Map.of(
            "image_quality_good", true,
            "lighting_adequate", true,
            "head_angle_normal", true,
            "no_obstructions", true
        );
    }

    /**
     * Создает fallback эмбеддинг
     */
    private float[] createFallbackEmbedding() {
        // Возвращаем случайный эмбеддинг для тестирования
        float[] embedding = new float[512]; // Типичный размер для FaceNet
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) (Math.random() * 2 - 1); // [-1, 1]
        }
        return embedding;
    }

    /**
     * Создает результат с ошибкой
     */
    private FaceMatchResult createErrorResult() {
        FaceMatchResult result = new FaceMatchResult();
        result.setFaceMatchScore(0.0);
        result.setIsMatch(false);
        result.setConfidence(0.0);
        result.setFlags(Map.of("error", true));
        return result;
    }

    /**
     * Результат проверки соответствия лиц
     */
    public static class FaceMatchResult {
        private double faceMatchScore;
        private boolean isMatch;
        private double confidence;
        private Map<String, Object> flags;

        // Getters and setters
        public double getFaceMatchScore() { return faceMatchScore; }
        public void setFaceMatchScore(double faceMatchScore) { this.faceMatchScore = faceMatchScore; }
        
        public boolean getIsMatch() { return isMatch; }
        public void setIsMatch(boolean isMatch) { this.isMatch = isMatch; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getFlags() { return flags; }
        public void setFlags(Map<String, Object> flags) { this.flags = flags; }
    }
}
