import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  // Sa-Token使用Cookie模式，token由后端通过Set-Cookie自动设置
  // 前端只记录用户信息和登录状态
  const userInfo = ref(null)
  const isLoginChecked = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => {
    // 如果有userInfo说明已登录并获取过信息
    return !!userInfo.value || !!localStorage.getItem('isLoggedIn')
  })

  const isAdmin = computed(() => userInfo.value?.role === 1)

  const token = computed(() => localStorage.getItem('token') || '')

  /**
   * 用户登录
   * Sa-Token会在响应中自动设置Cookie，无需手动存储token
   */
  async function login(loginForm) {
    const res = await request.post('/api/auth/login', loginForm)
    // 登录成功，保存返回的token值到localStorage（用于判断登录状态）
    if (res.data?.token) {
      localStorage.setItem('token', res.data.token)
    }
    // 标记已登录
    localStorage.setItem('isLoggedIn', 'true')
    return res
  }

  /**
   * 用户注册（注册成功后自动登录）
   */
  async function register(registerForm) {
    const res = await request.post('/api/auth/register', registerForm)
    // 注册成功后后端已自动登录，保存token和登录状态
    if (res.data?.token) {
      localStorage.setItem('token', res.data.token)
    }
    localStorage.setItem('isLoggedIn', 'true')
    return res
  }

  /**
   * 刷新Token
   * Sa-Token会自动更新Cookie中的token
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
      // Sa-Token返回的是loginId（用户ID），需要再获取完整用户信息
      const userId = res.data
      if (userId) {
        // 调用用户详情接口获取完整信息
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
      // 尝试获取用户信息，如果成功说明已登录（Cookie有效）
      await getUserInfo()
      isLoginChecked.value = true
    } catch (error) {
      console.error('检查登录状态失败:', error)
      // 获取失败，清除登录状态
      clearAuthState()
      isLoginChecked.value = true
    }
  }

  /**
   * 登出
   * 通知后端清除登录状态，同时清理本地状态
   */
  function logout() {
    // 通知后端登出（清除Redis中的session）
    request.post('/api/auth/logout').catch(() => {})
    // 清理本地状态
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
