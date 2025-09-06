package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.repository.VacancyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateVacancy() throws Exception {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Software Engineer");
        vacancy.setDescription("Develops and maintains software.");

        mockMvc.perform(post("/api/vacancies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Software Engineer"));
    }

    @Test
    void shouldGetAllVacancies() throws Exception {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Test Engineer");
        vacancyRepository.save(vacancy);

        mockMvc.perform(get("/api/vacancies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Engineer"));
    }
}
