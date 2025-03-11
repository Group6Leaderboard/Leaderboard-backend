package com.leaderboard.demo.service;

import com.leaderboard.demo.dto.ApiResponse;
import com.leaderboard.demo.dto.ProjectDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
   @Autowired
   private CollegeRepository collegeRepository;

    public Project saveProject(Project project) {

        return projectRepository.save(project);
    }


public List<Project> getAllProjects(){
        return projectRepository.findByIsDeletedFalse();
}

    public Optional<Project> getProjectById(UUID projectId){
        return projectRepository.findById(projectId)
                .filter(project -> !project.isDeleted());
    }




    public List<Project> getProjectsByMentor(UUID mentorId) {

        return projectRepository.findByMentorId(mentorId);
    }

    public Project updateProject(UUID projectId, ProjectDto projectDto) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            if (projectDto.getName() != null) {
                project.setName(projectDto.getName());
            }
            if (projectDto.getDescription() != null) {
                project.setDescription(projectDto.getDescription());
            }
            if (projectDto.getScore() != null) {
                project.setScore(projectDto.getScore());
            }

            if (projectDto.getMentorId() != null) {
                User mentor = userRepository.findById(projectDto.getMentorId())
                        .orElseThrow(() -> new RuntimeException("Mentor not found"));
                project.setMentor(mentor);
            }

            if (projectDto.getCollegeId() != null) {
                College college = collegeRepository.findById(projectDto.getCollegeId())
                        .orElseThrow(() -> new RuntimeException("College not found"));
                project.setCollege(college);
            }

            project.setUpdatedAt(LocalDateTime.now());

            return projectRepository.save(project);
        } else {
            throw new RuntimeException("Project not found");
        }
    }




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

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        College college = null;
        if (projectDto.getCollegeId() != null) {
            college = collegeRepository.findById(projectDto.getCollegeId()).orElse(null);
        }
        project.setCollege(college);

        Optional<User> mentorOptional = userRepository.findById(projectDto.getMentorId());
        if (mentorOptional.isEmpty()) {
            throw new IllegalArgumentException("Mentor not found");
        }
        project.setMentor(mentorOptional.get());
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        ProjectDto responseDto = new ProjectDto();
        responseDto.setId(savedProject.getId());
        responseDto.setName(savedProject.getName());
        responseDto.setDescription(savedProject.getDescription());
        responseDto.setCollegeId(savedProject.getCollege() != null ? savedProject.getCollege().getId() : null);
        responseDto.setMentorId(savedProject.getMentor() != null ? savedProject.getMentor().getId() : null);
        responseDto.setScore(savedProject.getScore());

        return responseDto;
    }




}
