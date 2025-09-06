package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.service.business.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

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
        when(vacancyService.getAllVacancies()).thenReturn(vacancies);

        // When
        ResponseEntity<List<Vacancy>> response = vacancyController.getAllVacancies();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Java Developer", response.getBody().get(0).getTitle());
        assertEquals("Python Developer", response.getBody().get(1).getTitle());
    }

    @Test
    @DisplayName("Должен получить вакансию по ID")
    void shouldGetVacancyById() {
        // Given
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setTitle("Java Developer");
        
        when(vacancyService.getVacancyById(1L)).thenReturn(Optional.of(vacancy));

        // When
        ResponseEntity<Vacancy> response = vacancyController.getVacancyById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java Developer", response.getBody().getTitle());
    }

    @Test
    @DisplayName("Должен вернуть 404 для несуществующей вакансии")
    void shouldReturn404ForNonExistentVacancy() {
        // Given
        when(vacancyService.getVacancyById(999L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Vacancy> response = vacancyController.getVacancyById(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
        
        when(vacancyService.createVacancy(any(Vacancy.class))).thenReturn(savedVacancy);

        // When
        ResponseEntity<Vacancy> response = vacancyController.createVacancy(vacancy);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java Developer", response.getBody().getTitle());
    }
}
