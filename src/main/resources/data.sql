-- =====================================================
-- Dream Journal System - 初始数据脚本
-- Spring Boot 自动执行
-- =====================================================

-- =====================================================
-- 插入默认管理员用户
-- 默认密码: admin123 (BCrypt加密)
-- =====================================================
INSERT IGNORE INTO users (username, password_hash, email, nickname, role, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6jAUy', 'admin@dreamjournal.com', '系统管理员', 'ADMIN', 'ACTIVE');
