package com.srms.srms_app.config;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.repository.TeacherRepository;
import com.srms.srms_app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader is running...");

        // Pehle check karo ki teachers hain ya nahi, agar nahi hain tabhi data load karo
        if (teacherRepository.count() == 0) {
            System.out.println("No existing data found. Creating new data...");

            // --- 1. ADMIN USER BANAO ---
            Teacher admin = new Teacher();
            admin.setTeacherId(1);
            admin.setName("Admin User");
            admin.setEmail("admin@srms.edu");
            admin.setContactNo("9000000001");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ROLE_ADMIN");
            teacherRepository.save(admin);
            System.out.println("Created new ADMIN user: " + admin.getUsername());

            // --- 2. SRM FACULTY KE NAAM PAR TEACHERS BANAO ---
            
            // Physics Teachers
            Teacher phyTeacher9 = new Teacher();
            phyTeacher9.setTeacherId(101);
            phyTeacher9.setName("Dr. Revathi Venkataraman");
            phyTeacher9.setSubject("Physics");
            phyTeacher9.setClassAssigned("9-A"); // Assigning class
            phyTeacher9.setEmail("revathi.v@srms.edu");
            phyTeacher9.setContactNo("9884000001");
            phyTeacher9.setUsername("revathi_phy");
            phyTeacher9.setPassword(passwordEncoder.encode("teacherpass"));
            phyTeacher9.setRole("ROLE_TEACHER");
            teacherRepository.save(phyTeacher9);

            Teacher phyTeacher10 = new Teacher();
            phyTeacher10.setTeacherId(102);
            phyTeacher10.setName("Dr. Vijayakumar K");
            phyTeacher10.setSubject("Physics");
            phyTeacher10.setClassAssigned("10-A"); // Assigning class
            phyTeacher10.setEmail("vijayakumar.k@srms.edu");
            phyTeacher10.setContactNo("9884000002");
            phyTeacher10.setUsername("vijay_phy");
            phyTeacher10.setPassword(passwordEncoder.encode("teacherpass"));
            phyTeacher10.setRole("ROLE_TEACHER");
            teacherRepository.save(phyTeacher10);

            // Maths Teachers
            Teacher mathTeacher9 = new Teacher();
            mathTeacher9.setTeacherId(201);
            mathTeacher9.setName("Dr. Sridhar S S");
            mathTeacher9.setSubject("Maths");
            mathTeacher9.setClassAssigned("9-A"); // Assigning class
            mathTeacher9.setEmail("sridhar.s@srms.edu");
            mathTeacher9.setContactNo("9884000003");
            mathTeacher9.setUsername("sridhar_math");
            mathTeacher9.setPassword(passwordEncoder.encode("teacherpass"));
            mathTeacher9.setRole("ROLE_TEACHER");
            teacherRepository.save(mathTeacher9);
            
            Teacher mathTeacher10 = new Teacher();
            mathTeacher10.setTeacherId(202);
            mathTeacher10.setName("Dr. Ritesh Kumar Dubey");
            mathTeacher10.setSubject("Maths");
            mathTeacher10.setClassAssigned("10-A"); // Assigning class
            mathTeacher10.setEmail("ritesh.k@srms.edu");
            mathTeacher10.setContactNo("9884000004");
            mathTeacher10.setUsername("ritesh_math");
            mathTeacher10.setPassword(passwordEncoder.encode("teacherpass"));
            mathTeacher10.setRole("ROLE_TEACHER");
            teacherRepository.save(mathTeacher10);

            // Chemistry Teachers
            Teacher chemTeacher9 = new Teacher();
            chemTeacher9.setTeacherId(301);
            chemTeacher9.setName("Dr. Lakshmi C");
            chemTeacher9.setSubject("Chemistry");
            chemTeacher9.setClassAssigned("9-A"); // Assigning class
            chemTeacher9.setEmail("lakshmi.c@srms.edu");
            chemTeacher9.setContactNo("9884000005");
            chemTeacher9.setUsername("lakshmi_chem");
            chemTeacher9.setPassword(passwordEncoder.encode("teacherpass"));
            chemTeacher9.setRole("ROLE_TEACHER");
            teacherRepository.save(chemTeacher9);

            Teacher chemTeacher10 = new Teacher();
            chemTeacher10.setTeacherId(302);
            chemTeacher10.setName("Dr. Prakash Muthuramalingam");
            chemTeacher10.setSubject("Chemistry");
            chemTeacher10.setClassAssigned("10-A"); // Assigning class
            chemTeacher10.setEmail("prakash.m@srms.edu");
            chemTeacher10.setContactNo("9884000006");
            chemTeacher10.setUsername("prakash_chem");
            chemTeacher10.setPassword(passwordEncoder.encode("teacherpass"));
            chemTeacher10.setRole("ROLE_TEACHER");
            teacherRepository.save(chemTeacher10);

            // --- 3. FACULTY ADVISORS (FAs) BANAO ---
            Teacher fa9A = new Teacher();
            fa9A.setTeacherId(901);
            fa9A.setName("FA for 9-A (Dr. M. Parani)");
            fa9A.setClassAssigned("9-A");
            fa9A.setEmail("fa.9a@srms.edu");
            fa9A.setContactNo("9000009001");
            fa9A.setUsername("fa_9a");
            fa9A.setPassword(passwordEncoder.encode("fapass"));
            fa9A.setRole("ROLE_FA");
            teacherRepository.save(fa9A);

            Teacher fa10A = new Teacher();
            fa10A.setTeacherId(902);
            fa10A.setName("FA for 10-A (Dr. S. Sahabudeen)");
            fa10A.setClassAssigned("10-A");
            fa10A.setEmail("fa.10a@srms.edu");
            fa10A.setContactNo("9000009002");
            fa10A.setUsername("fa_10a");
            fa10A.setPassword(passwordEncoder.encode("fapass"));
            fa10A.setRole("ROLE_FA");
            teacherRepository.save(fa10A);

            Teacher fa6A = new Teacher();
            fa6A.setTeacherId(903);
            fa6A.setName("FA for 6-A");
            fa6A.setClassAssigned("6-A");
            fa6A.setEmail("fa.6a@srms.edu");
            fa6A.setContactNo("9000009003");
            fa6A.setUsername("fa_6a");
            fa6A.setPassword(passwordEncoder.encode("fapass"));
            fa6A.setRole("ROLE_FA");
            teacherRepository.save(fa6A);

            // --- 4. STUDENTS (CLASS 6-12) BANAO ---
            // Naye "Smart Registration System" (Phase 7) ka istemaal karke
            
            try {
                // Class 6-A
                studentService.saveStudentWithCalculatedResults("Aarav Gupta", "6-A", "aarav6a", "studentpass");
                studentService.saveStudentWithCalculatedResults("Mira Singh", "6-A", "mira6a", "studentpass");
                
                // Class 7-A
                studentService.saveStudentWithCalculatedResults("Rohan Sharma", "7-A", "rohan7a", "studentpass");
                studentService.saveStudentWithCalculatedResults("Sameer Ali", "7-A", "sameer7a", "studentpass");
                
                // Class 8-B
                studentService.saveStudentWithCalculatedResults("Priya Singh", "8-B", "priya8b", "studentpass");
                
                // Class 9-A
                studentService.saveStudentWithCalculatedResults("Vihaan Reddy", "9-A", "vihaan9a", "studentpass");
                studentService.saveStudentWithCalculatedResults("Anika Verma", "9-A", "anika9a", "studentpass");
                
                // Class 10-A
                studentService.saveStudentWithCalculatedResults("Student One", "10-A", "student1", "studentpass");
                
                // Class 11-A
                studentService.saveStudentWithCalculatedResults("Kabir Kumar", "11-A", "kabir11a", "studentpass");
                
                // Class 12-A
                studentService.saveStudentWithCalculatedResults("Diya Mehta", "12-A", "diya12a", "studentpass");

                System.out.println("Created new students with auto-generated roll numbers.");
                
                // --- 5. KUCH STUDENTS KE MARKS UPDATE KARO ---
                // (Rollno, Class, Subject, Marks, TeacherUsername)
                
                // Student One (Rollno 1 in 10-A)
                studentService.updateStudentMarks(1, "10-A", "Physics", 95, "vijay_phy");
                studentService.updateStudentMarks(1, "10-A", "Maths", 75, "ritesh_math");
                studentService.updateStudentMarks(1, "10-A", "Chemistry", 85, "prakash_chem");
                
                // Aarav Gupta (Rollno 1 in 6-A)
                studentService.updateStudentMarks(1, "6-A", "Maths", 80, "admin"); // Admin can update
                
                // Vihaan Reddy (Rollno 1 in 9-A)
                studentService.updateStudentMarks(1, "9-A", "Physics", 88, "revathi_phy");
                studentService.updateStudentMarks(1, "9-A", "Maths", 92, "sridhar_math");
                studentService.updateStudentMarks(1, "9-A", "Chemistry", 90, "lakshmi_chem");

                System.out.println("Successfully updated marks for sample students.");

            } catch (Exception e) {
                System.out.println("Error during student data loading: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            System.out.println("Database is already populated. Skipping data loading.");
        }
        
        System.out.println("DataLoader finished.");
    }
}