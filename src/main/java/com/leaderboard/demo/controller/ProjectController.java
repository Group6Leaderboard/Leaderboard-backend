package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.ProjectDto;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.UserRepository;
import com.leaderboard.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();

        Map<String, Object> response = new LinkedHashMap<>();



        response.put("status", 200);
        response.put("message", "Ok");
        response.put("data", projects);


        return ResponseEntity.ok(response);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(@RequestBody ProjectDto projectDto) {
        try {
            ProjectDto savedProject = projectService.createProject(projectDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Project created successfully", savedProject));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (AccessDeniedException e){
            return  ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403,"You do not have permission to create a project",null));
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "An error occurred", null));
        }
    }






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
