package icu.nyat.dreamjournalsystem.service.impl;

import icu.nyat.dreamjournalsystem.dto.request.LoginRequest;
import icu.nyat.dreamjournalsystem.dto.request.RegisterRequest;
import icu.nyat.dreamjournalsystem.dto.response.LoginResponse;
import icu.nyat.dreamjournalsystem.entity.User;
import icu.nyat.dreamjournalsystem.exception.BusinessException;
import icu.nyat.dreamjournalsystem.exception.ErrorCode;
import icu.nyat.dreamjournalsystem.mapper.UserMapper;
import icu.nyat.dreamjournalsystem.security.JwtTokenProvider;
import icu.nyat.dreamjournalsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request, String ipAddress) {
        // 验证确认密码
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException(ErrorCode.USER_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已注册
        if (userMapper.countByEmail(request.getEmail()) > 0) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS, "邮箱已注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        log.info("新用户注册成功: {}", user.getUsername());

        // 注册成功后自动登录，生成令牌
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // 更新登录信息
        userMapper.updateLoginInfo(user.getId(), ipAddress);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .avatarUrl(user.getAvatarUrl())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // 查找用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            user = userMapper.findByEmail(request.getUsername());
        }

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 检查账户状态
        if (user.getStatus() == User.UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_BANNED, "账户已被禁用");
        }

        // 检查账户是否被锁定
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.USER_LOCKED, "账户已被锁定，请稍后再试");
        }

        try {
            // 认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            // 更新登录信息
            userMapper.updateLoginInfo(user.getId(), ipAddress);

            // 生成令牌
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

            log.info("用户登录成功: {}", user.getUsername());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                    .user(LoginResponse.UserInfo.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .nickname(user.getNickname())
                            .avatarUrl(user.getAvatarUrl())
                            .role(user.getRole().name())
                            .build())
                    .build();

        } catch (BadCredentialsException e) {
            // 增加登录失败次数
            userMapper.incrementLoginAttempts(user.getId());
            
            // 检查是否需要锁定账户
            if (user.getLoginAttempts() + 1 >= MAX_LOGIN_ATTEMPTS) {
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                userMapper.lockAccount(user.getId(), lockUntil);
                log.warn("用户 {} 因多次登录失败被锁定", user.getUsername());
                throw new BusinessException(ErrorCode.USER_LOCKED, "登录失败次数过多，账户已被锁定30分钟");
            }

            throw new BusinessException(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
        }
    }

    @Override
    public String refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "刷新令牌无效");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        return jwtTokenProvider.generateAccessToken(username);
    }

    @Override
    public void logout(String token) {
        // 可以将令牌加入黑名单（需要Redis支持）
        log.info("用户退出登录");
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "当前密码错误");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 修改密码成功", user.getUsername());
    }
}
