package com.leaderboard.demo.dto;

import java.util.UUID;

public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private UUID college;

    public UserResponseDto(UUID id, String name, String email, String phone, String role, UUID college) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.college = college;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getCollege() {
        return college;
    }

    public void setCollege(UUID college) {
        this.college = college;
    }
}
