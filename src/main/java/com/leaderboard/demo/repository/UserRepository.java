package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByIsDeletedFalse();
    List<User> findByRoleName(String roleName);
    List<User> findByCollegeId(UUID collegeId);
    Optional<User> findByEmail(String email);
}
