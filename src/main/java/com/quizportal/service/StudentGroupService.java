package com.quizportal.service;

import com.quizportal.entity.StudentGroup;

import java.util.List;

public interface StudentGroupService {

    StudentGroup createGroup(StudentGroup group);

    StudentGroup updateGroup(Long id, StudentGroup group);

    void deleteGroup(Long id);

    StudentGroup getGroup(Long id);

    List<StudentGroup> getAllGroups();
}