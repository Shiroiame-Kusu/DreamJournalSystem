# 梦境日记系统 (Dream Journal System)

<p align="center">
  <img src="docs/assets/logo.png" alt="Dream Journal Logo" width="120">
</p>

<p align="center">
  🌸 一个充满梦幻色彩的梦境记录与AI分析系统
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vue.js" alt="Vue 3.4">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=spring-boot" alt="Spring Boot 3.2">
  <img src="https://img.shields.io/badge/TypeScript-5.3-3178C6?logo=typescript" alt="TypeScript 5.3">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" alt="MySQL 8.0">
</p>

## ✨ 功能特性

### 🌙 核心功能
- **梦境记录** - 记录每一个梦境的详细内容、心情、睡眠质量
- **AI 智能分析** - 基于 "任何openai api兼容" ai模型 的梦境深度解读，包含象征意义、情绪分析、主题提取
- **标签管理** - 自定义标签，轻松分类和检索梦境
- **收藏系统** - 收藏有意义的梦境，随时回顾

### 🔐 用户系统
- JWT 安全认证
- 用户注册/登录/登出
- 个人资料管理
- 密码修改

### 👨‍💼 管理功能
- 用户管理（禁用/解禁/删除）
- 数据统计

### 🎨 Sakurairo 主题
- 樱花粉紫渐变色系
- 玻璃拟态设计风格
- 响应式布局
- 流畅动画效果
- 飘落的樱花背景

## 🛠️ 技术栈

### 前端
- **框架**: Vue 3.4 + TypeScript 5.3
- **构建工具**: Vite 5.0
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP 客户端**: Axios
- **样式**: SCSS + Sakurairo Style

### 后端
- **框架**: Spring Boot 3.2.1
- **安全**: Spring Security 6 + JWT
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **AI**: OpenAI GPT-4 API

## 📁 项目结构

```
DreamJournalSystem/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API 调用模块
│   │   ├── assets/             # 静态资源
│   │   │   └── styles/         # SCSS 样式
│   │   ├── components/         # Vue 组件
│   │   │   ├── common/         # 通用组件
│   │   │   └── ui/             # UI 组件库
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── types/              # TypeScript 类型定义
│   │   └── views/              # 页面视图
│   ├── package.json
│   └── vite.config.ts
│
├── src/main/java/              # 后端 Java 代码
│   └── icu/nyat/dreamjournalsystem/
│       ├── config/             # 配置类
│       ├── controller/         # 控制器
│       ├── dto/                # 数据传输对象
│       ├── entity/             # 实体类
│       ├── exception/          # 异常处理
│       ├── mapper/             # MyBatis 映射器
│       ├── security/           # 安全相关
│       └── service/            # 服务层
│
├── docs/                       # 文档目录
│   ├── ARCHITECTURE.md         # 系统架构
│   ├── DATABASE.md             # 数据库设计
│   ├── API.md                  # API 文档
│   ├── AI_PROMPT.md            # AI 提示词
│   └── UI_DESIGN.md            # UI 设计文档
│
├── pom.xml                     # Maven 配置
└── README.md                   # 项目说明
```

## 🚀 快速开始

### 环境要求

- **Node.js** >= 18.0
- **Java** >= 17
- **MySQL** >= 8.0
- **Redis** >= 6.0
- **Maven** >= 3.8

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/DreamJournalSystem.git
cd DreamJournalSystem
```

### 2. 配置数据库

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE dream_journal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置后端

1. 复制配置文件：

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

2. 修改 `application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dream_journal
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379

openai:
  api-key: your_openai_api_key
  base-url: https://api.openai.com
```

### 4. 启动后端

```bash
mvn spring-boot:run
```

后端服务将运行在 `http://localhost:8081`

### 5. 配置前端

```bash
cd frontend
npm install
```

### 6. 启动前端

```bash
npm run dev
```

前端服务将运行在 `http://localhost:5173`

### 7. 访问应用

打开浏览器访问 `http://localhost:5173`

## 📝 API 文档

详细的 API 文档请参考 [docs/API.md](docs/API.md)

### 主要接口

| 模块 | 接口 | 说明 |
|------|------|------|
| 认证 | `POST /api/auth/login` | 用户登录 |
| 认证 | `POST /api/auth/register` | 用户注册 |
| 梦境 | `GET /api/dreams` | 获取梦境列表 |
| 梦境 | `POST /api/dreams` | 创建梦境 |
| 梦境 | `POST /api/dreams/{id}/ai-summary` | 生成 AI 分析 |

## 🎨 设计规范

### Sakurairo 配色

| 颜色 | 色值 | 用途 |
|------|------|------|
| 樱花粉 | `#FFB7C5` | 主色调 |
| 薰衣草紫 | `#E0BBE4` | 次色调 |
| 柔紫色 | `#B8A9C9` | 强调色 |

详细设计规范请参考 [docs/UI_DESIGN.md](docs/UI_DESIGN.md)

## 🔧 开发指南

### 前端开发

```bash
cd frontend

# 开发模式
npm run dev

# 构建
npm run build

# 代码检查
npm run lint

# 类型检查
npm run type-check
```

### 后端开发

```bash
# 运行测试
mvn test

# 打包
mvn package -DskipTests

# 运行 JAR
java -jar target/dream-journal-system-1.0.0.jar
```

## 📄 许可证

本项目采用 [GPL-3.0 许可证](LICENSE)

---

<p align="center">
  🌸 愿你今夜好梦 🌸
</p>
