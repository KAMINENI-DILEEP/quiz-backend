package com.quizportal.service;

import com.quizportal.entity.Exam;

import java.util.List;

public interface ExamService {

    Exam createExam(Exam exam);

    Exam updateExam(Long id, Exam exam);

    void deleteExam(Long id);

    Exam publishExam(Long id);

    List<Exam> getAllExams();

    Exam getExam(Long id);

    void assignGroup(Long examId, Long groupId);

    void removeGroup(Long examId, Long groupId);

    List<Long> getAssignedGroups(Long examId);
}