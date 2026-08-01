package com.quizportal.service.impl;

import com.quizportal.dto.response.DashboardResponse;
import com.quizportal.enums.ExamPublishStatus;
import com.quizportal.enums.Role;
import com.quizportal.repository.ExamRepository;
import com.quizportal.repository.StudentExamRepository;
import com.quizportal.repository.StudentGroupRepository;
import com.quizportal.repository.UserRepository;
import com.quizportal.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final StudentGroupRepository groupRepository;
    private final ExamRepository examRepository;
    private final StudentExamRepository attemptRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            StudentGroupRepository groupRepository,
            ExamRepository examRepository,
            StudentExamRepository attemptRepository) {

        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.examRepository = examRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    public DashboardResponse getDashboard() {

        DashboardResponse dashboard = new DashboardResponse();

        dashboard.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        dashboard.setTotalGroups(groupRepository.count());
        dashboard.setTotalExams(examRepository.count());
        dashboard.setPublishedExams(
                examRepository.countByPublishStatus(
                        ExamPublishStatus.PUBLISHED));

        dashboard.setTotalAttempts(attemptRepository.count());
        dashboard.setCompletedAttempts(
                attemptRepository.countBySubmittedTrue());

        return dashboard;
    }
}