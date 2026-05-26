# taskmanager-面试总结3

## 一、当前阶段总结

截至目前，`testtaskmanager` 项目已经从最初的 Spring Boot 入门接口、内存版任务管理系统、MySQL 数据库版 CRUD，进一步升级为一个更规范的任务管理系统后端项目。

当前已经完成：

- Spring Boot + MyBatis + MySQL 数据库版 CRUD；
- 统一接口返回结构 `Result`；
- 参数校验；
- 全局异常处理；
- 业务异常处理；
- 任务状态枚举；
- 查询不存在任务时返回 404；
- 分页查询任务列表；
- 按任务状态筛选任务列表；
- HTTP Client 接口测试；
- 响应结果文件保存。

当前项目已经不只是“能跑 CRUD”，而是具备了更接近真实后端项目的接口规范。

---

## 二、当前项目可以怎么对外介绍

可以准备一个 1 分钟项目介绍版本：

> 我做了一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目。项目实现了任务的新增、分页查询、按状态筛选、根据 ID 查询、修改状态和删除功能。  
> 项目采用 Controller、Service、Mapper、Entity 分层结构。Controller 负责接收 HTTP 请求，Service 负责业务逻辑处理，Mapper 通过 MyBatis 操作 MySQL 数据库。  
> 在基础 CRUD 之上，我还实现了统一接口返回结构、参数校验、全局异常处理、业务异常处理、任务状态枚举、分页查询和按状态筛选，使接口返回和错误处理更加规范。

---

## 三、当前项目整体请求链路

当前项目的一次请求大致经过下面这条链路：

```text
HTTP Client / 前端
  ↓
Tomcat
  ↓
DispatcherServlet
  ↓
TaskController
  ↓
参数校验
  ↓
TaskService
  ↓
TaskMapper
  ↓
MySQL task 表
  ↓
TaskMapper 返回数据
  ↓
TaskService 封装业务结果
  ↓
TaskController 封装 Result
  ↓
返回 JSON 响应
```

面试表达：

> 当前项目采用典型的 Spring Boot 分层结构。请求进入后先由 Controller 接收，Controller 不直接操作数据库，而是调用 Service。Service 负责业务判断，比如设置默认状态、判断任务是否存在、校验分页参数等。Mapper 负责通过 MyBatis 执行 SQL 操作 MySQL。最后结果会统一封装为 Result 返回给客户端。

---

## 四、统一返回结构 Result

### 1. 为什么要统一返回结构

在项目早期，不同接口可能返回不同格式：

```text
POST /tasks 返回 Task
GET /tasks 返回 List<Task>
PUT /tasks/{id}/status 返回 true / false
DELETE /tasks/{id} 返回 true / false
```

这样虽然能用，但前端处理起来不统一。

因此项目中封装了 `Result`：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 2. Result 的作用

`Result` 用来统一接口响应格式。

成功时：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "测试任务"
  }
}
```

参数错误时：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

业务错误时：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

面试表达：

> 我在项目中封装了统一返回类 Result，使所有任务相关接口都返回 code、message、data 三个字段。这样前端可以用统一方式处理成功和失败响应，也让接口风格更加规范。

---

## 五、参数校验

### 1. 为什么需要参数校验

如果没有参数校验，用户可能提交非法数据，例如：

```json
{
  "title": "",
  "description": "测试"
}
```

或者：

```json
{
  "status": "ABC"
}
```

这些数据不应该直接进入业务逻辑和数据库。

### 2. 使用的校验方式

项目中引入了：

```xml
spring-boot-starter-validation
```

然后使用：

```text
@Valid
@NotBlank
@Size
@Pattern
```

### 3. Task 中的校验

新增任务时，对 `title` 和 `description` 进行校验：

```java
@NotBlank(message = "任务标题不能为空")
@Size(max = 100, message = "任务标题长度不能超过100个字符")
private String title;

@Size(max = 500, message = "任务描述长度不能超过500个字符")
private String description;
```

含义：

```text
title 不能为空
title 长度不能超过 100
description 长度不能超过 500
```

### 4. 修改状态请求的校验

项目中新增了 `UpdateTaskStatusRequest`：

```java
@NotBlank(message = "任务状态不能为空")
@Pattern(regexp = "TODO|DOING|DONE", message = "任务状态只能是 TODO、DOING 或 DONE")
private String status;
```

含义：

```text
status 不能为空
status 只能是 TODO、DOING、DONE
```

### 5. @Valid 的作用

Controller 中使用：

```java
public Result<Task> createTask(@Valid @RequestBody Task task)
```

和：

```java
public Result<Boolean> updateTaskStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTaskStatusRequest request
)
```

`@Valid` 的作用是触发对象中的校验注解。

如果参数不合法，就会抛出 `MethodArgumentNotValidException`，然后由全局异常处理器统一处理。

面试表达：

> 我使用 Spring Validation 对请求参数进行校验。例如新增任务时，标题不能为空且长度不能超过 100；修改任务状态时，只允许传入 TODO、DOING、DONE。Controller 方法中使用 @Valid 触发校验，校验失败后由全局异常处理统一返回 400 错误响应。

---

## 六、全局异常处理 GlobalExceptionHandler

### 1. 为什么需要全局异常处理

如果没有全局异常处理，参数错误或服务器异常时，Spring Boot 可能返回默认错误格式：

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "path": "/tasks"
}
```

这和项目自己的 `Result` 格式不一致。

所以项目中新增了 `GlobalExceptionHandler`。

### 2. @RestControllerAdvice

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

`@RestControllerAdvice` 表示这是一个全局异常处理类，可以统一处理 Controller 层抛出的异常，并返回 JSON。

### 3. 处理参数校验异常

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("参数校验失败");

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Result.error(400, message));
}
```

这段代码的作用是：

```text
1. 捕获参数校验异常；
2. 从异常中取出第一条字段错误信息；
3. 封装成 Result；
4. 返回 HTTP 400。
```

例如标题为空时返回：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

### 4. 处理未知异常

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Result<Void>> handleException(Exception e) {
    e.printStackTrace();

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error(500, "服务器内部错误"));
}
```

作用：

```text
兜底处理其他未知异常
避免直接暴露 Spring Boot 默认错误格式
```

面试表达：

> 我使用 @RestControllerAdvice 编写全局异常处理器，统一处理参数校验异常、业务异常和未知异常。这样可以避免每个 Controller 方法里重复写 try-catch，也能保证错误响应格式统一。

---

## 七、业务异常 BusinessException

### 1. 为什么需要业务异常

参数错误和业务错误不同。

参数错误：

```text
title 为空
status = ABC
page = 0
```

业务错误：

```text
任务不存在
修改不存在的任务
删除不存在的任务
```

任务不存在不是系统崩溃，而是业务请求对应的资源不存在，因此应该返回 404。

### 2. BusinessException

项目中定义了业务异常类：

```java
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

含义：

```text
code：业务错误码
message：错误信息
```

例如：

```java
throw new BusinessException(404, "任务不存在");
```

### 3. 全局处理 BusinessException

```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
    return ResponseEntity
            .status(e.getCode())
            .body(Result.error(e.getCode(), e.getMessage()));
}
```

当 Service 抛出：

```java
throw new BusinessException(404, "任务不存在");
```

最终返回：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

### 4. Service 中的使用

例如查询任务：

```java
public Task getTaskById(Long id) {
    Task task = taskMapper.findById(id);

    if (task == null) {
        throw new BusinessException(404, "任务不存在");
    }

    return task;
}
```

含义：

```text
先查询数据库
如果查不到，抛出业务异常
如果查到，正常返回任务
```

面试表达：

> 我自定义了 BusinessException 表示业务异常。在 Service 层判断任务是否存在，如果不存在，就抛出 BusinessException。然后由 GlobalExceptionHandler 统一捕获并返回 404。这比直接返回 null 或 false 更符合接口语义。

---

## 八、任务状态枚举 TaskStatus

### 1. 为什么要用枚举

之前状态可能直接写字符串：

```java
task.setStatus("TODO");
```

这样容易写错，例如：

```text
TOOD
Done
done
```

所以项目中定义了 `TaskStatus` 枚举：

```java
public enum TaskStatus {

    TODO,
    DOING,
    DONE

}
```

### 2. 默认状态

新增任务时：

```java
task.setStatus(TaskStatus.TODO.name());
```

表示新任务默认状态为 `TODO`。

### 3. 状态合法性判断

后来为了支持按状态筛选，给 `TaskStatus` 增加了：

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

作用：

```text
判断传入的 status 是否是 TODO、DOING、DONE 之一
```

面试表达：

> 我使用枚举集中管理任务状态，避免在代码中散落字符串。同时在分页筛选接口中，通过 TaskStatus.isValid 判断传入状态是否合法。

---

## 九、分页查询与按状态筛选

### 1. 为什么需要分页查询

早期 `GET /tasks` 是一次性查询所有任务。

如果数据很多，这样会有问题：

```text
接口响应慢
数据库压力大
前端无法分页展示
```

所以项目中将 `GET /tasks` 改造成分页查询接口。

### 2. 当前接口形式

分页查询：

```http
GET /tasks?page=1&pageSize=10
```

按状态筛选：

```http
GET /tasks?page=1&pageSize=10&status=TODO
```

### 3. PageResult

项目中新增了 `PageResult`：

```java
public class PageResult<T> {

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> records;
}
```

字段含义：

```text
page：当前页
pageSize：每页数量
total：符合条件的总记录数
records：当前页数据
```

返回示例：

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
        "title": "测试任务",
        "status": "TODO"
      }
    ]
  }
}
```

### 4. Controller 层

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

Controller 负责：

```text
接收 page
接收 pageSize
接收 status
调用 Service
返回 Result
```

### 5. Service 层

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

Service 负责：

```text
校验 page
校验 pageSize
校验 status
计算 offset
调用 Mapper 查询当前页数据
调用 Mapper 查询总数
封装 PageResult
```

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

### 6. Mapper 层

分页查询：

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

总数统计：

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

Mapper 负责：

```text
执行分页 SQL
执行总数统计 SQL
status 不为空时按状态筛选
status 为空时查询全部任务
```

面试表达：

> 我将任务列表接口改造成分页查询接口。Controller 接收 page、pageSize 和 status；Service 校验参数并计算 offset；Mapper 使用 MyBatis 动态 SQL，根据 status 是否为空决定是否拼接状态条件，并使用 LIMIT 和 OFFSET 实现分页查询。

---

## 十、当前项目错误类型总结

当前项目可以区分四类响应：

### 1. 正常请求：200

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 2. 参数错误：400

例如标题为空、状态非法、page 小于 1：

```json
{
  "code": 400,
  "message": "页码必须大于等于1",
  "data": null
}
```

### 3. 业务错误：404

例如任务不存在：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

### 4. 未知系统错误：500

例如未预期的程序异常：

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

---

## 十一、当前项目亮点总结

当前项目已经具备这些亮点：

```text
1. Spring Boot + MyBatis + MySQL 完整后端链路
2. Controller / Service / Mapper / Entity 分层清晰
3. 支持任务 CRUD
4. 支持统一返回结构
5. 支持参数校验
6. 支持全局异常处理
7. 支持业务异常处理
8. 支持任务状态枚举
9. 支持分页查询
10. 支持按状态筛选
11. 使用 HTTP Client 做接口测试
12. 保存接口响应结果，方便复盘和展示
```

简历中可以压缩为：

> 基于 Spring Boot + MyBatis + MySQL 实现任务管理系统后端，支持任务 CRUD、分页查询、按状态筛选、统一接口返回、参数校验和业务异常处理，并使用 HTTP Client 完成接口测试。

---

## 十二、现阶段面试需要掌握什么

### 必须掌握

```text
Controller / Service / Mapper 分别负责什么
Result 为什么要统一返回
@Valid 参数校验怎么触发
GlobalExceptionHandler 怎么统一处理异常
BusinessException 为什么属于业务异常
TaskStatus 枚举的作用
分页查询为什么需要 LIMIT 和 OFFSET
PageResult 中 page、pageSize、total、records 的含义
MyBatis 动态 SQL 中 <script>、<where>、<if> 的作用
```

### 了解即可

```text
Spring MVC 的完整请求调度流程
MyBatis Mapper 代理对象的生成过程
Jackson 的 JSON 序列化细节
异常处理器的底层匹配机制
```

### 暂时不用深挖

```text
Spring 源码
MyBatis 源码
复杂事务传播机制
缓存机制
分布式部署
微服务
```

---

## 十三、后续建议

现在项目已经基本具备简历项目雏形，下一阶段不建议盲目继续堆功能，而应该开始转向求职准备：

```text
1. 整理简历项目描述
2. 整理项目亮点
3. 准备 1 分钟、3 分钟项目介绍
4. 整理项目常见面试问答
5. 梳理 Java 基础、Spring Boot、MyBatis、MySQL 高频问题
6. 开始分析实习 JD
7. 准备投递简历
```

如果后续还有时间，可以继续补充：

```text
日志记录
接口文档 Swagger / Knife4j
部署
登录权限
更完善的错误码设计
```

但当前最重要的是：

> 把这个项目讲清楚、写进简历、用于投递和面试。

---

## 十四、当前阶段一句话总结

当前项目已经从“Spring Boot 入门 Demo”和“数据库版 CRUD 项目”，逐步升级为一个具备统一返回、参数校验、异常处理、业务错误处理、分页查询和状态筛选能力的 Java 后端项目雏形。

接下来重点不只是继续写代码，而是要把项目整理成简历和面试中能清楚表达的成果。
