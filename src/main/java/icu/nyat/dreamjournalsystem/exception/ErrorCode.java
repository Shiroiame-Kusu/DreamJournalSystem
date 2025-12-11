package icu.nyat.dreamjournalsystem.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    // 通用错误 (400xx)
    VALIDATION_ERROR(40001, "参数验证失败"),
    FILE_FORMAT_ERROR(40002, "文件格式不支持"),
    FILE_SIZE_ERROR(40003, "文件大小超限"),

    // 认证错误 (100xx)
    USER_EXISTS(10001, "用户名已存在"),
    EMAIL_EXISTS(10002, "邮箱已注册"),
    LOGIN_FAILED(10003, "用户名或密码错误"),
    USER_BANNED(10004, "账户已被禁用"),
    USER_LOCKED(10005, "账户已被锁定"),
    TOKEN_EXPIRED(10006, "Token已过期"),
    TOKEN_INVALID(10007, "Token无效"),
    ACCESS_DENIED(10008, "权限不足"),
    USER_NOT_FOUND(10009, "用户不存在"),

    // 梦境错误 (200xx)
    DREAM_NOT_FOUND(20001, "梦境不存在"),
    DREAM_ACCESS_DENIED(20002, "无权访问此梦境"),
    DREAM_CONTENT_TOO_SHORT(20003, "梦境内容过短"),

    // AI错误 (300xx)
    AI_SERVICE_UNAVAILABLE(30001, "AI服务暂不可用"),
    AI_GENERATION_FAILED(30002, "AI总结生成失败"),
    AI_REQUEST_TIMEOUT(30003, "AI请求超时"),

    // 系统错误 (500xx)
    INTERNAL_ERROR(50001, "系统内部错误"),
    DATABASE_ERROR(50002, "数据库错误"),
    EXTERNAL_SERVICE_ERROR(50003, "第三方服务错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
