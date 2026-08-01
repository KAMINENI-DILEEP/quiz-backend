package com.quizportal.controller;

import com.quizportal.entity.StudentAnswer;
import com.quizportal.entity.StudentExam;
import com.quizportal.service.StudentExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-exams")
@CrossOrigin("*")
public class StudentExamController {

    private final StudentExamService service;

    public StudentExamController(StudentExamService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public StudentExam startExam(
            @RequestParam Long studentId,
            @RequestParam Long examId) {

        return service.startExam(studentId, examId);
    }

    @PostMapping("/{attemptId}/answer")
    public StudentAnswer saveAnswer(
            @PathVariable Long attemptId,
            @RequestParam Long questionId,
            @RequestParam String selectedAnswer) {

        return service.saveAnswer(
                attemptId,
                questionId,
                selectedAnswer);
    }

    @PostMapping("/{attemptId}/submit")
    public StudentExam submitExam(
            @PathVariable Long attemptId) {

        return service.submitExam(attemptId);
    }

    @GetMapping("/student/{studentId}")
    public List<StudentExam> attempts(
            @PathVariable Long studentId) {

        return service.getStudentAttempts(studentId);
    }
}