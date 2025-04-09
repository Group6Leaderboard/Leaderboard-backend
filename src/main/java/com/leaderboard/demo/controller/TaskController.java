package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.dto.TaskPostDTO;
import com.leaderboard.demo.dto.TaskPutDTO;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.repository.StudentProjectRepository;
import com.leaderboard.demo.repository.UserRepository;
import com.leaderboard.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@RequestBody TaskPostDTO taskPostDTO) {
        try {
            UUID loggedInUserId = getLoggedInUserId();
            TaskDTO taskDTO = taskService.createTask(taskPostDTO, loggedInUserId);
            System.out.println("controller1");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Task created successfully", taskDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    private UUID getLoggedInUserId() {
        String loggedInUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = userRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
        return loggedInUser.getId();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MENTOR') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable UUID id,
                                              @RequestParam(value = "dueDate", required = false) LocalDateTime dueDate,
                                              @RequestParam(value = "score", required = false) Integer score,
                                              @RequestParam(value = "file", required = false) MultipartFile file)
            {
                try {
                    if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
                        return ApiResponse.badRequest("Due date cannot be in the past.");
                    }
                    TaskPutDTO taskPutDTO = new TaskPutDTO();
                    taskPutDTO.setDuedate(dueDate);
                    taskPutDTO.setScore(score);
                    taskPutDTO.setFile(file);
                    TaskDTO taskDTO = taskService.updateTask(id, taskPutDTO);
                    return ApiResponse.success(taskDTO, "Task updated successfully");
                } catch (RuntimeException e) {
                    return ApiResponse.badRequest(e.getMessage());
                } catch (IOException e) {
                    return ApiResponse.internalServerError("Internal server error");
                }

    }

    @GetMapping("/{id}")

    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable UUID id) {
        TaskDTO taskDTO = taskService.getTaskbyId(id);
        if (taskDTO == null) {
            return ApiResponse.notFound("Task not found");
        }
        return ApiResponse.success(taskDTO, "Task retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('MENTOR') or hasRole('STUDENT')")
    @Transactional
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Logged in user: " + loggedInUserEmail);

        User user = userRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<TaskDTO> tasks;

        if (user.getRole().getName().equals("MENTOR")) {
            tasks = taskService.getTasksAssignedBy(loggedInUserEmail);
        } else if (user.getRole().getName().equals("STUDENT")) {
            List<Project> studentProjects = studentProjectRepository.findProjectsByStudentId(user.getId());
            List<UUID> projectIds = studentProjects.stream()
                    .map(Project::getId)
                    .collect(Collectors.toList());

            tasks = taskService.getTasksAssignedToProjects(projectIds);
        } else {
            tasks = Collections.emptyList();
        }

        return ApiResponse.success(tasks, "Tasks retrieved successfully");
    }





    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR') or hasRole('COLLEGE') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProjectId(@PathVariable UUID projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tasks fetched successfully", tasks));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletTask(@PathVariable UUID id){
     try{
         taskService.deleteTask(id);
         return ApiResponse.noContent("Task deleted Succesfully");
     }catch (RuntimeException e){
         return ApiResponse.badRequest(e.getMessage());
     }
        }
}