package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.ProjectDto;
import com.leaderboard.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR') or hasRole('COLLEGE') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getAllProjects() {
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Projects fetched successfully", projects)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProjectById(@PathVariable UUID id) {
        ProjectDto project = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        return ResponseEntity.ok(new ApiResponse<>(200, "Project fetched successfully", project));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(@RequestBody ProjectDto projectDto) {
        ProjectDto savedProject = projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Project created successfully", savedProject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
            @PathVariable UUID id,
            @RequestBody ProjectDto projectDto) {

        ProjectDto updatedProject = projectService.updateProject(id, projectDto);
        return ResponseEntity.ok(new ApiResponse<>(200, "Project updated successfully", updatedProject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable UUID id) {
        boolean deleted = projectService.deleteProject(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Project deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Project not found", null));
        }
    }
}
