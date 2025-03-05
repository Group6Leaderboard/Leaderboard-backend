package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.Role;
import com.leaderboard.demo.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }

    public Role getRoleById(UUID id){
        return roleRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Role not found with ID: "+id));

    }
    public Role createRole(Role role){
        return roleRepository.save(role);
    }
}
