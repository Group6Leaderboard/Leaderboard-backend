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
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        ApiResponse<List<UserResponseDto>> response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable UUID id) {
        ApiResponse<UserResponseDto> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable UUID id,
//                                                                   @RequestBody UserDto userDto) {
//        ApiResponse<UserResponseDto> response = userService.updateUser(id, userDto);
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable UUID id,
                                                                   @RequestPart(value = "userDto", required = false) UserDto userDto,
                                                                   @RequestPart(value = "image", required = false) MultipartFile image) {
        ApiResponse<UserResponseDto> response = userService.updateUser(id, userDto, image);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        ApiResponse<String> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('COLLEGE') and #roleName == 'STUDENT')")
    public ResponseEntity<ApiResponse<List<BaseResponse>>> getUsersByRole(@PathVariable String roleName) {
        ApiResponse<List<BaseResponse>> response = userService.getUsersByRole(roleName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
