package com.srms.srms_app.service;

import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    // Get teacher details by username (for profile display)
    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found with username: " + username));
    }

    // Future methods like updateTeacherProfile can be added here
}