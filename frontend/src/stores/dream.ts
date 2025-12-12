import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Dream, DreamRequest } from '@/types'
import { dreamApi } from '@/api/dream'

export const useDreamStore = defineStore('dream', () => {
  // 状态
  const dreams = ref<Dream[]>([])
  const currentDream = ref<Dream | null>(null)
  const favorites = ref<Dream[]>([])
  const loading = ref(false)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(0)
  const searchKeyword = ref('')

  // 计算属性
  const hasDreams = computed(() => dreams.value.length > 0)
  const dreamCount = computed(() => totalElements.value)

  // 获取梦境列表
  async function fetchDreams(page = 0, size = 10, keyword?: string) {
    loading.value = true
    try {
      // 后端 page 从 1 开始，前端从 0 开始，需要 +1
      const params: any = { page: page + 1, size }
      if (keyword) {
        params.keyword = keyword
        searchKeyword.value = keyword
      }
      
      const response = await dreamApi.getDreams(params)
      dreams.value = response.content || []
      totalPages.value = response.totalPages || 0
      totalElements.value = response.totalElements || 0
      // 后端返回 currentPage（从1开始），转换为前端的页码（从0开始）
      currentPage.value = ((response as any).currentPage || 1) - 1
      return response
    } catch (error) {
      console.error('获取梦境列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取单个梦境
  async function fetchDream(id: number) {
    loading.value = true
    try {
      const dream = await dreamApi.getDream(id)
      currentDream.value = dream
      return dream
    } catch (error) {
      console.error('获取梦境详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建梦境
  async function createDream(data: DreamRequest) {
    loading.value = true
    try {
      const dream = await dreamApi.createDream(data)
      dreams.value.unshift(dream)
      totalElements.value++
      return dream
    } catch (error) {
      console.error('创建梦境失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新梦境
  async function updateDream(id: number, data: DreamRequest) {
    loading.value = true
    try {
      const dream = await dreamApi.updateDream(id, data)
      const index = dreams.value.findIndex(d => d.id === id)
      if (index !== -1) {
        dreams.value[index] = dream
      }
      if (currentDream.value?.id === id) {
        currentDream.value = dream
      }
      return dream
    } catch (error) {
      console.error('更新梦境失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 删除梦境
  async function deleteDream(id: number) {
    loading.value = true
    try {
      await dreamApi.deleteDream(id)
      dreams.value = dreams.value.filter(d => d.id !== id)
      totalElements.value--
      if (currentDream.value?.id === id) {
        currentDream.value = null
      }
    } catch (error) {
      console.error('删除梦境失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 切换收藏状态
  async function toggleFavorite(id: number) {
    try {
      const result = await dreamApi.toggleFavorite(id)
      const isFavorite = result.isFavorite
      
      // 更新列表中的梦境
      const index = dreams.value.findIndex(d => d.id === id)
      if (index !== -1) {
        dreams.value[index] = { ...dreams.value[index], isFavorite }
      }
      
      // 更新当前梦境
      if (currentDream.value?.id === id) {
        currentDream.value = { ...currentDream.value, isFavorite }
      }
      
      // 更新收藏列表
      if (isFavorite) {
        const dreamToAdd = dreams.value.find(d => d.id === id)
        if (dreamToAdd && !favorites.value.find(f => f.id === id)) {
          favorites.value.push(dreamToAdd)
        }
      } else {
        favorites.value = favorites.value.filter(f => f.id !== id)
      }
      
      return result
    } catch (error) {
      console.error('切换收藏状态失败:', error)
      throw error
    }
  }

  // 获取收藏列表
  async function fetchFavorites() {
    loading.value = true
    try {
      const response = await dreamApi.getFavorites()
      // 后端直接返回 Dream[] 数组
      favorites.value = Array.isArray(response) ? response : []
      return response
    } catch (error) {
      console.error('获取收藏列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 生成AI分析（带轮询）
  async function generateAISummary(id: number) {
    try {
      // 先触发生成
      await dreamApi.generateAISummary(id)
      
      // 轮询获取结果，最多等待 60 秒
      const maxAttempts = 30
      const pollInterval = 2000 // 2秒轮询一次
      
      for (let attempt = 0; attempt < maxAttempts; attempt++) {
        await new Promise(resolve => setTimeout(resolve, pollInterval))
        
        // 重新获取梦境详情
        const dream = await dreamApi.getDream(id)
        
        if (dream.aiSummary && dream.aiSummary.status === 'COMPLETED') {
          // 更新当前梦境的AI分析
          if (currentDream.value?.id === id) {
            currentDream.value = dream
          }
          
          const index = dreams.value.findIndex(d => d.id === id)
          if (index !== -1) {
            dreams.value[index] = dream
          }
          
          return dream.aiSummary
        }
        
        if (dream.aiSummary && dream.aiSummary.status === 'FAILED') {
          throw new Error(dream.aiSummary.errorMessage || 'AI 分析生成失败')
        }
      }
      
      throw new Error('AI 分析超时，请稍后刷新页面查看')
    } catch (error) {
      console.error('生成AI分析失败:', error)
      throw error
    }
  }

  // 清空状态
  function clearState() {
    dreams.value = []
    currentDream.value = null
    favorites.value = []
    totalPages.value = 0
    totalElements.value = 0
    currentPage.value = 0
    searchKeyword.value = ''
  }

  return {
    // 状态
    dreams,
    currentDream,
    favorites,
    loading,
    totalPages,
    totalElements,
    currentPage,
    searchKeyword,
    // 计算属性
    hasDreams,
    dreamCount,
    // 方法
    fetchDreams,
    fetchDream,
    createDream,
    updateDream,
    deleteDream,
    toggleFavorite,
    fetchFavorites,
    generateAISummary,
    clearState
  }
})
