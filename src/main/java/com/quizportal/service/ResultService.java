package com.quizportal.service;

import com.quizportal.dto.response.ResultResponse;

import java.util.List;

public interface ResultService {

    List<ResultResponse> getExamResults(Long examId);

}