import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { voiceApi } from '@/api'

/**
 * 语音录音与识别 composable
 * 使用 Web Audio API 录制 PCM 数据并编码为 WAV（16bit单声道16kHz）
 */
export function useVoiceRecorder() {
  const isRecording = ref(false)
  const isRecognizing = ref(false)

  let audioContext = null
  let mediaStream = null
  let scriptProcessor = null
  let pcmChunks = []

  /**
   * 开始录音
   */
  async function startRecording() {
    try {
      mediaStream = await navigator.mediaDevices.getUserMedia({
        audio: {
          channelCount: 1,
          sampleRate: 16000,
          echoCancellation: true,
          noiseSuppression: true,
        },
      })

      audioContext = new AudioContext({ sampleRate: 16000 })
      const source = audioContext.createMediaStreamSource(mediaStream)

      // 使用 ScriptProcessorNode 捕获 PCM 数据
      scriptProcessor = audioContext.createScriptProcessor(4096, 1, 1)
      pcmChunks = []

      scriptProcessor.onaudioprocess = (event) => {
        if (!isRecording.value) return
        const inputData = event.inputBuffer.getChannelData(0)
        // 复制一份数据，避免引用问题
        pcmChunks.push(new Float32Array(inputData))
      }

      source.connect(scriptProcessor)
      scriptProcessor.connect(audioContext.destination)

      isRecording.value = true
    } catch (error) {
      console.error('录音启动失败:', error)
      if (error.name === 'NotAllowedError') {
        message.error('请允许麦克风权限')
      } else {
        message.error('录音启动失败: ' + error.message)
      }
    }
  }

  /**
   * 停止录音并返回WAV Blob
   */
  function stopRecording() {
    return new Promise((resolve) => {
      isRecording.value = false

      if (scriptProcessor) {
        scriptProcessor.disconnect()
        scriptProcessor = null
      }
      if (audioContext) {
        audioContext.close()
        audioContext = null
      }
      if (mediaStream) {
        mediaStream.getTracks().forEach((track) => track.stop())
        mediaStream = null
      }

      if (pcmChunks.length === 0) {
        resolve(null)
        return
      }

      // 合并所有PCM数据
      const totalLength = pcmChunks.reduce((acc, chunk) => acc + chunk.length, 0)
      const merged = new Float32Array(totalLength)
      let offset = 0
      for (const chunk of pcmChunks) {
        merged.set(chunk, offset)
        offset += chunk.length
      }
      pcmChunks = []

      // 编码为WAV
      const wavBlob = encodeWAV(merged, 16000)
      resolve(wavBlob)
    })
  }

  /**
   * 停止录音并调用识别接口
   * @returns {Promise<string|null>} 识别出的文字
   */
  async function stopAndRecognize() {
    const wavBlob = await stopRecording()
    if (!wavBlob) {
      message.warning('未录制到音频')
      return null
    }

    isRecognizing.value = true
    try {
      const res = await voiceApi.recognize(wavBlob)
      return res.data.text
    } catch (error) {
      console.error('语音识别失败:', error)
      message.error('语音识别失败')
      return null
    } finally {
      isRecognizing.value = false
    }
  }

  /**
   * 将PCM Float32数据编码为WAV格式Blob
   */
  function encodeWAV(samples, sampleRate) {
    const buffer = new ArrayBuffer(44 + samples.length * 2)
    const view = new DataView(buffer)

    // RIFF头
    writeString(view, 0, 'RIFF')
    view.setUint32(4, 36 + samples.length * 2, true)
    writeString(view, 8, 'WAVE')

    // fmt子块
    writeString(view, 12, 'fmt ')
    view.setUint32(16, 16, true) // 子块大小
    view.setUint16(20, 1, true) // PCM格式
    view.setUint16(22, 1, true) // 单声道
    view.setUint32(24, sampleRate, true) // 采样率
    view.setUint32(28, sampleRate * 2, true) // 字节率
    view.setUint16(32, 2, true) // 块对齐
    view.setUint16(34, 16, true) // 位深度

    // data子块
    writeString(view, 36, 'data')
    view.setUint32(40, samples.length * 2, true)

    // 写入PCM采样数据（Float32转Int16）
    floatTo16BitPCM(view, 44, samples)

    return new Blob([buffer], { type: 'audio/wav' })
  }

  function writeString(view, offset, str) {
    for (let i = 0; i < str.length; i++) {
      view.setUint8(offset + i, str.charCodeAt(i))
    }
  }

  function floatTo16BitPCM(view, offset, samples) {
    for (let i = 0; i < samples.length; i++, offset += 2) {
      const s = Math.max(-1, Math.min(1, samples[i]))
      view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7fff, true)
    }
  }

  return {
    isRecording,
    isRecognizing,
    startRecording,
    stopRecording,
    stopAndRecognize,
  }
}
