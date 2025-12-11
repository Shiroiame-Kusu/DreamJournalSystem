<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { User } from '@/types'
import apiClient from '@/api/client'
import SakuraCard from '@/components/ui/SakuraCard.vue'
import SakuraInput from '@/components/ui/SakuraInput.vue'
import SakuraButton from '@/components/ui/SakuraButton.vue'
import SakuraModal from '@/components/ui/SakuraModal.vue'
import SakuraAlert from '@/components/ui/SakuraAlert.vue'

interface AdminUser extends User {
  dreamCount?: number
}

const users = ref<AdminUser[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedUser = ref<AdminUser | null>(null)
const showBanModal = ref(false)
const showDeleteModal = ref(false)
const actionLoading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

onMounted(() => {
  fetchUsers()
})

async function fetchUsers() {
  loading.value = true
  try {
    const response = await apiClient.get('/admin/users', {
      params: searchKeyword.value ? { keyword: searchKeyword.value } : undefined
    })
    users.value = response.content || response
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '获取用户列表失败'
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  fetchUsers()
}

function openBanModal(user: AdminUser) {
  selectedUser.value = user
  showBanModal.value = true
}

function openDeleteModal(user: AdminUser) {
  selectedUser.value = user
  showDeleteModal.value = true
}

async function handleToggleBan() {
  if (!selectedUser.value) return
  
  actionLoading.value = true
  errorMessage.value = ''
  
  try {
    const newStatus = selectedUser.value.status === 'active' ? 'banned' : 'active'
    await apiClient.put(`/admin/users/${selectedUser.value.id}/status`, {
      status: newStatus
    })
    
    selectedUser.value.status = newStatus
    const index = users.value.findIndex(u => u.id === selectedUser.value!.id)
    if (index !== -1) {
      users.value[index].status = newStatus
    }
    
    successMessage.value = newStatus === 'banned' ? '用户已被禁用' : '用户已解禁'
    showBanModal.value = false
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '操作失败'
  } finally {
    actionLoading.value = false
  }
}

async function handleDelete() {
  if (!selectedUser.value) return
  
  actionLoading.value = true
  errorMessage.value = ''
  
  try {
    await apiClient.delete(`/admin/users/${selectedUser.value.id}`)
    users.value = users.value.filter(u => u.id !== selectedUser.value!.id)
    successMessage.value = '用户已删除'
    showDeleteModal.value = false
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '删除失败'
  } finally {
    actionLoading.value = false
  }
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="admin-users-view">
    <div class="detail-header-spacer"></div>
    
    <main class="admin-users-view__main">
      <div class="container">
        <header class="admin-users-view__header">
          <h1 class="admin-users-view__title">⚙️ 用户管理</h1>
          <p class="admin-users-view__subtitle">管理系统中的所有用户</p>
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
        
        <!-- 搜索栏 -->
        <div class="admin-users-view__search">
          <SakuraInput
            v-model="searchKeyword"
            placeholder="搜索用户名或邮箱..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>🔍</template>
          </SakuraInput>
          <SakuraButton variant="secondary" @click="handleSearch">
            搜索
          </SakuraButton>
        </div>
        
        <!-- 用户列表 -->
        <SakuraCard variant="glass" padding="none">
          <div v-if="loading" class="admin-users-view__loading">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>
          
          <div v-else-if="users.length === 0" class="admin-users-view__empty">
            <p>没有找到用户</p>
          </div>
          
          <table v-else class="users-table">
            <thead>
              <tr>
                <th>用户</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>
                  <div class="user-cell">
                    <div class="user-cell__avatar">
                      {{ user.username.charAt(0).toUpperCase() }}
                    </div>
                    <div class="user-cell__info">
                      <span class="user-cell__name">{{ user.username }}</span>
                      <span v-if="user.nickname" class="user-cell__nickname">
                        {{ user.nickname }}
                      </span>
                    </div>
                  </div>
                </td>
                <td>{{ user.email }}</td>
                <td>
                  <span class="role-badge" :class="`role-badge--${user.role}`">
                    {{ user.role === 'admin' ? '管理员' : '用户' }}
                  </span>
                </td>
                <td>
                  <span class="status-badge" :class="`status-badge--${user.status}`">
                    {{ user.status === 'active' ? '正常' : '已禁用' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td>
                  <div class="action-buttons">
                    <SakuraButton
                      v-if="user.role !== 'admin'"
                      :variant="user.status === 'active' ? 'outline' : 'secondary'"
                      size="sm"
                      @click="openBanModal(user)"
                    >
                      {{ user.status === 'active' ? '禁用' : '解禁' }}
                    </SakuraButton>
                    <SakuraButton
                      v-if="user.role !== 'admin'"
                      variant="danger"
                      size="sm"
                      @click="openDeleteModal(user)"
                    >
                      删除
                    </SakuraButton>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </SakuraCard>
      </div>
    </main>
    
    <!-- 禁用/解禁确认弹窗 -->
    <SakuraModal v-model="showBanModal" title="确认操作" width="400px">
      <p v-if="selectedUser">
        确定要{{ selectedUser.status === 'active' ? '禁用' : '解禁' }}用户 
        <strong>{{ selectedUser.username }}</strong> 吗？
      </p>
      
      <template #footer>
        <SakuraButton variant="ghost" @click="showBanModal = false">
          取消
        </SakuraButton>
        <SakuraButton 
          :variant="selectedUser?.status === 'active' ? 'danger' : 'primary'"
          :loading="actionLoading"
          @click="handleToggleBan"
        >
          确认
        </SakuraButton>
      </template>
    </SakuraModal>
    
    <!-- 删除确认弹窗 -->
    <SakuraModal v-model="showDeleteModal" title="确认删除" width="400px">
      <p v-if="selectedUser">
        确定要删除用户 <strong>{{ selectedUser.username }}</strong> 吗？
        该用户的所有数据都将被删除，此操作无法撤销。
      </p>
      
      <template #footer>
        <SakuraButton variant="ghost" @click="showDeleteModal = false">
          取消
        </SakuraButton>
        <SakuraButton 
          variant="danger"
          :loading="actionLoading"
          @click="handleDelete"
        >
          确认删除
        </SakuraButton>
      </template>
    </SakuraModal>
  </div>
</template>

<style lang="scss" scoped>
.detail-header-spacer {
  height: 80px;
}

.admin-users-view {
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
  
  &__subtitle {
    color: $text-secondary;
    margin: 8px 0 0 0;
  }
  
  &__search {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    
    .sakura-input {
      flex: 1;
      max-width: 400px;
    }
  }
  
  &__loading,
  &__empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: $text-secondary;
  }
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  
  th,
  td {
    padding: 16px;
    text-align: left;
    border-bottom: 1px solid $border-color;
  }
  
  th {
    font-weight: 600;
    color: $text-primary;
    background: rgba($primary, 0.05);
    font-size: $font-size-sm;
  }
  
  td {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
  
  tbody tr:hover {
    background: rgba($primary, 0.03);
  }
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  &__avatar {
    width: 40px;
    height: 40px;
    background: $gradient-sakura;
    color: $text-on-primary;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius-full;
  }
  
  &__info {
    display: flex;
    flex-direction: column;
  }
  
  &__name {
    font-weight: 500;
    color: $text-primary;
  }
  
  &__nickname {
    font-size: $font-size-xs;
    color: $text-light;
  }
}

.role-badge {
  display: inline-block;
  padding: 4px 10px;
  font-size: $font-size-xs;
  font-weight: 500;
  border-radius: $border-radius-full;
  
  &--admin {
    background: $gradient-lavender;
    color: $text-on-primary;
  }
  
  &--user {
    background: rgba($info, 0.2);
    color: darken($info, 30%);
  }
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  font-size: $font-size-xs;
  font-weight: 500;
  border-radius: $border-radius-full;
  
  &--active {
    background: rgba($success, 0.2);
    color: darken($success, 30%);
  }
  
  &--banned {
    background: rgba($error, 0.2);
    color: darken($error, 10%);
  }
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid $border-color;
  border-top-color: $primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
