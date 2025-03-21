package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.Studentproject;
import com.leaderboard.demo.entity.StudentProject;
import com.leaderboard.demo.service.StudentProjectService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public StudentProjectController(StudentProjectService studentProjectService) {
        this.studentProjectService = studentProjectService;
    }
    @PostMapping("/students/{studentId}/projects/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLEGE')")
    public ResponseEntity<ApiResponse<Studentproject>> assignProjectToStudent(
            @PathVariable UUID studentId,
            @PathVariable UUID projectId) {

        ApiResponse<Studentproject> response = studentProjectService.assignProjectToStudent(studentId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<Studentproject>>> getStudentProjects(
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "projectId", required = false) UUID projectId) {

        ApiResponse<List<Studentproject>> response;

        if (studentId != null) {
            response = studentProjectService.getProjectsForStudent(studentId);
        } else if (projectId != null) {
            response = studentProjectService.getStudentsForProject(projectId);
        } else {
            response = studentProjectService.getAllStudentProjects();
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @DeleteMapping("/{studentProjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLEGE')")
    public ResponseEntity<ApiResponse<String>> deleteStudentProject(@PathVariable UUID studentProjectId) {
        ApiResponse<String> response = studentProjectService.deleteStudentProject(studentProjectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }


}