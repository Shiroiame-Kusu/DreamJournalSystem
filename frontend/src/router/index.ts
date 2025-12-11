import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 路由懒加载
const LoginView = () => import('@/views/auth/LoginView.vue')
const RegisterView = () => import('@/views/auth/RegisterView.vue')
const DreamListView = () => import('@/views/dream/DreamListView.vue')
const DreamCreateView = () => import('@/views/dream/DreamCreateView.vue')
const DreamDetailView = () => import('@/views/dream/DreamDetailView.vue')
const DreamEditView = () => import('@/views/dream/DreamEditView.vue')
const FavoritesView = () => import('@/views/dream/FavoritesView.vue')
const ProfileView = () => import('@/views/user/ProfileView.vue')
const AdminUsersView = () => import('@/views/admin/AdminUsersView.vue')
const NotFoundView = () => import('@/views/NotFoundView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dreams'
    },
    // 认证路由
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { guest: true, title: '登录 - 梦境日记' }
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { guest: true, title: '注册 - 梦境日记' }
    },
    // 梦境路由
    {
      path: '/dreams',
      name: 'dreams',
      component: DreamListView,
      meta: { requiresAuth: true, title: '我的梦境 - 梦境日记' }
    },
    {
      path: '/dreams/create',
      name: 'dream-create',
      component: DreamCreateView,
      meta: { requiresAuth: true, title: '记录梦境 - 梦境日记' }
    },
    {
      path: '/dreams/:id/edit',
      name: 'dream-edit',
      component: DreamEditView,
      meta: { requiresAuth: true, title: '编辑梦境 - 梦境日记' }
    },
    {
      path: '/dreams/:id',
      name: 'dream-detail',
      component: DreamDetailView,
      meta: { requiresAuth: true, title: '梦境详情 - 梦境日记' }
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: FavoritesView,
      meta: { requiresAuth: true, title: '收藏的梦境 - 梦境日记' }
    },
    // 用户路由
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true, title: '个人中心 - 梦境日记' }
    },
    // 管理员路由
    {
      path: '/admin/users',
      name: 'admin-users',
      component: AdminUsersView,
      meta: { requiresAuth: true, requiresAdmin: true, title: '用户管理 - 梦境日记' }
    },
    // 404
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: '页面未找到 - 梦境日记' }
    }
  ],
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  
  // 设置页面标题
  document.title = (to.meta.title as string) || '梦境日记'
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'dreams' })
    return
  }
  
  // 已登录用户访问游客页面
  if (to.meta.guest && authStore.isLoggedIn) {
    next({ name: 'dreams' })
    return
  }
  
  next()
})

export default router
