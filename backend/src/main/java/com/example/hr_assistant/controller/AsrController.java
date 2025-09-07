package com.example.hr_assistant.controller;

import com.example.hr_assistant.service.external.WhisperXClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/asr")
@Tag(name = "ASR (Распознавание речи)", description = "Прокси-эндпоинты для распознавания речи через сервис WhisperX")
public class AsrController {

    private final WhisperXClient whisperXClient;

    public AsrController(WhisperXClient whisperXClient) {
        this.whisperXClient = whisperXClient;
    }

    @Operation(
            summary = "Транскрибация аудио",
            description = "Принимает аудиофайл, проксирует его в сервис WhisperX и возвращает результат распознавания (JSON)"
    )
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> transcribe(
            @Parameter(description = "Аудио/видео файл (wav/mp3/mp4)") @RequestPart("file") MultipartFile file,
            @Parameter(description = "Код языка, например ru или en") @RequestParam(required = false) String language,
            @Parameter(description = "Размер модели Whisper (по умолчанию large-v2)") @RequestParam(required = false, name = "model_size") String modelSize,
            @Parameter(description = "Batch size для инференса") @RequestParam(required = false, name = "batch_size") Integer batchSize,
            @Parameter(description = "Тип вычислений: float16 (GPU) или int8 (CPU)") @RequestParam(required = false, name = "compute_type") String computeType,
            @Parameter(description = "Выполнять ли диаризацию (распознавание спикеров)") @RequestParam(required = false) Boolean diarize,
            @Parameter(description = "Минимальное число спикеров, если известно") @RequestParam(required = false, name = "min_speakers") Integer minSpeakers,
            @Parameter(description = "Максимальное число спикеров, если известно") @RequestParam(required = false, name = "max_speakers") Integer maxSpeakers
    ) throws IOException {
        File temp = File.createTempFile("asr_", "_upload");
        file.transferTo(temp);
        try {
            String json = whisperXClient.transcribe(temp, language, modelSize, batchSize, computeType, diarize, minSpeakers, maxSpeakers);
            return ResponseEntity.ok(json);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
        }
    }
}


