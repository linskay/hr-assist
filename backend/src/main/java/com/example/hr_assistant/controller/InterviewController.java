package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.Interview;
import com.example.hr_assistant.model.dto.*;
import com.example.hr_assistant.repository.InterviewRepository;
import com.example.hr_assistant.service.antifraud.AntifraudService;
// import com.example.hr_assistant.service.queue.QueueService;
import com.example.hr_assistant.service.storage.MediaStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с интервью
 */
@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Интервью", description = "API для проведения интервью с кандидатами")
public class InterviewController {

    private final InterviewRepository interviewRepository;
    private final AntifraudService antifraudService;
    private final MediaStorageService mediaStorageService;
    // private final QueueService queueService;

    @PostMapping
    @Operation(summary = "Создать интервью", description = "Создание нового интервью для кандидата")
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewCreateRequest request) {
        try {
            // Создаем интервью
            Interview interview = new Interview();
            interview.setCandidateName(request.getCandidateName());
            interview.setCandidateEmail(request.getCandidateEmail());
            interview.setCandidatePhone(request.getCandidatePhone());
            interview.setStatus(Interview.InterviewStatus.CREATED);
            
            // TODO: Получить вакансию по ID
            // interview.setVacancy(vacancy);
            
            interview = interviewRepository.save(interview);
            
            // Создаем антифрод запись
            antifraudService.createAntifraudRecord(interview);
            
            log.info("Создано интервью: id={}, candidate={}", interview.getId(), request.getCandidateName());
            
            return ResponseEntity.ok(new InterviewResponse(interview));
            
        } catch (Exception e) {
            log.error("Ошибка при создании интервью: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить интервью", description = "Получение информации об интервью")
    public ResponseEntity<InterviewResponse> getInterview(@PathVariable Long id) {
        try {
            Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Интервью не найдено: " + id));
            
            return ResponseEntity.ok(new InterviewResponse(interview));
            
        } catch (Exception e) {
            log.error("Ошибка при получении интервью {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Начать интервью", description = "Запуск интервью для кандидата")
    public ResponseEntity<Void> startInterview(@PathVariable Long id) {
        try {
            Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Интервью не найдено: " + id));
            
            interview.setStatus(Interview.InterviewStatus.STARTED);
            interview.setStartedAt(java.time.LocalDateTime.now());
            interviewRepository.save(interview);
            
            log.info("Интервью {} запущено", id);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Ошибка при запуске интервью {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/questions/{qid}/start-record")
    @Operation(summary = "Начать запись ответа", description = "Начало записи ответа на вопрос")
    public ResponseEntity<Void> startRecording(@PathVariable Long id, @PathVariable Long qid) {
        try {
            // Логика начала записи
            log.info("Начата запись для интервью {}, вопрос {}", id, qid);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Ошибка при начале записи: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/questions/{qid}/upload-chunk")
    @Operation(summary = "Загрузить чанк записи", description = "Загрузка части аудио/видео записи")
    public ResponseEntity<MediaUploadResponse> uploadChunk(
            @PathVariable Long id,
            @PathVariable Long qid,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "chunkIndex", defaultValue = "0") Integer chunkIndex,
            @RequestParam(value = "isFinal", defaultValue = "false") Boolean isFinal) {
        
        try {
            String folder = String.format("interviews/%d/questions/%d", id, qid);
            MediaUploadResponse response = mediaStorageService.uploadFile(file, folder);
            
            if (response.getSuccess()) {
                // Отправляем задачу транскрипции если это финальный чанк
                if (isFinal) {
                    // queueService.sendTranscriptionTask(id, response.getFileUrl());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при загрузке чанка: {}", e.getMessage(), e);
            
            MediaUploadResponse errorResponse = new MediaUploadResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Ошибка загрузки: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{id}/questions/{qid}/finish-record")
    @Operation(summary = "Завершить запись", description = "Завершение записи ответа и запуск анализа")
    public ResponseEntity<Void> finishRecording(@PathVariable Long id, @PathVariable Long qid) {
        try {
            // Логика завершения записи и запуска анализа
            log.info("Завершена запись для интервью {}, вопрос {}", id, qid);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Ошибка при завершении записи: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/heartbeat")
    @Operation(summary = "Heartbeat", description = "Отправка heartbeat от клиента")
    public ResponseEntity<Void> heartbeat(@PathVariable Long id, @RequestBody HeartbeatRequest request) {
        try {
            antifraudService.updateHeartbeat(id, request);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Ошибка при обработке heartbeat: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/results")
    @Operation(summary = "Получить результаты", description = "Получение полных результатов интервью")
    public ResponseEntity<InterviewResultsResponse> getResults(@PathVariable Long id) {
        try {
            Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Интервью не найдено: " + id));
            
            // TODO: Получить анализ и антифрод данные
            // InterviewResultsResponse response = new InterviewResultsResponse(analysis, antifraud);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Ошибка при получении результатов интервью {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/media/{type}")
    @Operation(summary = "Получить медиа", description = "Получение URL для доступа к медиа файлам")
    public ResponseEntity<String> getMediaUrl(@PathVariable Long id, @PathVariable String type) {
        try {
            // Логика получения URL медиа файла
            String mediaUrl = "http://example.com/media/" + id + "/" + type;
            return ResponseEntity.ok(mediaUrl);
            
        } catch (Exception e) {
            log.error("Ошибка при получении URL медиа: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Список интервью", description = "Получение списка интервью")
    public ResponseEntity<List<InterviewResponse>> getInterviews() {
        try {
            List<Interview> interviews = interviewRepository.findAll();
            List<InterviewResponse> responses = interviews.stream()
                .map(InterviewResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            log.error("Ошибка при получении списка интервью: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
