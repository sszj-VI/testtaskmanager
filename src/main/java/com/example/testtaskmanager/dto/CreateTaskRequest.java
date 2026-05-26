package com.example.testtaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题长度不能超过100个字符")
    private String title;

    @Size(max = 500, message = "任务描述长度不能超过500个字符")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
