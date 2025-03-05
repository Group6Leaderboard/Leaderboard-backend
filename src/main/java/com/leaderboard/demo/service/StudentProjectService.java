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


    public StudentProject assignProjectToStudent(StudentProject studentProject) {
        return studentProjectRepository.save(studentProject);
    }


    public List<StudentProject> getProjectsForStudent(UUID studentId) {
        return studentProjectRepository.findByStudentId(studentId);
    }


    public List<StudentProject> getStudentsForProject(UUID projectId) {
        return studentProjectRepository.findByProjectId(projectId);
    }


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
