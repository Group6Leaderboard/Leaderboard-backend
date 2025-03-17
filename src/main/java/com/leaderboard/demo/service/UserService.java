package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.*;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.config.JwtUtil;
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
    private CollegeRepository collegeRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User AddUser(UserDto userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setPhone(userDTO.getPhone());
        user.setScore(userDTO.getScore());

        Role role = roleRepository.findById(userDTO.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        if (userDTO.getCollege_id() != null) {
            College college = collegeRepository.findById(userDTO.getCollege_id())
                    .orElseThrow(() -> new RuntimeException("college not found"));
            user.setCollege(college);
        } else {
            user.setCollege(null);
        }
        return userRepository.save(user);

    }
//    public User updateUser(UUID id, UserDto userDto) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setEmail(userDto.getEmail());
//        user.setName(userDto.getName());
//        user.setPassword(userDto.getPassword());
//        user.setPhone(userDto.getPhone());
//        user.setScore(userDto.getScore());
//
//        return userRepository.save(user);
//    }

    public UserResponseDto updateUser(UUID id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        if (userDto.getScore() != 0) {
            user.setScore(userDto.getScore());
        }

        User updatedUser = userRepository.save(user);


        return new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole() != null ? updatedUser.getRole().getName() : null,
                updatedUser.getCollege() != null ? updatedUser.getCollege().getId() : null
        );
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId)
        .filter(user ->!user.isDeleted());
    }


    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }


    public List<User> getAllUsers() {
        return userRepository.findByIsDeletedFalse();
    }


    public boolean deleteUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            u.setDeleted(true);
            userRepository.save(u);
            return true;
        }
        return false;
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
