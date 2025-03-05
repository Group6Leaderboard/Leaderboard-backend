package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollegeService {

    @Autowired
    private CollegeRepository collegeRepository;

    // Create or update a college
    public College saveCollege(College college) {
        return collegeRepository.save(college);
    }

    // Find a college by ID
    public College getCollegeById(UUID collegeId) {
        return collegeRepository.findById(collegeId).orElse(null);
    }

    // Get all active colleges
    public List<College> getAllColleges() {
        return collegeRepository.findByIsDeletedFalse();
    }

    // Delete a college (soft delete)
    public College deleteCollege(UUID collegeId) {
        College college = collegeRepository.findById(collegeId).orElse(null);
        if (college != null) {
            college.setDeleted(true);
            return collegeRepository.save(college);
        }
        return null;
    }
}
