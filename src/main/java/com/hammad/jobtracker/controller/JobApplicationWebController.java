package com.hammad.jobtracker.controller;

import com.hammad.jobtracker.dto.JobApplicationDto;
import com.hammad.jobtracker.model.ApplicationStatus;
import com.hammad.jobtracker.model.JobApplication;
import com.hammad.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class JobApplicationWebController {

    private final JobApplicationService service;

    public JobApplicationWebController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public String dashboard(Model model,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("applications", service.search(search));
            model.addAttribute("search", search);
        } else if (status != null && !status.isBlank()) {
            model.addAttribute("applications", service.findByStatus(ApplicationStatus.valueOf(status)));
            model.addAttribute("activeFilter", status);
        } else {
            model.addAttribute("applications", service.findAll());
        }

        model.addAttribute("stats", service.getStats());
        model.addAttribute("statuses", ApplicationStatus.values());
        return "dashboard";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("applicationDto", new JobApplicationDto());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("formAction", "/new");
        model.addAttribute("formTitle", "New Application");
        return "form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("applicationDto") JobApplicationDto dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("formAction", "/new");
            model.addAttribute("formTitle", "New Application");
            return "form";
        }

        service.create(dto);
        redirectAttrs.addFlashAttribute("successMessage", "Application added successfully.");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        JobApplication app = service.findById(id);
        JobApplicationDto dto = mapToDto(app);
        model.addAttribute("applicationDto", dto);
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("formAction", "/edit/" + id);
        model.addAttribute("formTitle", "Edit Application");
        return "form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("applicationDto") JobApplicationDto dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            model.addAttribute("statuses", ApplicationStatus.values());
            model.addAttribute("formAction", "/edit/" + id);
            model.addAttribute("formTitle", "Edit Application");
            return "form";
        }

        service.update(id, dto);
        redirectAttrs.addFlashAttribute("successMessage", "Application updated.");
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Application deleted.");
        return "redirect:/";
    }

    private JobApplicationDto mapToDto(JobApplication app) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(app.getId());
        dto.setCompany(app.getCompany());
        dto.setRole(app.getRole());
        dto.setLocation(app.getLocation());
        dto.setUrl(app.getUrl());
        dto.setStatus(app.getStatus());
        dto.setAppliedDate(app.getAppliedDate());
        dto.setNotes(app.getNotes());
        return dto;
    }
}
