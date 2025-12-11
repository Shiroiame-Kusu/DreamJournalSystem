<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import SakuraBackground from './components/common/SakuraBackground.vue'
import SakuraNavbar from './components/ui/SakuraNavbar.vue'

const authStore = useAuthStore()
const route = useRoute()

const shouldShowNavbar = computed(() => {
  // Hide navbar on login/register pages or if explicitly disabled
  return !route.meta.guest && route.name !== 'not-found'
})

// 应用启动时验证 token 有效性
onMounted(async () => {
  if (authStore.token) {
    try {
      await authStore.fetchCurrentUser()
    } catch {
      // Token 无效，清除认证数据
      authStore.clearAuthData()
    }
  }
})
</script>

<template>
  <div class="app">
    <!-- 樱花背景效果 -->
    <SakuraBackground />
    
    <!-- 导航栏 -->
    <SakuraNavbar v-if="shouldShowNavbar" />
    
    <!-- 主内容区域 -->
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style lang="scss">
.app {
  min-height: 100vh;
  position: relative;
}
</style>
