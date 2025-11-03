package com.srms.srms_app.service;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Custom Exceptions ---
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }
    public static class ClassMismatchException extends RuntimeException {
        public ClassMismatchException(String message) {
            super(message);
        }
    }
    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }
    public static class TeacherMismatchException extends RuntimeException {
        public TeacherMismatchException(String message) {
            super(message);
        }
    }
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }
    public static class StaffUsernameAlreadyExistsException extends RuntimeException {
        public StaffUsernameAlreadyExistsException(String message) {
            super(message);
        }
    }
    // --- End of Exceptions ---

    
    // --- Phase 8: View Students Logic ---
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByClassSection(String classSection) {
        return studentRepository.findByClassSection(classSection);
    }
    
    // --- Phase 11: Edit Student Logic ---
    public Optional<Student> findStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // --- Phase 7: Smart Registration Logic (FIXED) ---
    public Student saveStudentWithCalculatedResults(String name, String classSection, String username, String password) 
            throws UsernameAlreadyExistsException { 
        
        if (studentRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Student username '" + username + "' already exists.");
        }

        // --- YAHAN FIX KIYA HAI (WAPAS CLASS-WISE KIYA) ---
        // Hum ab class ke hisaab se max rollno dhoondh rahe hain
        Long maxRollno = studentRepository.findMaxRollnoByClassSection(classSection);
        int newRollNo = (maxRollno == null) ? 1 : maxRollno.intValue() + 1; // Class-wise Serial No.

        Student student = new Student();
        student.setName(name);
        student.setClassSection(classSection);
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(password));
        student.setRollno(newRollNo); // Yeh Class Roll No. hai
        student.setRegistrationDate(LocalDate.now());
        
        student.setMaths(0);
        student.setPhysics(0);
        student.setChemistry(0);
        student.setTotal(0);
        student.setAverage(0.0f);
        student.setResult("N/A");

        return studentRepository.save(student);
    }

    // --- Phase 10: Marks Update Logic (FIXED) ---
    public Student updateStudentMarks(int rollNo, String classSection, String subject, int marks, String teacherUsername) 
            throws StudentNotFoundException, ClassMismatchException, TeacherMismatchException {

        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherUsername));

        Student student = studentRepository.findByRollnoAndClassSection(rollNo, classSection)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with Rollno: " + rollNo + " in class " + classSection));

        if (!teacher.getRole().equals("ROLE_ADMIN")) {
            if (teacher.getClassAssigned() == null || !teacher.getClassAssigned().equals(student.getClassSection())) {
                throw new ClassMismatchException("Rollno " + rollNo + " belongs to class " + student.getClassSection() + ", not your assigned class " + teacher.getClassAssigned());
            }
            if (teacher.getSubject() == null || !teacher.getSubject().equalsIgnoreCase(subject)) {
                 throw new TeacherMismatchException("You are not authorized to update marks for " + subject);
            }
        }
        
        if (subject.equalsIgnoreCase("Maths")) {
            student.setMaths(marks);
        } else if (subject.equalsIgnoreCase("Physics")) {
            student.setPhysics(marks);
        } else if (subject.equalsIgnoreCase("Chemistry")) {
            student.setChemistry(marks);
        }

        calculateResults(student);
        return studentRepository.save(student);
    }

    // --- Phase 11: Edit Student Logic ---
    public void updateStudentDetails(Student studentDataFromForm) {
        Student studentInDb = studentRepository.findById(studentDataFromForm.getId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentDataFromForm.getId()));

        studentInDb.setName(studentDataFromForm.getName());
        studentInDb.setClassSection(studentDataFromForm.getClassSection());
        studentInDb.setUsername(studentDataFromForm.getUsername());
        
        studentRepository.save(studentInDb);
    }

    
    // --- Phase 13: Student Change Password Logic ---
    public void changeStudentPassword(String username, String oldPassword, String newPassword, String confirmPassword)
            throws StudentNotFoundException, PasswordMismatchException {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("New password and confirm password do not match.");
        }
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));

        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            throw new PasswordMismatchException("Incorrect old password.");
        }
        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
    }

    // --- Phase 13: Teacher Change Password Logic ---
    public void changeTeacherPassword(String username, String oldPassword, String newPassword, String confirmPassword)
            throws RuntimeException, PasswordMismatchException {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("New password and confirm password do not match.");
        }
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found."));

        if (!passwordEncoder.matches(oldPassword, teacher.getPassword())) {
            throw new PasswordMismatchException("Incorrect old password.");
        }
        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacherRepository.save(teacher);
    }

    
    // --- Phase 14: Staff Registration Logic ---
    public void registerNewStaff(String name, String username, String password, String role, String subject, String classAssigned)
            throws StaffUsernameAlreadyExistsException {
        
        if (teacherRepository.findByUsername(username).isPresent()) {
            throw new StaffUsernameAlreadyExistsException("Staff username '" + username + "' already exists.");
        }

        Teacher staff = new Teacher();
        staff.setName(name);
        staff.setUsername(username);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(role); 

        if (role.equals("ROLE_TEACHER")) {
            staff.setSubject(subject);
            staff.setClassAssigned(classAssigned);
        }
        else if (role.equals("ROLE_FA")) {
            staff.setSubject(null); 
            staff.setClassAssigned(classAssigned);
        }
        teacherRepository.save(staff);
    }

    // --- Phase 16: FA Chart Logic ---
    public Map<String, Integer> getPassFailData(String classSection) {
        List<Student> students = studentRepository.findByClassSection(classSection);
        int passCount = 0;
        int failCount = 0;

        for (Student student : students) {
            if (student.getResult() == null || student.getResult().equals("N/A")) {
                continue;
            }
            if (student.getResult().equals("Pass")) {
                passCount++;
            } else {
                failCount++;
            }
        }
        
        Map<String, Integer> data = new HashMap<>();
        data.put("Pass", passCount);
        data.put("Fail", failCount);
        return data;
    }


    // --- Phase 17: Delete Student Logic ---
    public void deleteStudentById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }


    // --- Helper Method ---
    private void calculateResults(Student student) {
        int maths = student.getMaths();
        int physics = student.getPhysics();
        int chemistry = student.getChemistry();
        
        int total = maths + physics + chemistry;
        student.setTotal(total);

        float average = total / 3.0f;
        student.setAverage(average);

        if (maths >= 33 && physics >= 33 && chemistry >= 33) {
            student.setResult("Pass");
        } else {
            student.setResult("Fail");
        }
        
        if (maths == 0 && physics == 0 && chemistry == 0) {
            student.setResult("N/A");
        }
    }
}