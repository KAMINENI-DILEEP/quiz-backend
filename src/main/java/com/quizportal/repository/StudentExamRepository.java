package com.quizportal.repository;

import com.quizportal.entity.Exam;
import com.quizportal.entity.StudentExam;
import com.quizportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    long countBySubmittedTrue();
    
    List<StudentExam> findByStudent(User student);

    List<StudentExam> findByExam(Exam exam);

    Optional<StudentExam> findByStudentAndExam(User student, Exam exam);

}