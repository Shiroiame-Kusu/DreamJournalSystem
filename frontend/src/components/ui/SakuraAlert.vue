<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  type?: 'success' | 'warning' | 'error' | 'info'
  title?: string
  closable?: boolean
  showIcon?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  closable: false,
  showIcon: true
})

const emit = defineEmits<{
  close: []
}>()

const icons = {
  success: '✓',
  warning: '⚠',
  error: '✕',
  info: 'ℹ'
}

const icon = computed(() => icons[props.type])

function handleClose() {
  emit('close')
}
</script>

<template>
  <div :class="['sakura-alert', `sakura-alert--${type}`]">
    <span v-if="showIcon" class="sakura-alert__icon">{{ icon }}</span>
    
    <div class="sakura-alert__content">
      <div v-if="title" class="sakura-alert__title">{{ title }}</div>
      <div class="sakura-alert__message">
        <slot />
      </div>
    </div>
    
    <button v-if="closable" class="sakura-alert__close" @click="handleClose">
      ✕
    </button>
  </div>
</template>

<style lang="scss" scoped>


.sakura-alert {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: $border-radius;
  
  &--success {
    background: rgba($success, 0.15);
    border: 1px solid rgba($success, 0.3);
    
    .sakura-alert__icon {
      color: darken($success, 20%);
    }
  }
  
  &--warning {
    background: rgba($warning, 0.15);
    border: 1px solid rgba($warning, 0.3);
    
    .sakura-alert__icon {
      color: darken($warning, 20%);
    }
  }
  
  &--error {
    background: rgba($error, 0.15);
    border: 1px solid rgba($error, 0.3);
    
    .sakura-alert__icon {
      color: darken($error, 20%);
    }
  }
  
  &--info {
    background: rgba($info, 0.15);
    border: 1px solid rgba($info, 0.3);
    
    .sakura-alert__icon {
      color: darken($info, 20%);
    }
  }
  
  &__icon {
    flex-shrink: 0;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
  }
  
  &__content {
    flex: 1;
  }
  
  &__title {
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 4px;
  }
  
  &__message {
    font-size: $font-size-sm;
    color: $text-secondary;
    line-height: 1.5;
  }
  
  &__close {
    flex-shrink: 0;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-light;
    font-size: $font-size-sm;
    border-radius: $border-radius-sm;
    transition: all $transition-fast;
    
    &:hover {
      background: rgba(0, 0, 0, 0.1);
      color: $text-primary;
    }
  }
}
</style>
