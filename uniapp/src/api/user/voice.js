import request from '@/utils/request'

// #ifdef H5
const BASE_URL = import.meta.env.DEV ? '' : 'https://www.hfh.asia'
// #endif
// #ifdef APP-PLUS
const BASE_URL = 'https://www.hfh.asia'
// #endif

export const voiceApi = {
  recognize(filePath) {
    return new Promise((resolve, reject) => {
      const authStore = require('@/stores/auth').useAuthStore()
      uni.uploadFile({
        url: `${BASE_URL}/api/voice/recognize`,
        filePath,
        name: 'file',
        header: { 'Authorization': authStore.accessToken },
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 200) resolve(data)
          else reject(new Error(data.message))
        },
        fail: reject,
      })
    })
  },
  bookkeep(filePath) {
    return new Promise((resolve, reject) => {
      const authStore = require('@/stores/auth').useAuthStore()
      uni.uploadFile({
        url: `${BASE_URL}/api/voice/bookkeep`,
        filePath,
        name: 'file',
        header: { 'Authorization': authStore.accessToken },
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 200) resolve(data)
          else reject(new Error(data.message))
        },
        fail: reject,
      })
    })
  },
  bookkeepByText(text) {
    return request.post('/api/voice/bookkeep/text', { text }, { timeout: 60000 })
  },
}
