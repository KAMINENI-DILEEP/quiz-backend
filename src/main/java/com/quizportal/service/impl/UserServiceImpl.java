package com.quizportal.service.impl;

import com.quizportal.entity.User;
import com.quizportal.enums.Role;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.UserRepository;
import com.quizportal.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    // ================= Authentication =================

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    // ================= Student Management =================

    @Override
    public List<User> getAllStudents() {
        return repository.findByRole(Role.STUDENT);
    }

    @Override
    public List<User> searchStudents(String keyword) {
        return repository.findByRoleAndNameContainingIgnoreCase(
                Role.STUDENT,
                keyword
        );
    }

    @Override
    public User getStudent(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));
    }

    @Override
    public User enableDisableStudent(Long id) {

        User user = getStudent(id);

        user.setAccountEnabled(!user.isAccountEnabled());

        return repository.save(user);
    }
}