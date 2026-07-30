import request from '@/utils/request'

export const authApi = {
  login(data) { return request.post('/api/auth/login', data) },
  register(data) { return request.post('/api/auth/register', data) },
  logout() { return request.post('/api/auth/logout') },
  getUserInfo() { return request.get('/api/auth/info') },
  resetPassword(data) { return request.post('/api/auth/reset-password', data) },
}

export const captchaApi = {
  getSliderCaptcha() { return request.get('/api/captcha/slider') },
  verifySlider(data) { return request.post('/api/captcha/slider/verify', data) },
  sendEmailCode(data) { return request.post('/api/captcha/email/send', data) },
}
