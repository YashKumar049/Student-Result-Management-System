package com.srms.srms_app.controller;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.TeacherRepository;
import com.srms.srms_app.service.ExcelUploadService;
import com.srms.srms_app.service.StudentService;
import com.srms.srms_app.service.ExcelUploadService.UploadResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("teacher", teacher);

        List<Student> studentsInClass = Collections.emptyList();
        if (teacher.getClassAssigned() != null && !teacher.getClassAssigned().isEmpty()) {
            studentsInClass = studentService.findByClassSection(teacher.getClassAssigned());
        }
        model.addAttribute("studentsInClass", studentsInClass);

        return "teacher-dashboard";
    }

    // --- YAHAN FIX KIYA HAI (CRASH BUG FIX) ---
    @PostMapping("/upload-marks")
    public String uploadMarks(@RequestParam("file") MultipartFile file, Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        // 1. Get Logged-in Teacher's details
        User user = (User) authentication.getPrincipal();
        Teacher teacher = teacherRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        String subject = teacher.getSubject();
        String classAssigned = teacher.getClassAssigned();
        String teacherUsername = teacher.getUsername();

        // 2. Check karo ki file khaali (empty) toh nahi hai
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: Please select a file to upload.");
            return "redirect:/teacher/dashboard";
        }

        // 3. Check karo ki file .xlsx hai ya nahi
        if (!ExcelUploadService.isValidExcelFile(file)) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: Invalid file type. Please upload a .xlsx file.");
            return "redirect:/teacher/dashboard";
        }

        // 4. Bulletproof Try-Catch block
        try {
            UploadResult result = excelUploadService.processMarksExcel(file, subject, classAssigned, teacherUsername);

            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessages", result.getMessages());
            } else {
                redirectAttributes.addFlashAttribute("successMessages", result.getMessages());
            }
            redirectAttributes.addFlashAttribute("processedStudents", result.getProcessedStudents());

        } catch (Exception e) {
            // Agar file format (.xlsx) sahi hai lekin andar ka data (columns/rows) galat hai,
            // toh yeh crash hone ki jagah yeh error dikhayega.
            e.printStackTrace(); // Yeh log file mein error dikhayega
            redirectAttributes.addFlashAttribute("globalErrorMessage", 
                "Error: File is corrupted or data format is incorrect. Please check the file and try again.");
        }

        return "redirect:/teacher/dashboard";
    }

    
    @PostMapping("/update-mark-manual")
    public String updateMarkManually(
            @RequestParam("rollno") int rollno,
            @RequestParam("marks") int marks,
            @RequestParam("subject") String subject,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User user = (User) authentication.getPrincipal();
            String teacherUsername = user.getUsername();
            Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            String classAssigned = teacher.getClassAssigned();

            studentService.updateStudentMarks(rollno, classAssigned, subject, marks, teacherUsername);

            redirectAttributes.addFlashAttribute("globalSuccessMessage", 
                "Marks updated successfully for Reg. No: " + rollno);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", 
                "Error updating marks: " + e.getMessage());
        }

        return "redirect:/teacher/dashboard";
    }

    // --- Phase 13: Change Password ---
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        String username = principal.getName();
        
        try {
            studentService.changeTeacherPassword(username, oldPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("globalSuccessMessage", "Password changed successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/teacher/dashboard";
    }
}