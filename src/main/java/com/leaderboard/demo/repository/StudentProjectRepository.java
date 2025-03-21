package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.StudentProject;
import com.leaderboard.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByStudentAndProjectAndIsDeletedFalse(User student, Project project);

    boolean existsByStudentIdAndProjectIdAndIsDeletedFalse(UUID studentId, UUID projectId);

    @Query("SELECT COALESCE(SUM(t.score), 0) FROM Task t WHERE t.assignedTo.id IN " +
            "(SELECT sp.project.id FROM StudentProject sp WHERE sp.student.id = :studentId AND sp.isDeleted = false) " +
            "AND t.isDeleted = false")
    int sumScoresByStudentId(@Param("studentId") UUID studentId);


    @Query("SELECT COUNT(sp) FROM StudentProject sp WHERE sp.student.id = :studentId AND sp.isDeleted = false")
    int countByStudentId(@Param("studentId") UUID studentId);

}
