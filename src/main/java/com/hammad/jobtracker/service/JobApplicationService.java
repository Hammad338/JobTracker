package com.hammad.jobtracker.service;

import com.hammad.jobtracker.dto.JobApplicationDto;
import com.hammad.jobtracker.dto.StatsDto;
import com.hammad.jobtracker.exception.ResourceNotFoundException;
import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findAll() {
        return repository.findAllByOrderByAppliedDateDesc();
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findByStatus(ApplicationStatus status) {
        return repository.findByStatusOrderByAppliedDateDesc(status);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> search(String query) {
        return repository.findByCompanyContainingIgnoreCaseOrRoleContainingIgnoreCase(query, query);
    }

    @Transactional(readOnly = true)
    public JobApplication findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    public JobApplication create(JobApplicationDto dto) {
        JobApplication application = mapToEntity(dto, new JobApplication());
        return repository.save(application);
    }

    public JobApplication update(Long id, JobApplicationDto dto) {
        JobApplication existing = findById(id);
        mapToEntity(dto, existing);
        return repository.save(existing);
    }

    public void delete(Long id) {
        JobApplication existing = findById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public StatsDto getStats() {
        List<Object[]> rawCounts = repository.countByStatus();
        Map<String, Long> byStatus = new HashMap<>();

        for (Object[] row : rawCounts) {
            byStatus.put(row[0].toString(), (Long) row[1]);
        }

        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();
        long active = byStatus.getOrDefault(ApplicationStatus.APPLIED.name(), 0L)
                + byStatus.getOrDefault(ApplicationStatus.PHONE_SCREEN.name(), 0L)
                + byStatus.getOrDefault(ApplicationStatus.INTERVIEW.name(), 0L);
        long offers = byStatus.getOrDefault(ApplicationStatus.OFFER.name(), 0L);
        long rejected = byStatus.getOrDefault(ApplicationStatus.REJECTED.name(), 0L);

        return new StatsDto(total, active, offers, rejected, byStatus);
    }

    private JobApplication mapToEntity(JobApplicationDto dto, JobApplication target) {
        target.setCompany(dto.getCompany());
        target.setRole(dto.getRole());
        target.setLocation(dto.getLocation());
        target.setUrl(dto.getUrl());
        target.setStatus(dto.getStatus());
        target.setAppliedDate(dto.getAppliedDate());
        target.setNotes(dto.getNotes());
        return target;
    }
}
