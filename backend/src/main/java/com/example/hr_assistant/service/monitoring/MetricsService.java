package com.example.hr_assistant.service.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с метриками
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    /**
     * Увеличивает счетчик запущенных интервью
     */
    public void incrementInterviewsStarted() {
        Counter.builder("hr.interviews.started")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Увеличивает счетчик завершенных интервью
     */
    public void incrementInterviewsCompleted() {
        Counter.builder("hr.interviews.completed")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Увеличивает счетчик обработанных транскрипций
     */
    public void incrementTranscriptionProcessed() {
        Counter.builder("hr.transcription.processed")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Увеличивает счетчик обнаруженного мошенничества
     */
    public void incrementFraudDetected() {
        Counter.builder("hr.fraud.detected")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Записывает время обработки транскрипции
     */
    public void recordTranscriptionTime(long durationMs) {
        Timer.builder("hr.transcription.duration")
            .register(meterRegistry)
            .record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * Записывает время анализа
     */
    public void recordAnalysisTime(long durationMs) {
        Timer.builder("hr.analysis.duration")
            .register(meterRegistry)
            .record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * Записывает время антифрод проверки
     */
    public void recordAntifraudTime(long durationMs) {
        Timer.builder("hr.antifraud.duration")
            .register(meterRegistry)
            .record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * Увеличивает счетчик загруженных моделей
     */
    public void incrementModelsLoaded() {
        Counter.builder("hr.models.loaded")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Увеличивает счетчик ошибок моделей
     */
    public void incrementModelErrors() {
        Counter.builder("hr.models.errors")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Записывает кастомную метрику
     */
    public void recordCustomMetric(String name, double value, String... tags) {
        try {
            meterRegistry.gauge(name, value);
        } catch (Exception e) {
            log.error("Ошибка при записи метрики {}: {}", name, e.getMessage());
        }
    }

    /**
     * Записывает счетчик с тегами
     */
    public void incrementCounterWithTags(String name, String... tags) {
        try {
            Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment();
        } catch (Exception e) {
            log.error("Ошибка при увеличении счетчика {}: {}", name, e.getMessage());
        }
    }
}
