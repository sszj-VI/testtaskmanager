package com.example.testtaskmanager.controller;


import com.example.testtaskmanager.common.Result;
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
    public Result<Task> createTask(@RequestBody Task task) {
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

        //getTaskById 可能查不到
        return Result.success(task);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        boolean success = taskService.updateTaskStatus(id, status);

        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTask(@PathVariable Long id) {
        boolean success = taskService.deleteTask(id);

        return Result.success(success);
    }
}

