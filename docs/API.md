# 梦境日记系统 - API 文档

## 概述

- **基础URL**: `http://localhost:8080/api/v1`
- **数据格式**: JSON
- **认证方式**: JWT Bearer Token
- **字符编码**: UTF-8

## 通用响应格式

### 成功响应
```json
{
    "code": 200,
    "message": "success",
    "data": { ... },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 错误响应
```json
{
    "code": 400,
    "message": "错误信息",
    "errors": [
        {"field": "username", "message": "用户名不能为空"}
    ],
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

---

## 一、认证模块 (Auth)

### 1.1 用户注册

**请求**
```
POST /auth/register
Content-Type: application/json
```

**请求体**
```json
{
    "username": "dreamuser",
    "email": "user@example.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "nickname": "梦境探索者"
}
```

**参数说明**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名，3-50字符，字母数字下划线 |
| email | string | 是 | 邮箱地址 |
| password | string | 是 | 密码，8-32字符，需包含大小写字母和数字 |
| confirmPassword | string | 是 | 确认密码 |
| nickname | string | 否 | 昵称，2-50字符 |

**响应**
```json
{
    "code": 201,
    "message": "注册成功",
    "data": {
        "userId": 1,
        "username": "dreamuser",
        "email": "user@example.com",
        "nickname": "梦境探索者",
        "createdAt": "2024-01-01T12:00:00Z"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 1.2 用户登录

**请求**
```
POST /auth/login
Content-Type: application/json
```

**请求体**
```json
{
    "username": "dreamuser",
    "password": "SecurePass123!",
    "rememberMe": true
}
```

**参数说明**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名或邮箱 |
| password | string | 是 | 密码 |
| rememberMe | boolean | 否 | 记住我（延长Token有效期） |

**响应**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "tokenType": "Bearer",
        "expiresIn": 86400,
        "user": {
            "id": 1,
            "username": "dreamuser",
            "email": "user@example.com",
            "nickname": "梦境探索者",
            "avatarUrl": null,
            "role": "USER"
        }
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 1.3 刷新Token

**请求**
```
POST /auth/refresh
Content-Type: application/json
```

**请求体**
```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "expiresIn": 86400
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 1.4 退出登录

**请求**
```
POST /auth/logout
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "退出成功",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 1.5 修改密码

**请求**
```
PUT /auth/password
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**请求体**
```json
{
    "currentPassword": "OldPass123!",
    "newPassword": "NewPass456!",
    "confirmPassword": "NewPass456!"
}
```

**响应**
```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 1.6 获取当前登录用户信息

**请求**
```
GET /auth/me
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "dreamuser",
        "email": "user@example.com",
        "nickname": "梦境探索者",
        "role": "USER",
        "createdAt": "2023-12-01T08:00:00Z"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

**错误响应**
```json
{
    "code": 401,
    "message": "用户不存在",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 二、用户模块 (User)

### 2.1 获取当前用户信息

**请求**
```
GET /users/me
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "dreamuser",
        "email": "user@example.com",
        "nickname": "梦境探索者",
        "avatarUrl": "https://example.com/avatar.jpg",
        "role": "USER",
        "status": "ACTIVE",
        "lastLoginAt": "2024-01-01T10:00:00Z",
        "createdAt": "2023-12-01T08:00:00Z",
        "statistics": {
            "totalDreams": 42,
            "totalFavorites": 5,
            "thisMonthDreams": 8,
            "averageSleepQuality": 3.5
        }
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 2.2 更新用户信息

**请求**
```
PUT /users/me
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**请求体**
```json
{
    "nickname": "新昵称",
    "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": {
        "id": 1,
        "username": "dreamuser",
        "email": "user@example.com",
        "nickname": "新昵称",
        "avatarUrl": "https://example.com/new-avatar.jpg",
        "role": "USER",
        "status": "ACTIVE"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 2.3 上传头像

**请求**
```
POST /users/me/avatar
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件，支持jpg/png/gif，最大2MB |

**响应**
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "avatarUrl": "https://example.com/avatars/user_1_abc123.jpg"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 三、梦境模块 (Dream)

### 3.1 创建梦境记录

**请求**
```
POST /dreams
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**请求体**
```json
{
    "title": "飞翔在云端",
    "content": "梦见自己在天空中自由飞翔，穿越白云，俯瞰大地。感觉非常轻松自在，仿佛所有烦恼都消失了...",
    "dreamDate": "2024-01-01",
    "sleepStartTime": "23:30",
    "sleepEndTime": "07:30",
    "sleepQuality": "GOOD",
    "moodBeforeSleep": "CALM",
    "moodAfterWake": "REFRESHED",
    "dreamType": "LUCID",
    "vividness": 8,
    "isFavorite": false,
    "isPrivate": true,
    "tags": ["飞翔", "自由", "天空"],
    "generateAISummary": true
}
```

**参数说明**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 梦境标题，最多200字符 |
| content | string | 是 | 梦境内容，最少50字符 |
| dreamDate | string | 是 | 做梦日期，格式YYYY-MM-DD |
| sleepStartTime | string | 否 | 入睡时间，格式HH:mm |
| sleepEndTime | string | 否 | 醒来时间，格式HH:mm |
| sleepQuality | string | 否 | 睡眠质量：EXCELLENT/GOOD/FAIR/POOR/TERRIBLE |
| moodBeforeSleep | string | 否 | 睡前情绪 |
| moodAfterWake | string | 否 | 醒后情绪 |
| dreamType | string | 否 | 梦境类型：NORMAL/LUCID/NIGHTMARE/RECURRING/PROPHETIC |
| vividness | number | 否 | 清晰度1-10 |
| isFavorite | boolean | 否 | 是否收藏，默认false |
| isPrivate | boolean | 否 | 是否私密，默认true |
| tags | array | 否 | 标签数组 |
| generateAISummary | boolean | 否 | 是否生成AI总结，默认true |

**响应**
```json
{
    "code": 201,
    "message": "创建成功",
    "data": {
        "id": 1,
        "userId": 1,
        "title": "飞翔在云端",
        "content": "梦见自己在天空中自由飞翔...",
        "dreamDate": "2024-01-01",
        "sleepStartTime": "23:30",
        "sleepEndTime": "07:30",
        "sleepDuration": 480,
        "sleepQuality": "GOOD",
        "moodBeforeSleep": "CALM",
        "moodAfterWake": "REFRESHED",
        "dreamType": "LUCID",
        "vividness": 8,
        "isFavorite": false,
        "isPrivate": true,
        "tags": ["飞翔", "自由", "天空"],
        "aiSummary": null,
        "aiSummaryStatus": "PENDING",
        "createdAt": "2024-01-01T12:00:00Z",
        "updatedAt": "2024-01-01T12:00:00Z"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.2 获取梦境列表

**请求**
```
GET /dreams?page=1&size=10&sort=createdAt,desc&keyword=飞翔&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {accessToken}
```

**参数说明**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认1 |
| size | number | 否 | 每页数量，默认10，最大50 |
| sort | string | 否 | 排序，格式: field,direction |
| keyword | string | 否 | 关键词搜索 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| dreamType | string | 否 | 梦境类型筛选 |
| isFavorite | boolean | 否 | 只看收藏 |
| tags | string | 否 | 标签筛选，多个用逗号分隔 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "title": "飞翔在云端",
                "contentPreview": "梦见自己在天空中自由飞翔...",
                "dreamDate": "2024-01-01",
                "sleepQuality": "GOOD",
                "dreamType": "LUCID",
                "vividness": 8,
                "isFavorite": false,
                "tags": ["飞翔", "自由", "天空"],
                "hasAISummary": true,
                "aiEmotionSummary": "喜悦",
                "createdAt": "2024-01-01T12:00:00Z"
            }
        ],
        "totalElements": 42,
        "totalPages": 5,
        "currentPage": 1,
        "pageSize": 10,
        "hasNext": true,
        "hasPrevious": false
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.3 获取梦境详情

**请求**
```
GET /dreams/{id}
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "userId": 1,
        "title": "飞翔在云端",
        "content": "梦见自己在天空中自由飞翔，穿越白云，俯瞰大地...",
        "dreamDate": "2024-01-01",
        "sleepStartTime": "23:30",
        "sleepEndTime": "07:30",
        "sleepDuration": 480,
        "sleepQuality": "GOOD",
        "moodBeforeSleep": "CALM",
        "moodAfterWake": "REFRESHED",
        "dreamType": "LUCID",
        "vividness": 8,
        "isFavorite": false,
        "isPrivate": true,
        "tags": ["飞翔", "自由", "天空"],
        "aiSummary": {
            "id": 1,
            "summary": "这是一个典型的自由飞翔梦，梦者体验到了在天空中翱翔的愉悦感...",
            "keywords": {
                "primary": ["飞翔", "自由"],
                "secondary": ["天空", "云朵"],
                "emotions": ["喜悦", "平静"]
            },
            "emotionAnalysis": {
                "dominantEmotion": "喜悦",
                "emotionSpectrum": {
                    "positive": 0.75,
                    "negative": 0.10,
                    "neutral": 0.15
                },
                "intensity": 0.8
            },
            "symbolAnalysis": {
                "symbols": [
                    {
                        "symbol": "飞翔",
                        "meaning": "渴望自由、突破束缚",
                        "psychologicalInterpretation": "可能反映近期感到某种压力"
                    }
                ],
                "overallTheme": "追求自由与突破",
                "lifeConnection": "可能与近期的工作压力有关"
            },
            "psychologicalInsight": "飞翔梦通常代表着内心对自由的渴望...",
            "advice": "建议在日常生活中寻找释放压力的方式...",
            "status": "COMPLETED",
            "createdAt": "2024-01-01T12:05:00Z"
        },
        "createdAt": "2024-01-01T12:00:00Z",
        "updatedAt": "2024-01-01T12:05:00Z"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.4 更新梦境记录

**请求**
```
PUT /dreams/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**请求体**
```json
{
    "title": "飞翔在云端（更新）",
    "content": "更新后的内容...",
    "isFavorite": true,
    "tags": ["飞翔", "自由", "天空", "清明梦"]
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": { ... },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.5 删除梦境记录

**请求**
```
DELETE /dreams/{id}
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.6 切换收藏状态

**请求**
```
PUT /dreams/{id}/favorite
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "isFavorite": true
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.7 重新生成AI总结

**请求**
```
POST /dreams/{id}/ai-summary/regenerate
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "AI总结生成中",
    "data": {
        "status": "PENDING",
        "estimatedTime": 10
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.8 获取收藏的梦境列表

**请求**
```
GET /dreams/favorites
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "userId": 1,
            "title": "飞翔在云端",
            "content": "梦见自己在天空中自由飞翔...",
            "dreamDate": "2024-01-01",
            "sleepStartTime": "23:30",
            "sleepEndTime": "07:30",
            "sleepQuality": "GOOD",
            "dreamType": "LUCID",
            "vividness": 8,
            "isFavorite": true,
            "isPrivate": true,
            "tags": ["飞翔", "自由", "天空"],
            "createdAt": "2024-01-01T12:00:00Z",
            "updatedAt": "2024-01-01T12:05:00Z"
        },
        {
            "id": 5,
            "userId": 1,
            "title": "与家人团聚",
            "content": "梦见与久未见面的家人团聚...",
            "dreamDate": "2024-01-05",
            "sleepQuality": "EXCELLENT",
            "dreamType": "NORMAL",
            "vividness": 9,
            "isFavorite": true,
            "isPrivate": true,
            "tags": ["家人", "团聚"],
            "createdAt": "2024-01-05T08:00:00Z",
            "updatedAt": "2024-01-05T08:00:00Z"
        }
    ],
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 3.9 获取梦境统计

**请求**
```
GET /dreams/statistics?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {accessToken}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "totalDreams": 15,
        "dreamsByType": {
            "NORMAL": 8,
            "LUCID": 4,
            "NIGHTMARE": 2,
            "RECURRING": 1
        },
        "averageSleepQuality": 3.5,
        "averageVividness": 6.2,
        "emotionDistribution": {
            "喜悦": 5,
            "平静": 4,
            "焦虑": 3,
            "困惑": 2,
            "悲伤": 1
        },
        "topKeywords": [
            {"keyword": "飞翔", "count": 5},
            {"keyword": "家人", "count": 4},
            {"keyword": "学校", "count": 3}
        ],
        "dreamFrequency": [
            {"date": "2024-01-01", "count": 1},
            {"date": "2024-01-02", "count": 0},
            {"date": "2024-01-03", "count": 2}
        ]
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 四、管理员模块 (Admin)

### 4.1 获取用户列表

**请求**
```
GET /admin/users?page=1&size=10&status=ACTIVE&role=USER&keyword=dream
Authorization: Bearer {accessToken}
```

**需要权限**: ADMIN

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "username": "dreamuser",
                "email": "user@example.com",
                "nickname": "梦境探索者",
                "role": "USER",
                "status": "ACTIVE",
                "lastLoginAt": "2024-01-01T10:00:00Z",
                "createdAt": "2023-12-01T08:00:00Z",
                "dreamsCount": 42
            }
        ],
        "totalElements": 100,
        "totalPages": 10,
        "currentPage": 1
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4.2 获取用户详情

**请求**
```
GET /admin/users/{id}
Authorization: Bearer {accessToken}
```

**需要权限**: ADMIN

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "dreamuser",
        "email": "user@example.com",
        "nickname": "梦境探索者",
        "avatarUrl": "https://example.com/avatar.jpg",
        "role": "USER",
        "status": "ACTIVE",
        "lastLoginAt": "2024-01-01T10:00:00Z",
        "lastLoginIp": "192.168.1.1",
        "loginAttempts": 0,
        "createdAt": "2023-12-01T08:00:00Z",
        "statistics": {
            "totalDreams": 42,
            "totalAISummaries": 40,
            "lastDreamDate": "2024-01-01"
        }
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4.3 更新用户状态

**请求**
```
PUT /admin/users/{id}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**需要权限**: ADMIN

**请求体**
```json
{
    "status": "BANNED",
    "reason": "违反使用条款"
}
```

**响应**
```json
{
    "code": 200,
    "message": "状态更新成功",
    "data": {
        "id": 1,
        "status": "BANNED"
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4.4 删除用户

**请求**
```
DELETE /admin/users/{id}
Authorization: Bearer {accessToken}
```

**需要权限**: ADMIN

**响应**
```json
{
    "code": 200,
    "message": "用户删除成功",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4.5 获取操作日志

**请求**
```
GET /admin/logs?page=1&size=20&action=LOGIN&userId=1&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {accessToken}
```

**需要权限**: ADMIN

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "userId": 1,
                "username": "dreamuser",
                "action": "LOGIN",
                "resourceType": "AUTH",
                "resourceId": null,
                "details": {"method": "password"},
                "ipAddress": "192.168.1.1",
                "userAgent": "Mozilla/5.0...",
                "requestMethod": "POST",
                "requestUrl": "/api/v1/auth/login",
                "responseStatus": 200,
                "executionTimeMs": 125,
                "createdAt": "2024-01-01T10:00:00Z"
            }
        ],
        "totalElements": 1000,
        "totalPages": 50,
        "currentPage": 1
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4.6 获取系统统计

**请求**
```
GET /admin/statistics
Authorization: Bearer {accessToken}
```

**需要权限**: ADMIN

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "users": {
            "total": 1000,
            "active": 950,
            "inactive": 30,
            "banned": 20,
            "newToday": 15,
            "newThisWeek": 85,
            "newThisMonth": 320
        },
        "dreams": {
            "total": 50000,
            "todayCount": 200,
            "weekCount": 1500,
            "monthCount": 6000,
            "averagePerUser": 50
        },
        "aiSummaries": {
            "total": 48000,
            "pending": 50,
            "completed": 47800,
            "failed": 150,
            "totalTokensUsed": 5000000
        },
        "system": {
            "uptime": 864000,
            "cpuUsage": 25.5,
            "memoryUsage": 60.2,
            "diskUsage": 45.8
        }
    },
    "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 五、错误码说明

| 错误码 | 说明 |
|--------|------|
| 10001 | 用户名已存在 |
| 10002 | 邮箱已注册 |
| 10003 | 用户名或密码错误 |
| 10004 | 账户已被禁用 |
| 10005 | 账户已被锁定 |
| 10006 | Token已过期 |
| 10007 | Token无效 |
| 10008 | 权限不足 |
| 20001 | 梦境不存在 |
| 20002 | 无权访问此梦境 |
| 20003 | 梦境内容过短 |
| 30001 | AI服务暂不可用 |
| 30002 | AI总结生成失败 |
| 30003 | AI请求超时 |
| 40001 | 参数验证失败 |
| 40002 | 文件格式不支持 |
| 40003 | 文件大小超限 |
| 50001 | 系统内部错误 |
| 50002 | 数据库错误 |
| 50003 | 第三方服务错误 |
