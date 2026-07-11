import request from '@/utils/request'

/**
 * 用户端-个人中心 API
 */
export const profileApi = {
  /**
   * 获取用户信息
   */
  getUserInfo(userId) {
    return request.get(`/api/user/info/${userId}`)
  },

  /**
   * 更新用户信息（邮箱变更需验证码）
   */
  updateUserInfo(userId, data, emailCode) {
    return request.put(`/api/user/${userId}`, data, {
      params: { emailCode }
    })
  },

  /**
   * 修改密码
   */
  updatePassword(userId, oldPassword, newPassword) {
    return request.put(`/api/user/${userId}/password`, null, {
      params: { oldPassword, newPassword }
    })
  },

  /**
   * 检查邮箱是否已存在
   */
  checkEmail(email, excludeUserId) {
    return request.get('/api/user/check-email', {
      params: { email, excludeUserId }
    })
  }
}
