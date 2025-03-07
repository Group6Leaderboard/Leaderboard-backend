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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

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



    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }


    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }


    public List<User> getAllUsers() {
        return userRepository.findByIsDeletedFalse();
    }


    public User deleteUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            u.setDeleted(true);
            return userRepository.save(u);
        }
        return null;
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
