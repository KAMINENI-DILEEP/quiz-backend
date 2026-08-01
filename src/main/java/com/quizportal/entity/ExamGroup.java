package com.quizportal.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "exam_groups",
        uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id","group_id"})
)
public class ExamGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private StudentGroup group;

    public ExamGroup() {
    }

    public Long getId() {
        return id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public StudentGroup getGroup() {
        return group;
    }

    public void setGroup(StudentGroup group) {
        this.group = group;
    }
}