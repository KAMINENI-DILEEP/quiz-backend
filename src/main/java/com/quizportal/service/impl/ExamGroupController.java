package com.quizportal.controller;

import com.quizportal.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-groups")
@CrossOrigin("*")
public class ExamGroupController {

    private final ExamService examService;

    public ExamGroupController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/{examId}/{groupId}")
    public void assign(@PathVariable Long examId,
                       @PathVariable Long groupId) {

        examService.assignGroup(examId, groupId);
    }

    @DeleteMapping("/{examId}/{groupId}")
    public void remove(@PathVariable Long examId,
                       @PathVariable Long groupId) {

        examService.removeGroup(examId, groupId);
    }

    @GetMapping("/{examId}")
    public List<Long> assignedGroups(@PathVariable Long examId) {

        return examService.getAssignedGroups(examId);
    }
}