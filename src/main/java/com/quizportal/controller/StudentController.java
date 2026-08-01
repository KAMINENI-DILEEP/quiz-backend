package com.quizportal.controller;

import com.quizportal.entity.User;
import com.quizportal.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")

@CrossOrigin("*")

public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<User> allStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam String keyword) {
        return studentService.searchStudents(keyword);
    }

    @PutMapping("/{id}/toggle")
    public User toggle(@PathVariable Long id) {
        return studentService.enableDisableStudent(id);
    }
    @DeleteMapping("/{id}")
public void deleteStudent(@PathVariable Long id){

    studentService.deleteStudent(id);

}
}