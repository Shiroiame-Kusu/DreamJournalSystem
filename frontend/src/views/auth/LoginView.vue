<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const errorMessage = ref('')
const loading = ref(false)

function validate() {
  let valid = true
  errors.username = ''
  errors.password = ''
  
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    valid = false
  }
  
  if (!form.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少6位'
    valid = false
  }
  
  return valid
}

async function handleSubmit() {
  errorMessage.value = ''
  
  if (!validate()) return
  
  loading.value = true
  
  const result = await authStore.login({
    username: form.username.trim(),
    password: form.password
  })
  
  loading.value = false
  
  if (result.success) {
    const redirect = route.query.redirect as string
    router.push(redirect || '/dreams')
  } else {
    errorMessage.value = result.message || '登录失败，请检查用户名和密码'
  }
}
</script>

<template>
  <div class="login-view">
    <div class="login-view__container">
      <SakuraCard variant="glass" padding="lg" class="login-view__card">
        <div class="login-view__header">
          <div class="login-view__logo">🌸</div>
          <h1 class="login-view__title">梦境日记</h1>
          <p class="login-view__subtitle">记录你的每一个梦境</p>
        </div>
        
        <SakuraAlert v-if="errorMessage" type="error" closable @close="errorMessage = ''">
          {{ errorMessage }}
        </SakuraAlert>
        
        <form class="login-view__form" @submit.prevent="handleSubmit">
          <SakuraInput
            v-model="form.username"
            label="用户名"
            placeholder="请输入用户名"
            :error="errors.username"
            required
            clearable
          />
          
          <SakuraInput
            v-model="form.password"
            type="password"
            label="密码"
            placeholder="请输入密码"
            :error="errors.password"
            required
          />
          
          <SakuraButton
            type="submit"
            variant="primary"
            size="lg"
            block
            :loading="loading"
          >
            登录
          </SakuraButton>
        </form>
        
        <div class="login-view__footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="login-view__link">
            立即注册
          </router-link>
        </div>
      </SakuraCard>
    </div>
  </div>
</template>

<style lang="scss" scoped>


.login-view {
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
