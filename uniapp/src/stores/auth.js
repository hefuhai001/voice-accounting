import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'

// 持久化存储键名
const STORAGE_KEY = 'auth-store'

// 从本地存储加载数据
function loadFromStorage() {
  try {
    const data = uni.getStorageSync(STORAGE_KEY)
    return data ? JSON.parse(data) : null
  } catch (e) {
    return null
  }
}

// 保存到本地存储
function saveToStorage(data) {
  try {
    uni.setStorageSync(STORAGE_KEY, JSON.stringify(data))
  } catch (e) {
    console.error('保存认证信息失败:', e)
  }
}

// 清除本地存储
function clearStorage() {
  try {
    uni.removeStorageSync(STORAGE_KEY)
  } catch (e) {
    console.error('清除认证信息失败:', e)
  }
}

export const useAuthStore = defineStore('auth', () => {
  // 初始化时从本地存储加载
  const savedData = loadFromStorage()

  const accessToken = ref(savedData?.accessToken || '')
  const refreshToken = ref(savedData?.refreshToken || '')
  const userInfo = ref(savedData?.userInfo || null)
  const isLoginChecked = ref(false)

  const isLoggedIn = computed(() => !!userInfo.value || !!accessToken.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  function setTokens(newAccessToken, newRefreshToken) {
    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
    saveToStorage({
      accessToken: newAccessToken,
      refreshToken: newRefreshToken,
      userInfo: userInfo.value
    })
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
        // 更新持久化数据
        saveToStorage({
          accessToken: accessToken.value,
          refreshToken: refreshToken.value,
          userInfo: userRes.data
        })
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
    clearStorage()
  }

  return {
    accessToken, refreshToken, userInfo, isLoginChecked,
    isLoggedIn, isAdmin,
    setTokens, login, register, getUserInfo, checkLoginStatus, logout, clearAuthState,
  }
})
