import request from '@/utils/request.js'

/**
 * 用户端-语音识别API
 */
export const voiceApi = {
  /**
   * 语音转文字
   * @param {Blob} file - WAV音频文件（16bit单声道16kHz）
   */
  recognize(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/api/voice/recognize', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 30000,
    })
  },

  /**
   * 语音智能记账 - 上传音频自动识别并完成记账
   * @param {Blob} file - WAV音频文件（16bit单声道16kHz）
   */
  bookkeep(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/api/voice/bookkeep', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000,
    })
  },

  /**
   * 文字智能记账 - 传入文字自动分析并完成记账
   * @param {string} text - 记账需求文字
   */
  bookkeepByText(text) {
    return request.post('/api/voice/bookkeep/text', { text }, {
      timeout: 60000,
    })
  },
}
