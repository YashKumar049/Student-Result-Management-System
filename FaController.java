package com.srms.srms_app.controller;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.TeacherRepository;
import com.srms.srms_app.service.StudentService;
import org.json.JSONObject; // Naya Import (Phase 16)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map; // Naya Import (Phase 16)

@Controller
@RequestMapping("/fa")
public class FaController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String faDashboard(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();

        Teacher fa = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Faculty Advisor not found"));
        model.addAttribute("fa", fa);

        List<Student> studentsInClass = Collections.emptyList();
        String classAssigned = fa.getClassAssigned();

        if (classAssigned != null && !classAssigned.isEmpty()) {
            studentsInClass = studentService.findByClassSection(classAssigned);
            
            // --- NAYA CODE (PHASE 16) ---
            // 1. Chart ke liye data fetch karo
            Map<String, Integer> chartData = studentService.getPassFailData(classAssigned);
            
            // 2. Data ko JSON string mein convert karo
            String chartDataJson = new JSONObject(chartData).toString().replace("\"", "'");
            
            // 3. Model mein add karo
            model.addAttribute("chartData", chartDataJson);
            // --- END OF NAYA CODE ---
        }

        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("classAssigned", classAssigned);

        return "fa-dashboard";
    }
}