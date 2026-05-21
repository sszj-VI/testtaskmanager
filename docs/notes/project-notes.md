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
