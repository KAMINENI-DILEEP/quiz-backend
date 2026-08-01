package com.quizportal.service;

import com.quizportal.entity.User;

import java.util.List;

public interface UserService {

    // Authentication
    User save(User user);

    User findByEmail(String email);

    boolean emailExists(String email);

    List<User> findAll();

    // Student Management
    List<User> getAllStudents();

    List<User> searchStudents(String keyword);

    User getStudent(Long id);

    User enableDisableStudent(Long id);

}