package com.quizportal.service.impl;

import com.quizportal.entity.*;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.*;
import com.quizportal.service.StudentExamService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentExamServiceImpl implements StudentExamService {

    private final StudentExamRepository studentExamRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public StudentExamServiceImpl(
            StudentExamRepository studentExamRepository,
            StudentAnswerRepository studentAnswerRepository,
            UserRepository userRepository,
            ExamRepository examRepository,
            QuestionRepository questionRepository) {

        this.studentExamRepository = studentExamRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public StudentExam startExam(Long studentId, Long examId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        StudentExam attempt = new StudentExam();
        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setStartedAt(LocalDateTime.now());

        return studentExamRepository.save(attempt);
    }

    @Override
    public StudentAnswer saveAnswer(
            Long attemptId,
            Long questionId,
            String selectedAnswer) {

        StudentExam attempt = studentExamRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attempt not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found"));

        StudentAnswer answer =
                studentAnswerRepository
                        .findByStudentExamAndQuestion(attempt, question)
                        .orElse(new StudentAnswer());

        answer.setStudentExam(attempt);
        answer.setQuestion(question);
        answer.setSelectedAnswer(selectedAnswer);

        return studentAnswerRepository.save(answer);
    }

    @Override
    public StudentExam submitExam(Long attemptId) {

        StudentExam attempt = studentExamRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attempt not found"));

        List<StudentAnswer> answers =
                studentAnswerRepository.findByStudentExam(attempt);

        int score = 0;

        for (StudentAnswer answer : answers) {

            if (answer.getSelectedAnswer()
                    .equalsIgnoreCase(answer.getQuestion().getCorrectAnswer())) {

                score += answer.getQuestion().getMarks();

            }

        }

        attempt.setScore(score);
        attempt.setSubmitted(true);
        attempt.setSubmittedAt(LocalDateTime.now());

        return studentExamRepository.save(attempt);
    }

    @Override
    public List<StudentExam> getStudentAttempts(Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        return studentExamRepository.findByStudent(student);

    }
}