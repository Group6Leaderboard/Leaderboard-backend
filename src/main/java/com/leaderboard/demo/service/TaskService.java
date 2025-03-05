package com.leaderboard.demo.service;

import com.leaderboard.demo.entity.Task;
import com.leaderboard.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Create or update a task
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    // Get a task by ID
    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    // Get all tasks for a specific project
    public List<Task> getTasksByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Delete a task (soft delete)
    public Task deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setDeleted(true);
            return taskRepository.save(task);
        }
        return null;
    }
}
