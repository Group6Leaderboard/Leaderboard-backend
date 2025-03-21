package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.dto.TaskPostDTO;
import com.leaderboard.demo.dto.TaskPutDTO;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.TaskRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;



    //with validation
    public TaskDTO createTask(TaskPostDTO taskPostDTO,UUID assignedBy){
        Project project=projectRepository.findByIdAndIsDeletedFalse(taskPostDTO.getAssignedTo())
                .orElseThrow(()->new RuntimeException("project not found or deleted"));
        User mentor=userRepository.findByIdAndIsDeletedFalse(assignedBy)
                .orElseThrow(()->new RuntimeException("mentor not found"));

        if(!project.getMentor().getId().equals(mentor.getId())){
            throw new IllegalArgumentException("Mentor is not assigned to the project.");
        }
        //
        if(taskRepository.existsByNameAndAssignedToIdAndIsDeletedFalse(taskPostDTO.getName(),taskPostDTO.getAssignedTo())){
            throw new IllegalArgumentException("Task name already exists in the project.");
        }
        Task task = new Task();
        task.setName(taskPostDTO.getName());
        task.setDescription(taskPostDTO.getDescription());
        task.setDueDate(taskPostDTO.getDueDate());
        task.setStatus("Not Submitted");
        task.setAssignedTo(project);
        task.setAssignedBy(mentor);

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

public TaskDTO updateTask(UUID id,TaskPutDTO taskPutDTO) throws IOException{
    Task task = taskRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Task not found or deleted"));

    LocalDateTime now = LocalDateTime.now();

    if(task.getDueDate()!=null && now.isAfter(task.getDueDate()) && task.getStatus().equals("Not submitted")){
        task.setStatus("Overdue");
        taskRepository.save(task);
        if(taskPutDTO.getFile()!=null && !taskPutDTO.getFile().isEmpty()){
            throw new RuntimeException("Task is overdue and cannot be submitted.");
        }
    }
    //update if dueDate if provided
    if(taskPutDTO.getDuedate()!=null){
        task.setDueDate(taskPutDTO.getDuedate());
    }
    // Process file upload if provided
    if(taskPutDTO.getFile()!=null && !taskPutDTO.getFile().isEmpty()){
        if(task.getDueDate() !=null && now.isAfter(task.getDueDate())){
            throw new RuntimeException("Task is overdue and cannot be submitted .");
        }
        task.setFile(taskPutDTO.getFile().getBytes());
        task.setStatus("To be reviewed");
    }
    // Process scoring if provided
    if(taskPutDTO.getScore()!=null){

        //validate scoring conditions
        if(task.getDueDate()==null){
            throw new RuntimeException("Task cannot be scored as due date is not set.");

        }
        if(now.isBefore(task.getDueDate())){
            throw new RuntimeException("Task cannot be scored before te due date.");
        }
        if(!task.getStatus().equals("To be reviewed")){
            throw new RuntimeException("Task cannot be scored unless it is in 'To be reviewed' status.");
        }
        // Apply score and update status
        task.setScore(taskPutDTO.getScore());
        task.setStatus("Completed");
    }
    task = taskRepository.save(task);
    return convertToDTO(task);
}

    public TaskDTO getTaskbyId(UUID id){
        Task task = taskRepository.findByIdAndIsDeletedFalse(id).orElse(null);
        if (task == null) {
            return null;
        }
        return convertToDTO(task);
    }

    public List<TaskDTO> getAllTasks(){
        return taskRepository.findByIsDeletedFalse().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void deletTask(UUID id){
        Task task=taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new RuntimeException("Task not found or already deleted"));
        task.setDeleted(true);
        taskRepository.save(task);

    }
//
//    public TaskDTO scoreTask(UUID id, Integer score) {
//        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
//                .orElseThrow(() -> new RuntimeException("Task not found or deleted"));
//
//        if (!task.getStatus().equals("To be reviewed") && !LocalDateTime.now().isAfter(task.getDueDate())) {
//            throw new RuntimeException("Task cannot be scored before review or before due date.");
//        }
//
//        task.setScore(score);
//        task.setStatus("Completed");
//        task = taskRepository.save(task);
//        return convertToDTO(task);
//    }
public TaskDTO scoreTask(UUID id, Integer score) {
    Task task = taskRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Task not found or deleted"));

    LocalDateTime now = LocalDateTime.now();

    // ✅ Check if due date is set
    if (task.getDueDate() == null) {
        throw new RuntimeException("Task cannot be scored as due date is not set.");
    }

    // ✅ Ensure scoring is allowed only after the due date
    if (now.isBefore(task.getDueDate())) {
        throw new RuntimeException("Task cannot be scored before the due date.");
    }

    // ✅ Allow scoring only if status is "To be reviewed"
    if (!task.getStatus().equals("To be reviewed")) {
        throw new RuntimeException("Task cannot be scored unless it is in 'To be reviewed' status.");
    }

    task.setScore(score);
    task.setStatus("Completed");
    task = taskRepository.save(task);

    return convertToDTO(task);
}



    private TaskDTO convertToDTO(Task task){
        TaskDTO dto=new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setScore(task.getScore());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setDeleted(task.isDeleted());
        if (task.getAssignedBy() != null) {
            dto.setAssignedBy(task.getAssignedBy().getId());
        }

        dto.setAssignedTo(task.getAssignedTo().getId());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setFile(task.getFile());
        return dto;
    }
}