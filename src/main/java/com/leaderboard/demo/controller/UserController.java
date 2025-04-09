package com.leaderboard.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.BaseResponse;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.dto.UserResponseDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.UserRepository;
import com.leaderboard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollegeRepository collegeRepository;


    @GetMapping
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "(hasRole('COLLEGE') and (#roleName == null or #roleName == 'STUDENT')) or " +
                    "hasRole('STUDENT') or hasRole('MENTOR')"
    )
    @Transactional
    public ResponseEntity<ApiResponse<Object>> getUsers(
            @RequestParam(value = "role", required = false) String roleName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "id", required = false) UUID id) {

        if (id != null) {
            ApiResponse<UserResponseDto> response = userService.getUserById(id);
            return ResponseEntity.status(response.getStatus())
                    .body(new ApiResponse<>(response.getStatus(), response.getMessage(), response.getResponse()));
        }

        if (roleName != null) {
            ApiResponse<List<BaseResponse>> response = userService.getUsersByRole(roleName);
            return ResponseEntity.status(response.getStatus())
                    .body(new ApiResponse<>(response.getStatus(), response.getMessage(), response.getResponse()));
        }

        if (email != null) {
            ApiResponse<UserResponseDto> response = userService.getUsersByEmail(email);
            return ResponseEntity.status(response.getStatus())
                    .body(new ApiResponse<>(response.getStatus(), response.getMessage(), response.getResponse()));
        } else {
            ApiResponse<BaseResponse> response = userService.getUser();
            return ResponseEntity.status(response.getStatus())
                    .body(new ApiResponse<>(response.getStatus(), response.getMessage(), response.getResponse()));

        }

    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDto>> getUsers(
            @PathVariable UUID id) {

            ApiResponse<UserResponseDto> response = userService.getUserById(id);
            return ResponseEntity.status(response.getStatus())
                    .body(new ApiResponse<>(response.getStatus(), response.getMessage(), response.getResponse()));



    }



    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MENTOR')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @RequestPart(value = "userDto", required = false) String userDtoString,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            // Convert JSON string to UserDto
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(userDtoString, UserDto.class);

            String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ApiResponse<UserResponseDto> response = userService.updateUser(loggedInUserEmail, userDto, image);

            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<UserResponseDto> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(500);
            errorResponse.setMessage("Failed to update user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        ApiResponse<String> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }




}
