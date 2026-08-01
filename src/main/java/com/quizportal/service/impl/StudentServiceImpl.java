package com.quizportal.service.impl;

import com.quizportal.entity.User;
import com.quizportal.enums.Role;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.UserRepository;
import com.quizportal.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;

    public StudentServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT);
    }

    @Override
    public List<User> searchStudents(String keyword) {
        return userRepository.findByRoleAndNameContainingIgnoreCase(
                Role.STUDENT,
                keyword);
    }

    @Override
    public User getStudent(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    @Override
    public User enableDisableStudent(Long id) {

        User student = getStudent(id);

        student.setAccountEnabled(!student.isAccountEnabled());

        return userRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {

        userRepository.deleteById(id);

    }
}