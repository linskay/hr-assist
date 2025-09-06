package com.example.hr_assistant.service.business;

import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.repository.VacancyRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с вакансиями
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Data
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    /**
     * Получить все вакансии
     */
    public List<Vacancy> getAllVacancies() {
        log.debug("Получение списка всех вакансий");
        return vacancyRepository.findAll();
    }

    /**
     * Получить вакансию по ID
     */
    public Optional<Vacancy> getVacancyById(Long id) {
        log.debug("Получение вакансии по ID: {}", id);
        return vacancyRepository.findById(id);
    }

    /**
     * Создать новую вакансию
     */
    public Vacancy createVacancy(Vacancy vacancy) {
        log.info("Создание новой вакансии: {}", vacancy.getTitle());
        return vacancyRepository.save(vacancy);
    }

    /**
     * Обновить вакансию
     */
    public Vacancy updateVacancy(Long id, Vacancy vacancy) {
        log.info("Обновление вакансии с ID: {}", id);
        
        Vacancy existingVacancy = vacancyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Вакансия не найдена с ID: " + id));
        
        // Обновляем поля напрямую
        existingVacancy.setTitle(vacancy.getTitle());
        existingVacancy.setDescription(vacancy.getDescription());
        existingVacancy.setRequirements(vacancy.getRequirements());
        existingVacancy.setSalaryMin(vacancy.getSalaryMin());
        existingVacancy.setSalaryMax(vacancy.getSalaryMax());
        existingVacancy.setLocation(vacancy.getLocation());
        existingVacancy.setEmploymentType(vacancy.getEmploymentType());
        existingVacancy.setExperienceLevel(vacancy.getExperienceLevel());
        existingVacancy.setStatus(vacancy.getStatus());
        
        return vacancyRepository.save(existingVacancy);
    }

    /**
     * Удалить вакансию
     */
    public void deleteVacancy(Long id) {
        log.info("Удаление вакансии с ID: {}", id);
        
        Vacancy existingVacancy = vacancyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Вакансия не найдена с ID: " + id));
        
        vacancyRepository.deleteById(id);
    }
}
