package com.example.hr_assistant.service.business;

import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.repository.VacancyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private VacancyService vacancyService;

    @Test
    @DisplayName("Должен получить все вакансии")
    void shouldGetAllVacancies() {
        // Given
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setId(1L);
        vacancy1.setTitle("Java Developer");
        
        Vacancy vacancy2 = new Vacancy();
        vacancy2.setId(2L);
        vacancy2.setTitle("Python Developer");
        
        List<Vacancy> vacancies = Arrays.asList(vacancy1, vacancy2);
        when(vacancyRepository.findAll()).thenReturn(vacancies);

        // When
        List<Vacancy> result = vacancyService.getAllVacancies();

        // Then
        assertEquals(2, result.size());
        assertEquals("Java Developer", result.get(0).getTitle());
        assertEquals("Python Developer", result.get(1).getTitle());
    }

    @Test
    @DisplayName("Должен получить вакансию по ID")
    void shouldGetVacancyById() {
        // Given
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setTitle("Java Developer");
        
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // When
        Optional<Vacancy> result = vacancyService.getVacancyById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Java Developer", result.get().getTitle());
    }

    @Test
    @DisplayName("Должен создать новую вакансию")
    void shouldCreateVacancy() {
        // Given
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Java Developer");
        vacancy.setDescription("Senior Java Developer position");
        
        Vacancy savedVacancy = new Vacancy();
        savedVacancy.setId(1L);
        savedVacancy.setTitle("Java Developer");
        savedVacancy.setDescription("Senior Java Developer position");
        
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(savedVacancy);

        // When
        Vacancy result = vacancyService.createVacancy(vacancy);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("Java Developer", result.getTitle());
        assertEquals("Senior Java Developer position", result.getDescription());
    }

    @Test
    @DisplayName("Должен обновить существующую вакансию")
    void shouldUpdateVacancy() {
        // Given
        Vacancy existingVacancy = new Vacancy();
        existingVacancy.setId(1L);
        existingVacancy.setTitle("Java Developer");
        existingVacancy.setDescription("Old description");
        
        Vacancy updatedVacancy = new Vacancy();
        updatedVacancy.setTitle("Senior Java Developer");
        updatedVacancy.setDescription("New description");
        
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(existingVacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(existingVacancy);

        // When
        Vacancy result = vacancyService.updateVacancy(1L, updatedVacancy);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("Senior Java Developer", result.getTitle());
        assertEquals("New description", result.getDescription());
    }

    @Test
    @DisplayName("Должен удалить вакансию")
    void shouldDeleteVacancy() {
        // Given
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setTitle("Java Developer");
        
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // When & Then
        assertDoesNotThrow(() -> vacancyService.deleteVacancy(1L));
    }
}