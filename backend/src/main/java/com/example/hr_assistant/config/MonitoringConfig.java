package com.example.hr_assistant.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация мониторинга и метрик
 */
@Configuration
@RequiredArgsConstructor
public class MonitoringConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public Counter interviewStartedCounter() {
        return Counter.builder("hr.interviews.started")
            .description("Количество запущенных интервью")
            .register(meterRegistry);
    }

    @Bean
    public Counter interviewCompletedCounter() {
        return Counter.builder("hr.interviews.completed")
            .description("Количество завершенных интервью")
            .register(meterRegistry);
    }

    @Bean
    public Counter transcriptionProcessedCounter() {
        return Counter.builder("hr.transcription.processed")
            .description("Количество обработанных транскрипций")
            .register(meterRegistry);
    }

    @Bean
    public Counter fraudDetectedCounter() {
        return Counter.builder("hr.fraud.detected")
            .description("Количество обнаруженных случаев мошенничества")
            .register(meterRegistry);
    }

    @Bean
    public Timer transcriptionTimer() {
        return Timer.builder("hr.transcription.duration")
            .description("Время обработки транскрипции")
            .register(meterRegistry);
    }

    @Bean
    public Timer analysisTimer() {
        return Timer.builder("hr.analysis.duration")
            .description("Время анализа интервью")
            .register(meterRegistry);
    }

    @Bean
    public Timer antifraudTimer() {
        return Timer.builder("hr.antifraud.duration")
            .description("Время антифрод проверки")
            .register(meterRegistry);
    }

    @Bean
    public Counter modelLoadCounter() {
        return Counter.builder("hr.models.loaded")
            .description("Количество загруженных моделей")
            .register(meterRegistry);
    }

    @Bean
    public Counter modelErrorCounter() {
        return Counter.builder("hr.models.errors")
            .description("Количество ошибок моделей")
            .register(meterRegistry);
    }
}
