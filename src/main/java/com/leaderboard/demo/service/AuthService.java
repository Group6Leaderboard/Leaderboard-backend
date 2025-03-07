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

    public ResponseEntity<ApiResponse<UserResponseDto>> signup(UserSignupDto userSignupDto, User loggedInUser) {
        try {
            // Only admins can register users
            if (loggedInUser == null || !loggedInUser.getRole().getName().equalsIgnoreCase("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(403, "Unauthorized: Admin only", null));
            }

            // Check if email already exists
            if (userRepository.existsByEmail(userSignupDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "User already exists", null));
            }

            // Find role
            Role role = roleRepository.findByName(userSignupDto.getRole().toUpperCase())
                    .orElse(null);

            if (role == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Role not found", null));
            }

            // Create new user
            User user = new User();
            user.setPassword(passwordEncoder.encode(userSignupDto.getPassword() != null ?
                    userSignupDto.getPassword() : "Welcome@123"));
            user.setName(userSignupDto.getName());
            user.setEmail(userSignupDto.getEmail());
            user.setPhone(userSignupDto.getPhone());
            user.setRole(role);

            // Assign college if provided
            if (userSignupDto.getCollegeId() != null) {
                College college = collegeRepository.findById(userSignupDto.getCollegeId()).orElse(null);
                if (college == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(400, "College not found", null));
                }
                user.setCollege(college);
            }

            // Save user
            User savedUser = userRepository.save(user);

            // Convert user entity to response DTO (excludes password)
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
                    .body(new ApiResponse<>(500, "An error occurred during registration", null));
        }
    }

    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest loginRequest) {
        try {
            // Fetch user by email
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

            // Generic error message for either user not found or invalid password
            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid credentials", null));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setRole(user.getRole().getName());

            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "An error occurred during login", null));
        }
    }
}