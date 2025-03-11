package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.Studentproject;
import com.leaderboard.demo.service.StudentProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-projects")
public class StudentProjectController {

    private final StudentProjectService studentProjectService;

    public StudentProjectController(StudentProjectService studentProjectService) {
        this.studentProjectService = studentProjectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLEGE')")
    public ResponseEntity<ApiResponse<Studentproject>> assignProject(@RequestBody Studentproject dto) {
        ApiResponse<Studentproject> response = studentProjectService.assignProjectToStudent(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Studentproject>>> getProjectsForStudent(@PathVariable UUID studentId) {
        ApiResponse<List<Studentproject>> response = studentProjectService.getProjectsForStudent(studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<Studentproject>>> getStudentsForProject(@PathVariable UUID projectId) {
        ApiResponse<List<Studentproject>> response = studentProjectService.getStudentsForProject(projectId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studentProjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLEGE')")
    public ResponseEntity<ApiResponse<String>> deleteStudentProject(@PathVariable UUID studentProjectId) {
        ApiResponse<String> response = studentProjectService.deleteStudentProject(studentProjectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Studentproject>>> getAllStudentProjects() {
        ApiResponse<List<Studentproject>> response = studentProjectService.getAllStudentProjects();
        return ResponseEntity.ok(response);
    }
}