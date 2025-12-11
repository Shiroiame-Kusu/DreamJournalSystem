<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  disabled?: boolean
  block?: boolean
  icon?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  block: false,
  icon: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const classes = computed(() => [
  'sakura-button',
  `sakura-button--${props.variant}`,
  `sakura-button--${props.size}`,
  {
    'sakura-button--loading': props.loading,
    'sakura-button--disabled': props.disabled,
    'sakura-button--block': props.block,
    'sakura-button--icon': props.icon
  }
])

function handleClick(event: MouseEvent) {
  if (!props.loading && !props.disabled) {
    emit('click', event)
  }
}
</script>

<template>
  <button :class="classes" :disabled="disabled || loading" @click="handleClick">
    <span v-if="loading" class="sakura-button__loader">
      <svg class="spinner" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="3" />
      </svg>
    </span>
    <span class="sakura-button__content" :class="{ 'sakura-button__content--hidden': loading }">
      <slot />
    </span>
  </button>
</template>

<style lang="scss" scoped>


.sakura-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: $font-family;
  font-weight: 500;
  line-height: 1.5;
  border-radius: $border-radius-full;
  cursor: pointer;
  transition: all $transition;
  overflow: hidden;
  
  // 尺寸
  &--sm {
    padding: 8px 16px;
    font-size: $font-size-sm;
    min-height: 32px;
  }
  
  &--md {
    padding: 12px 24px;
    font-size: $font-size-base;
    min-height: 44px;
  }
  
  &--lg {
    padding: 16px 32px;
    font-size: $font-size-lg;
    min-height: 52px;
  }
  
  // 主要按钮
  &--primary {
    background: $gradient-sakura;
    color: $text-on-primary;
    border: none;
    box-shadow: $shadow;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: $shadow-lg;
    }
    
    &:active:not(:disabled) {
      transform: translateY(0);
    }
  }
  
  // 次要按钮
  &--secondary {
    background: $gradient-lavender;
    color: $text-on-primary;
    border: none;
    box-shadow: $shadow;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: $shadow-lg;
    }
  }
  
  // 轮廓按钮
  &--outline {
    background: transparent;
    color: $primary-dark;
    border: 2px solid $primary;
    
    &:hover:not(:disabled) {
      background: rgba($primary, 0.1);
      border-color: $primary-dark;
    }
  }
  
  // 幽灵按钮
  &--ghost {
    background: transparent;
    color: $text-primary;
    border: none;
    
    &:hover:not(:disabled) {
      background: rgba($primary, 0.1);
      color: $primary-dark;
    }
  }
  
  // 危险按钮
  &--danger {
    background: $error;
    color: $text-on-primary;
    border: none;
    
    &:hover:not(:disabled) {
      background: darken($error, 10%);
      transform: translateY(-2px);
    }
  }
  
  // 块级按钮
  &--block {
    width: 100%;
  }
  
  // 图标按钮
  &--icon {
    padding: 12px;
    min-width: 44px;
    
    &.sakura-button--sm {
      padding: 8px;
      min-width: 32px;
    }
    
    &.sakura-button--lg {
      padding: 16px;
      min-width: 52px;
    }
  }
  
  // 禁用状态
  &--disabled,
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none !important;
  }
  
  // 加载状态
  &--loading {
    cursor: wait;
  }
  
  &__loader {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .spinner {
      width: 20px;
      height: 20px;
      animation: spin 1s linear infinite;
      
      circle {
        stroke-dasharray: 60;
        stroke-dashoffset: 45;
        stroke-linecap: round;
      }
    }
  }
  
  &__content {
    display: flex;
    align-items: center;
    gap: 8px;
    
    &--hidden {
      visibility: hidden;
    }
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
