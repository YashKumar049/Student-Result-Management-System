package com.srms.srms_app.controller;

import com.srms.srms_app.service.ExcelUploadService;
import com.srms.srms_app.service.StudentService;
import com.srms.srms_app.service.ExcelUploadService.UploadResult;
import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.ModelAttribute; 
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("allStudents", studentService.findAll());
        return "admin-dashboard";
    }

    // --- YAHAN FIX KIYA HAI (CRASH BUG FIX) ---
    @PostMapping("/upload-students")
    public String uploadStudents(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        // 1. Check karo ki file khaali (empty) toh nahi hai
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: Please select a file to upload.");
            return "redirect:/admin/dashboard";
        }

        // 2. Check karo ki file .xlsx hai ya nahi
        if (!ExcelUploadService.isValidExcelFile(file)) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error: Invalid file type. Please upload a .xlsx file.");
            return "redirect:/admin/dashboard";
        }

        // 3. Bulletproof Try-Catch block
        try {
            // Humne processing ko try block mein daala
            UploadResult result = excelUploadService.processStudentExcel(file);
            
            // Ab hum messages check karenge
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessages", result.getMessages());
            } else {
                redirectAttributes.addFlashAttribute("successMessages", result.getMessages());
                redirectAttributes.addFlashAttribute("processedStudents", result.getProcessedStudents());
            }

        } catch (Exception e) {
            // Agar file format (.xlsx) sahi hai lekin andar ka data (columns/rows) galat hai,
            // toh yeh crash hone ki jagah yeh error dikhayega.
            e.printStackTrace(); // Yeh log file mein error dikhayega
            redirectAttributes.addFlashAttribute("globalErrorMessage", 
                "Error: File is corrupted or data format is incorrect. Please check the file and try again.");
        }

        return "redirect:/admin/dashboard";
    }

    // --- Phase 11: Edit Student (Page Dikhana) ---
    @GetMapping("/edit-student/{id}")
    public String editStudentPage(@PathVariable("id") Long id, Model model) {
        Student student = studentService.findStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        
        model.addAttribute("student", student);
        return "edit-student";
    }

    // --- Phase 11: Edit Student (Data Update Karna) ---
    @PostMapping("/update-student")
    public String updateStudent(@ModelAttribute("student") Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudentDetails(student);
            redirectAttributes.addFlashAttribute("globalSuccessMessage", "Student details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error updating student: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // --- Phase 14: Manual Staff Registration ---
    @PostMapping("/register-staff")
    public String registerStaff(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "classAssigned", required = false) String classAssigned,
            RedirectAttributes redirectAttributes) {

        try {
            studentService.registerNewStaff(name, username, password, role, subject, classAssigned);
            redirectAttributes.addFlashAttribute("globalSuccessMessage", 
                role.replace("ROLE_", "") + " (" + name + ") registered successfully!");
        
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", 
                "Error registering staff: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    // --- Phase 17: Delete Student ---
    @PostMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudentById(id);
            redirectAttributes.addFlashAttribute("globalSuccessMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalErrorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}