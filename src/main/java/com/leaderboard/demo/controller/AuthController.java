package com.leaderboard.demo.controller;

import com.leaderboard.demo.config.JwtUtil;
import com.leaderboard.demo.dto.*;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.UserRepository;
import com.leaderboard.demo.service.AuthService;
import com.leaderboard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;

    @Autowired

    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository, CollegeRepository collegeRepository) {

        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(
            @RequestBody UserSignupDto signupDto,
            @RequestHeader("Authorization") String token) {

        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid token format", null));
            }

            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);

            User loggedInUser = userRepository.findByEmail(email).orElse(null);

            College loggedInCollege = collegeRepository.findByEmail(email).orElse(null);

            if (loggedInUser != null) {
                return authService.signupByUser(signupDto, loggedInUser);
            } else if (loggedInCollege != null) {

                return authService.signupByCollege(signupDto, loggedInCollege);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid authentication", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Failure: " + e.getMessage(), null));
        }
    }





    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}