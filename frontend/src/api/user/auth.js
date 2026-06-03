import request from '@/utils/request.js'

/**
 * 用户端-认证API
 */
export const authApi = {
  /**
   * 用户登录（管理员和普通用户通用）
   * @param {Object} data - 登录数据
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise<{token: string, expiresIn: number}>}
   */
  login(data) {
    return request.post('/api/auth/login', data)
  },

  /**
   * 用户注册（默认为普通用户角色）
   * @param {Object} data - 注册数据
   * @param {string} data.username - 用户名（3-20字符）
   * @param {string} data.password - 密码（6-20字符）
   * @param {string} data.email - 邮箱
   * @param {string} [data.nickname] - 昵称（可选）
   */
  register(data) {
    return request.post('/api/auth/register', data)
  },

  /**
   * 无感刷新Token（延长有效期）
   * @returns {Promise<{token: string, expiresIn: number}>}
   */
  refreshToken() {
    return request.post('/api/auth/refresh-token')
  },

  /**
   * 用户登出
   */
  logout() {
    return request.post('/api/auth/logout')
  },

  /**
   * 获取当前登录用户信息
   * @returns {Promise<number>} 返回用户ID
   */
  getUserInfo() {
    return request.get('/api/auth/info')
  },
}
