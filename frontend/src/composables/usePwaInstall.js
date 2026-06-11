import { ref, onMounted, onUnmounted } from 'vue'

// 全局共享状态，只初始化一次
let deferredPrompt = null
const isIOS = ref(false)
const isStandalone = ref(false)

/**
 * 检测是否为 iOS 设备
 */
function detectIOS() {
  const ua = navigator.userAgent
  return /iPad|iPhone|iPod/.test(ua) ||
    (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1)
}

/**
 * 检测是否已处于独立模式（已添加到主屏幕）
 */
function detectStandalone() {
  return window.matchMedia('(display-mode: standalone)').matches ||
    window.navigator.standalone === true
}

/**
 * PWA 安装提示 composable
 * - Android Chrome: 捕获 beforeinstallprompt 事件，调用原生安装弹窗
 * - iOS Safari: 显示手动添加引导
 */
export function usePwaInstall() {
  const showInstallModal = ref(false)

  onMounted(() => {
    isIOS.value = detectIOS()
    isStandalone.value = detectStandalone()

    // 监听 beforeinstallprompt 事件（Android Chrome）
    const handler = (e) => {
      e.preventDefault()
      deferredPrompt = e
    }
    window.addEventListener('beforeinstallprompt', handler)

    onUnmounted(() => {
      window.removeEventListener('beforeinstallprompt', handler)
    })
  })

  /**
   * 触发安装提示
   * Android: 调用原生 prompt
   * iOS: 显示引导弹窗
   */
  async function promptInstall() {
    // 已是独立模式，不再提示
    if (isStandalone.value) return

    if (deferredPrompt) {
      // Android Chrome: 原生安装弹窗
      deferredPrompt.prompt()
      const { outcome } = await deferredPrompt.userChoice
      console.log('用户安装选择:', outcome)
      deferredPrompt = null
      if (outcome === 'accepted') {
        localStorage.setItem('pwa_installed', 'true')
      }
    } else if (isIOS.value) {
      // iOS: 显示引导弹窗
      showInstallModal.value = true
    }
  }

  /**
   * 关闭 iOS 引导弹窗
   */
  function dismissInstall() {
    showInstallModal.value = false
    localStorage.setItem('pwa_dismissed', 'true')
  }

  /**
   * 检查是否应该显示安装提示（注册后首次进入时）
   */
  function shouldShowPrompt() {
    if (isStandalone.value) return false
    if (localStorage.getItem('pwa_installed')) return false
    if (localStorage.getItem('pwa_dismissed')) return false
    return true
  }

  return {
    isIOS,
    isStandalone,
    showInstallModal,
    promptInstall,
    dismissInstall,
    shouldShowPrompt,
  }
}
