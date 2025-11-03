package com.srms.srms_app.service;

import com.srms.srms_app.entity.Student;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
public class PdfService {

    public ByteArrayInputStream generateReportCard(Student student) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Header ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLUE);
            Paragraph title = new Paragraph("Student Result Management System", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph reportTitle = new Paragraph("Official Report Card", headerFont);
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setSpacingAfter(20);
            document.add(reportTitle);

            // --- Student Info Table (2 column) ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            addCellToTable(infoTable, "Student Name:", student.getName());
            addCellToTable(infoTable, "Registration No.:", String.valueOf(student.getRollno()));
            addCellToTable(infoTable, "Class Section:", student.getClassSection());
            addCellToTable(infoTable, "Registration Date:", student.getRegistrationDate().toString());

            document.add(infoTable);

            // --- Marks Table (2 column) ---
            PdfPTable marksTable = new PdfPTable(2);
            marksTable.setWidthPercentage(100);
            
            // Header for Marks
            Font tableHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            PdfPCell headCell1 = new PdfPCell(new Paragraph("Subject", tableHeadFont));
            PdfPCell headCell2 = new PdfPCell(new Paragraph("Marks", tableHeadFont));
            headCell1.setBackgroundColor(Color.DARK_GRAY);
            headCell2.setBackgroundColor(Color.DARK_GRAY);
            headCell1.setPadding(5);
            headCell2.setPadding(5);
            marksTable.addCell(headCell1);
            marksTable.addCell(headCell2);

            // Marks Data
            addCellToTable(marksTable, "Maths", String.valueOf(student.getMaths()));
            addCellToTable(marksTable, "Physics", String.valueOf(student.getPhysics()));
            addCellToTable(marksTable, "Chemistry", String.valueOf(student.getChemistry()));

            // --- Summary ---
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            
            PdfPCell totalCellLabel = new PdfPCell(new Paragraph("Total Marks", boldFont));
            PdfPCell totalCellValue = new PdfPCell(new Paragraph(String.valueOf(student.getTotal()), boldFont));
            totalCellLabel.setPadding(5);
            totalCellValue.setPadding(5);
            marksTable.addCell(totalCellLabel);
            marksTable.addCell(totalCellValue);
            
            addCellToTable(marksTable, "Average", String.format("%.2f", student.getAverage()));

            // Result (Pass/Fail)
            PdfPCell resultCellLabel = new PdfPCell(new Paragraph("Final Result", boldFont));
            PdfPCell resultCellValue = new PdfPCell(new Paragraph(student.getResult(), boldFont));
            if(student.getResult().equals("Pass")) {
                resultCellValue.setBackgroundColor(new Color(230, 255, 230)); // Light Green
            } else {
                resultCellValue.setBackgroundColor(new Color(255, 230, 230)); // Light Red
            }
            resultCellLabel.setPadding(5);
            resultCellValue.setPadding(5);
            marksTable.addCell(resultCellLabel);
            marksTable.addCell(resultCellValue);

            document.add(marksTable);

            // --- Footer ---
            Paragraph footer = new Paragraph("\n\n(This is a digitally generated report card)", FontFactory.getFont(FontFactory.TIMES_ITALIC, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Helper method
    private void addCellToTable(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        
        PdfPCell cellLabel = new PdfPCell(new Paragraph(label, labelFont));
        cellLabel.setPadding(5);
        cellLabel.setBackgroundColor(new Color(240, 240, 240)); // Light Gray
        
        PdfPCell cellValue = new PdfPCell(new Paragraph(value, valueFont));
        cellValue.setPadding(5);
        
        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
}