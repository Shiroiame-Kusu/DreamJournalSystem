<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SakuraButton from './SakuraButton.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const mobileMenuOpen = ref(false)
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const navItems = computed(() => [
  { name: '我的梦境', path: '/dreams', icon: '🌙', exact: false },
  { name: '记录梦境', path: '/dreams/create', icon: '✨', exact: true },
  { name: '收藏', path: '/favorites', icon: '💜', exact: false }
])

const isActive = (path: string, exact: boolean = false) => {
  if (exact) {
    return route.path === path
  }
  // 对于 /dreams，需要排除 /dreams/create
  if (path === '/dreams') {
    return route.path === '/dreams' || (route.path.startsWith('/dreams/') && !route.path.startsWith('/dreams/create'))
  }
  return route.path === path || route.path.startsWith(path + '/')
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

async function handleLogout() {
  await authStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <nav class="sakura-navbar" :class="{ 'is-scrolled': isScrolled }">
    <div class="sakura-navbar__container">
      <!-- Logo -->
      <router-link to="/" class="sakura-navbar__logo" @click="closeMobileMenu">
        <span class="sakura-navbar__logo-icon">🌸</span>
        <span class="sakura-navbar__logo-text">梦境日记</span>
      </router-link>
      
      <!-- 桌面导航 -->
      <div class="sakura-navbar__nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="sakura-navbar__link"
          :class="{ 'sakura-navbar__link--active': isActive(item.path, item.exact) }"
        >
          <span class="sakura-navbar__link-icon">{{ item.icon }}</span>
          <span class="sakura-navbar__link-text">{{ item.name }}</span>
        </router-link>
      </div>
      
      <!-- 用户菜单 -->
      <div class="sakura-navbar__user">
        <div class="sakura-navbar__user-info">
          <span class="sakura-navbar__avatar">
            {{ authStore.username.charAt(0).toUpperCase() }}
          </span>
          <span class="sakura-navbar__username">{{ authStore.username }}</span>
        </div>
        
        <div class="sakura-navbar__dropdown">
          <router-link to="/profile" class="sakura-navbar__dropdown-item">
            👤 个人中心
          </router-link>
          <router-link 
            v-if="authStore.isAdmin" 
            to="/admin/users" 
            class="sakura-navbar__dropdown-item"
          >
            ⚙️ 用户管理
          </router-link>
          <button class="sakura-navbar__dropdown-item" @click="handleLogout">
            🚪 退出登录
          </button>
        </div>
      </div>
      
      <!-- 移动端菜单按钮 -->
      <button class="sakura-navbar__mobile-toggle" @click="toggleMobileMenu">
        <span :class="{ 'is-open': mobileMenuOpen }">
          <span></span>
          <span></span>
          <span></span>
        </span>
      </button>
    </div>
    
    <!-- 移动端菜单 -->
    <div class="sakura-navbar__mobile-menu" :class="{ 'is-open': mobileMenuOpen }">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="sakura-navbar__mobile-link"
        @click="closeMobileMenu"
      >
        <span>{{ item.icon }}</span>
        <span>{{ item.name }}</span>
      </router-link>
      
      <router-link to="/profile" class="sakura-navbar__mobile-link" @click="closeMobileMenu">
        <span>👤</span>
        <span>个人中心</span>
      </router-link>
      
      <router-link 
        v-if="authStore.isAdmin" 
        to="/admin/users" 
        class="sakura-navbar__mobile-link"
        @click="closeMobileMenu"
      >
        <span>⚙️</span>
        <span>用户管理</span>
      </router-link>
      
      <SakuraButton variant="outline" block @click="handleLogout">
        退出登录
      </SakuraButton>
    </div>
  </nav>
</template>

<style lang="scss" scoped>


.sakura-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
  
  &.is-scrolled {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(12px);
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  }
  
  &__container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    max-width: 1200px;
    margin: 0 auto;
    padding: 12px 24px;
    gap: 24px;
  }
  
  &__logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: $font-size-xl;
    font-weight: 700;
    color: $primary-dark;
    text-decoration: none;
    
    &-icon {
      font-size: 28px;
    }
    
    &-text {
      background: $gradient-sakura;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
  
  &__nav {
    display: none;
    align-items: center;
    gap: 8px;
    
    @include respond-to(md) {
      display: flex;
    }
  }
  
  &__link {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 16px;
    border-radius: $border-radius-full;
    color: $text-secondary;
    text-decoration: none;
    font-weight: 500;
    transition: all $transition;
    
    &:hover {
      background: rgba($primary, 0.1);
      color: $primary-dark;
    }
    
    &--active {
      background: $gradient-sakura;
      color: $text-on-primary;
      
      &:hover {
        background: $gradient-sakura;
        color: $text-on-primary;
      }
    }
    
    &-icon {
      font-size: $font-size-lg;
    }
  }
  
  &__user {
    display: none;
    position: relative;
    
    @include respond-to(md) {
      display: block;
    }
    
    &:hover &-info {
      background: rgba($primary, 0.1);
    }
    
    &:hover .sakura-navbar__dropdown {
      opacity: 1;
      visibility: visible;
      transform: translateY(0);
    }
    
    &-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-radius: $border-radius-full;
      cursor: pointer;
      transition: all $transition;
    }
  }
  
  &__avatar {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: $gradient-sakura;
    color: $text-on-primary;
    font-weight: 600;
    border-radius: $border-radius-full;
  }
  
  &__username {
    font-weight: 500;
    color: $text-primary;
  }
  
  &__dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 8px;
    min-width: 160px;
    background: $background-card;
    border-radius: $border-radius;
    box-shadow: $shadow-lg;
    padding: 8px;
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all $transition;
    
    &-item {
      display: flex;
      align-items: center;
      gap: 8px;
      width: 100%;
      padding: 10px 12px;
      border-radius: $border-radius-sm;
      color: $text-primary;
      font-size: $font-size-sm;
      text-decoration: none;
      text-align: left;
      transition: all $transition-fast;
      
      &:hover {
        background: rgba($primary, 0.1);
        color: $primary-dark;
      }
    }
  }
  
  &__mobile-toggle {
    display: flex;
    padding: 8px;
    
    @include respond-to(md) {
      display: none;
    }
    
    span {
      display: flex;
      flex-direction: column;
      gap: 5px;
      
      span {
        display: block;
        width: 24px;
        height: 2px;
        background: $primary;
        border-radius: 2px;
        transition: all $transition;
      }
      
      &.is-open {
        span:nth-child(1) {
          transform: rotate(45deg) translate(5px, 5px);
        }
        
        span:nth-child(2) {
          opacity: 0;
        }
        
        span:nth-child(3) {
          transform: rotate(-45deg) translate(5px, -5px);
        }
      }
    }
  }
  
  &__mobile-menu {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 16px 24px;
    background: $background-card;
    border-top: 1px solid $border-color;
    max-height: 0;
    overflow: hidden;
    transition: all $transition;
    
    @include respond-to(md) {
      display: none;
    }
    
    &.is-open {
      max-height: 400px;
      padding-bottom: 24px;
    }
  }
  
  &__mobile-link {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: $border-radius;
    color: $text-primary;
    text-decoration: none;
    font-weight: 500;
    transition: all $transition;
    
    &:hover {
      background: rgba($primary, 0.1);
    }
  }
}
</style>
