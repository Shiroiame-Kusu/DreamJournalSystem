<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useDreamStore } from '@/stores/dream'
import { MOOD_OPTIONS, SLEEP_QUALITY_OPTIONS } from '@/types'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraTextarea from '@/components/ui/SakuraTextarea.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraSelectGroup from '@/components/ui/SakuraSelectGroup.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

const router = useRouter()
const dreamStore = useDreamStore()

const form = reactive({
  title: '',
  content: '',
  dreamDate: new Date().toISOString().split('T')[0],
  mood: '',
  sleepQuality: 'FAIR' as string,
  tags: [] as string[],
  visibility: 'private' as 'private' | 'public'
})

const errors = reactive({
  title: '',
  content: '',
  dreamDate: ''
})

const tagInput = ref('')
const errorMessage = ref('')
const loading = ref(false)

function validate() {
  let valid = true
  errors.title = ''
  errors.content = ''
  errors.dreamDate = ''
  
  if (!form.title.trim()) {
    errors.title = '请输入梦境标题'
    valid = false
  }
  
  if (!form.content.trim()) {
    errors.content = '请描述你的梦境内容'
    valid = false
  } else if (form.content.length < 10) {
    errors.content = '梦境内容至少10个字符'
    valid = false
  }
  
  if (!form.dreamDate) {
    errors.dreamDate = '请选择做梦日期'
    valid = false
  }
  
  return valid
}

function addTag() {
  const tag = tagInput.value.trim()
  if (tag && !form.tags.includes(tag) && form.tags.length < 10) {
    form.tags.push(tag)
    tagInput.value = ''
  }
}

function removeTag(tag: string) {
  form.tags = form.tags.filter(t => t !== tag)
}

function handleTagKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' || event.key === ',') {
    event.preventDefault()
    addTag()
  }
}

async function handleSubmit() {
  errorMessage.value = ''
  
  if (!validate()) return
  
  loading.value = true
  
  try {
    const dream = await dreamStore.createDream({
      title: form.title.trim(),
      content: form.content.trim(),
      dreamDate: form.dreamDate,
      moodBeforeSleep: form.mood as any || undefined,
      sleepQuality: form.sleepQuality as any || undefined,
      tags: form.tags.length > 0 ? form.tags : undefined,
      isPrivate: form.visibility === 'private'
    })
    
    router.push(`/dreams/${dream.id}`)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '保存失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  router.back()
}
</script>

<template>
  <div class="dream-create-view">
    <div class="detail-header-spacer"></div>
    
    <main class="dream-create-view__main">
      <div class="container">
        <SakuraCard variant="glass" padding="lg" class="dream-create-view__card">
          <header class="dream-create-view__header">
            <h1 class="dream-create-view__title">✨ 记录梦境</h1>
            <p class="dream-create-view__subtitle">把你的梦境记录下来，让 AI 帮你解读</p>
          </header>
          
          <SakuraAlert v-if="errorMessage" type="error" closable @close="errorMessage = ''">
            {{ errorMessage }}
          </SakuraAlert>
          
          <form class="dream-create-view__form" @submit.prevent="handleSubmit">
            <!-- 标题 -->
            <SakuraInput
              v-model="form.title"
              label="梦境标题"
              placeholder="给这个梦起个名字"
              :error="errors.title"
              required
              clearable
              :maxlength="100"
              show-count
            />
            
            <!-- 内容 -->
            <SakuraTextarea
              v-model="form.content"
              label="梦境内容"
              placeholder="详细描述你的梦境，包括场景、人物、事件、感受等..."
              :error="errors.content"
              required
              :rows="8"
              :maxlength="5000"
              show-count
            />
            
            <!-- 做梦日期 -->
            <div class="form-group">
              <label class="form-label">做梦日期 <span class="required">*</span></label>
              <input
                v-model="form.dreamDate"
                type="date"
                class="date-input"
                :class="{ 'has-error': errors.dreamDate }"
              />
              <span v-if="errors.dreamDate" class="error-text">{{ errors.dreamDate }}</span>
            </div>
            
            <!-- 心情 -->
            <div class="form-group">
              <label class="form-label">梦境心情</label>
              <SakuraSelectGroup
                :value="form.mood"
                :options="MOOD_OPTIONS"
                @update:value="form.mood = $event as string"
              />
            </div>
            
            <!-- 睡眠质量 -->
            <div class="form-group">
              <label class="form-label">睡眠质量</label>
              <div class="sleep-quality">
                <button
                  v-for="option in SLEEP_QUALITY_OPTIONS"
                  :key="option.value"
                  type="button"
                  class="sleep-quality__option"
                  :class="{ 'is-selected': form.sleepQuality === option.value }"
                  @click="form.sleepQuality = option.value"
                >
                  <span class="sleep-quality__emoji">{{ option.emoji }}</span>
                  <span class="sleep-quality__label">{{ option.label }}</span>
                </button>
              </div>
            </div>
            
            <!-- 标签 -->
            <div class="form-group">
              <label class="form-label">标签（最多10个）</label>
              <div class="tags-input">
                <div class="tags-input__tags">
                  <span 
                    v-for="tag in form.tags" 
                    :key="tag" 
                    class="tags-input__tag"
                  >
                    {{ tag }}
                    <button type="button" @click="removeTag(tag)">✕</button>
                  </span>
                </div>
                <input
                  v-model="tagInput"
                  type="text"
                  placeholder="输入标签后按回车添加"
                  class="tags-input__input"
                  :disabled="form.tags.length >= 10"
                  @keydown="handleTagKeydown"
                />
              </div>
            </div>
            
            <!-- 可见性 -->
            <div class="form-group">
              <label class="form-label">可见性</label>
              <div class="visibility-options">
                <label class="visibility-option">
                  <input
                    v-model="form.visibility"
                    type="radio"
                    value="private"
                  />
                  <span class="visibility-option__icon">🔒</span>
                  <span class="visibility-option__text">仅自己可见</span>
                </label>
                <label class="visibility-option">
                  <input
                    v-model="form.visibility"
                    type="radio"
                    value="public"
                  />
                  <span class="visibility-option__icon">🌐</span>
                  <span class="visibility-option__text">公开</span>
                </label>
              </div>
            </div>
            
            <!-- 按钮 -->
            <div class="dream-create-view__actions">
              <SakuraButton 
                variant="ghost" 
                type="button"
                @click="handleCancel"
              >
                取消
              </SakuraButton>
              <SakuraButton
                variant="primary"
                type="submit"
                :loading="loading"
              >
                保存梦境
              </SakuraButton>
            </div>
          </form>
        </SakuraCard>
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.dream-create-view {
  min-height: 100vh;
  
  &__main {
    padding: 32px 0;
  }
  
  &__card {
    max-width: 700px;
    margin: 0 auto;
  }
  
  &__header {
    text-align: center;
    margin-bottom: 32px;
  }
  
  &__title {
    font-size: $font-size-2xl;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
  }
  
  &__subtitle {
    color: $text-secondary;
    margin: 0;
  }
  
  &__form {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }
  
  &__actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid $border-color;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: $font-size-sm;
  font-weight: 500;
  color: $text-primary;
  
  .required {
    color: $error;
    margin-left: 2px;
  }
}

.date-input {
  padding: 12px 16px;
  background: $background-card;
  border: 2px solid $border-color;
  border-radius: $border-radius;
  font-family: $font-family;
  font-size: $font-size-base;
  color: $text-primary;
  transition: all $transition;
  
  &:hover {
    border-color: rgba($primary, 0.5);
  }
  
  &:focus {
    outline: none;
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba($primary, 0.1);
  }
  
  &.has-error {
    border-color: $error;
  }
}

.error-text {
  font-size: $font-size-xs;
  color: $error;
}

.sleep-quality {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  
  &__option {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 12px 16px;
    background: $background-card;
    border: 2px solid $border-color;
    border-radius: $border-radius;
    transition: all $transition;
    
    &:hover {
      border-color: rgba($primary, 0.5);
    }
    
    &.is-selected {
      background: $gradient-sakura;
      border-color: transparent;
      color: $text-on-primary;
    }
  }
  
  &__emoji {
    font-size: 24px;
  }
  
  &__label {
    font-size: $font-size-xs;
    font-weight: 500;
  }
}

.tags-input {
  padding: 12px;
  background: $background-card;
  border: 2px solid $border-color;
  border-radius: $border-radius;
  
  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 8px;
    
    &:empty {
      margin-bottom: 0;
    }
  }
  
  &__tag {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 10px;
    background: $gradient-sakura;
    color: $text-on-primary;
    font-size: $font-size-sm;
    border-radius: $border-radius-full;
    
    button {
      color: inherit;
      opacity: 0.8;
      
      &:hover {
        opacity: 1;
      }
    }
  }
  
  &__input {
    width: 100%;
    padding: 8px 0;
    background: transparent;
    color: $text-primary;
    font-size: $font-size-sm;
    
    &::placeholder {
      color: $text-light;
    }
    
    &:disabled {
      opacity: 0.5;
    }
  }
}

.visibility-options {
  display: flex;
  gap: 16px;
}

.visibility-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: $background-card;
  border: 2px solid $border-color;
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition;
  
  input {
    display: none;
  }
  
  &:hover {
    border-color: rgba($primary, 0.5);
  }
  
  &:has(input:checked) {
    background: $gradient-sakura;
    border-color: transparent;
    color: $text-on-primary;
  }
  
  &__icon {
    font-size: 20px;
  }
  
  &__text {
    font-size: $font-size-sm;
    font-weight: 500;
  }
}
</style>
