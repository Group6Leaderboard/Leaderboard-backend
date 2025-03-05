package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ProjectDto;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getProjectById(@PathVariable UUID id) {
       Optional<Project> project=projectService.getProjectById(id);
       if (project.isPresent()){
           return new ResponseEntity<>(project.get(),HttpStatus.OK);
       }
       else {
           return new ResponseEntity<>(Map.of("message","No project found"),HttpStatus.NOT_FOUND);
       }
    }




    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectService.saveProject(project);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Project> deleteProject(@PathVariable UUID id) {
//        Project deletedProject = projectService.deleteProject(id);
//        return deletedProject != null ? new ResponseEntity<>(deletedProject, HttpStatus.OK)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID id){
        boolean isDeleted = projectService.deleteProject(id);
        return isDeleted
                ?new ResponseEntity<>("Project Deleted successfully.",HttpStatus.OK)
                :new ResponseEntity<>("project not found.",HttpStatus.NOT_FOUND);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody ProjectDto projectDto) {
        try {
            Project updatedProject = projectService.updateProject(id, projectDto);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
