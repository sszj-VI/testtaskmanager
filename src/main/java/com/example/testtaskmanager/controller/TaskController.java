package com.example.testtaskmanager.controller;


import com.example.testtaskmanager.entity.Task;
import com.example.testtaskmanager.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Object getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);

        if (task == null) {
            return Map.of("message", "task not found");
        }

        return task;
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        boolean success = taskService.updateTaskStatus(id, status);

        return Map.of("success", success);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTask(@PathVariable Long id) {
        boolean success = taskService.deleteTask(id);

        return Map.of("success", success);
    }
}

