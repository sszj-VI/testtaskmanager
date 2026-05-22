# testtaskmanager

## 项目简介

这是一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目，用于练习 Java 后端开发中的接口设计、数据库 CRUD、分层架构、统一返回结构、参数校验和接口测试。

当前项目已经实现任务的新增、查询、根据 ID 查询、修改任务状态和删除功能，并在此基础上增加了统一接口返回结构、基础参数校验和全局异常处理。

## 技术栈

- Java 17
- Spring Boot
- Spring Boot Validation
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
- 统一接口返回结构
- 新增任务参数校验
- 修改任务状态参数校验
- 全局异常处理
- 统一错误响应格式

## 接口列表

| 方法 | 路径 | 功能 |
|---|---|---|
| GET | `/hello` | 测试 Spring Boot 是否正常运行 |
| POST | `/tasks` | 新增任务 |
| GET | `/tasks` | 查询任务列表 |
| GET | `/tasks/{id}` | 根据 ID 查询任务 |
| PUT | `/tasks/{id}/status` | 修改任务状态 |
| DELETE | `/tasks/{id}` | 删除任务 |

## 统一返回结构

任务相关接口统一返回 `Result` 格式。

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

参数错误响应示例：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

服务器错误响应示例：

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

## 参数校验规则

当前项目已对部分请求参数增加基础校验。

### 新增任务

| 字段 | 校验规则 |
|---|---|
| `title` | 不能为空，长度不能超过 100 个字符 |
| `description` | 长度不能超过 500 个字符 |

### 修改任务状态

| 字段 | 校验规则 |
|---|---|
| `status` | 不能为空，只能是 `TODO`、`DOING`、`DONE` |

## 项目结构

```text
src/main/java/com/example/testtaskmanager
├── common
│   └── Result.java
├── controller
│   ├── HelloController.java
│   └── TaskController.java
├── dto
│   └── UpdateTaskStatusRequest.java
├── entity
│   └── Task.java
├── exception
│   └── GlobalExceptionHandler.java
├── mapper
│   └── TaskMapper.java
├── service
│   └── TaskService.java
└── TesttaskmanagerApplication.java
```

## 分层说明

- `common`：通用类，例如统一返回结果 `Result`
- `controller`：接收 HTTP 请求，调用 Service，并返回响应结果
- `dto`：请求数据传输对象，例如 `UpdateTaskStatusRequest`
- `entity`：表示任务对象，对应数据库中的 `task` 表
- `exception`：全局异常处理，例如 `GlobalExceptionHandler`
- `mapper`：通过 MyBatis 执行 SQL，操作 MySQL 数据库
- `service`：处理业务逻辑，例如新增任务、查询任务、修改状态和删除任务

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
src/test/resources/api-tests/
```

当前主要测试文件包括：

```text
api-test-3.http：CRUD 与统一返回结构测试
api-test-4.http：参数校验与错误响应测试
```

接口响应示例文件位于：

```text
docs/api-responses/
```

当前主要响应目录包括：

```text
docs/api-responses/crud/
docs/api-responses/validation/
```

## 当前版本说明

当前版本已经完成 Spring Boot + MyBatis + MySQL 数据库版 CRUD，并新增统一接口返回结构、基础参数校验和全局异常处理。

请求处理链路为：

```text
HTTP 请求
→ Controller
→ 参数校验
→ Service
→ Mapper
→ MySQL
→ 返回统一 Result 响应
```

参数校验失败时，请求不会继续进入业务逻辑，而是由全局异常处理器捕获异常，并返回统一错误响应。

## 下一步计划

下一阶段暂时不急着增加复杂功能，而是继续提升项目规范性：

- 任务状态枚举
- 查询不存在 ID 时的业务异常处理
- 分页查询
- 按状态筛选
- 更完善的错误码设计
