package com.leaderboard.demo.repository;


import com.leaderboard.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByIsDeletedFalse();
    List<Project> findByMentorId(UUID mentorId);

    Optional<Project> findByIdAndIsDeletedFalse(UUID projectId);
    List<Project> findByMentorIdAndIsDeletedFalse(UUID mentorId);

    List<Project> findByCollegeIdAndIsDeletedFalse(UUID collegeId);

}
