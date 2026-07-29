import request from '@/utils/request'
export const profileApi = {
  getUserInfo(userId) { return request.get(`/api/user/info/${userId}`) },
  updateUserInfo(userId, data, emailCode) {
    return request.put(`/api/user/${userId}`, data, { params: { emailCode } })
  },
  updatePassword(userId, oldPassword, newPassword) {
    return request.put(`/api/user/${userId}/password`, null, { params: { oldPassword, newPassword } })
  },
  checkEmail(email, excludeUserId) {
    return request.get('/api/user/check-email', { params: { email, excludeUserId } })
  },
}
