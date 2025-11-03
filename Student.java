package com.srms.srms_app.entity;

import jakarta.persistence.*; // Use jakarta persistence
import java.time.LocalDate; // Date ke liye import

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Naya Primary Key (Automatic)

    // 'rollno' ab 'Reg. No.' hai, ye Primary Key nahi hai
    // Hum ise logic se generate karenge
    @Column(nullable = false)
    private int rollno; 
    // --- End of Change ---

    @Column(nullable = false)
    private String name;

    @Column(name = "class_section", nullable = false)
    private String classSection;

    private int maths;
    private int physics;
    private int chemistry;
    private int total;
    private float average;
    private String result;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // --- NAYA FIELD (PHASE 7) ---
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    // --- End of Naya Field ---


    // --- Getters and Setters ---
    // (Aapke IDE mein poore getters/setters generate kar lein)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRollno() { return rollno; }
    public void setRollno(int rollno) { this.rollno = rollno; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassSection() { return classSection; }
    public void setClassSection(String classSection) { this.classSection = classSection; }

    public int getMaths() { return maths; }
    public void setMaths(int maths) { this.maths = maths; }

    public int getPhysics() { return physics; }
    public void setPhysics(int physics) { this.physics = physics; }

    public int getChemistry() { return chemistry; }
    public void setChemistry(int chemistry) { this.chemistry = chemistry; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public float getAverage() { return average; }
    public void setAverage(float average) { this.average = average; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}

