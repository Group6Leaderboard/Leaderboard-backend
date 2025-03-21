package com.leaderboard.demo.service;

import com.leaderboard.demo.config.JwtUtil;
import com.leaderboard.demo.dto.*;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.exception.UserAlreadyExistsException;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.RoleRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public ApiResponse<BaseResponse> signupByUser(UserSignupDto userSignupDto, User loggedInUser) {
        boolean isAdmin = loggedInUser.getRole().getName().equalsIgnoreCase("ADMIN");

        if (!isAdmin) {
            throw new IllegalArgumentException("Unauthorized: Only Admin can register users");
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(userSignupDto.getEmail()) ||
                collegeRepository.existsByEmail(userSignupDto.getEmail())) {
                    throw new UserAlreadyExistsException("User already exists");
        }

        Role assignedRole = roleRepository.findByName(userSignupDto.getRole().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid role"));

        BaseResponse responseDto;

        if (assignedRole.getName().equalsIgnoreCase("COLLEGE")) {
            College existingCollege = collegeRepository.findByEmailAndIsDeletedFalse(userSignupDto.getEmail()).orElse(null);
            if (existingCollege != null) {
                throw new UserAlreadyExistsException("College already exists");
            }

            College college = new College();
            college.setPassword(passwordEncoder.encode(
                    userSignupDto.getPassword() != null ? userSignupDto.getPassword() : "Welcome@123"));
            college.setName(userSignupDto.getName());
            college.setEmail(userSignupDto.getEmail());
            college.setRole(assignedRole);
            college.setCreatedAt(LocalDateTime.now());

            College savedCollege = collegeRepository.save(college);

            responseDto = new CollegeDTO(
                    savedCollege.getId(),
                    savedCollege.getName(),
                    savedCollege.getEmail(),
                    savedCollege.getLocation(),
                    savedCollege.getAbout(),
                    savedCollege.getRole() != null ? savedCollege.getRole().getName() : null
            );
        } else {
            College college = null;
            if (userSignupDto.getCollegeId() != null) {
                college = collegeRepository.findByIdAndIsDeletedFalse(userSignupDto.getCollegeId())
                        .orElseThrow(() -> new ResourceNotFoundException("College not found"));
            }

            User existingUser = userRepository.findByEmailAndIsDeletedFalse(userSignupDto.getEmail()).orElse(null);
            if (existingUser != null) {
                throw new UserAlreadyExistsException("User already exists");
            }

            User user = new User();
            user.setPassword(passwordEncoder.encode(
                    userSignupDto.getPassword() != null ? userSignupDto.getPassword() : "Welcome@123"));
            user.setName(userSignupDto.getName());
            user.setEmail(userSignupDto.getEmail());
            user.setPhone(userSignupDto.getPhone());
            user.setRole(assignedRole);
            user.setCollege(college);

            User savedUser = userRepository.save(user);

            responseDto = new UserResponseDto(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getScore(),
                    savedUser.getCollege() != null ? savedUser.getCollege().getId() : null,
                    savedUser.getRole().getName()
            );
        }

        return new ApiResponse<>(201, "Success", responseDto);
    }

    public ApiResponse<BaseResponse> signupByCollege(UserSignupDto userSignupDto, College loggedInCollege) {
        if (userRepository.existsByEmail(userSignupDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Default student role not found"));

        User user = new User();
        user.setPassword(passwordEncoder.encode(
                userSignupDto.getPassword() != null ? userSignupDto.getPassword() : "Welcome@123"));
        user.setName(userSignupDto.getName());
        user.setEmail(userSignupDto.getEmail());
        user.setPhone(userSignupDto.getPhone());
        user.setRole(studentRole);
        user.setCollege(loggedInCollege);

        User savedUser = userRepository.save(user);

        BaseResponse responseDto = new UserResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getScore(),
                savedUser.getCollege() != null ? savedUser.getCollege().getId() : null,
                savedUser.getRole().getName()


        );
        return new ApiResponse<>(201, "Success", responseDto);
    }



    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()).orElse(null);
        College college = null;
        if (user == null) {
            college = collegeRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()).orElse(null);
        }

        if (user == null && college == null) {
            throw new ResourceNotFoundException("User not found");
        } else if(
                (user != null && !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) ||
                (college != null && !passwordEncoder.matches(loginRequest.getPassword(), college.getPassword())) ){
            throw new IllegalArgumentException("Invalid credentials");
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

        return new ApiResponse<>(200, "Success", loginResponse);
    }


}