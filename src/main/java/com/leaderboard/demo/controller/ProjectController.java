package com.leaderboard.demo.controller;

import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return project != null ? new ResponseEntity<>(project, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectService.saveProject(project);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Project> deleteProject(@PathVariable UUID id) {
        Project deletedProject = projectService.deleteProject(id);
        return deletedProject != null ? new ResponseEntity<>(deletedProject, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
