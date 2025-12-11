<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'

interface Props {
  modelValue: boolean
  title?: string
  closable?: boolean
  closeOnOverlay?: boolean
  closeOnEsc?: boolean
  width?: string
  fullscreen?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  closable: true,
  closeOnOverlay: true,
  closeOnEsc: true,
  width: '480px',
  fullscreen: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const modalRef = ref<HTMLElement>()

// 关闭模态框
function close() {
  emit('update:modelValue', false)
  emit('close')
}

// 点击遮罩层
function handleOverlayClick() {
  if (props.closeOnOverlay) {
    close()
  }
}

// ESC 键关闭
function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape' && props.closeOnEsc && props.modelValue) {
    close()
  }
}

// 锁定/解锁滚动
watch(() => props.modelValue, (value) => {
  if (value) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="sakura-modal" ref="modalRef">
        <div class="sakura-modal__overlay" @click="handleOverlayClick" />
        
        <div 
          class="sakura-modal__container"
          :class="{ 'sakura-modal__container--fullscreen': fullscreen }"
          :style="{ maxWidth: fullscreen ? '100%' : width }"
        >
          <!-- 头部 -->
          <div v-if="title || closable" class="sakura-modal__header">
            <h3 v-if="title" class="sakura-modal__title">{{ title }}</h3>
            <slot name="header" />
            
            <button 
              v-if="closable" 
              class="sakura-modal__close" 
              @click="close"
              aria-label="关闭"
            >
              ✕
            </button>
          </div>
          
          <!-- 内容 -->
          <div class="sakura-modal__body">
            <slot />
          </div>
          
          <!-- 底部 -->
          <div v-if="$slots.footer" class="sakura-modal__footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style lang="scss" scoped>


.sakura-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1000;
  
  &__overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(4px);
  }
  
  &__container {
    position: relative;
    width: 100%;
    background: $background;
    border-radius: $border-radius-xl;
    box-shadow: $shadow-xl;
    overflow: hidden;
    
    &--fullscreen {
      height: 100%;
      border-radius: 0;
    }
  }
  
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 24px;
    border-bottom: 1px solid $border-color;
  }
  
  &__title {
    font-size: $font-size-xl;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }
  
  &__close {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    color: $text-secondary;
    font-size: $font-size-lg;
    border-radius: $border-radius-full;
    transition: all $transition-fast;
    
    &:hover {
      background: rgba($error, 0.1);
      color: $error;
    }
  }
  
  &__body {
    padding: 24px;
    max-height: calc(100vh - 200px);
    overflow-y: auto;
  }
  
  &__footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 24px;
    border-top: 1px solid $border-color;
  }
}

// 过渡动画
.modal-enter-active,
.modal-leave-active {
  transition: opacity $transition;
  
  .sakura-modal__container {
    transition: transform $transition;
  }
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  
  .sakura-modal__container {
    transform: scale(0.95) translateY(-20px);
  }
}
</style>
