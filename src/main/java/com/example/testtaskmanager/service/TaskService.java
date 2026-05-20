package com.example.testtaskmanager.service;

import com.example.testtaskmanager.entity.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private Long nextId = 1L;

    public Task createTask(Task task) {
        task.setId(nextId++);
        task.setStatus("TODO");
        task.setCreatedTime(LocalDateTime.now());
        tasks.add(task);
        return task;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task getTaskById(Long id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    public boolean updateTaskStatus(Long id, String status) {
        Task task = getTaskById(id);

        if (task == null) {
            return false;
        }

        if (status == null || status.isBlank()) {
            return false;
        }

        task.setStatus(status);
        return true;
    }

    public boolean deleteTask(Long id) {
        return tasks.removeIf(task -> task.getId().equals(id));
    }
}
