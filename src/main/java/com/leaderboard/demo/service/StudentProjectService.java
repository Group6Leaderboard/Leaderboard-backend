package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.StudentProject;
import com.leaderboard.demo.repository.StudentProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentProjectService {

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    // Assign project to student
    public StudentProject assignProjectToStudent(StudentProject studentProject) {
        return studentProjectRepository.save(studentProject);
    }

    // Get all projects for a student
    public List<StudentProject> getProjectsForStudent(UUID studentId) {
        return studentProjectRepository.findByStudentId(studentId);
    }

    // Get all students assigned to a project
    public List<StudentProject> getStudentsForProject(UUID projectId) {
        return studentProjectRepository.findByProjectId(projectId);
    }

    // Delete the student-project relation (soft delete)
    public StudentProject deleteStudentProject(UUID studentProjectId) {
        StudentProject studentProject = studentProjectRepository.findById(studentProjectId).orElse(null);
        if (studentProject != null) {
            studentProject.setDeleted(true);
            return studentProjectRepository.save(studentProject);
        }
        return null;
    }

    public List<StudentProject> getAllStudentProjects() {
        return List.of();
    }
}
