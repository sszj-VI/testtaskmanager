package com.example.testtaskmanager.entity;

import java.time.LocalDateTime;

public class Task {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdTime;

    public Task() {
    }

    public Task(Long id, String title, String description, String status, LocalDateTime createdTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdTime = createdTime;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
