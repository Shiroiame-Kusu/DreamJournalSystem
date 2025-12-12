-- =====================================================
-- Dream Journal System - 数据库初始化脚本
-- 用于 MySQL 数据库手动初始化
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dream_journal 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dream_journal;

-- =====================================================
-- 删除旧表（按依赖顺序）
-- 如需重建表结构，请取消注释以下行
-- =====================================================
-- DROP TABLE IF EXISTS operation_logs;
-- DROP TABLE IF EXISTS user_sessions;
-- DROP TABLE IF EXISTS ai_summaries;
-- DROP TABLE IF EXISTS dreams;
-- DROP TABLE IF EXISTS users;

-- =====================================================
-- 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '状态',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    login_attempts INT DEFAULT 0 COMMENT '登录尝试次数',
    locked_until DATETIME DEFAULT NULL COMMENT '锁定截止时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 梦境表
-- =====================================================
CREATE TABLE IF NOT EXISTS dreams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '梦境ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '梦境内容',
    dream_date DATE NOT NULL COMMENT '做梦日期',
    sleep_start_time TIME DEFAULT NULL COMMENT '入睡时间',
    sleep_end_time TIME DEFAULT NULL COMMENT '醒来时间',
    sleep_quality ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR', 'TERRIBLE') DEFAULT NULL COMMENT '睡眠质量',
    mood_before_sleep ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED', 'CONFUSED', 'REFRESHED') DEFAULT NULL COMMENT '睡前情绪',
    mood_after_wake ENUM('HAPPY', 'CALM', 'ANXIOUS', 'SAD', 'STRESSED', 'EXCITED', 'TIRED', 'CONFUSED', 'REFRESHED') DEFAULT NULL COMMENT '醒后情绪',
    dream_type ENUM('NORMAL', 'LUCID', 'NIGHTMARE', 'RECURRING', 'PROPHETIC') DEFAULT 'NORMAL' COMMENT '梦境类型',
    vividness INT DEFAULT NULL COMMENT '清晰度(1-10)',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    is_private BOOLEAN DEFAULT TRUE COMMENT '是否私密',
    tags JSON DEFAULT NULL COMMENT '标签列表',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_dream_date (dream_date),
    INDEX idx_is_favorite (is_favorite),
    INDEX idx_dream_type (dream_type),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX ft_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='梦境表';

-- =====================================================
-- AI分析结果表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'AI分析ID',
    dream_id BIGINT NOT NULL UNIQUE COMMENT '梦境ID',
    summary TEXT DEFAULT NULL COMMENT '梦境总结',
    keywords JSON DEFAULT NULL COMMENT '关键词分析',
    emotion_analysis JSON DEFAULT NULL COMMENT '情绪分析',
    symbol_analysis JSON DEFAULT NULL COMMENT '象征符号分析',
    psychological_insight TEXT DEFAULT NULL COMMENT '心理洞察',
    advice TEXT DEFAULT NULL COMMENT '建议',
    ai_model VARCHAR(50) DEFAULT NULL COMMENT '使用的AI模型',
    prompt_version VARCHAR(20) DEFAULT NULL COMMENT '提示词版本',
    confidence_score DECIMAL(5,4) DEFAULT NULL COMMENT '置信度分数',
    tokens_used INT DEFAULT NULL COMMENT '使用的token数',
    generation_time_ms INT DEFAULT NULL COMMENT '生成耗时(毫秒)',
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING' COMMENT '状态',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (dream_id) REFERENCES dreams(id) ON DELETE CASCADE,
    INDEX idx_dream_id (dream_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI分析结果表';

-- =====================================================
-- 用户会话表 (用于JWT令牌管理)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    refresh_token VARCHAR(500) NOT NULL COMMENT '刷新令牌',
    device_info VARCHAR(255) DEFAULT NULL COMMENT '设备信息',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_refresh_token (refresh_token(255)),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- =====================================================
-- 操作日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) DEFAULT NULL COMMENT '目标类型',
    target_id BIGINT DEFAULT NULL COMMENT '目标ID',
    details JSON DEFAULT NULL COMMENT '详细信息',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 插入默认管理员用户
-- 默认密码: admin123 (BCrypt加密)
-- =====================================================
INSERT IGNORE INTO users (username, password_hash, email, nickname, role, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6jAUy', 'admin@dreamjournal.com', '系统管理员', 'ADMIN', 'ACTIVE');

-- =====================================================
-- 创建测试用户（可选）
-- 默认密码: test123
-- =====================================================
-- INSERT IGNORE INTO users (username, password_hash, email, nickname, role, status) VALUES 
-- ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6jAUy', 'test@dreamjournal.com', '测试用户', 'USER', 'ACTIVE');
