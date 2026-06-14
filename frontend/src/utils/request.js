import axios from 'axios'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  // 关键：允许携带Cookie，Sa-Token基于Cookie进行身份验证
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Token刷新状态管理，防止并发请求时重复刷新
let isRefreshing = false
let pendingRequests = []

function onTokenRefreshed() {
  pendingRequests.forEach((cb) => cb())
  pendingRequests = []
}

function addPendingRequest(cb) {
  pendingRequests.push(cb)
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // Sa-Token使用Cookie模式，不需要手动添加Authorization头
    // 如果需要支持Header模式作为备用，可以保留以下代码
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = authStore.token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 处理401：尝试刷新Token，成功后重试原请求
function handleUnauthorized(originalRequest) {
  const authStore = useAuthStore()

  if (isRefreshing) {
    // 正在刷新中，将请求排队等待刷新完成后重试
    return new Promise((resolve) => {
      addPendingRequest(() => {
        originalRequest.headers.Authorization = authStore.token
        resolve(request(originalRequest))
      })
    })
  }

  isRefreshing = true

  return authStore
    .refreshToken()
    .then(() => {
      // 刷新成功，重试所有排队的请求
      onTokenRefreshed()
      // 重试原请求
      originalRequest.headers.Authorization = authStore.token
      return request(originalRequest)
    })
    .catch(() => {
      // 刷新失败，清除状态并跳转登录页
      message.error('登录已过期，请重新登录')
      authStore.clearAuthState()
      window.location.href = '/login'
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
    // 根据业务code判断请求是否成功
    if (res.code !== 200) {
      // 401: Token过期，尝试刷新
      if (res.code === 401) {
        const originalRequest = response.config
        return handleUnauthorized(originalRequest)
      }
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      const originalRequest = error.config
      return handleUnauthorized(originalRequest)
    }
    message.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request
