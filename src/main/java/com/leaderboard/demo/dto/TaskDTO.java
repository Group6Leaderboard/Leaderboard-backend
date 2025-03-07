package com.leaderboard.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDTO {
    private UUID id;
    private String description;
    private String status;
    private byte[] file;
    private String name;
    private int score;
    private UUID assignedBy;
    private UUID assignedTO;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
