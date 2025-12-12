<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDreamStore } from '@/stores/dream'
import type { Dream } from '@/types'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'

const router = useRouter()
const dreamStore = useDreamStore()

const searchKeyword = ref('')

const dreams = computed(() => dreamStore.dreams)
const loading = computed(() => dreamStore.loading)
const totalPages = computed(() => dreamStore.totalPages)
const currentPage = computed(() => dreamStore.currentPage)

onMounted(() => {
  fetchDreams()
})

async function fetchDreams(page = 0) {
  await dreamStore.fetchDreams(page, 10, searchKeyword.value || undefined)
}

function handleSearch() {
  fetchDreams(0)
}

function handlePageChange(page: number) {
  fetchDreams(page)
}

function goToCreate() {
  router.push('/dreams/create')
}

function goToDetail(id: number) {
  router.push(`/dreams/${id}`)
}

async function handleToggleFavorite(dream: Dream, event: Event) {
  event.stopPropagation()
  await dreamStore.toggleFavorite(dream.id)
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function getMoodEmoji(mood?: string) {
  const moodMap: Record<string, string> = {
    HAPPY: '😊',
    CALM: '😌',
    ANXIOUS: '😰',
    SAD: '😢',
    STRESSED: '😣',
    EXCITED: '🤩',
    TIRED: '😴',
    CONFUSED: '😕',
    REFRESHED: '🌟'
  }
  return mood ? moodMap[mood] || '🌙' : '🌙'
}

function getSleepQualityStars(quality?: string) {
  if (!quality) return ''
  const qualityMap: Record<string, number> = {
    'TERRIBLE': 1,
    'POOR': 2,
    'FAIR': 3,
    'GOOD': 4,
    'EXCELLENT': 5
  }
  const stars = qualityMap[quality] || 3
  return '★'.repeat(stars) + '☆'.repeat(5 - stars)
}
</script>

<template>
  <div class="dream-list-view">
    <div class="detail-header-spacer"></div>
    
    <main class="dream-list-view__main">
      <div class="container">
        <!-- 头部 -->
        <header class="dream-list-view__header">
          <div class="header-content">
            <h2 class="dream-list-view__title">梦境日记</h2>
            <p class="dream-list-view__subtitle">
              记录每一个荒诞而真实的梦
            </p>
          </div>
          
          <div class="header-actions">
            <div class="search-wrapper">
              <SakuraInput
                v-model="searchKeyword"
                placeholder="搜索梦境..."
                clearable
                @keyup.enter="handleSearch"
                class="search-input"
              >
                <template #prefix>🔍</template>
              </SakuraInput>
            </div>
            
            <SakuraButton variant="primary" @click="goToCreate" class="create-btn">
              ✨ 记录梦境
            </SakuraButton>
          </div>
        </header>
        
        <!-- 统计信息栏 -->
        <div class="stats-bar">
          <span class="stats-item">
            共记录 <strong>{{ dreamStore.dreamCount }}</strong> 个梦境
          </span>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="dream-list-view__loading">
          <div class="dream-list-view__spinner"></div>
          <p>加载中...</p>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="dreams.length === 0" class="dream-list-view__empty">
          <div class="dream-list-view__empty-icon">🌙</div>
          <h3>还没有记录任何梦境</h3>
          <p>点击上方按钮，开始记录你的第一个梦境吧！</p>
          <SakuraButton variant="primary" @click="goToCreate">
            记录第一个梦境
          </SakuraButton>
        </div>
        
        <!-- 梦境列表 -->
        <div v-else class="dream-list-view__grid">
          <SakuraCard
            v-for="dream in dreams"
            :key="dream.id"
            variant="glass"
            hoverable
            clickable
            class="dream-card"
            @click="goToDetail(dream.id)"
          >
            <div class="dream-card__header">
              <span class="dream-card__mood">{{ getMoodEmoji(dream.moodBeforeSleep) }}</span>
              <button 
                class="dream-card__favorite"
                :class="{ 'is-favorited': dream.isFavorite }"
                @click="handleToggleFavorite(dream, $event)"
              >
                {{ dream.isFavorite ? '💜' : '🤍' }}
              </button>
            </div>
            
            <h3 class="dream-card__title">{{ dream.title }}</h3>
            
            <p class="dream-card__content text-ellipsis-3">
              {{ dream.content }}
            </p>
            
            <div class="dream-card__meta">
              <span class="dream-card__date">{{ formatDate(dream.dreamDate) }}</span>
              <span v-if="dream.sleepQuality" class="dream-card__quality">
                {{ getSleepQualityStars(dream.sleepQuality) }}
              </span>
            </div>
            
            <div v-if="dream.tags?.length" class="dream-card__tags">
              <span 
                v-for="tag in dream.tags.slice(0, 3)" 
                :key="tag" 
                class="dream-card__tag"
              >
                #{{ tag }}
              </span>
              <span v-if="dream.tags.length > 3" class="dream-card__tag-more">
                +{{ dream.tags.length - 3 }}
              </span>
            </div>
            
            <div v-if="dream.aiSummary" class="dream-card__ai-badge">
              🤖 AI 已分析
            </div>
          </SakuraCard>
        </div>
        
        <!-- 分页 -->
        <div v-if="totalPages > 1" class="dream-list-view__pagination">
          <SakuraButton
            variant="outline"
            size="sm"
            :disabled="currentPage === 0"
            @click="handlePageChange(currentPage - 1)"
          >
            上一页
          </SakuraButton>
          
          <span class="dream-list-view__page-info">
            {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          
          <SakuraButton
            variant="outline"
            size="sm"
            :disabled="currentPage >= totalPages - 1"
            @click="handlePageChange(currentPage + 1)"
          >
            下一页
          </SakuraButton>
        </div>
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.dream-list-view {
  min-height: 100vh;
  
  &__main {
    padding: 20px 0 40px;
  }
  
  &__header {
    display: flex;
    flex-direction: column;
    gap: 24px;
    margin-bottom: 32px;
    
    @include respond-to(md) {
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;
    }
    
    .header-content {
      .dream-list-view__title {
        font-family: $font-family-serif;
        font-size: 2.5rem;
        font-weight: 700;
        color: $text-primary;
        margin: 0 0 8px 0;
        background: $gradient-sakura;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        display: inline-block;
      }
      
      .dream-list-view__subtitle {
        color: $text-secondary;
        font-size: 1rem;
        margin: 0;
        opacity: 0.8;
      }
    }
    
    .header-actions {
      display: flex;
      gap: 16px;
      align-items: center;
      width: 100%;
      
      @include respond-to(md) {
        width: auto;
      }
      
      .search-wrapper {
        flex: 1;
        min-width: 200px;
        
        @include respond-to(md) {
          width: 240px;
        }
      }
      
      .create-btn {
        white-space: nowrap;
      }
    }
  }
  
  .stats-bar {
    margin-bottom: 24px;
    padding: 0 4px;
    
    .stats-item {
      font-size: $font-size-sm;
      color: $text-light;
      
      strong {
        color: $primary-dark;
        font-weight: 600;
      }
    }
  }
  
  &__loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: $text-secondary;
  }
  
  &__spinner {
    width: 48px;
    height: 48px;
    border: 4px solid $border-color;
    border-top-color: $primary;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 16px;
  }
  
  &__empty {
    text-align: center;
    padding: 60px 20px;
    
    &-icon {
      font-size: 80px;
      margin-bottom: 24px;
    }
    
    h3 {
      font-size: $font-size-xl;
      color: $text-primary;
      margin: 0 0 8px 0;
    }
    
    p {
      color: $text-secondary;
      margin: 0 0 24px 0;
    }
  }
  
  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }
  
  &__pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    margin-top: 40px;
  }
  
  &__page-info {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.dream-card {
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  &__mood {
    font-size: 28px;
  }
  
  &__favorite {
    font-size: 24px;
    padding: 4px;
    transition: transform $transition-fast;
    
    &:hover {
      transform: scale(1.2);
    }
  }
  
  &__title {
    font-size: $font-size-lg;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 12px 0;
  }
  
  &__content {
    color: $text-secondary;
    font-size: $font-size-sm;
    line-height: 1.6;
    margin: 0 0 16px 0;
  }
  
  &__meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  &__date {
    color: $text-light;
    font-size: $font-size-xs;
  }
  
  &__quality {
    color: $warning;
    font-size: $font-size-xs;
    letter-spacing: -1px;
  }
  
  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  &__tag {
    padding: 4px 10px;
    background: rgba($primary, 0.1);
    color: $primary-dark;
    font-size: $font-size-xs;
    border-radius: $border-radius-full;
  }
  
  &__tag-more {
    padding: 4px 10px;
    background: rgba($secondary, 0.1);
    color: $secondary-dark;
    font-size: $font-size-xs;
    border-radius: $border-radius-full;
  }
  
  &__ai-badge {
    margin-top: 12px;
    padding: 6px 12px;
    background: $gradient-lavender;
    color: $text-on-primary;
    font-size: $font-size-xs;
    font-weight: 500;
    border-radius: $border-radius-full;
    display: inline-block;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
