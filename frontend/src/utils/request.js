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
      message.error(res.message || '请求失败')
      // 401: 未登录或Cookie过期
      if (res.code === 401) {
        localStorage.removeItem('isLoggedIn')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      message.error('登录已过期，请重新登录')
      localStorage.removeItem('isLoggedIn')
      window.location.href = '/login'
    } else {
      message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  },
)

export default request
