package com.srms.srms_app.service;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.repository.TeacherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service // Mark this as the primary UserDetailsService bean
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username); // Debug log

        // 1. Check Teachers
        Optional<Teacher> teacherOpt = teacherRepository.findByUsername(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            System.out.println("Found Teacher: " + username + " with role " + teacher.getRole()); // Debug log
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(teacher.getRole()));
            return new User(teacher.getUsername(), teacher.getPassword(), authorities);
        }

        // 2. Check Students
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
             System.out.println("Found Student: " + username); // Debug log
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
            return new User(student.getUsername(), student.getPassword(), authorities);
        }

        // 3. User Not Found
         System.out.println("User not found: " + username); // Debug log
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}