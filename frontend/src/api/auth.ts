import apiClient from './client'
import type { LoginRequest, RegisterRequest, LoginResponse, User } from '@/types'

export const authApi = {
  // 登录
  login(data: LoginRequest): Promise<LoginResponse> {
    return apiClient.post('/auth/login', data)
  },

  // 注册
  register(data: RegisterRequest): Promise<LoginResponse> {
    return apiClient.post('/auth/register', data)
  },

  // 刷新令牌
  refreshToken(refreshToken: string): Promise<{ accessToken: string; refreshToken: string }> {
    return apiClient.post('/auth/refresh', { refreshToken })
  },

  // 登出
  logout(): Promise<void> {
    return apiClient.post('/auth/logout')
  },

  // 获取当前用户信息
  getCurrentUser(): Promise<User> {
    return apiClient.get('/auth/me')
  },

  // 更新用户信息
  updateProfile(data: Partial<User>): Promise<User> {
    return apiClient.put('/auth/profile', data)
  },

  // 修改密码
  changePassword(data: { oldPassword: string; newPassword: string }): Promise<void> {
    return apiClient.put('/auth/password', data)
  }
}
