package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.BaseResponse;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.dto.UserResponseDto;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;



    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('COLLEGE') and #roleName == 'STUDENT')")
    public ResponseEntity<ApiResponse<Object>> getUsers(
            @RequestParam(value = "role", required = false) String roleName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "id", required = false) UUID id)
    {

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
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Provide either 'id', 'email', or 'role'", null));
    }


    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MENTOR')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @RequestPart(value = "userDto", required = false) UserDto userDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponseDto> response = userService.updateUser(loggedInUserEmail, userDto, image);

        return ResponseEntity.status(response.getStatus()).body(response);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        ApiResponse<String> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }




}
