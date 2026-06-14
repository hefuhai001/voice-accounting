import axios from 'axios'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 用于刷新Token的独立实例，不经过响应拦截器
const refreshRequest = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 刷新状态管理
let isRefreshing = false
let hasRedirected = false
let pendingRequests = []

function onTokenRefreshed(newToken) {
  pendingRequests.forEach((cb) => cb(newToken))
  pendingRequests = []
}

function clearPendingRequests() {
  pendingRequests = []
}

// 请求拦截器：从localStorage读取token设置到Authorization头
request.interceptors.request.use((config) => {
  const tokenValue = localStorage.getItem('token')
  if (tokenValue) {
    config.headers.Authorization = tokenValue
  }
  return config
})

// 刷新请求的请求拦截器：同样携带token
refreshRequest.interceptors.request.use((config) => {
  const tokenValue = localStorage.getItem('token')
  if (tokenValue) {
    config.headers.Authorization = tokenValue
  }
  return config
})

// 跳转登录页（全局只执行一次）
function redirectToLogin() {
  if (hasRedirected) return
  hasRedirected = true
  isRefreshing = false
  clearPendingRequests()

  const authStore = useAuthStore()
  authStore.clearAuthState()

  // 使用window.location.href整页跳转，确保完全重置应用状态
  window.location.href = '/login'
}

// 处理401：尝试刷新Token，成功后重试原请求
function handleUnauthorized(originalRequest) {
  if (hasRedirected) {
    return Promise.reject(new Error('登录已过期'))
  }

  if (originalRequest._retry) {
    redirectToLogin()
    return Promise.reject(new Error('登录已过期'))
  }
  originalRequest._retry = true

  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      pendingRequests.push((newToken) => {
        originalRequest.headers.Authorization = newToken
        request(originalRequest).then(resolve).catch(reject)
      })
    })
  }

  isRefreshing = true

  return refreshRequest
    .post('/api/auth/refresh-token')
    .then((response) => {
      const res = response.data
      if (res.code === 200) {
        const newToken = res.data?.token
        if (newToken) {
          localStorage.setItem('token', newToken)
        }
        const tokenToUse = newToken || localStorage.getItem('token')

        // 重试所有排队的请求
        onTokenRefreshed(tokenToUse)

        // 重试原请求
        originalRequest.headers.Authorization = tokenToUse
        return request(originalRequest)
      }
      message.error('登录已过期，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error('登录已过期'))
    })
    .catch(() => {
      message.error('登录已过期，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error('登录已过期'))
    })
    .finally(() => {
      isRefreshing = false
    })
}

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401) {
        return handleUnauthorized(response.config)
      }
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      return handleUnauthorized(error.config)
    }
    message.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

// 登录成功后重置状态（供 auth store 的 login 方法调用）
export function resetRefreshState() {
  hasRedirected = false
  isRefreshing = false
  clearPendingRequests()
}

export default request
