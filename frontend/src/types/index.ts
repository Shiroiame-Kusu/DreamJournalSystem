// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  nickname?: string
  avatarUrl?: string
  role: 'USER' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  createdAt: string
  updatedAt: string
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 注册请求
export interface RegisterRequest {
  username: string
  password: string
  confirmPassword: string
  email: string
  nickname?: string
}

// 登录响应
export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

// 梦境相关类型
export type SleepQuality = 'EXCELLENT' | 'GOOD' | 'FAIR' | 'POOR' | 'TERRIBLE'
export type Mood = 'HAPPY' | 'CALM' | 'ANXIOUS' | 'SAD' | 'STRESSED' | 'EXCITED' | 'TIRED' | 'CONFUSED' | 'REFRESHED'
export type DreamType = 'NORMAL' | 'LUCID' | 'NIGHTMARE' | 'RECURRING' | 'PROPHETIC'

export interface Dream {
  id: number
  userId: number
  title: string
  content: string
  dreamDate: string
  sleepStartTime?: string
  sleepEndTime?: string
  sleepQuality?: SleepQuality
  moodBeforeSleep?: Mood
  moodAfterWake?: Mood
  dreamType: DreamType
  vividness?: number
  isFavorite: boolean
  isPrivate: boolean
  tags?: string[]
  aiSummary?: AISummary
  createdAt: string
  updatedAt: string
}

// 梦境请求
export interface DreamRequest {
  title: string
  content: string
  dreamDate: string
  sleepStartTime?: string
  sleepEndTime?: string
  sleepQuality?: SleepQuality
  moodBeforeSleep?: Mood
  moodAfterWake?: Mood
  dreamType?: DreamType
  vividness?: number
  isFavorite?: boolean
  isPrivate?: boolean
  tags?: string[]
  generateAISummary?: boolean
}

// AI分析结果
export interface AISummary {
  id: number
  dreamId: number
  summary: string
  keywords?: Keywords
  emotionAnalysis?: EmotionAnalysis
  symbolAnalysis?: SymbolAnalysis
  psychologicalInsight?: string
  advice?: string
  aiModel?: string
  status: 'PENDING' | 'COMPLETED' | 'FAILED'
  errorMessage?: string
  createdAt: string
  updatedAt: string
}

export interface Keywords {
  primary: string[]
  secondary: string[]
  emotions: string[]
}

export interface EmotionAnalysis {
  dominantEmotion: string
  emotionSpectrum?: {
    positive: number
    negative: number
    neutral: number
  }
  intensity?: number
  emotionsDetected?: {
    emotion: string
    score: number
  }[]
}

export interface SymbolAnalysis {
  symbols: Symbol[]
  overallTheme?: string
  lifeConnection?: string
}

export interface Symbol {
  symbol: string
  meaning: string
  psychologicalInterpretation?: string
}

// 分页响应
export interface PageResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

// API响应
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 睡眠质量选项
export const SLEEP_QUALITY_OPTIONS = [
  { value: 'TERRIBLE', label: '很差', emoji: '😴' },
  { value: 'POOR', label: '较差', emoji: '😪' },
  { value: 'FAIR', label: '一般', emoji: '😐' },
  { value: 'GOOD', label: '较好', emoji: '😊' },
  { value: 'EXCELLENT', label: '很好', emoji: '😄' }
]

// 心情选项
export const MOOD_OPTIONS = [
  { value: 'HAPPY', label: '开心', emoji: '😊', color: '#FFD93D' },
  { value: 'CALM', label: '平静', emoji: '😌', color: '#A8E6CF' },
  { value: 'ANXIOUS', label: '焦虑', emoji: '😰', color: '#FFB347' },
  { value: 'SAD', label: '悲伤', emoji: '😢', color: '#87CEEB' },
  { value: 'STRESSED', label: '压力', emoji: '😣', color: '#DDA0DD' },
  { value: 'EXCITED', label: '兴奋', emoji: '🤩', color: '#FF69B4' },
  { value: 'TIRED', label: '疲惫', emoji: '😴', color: '#D3D3D3' },
  { value: 'CONFUSED', label: '困惑', emoji: '😕', color: '#E0E0E0' },
  { value: 'REFRESHED', label: '清爽', emoji: '🌟', color: '#87CEEB' }
]

// 梦境类型选项
export const DREAM_TYPE_OPTIONS = [
  { value: 'NORMAL', label: '普通梦', emoji: '💭' },
  { value: 'LUCID', label: '清明梦', emoji: '✨' },
  { value: 'NIGHTMARE', label: '噩梦', emoji: '😱' },
  { value: 'RECURRING', label: '重复梦', emoji: '🔄' },
  { value: 'PROPHETIC', label: '预知梦', emoji: '🔮' }
]
