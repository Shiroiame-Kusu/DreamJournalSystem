<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDreamStore } from '@/stores/dream'
import { MOOD_OPTIONS } from '@/types'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraModal from '@/components/ui/SakuraModal.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

const route = useRoute()
const router = useRouter()
const dreamStore = useDreamStore()

const dreamId = computed(() => Number(route.params.id))
const dream = computed(() => dreamStore.currentDream)
const loading = computed(() => dreamStore.loading)

const showDeleteModal = ref(false)
const aiLoading = ref(false)
const deleteLoading = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  try {
    await dreamStore.fetchDream(dreamId.value)
  } catch {
    router.push('/dreams')
  }
})

function getMoodInfo(mood?: string) {
  return MOOD_OPTIONS.find(m => m.value === mood)
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

function getSleepQualityText(quality?: number) {
  const texts = ['', '很差', '较差', '一般', '较好', '很好']
  return quality ? texts[quality] : ''
}

async function handleToggleFavorite() {
  if (!dream.value) return
  await dreamStore.toggleFavorite(dream.value.id)
}

async function handleGenerateAI() {
  if (!dream.value) return
  
  aiLoading.value = true
  errorMessage.value = ''
  
  try {
    await dreamStore.generateAISummary(dream.value.id)
  } catch (error: any) {
    errorMessage.value = error.message || error.response?.data?.message || 'AI 分析生成失败，请稍后重试'
  } finally {
    aiLoading.value = false
  }
}

async function handleRegenerateAI() {
  if (!dream.value) return
  
  aiLoading.value = true
  errorMessage.value = ''
  
  try {
    await dreamStore.generateAISummary(dream.value.id)
  } catch (error: any) {
    errorMessage.value = error.message || error.response?.data?.message || 'AI 分析重新生成失败，请稍后重试'
  } finally {
    aiLoading.value = false
  }
}

async function handleDelete() {
  if (!dream.value) return
  
  deleteLoading.value = true
  
  try {
    await dreamStore.deleteDream(dream.value.id)
    router.push('/dreams')
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '删除失败，请稍后重试'
    showDeleteModal.value = false
  } finally {
    deleteLoading.value = false
  }
}

function goToEdit() {
  router.push(`/dreams/${dreamId.value}/edit`)
}

function goBack() {
  router.push('/dreams')
}
</script>

<template>
  <div class="dream-detail-view">
    <div class="detail-header-spacer"></div>
    
    <main class="dream-detail-view__main">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading && !dream" class="dream-detail-view__loading">
          <div class="spinner"></div>
          <p>加载中...</p>
        </div>
        
        <template v-else-if="dream">
          <!-- 头部操作栏 -->
          <div class="dream-detail-view__actions-bar">
            <SakuraButton variant="ghost" @click="goBack">
              ← 返回列表
            </SakuraButton>
            
            <div class="dream-detail-view__actions">
              <SakuraButton
                :variant="dream.isFavorite ? 'secondary' : 'outline'"
                @click="handleToggleFavorite"
              >
                {{ dream.isFavorite ? '💜 已收藏' : '🤍 收藏' }}
              </SakuraButton>
              <SakuraButton variant="outline" @click="goToEdit">
                ✏️ 编辑
              </SakuraButton>
              <SakuraButton variant="danger" @click="showDeleteModal = true">
                🗑️ 删除
              </SakuraButton>
            </div>
          </div>
          
          <SakuraAlert v-if="errorMessage" type="error" closable @close="errorMessage = ''">
            {{ errorMessage }}
          </SakuraAlert>
          
          <!-- 梦境内容卡片 -->
          <SakuraCard variant="glass" padding="lg" class="dream-detail-view__content">
            <header class="dream-detail-view__header">
              <div class="dream-detail-view__meta">
                <span v-if="getMoodInfo(dream.mood)" class="dream-detail-view__mood">
                  {{ getMoodInfo(dream.mood)?.emoji }} {{ getMoodInfo(dream.mood)?.label }}
                </span>
                <span class="dream-detail-view__date">{{ formatDate(dream.dreamDate) }}</span>
                <span v-if="dream.sleepQuality" class="dream-detail-view__quality">
                  😴 睡眠质量: {{ getSleepQualityText(dream.sleepQuality) }}
                </span>
              </div>
              
              <h1 class="dream-detail-view__title">{{ dream.title }}</h1>
              
              <div v-if="dream.tags?.length" class="dream-detail-view__tags">
                <span v-for="tag in dream.tags" :key="tag" class="dream-detail-view__tag">
                  #{{ tag }}
                </span>
              </div>
            </header>
            
            <article class="dream-detail-view__body">
              <p v-for="(paragraph, index) in dream.content.split('\n')" :key="index">
                {{ paragraph }}
              </p>
            </article>
          </SakuraCard>
          
          <!-- AI 分析卡片 -->
          <SakuraCard variant="glass" padding="lg" class="dream-detail-view__ai">
            <header class="dream-detail-view__ai-header">
              <h2 class="dream-detail-view__ai-title">🤖 AI 梦境分析</h2>
              
              <SakuraButton
                v-if="!dream.aiSummary || dream.aiSummary.status === 'FAILED'"
                variant="secondary"
                :loading="aiLoading"
                @click="handleGenerateAI"
              >
                ✨ {{ dream.aiSummary?.status === 'FAILED' ? '重新生成' : '生成 AI 分析' }}
              </SakuraButton>
              
              <SakuraButton
                v-else-if="dream.aiSummary.status === 'COMPLETED'"
                variant="outline"
                :loading="aiLoading"
                @click="handleRegenerateAI"
              >
                🔄 重新生成
              </SakuraButton>
            </header>
            
            <!-- PENDING 状态 -->
            <div v-if="dream.aiSummary?.status === 'PENDING' || aiLoading" class="dream-detail-view__ai-loading">
              <div class="spinner"></div>
              <p>AI 正在分析你的梦境...</p>
            </div>
            
            <!-- FAILED 状态 -->
            <div v-else-if="dream.aiSummary?.status === 'FAILED'" class="dream-detail-view__ai-failed">
              <p>❌ AI 分析失败: {{ dream.aiSummary.errorMessage || '未知错误' }}</p>
              <p>请点击上方按钮重新生成</p>
            </div>
            
            <!-- COMPLETED 状态 -->
            <div v-else-if="dream.aiSummary?.status === 'COMPLETED'" class="dream-detail-view__ai-content">
              <!-- 总结 -->
              <section v-if="dream.aiSummary.summary" class="ai-section">
                <h3>📝 梦境总结</h3>
                <p>{{ dream.aiSummary.summary }}</p>
              </section>
              
              <!-- 象征符号 -->
              <section v-if="dream.aiSummary.symbolAnalysis?.symbols?.length" class="ai-section">
                <h3>🔮 象征解读</h3>
                <div class="ai-symbols">
                  <div 
                    v-for="symbol in dream.aiSummary.symbolAnalysis.symbols" 
                    :key="symbol.symbol" 
                    class="ai-symbol"
                  >
                    <div class="ai-symbol__name">{{ symbol.symbol }}</div>
                    <div class="ai-symbol__meaning">{{ symbol.meaning }}</div>
                    <div v-if="symbol.psychologicalInterpretation" class="ai-symbol__significance">
                      {{ symbol.psychologicalInterpretation }}
                    </div>
                  </div>
                </div>
                <p v-if="dream.aiSummary.symbolAnalysis.overallTheme" class="ai-theme-summary">
                  <strong>整体主题:</strong> {{ dream.aiSummary.symbolAnalysis.overallTheme }}
                </p>
                <p v-if="dream.aiSummary.symbolAnalysis.lifeConnection" class="ai-life-connection">
                  <strong>生活关联:</strong> {{ dream.aiSummary.symbolAnalysis.lifeConnection }}
                </p>
              </section>
              
              <!-- 情绪分析 -->
              <section v-if="dream.aiSummary.emotionAnalysis" class="ai-section">
                <h3>💭 情绪分析</h3>
                <div v-if="dream.aiSummary.emotionAnalysis.dominantEmotion" class="ai-dominant-emotion">
                  <strong>主导情绪:</strong> {{ dream.aiSummary.emotionAnalysis.dominantEmotion }}
                </div>
                <div v-if="dream.aiSummary.emotionAnalysis.emotionsDetected?.length" class="ai-emotions">
                  <div 
                    v-for="emotion in dream.aiSummary.emotionAnalysis.emotionsDetected" 
                    :key="emotion.emotion" 
                    class="ai-emotion"
                  >
                    <div class="ai-emotion__header">
                      <span class="ai-emotion__name">{{ emotion.emotion }}</span>
                      <span class="ai-emotion__intensity">
                        {{ Math.round((emotion.score || 0) * 100) }}%
                      </span>
                    </div>
                    <div class="ai-emotion__bar">
                      <div 
                        class="ai-emotion__fill" 
                        :style="{ width: `${(emotion.score || 0) * 100}%` }"
                      ></div>
                    </div>
                  </div>
                </div>
              </section>
              
              <!-- 关键词 -->
              <section v-if="dream.aiSummary.keywords" class="ai-section">
                <h3>🏷️ 关键词</h3>
                <div class="ai-keywords">
                  <div v-if="dream.aiSummary.keywords.primary?.length" class="ai-keyword-group">
                    <span class="ai-keyword-label">主要:</span>
                    <span v-for="kw in dream.aiSummary.keywords.primary" :key="kw" class="ai-keyword ai-keyword--primary">
                      {{ kw }}
                    </span>
                  </div>
                  <div v-if="dream.aiSummary.keywords.secondary?.length" class="ai-keyword-group">
                    <span class="ai-keyword-label">次要:</span>
                    <span v-for="kw in dream.aiSummary.keywords.secondary" :key="kw" class="ai-keyword ai-keyword--secondary">
                      {{ kw }}
                    </span>
                  </div>
                  <div v-if="dream.aiSummary.keywords.emotions?.length" class="ai-keyword-group">
                    <span class="ai-keyword-label">情绪:</span>
                    <span v-for="kw in dream.aiSummary.keywords.emotions" :key="kw" class="ai-keyword ai-keyword--emotion">
                      {{ kw }}
                    </span>
                  </div>
                </div>
              </section>
              
              <!-- 深度解读 -->
              <section v-if="dream.aiSummary.psychologicalInsight" class="ai-section">
                <h3>🌟 深度解读</h3>
                <p>{{ dream.aiSummary.psychologicalInsight }}</p>
              </section>
              
              <!-- 建议 -->
              <section v-if="dream.aiSummary.advice" class="ai-section">
                <h3>💡 建议</h3>
                <p>{{ dream.aiSummary.advice }}</p>
              </section>
            </div>
            
            <div v-else class="dream-detail-view__ai-empty">
              <p>点击上方按钮，让 AI 为你分析这个梦境的含义</p>
            </div>
          </SakuraCard>
        </template>
      </div>
    </main>
    
    <!-- 删除确认弹窗 -->
    <SakuraModal v-model="showDeleteModal" title="确认删除" width="400px">
      <p>确定要删除这个梦境吗？此操作无法撤销。</p>
      
      <template #footer>
        <SakuraButton variant="ghost" @click="showDeleteModal = false">
          取消
        </SakuraButton>
        <SakuraButton 
          variant="danger" 
          :loading="deleteLoading"
          @click="handleDelete"
        >
          确认删除
        </SakuraButton>
      </template>
    </SakuraModal>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.dream-detail-view {
  min-height: 100vh;
  
  &__main {
    padding: 32px 0;
    max-width: 800px;
    margin: 0 auto;
  }
  
  &__loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: $text-secondary;
  }
  
  &__actions-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 16px;
  }
  
  &__actions {
    display: flex;
    gap: 12px;
  }
  
  &__content {
    margin-bottom: 24px;
  }
  
  &__header {
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid $border-color;
  }
  
  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 16px;
    font-size: $font-size-sm;
    color: $text-secondary;
  }
  
  &__mood {
    padding: 4px 12px;
    background: $gradient-sakura;
    color: $text-on-primary;
    border-radius: $border-radius-full;
  }
  
  &__title {
    font-size: $font-size-2xl;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 16px 0;
  }
  
  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  &__tag {
    padding: 4px 12px;
    background: rgba($primary, 0.1);
    color: $primary-dark;
    font-size: $font-size-sm;
    border-radius: $border-radius-full;
  }
  
  &__body {
    font-size: $font-size-base;
    line-height: 1.8;
    color: $text-primary;
    
    p {
      margin: 0 0 16px 0;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &:empty {
        display: none;
      }
    }
  }
  
  &__ai {
    &-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }
    
    &-title {
      font-size: $font-size-xl;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
    }
    
    &-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 40px 20px;
      color: $text-secondary;
    }
    
    &-empty {
      text-align: center;
      padding: 40px 20px;
      color: $text-secondary;
    }
  }
}

.ai-section {
  margin-bottom: 32px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  h3 {
    font-size: $font-size-lg;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 16px 0;
  }
  
  p {
    color: $text-secondary;
    line-height: 1.7;
    margin: 0;
  }
}

.ai-symbols {
  display: grid;
  gap: 16px;
}

.ai-symbol {
  padding: 16px;
  background: rgba($primary, 0.05);
  border-radius: $border-radius;
  
  &__name {
    font-weight: 600;
    color: $primary-dark;
    margin-bottom: 8px;
  }
  
  &__meaning {
    color: $text-primary;
    margin-bottom: 4px;
  }
  
  &__significance {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

.ai-emotions {
  display: grid;
  gap: 16px;
}

.ai-emotion {
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }
  
  &__name {
    font-weight: 500;
    color: $text-primary;
  }
  
  &__intensity {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
  
  &__bar {
    height: 8px;
    background: rgba($primary, 0.1);
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 8px;
  }
  
  &__fill {
    height: 100%;
    background: $gradient-sakura;
    border-radius: 4px;
    transition: width $transition;
  }
  
  &__context {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

.ai-themes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ai-theme {
  padding: 8px 16px;
  background: $gradient-lavender;
  color: $text-on-primary;
  font-size: $font-size-sm;
  font-weight: 500;
  border-radius: $border-radius-full;
}

// 关键词样式
.ai-keywords {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-keyword-group {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.ai-keyword-label {
  font-size: $font-size-sm;
  color: $text-secondary;
  min-width: 40px;
}

.ai-keyword {
  padding: 4px 12px;
  font-size: $font-size-sm;
  border-radius: $border-radius-full;
  
  &--primary {
    background: rgba($primary, 0.15);
    color: $primary-dark;
  }
  
  &--secondary {
    background: rgba($text-secondary, 0.1);
    color: $text-secondary;
  }
  
  &--emotion {
    background: rgba(#e91e63, 0.15);
    color: #c2185b;
  }
}

.ai-dominant-emotion {
  font-size: $font-size-lg;
  margin-bottom: 16px;
  color: $text-primary;
}

.ai-theme-summary,
.ai-life-connection {
  margin-top: 12px;
  font-size: $font-size-sm;
  color: $text-secondary;
  line-height: 1.6;
}

.dream-detail-view__ai-failed {
  text-align: center;
  padding: 32px;
  color: #d32f2f;
  
  p:first-child {
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  p:last-child {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid $border-color;
  border-top-color: $primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
