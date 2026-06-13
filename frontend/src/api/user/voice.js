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
}
