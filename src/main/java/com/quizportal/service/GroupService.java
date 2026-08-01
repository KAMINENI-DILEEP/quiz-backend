package com.quizportal.service;

import com.quizportal.entity.StudentGroup;

import java.util.List;

public interface GroupService {

    StudentGroup save(StudentGroup group);

    List<StudentGroup> getAll();

    List<StudentGroup> search(String keyword);

    void delete(Long id);

}