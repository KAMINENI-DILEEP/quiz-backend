package com.quizportal.repository;

import com.quizportal.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentGroupRepository
        extends JpaRepository<StudentGroup, Long> {

    List<StudentGroup> findByGroupNameContainingIgnoreCase(String keyword);

}