package com.quizportal.service;

import com.quizportal.entity.User;

import java.util.List;

public interface StudentService {

    List<User> getAllStudents();

    List<User> searchStudents(String keyword);

    User getStudent(Long id);

    User enableDisableStudent(Long id);

    void deleteStudent(Long id);

}