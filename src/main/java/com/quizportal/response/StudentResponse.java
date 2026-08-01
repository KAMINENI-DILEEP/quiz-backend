package com.quizportal.dto.response;

public class StudentResponse {

    private Long userId;
    private String displayId;
    private String name;
    private String email;
    private String gender;
    private boolean accountEnabled;

    public StudentResponse() {
    }

    public StudentResponse(Long userId, String displayId, String name,
                           String email, String gender, boolean accountEnabled) {
        this.userId = userId;
        this.displayId = displayId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.accountEnabled = accountEnabled;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public boolean isAccountEnabled() {
        return accountEnabled;
    }
}