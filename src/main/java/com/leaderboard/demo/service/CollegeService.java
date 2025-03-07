package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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



    public boolean softDeleteCollege(UUID id) { // ✅ Make sure it's NOT static
        Optional<College> optionalCollege = collegeRepository.findByIdAndIsDeletedFalse(id);
        if (optionalCollege.isPresent()) {
            College college = optionalCollege.get();
            if (college.isDeleted()) {
                return false; // Already deleted
            }
            college.setDeleted(true);
            collegeRepository.save(college); // ✅ Save changes to database
            return true;
        }
        return false;
    }


}