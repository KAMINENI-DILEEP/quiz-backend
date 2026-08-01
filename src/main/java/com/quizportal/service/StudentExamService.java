package com.quizportal.service;

import com.quizportal.entity.StudentAnswer;
import com.quizportal.entity.StudentExam;

import java.util.List;

public interface StudentExamService {

    StudentExam startExam(Long studentId, Long examId);

    StudentAnswer saveAnswer(
            Long attemptId,
            Long questionId,
            String selectedAnswer
    );

    StudentExam submitExam(Long attemptId);

    List<StudentExam> getStudentAttempts(Long studentId);

}