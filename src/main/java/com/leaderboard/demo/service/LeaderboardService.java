package com.leaderboard.demo.service;


import com.leaderboard.demo.dto.LeaderboardDto;
import com.leaderboard.demo.entity.College;
import com.leaderboard.demo.entity.Project;
import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.repository.CollegeRepository;
import com.leaderboard.demo.repository.ProjectRepository;
import com.leaderboard.demo.repository.StudentProjectRepository;
import com.leaderboard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private ProjectRepository projectRepository;

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

    private List<LeaderboardDto> getProjectLeaderboard(){
        List<Project> projects = projectRepository.findTop10ByOrderByScoreDesc();
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank=1;
        for (Project project : projects) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(project.getName());
            dto.setScore(project.getScore());
            dto.setCollege(project.getCollege().getName());
            leaderboard.add(dto);
        }
        return leaderboard;
    }

    private List<LeaderboardDto> getStudentLeaderboard(){
        List<User> students = userRepository.findTop10ByRoleNameOrderByScoreDesc("STUDENT");
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank=1;
        for (User student : students) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(student.getName());
            dto.setScore(student.getScore());
            dto.setCollege(student.getCollege().getName());
            dto.setNumProjects(studentProjectRepository.countByStudentId(student.getId()));
            leaderboard.add(dto);
        }
        return leaderboard;
    }

    private List<LeaderboardDto> getCollegeLeaderboard(){
        List<College> colleges = collegeRepository.findTop10ByOrderByScoreDesc();
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        int rank=1;
        for (College college : colleges) {
            LeaderboardDto dto = new LeaderboardDto();
            dto.setRank(rank++);
            dto.setName(college.getName());
            dto.setScore(college.getScore());
            dto.setNumProjects(projectRepository.countByCollegeId(college.getId()));
            leaderboard.add(dto);
        }
        return leaderboard;
    }
}
