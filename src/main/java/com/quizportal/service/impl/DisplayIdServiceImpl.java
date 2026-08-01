package com.quizportal.service.impl;

import com.quizportal.entity.DisplayIdSequence;
import com.quizportal.repository.DisplayIdSequenceRepository;
import com.quizportal.service.DisplayIdService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DisplayIdServiceImpl implements DisplayIdService {

    private final DisplayIdSequenceRepository repository;

    public DisplayIdServiceImpl(DisplayIdSequenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateStudentId() {
        return nextId("STU");
    }

    @Override
    public String generateAdminId() {
        return nextId("ADM");
    }

    private String nextId(String prefix) {

        DisplayIdSequence sequence =
                repository.findById(prefix)
                        .orElse(new DisplayIdSequence(prefix, 0L));

        sequence.setSequenceValue(sequence.getSequenceValue() + 1);

        repository.save(sequence);

        return prefix + String.format("%03d", sequence.getSequenceValue());
    }

}