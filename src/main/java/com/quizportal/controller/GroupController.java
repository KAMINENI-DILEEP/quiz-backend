package com.quizportal.controller;

import com.quizportal.entity.StudentGroup;
import com.quizportal.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin("*")
public class GroupController {

    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentGroup> all() {
        return service.getAll();
    }

    @PostMapping
    public StudentGroup create(@RequestBody StudentGroup group) {
        return service.save(group);
    }

    @GetMapping("/search")
    public List<StudentGroup> search(
            @RequestParam String keyword) {

        return service.search(keyword);

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        service.delete(id);

    }

}