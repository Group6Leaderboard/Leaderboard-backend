package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.TaskDTO;
import com.leaderboard.demo.dto.TaskPostDTO;
import com.leaderboard.demo.dto.TaskPutDTO;
import com.leaderboard.demo.dto.UserDto;
import com.leaderboard.demo.entity.*;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<TaskDTO> getTasksAssignedBy(String email) {
        User mentor = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        return taskRepository.findByAssignedByAndIsDeletedFalse(mentor)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<TaskDTO> getTasksAssignedToProjects(List<UUID> projectIds) {
        List<Project> projects = projectRepository.findAllById(projectIds);
        List<Task> tasks = taskRepository.findByAssignedToInAndIsDeletedFalse(projects);

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public TaskDTO createTask(TaskPostDTO taskPostDTO,UUID assignedBy){
        Project project=projectRepository.findByIdAndIsDeletedFalse(taskPostDTO.getAssignedTo())
                .orElseThrow(()->new RuntimeException("project not found or deleted"));
        User mentor=userRepository.findByIdAndIsDeletedFalse(assignedBy)
                .orElseThrow(()->new RuntimeException("mentor not found"));

        if (!project.getMentor().getId().equals(mentor.getId())) {
            throw new IllegalArgumentException("Mentor is not assigned to the project.");
        }


        if(taskRepository.existsByNameAndAssignedToIdAndIsDeletedFalse(taskPostDTO.getName(),taskPostDTO.getAssignedTo())){
            throw new IllegalArgumentException("Task name already exists in the project.");
        }
        if (taskPostDTO.getDueDate() != null && taskPostDTO.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
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

        @Transactional
        public TaskDTO updateTask(UUID id, TaskPutDTO taskPutDTO) throws IOException {
            Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Task not found or deleted"));

            String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User loggedInUser = userRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

            LocalDateTime now = LocalDateTime.now();

            if (task.getDueDate() != null && now.isAfter(task.getDueDate()) && task.getStatus().equals("Not Submitted")) {
                task.setStatus("Overdue");
                taskRepository.save(task);

                if (taskPutDTO.getFile() != null && !taskPutDTO.getFile().isEmpty()) {
                    throw new RuntimeException("Task is overdue and cannot be submitted.");
                }
            }


            if ("MENTOR".equals(loggedInUser.getRole().getName())) {
                if (!task.getAssignedBy().getId().equals(loggedInUser.getId())) {
                    throw new IllegalArgumentException("Mentor is not assigned to this project.");
                }

                if (taskPutDTO.getDuedate() != null) {
                    task.setDueDate(taskPutDTO.getDuedate());
                }

                if (taskPutDTO.getScore() != null) {
                    validateScoring(task);
                    applyScoring(task, taskPutDTO.getScore());
                }
            }


            else if ("STUDENT".equals(loggedInUser.getRole().getName())) {
                boolean isStudentAssigned = studentProjectRepository.existsByStudentIdAndProjectIdAndIsDeletedFalse(
                        loggedInUser.getId(),
                        task.getAssignedTo().getId()
                );

                if (!isStudentAssigned) {
                    throw new IllegalArgumentException("Student is not assigned to this task.");
                }

                if (taskPutDTO.getFile() != null && !taskPutDTO.getFile().isEmpty()) {
                    if (task.getDueDate() != null && now.isAfter(task.getDueDate())) {
                        throw new RuntimeException("Task is overdue and cannot be submitted.");
                    }
                    task.setFile(taskPutDTO.getFile().getBytes());
                    task.setStatus("To be reviewed");
                } else {
                    throw new IllegalArgumentException("Include file");
                }
            } else {
                throw new IllegalArgumentException("Unauthorized action");
            }

            task.setUpdatedAt(now);
            task = taskRepository.save(task);
            return convertToDTO(task);
        }
        private void validateScoring(Task task) {
            LocalDateTime now = LocalDateTime.now();

            if (task.getDueDate() == null) {
                throw new RuntimeException("Task cannot be scored as due date is not set.");
            }

            if (now.isBefore(task.getDueDate())) {
                throw new RuntimeException("Task cannot be scored before the due date.");
            }

            if (!task.getStatus().equals("To be reviewed")) {
                throw new RuntimeException("Task cannot be scored unless it is in 'To be reviewed' status.");
            }
        }
        private void applyScoring(Task task, int score) {
            task.setScore(score);
            task.setStatus("Completed");

            Project project = task.getAssignedTo();
            int totalProjectScore = taskRepository.sumScoresByProjectId(project.getId());
            project.setScore(totalProjectScore);
            projectRepository.save(project);

            List<StudentProject> studentProjects = studentProjectRepository.findByProjectIdAndIsDeletedFalse(project.getId());
            for (StudentProject sp : studentProjects) {
                int totalStudentScore = studentProjectRepository.sumScoresByStudentId(sp.getStudent().getId());
                sp.getStudent().setScore(totalStudentScore);
                userRepository.save(sp.getStudent());
            }

            College college = project.getCollege();
            int totalCollegeScore = projectRepository.sumScoresByCollegeId(college.getId());
            college.setScore(totalCollegeScore);
            collegeRepository.save(college);
        }


    public TaskDTO getTaskbyId(UUID id){
        Task task = taskRepository.findByIdAndIsDeletedFalse(id).orElse(null);
        if (task == null) {
            return null;
        }
        return convertToDTO(task);
    }

    @Transactional
    public List<TaskDTO> getAllTasks(){
        return taskRepository.findByIsDeletedFalse().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void deleteTask(UUID id){
        Task task=taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new RuntimeException("Task not found or already deleted"));
        task.setDeleted(true);
        taskRepository.save(task);

    }
    @Transactional
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Object loggedInUser;
        String role;

        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLLEGE"))) {

            loggedInUser = collegeRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("College not found"));
            role = "COLLEGE";
        } else {
            loggedInUser = userRepository.findByEmailAndIsDeletedFalse(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            role = ((User) loggedInUser).getRole().getName();
        }

        List<Task> tasks;

        if ("ADMIN".equals(role)) {
            tasks = taskRepository.findByAssignedToIdAndIsDeletedFalse(projectId);
        } else if ("MENTOR".equals(role)) {
            tasks = taskRepository.findByAssignedToIdAndAssignedByIdAndIsDeletedFalse(
                    projectId, ((User) loggedInUser).getId());
        } else if ("COLLEGE".equals(role)) {
            College college = (College) loggedInUser;
            if (!projectRepository.existsByIdAndCollegeIdAndIsDeletedFalse(projectId, college.getId())) {
                throw new IllegalArgumentException("College does not have access to this project");
            }
            tasks = taskRepository.findByAssignedToIdAndIsDeletedFalse(projectId);
        } else if ("STUDENT".equals(role)) {
            boolean isAssigned = studentProjectRepository.existsByStudentIdAndProjectIdAndIsDeletedFalse(
                    ((User) loggedInUser).getId(), projectId);
            if (!isAssigned) {
                throw new IllegalArgumentException("Student is not assigned to this project");
            }
            List<StudentProject> studentProjects = studentProjectRepository.findAll();
            for (StudentProject sp : studentProjects) {
                System.out.println("Student: " + sp.getStudent().getId() + " -> Project: " + sp.getProject().getId());
            }
            tasks = taskRepository.findByAssignedToIdAndIsDeletedFalse(projectId);
            System.out.println("ernfi");
        } else {
            throw new RuntimeException("Unauthorized");
        }


        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }




    private TaskDTO convertToDTO(Task task){
        System.out.println("kerii");
        TaskDTO dto=new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setScore(task.getScore());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setDeleted(task.isDeleted());
        System.out.println("edkkan");
        if (task.getAssignedBy() != null) {
            System.out.println("povnuuu");
            dto.setAssignedBy(task.getAssignedBy().getId());
        }

        dto.setAssignedTo(task.getAssignedTo().getId());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        if (task.getFile() != null) {
            dto.setFile(task.getFile());
        }
        return dto;
    }


}