package com.exam.service;

import com.exam.model.DisplayIdSequence;
import com.exam.model.User;
import com.exam.repository.DisplayIdSequenceRepository;
import com.exam.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayIdService {
    private final UserRepository userRepository;
    private final DisplayIdSequenceRepository sequenceRepository;

    public DisplayIdService(UserRepository userRepository, DisplayIdSequenceRepository sequenceRepository) {
        this.userRepository = userRepository;
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public synchronized String next(User.Role role) {
        String prefix = role == User.Role.ADMIN ? "ADM" : "STU";
        DisplayIdSequence sequence = sequenceRepository.findById(prefix).orElseGet(() ->
                new DisplayIdSequence(prefix, existingMaximum(prefix)));
        long next = sequence.getLastValue() + 1;
        sequence.setLastValue(next);
        sequenceRepository.save(sequence);
        return String.format("%s%03d", prefix, next);
    }

    private long existingMaximum(String prefix) {
        return userRepository.findAll().stream()
                .filter(u -> u.getDisplayId() != null && u.getDisplayId().startsWith(prefix))
                .map(User::getDisplayId)
                .mapToLong(id -> parseNumber(id, prefix))
                .max().orElse(0L);
    }

    private long parseNumber(String id, String prefix) {
        try { return Long.parseLong(id.substring(prefix.length())); }
        catch (Exception ignored) { return 0L; }
    }
}
