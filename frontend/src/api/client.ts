import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 是否正在刷新令牌
let isRefreshing = false
// 等待刷新令牌的请求队列
let refreshSubscribers: ((token: string) => void)[] = []

// 订阅令牌刷新
function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback)
}

// 通知所有订阅者
function onTokenRefreshed(token: string) {
  refreshSubscribers.forEach(callback => callback(token))
  refreshSubscribers = []
}

// 请求拦截器
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore()
    
    // 添加认证头
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 直接返回响应数据
    return response.data?.data ?? response.data
  },
  async (error: AxiosError<{ code: number; message: string }>) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean }
    
    // 401 未授权 - 尝试刷新令牌
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      // 如果是登录/注册/刷新请求，不需要重试
      const url = originalRequest.url || ''
      if (url.includes('/auth/login') || url.includes('/auth/register') || url.includes('/auth/refresh')) {
        return Promise.reject(error)
      }
      
      if (!isRefreshing) {
        isRefreshing = true
        
        const authStore = useAuthStore()
        const success = await authStore.refreshAccessToken()
        
        isRefreshing = false
        
        if (success && authStore.token) {
          onTokenRefreshed(authStore.token)
          
          // 重试原请求
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${authStore.token}`
          }
          return apiClient(originalRequest)
        } else {
          // 刷新失败，清除等待队列并跳转登录页
          refreshSubscribers = []
          router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
          return Promise.reject(error)
        }
      } else {
        // 等待令牌刷新
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh((token: string) => {
            if (token) {
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${token}`
              }
              resolve(apiClient(originalRequest))
            } else {
              reject(error)
            }
          })
        })
      }
    }
    
    // 403 禁止访问
    if (error.response?.status === 403) {
      router.push({ name: 'dreams' })
    }
    
    // 处理错误消息
    const message = error.response?.data?.message || '请求失败，请稍后重试'
    console.error('API Error:', message)
    
    return Promise.reject(error)
  }
)

export default apiClient
