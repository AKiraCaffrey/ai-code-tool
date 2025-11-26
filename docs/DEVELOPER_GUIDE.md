# 开发者指南（Developer Guide）

> 帮助新成员在 15 分钟内完成环境准备、运行与协作。

## 1. 开发环境要求
- Node.js >= 18，NPM >= 9 或 PNPM >= 8
- Java 17（推荐），Gradle/Maven 对应版本
- MySQL 8.0+，Redis 6.2+
- Git，Docker（可选，用于本地编排）

## 2. 本地启动步骤
1. **克隆仓库**：`git clone <repo>`，进入项目根目录。
2. **启动后端**（在 `saki-ai-code-tool-backend`）
   - 配置 `src/main/resources/application.yml` 中的数据库/Redis 连接。
   - 运行 `./gradlew bootRun` 或 `mvn spring-boot:run`。
3. **启动前端**（在 `saki-ai-code-mother-frontend`）
   - 运行 `npm install` 或 `pnpm install`。
   - 执行 `npm run dev`，默认访问 `http://localhost:5173`。

## 3. 配置文件说明
- **application.yml**（后端）：
  - `spring.datasource`：MySQL 连接、池配置。
  - `spring.redis`：Redis 主机、端口、密码。
  - `spring.security`：JWT/鉴权相关。
  - `mq`：消息队列配置（如使用）。
- **.env / .env.local**（前端）：
  - `VITE_API_BASE_URL`：后端 API 网关地址。
  - `VITE_APP_NAME`：站点标题、埋点标识。

## 4. 项目目录结构
```
./
├─ docs/                      # 开发与设计文档
├─ saki-ai-code-tool-backend/ # Spring Boot 后端
│  ├─ src/main/java/
│  ├─ src/main/resources/
│  └─ build.gradle / pom.xml
└─ saki-ai-code-mother-frontend/ # Vue 前端
   ├─ src/
   ├─ public/
   └─ package.json
```

## 5. 常用脚本
- **后端 Gradle**：`./gradlew bootRun`、`./gradlew test`、`./gradlew build`。
- **后端 Maven**：`mvn spring-boot:run`、`mvn test`、`mvn package`。
- **前端 NPM**：`npm run dev`、`npm run build`、`npm run lint`。
- **前端 PNPM**：`pnpm dev`、`pnpm build`、`pnpm lint`。

## 6. Git 分支规范
- `main`：稳定发布分支。
- `dev`：日常集成分支。
- `feature/*`：功能分支，如 `feature/order-coupon`。

## 7. Commit Message 规范（Conventional Commit）
- 格式：`<type>(scope): <subject>`
- 常用 type：`feat`、`fix`、`docs`、`chore`、`refactor`、`test`、`style`、`perf`。
- 示例：`feat(order): support coupon apply`

## 8. 新功能开发流程
1. 从 `dev` 创建 `feature/*` 分支。
2. 开发与自测（遵循代码风格、添加必要测试）。
3. 提交符合规范的 Commit，推送到远端。
4. 创建 PR，填写变更摘要、测试结果，等待 Code Review。
5. Review 通过后合并到 `dev`，发布时从 `dev` 合并到 `main`。

## 9. 新增接口指南（Controller → Service → Mapper）
1. 在 `controller` 定义请求/响应模型，添加鉴权与参数校验。
2. 在 `service` 实现业务逻辑，控制事务与幂等。
3. 在 `manager` 处理跨域/缓存/消息等基础设施调用。
4. 在 `mapper` 定义 SQL/Mapper XML，确保索引覆盖与分页。必要时更新 `domain` 实体。
5. 补充集成/单元测试，并在 `api/`（前端）更新接口封装。

## 10. 运行测试
- 后端：`./gradlew test` 或 `mvn test`。
- 前端：`npm run test`（如使用 Vitest/Jest）。
- 接口测试：使用 `npm run dev` + Postman/Apifox，或后端集成测试脚本。

## 11. Code Review 要求
- PR 需附带变更摘要、测试结果与影响范围。
- 代码需通过自动化检查（CI、lint、test）。
- 核对安全与稳定性：鉴权、幂等、异常处理、日志与监控。
- 对公共接口与核心逻辑需至少两名 Reviewer 审阅。
