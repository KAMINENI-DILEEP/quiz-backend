package com.quizportal.service.impl;

import com.quizportal.entity.Exam;
import com.quizportal.entity.ExamGroup;
import com.quizportal.entity.StudentGroup;
import com.quizportal.enums.ExamPublishStatus;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.ExamGroupRepository;
import com.quizportal.repository.ExamRepository;
import com.quizportal.repository.StudentGroupRepository;
import com.quizportal.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamGroupRepository examGroupRepository;
    private final StudentGroupRepository groupRepository;

    public ExamServiceImpl(
            ExamRepository examRepository,
            ExamGroupRepository examGroupRepository,
            StudentGroupRepository groupRepository) {

        this.examRepository = examRepository;
        this.examGroupRepository = examGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Exam createExam(Exam exam) {
        exam.setPublishStatus(ExamPublishStatus.DRAFT);
        return examRepository.save(exam);
    }

    @Override
    public Exam updateExam(Long id, Exam updated) {

        Exam exam = getExam(id);

        exam.setExamTitle(updated.getExamTitle());
        exam.setDescription(updated.getDescription());
        exam.setDurationMinutes(updated.getDurationMinutes());
        exam.setTotalMarks(updated.getTotalMarks());
        exam.setPassMarks(updated.getPassMarks());
        exam.setStartTime(updated.getStartTime());
        exam.setEndTime(updated.getEndTime());

        return examRepository.save(exam);
    }

    @Override
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    @Override
    public Exam publishExam(Long id) {

        Exam exam = getExam(id);

        exam.setPublishStatus(ExamPublishStatus.PUBLISHED);

        return examRepository.save(exam);
    }

    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public Exam getExam(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));
    }

    @Override
    public void assignGroup(Long examId, Long groupId) {

        Exam exam = getExam(examId);

        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found"));

        if (examGroupRepository.existsByExamAndGroup(exam, group)) {
            return;
        }

        ExamGroup examGroup = new ExamGroup();
        examGroup.setExam(exam);
        examGroup.setGroup(group);

        examGroupRepository.save(examGroup);
    }

    @Override
    public void removeGroup(Long examId, Long groupId) {

        Exam exam = getExam(examId);

        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found"));

        examGroupRepository.deleteByExamAndGroup(exam, group);
    }

    @Override
    public List<Long> getAssignedGroups(Long examId) {

        Exam exam = getExam(examId);

        return examGroupRepository.findByExam(exam)
                .stream()
                .map(e -> e.getGroup().getGroupId())
                .toList();
    }
}