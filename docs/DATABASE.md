# 梦境日记系统 - 数据库设计文档

## 数据库概述

- **数据库类型**: MySQL 8.0+
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **存储引擎**: InnoDB

## 表结构设计

### 1. 用户表 (users)

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 用户ID，主键 |
| username | VARCHAR(50) | UNIQUE, NOT NULL | - | 用户名，唯一 |
| email | VARCHAR(100) | UNIQUE, NOT NULL | - | 邮箱地址，唯一 |
| password_hash | VARCHAR(255) | NOT NULL | - | BCrypt加密后的密码 |
| nickname | VARCHAR(50) | NULL | - | 昵称 |
| avatar_url | VARCHAR(500) | NULL | - | 头像URL |
| role | ENUM('USER', 'ADMIN') | NOT NULL | 'USER' | 用户角色 |
| status | ENUM('ACTIVE', 'INACTIVE', 'BANNED') | NOT NULL | 'ACTIVE' | 账户状态 |
| last_login_at | DATETIME | NULL | - | 最后登录时间 |
| last_login_ip | VARCHAR(45) | NULL | - | 最后登录IP |
| login_attempts | INT | NOT NULL | 0 | 登录失败次数 |
| locked_until | DATETIME | NULL | - | 账户锁定截止时间 |
| created_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引设计：**
- `idx_users_username` - username (UNIQUE)
- `idx_users_email` - email (UNIQUE)
- `idx_users_role` - role
- `idx_users_status` - status
- `idx_users_created_at` - created_at

### 2. 梦境日志表 (dreams)

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 梦境ID，主键 |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | - | 用户ID，外键关联users表 |
| title | VARCHAR(200) | NOT NULL | - | 梦境标题 |
| content | TEXT | NOT NULL | - | 梦境内容详情 |
| dream_date | DATE | NOT NULL | - | 做梦日期 |
| sleep_start_time | TIME | NULL | - | 入睡时间 |
| sleep_end_time | TIME | NULL | - | 醒来时间 |
| sleep_quality | ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR', 'TERRIBLE') | NULL | - | 睡眠质量 |
| mood_before_sleep | ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED') | NULL | - | 睡前情绪 |
| mood_after_wake | ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED', 'CONFUSED', 'REFRESHED') | NULL | - | 醒后情绪 |
| dream_type | ENUM('NORMAL', 'LUCID', 'NIGHTMARE', 'RECURRING', 'PROPHETIC') | NULL | 'NORMAL' | 梦境类型 |
| vividness | TINYINT | NULL | - | 梦境清晰度 (1-10) |
| is_favorite | BOOLEAN | NOT NULL | FALSE | 是否收藏 |
| is_private | BOOLEAN | NOT NULL | TRUE | 是否私密 |
| tags | JSON | NULL | - | 标签数组 |
| created_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引设计：**
- `idx_dreams_user_id` - user_id
- `idx_dreams_dream_date` - dream_date
- `idx_dreams_user_date` - user_id, dream_date
- `idx_dreams_is_favorite` - is_favorite
- `idx_dreams_created_at` - created_at

**外键约束：**
- `fk_dreams_user_id` - REFERENCES users(id) ON DELETE CASCADE

### 3. AI总结表 (ai_summaries)

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 总结ID，主键 |
| dream_id | BIGINT | FOREIGN KEY, UNIQUE, NOT NULL | - | 梦境ID，外键关联dreams表，一对一关系 |
| summary | TEXT | NOT NULL | - | AI生成的梦境总结 |
| keywords | JSON | NOT NULL | - | 关键词数组 |
| emotion_analysis | JSON | NOT NULL | - | 情绪分析结果 |
| symbol_analysis | JSON | NOT NULL | - | 象征意义分析 |
| psychological_insight | TEXT | NULL | - | 心理学洞察 |
| advice | TEXT | NULL | - | 建议和提示 |
| ai_model | VARCHAR(50) | NOT NULL | - | 使用的AI模型 |
| prompt_version | VARCHAR(20) | NOT NULL | - | Prompt版本号 |
| confidence_score | DECIMAL(3,2) | NULL | - | AI置信度 (0.00-1.00) |
| tokens_used | INT | NULL | - | 消耗的Token数 |
| generation_time_ms | INT | NULL | - | 生成耗时(毫秒) |
| status | ENUM('PENDING', 'COMPLETED', 'FAILED') | NOT NULL | 'PENDING' | 生成状态 |
| error_message | TEXT | NULL | - | 错误信息 |
| created_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引设计：**
- `idx_summaries_dream_id` - dream_id (UNIQUE)
- `idx_summaries_status` - status
- `idx_summaries_created_at` - created_at

**外键约束：**
- `fk_summaries_dream_id` - REFERENCES dreams(id) ON DELETE CASCADE

### 4. 操作日志表 (operation_logs)

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 日志ID，主键 |
| user_id | BIGINT | NULL | - | 操作用户ID |
| action | VARCHAR(100) | NOT NULL | - | 操作类型 |
| resource_type | VARCHAR(50) | NOT NULL | - | 资源类型 |
| resource_id | BIGINT | NULL | - | 资源ID |
| details | JSON | NULL | - | 操作详情 |
| ip_address | VARCHAR(45) | NOT NULL | - | IP地址 |
| user_agent | VARCHAR(500) | NULL | - | 用户代理 |
| request_method | VARCHAR(10) | NOT NULL | - | 请求方法 |
| request_url | VARCHAR(500) | NOT NULL | - | 请求URL |
| response_status | INT | NOT NULL | - | 响应状态码 |
| execution_time_ms | INT | NULL | - | 执行耗时(毫秒) |
| created_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- `idx_logs_user_id` - user_id
- `idx_logs_action` - action
- `idx_logs_created_at` - created_at
- `idx_logs_resource` - resource_type, resource_id

### 5. 用户会话表 (user_sessions)

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 会话ID，主键 |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | - | 用户ID |
| token_hash | VARCHAR(255) | NOT NULL | - | Token哈希值 |
| device_info | VARCHAR(255) | NULL | - | 设备信息 |
| ip_address | VARCHAR(45) | NOT NULL | - | IP地址 |
| expires_at | DATETIME | NOT NULL | - | 过期时间 |
| is_revoked | BOOLEAN | NOT NULL | FALSE | 是否已撤销 |
| created_at | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- `idx_sessions_user_id` - user_id
- `idx_sessions_token_hash` - token_hash
- `idx_sessions_expires_at` - expires_at

**外键约束：**
- `fk_sessions_user_id` - REFERENCES users(id) ON DELETE CASCADE

## SQL 建表语句

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS dream_journal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dream_journal;

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NULL,
    avatar_url VARCHAR(500) NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    last_login_ip VARCHAR(45) NULL,
    login_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY idx_users_username (username),
    UNIQUE KEY idx_users_email (email),
    KEY idx_users_role (role),
    KEY idx_users_status (status),
    KEY idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 梦境日志表
CREATE TABLE dreams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    dream_date DATE NOT NULL,
    sleep_start_time TIME NULL,
    sleep_end_time TIME NULL,
    sleep_quality ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR', 'TERRIBLE') NULL,
    mood_before_sleep ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED') NULL,
    mood_after_wake ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED', 'CONFUSED', 'REFRESHED') NULL,
    dream_type ENUM('NORMAL', 'LUCID', 'NIGHTMARE', 'RECURRING', 'PROPHETIC') NULL DEFAULT 'NORMAL',
    vividness TINYINT NULL CHECK (vividness >= 1 AND vividness <= 10),
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    is_private BOOLEAN NOT NULL DEFAULT TRUE,
    tags JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    KEY idx_dreams_user_id (user_id),
    KEY idx_dreams_dream_date (dream_date),
    KEY idx_dreams_user_date (user_id, dream_date),
    KEY idx_dreams_is_favorite (is_favorite),
    KEY idx_dreams_created_at (created_at),
    
    CONSTRAINT fk_dreams_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI总结表
CREATE TABLE ai_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dream_id BIGINT NOT NULL,
    summary TEXT NOT NULL,
    keywords JSON NOT NULL,
    emotion_analysis JSON NOT NULL,
    symbol_analysis JSON NOT NULL,
    psychological_insight TEXT NULL,
    advice TEXT NULL,
    ai_model VARCHAR(50) NOT NULL,
    prompt_version VARCHAR(20) NOT NULL,
    confidence_score DECIMAL(3,2) NULL CHECK (confidence_score >= 0 AND confidence_score <= 1),
    tokens_used INT NULL,
    generation_time_ms INT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    error_message TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY idx_summaries_dream_id (dream_id),
    KEY idx_summaries_status (status),
    KEY idx_summaries_created_at (created_at),
    
    CONSTRAINT fk_summaries_dream_id FOREIGN KEY (dream_id) REFERENCES dreams(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id BIGINT NULL,
    details JSON NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(500) NULL,
    request_method VARCHAR(10) NOT NULL,
    request_url VARCHAR(500) NOT NULL,
    response_status INT NOT NULL,
    execution_time_ms INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    KEY idx_logs_user_id (user_id),
    KEY idx_logs_action (action),
    KEY idx_logs_created_at (created_at),
    KEY idx_logs_resource (resource_type, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户会话表
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    device_info VARCHAR(255) NULL,
    ip_address VARCHAR(45) NOT NULL,
    expires_at DATETIME NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    KEY idx_sessions_user_id (user_id),
    KEY idx_sessions_token_hash (token_hash),
    KEY idx_sessions_expires_at (expires_at),
    
    CONSTRAINT fk_sessions_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认管理员账户 (密码: Admin@123456)
INSERT INTO users (username, email, password_hash, nickname, role, status) VALUES
('admin', 'admin@dreamjournal.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 'ACTIVE');
```

## JSON 字段结构说明

### dreams.tags
```json
["梦境", "飞翔", "童年", "家人"]
```

### ai_summaries.keywords
```json
{
    "primary": ["飞翔", "自由"],
    "secondary": ["天空", "云朵", "鸟"],
    "emotions": ["喜悦", "解脱"]
}
```

### ai_summaries.emotion_analysis
```json
{
    "dominant_emotion": "喜悦",
    "emotion_spectrum": {
        "positive": 0.75,
        "negative": 0.10,
        "neutral": 0.15
    },
    "intensity": 0.8,
    "emotions_detected": [
        {"emotion": "喜悦", "score": 0.65},
        {"emotion": "自由", "score": 0.55},
        {"emotion": "平静", "score": 0.40}
    ]
}
```

### ai_summaries.symbol_analysis
```json
{
    "symbols": [
        {
            "symbol": "飞翔",
            "meaning": "渴望自由、突破束缚",
            "psychological_interpretation": "可能反映近期在现实中感到某种压力或限制，内心渴望解脱"
        },
        {
            "symbol": "天空",
            "meaning": "无限可能、开阔的视野",
            "psychological_interpretation": "象征着对未来的期待和希望"
        }
    ],
    "overall_theme": "追求自由与突破",
    "life_connection": "可能与近期的工作压力或人际关系有关"
}
```
