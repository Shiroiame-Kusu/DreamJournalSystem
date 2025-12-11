<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  value: string | string[]
  options: { value: string; label: string; emoji?: string; color?: string }[]
  multiple?: boolean
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  disabled: false
})

const emit = defineEmits<{
  'update:value': [value: string | string[]]
}>()

const selectedValues = computed(() => {
  if (Array.isArray(props.value)) {
    return props.value
  }
  return props.value ? [props.value] : []
})

function isSelected(optionValue: string) {
  return selectedValues.value.includes(optionValue)
}

function handleSelect(optionValue: string) {
  if (props.disabled) return
  
  if (props.multiple) {
    const current = selectedValues.value
    if (current.includes(optionValue)) {
      emit('update:value', current.filter(v => v !== optionValue))
    } else {
      emit('update:value', [...current, optionValue])
    }
  } else {
    emit('update:value', optionValue)
  }
}
</script>

<template>
  <div class="sakura-select-group" :class="{ 'sakura-select-group--disabled': disabled }">
    <button
      v-for="option in options"
      :key="option.value"
      type="button"
      class="sakura-select-group__option"
      :class="{ 'sakura-select-group__option--selected': isSelected(option.value) }"
      :style="isSelected(option.value) && option.color ? { '--option-color': option.color } : {}"
      :disabled="disabled"
      @click="handleSelect(option.value)"
    >
      <span v-if="option.emoji" class="sakura-select-group__emoji">{{ option.emoji }}</span>
      <span class="sakura-select-group__label">{{ option.label }}</span>
    </button>
  </div>
</template>

<style lang="scss" scoped>


.sakura-select-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  &--disabled {
    opacity: 0.5;
    pointer-events: none;
  }
  
  &__option {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 16px;
    background: $background-card;
    border: 2px solid $border-color;
    border-radius: $border-radius-full;
    color: $text-secondary;
    font-size: $font-size-sm;
    font-weight: 500;
    transition: all $transition;
    
    &:hover:not(:disabled) {
      border-color: rgba($primary, 0.5);
      color: $primary-dark;
    }
    
    &--selected {
      background: var(--option-color, $gradient-sakura);
      border-color: transparent;
      color: $text-on-primary;
      
      &:hover:not(:disabled) {
        border-color: transparent;
        color: $text-on-primary;
      }
    }
  }
  
  &__emoji {
    font-size: $font-size-lg;
  }
}
</style>
