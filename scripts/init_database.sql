-- =====================================================
-- Dream Journal System - 数据库初始化脚本
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dream_journal 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dream_journal;

-- =====================================================
-- 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role ENUM('user', 'admin') DEFAULT 'user' COMMENT '角色',
    status ENUM('active', 'banned') DEFAULT 'active' COMMENT '状态',
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
    mood VARCHAR(20) DEFAULT NULL COMMENT '心情',
    sleep_quality TINYINT DEFAULT NULL COMMENT '睡眠质量(1-5)',
    tags JSON DEFAULT NULL COMMENT '标签列表',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    visibility ENUM('private', 'public') DEFAULT 'private' COMMENT '可见性',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_dream_date (dream_date),
    INDEX idx_is_favorite (is_favorite),
    INDEX idx_visibility (visibility),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX ft_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='梦境表';

-- =====================================================
-- AI分析结果表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'AI分析ID',
    dream_id BIGINT NOT NULL UNIQUE COMMENT '梦境ID',
    summary TEXT NOT NULL COMMENT '梦境总结',
    symbols JSON DEFAULT NULL COMMENT '象征符号分析',
    emotions JSON DEFAULT NULL COMMENT '情绪分析',
    themes JSON DEFAULT NULL COMMENT '主题列表',
    interpretation TEXT DEFAULT NULL COMMENT '深度解读',
    advice TEXT DEFAULT NULL COMMENT '建议',
    model VARCHAR(50) DEFAULT NULL COMMENT '使用的AI模型',
    tokens_used INT DEFAULT NULL COMMENT '使用的token数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (dream_id) REFERENCES dreams(id) ON DELETE CASCADE,
    INDEX idx_dream_id (dream_id),
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
INSERT INTO users (username, password, email, nickname, role, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6jAUy', 'admin@dreamjournal.com', '系统管理员', 'admin', 'active')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- =====================================================
-- 创建测试用户（可选）
-- 默认密码: test123
-- =====================================================
-- INSERT INTO users (username, password, email, nickname, role, status) VALUES 
-- ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6jAUy', 'test@dreamjournal.com', '测试用户', 'user', 'active');
