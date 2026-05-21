# testtaskmanager

## 项目简介

这是一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目，用于练习 Java 后端开发中的接口设计、数据库 CRUD、分层架构和接口测试。

当前项目已经实现任务的新增、查询、根据 ID 查询、修改任务状态和删除功能，并使用 IntelliJ IDEA HTTP Client 完成了接口测试。

## 技术栈

- Java 17
- Spring Boot
- MyBatis
- MySQL
- Maven
- IntelliJ IDEA HTTP Client

## 已实现功能

- 测试 Spring Boot 服务是否正常运行
- 新增任务
- 查询任务列表
- 根据 ID 查询任务
- 修改任务状态
- 删除任务

## 接口列表

| 方法 | 路径 | 功能 |
|---|---|---|
| GET | `/hello` | 测试 Spring Boot 是否正常运行 |
| POST | `/tasks` | 新增任务 |
| GET | `/tasks` | 查询任务列表 |
| GET | `/tasks/{id}` | 根据 ID 查询任务 |
| PUT | `/tasks/{id}/status` | 修改任务状态 |
| DELETE | `/tasks/{id}` | 删除任务 |

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
```

## 分层说明

- `controller`：接收 HTTP 请求，调用 Service，并返回响应结果
- `service`：处理业务逻辑，例如新增任务、查询任务、修改状态和删除任务
- `mapper`：通过 MyBatis 执行 SQL，操作 MySQL 数据库
- `entity`：表示任务对象，对应数据库中的 `task` 表

## 数据库说明

当前项目使用 MySQL 数据库。

数据库名：

```text
task_manager
```

主要数据表：

```text
task
```

`task` 表主要字段如下：

| 字段 | 说明 |
|---|---|
| `id` | 任务 ID，主键，自增 |
| `title` | 任务标题 |
| `description` | 任务描述 |
| `status` | 任务状态 |
| `created_time` | 创建时间 |
| `updated_time` | 更新时间 |

数据库初始化脚本位于：

```text
docs/sql/init.sql
```

## 接口测试

接口测试文件位于：

```text
src/test/resources/api-test-3.http
```

当前已经验证通过的接口包括：

- `GET /hello`
- `POST /tasks`
- `GET /tasks`
- `GET /tasks/{id}`
- `PUT /tasks/{id}/status`
- `DELETE /tasks/{id}`

测试过程中曾遇到过 `400`、`415`、`500` 等问题，目前已完成排查，并成功跑通数据库版 CRUD。

## 当前版本说明

当前版本已经完成 Spring Boot + MyBatis + MySQL 数据库版 CRUD。

请求处理链路为：

```text
HTTP 请求
→ Controller
→ Service
→ Mapper
→ MySQL
→ 返回 JSON 响应
```

通过这个阶段，项目已经从最初的 Spring Boot demo，升级为一个具备数据库 CRUD 能力的 Java 后端项目雏形。

## 下一步计划

下一阶段暂时不急着增加复杂功能，而是先提升接口规范性和项目完整度：

- 统一接口返回结构
- 参数校验
- 全局异常处理
- 任务状态枚举
- 分页查询
- 按状态筛选
