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
    public ResponseEntity<ApiResponse<BaseResponse>> signup(
            @RequestBody UserSignupDto signupDto,
            @RequestHeader("Authorization") String token) {

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid token format", null));
            }

            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);

            User loggedInUser = userRepository.findByEmail(email).orElse(null);
            College loggedInCollege = collegeRepository.findByEmail(email).orElse(null);

            ApiResponse<BaseResponse> response;
            if (loggedInUser != null) {
                response = authService.signupByUser(signupDto, loggedInUser);
            } else if (loggedInCollege != null) {
                response = authService.signupByCollege(signupDto, loggedInCollege);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid authentication", null));
            }

            return ResponseEntity.status(response.getStatus()).body(response);
    }



    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Invalid token format", null));
        }

        String jwt = token.substring(7);

        jwtUtil.invalidateToken(jwt);

        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
    }

}