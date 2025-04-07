package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.LeaderboardDto;
import com.leaderboard.demo.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboards")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    @Transactional
    public ResponseEntity<ApiResponse<List<LeaderboardDto>>> getLeaderboard(
            @RequestParam(value = "type") String type){
        try {
            List<LeaderboardDto> leaderboard = leaderboardService.getLeaderboard(type);
            return ApiResponse.success(leaderboard, "Fetched successfully");

        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
