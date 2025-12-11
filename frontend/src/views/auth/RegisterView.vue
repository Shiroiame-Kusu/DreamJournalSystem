<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const errorMessage = ref('')
const loading = ref(false)

function validate() {
  let valid = true
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })
  
  // 用户名验证
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    valid = false
  } else if (form.username.length < 3) {
    errors.username = '用户名至少3个字符'
    valid = false
  } else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) {
    errors.username = '用户名只能包含字母、数字和下划线'
    valid = false
  }
  
  // 邮箱验证
  if (!form.email.trim()) {
    errors.email = '请输入邮箱'
    valid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
    valid = false
  }
  
  // 密码验证
  if (!form.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少6位'
    valid = false
  }
  
  // 确认密码
  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    valid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次密码输入不一致'
    valid = false
  }
  
  return valid
}

async function handleSubmit() {
  errorMessage.value = ''
  
  if (!validate()) return
  
  loading.value = true
  
  const result = await authStore.register({
    username: form.username.trim(),
    email: form.email.trim(),
    nickname: form.nickname.trim() || undefined,
    password: form.password,
    confirmPassword: form.confirmPassword
  })
  
  loading.value = false
  
  if (result.success) {
    router.push('/dreams')
  } else {
    errorMessage.value = result.message || '注册失败，请稍后重试'
  }
}
</script>

<template>
  <div class="register-view">
    <div class="register-view__container">
      <SakuraCard variant="glass" padding="lg" class="register-view__card">
        <div class="register-view__header">
          <div class="register-view__logo">🌸</div>
          <h1 class="register-view__title">创建账号</h1>
          <p class="register-view__subtitle">开始记录你的梦境之旅</p>
        </div>
        
        <SakuraAlert v-if="errorMessage" type="error" closable @close="errorMessage = ''">
          {{ errorMessage }}
        </SakuraAlert>
        
        <form class="register-view__form" @submit.prevent="handleSubmit">
          <SakuraInput
            v-model="form.username"
            label="用户名"
            placeholder="请输入用户名（字母、数字、下划线）"
            :error="errors.username"
            required
            clearable
            hint="3-20个字符，只能包含字母、数字和下划线"
          />
          
          <SakuraInput
            v-model="form.email"
            type="email"
            label="邮箱"
            placeholder="请输入邮箱地址"
            :error="errors.email"
            required
            clearable
          />
          
          <SakuraInput
            v-model="form.nickname"
            label="昵称（可选）"
            placeholder="给自己起个昵称吧"
            :error="errors.nickname"
            clearable
          />
          
          <SakuraInput
            v-model="form.password"
            type="password"
            label="密码"
            placeholder="请输入密码（至少6位）"
            :error="errors.password"
            required
          />
          
          <SakuraInput
            v-model="form.confirmPassword"
            type="password"
            label="确认密码"
            placeholder="请再次输入密码"
            :error="errors.confirmPassword"
            required
          />
          
          <SakuraButton
            type="submit"
            variant="primary"
            size="lg"
            block
            :loading="loading"
          >
            注册
          </SakuraButton>
        </form>
        
        <div class="register-view__footer">
          <span>已有账号？</span>
          <router-link to="/login" class="register-view__link">
            立即登录
          </router-link>
        </div>
      </SakuraCard>
    </div>
  </div>
</template>

<style lang="scss" scoped>


.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  
  &__container {
    width: 100%;
    max-width: 420px;
    animation: fadeIn 0.6s ease;
  }
  
  &__card {
    text-align: center;
  }
  
  &__header {
    margin-bottom: 32px;
  }
  
  &__logo {
    font-size: 64px;
    margin-bottom: 16px;
  }
  
  &__title {
    font-size: $font-size-3xl;
    font-weight: 700;
    background: $gradient-sakura;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0 0 8px 0;
  }
  
  &__subtitle {
    color: $text-secondary;
    margin: 0;
  }
  
  &__form {
    display: flex;
    flex-direction: column;
    gap: 20px;
    text-align: left;
    margin-top: 24px;
  }
  
  &__footer {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid $border-color;
    color: $text-secondary;
    font-size: $font-size-sm;
  }
  
  &__link {
    color: $primary-dark;
    font-weight: 500;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
