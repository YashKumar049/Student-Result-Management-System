package com.srms.srms_app.entity;

import jakarta.persistence.*; // Use jakarta persistence

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private int teacherId;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(name = "contact_no")
    private String contactNo;

    private String subject; // Null for Admin/FA

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_ADMIN, ROLE_TEACHER, ROLE_FA

    @Column(name = "class_assigned")
    private String classAssigned; // Null if not FA

    // --- Getters and Setters ---
    // (Generate getters and setters for all fields using your IDE)

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getClassAssigned() { return classAssigned; }
    public void setClassAssigned(String classAssigned) { this.classAssigned = classAssigned; }
}