package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.CollegeDTO;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CollegeService(CollegeRepository collegeRepository, PasswordEncoder passwordEncoder) {
        this.collegeRepository = collegeRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public ApiResponse<CollegeDTO> getCollegeById(UUID collegeId) {
        College college = collegeRepository.findByIdAndIsDeletedFalse(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found "));

        CollegeDTO dto = mapToDto(college);
        return new ApiResponse<>(200, "Success", dto);
    }

    public ApiResponse<List<CollegeDTO>> getAllColleges() {
        List<College> colleges = collegeRepository.findByIsDeletedFalse();
        if (colleges.isEmpty()) {
            throw new ResourceNotFoundException("No colleges found");
        }

        List<CollegeDTO> dtos = colleges.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", dtos);
    }
    @Transactional
    public ApiResponse<CollegeDTO> updateCollege(UUID collegeId, College updatedCollege) {
        College existingCollege = collegeRepository.findByIdAndIsDeletedFalse(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found " ));

        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String loggedInUserRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        if ("ROLE_COLLEGE".equals(loggedInUserRole)) {
            College loggedInCollege = collegeRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Logged in college not found"));

            if (!loggedInCollege.getId().equals(collegeId)) {
                throw new IllegalArgumentException("Unauthorized: Cannot update other colleges' data");
            }
        }

        if (updatedCollege.getName() != null) {
            existingCollege.setName(updatedCollege.getName());
        }
        if (updatedCollege.getLocation() != null) {
            existingCollege.setLocation(updatedCollege.getLocation());
        }
        if (updatedCollege.getPassword() != null) {
            existingCollege.setPassword(passwordEncoder.encode(updatedCollege.getPassword()));

        }
        if (updatedCollege.getAbout() != null) {
            existingCollege.setAbout(updatedCollege.getAbout());
        }

        existingCollege.setUpdatedAt(LocalDateTime.now());
        College savedCollege = collegeRepository.save(existingCollege);

        return new ApiResponse<>(200, "College updated successfully", mapToDto(savedCollege));
    }

    @Transactional
    public ApiResponse<String> softDeleteCollege(UUID id) {
        College college = collegeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + id));


        college.setDeleted(true);
        college.setUpdatedAt(LocalDateTime.now());
        collegeRepository.save(college);

        return new ApiResponse<>(200, "College deleted successfully", "Deleted");
    }

    private CollegeDTO mapToDto(College college) {
        CollegeDTO dto = new CollegeDTO();
        dto.setId(college.getId());
        dto.setName(college.getName());
        dto.setLocation(college.getLocation());
        dto.setAbout(college.getAbout());
        dto.setEmail(college.getEmail());
        dto.setRoleName(college.getRole() != null ? college.getRole().getName() : null);
        return dto;
    }
}
