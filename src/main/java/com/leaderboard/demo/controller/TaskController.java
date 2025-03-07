//package com.leaderboard.demo.controller;
//
//import com.leaderboard.demo.entity.Task;
//import com.leaderboard.demo.service.TaskService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//
//@RequestMapping("/api/tasks")
//
//public class TaskController {
//
//    @Autowired
//    private TaskService taskService;
//
//
//    @GetMapping
//    public ResponseEntity<List<Task>> getAllTasks() {
//        List<Task> tasks = taskService.getAllTasks();
//        return new ResponseEntity<>(tasks, HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
//        Task task = taskService.getTaskById(id);
//        return task != null ? new ResponseEntity<>(task, HttpStatus.OK)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//
////    @PostMapping
////    public ResponseEntity<Task> createTask(@RequestBody Task task) {
////        Task savedTask = taskService.saveTask(task);
////        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
////    }
//
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<Task> createTask(
//            @RequestParam("description") String description,
//            @RequestParam("status") String status,
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("name") String name,
//            @RequestParam("score") int score,
//            @RequestParam("assignedBy") UUID assignedById,
//            @RequestParam("assignedTo") UUID assignedToId){
//
//
//
//     Task savedTask = taskService.saveTask(description, status,file,name,score,assignedById,assignedToId);
//        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
//    }
//
//
////    @DeleteMapping("/{id}")
////    public ResponseEntity<Task> deleteTask(@PathVariable UUID id) {
////        Task deletedTask = taskService.deleteTask(id);
////        return deletedTask != null ? new ResponseEntity<>(deletedTask, HttpStatus.OK)
////                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
////    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
//        boolean deleted = taskService.deleteTask(id);
//        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//}
package com.leaderboard.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService=taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestPart("task") String taskJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        TaskDTO taskDTO = objectMapper.readValue(taskJson, TaskDTO.class);

        TaskDTO savedTask = taskService.saveTask(taskDTO, file);
        return ResponseEntity.ok(savedTask);
    }


    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks=taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }





    @GetMapping("/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable UUID id){
        Optional<TaskDTO> task=taskService.getTaskById(id);

        if(task.isPresent()){
            return ResponseEntity.ok(task.get());

        }else {
            Map<String ,String > response =new HashMap<>();
            response.put("Status","200");
            response.put("message","No such task");
            return ResponseEntity.ok(response);

        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable UUID id,
            @RequestPart("task") String taskJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        TaskDTO taskDTO = objectMapper.readValue(taskJson, TaskDTO.class);

        TaskDTO updatedTask = taskService.updateTask(id, taskDTO, file);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable UUID id) {
        boolean deleted = taskService.softDeleteTask(id);

        Map<String, String> response = new HashMap<>();
        response.put("status", "200");

        if (deleted) {
            response.put("message", "Task deleted successfully");
        } else {
            response.put("message", "No such task");
        }

        return ResponseEntity.ok(response);
    }

}