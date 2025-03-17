package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.CollegeDTO;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CollegeService {

    @Autowired
    private CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CollegeService(CollegeRepository collegeRepository, PasswordEncoder passwordEncoder) {
        this.collegeRepository = collegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CollegeDTO saveCollege(College college) {
        college.setPassword(passwordEncoder.encode(college.getPassword()));
        College savedCollege = collegeRepository.save(college);

        CollegeDTO dto = new CollegeDTO();
        dto.setId(college.getId());
        dto.setName(savedCollege.getName());
        dto.setLocation(savedCollege.getLocation());
        dto.setAbout(savedCollege.getAbout());
        dto.setEmail(savedCollege.getEmail());
        dto.setRoleName(savedCollege.getRole() != null ? savedCollege.getRole().getName() : null);

        return dto;
    }
    public CollegeDTO getCollegeById(UUID collegeId) {
        College college = collegeRepository.findByIdAndIsDeletedFalse(collegeId).orElse(null);
        if (college != null) {
            CollegeDTO dto = new CollegeDTO();
            dto.setId(college.getId());
            dto.setName(college.getName());
            dto.setLocation(college.getLocation());
            dto.setAbout(college.getAbout());
            dto.setEmail(college.getEmail());
            dto.setRoleName(college.getRole() != null ? college.getRole().getName() : null);
            return dto;
        }
        return null;
    }

    public List<CollegeDTO> getAllColleges() {
        List<College> colleges = collegeRepository.findByIsDeletedFalse();
        List<CollegeDTO> dtos = new ArrayList<>();
        for (College college : colleges) {
            CollegeDTO dto = new CollegeDTO();
            dto.setId(college.getId());
            dto.setName(college.getName());
            dto.setLocation(college.getLocation());
            dto.setAbout(college.getAbout());
            dto.setEmail(college.getEmail());
            dto.setRoleName(college.getRole() != null ? college.getRole().getName() : null);
            dtos.add(dto);
        }
        return dtos;
    }


    public College updateCollege(UUID collegeId, College updatedCollege) {
        return collegeRepository.findById(collegeId).map(existingCollege -> {
            if (updatedCollege.getName() != null) {
                existingCollege.setName(updatedCollege.getName());
            }
            if (updatedCollege.getLocation() != null) {
                existingCollege.setLocation(updatedCollege.getLocation());
            }
            if (updatedCollege.getEmail() != null) {
                existingCollege.setEmail(updatedCollege.getEmail());
            }
            if (updatedCollege.getPassword() != null) {
                existingCollege.setPassword(updatedCollege.getPassword());
            }
            existingCollege.setScore(updatedCollege.getScore());
            if (updatedCollege.getAbout() != null) {
                existingCollege.setAbout(updatedCollege.getAbout());
            }

            existingCollege.setUpdatedAt(LocalDateTime.now());
            return collegeRepository.save(existingCollege);
        }).orElse(null);
    }



    public boolean softDeleteCollege(UUID id) {
        Optional<College> optionalCollege = collegeRepository.findByIdAndIsDeletedFalse(id);
        if (optionalCollege.isPresent()) {
            College college = optionalCollege.get();
            if (college.isDeleted()) {
                return false; // Already deleted
            }
            college.setDeleted(true);
            collegeRepository.save(college);
            return true;
        }
        return false;
    }


}