package com.example.testtaskmanager.controller;


import com.example.testtaskmanager.common.PageResult;
import com.example.testtaskmanager.common.Result;
import com.example.testtaskmanager.dto.CreateTaskRequest;
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
    public Result<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task createdTask = taskService.createTask(request);
        return Result.success(createdTask);
    }


    @GetMapping
    public Result<PageResult<Task>> getTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status
    ) {
        PageResult<Task> result = taskService.getTaskPage(page, pageSize, status);
        return Result.success(result);
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

