package com.quizportal.repository;

import com.quizportal.entity.Exam;
import com.quizportal.entity.ExamGroup;
import com.quizportal.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamGroupRepository extends JpaRepository<ExamGroup, Long> {

    List<ExamGroup> findByExam(Exam exam);

    List<ExamGroup> findByGroup(StudentGroup group);

    boolean existsByExamAndGroup(Exam exam, StudentGroup group);

    void deleteByExamAndGroup(Exam exam, StudentGroup group);

}