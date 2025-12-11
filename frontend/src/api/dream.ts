import apiClient from './client'
import type { Dream, DreamRequest, PageResponse, AISummary } from '@/types'

export const dreamApi = {
  // 获取梦境列表
  getDreams(params?: { 
    page?: number
    size?: number
    keyword?: string
    startDate?: string
    endDate?: string
    mood?: string
  }): Promise<PageResponse<Dream>> {
    return apiClient.get('/dreams', { params })
  },

  // 获取单个梦境
  getDream(id: number): Promise<Dream> {
    return apiClient.get(`/dreams/${id}`)
  },

  // 创建梦境
  createDream(data: DreamRequest): Promise<Dream> {
    return apiClient.post('/dreams', data)
  },

  // 更新梦境
  updateDream(id: number, data: DreamRequest): Promise<Dream> {
    return apiClient.put(`/dreams/${id}`, data)
  },

  // 删除梦境
  deleteDream(id: number): Promise<void> {
    return apiClient.delete(`/dreams/${id}`)
  },

  // 切换收藏状态
  toggleFavorite(id: number): Promise<{ isFavorite: boolean }> {
    return apiClient.put(`/dreams/${id}/favorite`)
  },

  // 获取收藏列表
  getFavorites(): Promise<PageResponse<Dream>> {
    return apiClient.get('/dreams/favorites')
  },

  // 生成/重新生成AI分析
  generateAISummary(id: number): Promise<{ status: string; estimatedTime: number }> {
    return apiClient.post(`/dreams/${id}/ai-summary/regenerate`)
  },

  // 获取梦境统计
  getStatistics(): Promise<{
    totalDreams: number
    totalFavorites: number
    dreamsByMood: Record<string, number>
    dreamsByMonth: Record<string, number>
    averageSleepQuality: number
  }> {
    return apiClient.get('/dreams/statistics')
  },

  // 搜索梦境
  searchDreams(keyword: string): Promise<Dream[]> {
    return apiClient.get('/dreams/search', { params: { keyword } })
  }
}
