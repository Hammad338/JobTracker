package com.hammad.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hammad.jobtracker.dto.JobApplicationDto;
import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class JobApplicationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobApplicationRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void getAll_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void create_returnsCreated() throws Exception {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setCompany("Zalando");
        dto.setRole("Backend Engineer");
        dto.setStatus(ApplicationStatus.APPLIED);
        dto.setAppliedDate(LocalDate.now());

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company").value("Zalando"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void create_missingCompany_returnsBadRequest() throws Exception {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setRole("Backend Engineer");
        dto.setStatus(ApplicationStatus.APPLIED);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/applications/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStats_returnsStatsObject() throws Exception {
        mockMvc.perform(get("/api/applications/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }
}
