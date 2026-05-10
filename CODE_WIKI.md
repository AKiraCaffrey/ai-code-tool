# SaKi AI Code Tool - Code Wiki

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 整体架构](#2-整体架构)
- [3. 技术栈](#3-技术栈)
- [4. 项目目录结构](#4-项目目录结构)
- [5. 后端模块详解](#5-后端模块详解)
  - [5.1 应用入口与配置](#51-应用入口与配置)
  - [5.2 Controller 层](#52-controller-层)
  - [5.3 Service 层](#53-service-层)
  - [5.4 AI 代码生成核心模块](#54-ai-代码生成核心模块)
  - [5.5 LangGraph4j 工作流模块](#55-langgraph4j-工作流模块)
  - [5.6 Core 管道模块](#56-core-管道模块)
  - [5.7 数据模型层](#57-数据模型层)
  - [5.8 安全与权限模块](#58-安全与权限模块)
  - [5.9 限流模块](#59-限流模块)
  - [5.10 监控模块](#510-监控模块)
  - [5.11 基础设施模块](#511-基础设施模块)
- [6. 前端模块详解](#6-前端模块详解)
  - [6.1 应用入口与配置](#61-应用入口与配置)
  - [6.2 路由系统](#62-路由系统)
  - [6.3 页面组件](#63-页面组件)
  - [6.4 共享组件](#64-共享组件)
  - [6.5 API 层](#65-api-层)
  - [6.6 状态管理](#66-状态管理)
  - [6.7 权限控制](#67-权限控制)
  - [6.8 工具函数](#68-工具函数)
- [7. 数据库设计](#7-数据库设计)
- [8. 核心数据流](#8-核心数据流)
- [9. 依赖关系图](#9-依赖关系图)
- [10. 项目运行方式](#10-项目运行方式)

---

## 1. 项目概述

**SaKi AI Code Tool** 是一个 AI 驱动的代码生成平台，用户通过自然语言描述需求，系统利用大语言模型（LLM）自动生成可运行的前端代码，并支持在线预览、部署和下载。

核心能力包括：

- **智能代码生成**：支持 HTML 单文件、多文件、Vue 工程三种生成模式，由 AI 自动路由选择
- **对话式交互**：基于 SSE 流式推送，实时展示 AI 生成过程（包括工具调用可视化）
- **一键部署**：生成的代码可一键部署为可访问的 Web 应用
- **社区功能**：用户可发帖、评论、点赞，形成应用分享社区
- **工作流编排**：基于 LangGraph4j 实现多步骤 AI 工作流（图片收集 → 提示词增强 → 路由 → 代码生成 → 质检 → 构建）

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3 + Vite)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │  首页     │ │ 应用对话  │ │ 社区     │ │ 管理后台      │  │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └──────┬────────┘  │
│       │             │            │               │           │
│  ┌────▼─────────────▼────────────▼───────────────▼────────┐ │
│  │              Axios (HTTP/SSE) + Pinia Store             │ │
│  └──────────────────────┬─────────────────────────────────┘ │
└─────────────────────────┼───────────────────────────────────┘
                          │ HTTP / SSE
┌─────────────────────────▼───────────────────────────────────┐
│                 后端 (Spring Boot 3.5 + Java 21)             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                  Controller 层                        │   │
│  │  AppController │ UserController │ PostController ...  │   │
│  └──────────┬───────────────────────────────────────────┘   │
│  ┌──────────▼───────────────────────────────────────────┐   │
│  │                  Service 层                           │   │
│  │  AppService │ UserService │ PostService │ ChatService │   │
│  └──────────┬───────────────────────────────────────────┘   │
│  ┌──────────▼───────────────────────────────────────────┐   │
│  │            AI 代码生成核心                             │   │
│  │  ┌─────────────────────────────────────────────┐     │   │
│  │  │  AiCodeGeneratorFacade (门面)                │     │   │
│  │  │    ├── AiCodeGeneratorService (LangChain4j)  │     │   │
│  │  │    ├── AiCodeGenTypeRoutingService (路由)    │     │   │
│  │  │    ├── CodeParser → CodeFileSaver (管道)     │     │   │
│  │  │    └── VueProjectBuilder (构建)              │     │   │
│  │  └─────────────────────────────────────────────┘     │   │
│  │  ┌─────────────────────────────────────────────┐     │   │
│  │  │  LangGraph4j 工作流                          │     │   │
│  │  │    ├── CodeGenWorkflow (串行)                │     │   │
│  │  │    ├── CodeGenConcurrentWorkflow (并发)      │     │   │
│  │  │    └── CodeGenSubgraphWorkflow (子图)        │     │   │
│  │  └─────────────────────────────────────────────┘     │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  MySQL (MyBatis-Flex) │ Redis (Session/Cache/Memory) │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## 3. 技术栈

### 后端

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.5.4 |
| ORM | MyBatis-Flex | 1.11.1 |
| 数据库 | MySQL | - |
| 缓存/会话 | Redis + Caffeine | - |
| AI 框架 | LangChain4j | 1.1.0 |
| AI 工作流 | LangGraph4j | 1.6.0-rc2 |
| 限流 | Redisson | 3.50.0 |
| 对象存储 | 腾讯云 COS | 5.6.227 |
| 网页截图 | Selenium | 4.33.0 |
| API 文档 | Knife4j (OpenAPI 3) | 4.4.0 |
| 监控 | Micrometer + Prometheus | - |
| 工具库 | Hutool | 5.8.38 |

### 前端

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | 3.5.17 |
| 构建 | Vite | 7.0.0 |
| UI 库 | Ant Design Vue | 4.2.6 |
| 状态管理 | Pinia | 3.0.3 |
| 路由 | Vue Router | 4.5.1 |
| HTTP | Axios | 1.11.0 |
| 富文本 | WangEditor | 5.1.23 |
| Markdown | markdown-it | 14.1.0 |
| 代码高亮 | highlight.js | 11.11.1 |
| API 生成 | @umijs/openapi | 1.13.15 |

---

## 4. 项目目录结构

```
saki-ai-code-tool/
├── docs/                                    # 项目文档
│   ├── ARCHITECTURE_DESIGN.md
│   ├── DATABASE_DESIGN.md
│   ├── DEVELOPER_GUIDE.md
│   ├── FEATURE_DESIGN.md
│   └── PROJECT_OVERVIEW.md
├── saki-ai-code-mother-frontend/            # 前端项目
│   ├── src/
│   │   ├── api/                             # API 接口层（自动生成）
│   │   ├── assets/                          # 静态资源
│   │   ├── components/                      # 共享组件
│   │   ├── config/                          # 环境配置
│   │   ├── layouts/                         # 布局组件
│   │   ├── pages/                           # 页面组件
│   │   │   ├── admin/                       # 管理后台页面
│   │   │   ├── app/                         # 应用相关页面
│   │   │   ├── community/                   # 社区页面
│   │   │   ├── post/                        # 帖子页面
│   │   │   └── user/                        # 用户页面
│   │   ├── router/                          # 路由配置
│   │   ├── stores/                          # Pinia 状态管理
│   │   ├── utils/                           # 工具函数
│   │   ├── access.ts                        # 路由权限守卫
│   │   ├── main.ts                          # 应用入口
│   │   ├── request.ts                       # Axios 封装
│   │   └── App.vue                          # 根组件
│   ├── vite.config.ts
│   └── package.json
├── saki-ai-code-tool-backend/               # 后端项目
│   ├── src/main/java/com/saki/sakiaicodetoolbackend/
│   │   ├── ai/                              # AI 代码生成模块
│   │   │   ├── guardrail/                   # 输入/输出护轨
│   │   │   ├── model/                       # AI 结果模型
│   │   │   ├── tools/                       # AI 工具（文件操作等）
│   │   │   ├── AiCodeGeneratorService.java
│   │   │   ├── AiCodeGeneratorServiceFactory.java
│   │   │   ├── AiCodeGenTypeRoutingService.java
│   │   │   └── AiCodeGenTypeRoutingServiceFactory.java
│   │   ├── annotation/                      # 自定义注解
│   │   ├── aop/                             # AOP 切面
│   │   ├── common/                          # 通用响应类
│   │   ├── config/                          # 配置类
│   │   ├── constant/                        # 常量
│   │   ├── controller/                      # REST 控制器
│   │   ├── core/                            # 代码生成管道核心
│   │   │   ├── builder/                     # 项目构建器
│   │   │   ├── handler/                     # 流处理器
│   │   │   ├── parser/                      # 代码解析器
│   │   │   ├── saver/                       # 代码保存器
│   │   │   └── AiCodeGeneratorFacade.java   # 门面类
│   │   ├── exception/                       # 异常处理
│   │   ├── generator/                       # 代码生成器
│   │   ├── langgraph4j/                     # LangGraph4j 工作流
│   │   │   ├── ai/                          # 工作流 AI 服务
│   │   │   ├── node/                        # 工作流节点
│   │   │   ├── state/                       # 工作流状态
│   │   │   ├── tools/                       # 工作流工具
│   │   │   ├── CodeGenWorkflow.java
│   │   │   ├── CodeGenConcurrentWorkflow.java
│   │   │   └── CodeGenSubgraphWorkflow.java
│   │   ├── manager/                         # 外部服务管理
│   │   ├── mapper/                          # MyBatis Mapper
│   │   ├── model/                           # 数据模型
│   │   │   ├── entity/                      # 实体类
│   │   │   ├── enums/                       # 枚举类
│   │   │   └── vo/                          # 视图对象
│   │   ├── monitor/                         # 监控模块
│   │   ├── ratelimter/                      # 限流模块
│   │   ├── service/                         # 业务服务层
│   │   │   └── impl/                        # 服务实现
│   │   └── utils/                           # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/                          # MyBatis XML
│   │   ├── prompt/                          # AI Prompt 模板
│   │   └── application.yml                  # 应用配置
│   ├── sql/                                 # 数据库脚本
│   └── pom.xml
└── .gitignore
```

---

## 5. 后端模块详解

### 5.1 应用入口与配置

#### SaKiAiCodeToolApplication

应用主入口类，位于 `com.saki.sakiaicodetoolbackend`。

```java
@EnableCaching
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.saki.sakiaicodetoolbackend.mapper")
public class SaKiAiCodeToolApplication
```

- `@EnableCaching`：启用 Caffeine 本地缓存
- `exclude RedisEmbeddingStoreAutoConfiguration`：排除 Redis 向量存储自动配置
- `@MapperScan`：扫描 MyBatis Mapper 接口

#### 核心配置类

| 配置类 | 职责 |
|--------|------|
| `CorsConfig` | 跨域资源共享配置 |
| `CosClientConfig` | 腾讯云 COS 对象存储客户端配置 |
| `JsonConfig` | JSON 序列化配置 |
| `OpenAiStreamChatModelConfig` | OpenAI 流式聊天模型配置（多例模式） |
| `ReasoningStreamingChatModelConfig` | 推理流式模型配置（多例模式，用于 Vue 项目生成） |
| `RedisCacheManagerConfig` | Redis 缓存管理器配置 |
| `RedisChatMemoryStoreConfig` | Redis 对话记忆存储配置 |
| `RoutingAiModelConfig` | 路由 AI 模型配置（多例模式） |
| `StreamingChatModelConfig` | 流式聊天模型配置（多例模式） |
| `WebMvcAsyncConfig` | Web MVC 异步处理配置 |

> **关键设计**：多个模型配置类使用 `@Scope("prototype")` 多例模式，解决并发请求时 StreamingChatModel 实例共享导致的线程安全问题。

#### 应用配置 (application.yml)

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `server.port` | 8123 | 服务端口 |
| `server.servlet.context-path` | /api | API 上下文路径 |
| `spring.session.store-type` | redis | Session 存储方式 |
| `spring.session.timeout` | 2592000 (30天) | Session 过期时间 |
| `mybatis-flex.global-config.logic-delete-column` | is_delete | 逻辑删除字段 |

---

### 5.2 Controller 层

所有 Controller 位于 `controller/` 包，提供 RESTful API 接口。

#### AppController

**路径**：`/api/app`

| 端点 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `GET /chat/gen/code` | `chatToGenCode` | AI 对话生成代码（SSE 流式） | 登录用户 + 限流 |
| `POST /deploy` | `deployApp` | 部署应用 | 应用创建者 |
| `GET /download/{appId}` | `downloadAppCode` | 下载应用代码 ZIP | 应用创建者 |
| `POST /add` | `addApp` | 创建应用 | 登录用户 |
| `POST /update` | `updateApp` | 更新应用名称 | 应用创建者 |
| `POST /delete` | `deleteApp` | 删除应用 | 创建者或管理员 |
| `GET /get/vo` | `getAppVOById` | 获取应用详情 | 公开 |
| `POST /my/list/page/vo` | `listMyAppVOByPage` | 我的应用列表 | 登录用户 |
| `POST /good/list/page/vo` | `listGoodAppVOByPage` | 精选应用列表 | 公开 |
| `POST /admin/delete` | `deleteAppByAdmin` | 管理员删除应用 | 管理员 |
| `POST /admin/update` | `updateAppByAdmin` | 管理员更新应用 | 管理员 |
| `POST /admin/list/page/vo` | `listAppVOByPageByAdmin` | 管理员应用列表 | 管理员 |

#### UserController

**路径**：`/api/user`

| 端点 | 方法 | 说明 |
|------|------|------|
| `POST /register` | `userRegister` | 用户注册 |
| `POST /login` | `userLogin` | 用户登录 |
| `POST /logout` | `userLogout` | 用户登出 |
| `GET /get/login` | `getLoginUser` | 获取当前登录用户 |
| `GET /get/vo` | `getUserVOById` | 获取用户信息 |
| `POST /update/my` | `updateMyUser` | 更新当前用户信息 |

#### PostController

**路径**：`/api/post`

| 端点 | 方法 | 说明 |
|------|------|------|
| `POST /add` | `addPost` | 发帖 |
| `POST /delete` | `deletePost` | 删帖 |
| `POST /update` | `updatePost` | 更新帖子 |
| `GET /get/vo` | `getPostVOById` | 帖子详情 |
| `POST /list/page/vo` | `listPostVOByPage` | 帖子分页列表 |
| `POST /my/list/page/vo` | `listMyPostVOByPage` | 我的帖子列表 |

#### CommentController

**路径**：`/api/comment`

支持二级评论（父评论 + 回复），提供评论的增删查和点赞功能。

#### ChatHistoryController

**路径**：`/api/chat_history`

提供对话历史的游标分页查询，基于 `(app_id, create_time)` 索引实现高效翻页。

#### WorkflowSseController

**路径**：`/api/workflow`

| 端点 | 方法 | 说明 |
|------|------|------|
| `POST /execute` | `executeWorkflow` | 同步执行工作流 |
| `GET /execute-flux` | `executeWorkflowWithFlux` | Flux 流式执行 |
| `GET /execute-sse` | `executeWorkflowWithSse` | SSE 流式执行 |

#### 其他 Controller

| Controller | 路径 | 说明 |
|------------|------|------|
| `FileController` | `/api/file` | 文件上传（COS） |
| `HealthController` | `/api/health` | 健康检查 |
| `PostCategoryController` | `/api/post_category` | 帖子分类管理 |
| `PostLikeController` | `/api/post_like` | 帖子点赞 |
| `CommentLikeController` | `/api/comment_like` | 评论点赞 |
| `StaticResourceController` | `/api/static` | 静态资源访问（应用预览） |

---

### 5.3 Service 层

#### AppService / AppServiceImpl

核心业务服务，实现应用的全生命周期管理。

| 方法 | 说明 |
|------|------|
| `chatToGenCode(appId, message, loginUser)` | AI 对话生成代码（核心方法） |
| `createApp(appAddRequest, loginUser)` | 创建应用（含 AI 路由选择生成类型） |
| `deployApp(appId, loginUser)` | 部署应用（含 Vue 项目构建） |
| `generateAppScreenshotAsync(appId, appUrl)` | 异步生成应用截图并更新封面 |
| `listGoodAppVOByPage(appQueryRequest)` | 精选应用列表（带 Redis 缓存） |
| `getAppVO(app)` / `getAppVOList(appList)` | 实体转 VO（批量优化 N+1 查询） |

**`chatToGenCode` 核心流程**：

1. 参数校验 + 权限校验
2. 保存用户消息到对话历史
3. 设置监控上下文
4. 调用 `AiCodeGeneratorFacade.generateAndSaveCodeStream()` 生成代码
5. 通过 `StreamHandlerExecutor` 处理流式响应并保存 AI 回复到对话历史
6. 清理监控上下文

#### UserService / UserServiceImpl

用户管理服务，提供注册、登录、Session 管理等功能。

#### PostService / PostServiceImpl

帖子管理服务，支持发帖、编辑、删除、分页查询等。

#### ChatHistoryService / ChatHistoryServiceImpl

对话历史服务，提供消息存储和游标分页查询，支持将数据库历史加载到 LangChain4j 的 ChatMemory 中。

#### ProjectDownloadService / ProjectDownloadServiceImpl

项目下载服务，将代码目录打包为 ZIP 文件并提供下载。

#### ScreenshotService / ScreenshotServiceImpl

截图服务，使用 Selenium WebDriver 对部署的应用进行网页截图并上传到 COS。

---

### 5.4 AI 代码生成核心模块

这是系统最核心的模块，位于 `ai/` 包，负责与 LLM 交互并生成代码。

#### AiCodeGeneratorService（接口）

AI 代码生成服务接口，基于 LangChain4j 的 `AiServices` 动态代理实现。

```java
public interface AiCodeGeneratorService {
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);
}
```

**三种生成模式**：

| 模式 | 枚举值 | 流式类型 | 工具调用 | 说明 |
|------|--------|----------|----------|------|
| HTML | `html` | `Flux<String>` | ❌ | 生成单个 HTML 文件 |
| MULTI_FILE | `multi_file` | `Flux<String>` | ❌ | 生成多文件代码 |
| VUE_PROJECT | `vue_project` | `TokenStream` | ✅ | 生成 Vue 工程（AI 通过工具写入文件） |

#### AiCodeGeneratorServiceFactory（工厂类）

负责创建和缓存 `AiCodeGeneratorService` 实例。

**关键设计**：

- 使用 **Caffeine 缓存**管理服务实例（最大 1000，写入 30 分钟过期）
- 每个应用（appId）+ 生成类型组合对应独立的服务实例
- 每个实例拥有独立的 **Redis 对话记忆**（`MessageWindowChatMemory`）
- 从数据库加载历史对话到记忆中（最近 40 条）
- Vue 项目模式注入所有 AI 工具、设置幻觉工具处理策略、最多 30 次连续工具调用
- 所有模式添加 **PromptSafetyInputGuardrail** 输入护轨

#### AiCodeGenTypeRoutingService（路由服务）

智能路由服务，根据用户输入的 prompt 自动选择代码生成类型。

```java
public interface AiCodeGenTypeRoutingService {
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
```

使用 LangChain4j 的结构化输出能力，直接返回 `CodeGenTypeEnum` 枚举值。

#### AI 工具体系

AI 工具用于 Vue 项目模式下的文件操作，位于 `ai/tools/` 包。

**工具继承体系**：

```
BaseTool (抽象基类)
├── FileWriteTool      → 写入文件
├── FileReadTool       → 读取文件
├── FileModifyTool     → 修改文件
├── FileDeleteTool     → 删除文件
├── FileDirReadTool    → 读取目录
└── ExitTool           → 退出工具调用循环
```

**BaseTool 抽象方法**：

| 方法 | 说明 |
|------|------|
| `getToolName()` | 工具英文名称 |
| `getDisplayName()` | 工具中文显示名称 |
| `generateToolRequestResponse()` | 生成工具请求时的前端展示内容 |
| `generateToolExecutedResult(arguments)` | 生成工具执行结果的格式化内容 |

**ToolManager**：统一管理所有工具，使用 `@PostConstruct` 自动注册，支持按名称获取工具实例。

#### AI 护轨（Guardrail）

| 护轨 | 类型 | 说明 |
|------|------|------|
| `PromptSafetyInputGuardrail` | 输入护轨 | 检查输入长度（≤1000字）、敏感词、注入攻击模式 |
| `RetryOutputGuardrail` | 输出护轨 | 输出重试（流式场景未启用） |

---

### 5.5 LangGraph4j 工作流模块

位于 `langgraph4j/` 包，基于 LangGraph4j 实现多步骤 AI 代码生成工作流。

#### 三种工作流实现

##### CodeGenWorkflow（串行工作流）

最基础的工作流，节点按顺序执行：

```
START → image_collector → prompt_enhancer → router → code_generator
      → code_quality_check → [条件路由] → project_builder → END
```

**条件路由逻辑**（`code_quality_check` 之后）：

| 条件 | 目标节点 | 说明 |
|------|----------|------|
| 质检失败 | `code_generator` | 重新生成代码 |
| 质检通过 + VUE_PROJECT | `project_builder` | 执行构建 |
| 质检通过 + HTML/MULTI_FILE | END | 直接结束 |

支持三种执行方式：同步执行、Flux 流式、SSE 流式。

##### CodeGenConcurrentWorkflow（并发工作流）

在串行工作流基础上，将图片收集拆分为并发执行：

```
START → image_plan ─┬→ content_image_collector ─┐
                    ├→ illustration_collector  ──┤
                    ├→ diagram_collector ────────┤→ image_aggregator → ...
                    └→ logo_collector ───────────┘
```

使用线程池（核心 10 / 最大 20）执行并发节点。

##### CodeGenSubgraphWorkflow（子图工作流）

与并发工作流类似，但将每个图片收集节点封装为独立的子图（Subgraph），实现更清晰的模块化。

#### 工作流节点

| 节点 | 类 | 说明 |
|------|-----|------|
| `ImageCollectorNode` | `node/ImageCollectorNode` | 图片收集（串行） |
| `ImagePlanNode` | `node/concurrent/ImagePlanNode` | 图片收集规划（并发） |
| `ContentImageCollectorNode` | `node/concurrent/ContentImageCollectorNode` | 内容图片收集 |
| `IllustrationCollectorNode` | `node/concurrent/IllustrationCollectorNode` | 插画收集 |
| `DiagramCollectorNode` | `node/concurrent/DiagramCollectorNode` | 架构图生成 |
| `LogoCollectorNode` | `node/concurrent/LogoCollectorNode` | Logo 生成 |
| `ImageAggregatorNode` | `node/concurrent/ImageAggregatorNode` | 图片聚合 |
| `PromptEnhancerNode` | `node/PromptEnhancerNode` | 提示词增强 |
| `RouterNode` | `node/RouterNode` | 生成类型路由 |
| `CodeGeneratorNode` | `node/CodeGeneratorNode` | 代码生成 |
| `CodeQualityCheckNode` | `node/CodeQualityCheckNode` | 代码质检 |
| `ProjectBuilderNode` | `node/ProjectBuilderNode` | 项目构建 |

#### 工作流状态 - WorkflowContext

工作流上下文，存储在 `MessagesState` 的 `data` 字段中，贯穿整个工作流执行过程。

| 字段 | 类型 | 说明 |
|------|------|------|
| `currentStep` | String | 当前执行步骤 |
| `originalPrompt` | String | 用户原始提示词 |
| `imageListStr` | String | 图片资源字符串 |
| `imageList` | List\<ImageResource\> | 图片资源列表 |
| `enhancedPrompt` | String | 增强后的提示词 |
| `generationType` | CodeGenTypeEnum | 代码生成类型 |
| `generatedCodeDir` | String | 生成代码目录 |
| `buildResultDir` | String | 构建结果目录 |
| `qualityResult` | QualityResult | 质检结果 |
| `errorMessage` | String | 错误信息 |
| `imageCollectionPlan` | ImageCollectionPlan | 图片收集计划 |

#### 工作流 AI 服务

| 服务 | 说明 |
|------|------|
| `ImageCollectionPlanService` | 图片收集规划 |
| `ImageCollectionService` | 图片收集 |
| `CodeQualityCheckService` | 代码质量检查 |

#### 工作流工具

| 工具 | 说明 |
|------|------|
| `ImageSearchTool` | 图片搜索 |
| `LogoGeneratorTool` | Logo 生成 |
| `MermaidDiagramTool` | Mermaid 图表生成 |
| `UndrawIllustrationTool` | Undraw 插画获取 |

---

### 5.6 Core 管道模块

位于 `core/` 包，实现代码生成的完整管道：**生成 → 解析 → 保存 → 构建**。

#### AiCodeGeneratorFacade（门面类）

代码生成的统一入口，组合了 AI 服务、解析器、保存器和构建器。

**核心方法**：

| 方法 | 说明 |
|------|------|
| `generateAndSaveCode(userMessage, codeGenTypeEnum, appId)` | 同步生成并保存代码 |
| `generateAndSaveCodeStream(userMessage, codeGenTypeEnum, appId)` | 流式生成并保存代码 |

**流式处理流程**：

- **HTML / MULTI_FILE**：`Flux<String>` → 实时收集代码片段 → 流完成后解析 → 保存
- **VUE_PROJECT**：`TokenStream` → 转换为 `Flux<String>` → AI 完成后触发 Vue 项目构建 → 推送构建状态

**TokenStream 事件处理**：

| 事件 | 处理 | 前端展示 |
|------|------|----------|
| `onPartialResponse` | AI 文本 Token | 实时代码展示 |
| `onPartialToolExecutionRequest` | 工具调用请求 | "AI 正在写入 XX 文件" |
| `onToolExecuted` | 工具执行完成 | 工具执行结果 |
| `onCompleteResponse` | AI 调用完成 | 触发 Vue 项目构建 |
| `onError` | 异常 | 错误提示 |

#### 代码解析器（Parser）

| 解析器 | 说明 |
|--------|------|
| `HtmlCodeParser` | 解析 HTML 单文件代码 |
| `MultiFileCodeParser` | 解析多文件代码（文件名 + 内容） |
| `CodeParserExecutor` | 解析器执行器，根据类型分发 |

#### 代码保存器（Saver）

| 保存器 | 说明 |
|--------|------|
| `HtmlCodeFileSaverTemplate` | HTML 代码保存模板 |
| `MultiFileCodeFileSaverTemplate` | 多文件代码保存模板 |
| `CodeFileSaverExecutor` | 保存器执行器，根据类型分发 |

#### 流处理器（Handler）

| 处理器 | 适用类型 | 说明 |
|--------|----------|------|
| `SimpleTextStreamHandler` | HTML / MULTI_FILE | 简单文本流处理，收集后保存到对话历史 |
| `JsonMessageStreamHandler` | VUE_PROJECT | JSON 消息流处理，解析各类事件并保存 |
| `StreamHandlerExecutor` | - | 流处理器分发器 |

#### 项目构建器（Builder）

**VueProjectBuilder**：Vue 项目构建器，执行 `npm install` + `npm run build`。

**构建状态枚举**：

| 状态 | 说明 |
|------|------|
| `INSTALLING` | 正在安装依赖 |
| `BUILDING` | 正在构建项目 |
| `COMPLETED` | 构建完成 |
| `FAILED` | 构建失败 |

---

### 5.7 数据模型层

#### 实体类 (Entity)

| 实体 | 表名 | 说明 |
|------|------|------|
| `User` | user | 用户 |
| `App` | app | 应用 |
| `ChatHistory` | chat_history | 对话历史 |
| `Post` | post | 帖子 |
| `PostCategory` | post_category | 帖子分类 |
| `Comment` | comment | 评论 |
| `PostLike` | post_like | 帖子点赞 |
| `CommentLike` | comment_like | 评论点赞 |

所有实体类使用 MyBatis-Flex 注解（`@Table`、`@Id`、`@Column`），主键采用雪花算法生成，支持逻辑删除（`is_delete` 字段）。

#### 视图对象 (VO)

| VO | 说明 |
|----|------|
| `UserVO` | 用户视图（脱敏） |
| `LoginUserVO` | 登录用户视图 |
| `AppVO` | 应用视图（含用户信息） |
| `PostVO` | 帖子视图 |
| `PostDetailVO` | 帖子详情视图 |
| `PostCategoryVO` | 分类视图 |
| `CommentVO` | 评论视图 |

#### 枚举类

| 枚举 | 说明 |
|------|------|
| `CodeGenTypeEnum` | 代码生成类型：HTML / MULTI_FILE / VUE_PROJECT |
| `UserRoleEnum` | 用户角色：USER / ADMIN |
| `ChatHistoryMessageTypeEnum` | 消息类型：USER / AI |

---

### 5.8 安全与权限模块

#### @AuthCheck 注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    String mustRole() default "";
}
```

#### AuthInterceptor（AOP 切面）

基于 Spring AOP 的 `@Around` 通知，拦截标注了 `@AuthCheck` 的方法：

1. 获取当前登录用户
2. 检查用户角色是否满足 `mustRole` 要求
3. 不满足则抛出 `BusinessException(NO_AUTH_ERROR)`

---

### 5.9 限流模块

#### @RateLimit 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";
    int rate() default 10;              // 时间窗口内允许的请求数
    int rateInterval() default 1;       // 时间窗口（秒）
    RateLimitType limitType() default RateLimitType.USER;
    String message() default "请求过于频繁，请稍后再试";
}
```

#### RateLimitAspect（限流切面）

基于 Redisson 的分布式限流实现，支持按用户或 IP 限流。

#### 限流类型

| 类型 | 说明 |
|------|------|
| `USER` | 按用户 ID 限流 |
| `IP` | 按客户端 IP 限流 |

---

### 5.10 监控模块

#### AiModelMetricsCollector

基于 Micrometer 的 AI 模型指标收集器，与 Prometheus 集成。

| 指标 | 类型 | 标签 |
|------|------|------|
| `ai_model_requests_total` | Counter | user_id, app_id, model_name, status |
| `ai_model_errors_total` | Counter | user_id, app_id, model_name, error_message |
| `ai_model_tokens_total` | Counter | user_id, app_id, model_name, token_type |
| `ai_model_response_duration_seconds` | Timer | user_id, app_id, model_name |

#### MonitorContext / MonitorContextHolder

基于 ThreadLocal 的监控上下文，存储当前请求的 userId 和 appId。

---

### 5.11 基础设施模块

#### CosManager

腾讯云 COS 对象存储管理器，提供文件上传功能。

#### WebScreenshotUtils / WebDriverHolder

基于 Selenium 的网页截图工具，用于生成应用封面图。

#### CursorUtils

游标分页工具，基于 `(create_time, id)` 实现高效的游标翻页。

#### SpringContextUtil

Spring 上下文工具类，支持在非 Spring 管理的类中获取 Bean 实例。

---

## 6. 前端模块详解

### 6.1 应用入口与配置

#### main.ts

```typescript
const app = createApp(App)
app.use(createPinia())   // 状态管理
app.use(router)           // 路由
app.use(Antd)             // Ant Design Vue
app.mount('#app')
```

导入 `@/access` 注册全局路由守卫。

#### 环境配置 (config/env.ts)

| 变量 | 开发环境 | 生产环境 | 说明 |
|------|----------|----------|------|
| `DEPLOY_DOMAIN` | `http://localhost` | `/dist` | 应用部署域名 |
| `API_BASE_URL` | `/api` | `/api` | API 基础地址 |

**关键函数**：

| 函数 | 说明 |
|------|------|
| `getDeployUrl(deployKey)` | 获取部署应用的完整 URL |
| `getStaticPreviewUrl(codeGenType, appId)` | 获取静态资源预览 URL（Vue 项目追加 `dist/index.html`） |

---

### 6.2 路由系统

#### 路由表

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | `HomePage` | 首页 |
| `/community` | `CommunityPage` | 交流社区 |
| `/post/create` | `PostCreatePage` | 发帖 |
| `/post/:id` | `PostDetailPage` | 帖子详情 |
| `/post/edit/:id` | `PostEditPage` | 编辑帖子 |
| `/user/login` | `UserLoginPage` | 用户登录 |
| `/user/register` | `UserRegisterPage` | 用户注册 |
| `/user/center` | `UserCenterPage` | 个人中心 |
| `/admin/userManage` | `UserManagePage` | 用户管理 |
| `/admin/appManage` | `AppManagePage` | 应用管理 |
| `/admin/postManage` | `PostManagePage` | 帖子管理 |
| `/admin/chatManage` | `ChatManagePage` | 对话管理 |
| `/app/chat/:id` | `AppChatPage` | 应用对话 |
| `/app/edit/:id` | `AppEditPage` | 编辑应用 |

---

### 6.3 页面组件

#### 首页 (HomePage)

展示精选应用列表，用户可浏览和创建新应用。

#### 应用对话页 (AppChatPage)

核心交互页面，用户与 AI 进行对话式代码生成。支持 SSE 流式接收 AI 响应，实时展示代码生成过程和工具调用信息。

#### 应用编辑页 (AppEditPage)

编辑应用基本信息，查看和修改应用配置。

#### 社区页 (CommunityPage)

展示帖子列表，支持按分类筛选和搜索。

#### 帖子详情页 (PostDetailPage)

展示帖子内容，支持评论和点赞。

#### 管理后台页面

| 页面 | 说明 |
|------|------|
| `UserManagePage` | 用户管理（查看、封禁） |
| `AppManagePage` | 应用管理（查看、删除、设为精选） |
| `PostManagePage` | 帖子管理（查看、删除、置顶） |
| `ChatManagePage` | 对话管理（查看对话历史） |

---

### 6.4 共享组件

| 组件 | 说明 |
|------|------|
| `GlobalHeader` | 全局头部导航（含用户信息、菜单） |
| `GlobalFooter` | 全局底部（版权信息） |
| `AppCard` | 应用卡片（展示应用信息） |
| `AppDetailModal` | 应用详情弹窗 |
| `DeploySuccessModal` | 部署成功弹窗 |
| `MarkdownRenderer` | Markdown 渲染器（代码高亮） |
| `CommentList` | 评论列表 |
| `CommentItem` | 评论项 |
| `CommentInput` | 评论输入框 |
| `CommentEditor` | 评论富文本编辑器 |
| `UserInfo` | 用户信息展示 |

---

### 6.5 API 层

API 接口文件通过 `openapi2ts` 工具从后端 OpenAPI 文档自动生成，位于 `api/` 目录。

| 文件 | 说明 |
|------|------|
| `userController.ts` | 用户相关 API |
| `appController.ts` | 应用相关 API |
| `chatHistoryController.ts` | 对话历史 API |
| `postController.ts` | 帖子相关 API |
| `commentController.ts` | 评论相关 API |
| `commentLikeController.ts` | 评论点赞 API |
| `postCategoryController.ts` | 帖子分类 API |
| `fileController.ts` | 文件上传 API |
| `staticResourceController.ts` | 静态资源 API |
| `healthController.ts` | 健康检查 API |

#### request.ts（Axios 封装）

- `baseURL`：从环境配置读取
- `timeout`：60 秒
- `withCredentials`：true（携带 Cookie）
- **响应拦截器**：检测 `code === 40100`（未登录），自动跳转登录页

---

### 6.6 状态管理

#### loginUser Store

```typescript
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({ userName: '未登录' })

  async function fetchLoginUser() { ... }  // 从后端获取登录用户信息
  function setLoginUser(newLoginUser) { ... }  // 更新登录用户信息

  return { loginUser, fetchLoginUser, setLoginUser }
})
```

---

### 6.7 权限控制

#### access.ts（路由守卫）

```typescript
router.beforeEach(async (to, from, next) => {
  // 首次加载时等待后端返回用户信息
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    firstFetchLoginUser = false
  }
  // /admin 路径需要管理员权限
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
```

---

### 6.8 工具函数

| 文件 | 说明 |
|------|------|
| `codeGenTypes.ts` | 代码生成类型枚举和格式化函数 |
| `time.ts` | 时间处理工具 |
| `visualEditor.ts` | 可视化编辑器工具 |

---

## 7. 数据库设计

数据库名：`saki_ai_code_tool`，字符集：`utf8mb4_unicode_ci`

### ER 关系图

```
┌──────────┐       ┌──────────┐       ┌──────────────┐
│   User   │──1:N──│   App    │──1:N──│ ChatHistory  │
└────┬─────┘       └──────────┘       └──────────────┘
     │
     │ 1:N
     │
┌────▼─────┐       ┌──────────────┐       ┌──────────────┐
│   Post   │──1:N──│   Comment    │       │  PostLike    │
└────┬─────┘       └──────┬───────┘       └──────────────┘
     │                    │
     │                    │ 1:N
     │              ┌────▼───────────┐
     │              │  CommentLike   │
     │              └────────────────┘
     │
┌────▼──────────┐
│ PostCategory  │
└───────────────┘
```

### 表结构详情

#### user 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint AUTO_INCREMENT | 主键 |
| user_account | varchar(256) | 账号（唯一） |
| user_password | varchar(512) | 密码 |
| user_name | varchar(256) | 昵称 |
| user_avatar | varchar(1024) | 头像 |
| user_profile | varchar(512) | 简介 |
| user_role | varchar(256) | 角色（user/admin） |
| is_delete | tinyint | 逻辑删除 |

#### app 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint AUTO_INCREMENT | 主键 |
| app_name | varchar(256) | 应用名称 |
| cover | varchar(512) | 封面 |
| init_prompt | text | 初始化 Prompt |
| code_gen_type | varchar(64) | 生成类型（html/multi_file/vue_project） |
| deploy_key | varchar(64) | 部署标识（唯一） |
| deployed_time | datetime | 部署时间 |
| priority | int | 优先级（99=精选） |
| user_id | bigint | 创建用户 ID |

#### chat_history 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint AUTO_INCREMENT | 主键 |
| message | text | 消息内容 |
| message_type | varchar(32) | 消息类型（user/ai） |
| app_id | bigint | 应用 ID |
| user_id | bigint | 用户 ID |

索引：`idx_appId_createTime(app_id, create_time)` — 游标查询核心索引

#### post 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint AUTO_INCREMENT | 主键 |
| user_id | bigint | 发帖用户 ID |
| category_id | bigint | 分类 ID |
| title | varchar(512) | 标题（全文索引） |
| content | longtext | 内容（富文本 HTML） |
| view_count | int | 浏览量 |
| like_count | int | 点赞数 |
| comment_count | int | 评论数 |
| is_top | tinyint | 是否置顶 |
| first_image_url | varchar(1024) | 首图地址 |

#### comment 表

支持二级评论，通过 `parent_comment_id` 和 `reply_user_id` 实现评论嵌套和回复功能。

#### post_like / comment_like 表

点赞记录表，使用联合唯一键防止重复点赞，`is_delete` 字段实现软删除（取消点赞）。

---

## 8. 核心数据流

### 8.1 AI 对话生成代码流程

```
用户输入 Prompt
       │
       ▼
  AppController.chatToGenCode()
       │
       ├── 1. 参数校验 + 权限校验
       ├── 2. 保存用户消息到 chat_history
       ├── 3. 设置 MonitorContext
       │
       ▼
  AppServiceImpl.chatToGenCode()
       │
       ▼
  AiCodeGeneratorFacade.generateAndSaveCodeStream()
       │
       ├── [HTML / MULTI_FILE 模式]
       │       │
       │       ▼
       │   AiCodeGeneratorService.generateHtmlCodeStream()
       │   或 generateMultiFileCodeStream()
       │       │  (Flux<String>)
       │       ▼
       │   processCodeStream()
       │       ├── 实时收集代码片段 → 推送给前端
       │       └── 流完成后 → CodeParserExecutor → CodeFileSaverExecutor
       │
       └── [VUE_PROJECT 模式]
               │
               ▼
           AiCodeGeneratorService.generateVueProjectCodeStream()
               │  (TokenStream)
               ▼
           processTokenStream()
               ├── onPartialResponse → AI 文本 Token → 前端
               ├── onPartialToolExecutionRequest → 工具调用请求 → 前端
               ├── onToolExecuted → 工具执行结果 → 前端
               ├── onCompleteResponse → 触发 VueProjectBuilder
               │       ├── npm install
               │       └── npm run build
               └── onError → 错误处理
       │
       ▼
  StreamHandlerExecutor.doExecute()
       ├── SimpleTextStreamHandler (HTML/MULTI_FILE)
       └── JsonMessageStreamHandler (VUE_PROJECT)
       │
       ├── 保存 AI 回复到 chat_history
       └── 清理 MonitorContext
```

### 8.2 应用创建流程

```
用户提交 initPrompt
       │
       ▼
  AppController.addApp()
       │
       ▼
  AppServiceImpl.createApp()
       │
       ├── 1. 参数校验
       ├── 2. AiCodeGenTypeRoutingService.routeCodeGenType(initPrompt)
       │       └── AI 智能选择生成类型 → CodeGenTypeEnum
       ├── 3. 构造 App 实体
       │       ├── appName = initPrompt 前 12 位
       │       └── codeGenType = AI 选择的结果
       └── 4. 保存到数据库
```

### 8.3 应用部署流程

```
用户点击部署
       │
       ▼
  AppServiceImpl.deployApp()
       │
       ├── 1. 参数校验 + 权限校验
       ├── 2. 生成/获取 deployKey（6位随机字符串）
       ├── 3. 获取代码生成路径
       ├── 4. [VUE_PROJECT] VueProjectBuilder.buildProject()
       │       ├── npm install
       │       └── npm run build → dist/
       ├── 5. 复制文件到部署目录 (tmp/code_deploy/{deployKey})
       ├── 6. 更新数据库 deployKey + deployedTime
       ├── 7. 构建访问 URL → {deployHost}/{deployKey}/
       └── 8. 异步截图 → ScreenshotService → 更新封面
```

---

## 9. 依赖关系图

### 后端模块依赖

```
Controller 层
    │
    ├── Service 层
    │       │
    │       ├── AiCodeGeneratorFacade (门面)
    │       │       ├── AiCodeGeneratorServiceFactory
    │       │       │       ├── ChatModel / StreamingChatModel
    │       │       │       ├── RedisChatMemoryStore
    │       │       │       ├── ChatHistoryService
    │       │       │       └── ToolManager → BaseTool[]
    │       │       ├── CodeParserExecutor → HtmlCodeParser / MultiFileCodeParser
    │       │       ├── CodeFileSaverExecutor → HtmlCodeFileSaverTemplate / MultiFileCodeFileSaverTemplate
    │       │       └── VueProjectBuilder
    │       │
    │       ├── AiCodeGenTypeRoutingServiceFactory
    │       │       └── RoutingChatModel
    │       │
    │       ├── StreamHandlerExecutor
    │       │       ├── SimpleTextStreamHandler
    │       │       └── JsonMessageStreamHandler
    │       │
    │       ├── ScreenshotService → Selenium + CosManager
    │       └── ProjectDownloadService
    │
    ├── Mapper 层 (MyBatis-Flex)
    │       └── MySQL
    │
    ├── AuthInterceptor (@AuthCheck)
    ├── RateLimitAspect (@RateLimit) → Redisson
    └── AiModelMetricsCollector → Micrometer → Prometheus
```

### 前端模块依赖

```
main.ts
    ├── Vue App
    ├── Pinia Store
    │       └── loginUser → API (userController)
    ├── Router
    │       └── access.ts (路由守卫)
    └── Ant Design Vue

页面组件
    ├── API 层 (api/*Controller.ts) → request.ts (Axios)
    ├── 共享组件 (components/*)
    ├── 工具函数 (utils/*)
    └── 环境配置 (config/env.ts)
```

---

## 10. 项目运行方式

### 环境要求

| 依赖 | 版本 |
|------|------|
| Java | 21+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Maven | 3.8+ |

### 后端启动

1. **初始化数据库**

```bash
mysql -u root -p < saki-ai-code-tool-backend/sql/create_table.sql
```

2. **配置应用**

编辑 `saki-ai-code-tool-backend/src/main/resources/application.yml`：

- 修改 MySQL 连接信息（url、username、password）
- 修改 Redis 连接信息（host、port、password）
- 配置 AI 模型 API Key（通过 Spring profiles 或环境变量）

3. **构建并运行**

```bash
cd saki-ai-code-tool-backend
./mvnw spring-boot:run
```

或打包后运行：

```bash
./mvnw clean package -DskipTests
java -jar target/saki-ai-code-tool-backend-0.0.1-SNAPSHOT.jar
```

后端启动在 `http://localhost:8123/api`，API 文档访问 `http://localhost:8123/api/doc.html`。

### 前端启动

1. **安装依赖**

```bash
cd saki-ai-code-mother-frontend
npm install
```

2. **生成 API 类型定义**（可选，需要后端运行）

```bash
npm run openapi2ts
```

3. **开发模式运行**

```bash
npm run dev
```

前端开发服务器默认启动在 `http://localhost:5173`，通过 Vite 代理将 `/api` 请求转发到后端 `http://localhost:8123`。

4. **生产构建**

```bash
npm run build
```

### 监控

- **Prometheus**：后端暴露 `/api/actuator/prometheus` 端点
- **Grafana**：配置文件位于 `saki-ai-code-tool-backend/grafana/ai_model_grafana_config.json`

### 关键文件路径

| 路径 | 说明 |
|------|------|
| `tmp/code_output/` | AI 生成的代码输出目录 |
| `tmp/code_deploy/` | 应用部署目录 |
| `src/main/resources/prompt/` | AI Prompt 模板文件 |
