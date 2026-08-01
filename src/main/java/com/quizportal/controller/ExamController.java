package com.quizportal.controller;

import com.quizportal.entity.Exam;
import com.quizportal.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin("*")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examService.createExam(exam);
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{id}")
    public Exam getExam(@PathVariable Long id) {
        return examService.getExam(id);
    }

    @PutMapping("/{id}")
    public Exam updateExam(@PathVariable Long id,
                           @RequestBody Exam exam) {
        return examService.updateExam(id, exam);
    }

    @DeleteMapping("/{id}")
    public void deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
    }

    @PutMapping("/{id}/publish")
    public Exam publishExam(@PathVariable Long id) {
        return examService.publishExam(id);
    }

    @PostMapping("/{examId}/groups/{groupId}")
    public void assignGroup(@PathVariable Long examId,
                            @PathVariable Long groupId) {
        examService.assignGroup(examId, groupId);
    }

    @DeleteMapping("/{examId}/groups/{groupId}")
    public void removeGroup(@PathVariable Long examId,
                            @PathVariable Long groupId) {
        examService.removeGroup(examId, groupId);
    }

    @GetMapping("/{examId}/groups")
    public List<Long> assignedGroups(@PathVariable Long examId) {
        return examService.getAssignedGroups(examId);
    }
}