# testtaskmanager 对话交接总结

> 用途：给新的 AI 对话读取，帮助无缝接续当前 Java 后端实习 / 秋招准备计划。  
> 当前主线：以 `testtaskmanager` 任务管理系统后端项目为核心，完成项目开发、项目理解、面试总结、简历包装和投递准备。

---

## 1. 用户当前目标与状态

用户正在准备 **Java 后端方向实习 / 秋招**，希望通过一个逐步完善的 Spring Boot 后端项目来建立简历项目和面试表达。

用户主要担心：

- 暑期实习是否来得及；
- 没有暑期实习是否会影响秋招；
- 当前项目是否足够写进简历；
- 是否需要继续做新项目；
- 如何把项目转化成简历和面试素材。

当前判断：

- 用户已经从“空想学习路线”进入了真实项目阶段；
- 当前项目已经具备简历项目雏形；
- 不建议马上开第二个项目；
- 下一阶段重点应从继续堆功能，转向 **简历项目描述、项目讲稿、面试问答、JD 分析和投递准备**。

---

## 2. 当前项目概况

项目名称：

```text
testtaskmanager
```

项目定位：

> 基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目，用于练习 Java 后端开发中的接口设计、数据库 CRUD、分层架构、统一返回结构、参数校验、异常处理、分页查询、状态筛选、接口测试和面试表达。

当前简历描述雏形：

```text
基于 Spring Boot + MyBatis + MySQL 实现任务管理系统后端，支持任务 CRUD、分页查询、按状态筛选、统一接口返回、参数校验和业务异常处理，并使用 HTTP Client 完成接口测试。
```

---

## 3. 已完成项目阶段

### 阶段 1：Spring Boot 入门与 `/hello`

完成：

- 创建 Spring Boot 项目；
- 启动内置 Tomcat；
- 能访问 `/hello`；
- 初步理解 Controller、请求映射、JSON 响应。

学习点：

- `@SpringBootApplication`
- `SpringApplication.run`
- `@RestController`
- `@GetMapping`
- Tomcat
- DispatcherServlet
- Spring MVC 请求链路

---

### 阶段 2：内存版任务管理系统

完成接口：

```http
POST   /tasks
GET    /tasks
GET    /tasks/{id}
PUT    /tasks/{id}/status
DELETE /tasks/{id}
```

早期使用 `List<Task>` 内存存储数据。

对应总结：

```text
taskmanager-面试总结1.md
```

重点：

- Spring Boot 基础运行原理；
- Controller / Service / Entity；
- 常用注解；
- RESTful；
- JSON 与 Java 对象转换；
- IoC / DI；
- 内存版项目不足。

---

### 阶段 3：MySQL + MyBatis 数据库版 CRUD

完成：

- 接入 MySQL；
- 创建 `task_manager` 数据库；
- 创建 `task` 表；
- 使用 MyBatis Mapper 操作数据库；
- 实现数据库版新增、查询、按 ID 查询、修改状态、删除；
- 数据真实写入 MySQL；
- 支持 `created_time` / `updated_time` 映射为 `createdTime` / `updatedTime`。

对应总结：

```text
taskmanager-面试总结2.md
```

重点：

- Controller / Service / Mapper / Entity；
- `@Mapper`;
- `@Insert` / `@Select` / `@Update` / `@Delete`;
- `@Options(useGeneratedKeys = true, keyProperty = "id")`;
- `@Param`;
- MyBatis 下划线转驼峰；
- Mapper 执行 SQL 的完整流程。

---

### 阶段 4：项目文档整理

维护了：

```text
README.md
docs/notes/project-notes.md
src/test/resources/api-tests/
docs/api-responses/
```

形成了“代码 + 测试 + 响应文件 + 文档 + Git 提交”的阶段性开发节奏。

---

### 阶段 5：统一接口返回结构

新增：

```text
common/Result.java
```

目标：

> 所有任务相关接口统一返回 `code`、`message`、`data` 三个字段。

成功响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

完成：

- `POST /tasks` 返回 `Result<Task>`;
- `GET /tasks/{id}` 返回 `Result<Task>`;
- `PUT /tasks/{id}/status` 返回 `Result<Boolean>`;
- `DELETE /tasks/{id}` 返回 `Result<Boolean>`。

---

### 阶段 6：参数校验 + 全局异常处理

新增 / 修改：

```text
pom.xml
entity/Task.java
dto/UpdateTaskStatusRequest.java
exception/GlobalExceptionHandler.java
common/Result.java
controller/TaskController.java
```

引入：

```xml
spring-boot-starter-validation
```

规则：

```text
title：不能为空，长度不能超过 100
description：长度不能超过 500
status：不能为空，只能是 TODO / DOING / DONE
```

涉及：

```text
@Valid
@NotBlank
@Size
@Pattern
@RestControllerAdvice
@ExceptionHandler
MethodArgumentNotValidException
```

错误响应示例：

```json
{
  "code": 400,
  "message": "任务标题不能为空",
  "data": null
}
```

测试通过：

- 空标题；
- 标题过长；
- 非法状态；
- 正常 CRUD 回归测试。

---

### 阶段 7：任务状态枚举 + 业务异常处理

新增：

```text
enums/TaskStatus.java
exception/BusinessException.java
```

目标：

> 区分参数错误和业务错误，让查询 / 修改 / 删除不存在任务时返回 404，而不是 `data: null` 或 `data: false`。

业务异常：

```java
throw new BusinessException(404, "任务不存在");
```

返回：

```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

测试通过：

```http
GET    /tasks/999999
PUT    /tasks/999999/status
DELETE /tasks/999999
```

均返回 404 和 `"任务不存在"`。

---

### 阶段 8：分页查询 + 按状态筛选

新增：

```text
common/PageResult.java
```

修改：

```text
enums/TaskStatus.java
mapper/TaskMapper.java
service/TaskService.java
controller/TaskController.java
```

目标：

> 改造 `GET /tasks`，使任务列表查询支持分页和按状态筛选，而不是一次性返回全部任务。

当前接口：

```http
GET /tasks?page=1&pageSize=10
GET /tasks?page=1&pageSize=10&status=TODO
GET /tasks?page=1&pageSize=10&status=DOING
GET /tasks?page=1&pageSize=10&status=DONE
```

分页响应：

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

校验：

```text
page >= 1
1 <= pageSize <= 100
status 如果传入，只能是 TODO / DOING / DONE
```

错误示例：

```json
{
  "code": 400,
  "message": "页码必须大于等于1",
  "data": null
}
```

分页公式：

```text
offset = (page - 1) * pageSize
```

MyBatis 使用：

```sql
LIMIT #{pageSize} OFFSET #{offset}
```

测试通过：

- 分页查询；
- TODO / DOING / DONE 状态筛选；
- 非法 page；
- 非法 pageSize；
- 非法 status；
- 正常 CRUD 回归测试。

---

## 4. 当前项目结构

```text
src/main/java/com/example/testtaskmanager
├── common
│   ├── PageResult.java
│   └── Result.java
├── controller
│   ├── HelloController.java
│   └── TaskController.java
├── dto
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

## 5. 当前核心接口

```http
GET    /hello
POST   /tasks
GET    /tasks?page=1&pageSize=10
GET    /tasks?page=1&pageSize=10&status=TODO
GET    /tasks/{id}
PUT    /tasks/{id}/status
DELETE /tasks/{id}
```

说明：

- `/hello` 只是测试 Spring Boot 是否正常；
- `/tasks` 相关接口是业务接口；
- `/tasks` 列表查询现在返回分页结构；
- 查询不存在任务返回 404；
- 修改 / 删除不存在任务返回 404。

---

## 6. 当前接口测试文件与响应目录

测试文件：

```text
src/test/resources/api-tests/
```

主要包括：

```text
api-test-3.http：CRUD 与统一返回结构测试
api-test-4.http：参数校验与错误响应测试
api-test-5.http：业务异常处理测试
api-test-6.http：分页查询与状态筛选测试
```

响应目录：

```text
docs/api-responses/
├── crud/
├── validation/
├── business-exception/
└── page-query/
```

测试总体结论：

- CRUD 回归测试通过；
- 参数校验测试通过；
- 业务异常测试通过；
- 分页查询与状态筛选测试通过。

---

## 7. 当前文档与面试总结

当前应维护：

```text
README.md
docs/notes/project-notes.md
docs/summaries/taskmanager-面试总结1.md
docs/summaries/taskmanager-面试总结2.md
docs/summaries/taskmanager-面试总结3.md
```

### 面试总结 1

定位：

> 内存版任务管理系统阶段总结。

重点：

- Spring Boot 启动流程；
- Tomcat；
- DispatcherServlet；
- Controller / Service / Entity；
- RESTful；
- JSON 转换；
- IoC / DI。

### 面试总结 2

定位：

> MySQL + MyBatis 数据库版 CRUD 阶段总结。

重点：

- Mapper；
- MyBatis 注解；
- 主键回填；
- 参数绑定；
- 下划线转驼峰；
- 数据库 CRUD 流程。

### 面试总结 3

定位：

> 规范化后端项目阶段总结。

重点：

- 统一返回结构；
- 参数校验；
- 全局异常处理；
- BusinessException；
- TaskStatus 枚举；
- 业务异常 404；
- 分页查询；
- 按状态筛选；
- PageResult；
- 面试表达草稿。

---

## 8. 已重点研究过的知识点

用户已经研究过：

- Controller 和 Service 的职责区别；
- exception 包下各类作用；
- 自定义异常如何定义、抛出和捕获；
- `BusinessException extends RuntimeException`；
- `throw new BusinessException(...)`；
- `@RestControllerAdvice` 和 `@ExceptionHandler`；
- `MethodArgumentNotValidException`；
- `BindingResult / FieldError / stream / findFirst / map / orElse`；
- 分页查询整体链路；
- `PageResult` 字段含义；
- `TaskStatus.isValid`；
- MyBatis 动态 SQL：`<script>`、`<where>`、`<if>`；
- `LIMIT` / `OFFSET`；
- `@RequestParam(defaultValue = "...")`；
- WSL2 Ubuntu 中 MySQL 启动问题；
- MySQL 自增 ID 删除后不会递减。

---

## 9. 已解决的重要疑问

### WSL2 中 MySQL 是否要打开 Ubuntu 才能启动

如果 MySQL 安装在 WSL2 Ubuntu 中，Windows 开机不等于 MySQL 已启动。建议顺序：

```text
1. 打开 Ubuntu / WSL
2. sudo service mysql status
3. sudo service mysql start
4. 启动 Spring Boot
5. 测试 /hello
6. 测试 /tasks
```

如果 `/hello` 正常而 `/tasks` 500，常见原因是数据库没启动或连接尚未稳定。

---

### 自增 ID 删除后不会递减

MySQL 自增主键只保证唯一递增，不保证连续。删除 `id=1` 后，`id=2` 不会自动变成 `id=1`。

开发测试阶段如果想清空并重置 ID：

```sql
TRUNCATE TABLE task;
```

不要为了“好看”手动重排主键 ID。

---

### 是否需要增加“清空全部任务”接口

当前不建议作为正式接口加入。开发测试清空数据用 SQL：

```sql
TRUNCATE TABLE task;
```

原因：

- 一键删除全部数据风险高；
- 真实项目需要权限、二次确认、日志记录；
- 目前不适合作为正式业务接口。

---

### Git / 压缩包注意点

曾出现：

- CRLF / LF 换行符噪音；
- 中文文件名在压缩包或不同环境中变成 `#Uxxxx`；
- AI 误读旧文件或传错压缩包。

后续新对话应注意：

> 如果用户本地 `git status` 正常，不要反复纠结压缩包里的换行符或中文文件名异常。  
> 判断项目状态前要看最新上传的 `testtaskmanager`，不要误读旧包。

---

## 10. 当前项目是否足够写简历

当前判断：

> 已经足够作为简历项目雏形，而且比普通 CRUD 项目更完整。

可以写：

```text
基于 Spring Boot + MyBatis + MySQL 实现任务管理系统后端，支持任务 CRUD、分页查询、按状态筛选、统一接口返回、参数校验和业务异常处理，并使用 HTTP Client 完成接口测试。
```

项目亮点：

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

---

## 11. 当前下一步建议

现在不建议继续盲目加功能。推荐进入：

> **求职材料整理 + 面试表达准备阶段**

优先级：

```text
1. 整理简历项目描述
2. 整理项目亮点
3. 准备 1 分钟项目介绍
4. 准备 3 分钟项目介绍
5. 整理项目常见面试问答
6. 梳理 Java / Spring Boot / MyBatis / MySQL 高频问题
7. 开始分析 Java 后端实习 JD
8. 开始准备投递简历
```

如果还有时间再做：

```text
1. Swagger / Knife4j 接口文档
2. 日志记录
3. 更完善的错误码设计
4. 简单部署
5. 登录权限
6. Docker
7. Redis
```

注意：之前多次提醒，不要在当前阶段盲目继续堆 Redis、JWT、Spring Security、Docker。当前更重要的是把项目讲清楚、写进简历、用于投递和面试。

---

## 12. 给下一个 AI 的接续建议

新的 AI 对话应优先：

1. 确认用户上传的是最新 `testtaskmanager` 项目，而不是旧的 `testprojects` 或其他包；
2. 不要默认怀疑用户没做完，先看最新文件和测试结果；
3. 如果用户问“下一步做什么”，优先建议进入简历和面试准备；
4. 如果继续开发功能，保持“小步推进”：代码 + 测试 + 响应文件 + 文档 + Git 提交；
5. 用户喜欢逐句理解代码，因此解释代码时要细致；
6. 用户有求职焦虑，需要现实但鼓励的反馈；
7. 如果判断出错，要承认并重新检查最新上传文件。

---

## 13. 当前最佳接续点

当前最佳接续点：

> 项目功能阶段基本完成，可以开始做 `taskmanager-面试总结3.md` 的落地、简历项目描述、项目讲稿和常见面试问答。

推荐下一步任务：

```text
1. 确认 README.md 和 project-notes.md 已更新到分页筛选阶段；
2. 确认 taskmanager-面试总结3.md 已保存；
3. 如未提交分页筛选阶段，提交：
   git add -A
   git commit -m "feat: add pagination and status filter"
4. 开始整理简历项目描述；
5. 准备 1 分钟项目介绍；
6. 准备项目常见面试问答。
```

---

## 14. 一句话总结

用户正在准备 Java 后端实习 / 秋招，当前主项目 `testtaskmanager` 已完成 Spring Boot + MyBatis + MySQL CRUD、统一返回、参数校验、全局异常、业务异常、状态枚举、分页查询、状态筛选和接口测试。下一步不应盲目堆功能，应优先把项目写进简历、准备项目介绍和面试问答，同时根据时间再决定是否补 Swagger、日志、部署等增强项。
