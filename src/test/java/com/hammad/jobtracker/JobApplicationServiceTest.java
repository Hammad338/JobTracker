package com.hammad.jobtracker;

import com.hammad.jobtracker.dto.JobApplicationDto;
import com.hammad.jobtracker.exception.ResourceNotFoundException;
import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.repository.JobApplicationRepository;
import com.hammad.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repository;

    @InjectMocks
    private JobApplicationService service;

    private JobApplication sampleApp;

    @BeforeEach
    void setUp() {
        sampleApp = new JobApplication();
        sampleApp.setId(1L);
        sampleApp.setCompany("Zalando");
        sampleApp.setRole("Backend Engineer");
        sampleApp.setStatus(ApplicationStatus.APPLIED);
    }

    @Test
    void findAll_returnsAllApplications() {
        when(repository.findAllByOrderByAppliedDateDesc()).thenReturn(List.of(sampleApp));
        List<JobApplication> result = service.findAll();
        assertThat(result).hasSize(1).containsExactly(sampleApp);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsApplication() {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setCompany("N26");
        dto.setRole("Java Developer");
        dto.setStatus(ApplicationStatus.APPLIED);

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JobApplication result = service.create(dto);
        assertThat(result.getCompany()).isEqualTo("N26");
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        verify(repository).save(any(JobApplication.class));
    }

    @Test
    void delete_removesApplication() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleApp));
        service.delete(1L);
        verify(repository).delete(sampleApp);
    }
}
