import { useAuthStore } from '@/stores/auth'

// #ifdef H5
const BASE_URL = import.meta.env.DEV ? '' : 'https://www.hfh.asia'
// #endif
// #ifdef APP-PLUS
const BASE_URL = 'https://www.hfh.asia/api'
// #endif

let isRefreshing = false
let pendingRequests = []

function handleTokenRefresh(failedConfig) {
  const authStore = useAuthStore()
  const refreshToken = authStore.refreshToken

  if (!refreshToken) {
    authStore.clearAuthState()
    uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
    uni.reLaunch({ url: '/pages/login/login' })
    return Promise.reject(new Error('无Refresh Token'))
  }

  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      pendingRequests.push({ resolve, reject, failedConfig })
    })
  }

  isRefreshing = true

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}/api/auth/refresh-token`,
      method: 'POST',
      header: { 'Refresh-Token': refreshToken },
      success: (res) => {
        const data = res.data
        if (data.code !== 200) {
          // 刷新失败：拒绝所有等待中的请求并清空队列，避免请求永久挂起
          pendingRequests.forEach(({ reject: r }) => r(new Error(data.message || '刷新Token失败')))
          pendingRequests = []
          authStore.clearAuthState()
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error(data.message || '刷新Token失败'))
          return
        }
        authStore.setTokens(data.data.token, data.data.refreshToken)
        // 逐个用各自请求的 config 重试，避免所有请求重放第一个请求
        pendingRequests.forEach(({ resolve: r, failedConfig }) => {
          r(request(failedConfig))
        })
        pendingRequests = []
        resolve(request(failedConfig))
      },
      fail: (err) => {
        pendingRequests.forEach(({ reject: r }) => r(err))
        pendingRequests = []
        authStore.clearAuthState()
        uni.reLaunch({ url: '/pages/login/login' })
        reject(err)
      },
      complete: () => {
        isRefreshing = false
      }
    })
  })
}

function request(options) {
  const authStore = useAuthStore()

  const config = {
    url: options.url.startsWith('http') ? options.url : `${BASE_URL}${options.url}`,
    method: options.method || 'GET',
    data: options.data || options.params,
    header: {
      'Content-Type': 'application/json',
      ...options.header,
    },
    timeout: options.timeout || 10000,
  }

  // Add auth token
  if (authStore.accessToken) {
    config.header['Authorization'] = authStore.accessToken
  }
  // Add refresh token for logout
  if (options.url?.includes('/auth/logout') && authStore.refreshToken) {
    config.header['Refresh-Token'] = authStore.refreshToken
  }

  return new Promise((resolve, reject) => {
    uni.request({
      ...config,
      success: (res) => {
        const data = res.data
        if (data.code === 200) {
          resolve(data)
        } else if (data.code === 401) {
          // 登录/注册/找回密码的401表示凭证错误，不应触发Token刷新（避免死循环）
          const url = options.url || ''
          if (url.includes('/auth/login') || url.includes('/auth/register') || url.includes('/auth/reset-password')) {
            uni.showToast({ title: data.message || '用户名或密码错误', icon: 'none' })
            reject(new Error(data.message || '用户名或密码错误'))
          } else {
            // Try refresh token
            handleTokenRefresh(options).then(resolve).catch(reject)
          }
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(new Error(data.message || '请求失败'))
        }
      },
      fail: (err) => {
        if (err.statusCode === 401) {
          handleTokenRefresh(options).then(resolve).catch(reject)
        } else {
          uni.showToast({ title: '网络错误', icon: 'none' })
          reject(err)
        }
      }
    })
  })
}

// Convenience methods
request.get = (url, options = {}) => request({ url, method: 'GET', params: options.params, ...options })
request.post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
request.put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
request.delete = (url, options = {}) => request({ url, method: 'DELETE', ...options })

export default request
