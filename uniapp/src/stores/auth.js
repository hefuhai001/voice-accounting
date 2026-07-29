import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref('')
  const refreshToken = ref('')
  const userInfo = ref(null)
  const isLoginChecked = ref(false)

  const isLoggedIn = computed(() => !!userInfo.value || !!accessToken.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  function setTokens(newAccessToken, newRefreshToken) {
    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
  }

  async function login(loginForm) {
    const res = await request.post('/api/auth/login', loginForm)
    setTokens(res.data.token, res.data.refreshToken)
    return res
  }

  async function register(registerForm) {
    const res = await request.post('/api/auth/register', registerForm)
    setTokens(res.data.token, res.data.refreshToken)
    return res
  }

  async function getUserInfo() {
    try {
      const res = await request.get('/api/auth/info')
      const userId = res.data
      if (userId) {
        const userRes = await request.get(`/api/user/info/${userId}`)
        userInfo.value = userRes.data
      }
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

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

  function logout() {
    request.post('/api/auth/logout').catch(() => {})
    clearAuthState()
  }

  function clearAuthState() {
    accessToken.value = ''
    refreshToken.value = ''
    userInfo.value = null
    isLoginChecked.value = false
  }

  return {
    accessToken, refreshToken, userInfo, isLoginChecked,
    isLoggedIn, isAdmin,
    setTokens, login, register, getUserInfo, checkLoginStatus, logout, clearAuthState,
  }
}, {
  persist: {
    pick: ['accessToken', 'refreshToken', 'userInfo'],
  },
})
