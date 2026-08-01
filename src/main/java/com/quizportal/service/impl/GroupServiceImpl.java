package com.quizportal.service.impl;

import com.quizportal.entity.StudentGroup;
import com.quizportal.repository.StudentGroupRepository;
import com.quizportal.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final StudentGroupRepository repository;

    public GroupServiceImpl(StudentGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public StudentGroup save(StudentGroup group) {
        return repository.save(group);
    }

    @Override
    public List<StudentGroup> getAll() {
        return repository.findAll();
    }

    @Override
    public List<StudentGroup> search(String keyword) {
        return repository.findByGroupNameContainingIgnoreCase(keyword);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}