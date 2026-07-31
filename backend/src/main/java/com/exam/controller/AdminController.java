package com.exam.controller;

import com.exam.model.StudentExam;
import com.exam.repository.StudentExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private StudentExamRepository studentExamRepository;

    @GetMapping("/results")
    public ResponseEntity<List<StudentExam>> getAllResults(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false, defaultValue = "high-to-low") String sort) {
        
        List<StudentExam> results = studentExamRepository.findAll();

        if (gender != null && !gender.isEmpty() && !gender.equalsIgnoreCase("ALL")) {
            results = results.stream()
                    .filter(res -> res.getUser() != null && gender.equalsIgnoreCase(res.getUser().getGender()))
                    .collect(Collectors.toList());
        }

        if ("low-to-high".equalsIgnoreCase(sort)) {
            results.sort(Comparator.comparing(StudentExam::getScore, Comparator.nullsLast(Comparator.naturalOrder())));
        } else {
            results.sort(Comparator.comparing(StudentExam::getScore, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return ResponseEntity.ok(results);
    }
}
