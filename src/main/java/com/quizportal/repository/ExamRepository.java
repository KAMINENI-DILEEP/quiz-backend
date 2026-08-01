package com.quizportal.repository;

import com.quizportal.entity.Exam;
import com.quizportal.entity.User;
import com.quizportal.enums.ExamPublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    long countByPublishStatus(ExamPublishStatus publishStatus);
    
    List<Exam> findByPublishStatus(ExamPublishStatus publishStatus);

    List<Exam> findByCreatedBy(User createdBy);

    List<Exam> findByExamTitleContainingIgnoreCase(String keyword);

}