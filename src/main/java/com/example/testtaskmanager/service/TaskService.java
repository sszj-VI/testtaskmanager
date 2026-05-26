package com.example.testtaskmanager.service;

import com.example.testtaskmanager.common.PageResult;
import com.example.testtaskmanager.dto.CreateTaskRequest;
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

    public Task createTask(CreateTaskRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO.name());
        task.setCreatedTime(now);
        task.setUpdatedTime(now);

        taskMapper.insert(task);

        return task;
    }
    @Deprecated
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
    
    public PageResult<Task> getTaskPage(Integer page, Integer pageSize, String status) {
        if (page == null || page < 1) {
            throw new BusinessException(400, "页码必须大于等于1");
        }

        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new BusinessException(400, "每页数量必须在1到100之间");
        }

        String queryStatus = null;

        if (status != null && !status.isBlank()) {
            queryStatus = status.trim();

            if (!TaskStatus.isValid(queryStatus)) {
                throw new BusinessException(400, "任务状态只能是 TODO、DOING 或 DONE");
            }
        }

        int offset = (page - 1) * pageSize;

        List<Task> records = taskMapper.findPage(queryStatus, offset, pageSize);
        Long total = taskMapper.count(queryStatus);

        return new PageResult<>(page, pageSize, total, records);
    }

}
