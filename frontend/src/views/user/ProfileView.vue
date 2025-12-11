<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

const authStore = useAuthStore()

const profileForm = reactive({
  nickname: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordErrors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileLoading = ref(false)
const passwordLoading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

onMounted(() => {
  if (authStore.user) {
    profileForm.nickname = authStore.user.nickname || ''
    profileForm.email = authStore.user.email || ''
  }
})

async function handleUpdateProfile() {
  profileLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    await authApi.updateProfile({
      nickname: profileForm.nickname || undefined,
      email: profileForm.email
    })
    await authStore.fetchCurrentUser()
    successMessage.value = '个人信息更新成功！'
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '更新失败，请稍后重试'
  } finally {
    profileLoading.value = false
  }
}

function validatePassword() {
  let valid = true
  passwordErrors.oldPassword = ''
  passwordErrors.newPassword = ''
  passwordErrors.confirmPassword = ''
  
  if (!passwordForm.oldPassword) {
    passwordErrors.oldPassword = '请输入当前密码'
    valid = false
  }
  
  if (!passwordForm.newPassword) {
    passwordErrors.newPassword = '请输入新密码'
    valid = false
  } else if (passwordForm.newPassword.length < 6) {
    passwordErrors.newPassword = '密码至少6位'
    valid = false
  }
  
  if (!passwordForm.confirmPassword) {
    passwordErrors.confirmPassword = '请确认新密码'
    valid = false
  } else if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordErrors.confirmPassword = '两次密码输入不一致'
    valid = false
  }
  
  return valid
}

async function handleChangePassword() {
  if (!validatePassword()) return
  
  passwordLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    successMessage.value = '密码修改成功！'
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '密码修改失败，请检查当前密码是否正确'
  } finally {
    passwordLoading.value = false
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="profile-view">
    <div class="detail-header-spacer"></div>
    
    <main class="profile-view__main">
      <div class="container">
        <header class="profile-view__header">
          <h1 class="profile-view__title">👤 个人中心</h1>
        </header>
        
        <SakuraAlert 
          v-if="successMessage" 
          type="success" 
          closable 
          @close="successMessage = ''"
        >
          {{ successMessage }}
        </SakuraAlert>
        
        <SakuraAlert 
          v-if="errorMessage" 
          type="error" 
          closable 
          @close="errorMessage = ''"
        >
          {{ errorMessage }}
        </SakuraAlert>
        
        <div class="profile-view__grid">
          <!-- 用户信息卡片 -->
          <SakuraCard variant="glass" padding="lg">
            <h2 class="section-title">账户信息</h2>
            
            <div class="user-info">
              <div class="user-info__avatar">
                {{ authStore.username.charAt(0).toUpperCase() }}
              </div>
              
              <div class="user-info__details">
                <div class="user-info__item">
                  <span class="user-info__label">用户名</span>
                  <span class="user-info__value">{{ authStore.user?.username }}</span>
                </div>
                <div class="user-info__item">
                  <span class="user-info__label">角色</span>
                  <span class="user-info__value user-info__role">
                    {{ authStore.isAdmin ? '管理员' : '普通用户' }}
                  </span>
                </div>
                <div class="user-info__item">
                  <span class="user-info__label">注册时间</span>
                  <span class="user-info__value">
                    {{ formatDate(authStore.user?.createdAt) }}
                  </span>
                </div>
              </div>
            </div>
          </SakuraCard>
          
          <!-- 编辑个人信息 -->
          <SakuraCard variant="glass" padding="lg">
            <h2 class="section-title">编辑信息</h2>
            
            <form class="profile-form" @submit.prevent="handleUpdateProfile">
              <SakuraInput
                v-model="profileForm.nickname"
                label="昵称"
                placeholder="设置一个昵称"
                clearable
              />
              
              <SakuraInput
                v-model="profileForm.email"
                type="email"
                label="邮箱"
                placeholder="邮箱地址"
                clearable
              />
              
              <SakuraButton
                type="submit"
                variant="primary"
                :loading="profileLoading"
              >
                保存修改
              </SakuraButton>
            </form>
          </SakuraCard>
          
          <!-- 修改密码 -->
          <SakuraCard variant="glass" padding="lg">
            <h2 class="section-title">修改密码</h2>
            
            <form class="profile-form" @submit.prevent="handleChangePassword">
              <SakuraInput
                v-model="passwordForm.oldPassword"
                type="password"
                label="当前密码"
                placeholder="请输入当前密码"
                :error="passwordErrors.oldPassword"
              />
              
              <SakuraInput
                v-model="passwordForm.newPassword"
                type="password"
                label="新密码"
                placeholder="请输入新密码（至少6位）"
                :error="passwordErrors.newPassword"
              />
              
              <SakuraInput
                v-model="passwordForm.confirmPassword"
                type="password"
                label="确认新密码"
                placeholder="请再次输入新密码"
                :error="passwordErrors.confirmPassword"
              />
              
              <SakuraButton
                type="submit"
                variant="secondary"
                :loading="passwordLoading"
              >
                修改密码
              </SakuraButton>
            </form>
          </SakuraCard>
        </div>
      </div>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.profile-view {
  min-height: 100vh;
  
  &__main {
    padding: 32px 0;
  }
  
  &__header {
    margin-bottom: 32px;
  }
  
  &__title {
    font-size: $font-size-3xl;
    font-weight: 700;
    color: $text-primary;
    margin: 0;
  }
  
  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 24px;
  }
}

.section-title {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 24px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid $border-color;
}

.user-info {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  
  &__avatar {
    width: 80px;
    height: 80px;
    background: $gradient-sakura;
    color: $text-on-primary;
    font-size: $font-size-3xl;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius-full;
    flex-shrink: 0;
  }
  
  &__details {
    flex: 1;
  }
  
  &__item {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  &__label {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
  
  &__value {
    color: $text-primary;
    font-weight: 500;
  }
  
  &__role {
    padding: 2px 10px;
    background: $gradient-lavender;
    color: $text-on-primary;
    font-size: $font-size-xs;
    border-radius: $border-radius-full;
  }
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
