<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  modelValue?: string
  placeholder?: string
  label?: string
  error?: string
  hint?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  rows?: number
  maxlength?: number
  showCount?: boolean
  autoResize?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  rows: 4,
  disabled: false,
  readonly: false,
  required: false,
  showCount: false,
  autoResize: true
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
}>()

const textareaRef = ref<HTMLTextAreaElement>()
const focused = ref(false)

const hasValue = computed(() => {
  const value = props.modelValue
  return value !== undefined && value !== null && value !== ''
})

const currentLength = computed(() => {
  return String(props.modelValue || '').length
})

const wrapperClasses = computed(() => [
  'sakura-textarea',
  {
    'sakura-textarea--focused': focused.value,
    'sakura-textarea--error': props.error,
    'sakura-textarea--disabled': props.disabled,
    'sakura-textarea--has-value': hasValue.value
  }
])

function handleInput(event: Event) {
  const target = event.target as HTMLTextAreaElement
  emit('update:modelValue', target.value)
  
  if (props.autoResize) {
    autoResize()
  }
}

function handleFocus(event: FocusEvent) {
  focused.value = true
  emit('focus', event)
}

function handleBlur(event: FocusEvent) {
  focused.value = false
  emit('blur', event)
}

function autoResize() {
  const textarea = textareaRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}

function focus() {
  textareaRef.value?.focus()
}

defineExpose({ focus })
</script>

<template>
  <div :class="wrapperClasses">
    <label v-if="label" class="sakura-textarea__label" @click="focus">
      {{ label }}
      <span v-if="required" class="sakura-textarea__required">*</span>
    </label>
    
    <div class="sakura-textarea__wrapper">
      <textarea
        ref="textareaRef"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :rows="rows"
        :maxlength="maxlength"
        class="sakura-textarea__field"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
      />
    </div>
    
    <div class="sakura-textarea__footer">
      <span v-if="error" class="sakura-textarea__error">{{ error }}</span>
      <span v-else-if="hint" class="sakura-textarea__hint">{{ hint }}</span>
      <span v-if="showCount && maxlength" class="sakura-textarea__count">
        {{ currentLength }}/{{ maxlength }}
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>


.sakura-textarea {
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
  }
  
  &--disabled &__wrapper {
    background: rgba($text-light, 0.1);
    cursor: not-allowed;
  }
  
  &__field {
    width: 100%;
    padding: 12px 16px;
    background: transparent;
    color: $text-primary;
    font-size: $font-size-base;
    line-height: 1.6;
    resize: none;
    min-height: 120px;
    
    &::placeholder {
      color: $text-light;
    }
    
    &:disabled {
      cursor: not-allowed;
      color: $text-secondary;
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
