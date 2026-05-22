# 项目开发记录

## 2026-05-21：MySQL 数据库版 CRUD 整理

### 今日目标

整理当前 `testtaskmanager` 项目，确认 Spring Boot + MyBatis + MySQL 数据库版 CRUD 可以正常运行，并补充接口测试文件和项目文档。

### 已完成内容

- 启动 Spring Boot 项目成功
- 确认 `/hello` 接口可以正常访问
- 确认 MySQL 数据库连接正常
- 确认 `task_manager` 数据库存在
- 确认 `task` 表存在
- 使用 HTTP Client 测试 `POST /tasks` 新增任务
- 使用 HTTP Client 测试 `GET /tasks` 查询任务列表
- 使用 HTTP Client 测试 `GET /tasks/{id}` 根据 ID 查询任务
- 使用 HTTP Client 测试 `PUT /tasks/{id}/status` 修改任务状态
- 使用 HTTP Client 测试 `DELETE /tasks/{id}` 删除任务
- 整理 `api-test-3.http`
- 更新 `README.md`

### 当前项目链路理解

当前项目的请求处理流程是：

```text
HTTP 请求进入 Controller
Controller 调用 Service
Service 处理业务逻辑
Service 调用 Mapper
Mapper 通过 MyBatis 执行 SQL
MySQL 返回数据
后端返回 JSON 响应
```

### 当前项目分层理解

#### Controller 层

Controller 负责接收前端或 HTTP Client 发来的请求。

例如：

- 新增任务请求进入 `TaskController`
- 查询任务请求进入 `TaskController`
- 修改任务状态请求进入 `TaskController`
- 删除任务请求进入 `TaskController`

Controller 本身不直接操作数据库，而是调用 Service。

#### Service 层

Service 负责处理业务逻辑。

例如：

- 新增任务时，设置默认任务状态
- 查询任务时，调用 Mapper 获取数据
- 修改任务状态时，调用 Mapper 更新数据库
- 删除任务时，调用 Mapper 删除对应记录

Service 是 Controller 和 Mapper 之间的中间层。

#### Mapper 层

Mapper 负责通过 MyBatis 操作 MySQL 数据库。

例如：

- 执行新增 SQL
- 执行查询 SQL
- 执行更新 SQL
- 执行删除 SQL

Mapper 是 Java 代码和数据库之间的连接点。

#### Entity 层

Entity 表示项目中的数据对象。

当前项目中的核心实体是 `Task`，对应数据库中的 `task` 表。

### 本阶段遇到的问题

#### 1. HTTP 400 问题

问题表现：

修改任务状态接口返回 `400 Bad Request`。

原因分析：

后端接口使用了 `@RequestBody` 接收 JSON 请求体，但一开始请求写成了 URL 参数形式。

错误写法：

```http
PUT http://localhost:8080/tasks/1/status?status=DONE
```

正确写法：

```http
PUT http://localhost:8080/tasks/1/status
Content-Type: application/json

{
  "status": "DONE"
}
```

本次问题说明：后端接口参数接收方式和 HTTP 请求写法必须匹配。

#### 2. HTTP 415 问题

问题表现：

新增任务接口返回 `415 Unsupported Media Type`。

原因分析：

`.http` 文件中，`POST` 请求行和 `Content-Type` 请求头之间多了空行，导致 IntelliJ HTTP Client 没有把 `Content-Type: application/json` 正确识别为请求头。

错误写法：

```http
POST http://localhost:8080/tasks

Content-Type: application/json
```

正确写法：

```http
POST http://localhost:8080/tasks
Content-Type: application/json
```

本次问题说明：`.http` 文件中，请求头必须紧跟在请求行下面；请求头和请求体之间才需要空行。

#### 3. HTTP 500 问题

问题表现：

多个任务接口曾经返回 `500 Internal Server Error`。

排查思路：

- 先测试 `/hello`，确认 Spring Boot 服务本身正常
- 再检查 MySQL 是否启动
- 再检查 `task_manager` 数据库是否存在
- 再检查 `task` 表是否存在
- 再检查 HTTP 请求格式是否正确
- 最后重新运行接口测试

后续结果：

重新确认环境和请求格式后，数据库版 CRUD 已经成功跑通。

本次问题说明：遇到 500 时，不能只看 HTTP Client 的响应码，还要看 Spring Boot 控制台中的红色异常信息，重点关注 `Caused by` 后面的具体报错。

### 当前测试结果

当前已经验证通过的接口包括：

```text
GET /hello：通过
POST /tasks：通过
GET /tasks：通过
GET /tasks/{id}：通过
PUT /tasks/{id}/status：通过
DELETE /tasks/{id}：通过
```

### 当前项目价值

这个项目已经从最初的 Spring Boot demo，升级为一个具备数据库 CRUD 能力的 Java 后端项目雏形。

目前它覆盖了 Java 后端开发中的几个基础能力：

- Spring Boot 接口开发
- REST 风格接口设计
- Controller / Service / Mapper 分层
- MyBatis 操作 MySQL
- MySQL 表结构设计
- HTTP Client 接口测试
- 项目文档整理
- 基础错误排查

### 当前项目不足

当前项目虽然已经完成数据库版 CRUD，但仍然比较基础，主要不足包括：

- 不同接口返回格式还不统一
- 参数缺少校验，例如标题为空时没有明确提示
- 查询不存在 ID 时返回结果还不够规范
- 异常处理还比较粗糙
- 任务状态目前还是字符串，缺少枚举限制
- 查询列表还没有分页
- 查询列表还不能按状态筛选

### 后续优化方向

下一阶段不急着增加复杂功能，而是先提升项目规范性：

1. 统一接口返回结构
2. 增加参数校验
3. 增加全局异常处理
4. 使用枚举管理任务状态
5. 增加分页查询
6. 增加按状态筛选

### 面试表达草稿

这个项目是一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目。

项目采用 Controller、Service、Mapper 分层结构。Controller 负责接收 HTTP 请求，Service 负责处理业务逻辑，Mapper 通过 MyBatis 执行 SQL 操作 MySQL 数据库。

目前项目实现了任务的新增、查询列表、根据 ID 查询、修改任务状态和删除功能，并使用 IntelliJ IDEA HTTP Client 对接口进行了测试。

通过这个项目，我熟悉了 Spring Boot 后端接口开发、MyBatis 数据库操作、REST API 设计、MySQL 表结构设计以及基础的接口调试流程。

在开发过程中，我还遇到并排查了 `400`、`415`、`500` 等问题，进一步理解了请求参数格式、请求头设置、数据库连接和后端异常排查的重要性。

## 2026-05-22：参数校验与全局异常处理

### 本阶段目标

为任务管理系统增加基础参数校验，并通过全局异常处理统一返回错误响应，避免非法参数进入业务逻辑和数据库。

### 已完成内容

- 在 `pom.xml` 中引入 `spring-boot-starter-validation`
- 在 `Task` 实体类中为 `title` 添加非空校验
- 在 `Task` 实体类中为 `title` 添加长度校验
- 在 `Task` 实体类中为 `description` 添加长度校验
- 新增 `UpdateTaskStatusRequest`，用于接收修改任务状态请求
- 使用 `@Pattern` 限制任务状态只能是 `TODO`、`DOING`、`DONE`
- 在 `TaskController` 中使用 `@Valid` 触发参数校验
- 新增 `GlobalExceptionHandler` 全局异常处理类
- 统一处理 `MethodArgumentNotValidException`
- 将参数校验错误封装为统一 `Result` 格式
- 保留兜底异常处理，避免直接返回 Spring Boot 默认错误结构
- 完成参数校验接口测试
- 完成正常 CRUD 回归测试

### 本阶段新增和修改的关键文件

#### `pom.xml`

新增 Validation 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

该依赖用于支持 `@Valid`、`@NotBlank`、`@Size`、`@Pattern` 等参数校验注解。

#### `Task.java`

为任务标题和描述增加基础校验规则：

```java
@NotBlank(message = "任务标题不能为空")
@Size(max = 100, message = "任务标题长度不能超过100个字符")
private String title;

@Size(max = 500, message = "任务描述长度不能超过500个字符")
private String description;
```

当前规则含义：

- `title` 不能为空
- `title` 长度不能超过 100 个字符
- `description` 长度不能超过 500 个字符

#### `UpdateTaskStatusRequest.java`

新增 DTO 类，用于接收修改任务状态请求。

```java
@NotBlank(message = "任务状态不能为空")
@Pattern(regexp = "TODO|DOING|DONE", message = "任务状态只能是 TODO、DOING 或 DONE")
private String status;
```

当前规则含义：

- `status` 不能为空
- `status` 只能是 `TODO`、`DOING`、`DONE`

#### `Result.java`

在统一返回类中增加错误返回方法：

```java
public static <T> Result<T> error(Integer code, String message) {
    return new Result<>(code, message, null);
}
```

这样成功响应和错误响应都可以使用统一格式。

#### `GlobalExceptionHandler.java`

新增全局异常处理类，用于统一处理 Controller 层抛出的异常。

当前主要处理两类异常：

1. 参数校验异常：返回 `400`
2. 其他未知异常：返回 `500`

参数校验失败时返回示例：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

未知异常时返回示例：

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

#### `TaskController.java`

在请求体参数前增加 `@Valid`，触发参数校验。

新增任务接口：

```java
@PostMapping
public Result<Task> createTask(@Valid @RequestBody Task task) {
    Task createdTask = taskService.createTask(task);
    return Result.success(createdTask);
}
```

修改任务状态接口：

```java
@PutMapping("/{id}/status")
public Result<Boolean> updateTaskStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTaskStatusRequest request
) {
    boolean success = taskService.updateTaskStatus(id, request.getStatus());
    return Result.success(success);
}
```

### 当前测试结果

#### 1. 空标题测试

请求：

```json
{
  "title": "",
  "description": "测试标题为空"
}
```

预期结果：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

测试结果：通过。

#### 2. 标题过长测试

请求中 `title` 超过 100 个字符。

预期结果：

```json
{
  "code": 400,
  "message": "任务标题长度不能超过100个字符",
  "data": null
}
```

测试结果：通过。

#### 3. 非法状态测试

请求：

```json
{
  "status": "ABC"
}
```

预期结果：

```json
{
  "code": 400,
  "message": "任务状态只能是 TODO、DOING 或 DONE",
  "data": null
}
```

测试结果：通过。

#### 4. 正常 CRUD 回归测试

正常新增、查询、修改状态、删除任务仍然可以正常执行。

测试结果：通过。

### 本阶段理解

参数校验的作用是阻止明显非法的数据进入业务逻辑和数据库。

本阶段中，`Task` 和 `UpdateTaskStatusRequest` 负责声明校验规则，`TaskController` 中的 `@Valid` 负责触发校验，`GlobalExceptionHandler` 负责捕获校验异常并统一返回错误结果。

这让项目从“只处理正常请求”提升到“可以处理错误请求”，接口表现更接近真实后端项目。

### 本阶段遇到和解决的问题

#### 1. 为什么只写注解还不够？

只在字段上写 `@NotBlank`、`@Size`、`@Pattern` 只是声明规则。

如果 Controller 中没有使用 `@Valid`，这些规则不会自动生效。

所以需要这样写：

```java
public Result<Task> createTask(@Valid @RequestBody Task task)
```

#### 2. 为什么要新增 DTO，而不是继续用 Map？

之前修改任务状态使用 `Map<String, String>` 接收请求，虽然能拿到 `status`，但不方便声明校验规则。

新增 `UpdateTaskStatusRequest` 后，可以直接在 `status` 字段上使用 `@NotBlank` 和 `@Pattern`，代码更清晰，也更符合真实项目写法。

#### 3. 为什么要有全局异常处理？

如果没有全局异常处理，参数错误时 Spring Boot 会返回默认错误格式，例如：

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "path": "/tasks"
}
```

这和项目自己的 `Result` 返回结构不一致。

加入 `GlobalExceptionHandler` 后，可以把错误响应也统一成：

```json
{
  "code": 400,
  "message": "具体错误信息",
  "data": null
}
```

### 当前项目价值

经过本阶段更新后，项目已经具备以下能力：

- 基础 CRUD
- MyBatis 操作 MySQL
- 统一成功响应
- 参数校验
- 统一错误响应
- 初步全局异常处理
- HTTP Client 接口测试
- 响应结果文件保存

这说明项目已经不只是“能跑”，而是开始具备基本接口规范。

### 当前仍然存在的不足

- 查询不存在的任务 ID 时，业务语义还不够清晰
- 修改或删除不存在的任务时，目前主要返回 `false`
- 任务状态虽然已经在请求层做校验，但还没有抽成枚举
- 错误码设计还比较简单
- 查询列表还没有分页
- 查询列表还不能按状态筛选

### 后续优化方向

下一阶段可以继续优化：

1. 任务状态枚举
2. 查询不存在 ID 时的业务异常处理
3. 修改和删除不存在任务时的错误提示
4. 分页查询
5. 按状态筛选
6. 更完善的错误码设计

### 面试表达草稿

我在项目中引入了 `spring-boot-starter-validation`，使用 `@NotBlank`、`@Size`、`@Pattern` 等注解对请求参数进行基础校验。

例如新增任务时，任务标题不能为空且长度不能超过 100 个字符；修改任务状态时，只允许传入 `TODO`、`DOING`、`DONE` 三种状态。

同时，我新增了 `GlobalExceptionHandler` 全局异常处理类，使用 `@RestControllerAdvice` 统一捕获参数校验异常，并将错误信息封装成统一的 `Result` 返回结构。

通过这次更新，项目不仅能处理正常请求，也能对错误请求返回清晰、统一的错误信息，避免直接暴露 Spring Boot 默认错误格式。

