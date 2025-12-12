import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest, RegisterRequest, LoginResponse } from '@/types'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const username = computed(() => user.value?.username || '')

  // 登录
  async function login(credentials: LoginRequest) {
    loading.value = true
    try {
      const response = await authApi.login(credentials)
      setAuthData(response)
      return { success: true }
    } catch (error: any) {
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败，请检查用户名和密码' 
      }
    } finally {
      loading.value = false
    }
  }

  // 注册
  async function register(data: RegisterRequest) {
    loading.value = true
    try {
      const response = await authApi.register(data)
      setAuthData(response)
      return { success: true }
    } catch (error: any) {
      return { 
        success: false, 
        message: error.response?.data?.message || '注册失败，请稍后重试' 
      }
    } finally {
      loading.value = false
    }
  }

  // 刷新令牌
  async function refreshAccessToken() {
    if (!refreshToken.value) {
      clearAuthData()
      return false
    }
    
    try {
      const response = await authApi.refreshToken(refreshToken.value)
      token.value = response.accessToken
      refreshToken.value = response.refreshToken
      return true
    } catch {
      clearAuthData()
      return false
    }
  }

  // 登出
  async function logout() {
    try {
      if (token.value) {
        await authApi.logout()
      }
    } catch {
      // 忽略登出错误
    } finally {
      clearAuthData()
    }
  }

  // 获取当前用户信息
  async function fetchCurrentUser() {
    if (!token.value) return
    
    const userData = await authApi.getCurrentUser()
    user.value = userData
  }

  // 设置认证数据
  function setAuthData(response: LoginResponse) {
    token.value = response.accessToken
    refreshToken.value = response.refreshToken
    user.value = response.user
  }

  // 清除认证数据
  function clearAuthData() {
    user.value = null
    token.value = null
    refreshToken.value = null
  }

  return {
    // 状态
    user,
    token,
    refreshToken,
    loading,
    // 计算属性
    isLoggedIn,
    isAdmin,
    username,
    // 方法
    login,
    clearAuthData,
    register,
    logout,
    refreshAccessToken,
    fetchCurrentUser
  }
}, {
  persist: {
    pick: ['token', 'refreshToken', 'user']
  }
})
