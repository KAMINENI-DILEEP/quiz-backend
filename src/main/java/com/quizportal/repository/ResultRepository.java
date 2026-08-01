package com.quizportal.repository;

import com.quizportal.entity.Exam;
import com.quizportal.entity.Result;
import com.quizportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByStudent(User student);

    List<Result> findByExam(Exam exam);

    Optional<Result> findByExamAndStudent(Exam exam, User student);

}