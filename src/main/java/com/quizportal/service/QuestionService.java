package com.quizportal.service;

import com.quizportal.entity.Question;

import java.util.List;

public interface QuestionService {

    Question addQuestion(Long examId, Question question);

    Question updateQuestion(Long id, Question question);

    void deleteQuestion(Long id);

    Question getQuestion(Long id);

    List<Question> getQuestionsByExam(Long examId);
}