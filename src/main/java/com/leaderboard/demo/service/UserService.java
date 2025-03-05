package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


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
