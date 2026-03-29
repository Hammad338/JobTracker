package com.hammad.jobtracker.controller;

import com.hammad.jobtracker.dto.JobApplicationDto;
import com.hammad.jobtracker.dto.StatsDto;
import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationApiController {

    private final JobApplicationService service;

    public JobApplicationApiController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobApplication> getAll(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return service.search(search);
        }
        if (status != null) {
            return service.findByStatus(status);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(@Valid @RequestBody JobApplicationDto dto) {
        JobApplication created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @Valid @RequestBody JobApplicationDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public StatsDto getStats() {
        return service.getStats();
    }
}
