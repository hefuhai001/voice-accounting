import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 路由配置
const routes = [
  // 登录页（无需认证）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false },
  },

  // ==================== 用户端路由 ====================
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { title: '用户端', requiresAuth: true, role: ['user', 'admin'] },
    children: [
      {
        path: '',
        redirect: '/dashboard',
      },
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/user/DashboardView.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'books',
        name: 'BookList',
        component: () => import('@/views/user/BookListView.vue'),
        meta: { title: '我的账本' },
      },
      {
        path: 'transaction',
        name: 'Transaction',
        component: () => import('@/views/user/TransactionView.vue'),
        meta: { title: '记一笔' },
      },
      {
        path: 'records',
        name: 'RecordList',
        component: () => import('@/views/user/RecordListView.vue'),
        meta: { title: '记账记录' },
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('@/views/user/CategoryView.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'reminder',
        name: 'ReminderList',
        component: () => import('@/views/user/ReminderView.vue'),
        meta: { title: '我的提醒' },
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/ProfileView.vue'),
        meta: { title: '个人中心' },
      },
    ],
  },

  // ==================== 管理端路由 ====================
  // 注意：使用 /manage 前缀，避免与后端 /admin API 路径冲突（Vite代理会把 /admin 转发到后端）
  {
    path: '/manage',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { title: '管理后台', requiresAuth: true, role: 'admin' },
    children: [
      {
        path: '',
        redirect: '/manage/dashboard',
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
        meta: { title: '控制台' },
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserView.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'books',
        name: 'AdminBooks',
        component: () => import('@/views/admin/BookManageView.vue'),
        meta: { title: '账本管理' },
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManageView.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'transactions',
        name: 'AdminTransactions',
        component: () => import('@/views/admin/TransactionManageView.vue'),
        meta: { title: '记账记录' },
      },
      {
        path: 'reminders',
        name: 'AdminReminders',
        component: () => import('@/views/admin/ReminderManageView.vue'),
        meta: { title: '提醒管理' },
      },
    ],
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在', requiresAuth: false },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  const title = to.meta.title
  if (title) {
    document.title = `${title} - Voice Accounting`
  }

  const authStore = useAuthStore()

  // 不需要认证的页面直接放行
  if (to.meta.requiresAuth === false) {
    // 如果已登录，访问登录页则重定向到首页
    if (to.path === '/login' && authStore.isLoggedIn) {
      next(authStore.isAdmin ? '/manage' : '/dashboard')
      return
    }
    next()
    return
  }

  // 检查是否已登录
  if (!authStore.isLoggedIn) {
    // 未登录，跳转到登录页，并保存目标路径
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  // 如果有Token但还没有获取用户信息（如刷新页面后），先获取用户信息
  if (!authStore.userInfo && authStore.isLoggedIn) {
    try {
      await authStore.getUserInfo()
    } catch (error) {
      // 获取用户信息失败，清除状态并跳转登录
      authStore.clearAuthState()
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }

  // 检查角色权限
  if (to.meta.role) {
    const requiredRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]
    const userRole = authStore.isAdmin ? 'admin' : 'user'

    if (!requiredRoles.includes(userRole)) {
      // 无权限，跳转到首页
      next({ path: '/' })
      return
    }
  }

  next()
})

export default router
