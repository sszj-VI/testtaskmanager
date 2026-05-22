package com.example.testtaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateTaskStatusRequest {
    @NotBlank(message = "任务状态不能为空")
    @Pattern(regexp = "TODO|DOING|DONE", message = "任务状态只能是 TODO、DOING 或 DONE")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
