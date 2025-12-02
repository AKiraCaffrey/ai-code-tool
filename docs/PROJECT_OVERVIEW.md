# 项目概述

## 项目简介
- **项目背景**：本项目面向 AI 编程赛道，提供企业级的 AI 代码生成与应用搭建平台，结合 LangChain4j / LangGraph4j 等大模型工具链，让用户通过自然语言快速生成、预览并迭代 Web 应用。项目由“鱼皮”团队开源，旨在教学与实战并重，帮助开发者掌握 AI 智能体、AI 工作流和后端架构设计。 
- **项目目标**：构建一套对标大厂的 AI 应用生成平台，覆盖需求输入、智能生成、实时预览、可视化编辑、部署分享以及后台运营管理的全流程体验，降低前后端开发门槛。 
- **主要功能**：
  - 智能代码生成与流式输出：用户输入提示词，AI 自动生成应用并实时展示过程。
  - 可视化编辑与对话迭代：在预览画面中选择元素并与 AI 对话修改页面。
  - 一键部署与分享：将生成的应用部署到云端，自动生成可访问链接与封面截图，并支持源码下载。
  - 用户与应用管理：个人应用创建、修改、删除、查看，管理员额外支持批量查询、编辑、精选与删除应用。
  - 监控与运营：提供系统监控、AI 调用指标及业务指标看板，辅助平台稳定运行。
- **定位 / 目标用户**：定位为 AI 时代的全栈教学与实战平台，目标用户包括希望提升 AI 开发能力的后端 / 全栈工程师、对 AI 应用生成感兴趣的个人开发者，以及需要快速验证 AI 产品想法的团队。

## 技术栈
- **后端**：Spring Boot 3、LangChain4j、LangGraph4j、Reactor、Knife4j；MyBatis-Flex 持久层；Lombok；基于 Java 21。 
- **AI / 工具链**：OpenAI / DashScope 接入、LangGraph 工作流、Redis 社区组件、Selenium + WebDriverManager 进行网页截图、COS 对象存储。 
- **缓存与会话**：Redis + Spring Session，Redisson 分布式特性，本地使用 Caffeine 作为多级缓存。 
- **数据库**：MySQL（HikariCP 连接池）。 
- **监控与运维**：Spring Boot Actuator、Micrometer + Prometheus、Grafana 仪表盘。 
- **前端**：Vue 3 + TypeScript、Vite、Ant Design Vue、Pinia、Vue Router、Axios、Day.js、ESLint + Prettier。 
- **构建与工具**：Maven（后端）、npm（前端）。

## 系统架构
- **架构类型**：前后端分离的单体后端架构。前端提供用户界面与管理控制台，后端单体服务承载 API、AI 工具编排、业务逻辑与数据存储。 
- **关键组件设计**：
  - **API 层**：基于 Spring MVC 提供用户、应用、对话、部署等 RESTful 接口，并通过 Knife4j 提供接口文档。
  - **服务层**：封装 AI 生成策略、LangGraph 工作流编排、缓存管理、部署与截图等核心能力，利用 Redisson / Caffeine / Redis 组合实现多级缓存与分布式能力。
  - **数据层**：MyBatis-Flex 连接 MySQL，HikariCP 负责连接池管理；会话信息通过 Spring Session Redis 存储。
  - **AI 与工具集成**：通过 LangChain4j / LangGraph4j 对接大模型与工具调用，集成 DashScope / OpenAI、网页截图（Selenium）、对象存储（COS）等周边能力。
  - **监控与运维**：Actuator 暴露健康与指标，Micrometer 推送 Prometheus，Grafana 负责可视化监控；额外提供业务指标与 AI 调用监控面板。 
- **数据流 / 模块交互**：
  1. 用户在前端输入需求或与 AI 对话；前端通过 Axios 调用后端 API。
  2. 后端 API 层接收请求，调用服务层触发 LangGraph 工作流与 LangChain4j 工具链，实时生成或修改页面代码，并使用 SSE/流式响应反馈前端。
  3. 生成结果存储在 MySQL，对话与会话状态存入 Redis；热点数据通过 Caffeine / Redis 缓存加速。
  4. 部署与截图任务调用 Selenium 与对象存储，生成可分享链接与封面图。
  5. 监控链路通过 Actuator + Micrometer 上报到 Prometheus，并在 Grafana 中展示。
- **可视化架构示意**：
  - 前端：Vue 3 SPA（用户侧 + 管理端）。
  - 后端：Spring Boot 单体应用，内含 API 层、AI 编排层、业务服务层、数据访问层、缓存与会话层。
  - 基础设施：MySQL、Redis、对象存储（COS）、Prometheus + Grafana 监控、部署与截图服务。
