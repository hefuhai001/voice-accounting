import axios from 'axios'
import { message } from 'ant-design-vue'

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

// 是否正在刷新Token
let isRefreshing = false
// 刷新期间等待的请求队列
let pendingRequests = []

/**
 * 处理Token刷新
 * 成功后重试所有等待中的请求，失败则跳转登录页
 */
function handleTokenRefresh(failedRequest) {
  if (isRefreshing) {
    // 已有刷新请求进行中，将当前请求加入等待队列
    return new Promise((resolve, reject) => {
      pendingRequests.push({ resolve, reject, failedRequest })
    })
  }

  isRefreshing = true

  return axios
    .post('/api/auth/refresh-token', null, { withCredentials: true })
    .then(() => {
      // 刷新成功，重试所有等待中的请求
      pendingRequests.forEach(({ resolve, reject, failedRequest }) => {
        resolve(request(failedRequest.config))
      })
      pendingRequests = []
      // 重试当前请求
      return request(failedRequest.config)
    })
    .catch((error) => {
      // 刷新失败，拒绝所有等待中的请求
      pendingRequests.forEach(({ reject }) => reject(error))
      pendingRequests = []
      // 跳转登录页
      message.error('登录已过期，请重新登录')
      localStorage.removeItem('isLoggedIn')
      window.location.href = '/login'
      return Promise.reject(error)
    })
    .finally(() => {
      isRefreshing = false
    })
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // Sa-Token使用Cookie模式，由浏览器自动携带Cookie，无需手动添加Authorization头
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 根据业务code判断请求是否成功
    if (res.code !== 200) {
      // 401: Cookie过期，尝试无感刷新
      if (res.code === 401) {
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
