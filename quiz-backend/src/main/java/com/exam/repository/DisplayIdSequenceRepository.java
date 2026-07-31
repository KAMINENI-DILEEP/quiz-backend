package com.exam.repository;

import com.exam.model.DisplayIdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayIdSequenceRepository extends JpaRepository<DisplayIdSequence, String> {}
