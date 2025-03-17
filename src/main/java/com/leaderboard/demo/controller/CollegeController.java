package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.CollegeDTO;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController

@RequestMapping("/api/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;


    @GetMapping
    public ResponseEntity<List<CollegeDTO>> getAllColleges() {
        List<CollegeDTO> colleges = collegeService.getAllColleges();
        return ResponseEntity.ok(colleges);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CollegeDTO> getCollegeById(@PathVariable UUID id) {
        CollegeDTO college = collegeService.getCollegeById(id);
        if (college == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(college);
    }

    @PostMapping
    public ResponseEntity<CollegeDTO> createCollege(@RequestBody College college) {
        CollegeDTO savedCollege = collegeService.saveCollege(college);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollege);
    }

    @PutMapping("/{id}")
    public ResponseEntity<College> updateCollege(@PathVariable UUID id, @RequestBody College college) {
        College updatedCollege = collegeService.updateCollege(id, college);
        return updatedCollege != null ? new ResponseEntity<>(updatedCollege, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCollege(@PathVariable UUID id) {
        boolean deleted = collegeService.softDeleteCollege(id); // ✅ Use instance method correctly

        Map<String, String> response = new HashMap<>();
        response.put("status", "200");

        if (deleted) {
            response.put("message", "College deleted successfully");
        } else {
            response.put("message", "No such college");
        }

        return ResponseEntity.ok(response);
    }


}
