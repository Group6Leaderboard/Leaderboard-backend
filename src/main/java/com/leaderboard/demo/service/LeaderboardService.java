package com.leaderboard.demo.service;


import com.leaderboard.demo.dto.LeaderboardDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;

    public List<LeaderboardDto> getLeaderboard(String type) {
        if ("project".equalsIgnoreCase(type)) {
            return getProjectLeaderboard();
        } else if ("student".equalsIgnoreCase(type)) {
            return getStudentLeaderboard();
        } else if ("college".equalsIgnoreCase(type)) {
            return getCollegeLeaderboard();
        } else {
            throw new IllegalArgumentException("Invalid leaderboard type");
        }
    }

    private List<LeaderboardDto> getProjectLeaderboard() {
        List<Project> projects = projectRepository.findByScoreGreaterThanOrderByScoreDesc(0);
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank = 1;

        for (Project project : projects) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(project.getName());
            dto.setScore(project.getScore());
            dto.setCollege(project.getCollege().getName());

            int taskCount = taskRepository.countByAssignedToAndIsDeletedFalse(project);
            dto.setNumTasks(taskCount);

            leaderboard.add(dto);
        }

        return leaderboard;
    }

    @Transactional
    private List<LeaderboardDto> getStudentLeaderboard(){
        List<User> students = userRepository.findByRoleNameAndScoreGreaterThanOrderByScoreDesc("STUDENT", 0);
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank=1;
        for (User student : students) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(student.getName());
            dto.setScore(student.getScore());
            dto.setCollege(student.getCollege().getName());
            dto.setNumProjects(studentProjectRepository.countByStudentId(student.getId()));
            dto.setImage(student.getImage());
            System.out.println(dto.getImage());

            List<Project> projects = studentProjectRepository.findProjectsByStudentId(student.getId());

            int taskCount = projects.isEmpty() ? 0 : taskRepository.countByAssignedToInAndIsDeletedFalse(projects);
            dto.setNumTasks(taskCount);

            leaderboard.add(dto);
        }
        return leaderboard;
    }

    private List<LeaderboardDto> getCollegeLeaderboard() {
        List<College> colleges = collegeRepository.findByScoreGreaterThanOrderByScoreDesc(0);
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank = 1;

        for (College college : colleges) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(college.getName());
            dto.setScore(college.getScore());

            int projectCount = projectRepository.countByCollegeId(college.getId());
            dto.setNumProjects(projectCount);

            List<Project> projects = projectRepository.findByCollegeId(college.getId());

            int taskCount = projects.isEmpty() ? 0 : taskRepository.countByAssignedToInAndIsDeletedFalse(projects);
            dto.setNumTasks(taskCount);

            leaderboard.add(dto);

        }

        return leaderboard;
    }

}
