<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  modelValue?: string | number
  type?: 'text' | 'password' | 'email' | 'number' | 'tel' | 'url'
  placeholder?: string
  label?: string
  error?: string
  hint?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  maxlength?: number
  showCount?: boolean
  clearable?: boolean
  prefixIcon?: string
  suffixIcon?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  readonly: false,
  required: false,
  showCount: false,
  clearable: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  clear: []
}>()

const inputRef = ref<HTMLInputElement>()
const focused = ref(false)
const showPassword = ref(false)

const inputType = computed(() => {
  if (props.type === 'password') {
    return showPassword.value ? 'text' : 'password'
  }
  return props.type
})

const hasValue = computed(() => {
  const value = props.modelValue
  return value !== undefined && value !== null && value !== ''
})

const currentLength = computed(() => {
  return String(props.modelValue || '').length
})

const wrapperClasses = computed(() => [
  'sakura-input',
  {
    'sakura-input--focused': focused.value,
    'sakura-input--error': props.error,
    'sakura-input--disabled': props.disabled,
    'sakura-input--has-value': hasValue.value,
    'sakura-input--has-label': props.label
  }
])

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

function handleFocus(event: FocusEvent) {
  focused.value = true
  emit('focus', event)
}

function handleBlur(event: FocusEvent) {
  focused.value = false
  emit('blur', event)
}

function handleClear() {
  emit('update:modelValue', '')
  emit('clear')
  inputRef.value?.focus()
}

function togglePassword() {
  showPassword.value = !showPassword.value
}

function focus() {
  inputRef.value?.focus()
}

defineExpose({ focus })
</script>

<template>
  <div :class="wrapperClasses">
    <label v-if="label" class="sakura-input__label" @click="focus">
      {{ label }}
      <span v-if="required" class="sakura-input__required">*</span>
    </label>
    
    <div class="sakura-input__wrapper">
      <span v-if="prefixIcon" class="sakura-input__prefix">
        <slot name="prefix">
          {{ prefixIcon }}
        </slot>
      </span>
      
      <input
        ref="inputRef"
        :type="inputType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        class="sakura-input__field"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
      />
      
      <!-- 清除按钮 -->
      <button
        v-if="clearable && hasValue && !disabled && !readonly"
        type="button"
        class="sakura-input__clear"
        @click="handleClear"
      >
        ✕
      </button>
      
      <!-- 密码切换 -->
      <button
        v-if="type === 'password'"
        type="button"
        class="sakura-input__toggle-password"
        @click="togglePassword"
      >
        {{ showPassword ? '🙈' : '👁️' }}
      </button>
      
      <span v-if="suffixIcon" class="sakura-input__suffix">
        <slot name="suffix">
          {{ suffixIcon }}
        </slot>
      </span>
    </div>
    
    <div v-if="error || hint || (showCount && maxlength)" class="sakura-input__footer">
      <span v-if="error" class="sakura-input__error">{{ error }}</span>
      <span v-else-if="hint" class="sakura-input__hint">{{ hint }}</span>
      <span v-if="showCount && maxlength" class="sakura-input__count">
        {{ currentLength }}/{{ maxlength }}
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>


.sakura-input {
  display: flex;
  flex-direction: column;
  gap: 6px;
  
  &__label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $text-primary;
    cursor: pointer;
  }
  
  &__required {
    color: $error;
    margin-left: 2px;
  }
  
  &__wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: $background-card;
    border: 2px solid $border-color;
    border-radius: $border-radius;
    transition: all $transition;
    overflow: hidden;
    
    &:hover {
      border-color: rgba($primary, 0.5);
    }
  }
  
  &--focused &__wrapper {
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba($primary, 0.1);
  }
  
  &--error &__wrapper {
    border-color: $error;
    
    &:hover {
      border-color: $error;
    }
  }
  
  &--error.sakura-input--focused &__wrapper {
    box-shadow: 0 0 0 3px rgba($error, 0.1);
  }
  
  &--disabled &__wrapper {
    background: rgba($text-light, 0.1);
    cursor: not-allowed;
  }
  
  &__field {
    flex: 1;
    padding: 12px 16px;
    background: transparent;
    color: $text-primary;
    font-size: $font-size-base;
    line-height: 1.5;
    
    &::placeholder {
      color: $text-light;
    }
    
    &:disabled {
      cursor: not-allowed;
      color: $text-secondary;
    }
  }
  
  &__prefix,
  &__suffix {
    display: flex;
    align-items: center;
    padding: 0 12px;
    color: $text-secondary;
  }
  
  &__prefix {
    padding-right: 0;
  }
  
  &__suffix {
    padding-left: 0;
  }
  
  &__clear,
  &__toggle-password {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    margin-right: 8px;
    color: $text-light;
    font-size: $font-size-sm;
    border-radius: $border-radius-full;
    transition: all $transition-fast;
    
    &:hover {
      background: rgba($primary, 0.1);
      color: $text-primary;
    }
  }
  
  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 20px;
    padding: 0 4px;
  }
  
  &__error {
    font-size: $font-size-xs;
    color: $error;
  }
  
  &__hint {
    font-size: $font-size-xs;
    color: $text-light;
  }
  
  &__count {
    font-size: $font-size-xs;
    color: $text-light;
    margin-left: auto;
  }
}
</style>
