# testtaskmanager

## 项目简介

`testtaskmanager` 是一个基于 **Spring Boot + MyBatis + MySQL** 的任务管理系统后端项目。

本项目用于练习 Java 后端开发中的接口设计、数据库 CRUD、分层架构、统一返回结构、参数校验、全局异常处理、业务异常处理、枚举状态管理、分页查询、按状态筛选和接口测试。

当前项目已实现任务的新增、分页查询、按状态筛选、根据 ID 查询、修改任务状态、删除任务等核心功能，并通过统一返回结构、DTO 请求对象、参数校验、业务异常和接口测试记录，逐步从基础 CRUD 项目整理为一个较完整的 Java 后端练习项目。

---

## 技术栈

- Java 17
- Spring Boot
- Spring Boot Validation
- MyBatis
- MySQL
- Maven
- IntelliJ IDEA HTTP Client
- Git

---

## 项目定位

本项目不是复杂业务系统，而是一个用于求职准备的 Java 后端基础项目。

项目重点不在业务规模，而在于完整实践以下后端开发流程：

```text
接口设计
Controller / Service / Mapper 分层
DTO 与 Entity 区分
MyBatis 操作 MySQL
统一返回结构
参数校验
全局异常处理
业务异常处理
分页查询
状态筛选
接口测试与响应结果保存
项目文档整理
```

---

## 已实现功能

### 基础功能

- 测试 Spring Boot 服务是否正常运行
- 新增任务
- 分页查询任务列表
- 按任务状态筛选任务列表
- 根据 ID 查询任务
- 修改任务状态
- 删除任务

### 接口规范

- 使用统一返回结构 `Result`
- 使用分页返回结构 `PageResult`
- 成功响应统一包含 `code`、`message`、`data`
- 参数错误统一返回 400
- 业务异常统一返回对应业务错误码
- 系统异常统一返回 500

### 参数校验

- 新增任务标题不能为空
- 新增任务标题长度不能超过 100 个字符
- 新增任务描述长度不能超过 500 个字符
- 修改任务状态不能为空
- 修改任务状态只能是 `TODO`、`DOING`、`DONE`
- 分页页码必须大于等于 1
- 每页数量必须在 1 到 100 之间
- 状态筛选参数只能是 `TODO`、`DOING`、`DONE`

### 业务异常

- 查询不存在任务时返回 404
- 修改不存在任务状态时返回 404
- 删除不存在任务时返回 404

### 代码整理

- 使用 `CreateTaskRequest` 接收新增任务请求参数
- 使用 `UpdateTaskStatusRequest` 接收修改任务状态请求参数
- 将接口请求参数 DTO 与数据库实体 `Task` 区分
- 使用 `TaskStatus` 枚举统一维护任务状态
- 使用 `BusinessException` 表示业务异常
- 使用 `GlobalExceptionHandler` 统一处理异常

---

## 接口列表

| 方法 | 路径 | 功能 |
|---|---|---|
| GET | `/hello` | 测试 Spring Boot 服务是否正常运行 |
| POST | `/tasks` | 新增任务 |
| GET | `/tasks` | 分页查询任务列表，可按状态筛选 |
| GET | `/tasks/{id}` | 根据 ID 查询任务 |
| PUT | `/tasks/{id}/status` | 修改任务状态 |
| DELETE | `/tasks/{id}` | 删除任务 |

---

## 接口说明

### 1. 测试服务

```http
GET /hello
```

作用：确认 Spring Boot 服务是否正常启动。

---

### 2. 新增任务

```http
POST /tasks
Content-Type: application/json
```

请求体：

```json
{
  "title": "学习 Spring Boot",
  "description": "完成任务管理系统后端项目"
}
```

说明：

- 新增任务接口使用 `CreateTaskRequest` 接收请求参数
- 前端只需要传入 `title` 和 `description`
- `id`、`status`、`createdTime`、`updatedTime` 由后端负责维护
- 新增任务的默认状态为 `TODO`

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "学习 Spring Boot",
    "description": "完成任务管理系统后端项目",
    "status": "TODO",
    "createdTime": "2026-05-22T10:00:00",
    "updatedTime": "2026-05-22T10:00:00"
  }
}
```

---

### 3. 分页查询任务列表

```http
GET /tasks?page=1&pageSize=10
```

查询参数：

| 参数 | 是否必填 | 说明 |
|---|---|---|
| `page` | 否 | 页码，默认值为 1 |
| `pageSize` | 否 | 每页数量，默认值为 10 |

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "page": 1,
    "pageSize": 10,
    "total": 1,
    "records": [
      {
        "id": 1,
        "title": "学习 Spring Boot",
        "description": "完成任务管理系统后端项目",
        "status": "TODO",
        "createdTime": "2026-05-22T10:00:00",
        "updatedTime": "2026-05-22T10:00:00"
      }
    ]
  }
}
```

---

### 4. 按状态筛选任务列表

```http
GET /tasks?page=1&pageSize=10&status=TODO
```

当前支持的任务状态：

```text
TODO
DOING
DONE
```

说明：

- `status` 是可选参数
- 不传 `status` 时，查询全部任务
- 传入 `TODO`、`DOING`、`DONE` 时，只查询对应状态的任务
- 传入非法状态时，返回 400

---

### 5. 根据 ID 查询任务

```http
GET /tasks/{id}
```

示例：

```http
GET /tasks/1
```

如果任务存在，返回对应任务信息。

如果任务不存在，返回：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

---

### 6. 修改任务状态

```http
PUT /tasks/{id}/status
Content-Type: application/json
```

请求体：

```json
{
  "status": "DONE"
}
```

说明：

- 修改任务状态接口使用 `UpdateTaskStatusRequest` 接收请求参数
- `status` 不能为空
- `status` 只能是 `TODO`、`DOING`、`DONE`

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 7. 删除任务

```http
DELETE /tasks/{id}
```

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

如果任务不存在，返回：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

---

## 统一返回结构

任务相关接口统一返回 `Result<T>` 格式。

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

字段说明：

| 字段 | 说明 |
|---|---|
| `code` | 业务状态码 |
| `message` | 响应信息 |
| `data` | 具体响应数据 |

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 参数错误响应

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

### 业务错误响应

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

### 服务器错误响应

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

---

## 参数校验规则

### 新增任务

| 字段 | 校验规则 |
|---|---|
| `title` | 不能为空，长度不能超过 100 个字符 |
| `description` | 长度不能超过 500 个字符 |

### 修改任务状态

| 字段 | 校验规则 |
|---|---|
| `status` | 不能为空，只能是 `TODO`、`DOING`、`DONE` |

### 分页查询

| 参数 | 校验规则 |
|---|---|
| `page` | 页码必须大于等于 1 |
| `pageSize` | 每页数量必须在 1 到 100 之间 |
| `status` | 可选；如果传入，只能是 `TODO`、`DOING`、`DONE` |

---

## 异常处理设计

当前项目使用 `GlobalExceptionHandler` 统一处理异常。

| 异常类型 | 处理方式 |
|---|---|
| `MethodArgumentNotValidException` | 参数校验失败，返回 400 |
| `BusinessException` | 业务异常，返回异常中定义的 code |
| `Exception` | 未预期系统异常，返回 500 |

### 业务异常

当前项目使用 `BusinessException` 表示业务异常。

典型场景：

| 场景 | 返回结果 |
|---|---|
| 查询不存在的任务 | HTTP 404，`message = 任务不存在` |
| 修改不存在任务的状态 | HTTP 404，`message = 任务不存在` |
| 删除不存在的任务 | HTTP 404，`message = 任务不存在` |

---

## 项目结构

```text
src/main/java/com/example/testtaskmanager
├── common
│   ├── PageResult.java
│   └── Result.java
├── controller
│   ├── HelloController.java
│   └── TaskController.java
├── dto
│   ├── CreateTaskRequest.java
│   └── UpdateTaskStatusRequest.java
├── entity
│   └── Task.java
├── enums
│   └── TaskStatus.java
├── exception
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── mapper
│   └── TaskMapper.java
├── service
│   └── TaskService.java
└── TesttaskmanagerApplication.java
```

---

## 分层说明

| 包名 | 作用 |
|---|---|
| `common` | 通用类，例如统一返回结果 `Result` 和分页结果 `PageResult` |
| `controller` | 接收 HTTP 请求，调用 Service，并返回响应结果 |
| `dto` | 请求数据传输对象，用于接收前端请求参数 |
| `entity` | 数据库实体对象，对应数据库中的 `task` 表 |
| `enums` | 枚举类，例如任务状态枚举 `TaskStatus` |
| `exception` | 异常处理相关类，例如 `BusinessException` 和 `GlobalExceptionHandler` |
| `mapper` | 通过 MyBatis 执行 SQL，操作 MySQL 数据库 |
| `service` | 处理业务逻辑，例如新增任务、查询任务、分页筛选、修改状态、删除任务和业务异常判断 |

---

## DTO 与 Entity 的区分

当前项目中：

- `CreateTaskRequest`：用于接收新增任务请求
- `UpdateTaskStatusRequest`：用于接收修改任务状态请求
- `Task`：用于表示数据库中的任务记录

新增任务时，前端只需要传入：

```text
title
description
```

不应该由前端传入：

```text
id
status
createdTime
updatedTime
```

因此使用 `CreateTaskRequest` 接收请求参数，再在 Service 层转换为 `Task` 实体对象。

这样可以让接口请求模型和数据库实体模型解耦，使项目分层更加清晰。

---

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

---

## 本地运行方式

### 1. 创建数据库和数据表

执行：

```text
docs/sql/init.sql
```

### 2. 配置数据库连接

在 `src/main/resources/application.properties` 中配置本地 MySQL 连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_manager?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

注意：如果项目后续上传到公开仓库，不建议提交真实数据库密码。

### 3. 启动项目

可以在 IntelliJ IDEA 中运行：

```text
TesttaskmanagerApplication
```

也可以使用 Maven 启动：

```bash
./mvnw spring-boot:run
```

### 4. 访问测试接口

```http
GET http://localhost:8080/hello
```

---

## 接口测试

接口测试文件位于：

```text
src/test/resources/api-tests/
```

当前主要测试文件包括：

```text
api-test-3.http：CRUD 与统一返回结构测试
api-test-4.http：参数校验与错误响应测试
api-test-5.http：业务异常处理测试
api-test-6.http：分页查询与状态筛选测试
```

接口响应示例文件位于：

```text
docs/api-responses/
```

当前主要响应目录包括：

```text
docs/api-responses/crud/
docs/api-responses/validation/
docs/api-responses/business-exception/
docs/api-responses/page-query/
```

---

## 测试覆盖情况

| 测试内容 | 是否已覆盖 |
|---|---|
| `/hello` 服务测试 | 已覆盖 |
| 新增任务 | 已覆盖 |
| 查询任务列表 | 已覆盖 |
| 根据 ID 查询任务 | 已覆盖 |
| 修改任务状态 | 已覆盖 |
| 删除任务 | 已覆盖 |
| 新增任务参数校验 | 已覆盖 |
| 修改任务状态参数校验 | 已覆盖 |
| 查询不存在任务 | 已覆盖 |
| 修改不存在任务状态 | 已覆盖 |
| 删除不存在任务 | 已覆盖 |
| 分页查询 | 已覆盖 |
| 按状态筛选 | 已覆盖 |
| 非法分页参数 | 已覆盖 |
| 非法状态筛选参数 | 已覆盖 |

---

## 当前项目亮点

- 使用 Spring Boot 搭建 REST 风格后端接口
- 使用 Controller / Service / Mapper 实现基础分层
- 使用 MyBatis 操作 MySQL，实现任务数据持久化
- 使用统一返回结构 `Result` 规范接口响应
- 使用 `PageResult` 封装分页查询结果
- 使用 Validation 实现请求参数校验
- 使用全局异常处理统一错误响应
- 使用自定义业务异常处理任务不存在场景
- 使用任务状态枚举统一管理合法状态
- 使用 DTO 区分请求参数和数据库实体
- 使用 HTTP Client 编写接口测试并保存响应结果
- 使用 README 和 project-notes 记录项目开发过程

---

## 后续可优化方向

当前项目已经可以作为 Java 后端实习简历项目的基础版本。

后续可以按优先级逐步优化：

```text
1. 删除已废弃的 getAllTasks 和 findAll 方法
2. 补充 Swagger / OpenAPI 接口文档
3. 补充 JUnit 单元测试或集成测试
4. 增加登录注册和 JWT 鉴权
5. 增加用户维度的任务管理
6. 增加日志记录
7. 使用 Docker 部署项目
```

当前阶段不建议一次性加入过多新技术，应优先保证项目代码清晰、文档完整、面试能讲清楚。

---

## 简历描述参考

```text
基于 Spring Boot + MyBatis + MySQL 实现任务管理系统后端，支持任务新增、分页查询、按状态筛选、根据 ID 查询、修改状态和删除等接口；设计统一返回结构 Result 和分页结果 PageResult，结合参数校验、全局异常处理和自定义业务异常，规范接口成功与错误响应；使用 DTO 区分请求参数与数据库实体，并通过 IntelliJ HTTP Client 编写接口测试用例，保存典型响应结果用于项目复盘。
```

