package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;


public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectId(UUID projectId);  // Get all tasks linked to a project

    List<Task> findByAssignedToId(UUID userId);

}
