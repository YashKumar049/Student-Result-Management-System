package com.srms.srms_app.controller;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.service.StudentService;
import com.srms.srms_app.service.PdfService; // Naya Import (Phase 15)
import jakarta.servlet.http.HttpServletResponse; // Naya Import (Phase 15)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource; // Naya Import (Phase 15)
import org.springframework.http.HttpHeaders; // Naya Import (Phase 15)
import org.springframework.http.MediaType; // Naya Import (Phase 15)
import org.springframework.http.ResponseEntity; // Naya Import (Phase 15)
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream; // Naya Import (Phase 15)
import java.io.IOException; // Naya Import (Phase 15)
import java.security.Principal;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private PdfService pdfService; // Naya (Phase 15)

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student details not found for username: " + username));
        model.addAttribute("student", student);
        return "student-dashboard";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        String username = principal.getName();
        try {
            studentService.changeStudentPassword(username, oldPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("globalSuccessMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    // --- NAYA METHOD (PHASE 15) ---
    // PDF Download ko handle karne ke liye
    @GetMapping("/download-report")
    public ResponseEntity<InputStreamResource> downloadReportCard(Principal principal, HttpServletResponse response) throws IOException {
        String username = principal.getName();
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ByteArrayInputStream bis = pdfService.generateReportCard(student);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ReportCard-" + student.getUsername() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}