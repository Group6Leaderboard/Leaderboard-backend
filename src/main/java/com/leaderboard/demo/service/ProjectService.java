package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.ProjectDto;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

   @Autowired
   private UserRepository userRepository;

    public Project saveProject(Project project) {

        return projectRepository.save(project);
    }


//    public Project getProjectById(UUID projectId) {
//
//        return projectRepository.findById(projectId).orElse(null);
//    }

    public Optional<Project> getProjectById(UUID projectId){
        return projectRepository.findById(projectId)
                .filter(project -> !project.isDeleted());
    }


    public List<Project> getAllProjects() {
        return projectRepository.findByIsDeletedFalse();
    }


    public List<Project> getProjectsByMentor(UUID mentorId) {

        return projectRepository.findByMentorId(mentorId);
    }

    // ✅ New PUT method for updating projects
    public Project updateProject(UUID projectId, ProjectDto projectDto) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            // Update only non-null fields
            if (projectDto.getName() != null) {
                project.setName(projectDto.getName());
            }
            if (projectDto.getDescription() != null) {
                project.setDescription(projectDto.getDescription());
            }
            if (projectDto.getScore() != null) {
                project.setScore(projectDto.getScore());
            }

            // Update Mentor if provided
            if (projectDto.getMentorId() != null) {
                User mentor = userRepository.findById(projectDto.getMentorId())
                        .orElseThrow(() -> new RuntimeException("Mentor not found"));
                project.setMentor(mentor);
            }

            // Update College if provided
            if (projectDto.getCollegeId() != null) {
                User college = userRepository.findById(projectDto.getCollegeId())
                        .orElseThrow(() -> new RuntimeException("College not found"));
                project.setCollege(college);
            }

            // Update timestamp
            project.setUpdatedAt(LocalDateTime.now());

            return projectRepository.save(project);
        } else {
            throw new RuntimeException("Project not found");
        }
    }


//    public Project deleteProject(UUID projectId) {
//        Project project = projectRepository.findById(projectId).orElse(null);
//        if (project != null) {
//            project.setDeleted(true);
//            return projectRepository.save(project);
//        }
//        return null;
//    }

    public boolean deleteProject(UUID projectId){
        Optional<Project> project=projectRepository.findById(projectId);
        if(project.isPresent()){
            Project p=project.get();
            p.setDeleted(true);
            projectRepository.save(p);
            return true;
        }
        return false;
    }


}
