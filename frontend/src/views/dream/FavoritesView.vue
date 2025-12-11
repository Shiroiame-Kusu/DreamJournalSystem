<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDreamStore } from '@/stores/dream'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'

const router = useRouter()
const dreamStore = useDreamStore()

const favorites = computed(() => dreamStore.favorites)
const loading = computed(() => dreamStore.loading)

onMounted(() => {
  dreamStore.fetchFavorites()
})

function goToDetail(id: number) {
  router.push(`/dreams/${id}`)
}

async function handleToggleFavorite(id: number, event: Event) {
  event.stopPropagation()
  await dreamStore.toggleFavorite(id)
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
    happy: '😊',
    peaceful: '😌',
    anxious: '😰',
    sad: '😢',
    scared: '😨',
    confused: '😕',
    excited: '🤩',
    neutral: '😶'
  }
  return mood ? moodMap[mood] || '🌙' : '🌙'
}
</script>

<template>
  <div class="favorites-view">
    <div class="detail-header-spacer"></div>
    
    <main class="favorites-view__main">
      <div class="container">
        <header class="favorites-view__header">
          <h1 class="favorites-view__title">💜 收藏的梦境</h1>
          <p class="favorites-view__subtitle">
            共收藏了 {{ favorites.length }} 个梦境
          </p>
        </header>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="favorites-view__loading">
          <div class="spinner"></div>
          <p>加载中...</p>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="favorites.length === 0" class="favorites-view__empty">
          <div class="favorites-view__empty-icon">💜</div>
          <h3>还没有收藏任何梦境</h3>
          <p>在梦境详情页点击收藏按钮，就可以在这里看到啦！</p>
          <SakuraButton variant="primary" @click="router.push('/dreams')">
            浏览我的梦境
          </SakuraButton>
        </div>
        
        <!-- 收藏列表 -->
        <div v-else class="favorites-view__grid">
          <SakuraCard
            v-for="dream in favorites"
            :key="dream.id"
            variant="glass"
            hoverable
            clickable
            class="favorite-card"
            @click="goToDetail(dream.id)"
          >
            <div class="favorite-card__header">
              <span class="favorite-card__mood">{{ getMoodEmoji(dream.mood) }}</span>
              <button 
                class="favorite-card__favorite"
                @click="handleToggleFavorite(dream.id, $event)"
              >
                💜
              </button>
            </div>
            
            <h3 class="favorite-card__title">{{ dream.title }}</h3>
            
            <p class="favorite-card__content text-ellipsis-3">
              {{ dream.content }}
            </p>
            
            <div class="favorite-card__meta">
              <span class="favorite-card__date">{{ formatDate(dream.dreamDate) }}</span>
            </div>
            
            <div v-if="dream.tags?.length" class="favorite-card__tags">
              <span 
                v-for="tag in dream.tags.slice(0, 3)" 
                :key="tag" 
                class="favorite-card__tag"
              >
                #{{ tag }}
              </span>
            </div>
          </SakuraCard>
        </div>
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.favorites-view {
  min-height: 100vh;
  
  &__main {
    padding: 32px 0;
  }
  
  &__header {
    margin-bottom: 32px;
  }
  
  &__title {
    font-size: $font-size-3xl;
    font-weight: 700;
    color: $text-primary;
    margin: 0;
  }
  
  &__subtitle {
    color: $text-secondary;
    margin: 8px 0 0 0;
  }
  
  &__loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: $text-secondary;
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
}

.favorite-card {
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
    margin-bottom: 12px;
  }
  
  &__date {
    color: $text-light;
    font-size: $font-size-xs;
  }
  
  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  &__tag {
    padding: 4px 10px;
    background: rgba($secondary, 0.1);
    color: $secondary-dark;
    font-size: $font-size-xs;
    border-radius: $border-radius-full;
  }
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid $border-color;
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
