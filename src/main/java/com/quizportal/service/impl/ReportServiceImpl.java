package com.quizportal.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.quizportal.dto.response.ResultResponse;
import com.quizportal.service.ReportService;
import com.quizportal.service.ResultService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ResultService resultService;

    public ReportServiceImpl(ResultService resultService) {
        this.resultService = resultService;
    }

    @Override
    public byte[] generateResultPdf(Long examId) {

        try {

            List<ResultResponse> results =
                    resultService.getExamResults(examId);

            Document document = new Document();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Exam Result Report"));
            document.add(new Paragraph(" "));

            for (ResultResponse r : results) {

                document.add(new Paragraph(
                        "Student : " + r.getStudentName()));

                document.add(new Paragraph(
                        "Score : " + r.getScore()));

                document.add(new Paragraph(
                        "Percentage : " + r.getPercentage()));

                document.add(new Paragraph(
                        "Result : " + r.getResult()));

                document.add(new Paragraph("--------------------------"));
            }

            document.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }
    }

    @Override
    public byte[] generateResultExcel(Long examId) {

        try {

            List<ResultResponse> results =
                    resultService.getExamResults(examId);

            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet =
                    workbook.createSheet("Results");

            int rowNum = 0;

            Row header = sheet.createRow(rowNum++);

            header.createCell(0).setCellValue("Student");
            header.createCell(1).setCellValue("Score");
            header.createCell(2).setCellValue("Percentage");
            header.createCell(3).setCellValue("Result");

            for (ResultResponse r : results) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(r.getStudentName());
                row.createCell(1).setCellValue(r.getScore());
                row.createCell(2).setCellValue(r.getPercentage());
                row.createCell(3).setCellValue(r.getResult());
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);
            workbook.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }
    }
}