
package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.TaskRepository;

import com.leaderboard.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

     public TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository){
         this.taskRepository=taskRepository;
         this.userRepository = userRepository;
         this.projectRepository=projectRepository;

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
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String email = auth.getName();

         Optional<User> mentorOpt = userRepository.findByEmail(email);
         if (!mentorOpt.isPresent()) {
             throw new IllegalStateException("Authenticated mentor not found");
         }
         User mentor = mentorOpt.get();

         if (taskDTO.getAssignedTO() == null) {
             throw new IllegalArgumentException("Task must be assigned to a project");
         }

         Optional<Project> projectOpt = projectRepository.findById(taskDTO.getAssignedTO());
         if (!projectOpt.isPresent()) {
             throw new IllegalArgumentException("Project not found with ID: " + taskDTO.getAssignedTO());
         }
         Project project = projectOpt.get();

         if (!project.getMentor().getId().equals(mentor.getId())) {
             throw new AccessDeniedException("You are not assigned to this project");
         }

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
            task.setStatus("Not submitted");
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
                return false;
            }
            task.setDeleted(true);
            taskRepository.save(task);
            return true;
        }
        return false;
    }
    public TaskDTO scoreTask(UUID id, int score) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setScore(score);
            task.setStatus("Completed");
            task.setUpdatedAt(LocalDateTime.now());
            Task updatedTask = taskRepository.save(task);
            return convertToDTO(updatedTask);
        }
        return null;
    }

    public TaskDTO updateTaskFile(UUID id, MultipartFile file) throws IOException {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (file != null && !file.isEmpty()) {
                task.setFile(file.getBytes());
                task.setStatus("Submitted");
                task.setUpdatedAt(LocalDateTime.now());
                Task updatedTask = taskRepository.save(task);
                return convertToDTO(updatedTask);
            }
        }
        return null;
    }

    }
