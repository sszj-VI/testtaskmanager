package com.example.testtaskmanager.service;

import com.example.testtaskmanager.entity.Task;
import com.example.testtaskmanager.enums.TaskStatus;
import com.example.testtaskmanager.exception.BusinessException;
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

        task.setStatus(TaskStatus.TODO.name());
        task.setCreatedTime(now);
        task.setUpdatedTime(now);

        taskMapper.insert(task);

        return task;
    }

    public List<Task> getAllTasks() {
        return taskMapper.findAll();
    }

    public Task getTaskById(Long id) {
        Task task = taskMapper.findById(id);

        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        return task;
    }

    public boolean updateTaskStatus(Long id, String status) {
        Task task = taskMapper.findById(id);

        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        int rows = taskMapper.updateStatus(id, status, LocalDateTime.now());

        return rows > 0;
    }

    public boolean deleteTask(Long id) {
        Task task = taskMapper.findById(id);

        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        int rows = taskMapper.deleteById(id);

        return rows > 0;
    }
}
