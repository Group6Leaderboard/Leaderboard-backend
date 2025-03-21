package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByAssignedToId(UUID projectId);
    List<Task> findByIsDeletedFalse();
    Optional<Task> findByIdAndIsDeletedFalse(UUID id);


    List<Task> findByAssignedToIdAndIsDeletedFalse(UUID assignedToId);


    List<Task> findByAssignedToIdAndAssignedByIdAndIsDeletedFalse(UUID projectId, UUID mentorId);

    @Query("SELECT COALESCE(SUM(t.score), 0) FROM Task t WHERE t.assignedTo.id = :projectId AND t.isDeleted = false")
    int sumScoresByProjectId(@Param("projectId") UUID projectId);

}
