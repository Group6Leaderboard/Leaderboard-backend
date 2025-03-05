package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CollegeService {

    @Autowired
    private CollegeRepository collegeRepository;


    public College saveCollege(College college) {
        return collegeRepository.save(college);
    }


    public College getCollegeById(UUID collegeId) {
        return collegeRepository.findById(collegeId).orElse(null);
    }


    public List<College> getAllColleges() {
        return collegeRepository.findByIsDeletedFalse();
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
            existingCollege.setScore(updatedCollege.getScore()); // Assuming score can be 0
            if (updatedCollege.getAbout() != null) {
                existingCollege.setAbout(updatedCollege.getAbout());
            }

            existingCollege.setUpdatedAt(LocalDateTime.now()); // Track update timestamp

            return collegeRepository.save(existingCollege);
        }).orElse(null);
    }


    public College deleteCollege(UUID collegeId) {
        College college = collegeRepository.findById(collegeId).orElse(null);
        if (college != null) {
            college.setDeleted(true);
            return collegeRepository.save(college);
        }
        return null;
    }
}
