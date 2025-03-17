package com.leaderboard.demo.controller;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.dto.TaskPostDTO;
import com.leaderboard.demo.dto.TaskPutDTO;
import com.leaderboard.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/{mentorId}")
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@RequestBody TaskPostDTO taskPostDTO, @PathVariable UUID mentorId) {
        try {
            TaskDTO taskDTO = taskService.createTask(taskPostDTO, mentorId);
            return ApiResponse.created(taskDTO, "Task created successfully");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable UUID id,
                                              @RequestParam(value = "dueDate", required = false) LocalDateTime dueDate,
                                              @RequestParam(value = "score", required = false) Integer score,
                                              @RequestParam(value = "file", required = false) MultipartFile file)
            {
                try {
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
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ApiResponse.success(tasks, "Tasks retrieved successfully");

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletTask(@PathVariable UUID id){
 try{
     taskService.deletTask(id);
     return ApiResponse.noContent("Task deleted Succesfully");
 }catch (RuntimeException e){
     return ApiResponse.badRequest(e.getMessage());
 }
    }
}