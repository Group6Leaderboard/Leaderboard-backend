package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
//        Optional<User> user = userService.getUserById(id);
//        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "No user found  "), HttpStatus.NOT_FOUND);
        }
    }



    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User savedUser = userService.AddUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<User> deleteUser(@PathVariable UUID id) {
//        User deletedUser = userService.deleteUser(id);
//        return deletedUser != null ? new ResponseEntity<>(deletedUser, HttpStatus.OK)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        boolean isDeleted = userService.deleteUser(id);
        return isDeleted
                ? new ResponseEntity<>("User deleted successfully.", HttpStatus.OK)
                : new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
}
