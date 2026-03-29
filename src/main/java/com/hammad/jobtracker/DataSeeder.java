package com.hammad.jobtracker;

import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.repository.JobApplicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("h2")
public class DataSeeder implements CommandLineRunner {

    private final JobApplicationRepository repository;

    public DataSeeder(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.saveAll(List.of(
                build("Senacor Technologies", "Junior Java Developer", "Berlin", ApplicationStatus.INTERVIEW,
                        LocalDate.now().minusDays(10), "Interviewed on-site. Awaiting feedback."),
                build("Zalando", "Backend Engineer", "Berlin (Hybrid)", ApplicationStatus.APPLIED,
                        LocalDate.now().minusDays(5), "Applied via careers page."),
                build("HelloFresh", "Software Engineer", "Berlin", ApplicationStatus.PHONE_SCREEN,
                        LocalDate.now().minusDays(14), "Had a 30-min intro call. Technical round next week."),
                build("N26", "Java Developer", "Berlin", ApplicationStatus.REJECTED,
                        LocalDate.now().minusDays(20), "Rejected after first round. Role filled internally."),
                build("Owkin", "Junior Backend Developer", "Remote", ApplicationStatus.APPLIED,
                        LocalDate.now().minusDays(3), "Tailored CV for their ML infrastructure stack."),
                build("FC Bayern München", "IT-Systemadministrator", "Munich", ApplicationStatus.WISHLIST,
                        null, "Dream role. Preparing application.")
        ));
    }

    private JobApplication build(String company, String role, String location,
                                  ApplicationStatus status, LocalDate date, String notes) {
        JobApplication app = new JobApplication();
        app.setCompany(company);
        app.setRole(role);
        app.setLocation(location);
        app.setStatus(status);
        app.setAppliedDate(date != null ? date : LocalDate.now());
        app.setNotes(notes);
        return app;
    }
}
