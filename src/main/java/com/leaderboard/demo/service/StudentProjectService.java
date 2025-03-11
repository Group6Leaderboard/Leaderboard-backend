package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.Studentproject;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.StudentProject;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.exception.ResourceNotFoundException;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.StudentProjectRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentProjectService {
    private final StudentProjectRepository studentProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public StudentProjectService(StudentProjectRepository studentProjectRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.studentProjectRepository = studentProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public ApiResponse<Studentproject> assignProjectToStudent(Studentproject studentProjectDto) {
        Optional<User> studentOpt = userRepository.findById(studentProjectDto.getStudentId());
        if (studentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentProjectDto.getStudentId());
        }

        Optional<Project> projectOpt = projectRepository.findById(studentProjectDto.getProjectId());
        if (projectOpt.isEmpty()) {
            throw new ResourceNotFoundException("Project not found with ID: " + studentProjectDto.getProjectId());
        }

        User student = studentOpt.get();
        Project project = projectOpt.get();

        if (!student.getCollege().getId().equals(project.getCollege().getId())) {
            throw new IllegalArgumentException("Student and Project belong to different colleges!");
        }

        Optional<StudentProject> existingAssignment = studentProjectRepository.findByStudentAndProjectAndIsDeletedFalse(student, project);
        if (existingAssignment.isPresent()) {
            throw new IllegalArgumentException("This student is already assigned to this project");
        }

        StudentProject studentEntity = new StudentProject();
        studentEntity.setStudent(student);
        studentEntity.setProject(project);
        studentEntity.setCreatedAt(LocalDateTime.now());
        studentEntity.setUpdatedAt(LocalDateTime.now());
        studentEntity.setDeleted(false);

        StudentProject savedEntity = studentProjectRepository.save(studentEntity);
        Studentproject dto = new Studentproject(savedEntity.getId(), student.getId(), project.getId());
        return new ApiResponse<>(200, "Success", dto);
    }

    public ApiResponse<List<Studentproject>> getProjectsForStudent(UUID studentId) {
        // Check if student exists
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        List<StudentProject> studentProjects = studentProjectRepository.findByStudentIdAndIsDeletedFalse(studentId);
        List<Studentproject> dtoList = studentProjects.stream()
                .map(sp -> new Studentproject(sp.getId(), sp.getStudent().getId(), sp.getProject().getId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", dtoList);
    }

    public ApiResponse<List<Studentproject>> getStudentsForProject(UUID projectId) {
        // Check if project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        List<StudentProject> studentProjects = studentProjectRepository.findByProjectIdAndIsDeletedFalse(projectId);
        List<Studentproject> dtoList = studentProjects.stream()
                .map(sp -> new Studentproject(sp.getId(), sp.getStudent().getId(), sp.getProject().getId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", dtoList);
    }

    public ApiResponse<String> deleteStudentProject(UUID studentProjectId) {
        Optional<StudentProject> studentProjectOpt = studentProjectRepository.findByIdAndIsDeletedFalse(studentProjectId);
        if (studentProjectOpt.isEmpty()) {
            throw new ResourceNotFoundException("StudentProject not found with ID: " + studentProjectId);
        }

        StudentProject studentProject = studentProjectOpt.get();
        studentProject.setDeleted(true);
        studentProject.setUpdatedAt(LocalDateTime.now());
        studentProjectRepository.save(studentProject);

        return new ApiResponse<>(200, "Success", "Deleted");
    }

    public ApiResponse<List<Studentproject>> getAllStudentProjects() {
        List<StudentProject> studentProjects = studentProjectRepository.findByIsDeletedFalse();
        List<Studentproject> dtoList = studentProjects.stream()
                .map(sp -> new Studentproject(sp.getId(), sp.getStudent().getId(), sp.getProject().getId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", dtoList);
    }
}