package com.quizportal.service;

public interface ReportService {

    byte[] generateResultPdf(Long examId);

    byte[] generateResultExcel(Long examId);

}