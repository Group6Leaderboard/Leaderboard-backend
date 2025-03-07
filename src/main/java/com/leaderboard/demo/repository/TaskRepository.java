package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByAssignedToId(UUID projectId);
    List<Task> findByIsDeletedFalse();
    Optional<Task> findByIdAndIsDeletedFalse(UUID id);
}
