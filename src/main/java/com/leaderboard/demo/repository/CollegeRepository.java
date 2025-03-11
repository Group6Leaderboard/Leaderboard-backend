
package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollegeRepository extends JpaRepository<College, UUID> {
    List<College> findByIsDeletedFalse();

    Optional<College> findByIdAndIsDeletedFalse(UUID id);

    Optional<College> findByEmail(String email);
}
