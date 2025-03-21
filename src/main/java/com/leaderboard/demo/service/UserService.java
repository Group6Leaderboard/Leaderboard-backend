package com.leaderboard.demo.service;
import com.leaderboard.demo.dto.*;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.RoleRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }


    @Transactional
    public ApiResponse<UserResponseDto> updateUser(String loggedInUserEmail, UserDto userDto, MultipartFile image) {
        User user = userRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDto != null) {
            if (userDto.getEmail() != null) {
                throw new IllegalArgumentException("Email cannot be updated");
            }
            if (userDto.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            if (userDto.getPhone() != null) {
                user.setPhone(userDto.getPhone());
            }
        }

        if (image != null && !image.isEmpty()) {
            try {
                user.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image upload", e);
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        UserResponseDto dto = mapToDto(updatedUser);

        return new ApiResponse<>(200, "User updated successfully", dto);
    }

    public ApiResponse<UserResponseDto> getUserById(UUID userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponseDto dto = mapToDto(user);
        return new ApiResponse<>(200, "Success", dto);
    }

    public ApiResponse<List<BaseResponse>> getUsersByRole(String roleName) {
        List<BaseResponse> responses;

        if ("COLLEGE".equalsIgnoreCase(roleName)) {
            if (!hasRole("ADMIN")) {
                throw new IllegalArgumentException("Unauthorized to access college details");
            }

            List<College> colleges = collegeRepository.findByIsDeletedFalse();

            if (colleges.isEmpty()) {
                return new ApiResponse<>(404, "No colleges found", null);
            }

            responses = colleges.stream()
                    .map(college -> new CollegeDTO(
                            college.getId(),
                            college.getName(),
                            college.getEmail(),
                            college.getLocation(),
                            college.getAbout(),
                            college.getRole() != null ? college.getRole().getName() : null
                    ))
                    .collect(Collectors.toList());

        } else if ("STUDENT".equalsIgnoreCase(roleName)) {
            if (hasRole("COLLEGE")) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                College loggedInCollege = collegeRepository.findByEmailAndIsDeletedFalse(email)
                        .orElseThrow(() -> new IllegalArgumentException("Logged-in college not found"));

                List<User> students = userRepository.findByRoleNameAndCollegeIdAndIsDeletedFalse(roleName, loggedInCollege.getId());

                if (students.isEmpty()) {
                    return new ApiResponse<>(404, "No students found for logged-in college", null);
                }

                responses = students.stream()
                        .map(user -> new UserResponseDto(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhone(),
                                user.getScore(),
                                user.getCollege() != null ? user.getCollege().getId() : null,
                                user.getRole().getName()
                        ))
                        .collect(Collectors.toList());
            } else if (hasRole("ADMIN")) {
                List<User> users = userRepository.findByRoleNameAndIsDeletedFalse(roleName);

                if (users.isEmpty()) {
                    return new ApiResponse<>(404, "No users found for role: " + roleName, null);
                }

                responses = users.stream()
                        .map(user -> new UserResponseDto(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhone(),
                                user.getScore(),
                                user.getCollege() != null ? user.getCollege().getId() : null,
                                user.getRole().getName()
                        ))
                        .collect(Collectors.toList());
            } else {
                throw new IllegalArgumentException("Unauthorized to access student details");
            }
        } else {
            if (!hasRole("ADMIN")) {
                throw new IllegalArgumentException("Unauthorized to access " + roleName + " details");
            }

            List<User> users = userRepository.findByRoleNameAndIsDeletedFalse(roleName);

            if (users.isEmpty()) {
                return new ApiResponse<>(404, "No users found for role: " + roleName, null);
            }

            responses = users.stream()
                    .map(user -> new UserResponseDto(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getScore(),
                            user.getCollege() != null ? user.getCollege().getId() : null,
                            user.getRole().getName()
                    ))
                    .collect(Collectors.toList());
        }

        return new ApiResponse<>(200, "Success", responses);
    }
    public ApiResponse<UserResponseDto> getUsersByEmail(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        UserResponseDto dto = mapToDto(user);
        return new ApiResponse<>(200, "User fetched successfully", dto);
    }




    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userRepository.findByIsDeletedFalse();

        List<UserResponseDto> userDtos = users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", userDtos);
    }

    public ApiResponse<String> deleteUser(UUID userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new IllegalStateException("User already deleted");
        }

        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return new ApiResponse<>(200, "User deleted successfully", "Deleted");
    }

    private UserResponseDto mapToDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getScore(),
                user.getCollege() != null ? user.getCollege().getId() : null,
                user.getRole().getName()
        );
    }


}
