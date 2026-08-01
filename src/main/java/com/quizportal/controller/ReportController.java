package com.quizportal.controller;

import com.quizportal.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/result/{examId}")
    public ResponseEntity<byte[]> resultPdf(@PathVariable Long examId) {

        return ResponseEntity.ok(reportService.generateResultPdf(examId));
    }

    @GetMapping("/result/excel/{examId}")
    public ResponseEntity<byte[]> resultExcel(@PathVariable Long examId) {

        return ResponseEntity.ok(reportService.generateResultExcel(examId));
    }
}