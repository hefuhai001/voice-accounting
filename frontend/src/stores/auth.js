import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import request, { resetRefreshState } from '@/utils/request'

// 定时刷新间隔：25分钟（active-timeout为30分钟，提前5分钟刷新）
const REFRESH_INTERVAL = 25 * 60 * 1000

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const userInfo = ref(null)
  const isLoginChecked = ref(false)

  // 定时刷新定时器
  let refreshTimer = null

  // 计算属性
  const isLoggedIn = computed(() => {
    return !!userInfo.value || !!localStorage.getItem('isLoggedIn')
  })

  const isAdmin = computed(() => userInfo.value?.role === 1)

  const token = computed(() => localStorage.getItem('token') || '')

  /**
   * 启动定时刷新Token
   * 每隔25分钟主动刷新一次，避免activity-timeout过期触发401
   */
  function startRefreshTimer() {
    stopRefreshTimer()
    refreshTimer = setInterval(() => {
      if (localStorage.getItem('isLoggedIn')) {
        request.post('/api/auth/refresh-token').then((res) => {
          if (res.data?.token) {
            localStorage.setItem('token', res.data.token)
          }
        }).catch(() => {
          // 静默失败，后续请求会触发401走正常刷新流程
        })
      }
    }, REFRESH_INTERVAL)
  }

  /**
   * 停止定时刷新Token
   */
  function stopRefreshTimer() {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  /**
   * 用户登录
   */
  async function login(loginForm) {
    const res = await request.post('/api/auth/login', loginForm)
    // 保存token到localStorage，用于请求头鉴权
    if (res.data?.token) {
      localStorage.setItem('token', res.data.token)
    }
    localStorage.setItem('isLoggedIn', 'true')
    resetRefreshState()
    startRefreshTimer()
    return res
  }

  /**
   * 用户注册（注册成功后自动登录）
   */
  async function register(registerForm) {
    const res = await request.post('/api/auth/register', registerForm)
    if (res.data?.token) {
      localStorage.setItem('token', res.data.token)
    }
    localStorage.setItem('isLoggedIn', 'true')
    resetRefreshState()
    startRefreshTimer()
    return res
  }

  /**
   * 刷新Token
   */
  async function refreshToken() {
    try {
      const res = await request.post('/api/auth/refresh-token')
      if (res.data?.token) {
        localStorage.setItem('token', res.data.token)
      }
      return res
    } catch (error) {
      logout()
      throw error
    }
  }

  /**
   * 获取当前登录用户信息
   */
  async function getUserInfo() {
    try {
      const res = await request.get('/api/auth/info')
      const userId = res.data
      if (userId) {
        const userRes = await request.get(`/admin/user/${userId}`)
        userInfo.value = userRes.data
      }
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  /**
   * 检查登录状态
   * 用于页面刷新时恢复登录状态
   */
  async function checkLoginStatus() {
    if (isLoginChecked.value) return

    try {
      await getUserInfo()
      isLoginChecked.value = true
      startRefreshTimer()
    } catch (error) {
      clearAuthState()
      isLoginChecked.value = true
    }
  }

  /**
   * 登出
   */
  function logout() {
    request.post('/api/auth/logout').catch(() => {})
    clearAuthState()
  }

  /**
   * 清理认证状态
   */
  function clearAuthState() {
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('isLoggedIn')
    isLoginChecked.value = false
    stopRefreshTimer()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    refreshToken,
    getUserInfo,
    checkLoginStatus,
    logout,
  }
})
