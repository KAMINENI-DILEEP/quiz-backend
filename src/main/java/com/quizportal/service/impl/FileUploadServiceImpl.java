package com.quizportal.service.impl;

import com.quizportal.entity.Exam;
import com.quizportal.entity.Question;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.ExamRepository;
import com.quizportal.repository.QuestionRepository;
import com.quizportal.service.FileUploadService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public FileUploadServiceImpl(
            ExamRepository examRepository,
            QuestionRepository questionRepository) {

        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void importQuestions(Long examId, MultipartFile file) {

        try {

            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Exam not found"));

            InputStream inputStream = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            boolean firstRow = true;

            for (Row row : sheet) {

                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                Question question = new Question();

                question.setExam(exam);
                question.setQuestionText(row.getCell(0).getStringCellValue());
                question.setOptionA(row.getCell(1).getStringCellValue());
                question.setOptionB(row.getCell(2).getStringCellValue());
                question.setOptionC(row.getCell(3).getStringCellValue());
                question.setOptionD(row.getCell(4).getStringCellValue());
                question.setCorrectAnswer(row.getCell(5).getStringCellValue());

                questionRepository.save(question);
            }

            workbook.close();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to import questions", ex);
        }
    }
}