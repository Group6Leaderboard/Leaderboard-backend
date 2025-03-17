package com.leaderboard.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto implements BaseResponse{


    private UUID id;
    private String name;
    private String email;
    private String phone;
    private int score;
    private UUID collegeId;
    private String role;

    public UserResponseDto(UUID id, String name, String email, String phone, int score, UUID collegeId, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.score = score;
        this.collegeId = collegeId;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public UUID getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(UUID collegeId) {
        this.collegeId = collegeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
