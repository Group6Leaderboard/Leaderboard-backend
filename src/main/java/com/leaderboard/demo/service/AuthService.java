package com.leaderboard.demo.service;

import com.leaderboard.demo.config.JwtUtil;
import com.leaderboard.demo.dto.*;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.RoleRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CollegeRepository collegeRepository;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            CollegeRepository collegeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.collegeRepository = collegeRepository;
    }
    public ResponseEntity<ApiResponse<UserResponseDto>> signupByUser(UserSignupDto userSignupDto, User loggedInUser) {
        try {
            boolean isAdmin = loggedInUser.getRole().getName().equalsIgnoreCase("ADMIN");

            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(403, "Unauthorized: Only Admin can register users", null));
            }

            if (userRepository.existsByEmail(userSignupDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "User already exists", null));
            }

            Role assignedRole = roleRepository.findByName(userSignupDto.getRole().toUpperCase())
                    .orElse(null);

            if (assignedRole == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Invalid role", null));
            }

            College college = null;
            if (userSignupDto.getCollegeId() != null) {
                college = collegeRepository.findById(userSignupDto.getCollegeId()).orElse(null);
                if (college == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(400, "College not found", null));
                }
            }

            User user = new User();
            user.setPassword(passwordEncoder.encode(userSignupDto.getPassword() != null ?
                    userSignupDto.getPassword() : "Welcome@123"));
            user.setName(userSignupDto.getName());
            user.setEmail(userSignupDto.getEmail());
            user.setPhone(userSignupDto.getPhone());
            user.setRole(assignedRole);
            user.setCollege(college);

            User savedUser = userRepository.save(user);

            UserResponseDto responseDto = new UserResponseDto(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getRole().getName(),
                    savedUser.getCollege() != null ? savedUser.getCollege().getId() : null
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "An error occurred during registration: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ApiResponse<UserResponseDto>> signupByCollege(UserSignupDto userSignupDto, College loggedInCollege) {
        try {
            if (userRepository.existsByEmail(userSignupDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "User already exists", null));
            }

            Role studentRole = roleRepository.findByName("STUDENT").orElse(null);

            if (studentRole == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(500, "Default student role not found", null));
            }

            User user = new User();
            user.setPassword(passwordEncoder.encode(userSignupDto.getPassword() != null ?
                    userSignupDto.getPassword() : "Welcome@123"));
            user.setName(userSignupDto.getName());
            user.setEmail(userSignupDto.getEmail());
            user.setPhone(userSignupDto.getPhone());
            user.setRole(studentRole);
            user.setCollege(loggedInCollege);

            User savedUser = userRepository.save(user);

            UserResponseDto responseDto = new UserResponseDto(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getRole().getName(),
                    savedUser.getCollege().getId()
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "Student registered successfully", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "An error occurred during registration: " + e.getMessage(), null));
        }
    }



    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
            College college = null;
            if (user == null) {
                college = collegeRepository.findByEmail(loginRequest.getEmail()).orElse(null);
            }

            if ((user == null && college == null) ||
                    (user != null && !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) ||
                    (college != null && !passwordEncoder.matches(loginRequest.getPassword(), college.getPassword()))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid credentials", null));
            }

            String token;
            String role;

            if (user != null) {
                token = jwtUtil.generateToken(user);
                role = user.getRole().getName();
            } else {
                token = jwtUtil.generateToken(college);
                role = "COLLEGE";
            }

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setRole(role);

            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "An error occurred during login", null));
        }
    }




}