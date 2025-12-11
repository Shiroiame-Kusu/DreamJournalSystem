<script setup lang="ts">
import { useRouter } from 'vue-router'
import SakuraButton from '@/components/ui/SakuraButton.vue'

const router = useRouter()

function goHome() {
  router.push('/dreams')
}

function goBack() {
  router.back()
}
</script>

<template>
  <div class="not-found-view">
    <div class="not-found-view__content">
      <div class="not-found-view__icon">🌸</div>
      <h1 class="not-found-view__code">404</h1>
      <h2 class="not-found-view__title">页面未找到</h2>
      <p class="not-found-view__message">
        你访问的页面已经消散在梦境中了...
      </p>
      <div class="not-found-view__actions">
        <SakuraButton variant="ghost" @click="goBack">
          ← 返回上页
        </SakuraButton>
        <SakuraButton variant="primary" @click="goHome">
          回到首页
        </SakuraButton>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>


.not-found-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  
  &__content {
    text-align: center;
    animation: fadeIn 0.6s ease;
  }
  
  &__icon {
    font-size: 80px;
    margin-bottom: 24px;
    animation: float 3s ease-in-out infinite;
  }
  
  &__code {
    font-size: 120px;
    font-weight: 900;
    background: $gradient-sakura;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
    margin: 0 0 16px 0;
  }
  
  &__title {
    font-size: $font-size-2xl;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 12px 0;
  }
  
  &__message {
    color: $text-secondary;
    margin: 0 0 32px 0;
  }
  
  &__actions {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}
</style>
