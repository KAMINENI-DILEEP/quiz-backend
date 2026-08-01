package com.quizportal.controller;

import com.quizportal.service.FileUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/questions/{examId}")
    public String uploadQuestions(@PathVariable Long examId,
                                  @RequestParam("file") MultipartFile file) {

        fileUploadService.importQuestions(examId, file);

        return "Questions imported successfully";
    }
}