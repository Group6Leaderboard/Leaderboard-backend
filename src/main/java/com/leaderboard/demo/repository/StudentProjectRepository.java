package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentProjectRepository extends JpaRepository<StudentProject, UUID> {
    List<StudentProject> findByStudentId(UUID studentId);
    List<StudentProject> findByProjectId(UUID projectId);
}
