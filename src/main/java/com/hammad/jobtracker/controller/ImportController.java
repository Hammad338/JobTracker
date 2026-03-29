package com.hammad.jobtracker.controller;

import com.hammad.jobtracker.service.LinkedInImportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ImportController {

    private final LinkedInImportService importService;

    public ImportController(LinkedInImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import/linkedin")
    public String importLinkedIn(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes ra) {
        if (file.isEmpty()) {
            ra.addFlashAttribute("successMessage", "No file selected.");
            return "redirect:/";
        }
        try {
            int count = importService.importFromCsv(file);
            ra.addFlashAttribute("successMessage",
                    "Imported " + count + " application" + (count == 1 ? "" : "s") + " from LinkedIn.");
        } catch (Exception e) {
            ra.addFlashAttribute("successMessage", "Import failed: " + e.getMessage());
        }
        return "redirect:/";
    }
}
