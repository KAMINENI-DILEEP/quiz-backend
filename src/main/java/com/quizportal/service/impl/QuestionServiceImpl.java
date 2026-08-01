package com.quizportal.service.impl;

import com.quizportal.entity.Exam;
import com.quizportal.entity.Question;
import com.quizportal.exception.ResourceNotFoundException;
import com.quizportal.repository.ExamRepository;
import com.quizportal.repository.QuestionRepository;
import com.quizportal.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ExamRepository examRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }

    @Override
    public Question addQuestion(Long examId, Question question) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        question.setExam(exam);

        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long questionId, Question updated) {

        Question question = getQuestion(questionId);

        question.setQuestionText(updated.getQuestionText());
        question.setQuestionType(updated.getQuestionType());
        question.setOptionA(updated.getOptionA());
        question.setOptionB(updated.getOptionB());
        question.setOptionC(updated.getOptionC());
        question.setOptionD(updated.getOptionD());
        question.setCorrectAnswer(updated.getCorrectAnswer());
        question.setMarks(updated.getMarks());

        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long questionId) {

        questionRepository.deleteById(questionId);

    }

    @Override
    public Question getQuestion(Long questionId) {

        return questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found"));

    }

    @Override
    public List<Question> getQuestionsByExam(Long examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        return questionRepository.findByExam(exam);

    }
}