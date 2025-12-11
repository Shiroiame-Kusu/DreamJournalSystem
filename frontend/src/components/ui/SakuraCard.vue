<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'glass' | 'outline'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  hoverable?: boolean
  clickable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'glass',
  padding: 'md',
  hoverable: false,
  clickable: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const classes = computed(() => [
  'sakura-card',
  `sakura-card--${props.variant}`,
  `sakura-card--padding-${props.padding}`,
  {
    'sakura-card--hoverable': props.hoverable,
    'sakura-card--clickable': props.clickable
  }
])

function handleClick(event: MouseEvent) {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<template>
  <div :class="classes" @click="handleClick">
    <div v-if="$slots.header" class="sakura-card__header">
      <slot name="header" />
    </div>
    
    <div class="sakura-card__body">
      <slot />
    </div>
    
    <div v-if="$slots.footer" class="sakura-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<style lang="scss" scoped>


.sakura-card {
  border-radius: $border-radius-xl;
  transition: all $transition;
  
  // 变体
  &--default {
    background: $background-card;
    box-shadow: $shadow;
  }
  
  &--glass {
    @include glass-morphism(0.75);
  }
  
  &--outline {
    background: transparent;
    border: 2px solid $border-color;
  }
  
  // 内边距
  &--padding-none {
    .sakura-card__body {
      padding: 0;
    }
  }
  
  &--padding-sm {
    .sakura-card__body {
      padding: 12px;
    }
  }
  
  &--padding-md {
    .sakura-card__body {
      padding: 20px;
    }
  }
  
  &--padding-lg {
    .sakura-card__body {
      padding: 32px;
    }
  }
  
  // 悬停效果
  &--hoverable {
    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-xl;
    }
  }
  
  // 可点击
  &--clickable {
    cursor: pointer;
    
    &:hover {
      transform: translateY(-2px);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  &__header {
    padding: 16px 20px;
    border-bottom: 1px solid $border-color;
    font-weight: 600;
    color: $text-primary;
  }
  
  &__body {
    // 内边距由修饰符控制
  }
  
  &__footer {
    padding: 16px 20px;
    border-top: 1px solid $border-color;
  }
}
</style>
