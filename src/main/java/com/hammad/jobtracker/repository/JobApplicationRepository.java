package com.hammad.jobtracker.repository;

import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatusOrderByAppliedDateDesc(ApplicationStatus status);

    List<JobApplication> findAllByOrderByAppliedDateDesc();

    List<JobApplication> findByCompanyContainingIgnoreCaseOrRoleContainingIgnoreCase(String company, String role);

    @Query("SELECT j.status, COUNT(j) FROM JobApplication j GROUP BY j.status")
    List<Object[]> countByStatus();
}
