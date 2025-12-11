<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 樱花花瓣配置
interface Petal {
  id: number
  left: number
  delay: number
  duration: number
  size: number
}

const petals = ref<Petal[]>([])
const maxPetals = 15
let petalId = 0

// 创建花瓣
function createPetal(): Petal {
  return {
    id: petalId++,
    left: Math.random() * 100,
    delay: Math.random() * 5,
    duration: 8 + Math.random() * 7,
    size: 8 + Math.random() * 8
  }
}

// 初始化花瓣
function initPetals() {
  petals.value = Array(maxPetals).fill(null).map(() => createPetal())
}

// 定时更新花瓣
let interval: number | null = null

onMounted(() => {
  initPetals()
  interval = window.setInterval(() => {
    if (petals.value.length < maxPetals) {
      petals.value.push(createPetal())
    }
  }, 2000)
})

onUnmounted(() => {
  if (interval) {
    clearInterval(interval)
  }
})

// 花瓣动画结束后移除
function removePetal(id: number) {
  petals.value = petals.value.filter(p => p.id !== id)
}
</script>

<template>
  <div class="sakura-background">
    <div
      v-for="petal in petals"
      :key="petal.id"
      class="petal"
      :style="{
        left: `${petal.left}%`,
        animationDelay: `${petal.delay}s`,
        animationDuration: `${petal.duration}s`,
        width: `${petal.size}px`,
        height: `${petal.size}px`
      }"
      @animationend="removePetal(petal.id)"
    />
  </div>
</template>

<style lang="scss" scoped>
.sakura-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

.petal {
  position: absolute;
  top: -20px;
  background: $gradient-sakura;
  border-radius: 150% 0 150% 0;
  opacity: 0.7;
  animation: sakuraFall linear forwards;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) rotate(45deg);
    width: 30%;
    height: 60%;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 50%;
  }
}

@keyframes sakuraFall {
  0% {
    transform: translateY(0) rotate(0deg) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 0.7;
  }
  90% {
    opacity: 0.7;
  }
  100% {
    transform: translateY(100vh) rotate(720deg) translateX(100px);
    opacity: 0;
  }
}
</style>
