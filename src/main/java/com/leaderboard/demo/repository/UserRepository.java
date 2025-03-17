package com.leaderboard.demo.repository;

import com.leaderboard.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByIsDeletedFalse();
    List<User> findByRoleName(String roleName);
    List<User> findByCollegeId(UUID collegeId);


    Optional<User> findByIdAndIsDeletedFalse(UUID mentorId);
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.isDeleted = false")
    List<User> findByRoleNameAndIsDeletedFalse(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.college.id = :collegeId AND u.isDeleted = false")
    List<User> findByRoleNameAndCollegeIdAndIsDeletedFalse(@Param("roleName") String roleName, @Param("collegeId") UUID collegeId);


    Optional<User> findByEmailAndIsDeletedFalse(String Email);
}
