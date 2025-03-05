
package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CollegeRepository extends JpaRepository<College, UUID> {
    List<College> findByIsDeletedFalse();
}
