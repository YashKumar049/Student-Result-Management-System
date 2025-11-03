package com.srms.srms_app.repository;

import com.srms.srms_app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    List<Student> findByClassSection(String classSection);

    // --- YEH HAI SAHI LOGIC (PHASE 7) ---
    // Humne isko wapas Class-wise kar diya hai
    @Query("SELECT MAX(s.rollno) FROM Student s WHERE s.classSection = :classSection")
    Long findMaxRollnoByClassSection(String classSection); // Yeh class ke andar MAX rollno dhoondhega

    Optional<Student> findByRollnoAndClassSection(int rollno, String classSection);
}