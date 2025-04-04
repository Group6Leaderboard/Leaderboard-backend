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
    private String collegeName;
    private String role;
    private byte[] image;

    public UserResponseDto(UUID id, String name, String email, String phone, int score, String collegeName, String role, byte[] image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.score = score;
        this.collegeName = collegeName;
        this.role = role;
        this.image = image;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeId(String collegeId) {
        this.collegeName = collegeName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
