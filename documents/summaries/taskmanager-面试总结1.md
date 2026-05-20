# taskmanager-面试总结1

> 项目：task-manager-backend  
> 阶段：目标 3，内存版任务管理系统  
> 用途：复习项目运行原理、Spring Boot 基础概念、面试表达

---

## 1. 当前项目做了什么

当前项目是一个基于 Spring Boot 的任务管理系统后端项目，已经实现了任务的基础增删改查接口。

目前已有接口：

```http
POST   /tasks              新增任务
GET    /tasks              查询任务列表
GET    /tasks/{id}         根据 ID 查询任务
PUT    /tasks/{id}/status  修改任务状态
DELETE /tasks/{id}         删除任务
```

当前版本使用 `List<Task>` 在内存中保存数据，暂时没有接入 MySQL。

---

## 2. 项目整体运行链路

一次请求从客户端进入后端，大致经过下面这条链路：

```text
HTTP Client / 浏览器
        ↓
Tomcat
        ↓
DispatcherServlet
        ↓
Spring MVC 根据路径找到 Controller 方法
        ↓
Controller
        ↓
Service
        ↓
List<Task> 内存数据
        ↓
返回 Java 对象
        ↓
Spring Boot 转成 JSON
        ↓
客户端收到响应
```

面试表达：

> 我的项目基于 Spring Boot 开发。项目启动后会启动内置 Tomcat，HTTP 请求进入后先由 Spring MVC 的 DispatcherServlet 接收，再根据请求方式和路径找到对应的 Controller 方法。Controller 接收参数后调用 Service 处理业务逻辑，Service 操作内存中的任务列表，最后返回 Java 对象，由 Spring Boot 自动转换成 JSON 响应。

---

## 3. Spring Boot 为什么能启动项目

项目入口类通常是：

```java
@SpringBootApplication
public class TaskManagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerBackendApplication.class, args);
    }
}
```

关键点：

```text
@SpringBootApplication：说明这是一个 Spring Boot 应用
main 方法：Java 程序启动入口
SpringApplication.run：启动 Spring Boot 项目
```

`SpringApplication.run(...)` 大致会做这些事：

```text
1. 启动 Spring 容器
2. 扫描项目中的类
3. 识别 @RestController、@Service 等注解
4. 自动配置 Web 环境
5. 启动内置 Tomcat
6. 监听 8080 端口
```

面试表达：

> Spring Boot 项目通过 main 方法启动，核心是 `SpringApplication.run(...)`。它会启动 Spring 容器，进行组件扫描和自动配置，并启动内置 Tomcat，使项目可以直接以 Web 服务的形式运行。

---

## 4. Tomcat 是什么

Tomcat 可以先理解成：

> 一个运行 Java Web 项目的服务器。

在当前项目中，Tomcat 的作用是：

```text
1. 监听 8080 端口
2. 接收 HTTP 请求
3. 把请求交给 Spring MVC 处理
4. 把 Spring MVC 返回的结果再发回客户端
```

比如访问：

```http
http://localhost:8080/tasks
```

这里的含义是：

```text
localhost：访问自己电脑
8080：Tomcat 当前监听的端口
/tasks：接口路径
```

面试表达：

> Spring Boot 默认内置 Tomcat，所以不需要手动部署到外部 Tomcat。项目启动后，Tomcat 会监听指定端口，接收 HTTP 请求，并把请求交给 Spring MVC 继续处理。

---

## 5. DispatcherServlet 是什么

`DispatcherServlet` 是 Spring MVC 的核心组件，可以理解成：

> Spring MVC 的请求总调度员。

它负责：

```text
1. 接收进入 Spring MVC 的请求
2. 根据请求方法和路径找到对应的 Controller 方法
3. 调用 Controller
4. 处理返回结果
```

流程：

```text
请求进入 Tomcat
        ↓
Tomcat 转交给 DispatcherServlet
        ↓
DispatcherServlet 找到对应 Controller 方法
        ↓
执行 Controller
```

面试表达：

> 在 Spring MVC 中，请求不是直接进入 Controller，而是先进入 DispatcherServlet。DispatcherServlet 会根据请求路径和请求方式，通过 HandlerMapping 找到对应的 Controller 方法，然后调用该方法处理请求。

---

## 6. Controller、Service、Entity 分别是什么

当前项目分了三层：

```text
Controller：接收 HTTP 请求，返回响应
Service：处理业务逻辑
Entity：表示数据结构
```

对应文件：

```text
TaskController.java
TaskService.java
Task.java
```

### 6.1 TaskController

`TaskController` 负责处理 `/tasks` 相关请求。

它主要做：

```text
1. 接收请求
2. 获取参数
3. 调用 TaskService
4. 返回结果
```

示例：

```java
@PostMapping
public Task createTask(@RequestBody Task task) {
    return taskService.createTask(task);
}
```

面试表达：

> Controller 层主要负责接收 HTTP 请求和返回响应，不直接写复杂业务逻辑。在我的项目中，TaskController 接收任务相关接口请求，然后调用 TaskService 完成具体操作。

### 6.2 TaskService

`TaskService` 负责真正的业务逻辑。

它主要做：

```text
1. 新增任务
2. 查询任务
3. 修改任务状态
4. 删除任务
5. 管理内存中的 List<Task>
```

示例：

```java
public Task createTask(Task task) {
    task.setId(nextId++);
    task.setStatus("TODO");
    task.setCreatedTime(LocalDateTime.now());
    tasks.add(task);
    return task;
}
```

面试表达：

> Service 层负责处理业务逻辑，比如创建任务时设置 ID、默认状态和创建时间，再把任务保存到任务列表中。这样可以避免 Controller 过于臃肿，也方便后续扩展数据库操作。

### 6.3 Task

`Task` 是任务实体类，表示一个任务有哪些字段。

```java
public class Task {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdTime;

}
```

字段含义：

```text
id：任务编号
title：任务标题
description：任务描述
status：任务状态
createdTime：创建时间
```

面试表达：

> Entity 层用于表示业务数据结构。在我的项目里，Task 表示一个任务对象，包含 ID、标题、描述、状态和创建时间等字段。

---

## 7. 常用注解总结

### 7.1 @RestController

作用：

```text
说明这个类是一个接口控制器
方法返回值会直接作为 HTTP 响应
通常会自动转成 JSON
```

示例：

```java
@RestController
public class TaskController {
}
```

面试表达：

> `@RestController` 表示这个类是 REST 接口控制器，方法返回的对象会通过消息转换器转换成 JSON 响应。

### 7.2 @RequestMapping

作用：

```text
设置一组接口的公共路径
```

示例：

```java
@RequestMapping("/tasks")
```

表示这个类下的接口都以 `/tasks` 开头。

### 7.3 @GetMapping / @PostMapping / @PutMapping / @DeleteMapping

作用：

```text
@GetMapping：处理 GET 请求，通常用于查询
@PostMapping：处理 POST 请求，通常用于新增
@PutMapping：处理 PUT 请求，通常用于修改
@DeleteMapping：处理 DELETE 请求，通常用于删除
```

示例：

```java
@GetMapping("/{id}")
public Object getTaskById(@PathVariable Long id) {
    return taskService.getTaskById(id);
}
```

对应请求：

```http
GET /tasks/1
```

### 7.4 @PathVariable

作用：

```text
从 URL 路径中取参数
```

示例：

```java
@GetMapping("/{id}")
public Object getTaskById(@PathVariable Long id) {
    return taskService.getTaskById(id);
}
```

请求：

```http
GET /tasks/1
```

含义：

```text
路径中的 1 会赋值给 id
```

面试表达：

> `@PathVariable` 用于获取路径参数，比如 `/tasks/1` 中的 `1` 会绑定到方法参数 `id` 上。

### 7.5 @RequestBody

作用：

```text
从请求体中读取 JSON，并转换成 Java 对象
```

示例：

```java
@PostMapping
public Task createTask(@RequestBody Task task) {
    return taskService.createTask(task);
}
```

请求体：

```json
{
  "title": "学习 Spring Boot",
  "description": "完成目标3"
}
```

会被转换成：

```java
Task task
```

面试表达：

> `@RequestBody` 用来读取 HTTP 请求体中的 JSON 数据，并通过 Jackson 等工具反序列化成 Java 对象。

### 7.6 @Service

作用：

```text
说明这个类是业务逻辑类
让 Spring 创建并管理它
```

示例：

```java
@Service
public class TaskService {
}
```

面试表达：

> `@Service` 标记业务逻辑类，Spring 启动时会扫描到它，并把它注册为 Bean，之后可以注入到 Controller 中使用。

---

## 8. 为什么注解能生效

注解本身可以理解成“标签”。

Spring Boot 启动时会扫描这些标签：

```text
@RestController
@Service
@GetMapping
@PostMapping
@RequestMapping
```

然后建立一张映射表：

```text
请求方式   请求路径              Java 方法

POST      /tasks              createTask()
GET       /tasks              getAllTasks()
GET       /tasks/{id}         getTaskById()
PUT       /tasks/{id}/status  updateTaskStatus()
DELETE    /tasks/{id}         deleteTask()
```

所以当请求进来时，Spring MVC 就知道应该调用哪个方法。

面试表达：

> 注解不是魔法。Spring Boot 启动时会进行组件扫描，读取类和方法上的注解，并建立请求路径和 Controller 方法之间的映射关系。请求到达时，Spring MVC 会根据映射关系调用对应方法。

---

## 9. JSON 与 Java 对象转换

### 9.1 JSON 转 Java 对象

请求：

```http
POST /tasks
Content-Type: application/json

{
  "title": "学习 Spring Boot",
  "description": "完成目标3"
}
```

Controller：

```java
public Task createTask(@RequestBody Task task)
```

Spring Boot 会把 JSON 转成：

```java
Task task = new Task();
task.setTitle("学习 Spring Boot");
task.setDescription("完成目标3");
```

这个过程叫：

```text
反序列化：JSON → Java 对象
```

### 9.2 Java 对象转 JSON

方法返回：

```java
return task;
```

客户端看到：

```json
{
  "id": 1,
  "title": "学习 Spring Boot",
  "description": "完成目标3",
  "status": "TODO",
  "createdTime": "2026-05-17T18:46:03"
}
```

这个过程叫：

```text
序列化：Java 对象 → JSON
```

面试表达：

> Spring Boot 默认使用 Jackson 进行 JSON 和 Java 对象之间的转换。请求进入时，`@RequestBody` 会触发反序列化；方法返回对象时，Spring MVC 会通过消息转换器把对象序列化为 JSON 响应。

---

## 10. 依赖注入和 IoC

Controller 中通常这样使用 Service：

```java
private final TaskService taskService;

public TaskController(TaskService taskService) {
    this.taskService = taskService;
}
```

这里没有手动写：

```java
TaskService taskService = new TaskService();
```

原因是：

```text
TaskService 被 @Service 标记
Spring 会创建并管理 TaskService 对象
TaskController 需要 TaskService
Spring 自动把 TaskService 传入构造方法
```

这叫：

```text
依赖注入 Dependency Injection，简称 DI
```

更大的思想叫：

```text
控制反转 Inversion of Control，简称 IoC
```

简单理解：

```text
传统写法：对象由程序员自己 new
Spring 写法：对象由 Spring 容器创建和管理
```

面试表达：

> 在我的项目中，TaskService 被 `@Service` 标记后会注册为 Spring Bean。TaskController 通过构造方法声明自己依赖 TaskService，Spring 创建 Controller 时会自动把 TaskService 注入进去。这体现了 Spring 的 IoC 和依赖注入思想。

---

## 11. RESTful 接口设计

当前接口使用了比较基础的 REST 风格：

```text
POST   /tasks              新增任务
GET    /tasks              查询任务列表
GET    /tasks/{id}         查询单个任务
PUT    /tasks/{id}/status  修改任务状态
DELETE /tasks/{id}         删除任务
```

HTTP 方法含义：

```text
GET：查询
POST：新增
PUT：修改
DELETE：删除
```

面试表达：

> 我按照 RESTful 风格设计接口，用 HTTP 方法表示操作类型，用 URL 表示资源。比如任务资源统一用 `/tasks` 表示，新增任务用 `POST /tasks`，查询任务用 `GET /tasks`，删除任务用 `DELETE /tasks/{id}`。

---

## 12. 当前项目的不足和后续优化

### 12.1 数据没有持久化

当前使用：

```java
private final List<Task> tasks = new ArrayList<>();
```

问题：

```text
数据存在内存中
项目重启后数据会丢失
```

后续优化：

```text
接入 MySQL
设计 task 表
使用 MyBatis 或 MyBatis-Plus 完成数据库 CRUD
```

面试表达：

> 当前版本使用内存 List 存储任务，主要用于验证接口流程。它的缺点是服务重启后数据会丢失，后续计划接入 MySQL 实现数据持久化。

### 12.2 返回结构不统一

当前可能有的接口返回 Task，有的接口返回：

```json
{
  "success": true
}
```

后续可以统一成：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

面试表达：

> 当前返回结构还不够统一，后续可以封装统一响应对象，比如包含 code、message 和 data 字段，方便前端统一处理。

### 12.3 参数校验不足

当前如果传空标题，可能也能新增任务。

后续可以加：

```text
title 不能为空
status 只能是 TODO / DOING / DONE
description 限制长度
```

可以用：

```java
@NotBlank
@Valid
```

面试表达：

> 当前项目还没有完善参数校验，后续可以使用 Spring Validation，比如 `@NotBlank` 和 `@Valid`，对请求参数进行校验。

### 12.4 缺少全局异常处理

当前如果出错，处理方式不够统一。

后续可以使用：

```java
@RestControllerAdvice
@ExceptionHandler
```

面试表达：

> 后续可以使用全局异常处理统一捕获业务异常和参数异常，避免每个 Controller 方法里都写重复的错误处理逻辑。

### 12.5 内存 List 存在线程安全问题

当前使用：

```java
private final List<Task> tasks = new ArrayList<>();
private Long nextId = 1L;
```

潜在问题：

```text
ArrayList 不是线程安全的
nextId++ 不是原子操作
多个请求同时新增任务时可能出现并发问题
```

后续优化：

```text
使用数据库自增 ID
使用事务
使用 AtomicLong
使用线程安全集合
```

面试表达：

> 当前内存版主要用于学习接口流程，没有考虑并发安全。真实项目不会直接用 ArrayList 存业务数据，而会使用数据库持久化，并通过数据库主键、事务等机制保证数据一致性。

---

## 13. 一分钟项目介绍模板

可以这样介绍：

> 我做了一个基于 Spring Boot 的任务管理系统后端项目，主要用于练习后端接口开发和分层设计。当前实现了任务的新增、查询列表、根据 ID 查询、修改状态和删除功能。接口采用 RESTful 风格设计，例如 `POST /tasks` 新增任务，`GET /tasks` 查询任务列表，`PUT /tasks/{id}/status` 修改任务状态。项目采用 Controller、Service、Entity 分层结构。Controller 负责接收 HTTP 请求，Service 负责业务逻辑处理，Entity 表示任务数据结构。当前版本使用内存 List 存储任务，后续计划接入 MySQL，实现数据持久化，并加入统一返回、参数校验和全局异常处理。

---

## 14. 高频面试问答

### 问：请求是怎么进入你的接口方法的？

答：

> Spring Boot 启动后会启动内置 Tomcat。请求进入后先到 Spring MVC 的 DispatcherServlet，DispatcherServlet 根据请求方式和路径，通过 HandlerMapping 找到对应的 Controller 方法，比如 `POST /tasks` 会匹配到 `@PostMapping` 标注的 `createTask` 方法，然后执行该方法并返回结果。

### 问：@RequestBody 是干什么的？

答：

> `@RequestBody` 用来读取 HTTP 请求体中的数据。比如前端传 JSON 时，Spring Boot 会通过 Jackson 把 JSON 反序列化成 Java 对象。在我的项目里，新增任务接口会把请求体中的 title 和 description 转成 Task 对象。

### 问：@PathVariable 是干什么的？

答：

> `@PathVariable` 用来从 URL 路径中取参数。比如 `GET /tasks/1` 会匹配 `@GetMapping("/{id}")`，其中路径里的 `1` 会赋值给方法参数 `Long id`。

### 问：为什么要分 Controller 和 Service？

答：

> Controller 主要负责接收请求和返回响应，不应该写太多业务逻辑。Service 负责具体业务处理，比如创建任务、查询任务、修改状态。这样代码职责更清晰，后续如果接 MySQL，只需要调整 Service 或数据访问层，Controller 可以保持相对稳定。

### 问：为什么不直接 new TaskService？

答：

> 因为 TaskService 由 Spring 容器管理。`@Service` 会把 TaskService 注册为 Bean，TaskController 通过构造方法声明依赖，Spring 会自动进行依赖注入。这体现了 IoC 思想。

### 问：当前项目有什么缺点？

答：

> 当前版本是内存版，数据存在 `List` 中，服务重启后数据会丢失，也没有考虑并发安全和参数校验。后续我会接入 MySQL 做持久化，增加统一返回结构、参数校验、全局异常处理和分页查询，让项目更接近真实后端开发。

---

## 15. 当前阶段最需要掌握的主线

现在最重要的是理解这条链路：

```text
main 方法启动 Spring Boot
        ↓
Spring Boot 启动内置 Tomcat
        ↓
Spring 扫描 @RestController、@Service 等注解
        ↓
Spring MVC 建立 URL 和 Controller 方法的映射
        ↓
请求进入 DispatcherServlet
        ↓
DispatcherServlet 找到对应 Controller 方法
        ↓
@RequestBody 把 JSON 转成 Java 对象
        ↓
Controller 调用 Service
        ↓
Service 处理业务并返回结果
        ↓
Spring 把 Java 对象转成 JSON 响应
```

只要能讲清楚这条链路，就说明已经理解了当前 Spring Boot 后端项目的核心运行流程。
