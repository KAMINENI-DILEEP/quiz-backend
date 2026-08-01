package com.quizportal.service.impl;

import com.quizportal.dto.response.ResultResponse;
import com.quizportal.entity.Exam;
import com.quizportal.entity.StudentExam;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.ExamRepository;
import com.quizportal.repository.StudentExamRepository;
import com.quizportal.service.ResultService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    private final ExamRepository examRepository;
    private final StudentExamRepository studentExamRepository;

    public ResultServiceImpl(
            ExamRepository examRepository,
            StudentExamRepository studentExamRepository) {

        this.examRepository = examRepository;
        this.studentExamRepository = studentExamRepository;
    }

    @Override
    public List<ResultResponse> getExamResults(Long examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        List<StudentExam> attempts =
                studentExamRepository.findByExam(exam);

        List<ResultResponse> results = new ArrayList<>();

        for (StudentExam attempt : attempts) {

            ResultResponse response = new ResultResponse();

            response.setStudentName(
                    attempt.getStudent().getName());

            response.setExamTitle(
                    exam.getExamTitle());

            response.setScore(
                    attempt.getScore());

            response.setTotalMarks(
                    exam.getTotalMarks());

            double percentage =
                    (attempt.getScore() * 100.0)
                            / exam.getTotalMarks();

            response.setPercentage(percentage);

            if (attempt.getScore() >= exam.getPassMarks()) {
                response.setResult("PASS");
            } else {
                response.setResult("FAIL");
            }

            results.add(response);
        }

        results.sort(
                Comparator.comparing(ResultResponse::getScore)
                        .reversed());

        return results;
    }
}