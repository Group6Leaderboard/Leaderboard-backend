package com.leaderboard.demo.controller;
import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.CollegeDTO;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getAllColleges() {
        ApiResponse<List<CollegeDTO>> response = collegeService.getAllColleges();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollegeDTO>> getCollegeById(@PathVariable UUID id) {
        ApiResponse<CollegeDTO> response = collegeService.getCollegeById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLEGE')")
    public ResponseEntity<ApiResponse<CollegeDTO>> updateCollege(@PathVariable UUID id, @RequestBody College college) {
        ApiResponse<CollegeDTO> response = collegeService.updateCollege(id, college);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCollege(@PathVariable UUID id) {
        ApiResponse<String> response = collegeService.softDeleteCollege(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
