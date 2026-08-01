package com.quizportal.controller;

import com.quizportal.dto.response.ResultResponse;
import com.quizportal.service.ResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin("*")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/{examId}")
    public List<ResultResponse> getResults(
            @PathVariable Long examId) {

        return resultService.getExamResults(examId);
    }
}