package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.service.business.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с вакансиями
 */
@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Вакансии", description = "API для управления вакансиями")
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    @Operation(summary = "Получить все вакансии", description = "Получение списка всех активных вакансий")
    public ResponseEntity<List<Vacancy>> getAllVacancies() {
        try {
            List<Vacancy> vacancies = vacancyService.getAllVacancies();
            log.info("Получено {} вакансий", vacancies.size());
            return ResponseEntity.ok(vacancies);
        } catch (Exception e) {
            log.error("Ошибка при получении вакансий: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить вакансию по ID", description = "Получение детальной информации о вакансии")
    public ResponseEntity<Vacancy> getVacancyById(@PathVariable Long id) {
        try {
            return vacancyService.getVacancyById(id)
                    .map(vacancy -> {
                        log.info("Получена вакансия: {}", vacancy.getTitle());
                        return ResponseEntity.ok(vacancy);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Ошибка при получении вакансии {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @Operation(summary = "Создать вакансию", description = "Создание новой вакансии")
    public ResponseEntity<Vacancy> createVacancy(@RequestBody Vacancy vacancy) {
        try {
            Vacancy createdVacancy = vacancyService.createVacancy(vacancy);
            log.info("Создана вакансия: {}", createdVacancy.getTitle());
            return ResponseEntity.ok(createdVacancy);
        } catch (Exception e) {
            log.error("Ошибка при создании вакансии: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить вакансию", description = "Обновление информации о вакансии")
    public ResponseEntity<Vacancy> updateVacancy(@PathVariable Long id, @RequestBody Vacancy vacancy) {
        try {
            Vacancy updatedVacancy = vacancyService.updateVacancy(id, vacancy);
            log.info("Обновлена вакансия: {}", updatedVacancy.getTitle());
            return ResponseEntity.ok(updatedVacancy);
        } catch (Exception e) {
            log.error("Ошибка при обновлении вакансии {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить вакансию", description = "Удаление вакансии")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long id) {
        try {
            vacancyService.deleteVacancy(id);
            log.info("Удалена вакансия: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении вакансии {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
