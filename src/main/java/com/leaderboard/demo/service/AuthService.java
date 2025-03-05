package com.leaderboard.demo.service;

import com.leaderboard.demo.config.JwtUtil;
import com.leaderboard.demo.dto.LoginRequest;
import com.leaderboard.demo.dto.LoginResponse;
import com.leaderboard.demo.dto.UserSignupDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.RoleRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CollegeRepository collegeRepository;


    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }




    public User signup(UserSignupDto userSignupDto) {
        if (userRepository.existsByEmail(userSignupDto.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        Role role = roleRepository.findByName(userSignupDto.getRole().toUpperCase()).orElseThrow(() -> new RuntimeException("Role not found"));
        College college = collegeRepository.findById(userSignupDto.getCollegeId()).orElseThrow(() -> new RuntimeException("College not found"));

        User user = new User();
        user.setName(userSignupDto.getName());
        user.setEmail(userSignupDto.getEmail());
        user.setPassword(passwordEncoder.encode(userSignupDto.getPassword()));
        user.setPhone(userSignupDto.getPhone());
        user.setCollege(college);
        user.setRole(role);

        return userRepository.save(user);
    }


    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Attempting authentication for: " + loginRequest.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare hashed password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        System.out.println("Authentication successful for: " + loginRequest.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Return login response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRole(user.getRole().getName());
        return loginResponse;
    }

}
