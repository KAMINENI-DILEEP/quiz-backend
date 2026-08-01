package com.quizportal.controller;

import com.quizportal.entity.Question;
import com.quizportal.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @PostMapping("/{examId}")
    public Question addQuestion(@PathVariable Long examId,
                                @RequestBody Question question) {
        return service.addQuestion(examId, question);
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable Long id,
                           @RequestBody Question question) {
        return service.updateQuestion(id, question);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteQuestion(id);
    }

    @GetMapping("/{id}")
    public Question get(@PathVariable Long id) {
        return service.getQuestion(id);
    }

    @GetMapping("/exam/{examId}")
    public List<Question> examQuestions(@PathVariable Long examId) {
        return service.getQuestionsByExam(examId);
    }
}