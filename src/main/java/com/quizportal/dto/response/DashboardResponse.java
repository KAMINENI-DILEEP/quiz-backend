package com.quizportal.dto.response;

public class DashboardResponse {

    private long totalStudents;
    private long totalGroups;
    private long totalExams;
    private long publishedExams;
    private long totalAttempts;
    private long completedAttempts;

    public DashboardResponse() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(long totalGroups) {
        this.totalGroups = totalGroups;
    }

    public long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(long totalExams) {
        this.totalExams = totalExams;
    }

    public long getPublishedExams() {
        return publishedExams;
    }

    public void setPublishedExams(long publishedExams) {
        this.publishedExams = publishedExams;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getCompletedAttempts() {
        return completedAttempts;
    }

    public void setCompletedAttempts(long completedAttempts) {
        this.completedAttempts = completedAttempts;
    }
}