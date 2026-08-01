package com.quizportal.repository;

import com.quizportal.entity.Question;
import com.quizportal.entity.StudentAnswer;
import com.quizportal.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByStudentExam(StudentExam studentExam);

    Optional<StudentAnswer> findByStudentExamAndQuestion(
            StudentExam studentExam,
            Question question
    );
}