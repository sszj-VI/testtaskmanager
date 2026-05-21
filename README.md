# task-manager-backend

## 项目简介

这是一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目，用于练习 Java 后端开发中的接口设计、数据库 CRUD、分层架构和接口测试。

当前项目已经实现任务的新增、查询、修改状态和删除功能，并通过 IntelliJ IDEA HTTP Client 完成接口测试。

## 技术栈

- Java 17
- Spring Boot
- MyBatis
- MySQL
- Maven
- IntelliJ IDEA HTTP Client

## 已实现功能

- 新增任务
- 查询任务列表
- 根据 ID 查询任务
- 修改任务状态
- 删除任务

## 接口列表

| 方法 | 路径 | 功能 |
|---|---|---|
| GET | /hello | 测试 Spring Boot 是否正常运行 |
| POST | /tasks | 新增任务 |
| GET | /tasks | 查询任务列表 |
| GET | /tasks/{id} | 根据 ID 查询任务 |
| PUT | /tasks/{id}/status | 修改任务状态 |
| DELETE | /tasks/{id} | 删除任务 |

## 项目结构

```text
src/main/java/com/example/testtaskmanager
├── controller
│   ├── HelloController.java
│   └── TaskController.java
├── entity
│   └── Task.java
├── mapper
│   └── TaskMapper.java
├── service
│   └── TaskService.java
└── TesttaskmanagerApplication.java
 