package com.quizportal.service.impl;

import com.quizportal.entity.StudentGroup;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.StudentGroupRepository;
import com.quizportal.service.StudentGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentGroupServiceImpl implements StudentGroupService {

    private final StudentGroupRepository repository;

    public StudentGroupServiceImpl(StudentGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public StudentGroup createGroup(StudentGroup group) {
        return repository.save(group);
    }

    @Override
    public StudentGroup updateGroup(Long id, StudentGroup updated) {

        StudentGroup group = getGroup(id);

        group.setGroupName(updated.getGroupName());
        group.setDescription(updated.getDescription());

        return repository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        repository.deleteById(id);
    }

    @Override
    public StudentGroup getGroup(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found"));
    }

    @Override
    public List<StudentGroup> getAllGroups() {
        return repository.findAll();
    }
}