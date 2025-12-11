package icu.nyat.dreamjournalsystem.service;

import icu.nyat.dreamjournalsystem.dto.request.LoginRequest;
import icu.nyat.dreamjournalsystem.dto.request.RegisterRequest;
import icu.nyat.dreamjournalsystem.dto.response.LoginResponse;
import icu.nyat.dreamjournalsystem.entity.User;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册（注册后自动登录）
     */
    LoginResponse register(RegisterRequest request, String ipAddress);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request, String ipAddress);

    /**
     * 刷新令牌
     */
    String refreshToken(String refreshToken);

    /**
     * 退出登录
     */
    void logout(String token);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String currentPassword, String newPassword);
}
