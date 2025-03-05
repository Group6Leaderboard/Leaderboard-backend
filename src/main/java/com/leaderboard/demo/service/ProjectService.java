package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;


    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }


    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }


    public List<Project> getAllProjects() {
        return projectRepository.findByIsDeletedFalse();
    }


    public List<Project> getProjectsByMentor(UUID mentorId) {
        return projectRepository.findByMentorId(mentorId);
    }


    public Project deleteProject(UUID projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null) {
            project.setDeleted(true);
            return projectRepository.save(project);
        }
        return null;
    }
}
