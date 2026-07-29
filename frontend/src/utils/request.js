import axios from 'axios'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 用于刷新Token的独立实例（不带拦截器，避免递归）
const refreshRequest = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 是否正在刷新Token
let isRefreshing = false
// 刷新期间等待的请求队列
let pendingRequests = []

/**
 * 使用Refresh Token获取新的双Token
 */
function handleTokenRefresh(failedRequest) {
  const authStore = useAuthStore()
  const refreshToken = authStore.refreshToken

  // 没有Refresh Token，直接跳转登录
  if (!refreshToken) {
    authStore.clearAuthState()
    message.error('登录已过期，请重新登录')
    window.location.replace('/login')
    return Promise.reject(new Error('无Refresh Token'))
  }

  if (isRefreshing) {
    // 已有刷新请求进行中，加入等待队列
    return new Promise((resolve, reject) => {
      pendingRequests.push({ resolve, reject, failedRequest })
    })
  }

  isRefreshing = true

  return refreshRequest
    .post('/api/auth/refresh-token', null, {
      headers: { 'Refresh-Token': refreshToken },
    })
    .then((response) => {
      const res = response.data
      if (res.code !== 200) {
        throw new Error(res.message || '刷新Token失败')
      }

      // 通过store保存新的双Token（自动持久化）
      authStore.setTokens(res.data.token, res.data.refreshToken)

      // 重试所有等待中的请求
      // eslint-disable-next-line no-unused-vars, no-constant-condition
      pendingRequests.forEach(({ resolve, reject, failedRequest }) => {
        failedRequest.config.headers['Authorization'] = res.data.token
        resolve(request(failedRequest.config))
      })
      pendingRequests = []

      // 重试当前请求
      failedRequest.config.headers['Authorization'] = res.data.token
      return request(failedRequest.config)
    })
    .catch((error) => {
      // 刷新失败，拒绝所有等待中的请求
      pendingRequests.forEach(({ reject }) => reject(error))
      pendingRequests = []

      // 清除状态并跳转登录页
      authStore.clearAuthState()
      message.error('登录已过期，请重新登录')
      window.location.replace('/login')
      return Promise.reject(error)
    })
    .finally(() => {
      isRefreshing = false
    })
}

// 请求拦截器：自动附加Access Token
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
      config.headers['Authorization'] = authStore.accessToken
    }
    // 登出接口需要附加Refresh Token
    if (config.url?.includes('/auth/logout')) {
      if (authStore.refreshToken) {
        config.headers['Refresh-Token'] = authStore.refreshToken
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器：处理401自动刷新
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      // 登录/注册接口的401表示凭证错误，仅提示
      if (res.code === 401) {
        const url = response.config.url || ''
        if (url.includes('/auth/login') || url.includes('/auth/register')) {
          message.error(res.message || '用户名或密码错误')
          return Promise.reject(new Error(res.message || '用户名或密码错误'))
        }
        // 其他接口401：Access Token过期，尝试刷新
        return handleTokenRefresh(response)
      }
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      return handleTokenRefresh(error.response)
    }
    message.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request
