# Voice Accounting（语音记账）

基于语音交互的个人记账系统，支持多账本、收支分类、交易记录与提醒管理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 4.0.6 + Java 25 + MyBatis |
| 前端 | Vue 3.5 + Vite + Ant Design Vue 4.x + Pinia |
| 数据库 | MySQL 8.0 + Redis |
| 认证 | Sa-Token |
| 部署 | Docker Compose |

## 功能模块

- **用户体系** — 注册 / 登录，区分管理员与普通用户角色
- **账本管理** — 创建与管理多个独立账本
- **分类管理** — 自定义收支类别
- **记账功能** — 录入、查看、编辑交易记录
- **提醒功能** — 设置账单/还款等周期性提醒
- **管理后台** — 用户管理、全局分类/账本/交易/提醒管理

## 项目预览

| 首页仪表盘 | 账本管理 |
|------------|----------|
| ![首页](preview/Snipaste_2026-08-21_11-48-01.png) | ![账本](preview/Snipaste_2026-08-21_11-48-21.png) |

| 语音/手动记账 | 交易记录 |
|---------------|----------|
| ![记账](preview/Snipaste_2026-08-21_11-48-26.png) | ![交易记录](preview/Snipaste_2026-08-21_11-48-34.png) |

| 分类管理 | 提醒管理 |
|----------|----------|
| ![分类](preview/Snipaste_2026-08-21_11-48-39.png) | ![提醒](preview/Snipaste_2026-08-21_11-48-52.png) |

## 项目结构

```
voice-accounting/
├── backend/          # Spring Boot 后端
│   └── src/main/java/com/hfh/api/
│       ├── admin/    # 管理员接口
│       ├── user/     # 用户接口
│       ├── entity/   # 实体类
│       ├── service/  # 业务逻辑
│       └── mapper/   # MyBatis Mapper
├── frontend/         # Vue 3 前端
│   └── src/
│       ├── api/      # API 请求封装
│       ├── views/    # 页面视图
│       ├── stores/   # Pinia 状态管理
│       └── router/   # 路由配置
├── docker-compose.yml
└── .env              # 环境变量配置
```

## 快速启动

### 1. 配置环境变量

复制或编辑 `.env` 文件，配置数据库连接信息。

### 2. 启动基础服务

```bash
docker compose up -d mysql redis
```

### 3. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```
