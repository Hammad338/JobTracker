package com.hammad.jobtracker.service;

import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.repository.JobApplicationRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class LinkedInImportService {

    private final JobApplicationRepository repo;

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MMM dd, yyyy"),
            DateTimeFormatter.ofPattern("MMMM dd, yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy")
    );

    public LinkedInImportService(JobApplicationRepository repo) {
        this.repo = repo;
    }

    public int importFromCsv(MultipartFile file) throws IOException, CsvException {
        try (var reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            if (rows.size() < 2) return 0;

            // Parse header row — find column indices case-insensitively
            String[] headers = rows.get(0);
            Map<String, Integer> idx = buildIndex(headers);

            int imported = 0;
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length == 0 || allBlank(row)) continue;

                String company  = get(row, idx, "company name", "company", "employer");
                String role     = get(row, idx, "title", "job title", "position", "role");
                String dateStr  = get(row, idx, "application date", "applied on", "date", "applied date");
                String status   = get(row, idx, "status", "application status", "state");
                String location = get(row, idx, "location", "job location", "city");

                if (company.isBlank() && role.isBlank()) continue;

                JobApplication app = new JobApplication();
                app.setCompany(company.isBlank() ? "Unknown" : company);
                app.setRole(role.isBlank() ? "Unknown" : role);
                app.setLocation(location.isBlank() ? null : location);
                app.setStatus(mapStatus(status));
                app.setAppliedDate(parseDate(dateStr));

                repo.save(app);
                imported++;
            }
            return imported;
        }
    }

    private Map<String, Integer> buildIndex(String[] headers) {
        var map = new java.util.HashMap<String, Integer>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim().toLowerCase(), i);
        }
        return map;
    }

    private String get(String[] row, Map<String, Integer> idx, String... candidates) {
        for (String candidate : candidates) {
            Integer i = idx.get(candidate.toLowerCase());
            if (i != null && i < row.length) {
                return row[i].trim();
            }
        }
        return "";
    }

    private boolean allBlank(String[] row) {
        return Arrays.stream(row).allMatch(String::isBlank);
    }

    private ApplicationStatus mapStatus(String raw) {
        if (raw == null || raw.isBlank()) return ApplicationStatus.APPLIED;
        return switch (raw.trim().toLowerCase()) {
            case "interviewing", "interview", "assessment", "technical assessment" -> ApplicationStatus.INTERVIEW;
            case "phone screen", "phone", "screening"                              -> ApplicationStatus.PHONE_SCREEN;
            case "offer", "offer extended", "offer received"                       -> ApplicationStatus.OFFER;
            case "not selected", "rejected", "declined", "not a fit"               -> ApplicationStatus.REJECTED;
            case "withdrawn", "cancelled", "canceled"                              -> ApplicationStatus.WITHDRAWN;
            case "wishlist", "saved", "bookmarked"                                 -> ApplicationStatus.WISHLIST;
            default                                                                -> ApplicationStatus.APPLIED;
        };
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return LocalDate.now();
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(raw.trim(), fmt);
            } catch (DateTimeParseException ignored) {}
        }
        return LocalDate.now();
    }
}
