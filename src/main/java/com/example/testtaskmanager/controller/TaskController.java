package com.example.testtaskmanager.controller;


import com.example.testtaskmanager.common.Result;
import com.example.testtaskmanager.dto.UpdateTaskStatusRequest;
import com.example.testtaskmanager.entity.Task;
import com.example.testtaskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Result<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return Result.success(createdTask);
    }

    @GetMapping
    public Result<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return Result.success(tasks);
    }

    @GetMapping("/{id}")
    public Result<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return Result.success(task);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        boolean success = taskService.updateTaskStatus(id, request.getStatus());
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTask(@PathVariable Long id) {
        boolean success = taskService.deleteTask(id);

        return Result.success(success);
    }
}

