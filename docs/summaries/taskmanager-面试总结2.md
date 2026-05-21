# taskmanager-面试总结2.md

## 一、当前阶段总结

截至目前，`task-manager` 项目已经从最初的 Spring Boot 入门接口，推进到了 **Spring Boot + MyBatis + MySQL 数据库版 CRUD** 阶段。

当前已经完成：

- Spring Boot 项目能够正常启动；
- 已完成任务管理系统内存版 CRUD；
- 已接入 MySQL 数据库；
- 已创建 `task_manager` 数据库与 `task` 表；
- 已通过 MyBatis Mapper 操作数据库；
- 已完成数据库版任务新增、查询列表、按 ID 查询、修改状态、删除；
- 已验证数据可以真实写入 MySQL，而不是只存在 Java 内存中；
- 已验证 `created_time` / `updated_time` 可以映射为 Java 中的 `createdTime` / `updatedTime`。

当前项目已经具备一条完整后端链路：

```text
HTTP Client
  ↓
TaskController
  ↓
TaskService
  ↓
TaskMapper
  ↓
MySQL task 表
  ↓
返回 JSON
```

---

## 二、当前项目可以怎么对外介绍

可以准备一个 1 分钟项目介绍版本：

> 我做了一个基于 Spring Boot + MyBatis + MySQL 的任务管理系统后端项目。项目实现了任务的新增、查询列表、按 ID 查询、修改状态和删除功能。  
> 项目采用 Controller、Service、Mapper、Entity 分层结构：Controller 负责接收 HTTP 请求，Service 负责业务逻辑处理，Mapper 负责通过 MyBatis 执行 SQL 操作 MySQL 数据库。  
> 当前项目已经从最初的内存版 `List<Task>` 存储升级为 MySQL 持久化存储，数据可以在服务重启后保留。项目中还使用了 MyBatis 的下划线转驼峰配置，将数据库中的 `created_time`、`updated_time` 自动映射为 Java 对象中的 `createdTime`、`updatedTime`。

---

## 三、项目分层应该怎么理解

当前项目主要分为四层：

```text
Controller：接收请求
Service：处理业务逻辑
Mapper：操作数据库
Entity：表示数据对象
```

### 1. Controller 层

作用：

- 接收 HTTP 请求；
- 从 URL 或请求体中获取参数；
- 调用 Service；
- 把结果返回给前端或 HTTP Client。

例如：

```java
@PostMapping
public Task createTask(@RequestBody Task task) {
    return taskService.createTask(task);
}
```

面试表达：

> Controller 主要负责接收外部请求，并调用 Service 完成业务处理。它不直接操作数据库，这样可以让职责更清晰。

---

### 2. Service 层

作用：

- 处理业务逻辑；
- 设置默认值；
- 做简单参数判断；
- 调用 Mapper 操作数据库。

例如新增任务时，Service 会做这些事情：

```text
设置默认状态 TODO
设置创建时间 createdTime
设置更新时间 updatedTime
调用 Mapper 插入数据库
```

面试表达：

> Service 层负责业务逻辑，比如新增任务时设置默认状态和时间，修改状态时判断参数是否为空，然后再调用 Mapper 执行数据库操作。

---

### 3. Mapper 层

作用：

- 专门和数据库打交道；
- 通过 MyBatis 注解执行 SQL；
- 把 SQL 查询结果转换成 Java 对象。

例如：

```java
@Select("SELECT id, title, description, status, created_time, updated_time FROM task WHERE id = #{id}")
Task findById(Long id);
```

面试表达：

> Mapper 是数据访问层，负责执行 SQL。MyBatis 会根据 Mapper 接口上的注解生成代理对象，运行时执行对应 SQL，并把数据库结果映射成 Java 对象。

---

### 4. Entity 层

作用：

- 表示业务数据结构；
- 与数据库表字段对应；
- 与接口返回 JSON 对应。

例如：

```java
private Long id;
private String title;
private String description;
private String status;
private LocalDateTime createdTime;
private LocalDateTime updatedTime;
```

面试表达：

> Entity 用来表示任务对象，它的字段和数据库表中的字段对应。数据库中使用下划线命名，Java 中使用驼峰命名，通过 MyBatis 配置完成自动映射。

---

## 四、Mapper 是什么

一句话理解：

> Mapper 是专门负责数据库操作的接口。

在项目中，`TaskMapper` 负责操作 `task` 表。

它包含这些方法：

```text
insert        新增任务
findAll       查询所有任务
findById      根据 ID 查询任务
updateStatus  修改任务状态
deleteById    删除任务
```

### 为什么 Mapper 是接口？

因为 MyBatis 会在程序运行时自动生成实现类。

你虽然只写了：

```java
public interface TaskMapper
```

但 MyBatis 会根据 `@Insert`、`@Select`、`@Update`、`@Delete` 这些注解，自动生成真正执行 SQL 的代理对象。

面试表达：

> Mapper 接口本身没有方法体，但 MyBatis 会在运行时为它生成代理对象。当调用 Mapper 方法时，MyBatis 会读取方法上的 SQL 注解，绑定参数，执行 SQL，然后把结果返回给 Java 程序。

---

## 五、Mapper 中常见注解总结

### 1. `@Mapper`

作用：

```text
告诉 MyBatis：这是一个数据库操作接口。
```

代码：

```java
@Mapper
public interface TaskMapper {
}
```

面试表达：

> `@Mapper` 用来标记 MyBatis 的 Mapper 接口，让 Spring Boot 能扫描并注册它，之后 Service 就可以注入这个 Mapper。

---

### 2. `@Insert`

作用：

```text
执行新增 SQL。
```

代码：

```java
@Insert("""
        INSERT INTO task(title, description, status, created_time, updated_time)
        VALUES(#{title}, #{description}, #{status}, #{createdTime}, #{updatedTime})
        """)
int insert(Task task);
```

重点：

```text
#{title} 会从 Task 对象的 title 属性中取值。
#{description} 会从 Task 对象的 description 属性中取值。
#{createdTime} 会从 Task 对象的 createdTime 属性中取值。
```

面试表达：

> `@Insert` 用于执行插入 SQL，`#{}` 用来绑定 Java 对象中的属性，MyBatis 会把对象属性安全地传入 SQL。

---

### 3. `@Options(useGeneratedKeys = true, keyProperty = "id")`

作用：

```text
插入数据后，把数据库自动生成的主键 id 回填到 Java 对象的 id 属性中。
```

例如：

```java
@Options(useGeneratedKeys = true, keyProperty = "id")
```

假设新增任务前：

```text
task.id = null
```

MySQL 插入后自动生成：

```text
id = 8
```

MyBatis 会把它回填为：

```text
task.id = 8
```

面试表达：

> 因为数据库表中的 id 是自增主键，所以插入时不需要手动设置 id。`@Options` 可以让 MyBatis 获取数据库生成的主键，并回填到 Java 对象的 id 属性中。

---

### 4. `int insert(Task task);`

作用：

```text
定义一个新增任务的方法。
```

拆开看：

```text
int：返回影响了几行数据；
insert：方法名；
Task task：要插入数据库的任务对象。
```

通常：

```text
返回 1：插入成功 1 行；
返回 0：没有插入成功。
```

面试表达：

> Mapper 中的 insert 方法返回 int，表示 SQL 影响的行数。新增成功一般返回 1。

---

### 5. `@Select`

作用：

```text
执行查询 SQL。
```

查询所有任务：

```java
@Select("""
        SELECT id, title, description, status, created_time, updated_time
        FROM task
        ORDER BY id DESC
        """)
List<Task> findAll();
```

根据 ID 查询：

```java
@Select("""
        SELECT id, title, description, status, created_time, updated_time
        FROM task
        WHERE id = #{id}
        """)
Task findById(Long id);
```

面试表达：

> `@Select` 用于执行查询 SQL。如果查询多条记录，返回 `List<Task>`；如果根据主键查询一条记录，返回单个 `Task`，查不到时通常返回 `null`。

---

### 6. `@Update`

作用：

```text
执行修改 SQL。
```

代码：

```java
@Update("""
        UPDATE task
        SET status = #{status}, updated_time = #{updatedTime}
        WHERE id = #{id}
        """)
int updateStatus(@Param("id") Long id,
                 @Param("status") String status,
                 @Param("updatedTime") LocalDateTime updatedTime);
```

面试表达：

> `@Update` 用于执行更新操作，返回值 int 表示影响行数。如果返回 1，说明成功修改了一条记录；如果返回 0，说明没有找到对应 id 的数据。

---

### 7. `@Param`

作用：

```text
给 Mapper 方法中的参数起名字，让 SQL 中可以通过 #{名字} 引用。
```

例如：

```java
@Param("id") Long id
@Param("status") String status
@Param("updatedTime") LocalDateTime updatedTime
```

对应 SQL：

```sql
WHERE id = #{id}
SET status = #{status}, updated_time = #{updatedTime}
```

什么时候需要？

```text
Mapper 方法有多个参数时，建议使用 @Param。
```

面试表达：

> 当 Mapper 方法有多个参数时，我会使用 `@Param` 明确参数名，这样 SQL 中的 `#{id}`、`#{status}` 就能准确对应 Java 方法参数。

---

### 8. `@Delete`

作用：

```text
执行删除 SQL。
```

代码：

```java
@Delete("""
        DELETE FROM task
        WHERE id = #{id}
        """)
int deleteById(Long id);
```

面试表达：

> `@Delete` 用于执行删除 SQL，返回值也是影响行数。返回 1 表示删除成功，返回 0 表示没有找到对应记录。

---

## 六、MyBatis 下划线转驼峰

数据库字段通常写成：

```text
created_time
updated_time
```

Java 属性通常写成：

```text
createdTime
updatedTime
```

为了让它们自动对应，项目中配置了：

```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

它的作用是：

```text
created_time → createdTime
updated_time → updatedTime
```

面试表达：

> 数据库字段通常采用下划线命名，Java 属性采用驼峰命名。我在项目中开启了 MyBatis 的下划线转驼峰配置，使 `created_time` 能自动映射到 `createdTime`，避免手动写大量字段映射。

---

## 七、一次新增任务完整流程

以 `POST /tasks` 为例：

```text
1. HTTP Client 发送 POST /tasks 请求；
2. 请求体中包含 title 和 description；
3. TaskController 使用 @RequestBody 把 JSON 转成 Task 对象；
4. Controller 调用 TaskService.createTask(task)；
5. Service 设置默认状态 TODO；
6. Service 设置 createdTime 和 updatedTime；
7. Service 调用 taskMapper.insert(task)；
8. MyBatis 读取 @Insert 注解中的 SQL；
9. MyBatis 把 Task 对象中的属性绑定到 SQL；
10. MySQL 插入一条数据；
11. MySQL 自动生成 id；
12. @Options 把自增 id 回填到 task.id；
13. Service 返回 task；
14. Controller 把 task 转成 JSON 返回。
```

面试表达：

> 新增任务时，请求先进入 Controller，Controller 将 JSON 转成 Task 对象并调用 Service。Service 设置默认状态和时间后调用 Mapper，Mapper 通过 MyBatis 执行 INSERT SQL，将数据写入 MySQL。由于数据库 id 是自增主键，所以通过 `@Options` 将生成的 id 回填到 Task 对象中，最后返回给客户端。

---

## 八、一次查询任务完整流程

以 `GET /tasks/{id}` 为例：

```text
1. HTTP Client 发送 GET /tasks/3；
2. Controller 通过 @PathVariable 取出 id = 3；
3. Controller 调用 Service；
4. Service 调用 taskMapper.findById(3L)；
5. MyBatis 执行 SELECT SQL；
6. MySQL 返回查询结果；
7. MyBatis 把数据库记录封装成 Task 对象；
8. Controller 返回 JSON。
```

面试表达：

> 查询任务时，Controller 从 URL 中取出 id，然后调用 Service，Service 调用 Mapper 执行 SELECT 语句。MyBatis 会将数据库结果映射成 Task 对象，再由 Spring Boot 转成 JSON 返回。

---

## 九、一次修改状态完整流程

以 `PUT /tasks/{id}/status` 为例：

```text
1. HTTP Client 发送 PUT 请求；
2. URL 中带有任务 id；
3. 请求体中带有 status；
4. Controller 分别通过 @PathVariable 和 @RequestBody 获取参数；
5. Controller 调用 Service；
6. Service 判断 status 是否为空；
7. Service 调用 taskMapper.updateStatus；
8. Mapper 执行 UPDATE SQL；
9. MySQL 修改 status 和 updated_time；
10. Mapper 返回影响行数；
11. Service 根据 rows > 0 判断是否成功；
12. Controller 返回 success。
```

面试表达：

> 修改状态时，Service 会先做简单参数校验，然后调用 Mapper 执行 UPDATE SQL。Mapper 返回影响行数，Service 根据影响行数判断是否修改成功。

---

## 十、一次删除任务完整流程

以 `DELETE /tasks/{id}` 为例：

```text
1. HTTP Client 发送 DELETE /tasks/3；
2. Controller 从路径中取出 id；
3. Controller 调用 Service；
4. Service 调用 taskMapper.deleteById(id)；
5. Mapper 执行 DELETE SQL；
6. MySQL 删除对应记录；
7. Mapper 返回影响行数；
8. Service 根据 rows > 0 判断是否删除成功；
9. Controller 返回 success。
```

面试表达：

> 删除任务时，Mapper 执行 DELETE SQL，返回影响行数。Service 用返回值判断是否真的删除了记录。

---

## 十一、这部分面试需要掌握到什么程度

### 必须掌握

这些一定要能讲清楚：

```text
Controller / Service / Mapper 分别负责什么；
Mapper 是什么；
MyBatis 为什么不用自己写实现类；
@Insert / @Select / @Update / @Delete 分别干什么；
#{xxx} 是参数绑定；
@Param 是给多个参数命名；
@Options 是拿回数据库自增主键；
返回 int 表示影响行数；
数据库字段如何映射到 Java 属性。
```

### 了解即可

这些知道大概意思即可：

```text
MyBatis 会生成 Mapper 代理对象；
Spring Boot 会扫描 @Mapper；
Mapper 代理对象会被注入到 Service；
MyBatis 会把 SQL 查询结果封装成 Java 对象。
```

### 暂时不用深挖

这些可以后面再学：

```text
MyBatis 源码；
动态代理源码；
SqlSessionFactory 细节；
一级缓存和二级缓存；
复杂动态 SQL；
事务传播机制源码。
```

---

## 十二、当前项目还有哪些不足

当前阶段项目已经完成数据库版 CRUD，但还不是最终简历项目。后续建议继续优化：

```text
统一返回结构；
参数校验；
全局异常处理；
状态枚举；
分页查询；
按状态筛选；
README；
project-notes；
接口测试文档；
Git 提交记录；
简历项目描述。
```

这些是下一阶段重点。

---

## 十三、下一阶段目标

建议下一阶段目标定为：

```text
目标：把数据库版 CRUD 项目整理成规范化后端项目雏形。
```

具体任务：

1. 更新 README；
2. 更新 project-notes；
3. 提交 Git；
4. 增加统一返回结构；
5. 增加参数校验；
6. 增加全局异常处理；
7. 增加分页查询；
8. 增加按状态筛选；
9. 整理简历项目描述；
10. 更新面试总结。

---

## 十四、现阶段一句话总结

当前项目已经从“Spring Boot 入门 Demo”升级为“Spring Boot + MyBatis + MySQL 数据库版 CRUD 项目”。  
接下来最重要的不是继续盲目加功能，而是把现有代码理解清楚、文档整理清楚，并逐步加入统一返回、参数校验、异常处理和分页筛选，让项目更接近真实后端项目。
