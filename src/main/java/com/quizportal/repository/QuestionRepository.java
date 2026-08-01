package com.quizportal.repository;

import com.quizportal.entity.Exam;
import com.quizportal.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByExam(Exam exam);

    long countByExam(Exam exam);

    void deleteByExam(Exam exam);

}