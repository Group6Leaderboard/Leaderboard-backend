package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.StudentProject;
import com.leaderboard.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProjectRepository extends JpaRepository<StudentProject, UUID> {
    List<StudentProject> findByStudentIdAndIsDeletedFalse(UUID studentId);
    List<StudentProject> findByProjectIdAndIsDeletedFalse(UUID projectId);
    Optional<StudentProject> findByIdAndIsDeletedFalse(UUID id);
    Optional<StudentProject> findByStudentAndProjectAndIsDeletedFalse(User student, Project project);
    List<StudentProject> findByIsDeletedFalse();
}
