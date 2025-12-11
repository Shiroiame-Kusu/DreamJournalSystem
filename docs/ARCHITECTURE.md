# 梦境日记系统 - 系统架构文档

## 一、系统总体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户层 (Client Layer)                           │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Vite + Vue 3 + TypeScript                         │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │   │
│  │  │ 登录页面 │ │ 注册页面 │ │ 梦境记录 │ │ 梦境详情 │ │ 管理后台 │   │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘   │   │
│  │                    Sakurairo 樱落主题风格                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      │ HTTPS / REST API
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            网关层 (Gateway Layer)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │  Nginx 反向代理  │  │  CORS 跨域处理  │  │  Rate Limiting 请求限流     │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          应用层 (Application Layer)                          │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Spring Boot 2.7.x / 3.x                         │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────────────┐    │   │
│  │  │ 认证控制器    │  │ 梦境控制器    │  │ 管理员控制器          │    │   │
│  │  │ AuthController│  │DreamController│  │ AdminController       │    │   │
│  │  └───────────────┘  └───────────────┘  └───────────────────────┘    │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────────────┐    │   │
│  │  │  JWT 过滤器   │  │ 权限拦截器    │  │ 全局异常处理          │    │   │
│  │  └───────────────┘  └───────────────┘  └───────────────────────┘    │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────────────┐    │   │
│  │  │  用户服务     │  │  梦境服务     │  │  AI 总结服务          │    │   │
│  │  │ UserService   │  │ DreamService  │  │ AISummaryService      │    │   │
│  │  └───────────────┘  └───────────────┘  └───────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    ▼                 ▼                 ▼
┌───────────────────────┐ ┌───────────────────┐ ┌───────────────────────────┐
│    数据层 (Data)      │ │  缓存层 (Cache)   │ │   AI服务层 (AI Service)   │
├───────────────────────┤ ├───────────────────┤ ├───────────────────────────┤
│  ┌─────────────────┐  │ │ ┌───────────────┐ │ │ ┌───────────────────────┐ │
│  │     MySQL       │  │ │ │    Redis      │ │ │ │   DeepSeek API        │ │
│  │   - users       │  │ │ │ - JWT Token   │ │ │ │   (via Retrofit)      │ │
│  │   - dreams      │  │ │ │ - Rate Limit  │ │ │ │   异步轮询生成        │ │
│  │   - ai_summaries│  │ │ │ - Session     │ │ │ └───────────────────────┘ │
│  └─────────────────┘  │ │ └───────────────┘ │ └───────────────────────────┘
└───────────────────────┘ └───────────────────┘
```

## 二、技术栈选择说明

### 后端技术栈：Spring Boot

**选择理由：**

1. **成熟稳定**：Spring Boot 是 Java 生态中最成熟的框架，拥有完善的生态系统
2. **安全性强**：Spring Security 提供企业级的安全解决方案
3. **性能优秀**：相比 Node.js，Java 在高并发场景下表现更稳定
4. **团队适配**：根据现有项目结构，已采用 Spring Boot
5. **丰富的中间件支持**：JWT、MyBatis Plus、Redis 等集成方便

### 完整技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 前端框架 | Vue 3 + Vite | 3.4+ / 5.0+ |
| 前端语言 | TypeScript | 5.0+ |
| 状态管理 | Pinia | 2.1+ |
| 路由管理 | Vue Router | 4.2+ |
| HTTP 客户端 | Axios | 1.6+ |
| 后端框架 | Spring Boot | 3.2+ |
| 安全框架 | Spring Security | 6.2+ |
| ORM 框架 | MyBatis Plus | 3.5+ |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |
| JWT | jjwt | 0.12+ |
| 密码加密 | BCrypt | - |
| AI 服务 | DeepSeek API (Retrofit) | deepseek-chat |

## 三、目录结构

### 后端目录结构

```
src/main/java/icu/nyat/dreamjournalsystem/
├── DreamJournalSystemApplication.java      # 启动类
├── config/                                  # 配置类
│   ├── SecurityConfig.java                 # Spring Security 配置
│   ├── JwtConfig.java                      # JWT 配置
│   ├── CorsConfig.java                     # 跨域配置
│   ├── RedisConfig.java                    # Redis 配置
│   └── OpenAIConfig.java                   # AI 服务配置
├── controller/                             # 控制器层
│   ├── AuthController.java                 # 认证控制器
│   ├── UserController.java                 # 用户控制器
│   ├── DreamController.java                # 梦境控制器
│   └── AdminController.java                # 管理员控制器
├── service/                                # 服务层
│   ├── AuthService.java
│   ├── UserService.java
│   ├── DreamService.java
│   ├── AISummaryService.java
│   └── impl/                               # 服务实现
│       ├── AuthServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── DreamServiceImpl.java
│       └── AISummaryServiceImpl.java
├── mapper/                                 # MyBatis Mapper
│   ├── UserMapper.java
│   ├── DreamMapper.java
│   └── AISummaryMapper.java
├── entity/                                 # 实体类
│   ├── User.java
│   ├── Dream.java
│   └── AISummary.java
├── dto/                                    # 数据传输对象
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── DreamRequest.java
│   └── response/
│       ├── ApiResponse.java
│       ├── UserResponse.java
│       └── DreamResponse.java
├── security/                               # 安全相关
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── exception/                              # 异常处理
│   ├── GlobalExceptionHandler.java
│   ├── BusinessException.java
│   └── ErrorCode.java
└── util/                                   # 工具类
    ├── PasswordUtil.java
    └── ValidationUtil.java
```

### 前端目录结构

```
frontend/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── api/                               # API 接口
│   │   ├── auth.ts
│   │   ├── dream.ts
│   │   ├── user.ts
│   │   └── admin.ts
│   ├── assets/                            # 静态资源
│   │   ├── images/
│   │   └── styles/
│   │       ├── variables.scss             # Sakurairo 配色变量
│   │       ├── global.scss
│   │       └── animations.scss
│   ├── components/                        # 通用组件
│   │   ├── common/
│   │   │   ├── SakuraButton.vue
│   │   │   ├── SakuraInput.vue
│   │   │   ├── SakuraCard.vue
│   │   │   ├── SakuraModal.vue
│   │   │   └── SakuraNavbar.vue
│   │   ├── dream/
│   │   │   ├── DreamInputCard.vue
│   │   │   ├── DreamListItem.vue
│   │   │   └── AISummaryCard.vue
│   │   └── layout/
│   │       ├── MainLayout.vue
│   │       └── AdminLayout.vue
│   ├── composables/                       # 组合式函数
│   │   ├── useAuth.ts
│   │   └── useDream.ts
│   ├── router/                            # 路由配置
│   │   └── index.ts
│   ├── stores/                            # Pinia 状态管理
│   │   ├── auth.ts
│   │   ├── dream.ts
│   │   └── user.ts
│   ├── types/                             # TypeScript 类型
│   │   ├── api.ts
│   │   ├── dream.ts
│   │   └── user.ts
│   ├── utils/                             # 工具函数
│   │   ├── request.ts
│   │   └── storage.ts
│   └── views/                             # 页面视图
│       ├── auth/
│       │   ├── LoginView.vue
│       │   └── RegisterView.vue
│       ├── dream/
│       │   ├── DreamListView.vue
│       │   ├── DreamDetailView.vue
│       │   └── DreamCreateView.vue
│       ├── user/
│       │   └── ProfileView.vue
│       └── admin/
│           ├── DashboardView.vue
│           ├── UserManageView.vue
│           └── LogManageView.vue
└── public/
    └── favicon.ico
```
