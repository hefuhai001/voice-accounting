<template>
  <div class="min-h-screen bg-background flex flex-col font-body-md mesh-bg">
    <!-- 顶部装饰区 -->
    <div class="flex-shrink-0 pt-12 pb-8 px-6 text-center relative overflow-hidden">
      <!-- 背景装饰圆 -->
      <div
        class="absolute -top-20 -left-20 w-64 h-64 rounded-full bg-gradient-to-br from-primary/20 to-secondary/10 blur-3xl"
      />
      <div
        class="absolute -top-10 -right-16 w-48 h-48 rounded-full bg-gradient-to-br from-tertiary/15 to-primary/10 blur-3xl"
      />

      <!-- Logo -->
      <div
        class="relative inline-flex items-center justify-center w-20 h-20 rounded-3xl bg-gradient-to-br from-[#983f19] to-[#ab3500] shadow-lg shadow-primary/25 mb-5"
      >
        <span class="material-symbols-outlined text-white text-[40px]" style="font-variation-settings: 'FILL' 1;">mic</span>
      </div>

      <h1 class="font-display-lg text-display-lg text-on-surface tracking-tight">语音记账</h1>
      <p class="text-label-sm text-on-surface-variant mt-1 uppercase tracking-widest">Voice Accounting</p>
    </div>

    <!-- 表单卡片 -->
    <div class="flex-1 px-5 pb-8">
      <div class="glass-panel rounded-3xl shadow-sm px-6 pt-6 pb-8 max-w-md mx-auto">
        <!-- iOS 分段控制器 -->
        <div class="flex bg-surface-container-low rounded-2xl p-1 mb-7">
          <button
            v-for="tab in tabOptions"
            :key="tab.key"
            @click="switchTab(tab.key)"
            class="flex-1 py-2.5 text-label-md font-semibold rounded-xl transition-all duration-300"
            :class="activeTab === tab.key ? 'bg-white text-on-surface shadow-sm' : 'text-on-surface-variant'"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 登录表单 -->
        <LoginForm
          v-if="activeTab === 'login'"
          ref="loginFormRef"
          :loading="loading"
          @submit="handleLogin"
        />

        <!-- 注册表单 -->
        <RegisterForm
          v-else-if="activeTab === 'register'"
          ref="registerFormRef"
          :loading="loading"
          :code-sending="codeSending"
          :countdown="countdown"
          @submit="handleRegister"
          @send-code="handleSendCode"
        />

        <!-- 找回密码表单 -->
        <ResetPasswordForm
          v-else-if="activeTab === 'reset'"
          ref="resetFormRef"
          :loading="loading"
          :code-sending="codeSending"
          :countdown="resetCountdown"
          @submit="handleResetPassword"
          @send-code="handleSendResetCode"
        />
      </div>

      <!-- 底部提示 -->
      <p class="text-center text-label-sm text-on-surface-variant/60 mt-6">安全登录 · 数据加密传输</p>
    </div>

    <!-- 滑块验证弹窗 -->
    <SliderCaptcha
      v-model:visible="sliderVisible"
      @success="onSliderSuccess"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'
import { authApi, captchaApi } from '@/api/user/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'
import LoginForm from './login/LoginForm.vue'
import RegisterForm from './login/RegisterForm.vue'
import ResetPasswordForm from './login/ResetPasswordForm.vue'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('login')
const loading = ref(false)

const loginFormRef = ref()
const registerFormRef = ref()
const resetFormRef = ref()

const tabOptions = [
  { key: 'login', label: '登录' },
  { key: 'register', label: '注册' },
  { key: 'reset', label: '找回密码' }
]

// 验证码相关
const sliderVisible = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
const resetCountdown = ref(0)
const currentEmail = ref('')
let countdownTimer = null
let resetCountdownTimer = null

// 切换 Tab
function switchTab(key) {
  activeTab.value = key
}

// 发送注册验证码
function handleSendCode(email) {
  currentEmail.value = email
  sliderVisible.value = true
}

// 发送找回密码验证码
function handleSendResetCode(email) {
  currentEmail.value = email
  sliderVisible.value = true
}

// 滑块验证成功 -> 发送邮箱验证码
async function onSliderSuccess(captchaToken) {
  codeSending.value = true
  try {
    await captchaApi.sendEmailCode({
      email: currentEmail.value,
      captchaToken
    })
    message.success('验证码已发送')
    if (activeTab.value === 'reset') {
      startResetCountdown()
    } else {
      startCountdown()
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
  } finally {
    codeSending.value = false
  }
}

// 倒计时
function startCountdown() {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

function startResetCountdown() {
  resetCountdown.value = 60
  resetCountdownTimer = setInterval(() => {
    resetCountdown.value--
    if (resetCountdown.value <= 0) {
      clearInterval(resetCountdownTimer)
      resetCountdownTimer = null
    }
  }, 1000)
}

// 处理登录
async function handleLogin(data) {
  loading.value = true
  try {
    await authStore.login(data)
    message.success('登录成功')
    await authStore.getUserInfo()
    if (authStore.isAdmin) {
      router.push('/manage')
    } else {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理注册
async function handleRegister(data) {
  loading.value = true
  try {
    await authStore.register(data)
    message.success('注册成功')
    sessionStorage.setItem('just_registered', 'true')
    await authStore.getUserInfo()
    if (authStore.isAdmin) {
      router.push('/manage')
    } else {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理找回密码
async function handleResetPassword(data) {
  loading.value = true
  try {
    await authApi.resetPassword(data)
    message.success('密码重置成功，请登录')
    activeTab.value = 'login'
    loginFormRef.value?.reset()
  } catch (error) {
    console.error('重置密码失败:', error)
    message.error(error.response?.data?.msg || '重置密码失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
</style>
