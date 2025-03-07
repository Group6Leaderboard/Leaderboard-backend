//package com.leaderboard.demo.service;
//
//import com.leaderboard.demo.entity.Task;
//import com.leaderboard.demo.repository.TaskRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class TaskService {
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//
////    public Task saveTask(Task task) {
////        return taskRepository.save(task);
////    }
//
//    public Task saveTask(String title, String description, UUID assignedToId, MultipartFile file){
//        Task task=new Task();
//        task.setTitle(title);
//        task.setDescription(description);
//        task.setAssignedToId(assignedToId);
//        task.setDeleted(false);
//
//        if(file !=null && !file.isEmpty()){
//            try{
//                task.setFile(file.getBytes());
//                task.setFileName(file.getOriginalFilename());
//            }catch (IOException e){
//                throw new RuntimeException("File upload failed",e);
//            }
//        }
//        return taskRepository.save(task);
//    }
//
//
////    public Task getTaskById(UUID taskId) {
////        return taskRepository.findById(taskId).orElse(null);
////    }
//
//    public Task getTaskById(UUID taskId){
//        Task task=taskRepository.findById(taskId).orElse(null);
//        return (task !=null && !task.isDeleted()) ? task :null;
//    }
//
//
//    public List<Task> getTasksByProjectId(UUID projectId) {
//
//        return taskRepository.findByAssignedToId(projectId);
//
//    }
//
////
////    public List<Task> getAllTasks() {
////        return taskRepository.findAll();
////    }
//
//    public List<Task> getAllTasks(){
//        return taskRepository.findAll().stream()
//                .filter(task -> !task.isDeleted())
//                .collect(Collectors.toList());
//    }
//
////
////    public Task deleteTask(UUID taskId) {
////        Task task = taskRepository.findById(taskId).orElse(null);
////        if (task != null) {
////            task.setDeleted(true);
////            return taskRepository.save(task);
////        }
////        return null;
////    }
//
//    public boolean deleteTask(UUID taskId){
//        Task task=taskRepository.findById(taskId).orElse(null);
//        if(task !=null){
//            task.setDeleted(true);
//            taskRepository.save(task);
//            return true;
//        }
//      return false;
//    }
//}

package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
     public TaskService(TaskRepository taskRepository){
         this.taskRepository=taskRepository;

     }

    // Convert Entity -> DTO
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setName(task.getName());
        dto.setScore(task.getScore());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setAssignedBy(task.getAssignedBy() != null ? task.getAssignedBy().getId() : null);
        dto.setAssignedTO(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null);


        return dto;
    }

    // Convert DTO -> Entity
    private Task convertToEntity(TaskDTO dto, MultipartFile file) throws IOException {
        Task task = new Task();
        task.setId(dto.getId());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setName(dto.getName());
        task.setScore(dto.getScore());
        task.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            task.setFile(file.getBytes());
        } else {
            task.setFile(null);
        }

        task.setCreatedAt(LocalDateTime.now());

        return task;
    }

     public TaskDTO saveTask(TaskDTO taskDTO,MultipartFile file) throws IOException{
         Task task = convertToEntity(taskDTO, file);
         Task savedTask = taskRepository.save(task);
         return convertToDTO(savedTask);
     }

     public List<TaskDTO> getAllTasks(){
         return taskRepository.findByIsDeletedFalse()
                 .stream()
                 .map(this::convertToDTO)
                 .collect(Collectors.toList());
     }
    public Optional<TaskDTO> getTaskById(UUID id){
         return taskRepository.findByIdAndIsDeletedFalse(id).map(this::convertToDTO);
    }

    public TaskDTO updateTask(UUID id,TaskDTO taskDTO,MultipartFile file) throws IOException{
         Optional<Task> optionalTask=taskRepository.findById((id));
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDescription(taskDTO.getDescription());
            task.setStatus(taskDTO.getStatus());
            task.setName(taskDTO.getName());
            task.setScore(taskDTO.getScore());

            if (file != null && !file.isEmpty()) {
                task.setFile(file.getBytes());
            }

            task.setUpdatedAt(LocalDateTime.now());
            Task updatedTask = taskRepository.save(task);
            return convertToDTO(updatedTask);
        }
        return null;
    }

    public boolean softDeleteTask(UUID id) {
        Optional<Task> optionalTask = taskRepository.findByIdAndIsDeletedFalse(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (task.isDeleted()) {
                return false; // Task is already deleted
            }
            task.setDeleted(true);
            taskRepository.save(task);
            return true;
        }
        return false;
    }

    }
