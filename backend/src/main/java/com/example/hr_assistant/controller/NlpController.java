package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.dto.NlpDtos.ClassificationRequest;
import com.example.hr_assistant.model.dto.NlpDtos.ClassificationResponse;
import com.example.hr_assistant.model.dto.NlpDtos.SimilarityRequest;
import com.example.hr_assistant.model.dto.NlpDtos.SimilarityResponse;
import com.example.hr_assistant.service.ml.ClassificationService;
import com.example.hr_assistant.service.ml.EmbeddingService;
import com.example.hr_assistant.service.external.LlmClient;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nlp")
@RequiredArgsConstructor
@Tag(name = "NLP", description = "Сравнение текстов и классификация соответствия кандидата требованиям")
public class NlpController {

    private final EmbeddingService embeddingService;
    private final ClassificationService classificationService;
    private final LlmClient llmClient;

    @PostMapping("/similarity")
    @Operation(summary = "Вычисление сходства", description = "Принимает текст кандидата и список текстов требований вакансии. Возвращает косинусное сходство для каждого требования и максимальное значение.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SimilarityResponse.class)))
    })
    public ResponseEntity<SimilarityResponse> similarity(@Valid @RequestBody SimilarityRequest request) {
        List<Double> sims = new ArrayList<>();
        float[] textEmb = embeddingService.createEmbedding(request.getText());
        for (String ref : request.getReferences()) {
            float[] refEmb = embeddingService.createEmbedding(ref);
            sims.add(embeddingService.cosineSimilarity(textEmb, refEmb));
        }
        SimilarityResponse resp = new SimilarityResponse();
        resp.setSimilarities(sims);
        resp.setMaxSimilarity(sims.stream().mapToDouble(d -> d).max().orElse(0.0));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/classify")
    @Operation(summary = "Классификация соответствия", description = "Принимает ответ кандидата и карту требований (компетенция → текст требования). Возвращает оценки по каждой компетенции, общий балл, предполагаемый уровень (junior/middle/senior) и флаг годности.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = ClassificationResponse.class)))
    })
    public ResponseEntity<ClassificationResponse> classify(@Valid @RequestBody ClassificationRequest request) {
        Map<String, Double> scores = classificationService.classifyCompliance(request.getAnswer(), request.getRequirements());
        double overall = classificationService.calculateOverallScore(scores, request.getWeights() == null ? Map.of() : request.getWeights());

        ClassificationResponse resp = new ClassificationResponse();
        resp.setScores(scores);
        resp.setOverallScore(overall);

        // Простая логика уровня и годности
        String level = overall >= 0.8 ? "senior" : overall >= 0.6 ? "middle" : overall >= 0.4 ? "junior" : "trainee";
        boolean eligible = overall >= 0.5;
        resp.setLevel(level);
        resp.setEligible(eligible);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/explanation")
    @Operation(summary = "Генерация объяснения пригодности", description = "Формирует промпт на основе вакансии, ответа кандидата и требований, отправляет в LLM и возвращает объяснение.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<String> explanation(
            @RequestParam("vacancy") String vacancy,
            @RequestParam("answer") String answer,
            @RequestParam("requirements") String requirements
    ) {
        String prompt = "Вакансия: " + vacancy + "\n" +
                "Ответ кандидата: \"" + answer + "\"\n" +
                "Требования: \"" + requirements + "\"\n" +
                "Сформируй объяснение, подходит ли кандидат. Ответь кратко и по делу.";
        String output = llmClient.generate(prompt, 384, 0.3);
        return ResponseEntity.ok(output);
    }
}


