# 项目开发记录

## 2026-05-21：MySQL 数据库版 CRUD 整理

### 本阶段目标

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
- 整理接口测试文件
- 更新项目 README 文档

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

---

## 2026-05-21：统一接口返回结构

### 本阶段目标

将任务管理系统中不同接口的返回格式统一为 `Result`，提升接口规范性。

### 已完成内容

- 新增 `common.Result` 通用返回类
- 将 `POST /tasks` 返回值改为 `Result<Task>`
- 将 `GET /tasks` 返回值改为 `Result<List<Task>>`
- 将 `GET /tasks/{id}` 返回值改为 `Result<Task>`
- 将 `PUT /tasks/{id}/status` 返回值改为 `Result<Boolean>`
- 将 `DELETE /tasks/{id}` 返回值改为 `Result<Boolean>`
- 使用 HTTP Client 重新测试核心接口
- 保存 CRUD 接口响应结果

### 统一后的响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 本阶段理解

统一返回结构可以让不同接口的响应格式保持一致，方便前端统一处理，也让后端接口更规范。

原来不同接口可能分别返回对象、列表或 Map。统一之后，所有任务相关接口都返回 `Result`，真实数据放在 `data` 字段中。

### 当前项目价值

通过统一返回结构，项目不再只是“接口能跑”，而是开始具备基础接口规范。

统一返回结构让后续参数校验、业务异常和系统异常都可以用类似格式返回，为后续全局异常处理打下基础。

---

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

### 当时仍然存在的不足

在本阶段完成时，项目仍然存在以下不足：

- 查询不存在的任务 ID 时，业务语义还不够清晰
- 修改或删除不存在的任务时，主要返回 `false`
- 任务状态虽然已经在请求层做校验，但还没有抽成枚举
- 错误码设计还比较简单
- 查询列表还没有分页
- 查询列表还不能按状态筛选

这些问题中的“任务状态枚举”和“任务不存在时的业务异常处理”已在下一阶段解决。

---

## 2026-05-22：任务状态枚举与业务异常处理

### 本阶段目标

使用枚举管理任务状态，并在查询、修改、删除不存在任务时返回明确的业务错误。

### 已完成内容

- 新增 `TaskStatus` 枚举
- 新增 `BusinessException` 业务异常类
- 在 `GlobalExceptionHandler` 中统一处理 `BusinessException`
- 在 `TaskService` 中判断任务是否存在
- 查询不存在任务时返回 404
- 修改不存在任务状态时返回 404
- 删除不存在任务时返回 404
- 正常 CRUD 回归测试通过
- 新增业务异常响应结果文件

### 本阶段新增和修改的关键文件

#### `TaskStatus.java`

新增任务状态枚举：

```java
package com.example.testtaskmanager.enums;

public enum TaskStatus {

    TODO,
    DOING,
    DONE

}
```

该枚举用于集中管理任务状态，避免在代码中直接散落字符串。

例如新增任务时，默认状态可以写成：

```java
task.setStatus(TaskStatus.TODO.name());
```

这样比直接写 `"TODO"` 更清晰，也更不容易写错。

#### `BusinessException.java`

新增业务异常类：

```java
package com.example.testtaskmanager.exception;

public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
```

该类用于表示业务层面的错误，例如任务不存在。

它和系统异常不同。任务不存在不是代码崩溃，而是业务请求对应的资源不存在。

#### `GlobalExceptionHandler.java`

新增对 `BusinessException` 的处理：

```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
    return ResponseEntity
            .status(e.getCode())
            .body(Result.error(e.getCode(), e.getMessage()));
}
```

当 Service 层抛出：

```java
throw new BusinessException(404, "任务不存在");
```

全局异常处理器会捕获该异常，并返回：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

#### `TaskService.java`

在查询、修改、删除任务时增加任务是否存在的判断。

查询任务：

```java
public Task getTaskById(Long id) {
    Task task = taskMapper.findById(id);

    if (task == null) {
        throw new BusinessException(404, "任务不存在");
    }

    return task;
}
```

修改任务状态：

```java
public boolean updateTaskStatus(Long id, String status) {
    Task task = taskMapper.findById(id);

    if (task == null) {
        throw new BusinessException(404, "任务不存在");
    }

    int rows = taskMapper.updateStatus(id, status, LocalDateTime.now());

    return rows > 0;
}
```

删除任务：

```java
public boolean deleteTask(Long id) {
    Task task = taskMapper.findById(id);

    if (task == null) {
        throw new BusinessException(404, "任务不存在");
    }

    int rows = taskMapper.deleteById(id);

    return rows > 0;
}
```

### 当前测试结果

#### 1. 查询不存在任务

请求：

```http
GET /tasks/999999
```

预期结果：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

测试结果：通过。

#### 2. 修改不存在任务状态

请求：

```http
PUT /tasks/999999/status
Content-Type: application/json

{
  "status": "DONE"
}
```

预期结果：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

测试结果：通过。

#### 3. 删除不存在任务

请求：

```http
DELETE /tasks/999999
```

预期结果：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

测试结果：通过。

#### 4. 正常 CRUD 回归测试

正常新增、查询、修改状态、删除任务仍然可以正常执行。

测试结果：通过。

### 本阶段理解

参数错误和业务错误是不同的。

参数错误通常表示请求内容本身不合法，例如标题为空、任务状态非法，适合返回 400。

业务错误通常表示请求格式正确，但请求的业务对象不存在或当前状态不允许操作，例如任务不存在，适合返回 404。

本阶段中，Service 层负责判断任务是否存在。如果不存在，就抛出 `BusinessException`。`GlobalExceptionHandler` 捕获该异常，并统一封装成 `Result` 错误响应。

### 本阶段请求链路理解

以查询不存在任务为例：

```text
GET /tasks/999999
→ TaskController.getTaskById
→ TaskService.getTaskById
→ taskMapper.findById
→ 查不到任务，返回 null
→ TaskService 抛出 BusinessException(404, "任务不存在")
→ GlobalExceptionHandler 捕获 BusinessException
→ 返回 HTTP 404 + Result.error(404, "任务不存在")
```

### 当前项目价值

经过本阶段更新后，项目已经可以区分不同类型的错误：

```text
正常请求：200
参数错误：400
业务错误：404
未知系统错误：500
```

这让项目的接口语义更清晰，也更接近真实后端项目的设计方式。

### 当时仍然存在的不足

在本阶段完成时，项目仍然存在以下不足：

- 查询列表还没有分页
- 查询列表还不能按状态筛选
- 错误码设计仍然比较简单
- 业务异常类型还比较少
- 项目还没有登录、权限、部署等更复杂功能

这些问题中的“分页查询”和“按状态筛选”已在下一阶段解决。

---

## 2026-05-22：分页查询与按状态筛选

### 本阶段目标

改造任务列表查询接口，使 `GET /tasks` 不再一次性返回所有任务，而是支持分页查询和按任务状态筛选。

### 已完成内容

- 新增 `PageResult` 分页结果类
- 在 `TaskStatus` 中新增 `isValid` 方法，用于判断状态是否合法
- 在 `TaskMapper` 中新增分页查询方法
- 在 `TaskMapper` 中新增总数统计方法
- 在 `TaskService` 中新增分页查询业务逻辑
- 在 `TaskController` 中改造 `GET /tasks` 接口
- 支持 `page`、`pageSize`、`status` 查询参数
- 支持按 `TODO`、`DOING`、`DONE` 状态筛选
- 支持分页参数校验
- 支持状态筛选参数校验
- 完成分页查询测试
- 完成按状态筛选测试
- 完成正常 CRUD 回归测试

### 本阶段新增和修改的关键文件

#### `PageResult.java`

新增分页结果类：

```java
package com.example.testtaskmanager.common;

import java.util.List;

public class PageResult<T> {

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Integer page, Integer pageSize, Long total, List<T> records) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
```

该类用于统一封装分页查询结果。

字段含义：

- `page`：当前页码
- `pageSize`：每页数量
- `total`：符合条件的总记录数
- `records`：当前页数据列表

分页接口最终返回格式示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "page": 1,
    "pageSize": 10,
    "total": 1,
    "records": []
  }
}
```

#### `TaskStatus.java`

新增状态合法性判断方法：

```java
public static boolean isValid(String status) {
    if (status == null) {
        return false;
    }

    for (TaskStatus taskStatus : TaskStatus.values()) {
        if (taskStatus.name().equals(status)) {
            return true;
        }
    }

    return false;
}
```

该方法用于判断用户传入的 `status` 是否属于合法任务状态。

合法状态包括：

```text
TODO
DOING
DONE
```

#### `TaskMapper.java`

新增分页查询方法和总数统计方法。

分页查询方法：

```java
@Select("""
        <script>
        SELECT id, title, description, status, created_time, updated_time
        FROM task
        <where>
            <if test="status != null and status != ''">
                status = #{status}
            </if>
        </where>
        ORDER BY id DESC
        LIMIT #{pageSize} OFFSET #{offset}
        </script>
        """)
List<Task> findPage(
                @Param("status") String status,
                @Param("offset") Integer offset,
                @Param("pageSize") Integer pageSize
        );
```

总数统计方法：

```java
@Select("""
        <script>
        SELECT COUNT(*)
        FROM task
        <where>
            <if test="status != null and status != ''">
                status = #{status}
            </if>
        </where>
        </script>
        """)
Long count(@Param("status") String status);
```

`findPage` 用于查询当前页数据，`count` 用于查询符合条件的总记录数。

其中：

- `LIMIT` 控制每页返回多少条
- `OFFSET` 控制跳过多少条
- `status` 不为空时按状态筛选
- `status` 为空时查询全部任务

#### `TaskService.java`

新增分页查询业务逻辑：

```java
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
```

该方法主要完成：

1. 校验 `page`
2. 校验 `pageSize`
3. 校验 `status`
4. 计算 `offset`
5. 查询当前页数据
6. 查询总记录数
7. 封装 `PageResult`

分页计算公式：

```text
offset = (page - 1) * pageSize
```

例如：

```text
page = 1, pageSize = 10, offset = 0
page = 2, pageSize = 10, offset = 10
page = 3, pageSize = 10, offset = 20
```

#### `TaskController.java`

改造 `GET /tasks` 接口：

```java
@GetMapping
public Result<PageResult<Task>> getTasks(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String status
) {
    PageResult<Task> result = taskService.getTaskPage(page, pageSize, status);
    return Result.success(result);
}
```

该接口现在支持：

```http
GET /tasks?page=1&pageSize=10
```

以及：

```http
GET /tasks?page=1&pageSize=10&status=TODO
```

### 当前测试结果

#### 1. 分页查询测试

请求：

```http
GET /tasks?page=1&pageSize=10
```

预期结果：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "page": 1,
    "pageSize": 10,
    "total": 1,
    "records": []
  }
}
```

测试结果：通过。

#### 2. 按 TODO 状态筛选

请求：

```http
GET /tasks?page=1&pageSize=10&status=TODO
```

测试结果：通过。

#### 3. 按 DOING 状态筛选

请求：

```http
GET /tasks?page=1&pageSize=10&status=DOING
```

测试结果：通过。

#### 4. 按 DONE 状态筛选

请求：

```http
GET /tasks?page=1&pageSize=10&status=DONE
```

测试结果：通过。

#### 5. 非法 page 测试

请求：

```http
GET /tasks?page=0&pageSize=10
```

预期结果：

```json
{
  "code": 400,
  "message": "页码必须大于等于1",
  "data": null
}
```

测试结果：通过。

#### 6. 非法 pageSize 测试

请求：

```http
GET /tasks?page=1&pageSize=101
```

预期结果：

```json
{
  "code": 400,
  "message": "每页数量必须在1到100之间",
  "data": null
}
```

测试结果：通过。

#### 7. 非法 status 测试

请求：

```http
GET /tasks?page=1&pageSize=10&status=ABC
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

#### 8. 正常 CRUD 回归测试

正常新增、分页查询、根据 ID 查询、修改状态、删除任务仍然可以正常执行。

测试结果：通过。

### 本阶段理解

真实项目中的列表查询通常不会一次性返回所有数据，而是会支持分页、筛选、排序等能力。

本阶段中，`GET /tasks` 从原来的“查询全部任务”升级为“分页查询任务列表”，并且可以根据任务状态进行筛选。

Controller 负责接收 `page`、`pageSize` 和 `status` 参数；Service 负责参数校验、状态校验、offset 计算和结果封装；Mapper 负责执行分页 SQL 和总数统计 SQL。

分页查询和状态筛选让任务列表接口更加接近真实后端项目。

### 本阶段请求链路理解

以分页查询 TODO 任务为例：

```text
GET /tasks?page=1&pageSize=10&status=TODO
→ TaskController.getTasks
→ TaskService.getTaskPage
→ 校验 page、pageSize、status
→ 计算 offset = (page - 1) * pageSize
→ TaskMapper.findPage 查询当前页数据
→ TaskMapper.count 查询总记录数
→ TaskService 封装 PageResult
→ TaskController 封装 Result.success
→ 返回分页 JSON 响应
```

### 当前项目价值

经过本阶段更新后，项目已经具备以下能力：

- 基础 CRUD
- 统一接口返回结构
- 参数校验
- 全局异常处理
- 任务状态枚举
- 业务异常处理
- 分页查询
- 按状态筛选
- HTTP Client 接口测试
- 响应结果文件保存

项目已经从一个简单 CRUD demo，逐步演进成一个更接近真实后端项目的小型任务管理系统。

### 当前仍然存在的不足

- 错误码设计仍然比较简单
- 项目还没有登录和权限控制
- 项目还没有部署
- 还没有使用日志框架记录异常
- 还没有接口文档工具，例如 Swagger / Knife4j
- 还没有针对复杂业务状态流转做限制

### 后续优化方向

下一阶段可以不急着继续堆功能，而是开始转向求职准备：

1. 整理简历项目描述
2. 整理项目亮点
3. 准备项目面试讲稿
4. 梳理 Java / Spring Boot / MyBatis / MySQL 高频问题
5. 根据时间决定是否继续补充部署、日志或接口文档

### 面试表达草稿

我在任务列表查询接口中增加了分页查询和按状态筛选功能。

Controller 通过 `@RequestParam` 接收 `page`、`pageSize` 和 `status` 参数；Service 层负责校验分页参数和状态参数，并根据页码和每页数量计算 `offset`；Mapper 层通过 MyBatis 动态 SQL 实现可选的状态筛选，并使用 `LIMIT` 和 `OFFSET` 实现分页查询。

同时，我封装了 `PageResult`，用于统一返回 `page`、`pageSize`、`total` 和 `records`。这样列表接口不再一次性返回所有数据，而是更符合真实项目中的分页查询设计。

---

## 2026-05-26：DTO 与 Entity 分离整理

### 本阶段目标

本阶段的目标不是继续增加新业务功能，而是对已经完成的任务管理系统后端做一次代码收尾，让接口入参、数据库实体、Service 业务逻辑之间的职责更清晰。

这次整理的重点是：

```text
1. 新增 CreateTaskRequest，用于接收新增任务请求
2. 让 POST /tasks 不再直接使用 Task Entity 作为请求参数
3. 在 Service 层将 CreateTaskRequest 转换为 Task 实体
4. 保持任务创建时的默认状态、创建时间、更新时间仍由后端控制
5. 保留分页查询、状态筛选、参数校验、业务异常等既有功能不被破坏
```

### 为什么要做 DTO 与 Entity 分离

之前新增任务接口直接使用 `Task` 接收请求参数：

```java
public Result<Task> createTask(@Valid @RequestBody Task task) {
    Task createdTask = taskService.createTask(task);
    return Result.success(createdTask);
}
```

这种写法在项目早期可以使用，因为代码简单、功能少，能够快速完成 CRUD。

但是随着项目逐渐规范化，`Task` 实体类已经不仅仅表示前端请求参数，而是对应数据库中的 `task` 表结构。它包含：

```text
id
title
description
status
createdTime
updatedTime
```

而新增任务时，前端真正应该传入的字段只有：

```text
title
description
```

`id`、`status`、`createdTime`、`updatedTime` 都应该由后端或数据库控制，不应该直接暴露给前端作为新增任务接口的请求参数。

因此本阶段新增 `CreateTaskRequest`，专门表示新增任务接口的请求体。

### 新增文件：CreateTaskRequest.java

新增位置：

```text
src/main/java/com/example/testtaskmanager/dto/CreateTaskRequest.java
```

主要内容：

```java
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
```

这样新增任务接口的入参就只包含前端真正需要提交的字段。

### Controller 层调整

`TaskController` 中的新增任务接口从直接接收 `Task`，调整为接收 `CreateTaskRequest`。

期望形式：

```java
@PostMapping
public Result<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
    Task createdTask = taskService.createTask(request);
    return Result.success(createdTask);
}
```

这里需要注意：

```text
@PostMapping 不能漏掉。
```

如果缺少 `@PostMapping`，Spring Boot 就不会把这个方法注册为 `POST /tasks` 接口，新增任务接口会无法正常访问。

### Service 层调整

`TaskService#createTask` 从接收 `Task` 改为接收 `CreateTaskRequest`。

调整后的逻辑是：

```java
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
```

这段代码体现了一个重要分层思想：

```text
Controller 层负责接收请求
DTO 负责承接请求参数
Service 层负责业务处理和对象转换
Entity 负责对应数据库表结构
Mapper 层负责数据库操作
```

### 本阶段理解

DTO 和 Entity 的区别可以这样理解：

```text
DTO：接口层对象，表示这次请求允许用户提交什么数据
Entity：数据库实体对象，表示数据库表中有哪些字段
```

新增任务时，前端只应该提交 `title` 和 `description`。

任务的默认状态应该由后端设置为 `TODO`，创建时间和更新时间也应该由后端生成。

所以使用 `CreateTaskRequest` 后，接口边界更清楚，也避免了前端直接影响数据库实体中的内部字段。

### 当前检查结果

截至本阶段检查，项目已经完成：

```text
1. CreateTaskRequest.java 已存在
2. TaskController 已经引入 CreateTaskRequest
3. TaskService#createTask 已经改为接收 CreateTaskRequest
4. Service 层已经手动创建 Task，并设置默认状态和时间字段
5. 分页查询、状态筛选、参数校验、业务异常等主体功能仍然保留
```

仍需确认或收尾：

```text
1. 确认 TaskController#createTask 上方存在 @PostMapping
2. 确认 POST /tasks 新增任务接口重新测试通过
3. 确认空 title、title 过长等参数校验仍然返回 400
4. 确认 getAllTasks 和 findAll 是否删除，或者明确标记为废弃
5. 确认 README 中 dto 目录已经写入 CreateTaskRequest
```

### 关于 getAllTasks 和 findAll

当前项目已经将 `GET /tasks` 改造成分页查询接口，因此早期全量查询方法：

```java
getAllTasks()
findAll()
```

已经不是主流程必需方法。

可以有两种处理方式：

```text
方案一：直接删除，保持代码干净
方案二：暂时保留，并使用 @Deprecated 标记为废弃方法
```

如果后续确定不再需要全量查询，建议删除这两个方法，避免项目中保留无用代码。

### 本阶段测试建议

完成 DTO 调整后，需要重新运行以下测试：

```text
api-test-3.http：CRUD 回归测试
api-test-4.http：参数校验测试
api-test-6.http：分页查询和状态筛选测试
```

重点确认：

```text
POST /tasks 可以正常新增任务
空标题仍然返回 400
标题过长仍然返回 400
GET /tasks 分页查询仍然正常
status=TODO / DOING / DONE 筛选仍然正常
非法 status 仍然返回 400
查询、修改、删除不存在任务仍然返回 404
```

### 本阶段项目价值

完成这一步后，项目不只是功能能跑，而是在代码结构上更接近真实后端项目。

这部分后续可以在简历或面试中这样表达：

```text
在项目中将请求 DTO 与数据库 Entity 进行拆分，使用 CreateTaskRequest 接收新增任务请求，并在 Service 层转换为 Task 实体，避免接口入参与数据库表结构强耦合，使接口边界更加清晰。
```

### 当前项目整体状态

截至本阶段，项目已经完成的核心能力包括：

```text
Spring Boot 项目搭建
MyBatis 操作 MySQL
任务 CRUD
统一返回结构 Result
参数校验
全局异常处理
业务异常 BusinessException
任务状态枚举 TaskStatus
分页查询 PageResult
按任务状态筛选
DTO 请求参数封装
HTTP Client 接口测试
测试响应结果保存
README 与项目开发记录整理
```

因此，项目已经可以进入简历项目整理阶段。

后续不建议马上盲目加入 Redis、JWT、Docker、微服务等内容，而应该优先完成：

```text
1. README 最终版整理
2. project-notes 保留完整开发记录
3. 简历项目描述 v0.1
4. 1 分钟项目介绍
5. 3 分钟项目介绍
6. 项目常见面试问答
7. Java 后端实习 JD 对照修改
```

