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
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    @Autowired
    private StudentExamRepository studentExamRepository;

    @GetMapping
    public ResponseEntity<List<StudentExam>> getLeaderboard() {
        List<StudentExam> leaderboard = studentExamRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StudentExam::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }
}
