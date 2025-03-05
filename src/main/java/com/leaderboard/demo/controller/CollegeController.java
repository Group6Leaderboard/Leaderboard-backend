package com.leaderboard.demo.controller;

import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    // Get all colleges
    @GetMapping
    public ResponseEntity<List<College>> getAllColleges() {
        List<College> colleges = collegeService.getAllColleges();
        return new ResponseEntity<>(colleges, HttpStatus.OK);
    }

    // Get college by ID
    @GetMapping("/{id}")
    public ResponseEntity<College> getCollegeById(@PathVariable UUID id) {
        College college = collegeService.getCollegeById(id);
        return college != null ? new ResponseEntity<>(college, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Create or update a college
    @PostMapping
    public ResponseEntity<College> createCollege(@RequestBody College college) {
        College savedCollege = collegeService.saveCollege(college);
        return new ResponseEntity<>(savedCollege, HttpStatus.CREATED);
    }

    // Delete a college (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<College> deleteCollege(@PathVariable UUID id) {
        College deletedCollege = collegeService.deleteCollege(id);
        return deletedCollege != null ? new ResponseEntity<>(deletedCollege, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
