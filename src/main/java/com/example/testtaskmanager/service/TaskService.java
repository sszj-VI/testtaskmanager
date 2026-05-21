package com.example.testtaskmanager.service;

import com.example.testtaskmanager.entity.Task;
import com.example.testtaskmanager.mapper.TaskMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskMapper taskMapper;

    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public Task createTask(Task task) {
        LocalDateTime now = LocalDateTime.now();

        task.setStatus("TODO");
        task.setCreatedTime(now);
        task.setUpdatedTime(now);

        taskMapper.insert(task);

        return task;
    }

    public List<Task> getAllTasks() {
        return taskMapper.findAll();
    }

    public Task getTaskById(Long id) {
        return taskMapper.findById(id);
    }

    public boolean updateTaskStatus(Long id, String status) {
        Task task = taskMapper.findById(id);

        if (task == null) {
            return false;
        }

        if (status == null || status.isBlank()) {
            return false;
        }

        int rows = taskMapper.updateStatus(id, status, LocalDateTime.now());

        return rows > 0;
    }

    public boolean deleteTask(Long id) {
        int rows = taskMapper.deleteById(id);

        return rows > 0;
    }
}
