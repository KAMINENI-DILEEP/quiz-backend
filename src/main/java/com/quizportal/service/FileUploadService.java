package com.quizportal.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    void importQuestions(Long examId, MultipartFile file);

}