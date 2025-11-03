package com.srms.srms_app.service;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelUploadService {

    @Autowired
    private StudentService studentService; // Use StudentService

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // --- Helper Class (Yeh naye Controllers se match karta hai) ---
    public static class UploadResult {
        private final List<String> messages;
        private final List<Student> processedStudents;
        private boolean hasErrors = false; // Error tracking ke liye

        public UploadResult(List<String> messages, List<Student> processedStudents) {
            this.messages = messages;
            this.processedStudents = processedStudents;
        }

        public List<String> getMessages() { return messages; }
        public List<Student> getProcessedStudents() { return processedStudents; }
        public boolean hasErrors() { return hasErrors; }
        public void setHasErrors(boolean hasErrors) { this.hasErrors = hasErrors; }
    }


    // Helper method to check file type
    public static boolean isValidExcelFile(MultipartFile file) {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
    }

    // Helper method to get cell value safely
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cellType == CellType.NUMERIC) {
            // Rollno/Marks ko integer ki tarah padhna
            return String.valueOf((int) cell.getNumericCellValue());
        }
        if (cellType == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (IllegalStateException e) {
                return String.valueOf((int) cell.getNumericCellValue());
            }
        }
        return "";
    }

    // --- Admin Upload Logic (Crash-proof) ---
    public UploadResult processStudentExcel(MultipartFile file) {
        List<String> messages = new ArrayList<>();
        List<Student> processedStudents = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Header row check karo
            if (!rows.hasNext()) {
                throw new RuntimeException("File is empty.");
            }
            Row headerRow = rows.next();
            if (!getCellStringValue(headerRow.getCell(0)).equalsIgnoreCase("name") ||
                !getCellStringValue(headerRow.getCell(1)).equalsIgnoreCase("classSection")) {
                throw new RuntimeException("File format is incorrect. Columns must be: name, classSection, username, password");
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int rowNum = currentRow.getRowNum() + 1;

                try {
                    String name = getCellStringValue(currentRow.getCell(0));
                    String classSection = getCellStringValue(currentRow.getCell(1));
                    String username = getCellStringValue(currentRow.getCell(2));
                    String password = getCellStringValue(currentRow.getCell(3));

                    if (name.isEmpty() || classSection.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        throw new Exception("Row " + rowNum + " has missing data. Skipped.");
                    }
                    
                    Student savedStudent = studentService.saveStudentWithCalculatedResults(name, classSection, username, password); 
                    processedStudents.add(savedStudent);
                    successCount++;

                } catch (StudentService.UsernameAlreadyExistsException e) {
                    errorCount++;
                    messages.add("Error: Row " + rowNum + " skipped. " + e.getMessage());
                } catch (Exception e) {
                    errorCount++;
                    messages.add("Error: " + e.getMessage());
                }
            }

            if (successCount > 0) {
                messages.add(0, "Success! " + successCount + " new students registered.");
            }
            if (errorCount > 0) {
                messages.add(0, errorCount + " row(s) had errors and were skipped.");
            }
            
            UploadResult result = new UploadResult(messages, processedStudents);
            if(errorCount > 0) result.setHasErrors(true);
            return result;

        } catch (Exception e) {
            // Yeh poori file ke errors (jaise galat format) ko pakdega
            messages.add("File processing failed: " + e.getMessage());
            UploadResult result = new UploadResult(messages, processedStudents);
            result.setHasErrors(true);
            return result;
        }
    }

    // --- Teacher Marks Upload Logic (Crash-proof) ---
    public UploadResult processMarksExcel(MultipartFile file, String subject, String classAssigned, String teacherUsername) {
        List<String> messages = new ArrayList<>();
        List<Student> processedStudents = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Header row check karo
            if (!rows.hasNext()) {
                throw new RuntimeException("File is empty.");
            }
            Row headerRow = rows.next();
            if (!getCellStringValue(headerRow.getCell(0)).equalsIgnoreCase("rollno") ||
                !getCellStringValue(headerRow.getCell(2)).equalsIgnoreCase(subject)) {
                throw new RuntimeException("File format is incorrect. Columns must be: rollno, name, " + subject);
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int rowNum = currentRow.getRowNum() + 1;

                try {
                    Cell rollnoCell = currentRow.getCell(0);
                    if (rollnoCell == null) throw new Exception("Row " + rowNum + " has missing rollno.");
                    int rollno = (int) rollnoCell.getNumericCellValue();

                    Cell marksCell = currentRow.getCell(2); // Column C (index 2)
                    if (marksCell == null) throw new Exception("Row " + rowNum + " has missing marks.");
                    int marks = (int) marksCell.getNumericCellValue();
                    
                    Student updatedStudent = studentService.updateStudentMarks(rollno, classAssigned, subject, marks, teacherUsername);

                    processedStudents.add(updatedStudent);
                    successCount++;

                } catch (StudentService.StudentNotFoundException | StudentService.ClassMismatchException | StudentService.TeacherMismatchException e) {
                    errorCount++;
                    messages.add("Error: Row " + (rowNum) + " skipped. " + e.getMessage());
                } catch (NumberFormatException e) {
                    errorCount++;
                    messages.add("Error: Row " + (rowNum) + " has invalid number format. Skipped.");
                } catch (Exception e) {
                    errorCount++;
                    messages.add("Error: Row " + (rowNum) + " (" + e.getMessage() + "). Skipped.");
                }
            }

            if (successCount > 0) {
                messages.add(0, "Success! " + successCount + " students' marks were updated.");
            }
            if (errorCount > 0) {
                messages.add(0, errorCount + " row(s) had errors and were skipped.");
            }

            UploadResult result = new UploadResult(messages, processedStudents);
            if(errorCount > 0) result.setHasErrors(true);
            return result;

        } catch (Exception e) {
            // Yeh poori file ke errors (jaise galat format) ko pakdega
            messages.add("File processing failed: " + e.getMessage());
            UploadResult result = new UploadResult(messages, processedStudents);
            result.setHasErrors(true);
            return result;
        }
    }
}