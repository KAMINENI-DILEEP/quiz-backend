package com.quizportal.repository;

import com.quizportal.entity.DisplayIdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayIdSequenceRepository
        extends JpaRepository<DisplayIdSequence, String> {
}