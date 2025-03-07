package com.leaderboard.demo.dto;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String email;
    private String name;
    private String password;
    private String phone;
    private int score;
    private UUID college_id;
    private UUID role_id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public UUID getCollege_id() {
        return college_id;
    }

    public void setCollege_id(UUID college_id) {
        this.college_id = college_id;
    }

    public UUID getRole_id() {
        return role_id;
    }

    public void setRole_id(UUID role_id) {
        this.role_id = role_id;
    }
}
