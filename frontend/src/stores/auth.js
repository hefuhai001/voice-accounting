import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  // 持久化状态（通过 pinia-plugin-persistedstate 自动同步 localStorage）
  const accessToken = ref('')
  const refreshToken = ref('')
  const userInfo = ref(null)

  // 非持久化状态
  const isLoginChecked = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => {
    return !!userInfo.value || !!accessToken.value
  })

  const isAdmin = computed(() => userInfo.value?.role === 1)

  /**
   * 保存双Token
   */
  function setTokens(newAccessToken, newRefreshToken) {
    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
  }

  /**
   * 用户登录
   */
  async function login(loginForm) {
    const res = await request.post('/api/auth/login', loginForm)
    setTokens(res.data.token, res.data.refreshToken)
    return res
  }

  /**
   * 用户注册（注册成功后自动登录）
   */
  async function register(registerForm) {
    const res = await request.post('/api/auth/register', registerForm)
    setTokens(res.data.token, res.data.refreshToken)
    return res
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
   */
  async function checkLoginStatus() {
    if (isLoginChecked.value) return

    try {
      await getUserInfo()
      isLoginChecked.value = true
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
    accessToken.value = ''
    refreshToken.value = ''
    userInfo.value = null
    isLoginChecked.value = false
  }

  return {
    accessToken,
    refreshToken,
    userInfo,
    isLoginChecked,
    isLoggedIn,
    isAdmin,
    setTokens,
    login,
    register,
    getUserInfo,
    checkLoginStatus,
    logout,
    clearAuthState,
  }
}, {
  persist: {
    pick: ['accessToken', 'refreshToken', 'userInfo'],
  },
})
