<template>
  <view class="min-h-screen bg-background flex flex-col">
    <!-- Status bar padding -->
    <view :style="{ height: statusBarHeight + 'px' }"></view>

    <!-- Top decorative area -->
    <view class="pt-10 pb-6 px-6 items-center relative overflow-hidden">
      <view class="absolute -top-20 -left-20 w-64 h-64 rounded-full bg-gradient-to-br from-primary/20 to-secondary/10 opacity-30"></view>
      <view class="relative items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-[#983f19] to-[#ab3500] shadow-lg mb-4 mx-auto flex">
        <text class="text-white text-3xl" style="font-family: 'Material Symbols Outlined'; font-variation-settings: 'FILL' 1;">mic</text>
      </view>
      <text class="font-display-lg text-on-surface tracking-tight block text-center">语音记账</text>
      <text class="text-label-sm text-on-surface-variant uppercase tracking-widest mt-1 block text-center">Voice Accounting</text>
    </view>

    <!-- Form Card -->
    <view class="flex-1 px-5 pb-6">
      <view class="glass-panel rounded-3xl shadow-sm px-5 pt-5 pb-6 max-w-md mx-auto">
        <!-- iOS Segmented Control -->
        <view class="flex bg-surface-container-low rounded-2xl p-1 mb-7">
          <view v-for="tab in tabOptions" :key="tab.key" @click="switchTab(tab.key)" class="flex-1 py-2.5 text-center font-semibold rounded-xl transition-all" :class="activeTab === tab.key ? 'bg-white text-on-surface shadow-sm' : 'text-on-surface-variant'">
            <text :class="activeTab === tab.key ? 'text-on-surface' : 'text-on-surface-variant'" class="font-semibold">{{ tab.label }}</text>
          </view>
        </view>

        <!-- Login Form -->
        <view v-if="activeTab === 'login'" class="space-y-5">
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">用户名 / 邮箱</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">person</text>
              <input v-model="loginForm.account" placeholder="请输入用户名或邮箱" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">密码</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">lock</text>
              <input v-model="loginForm.password" placeholder="请输入密码" :password="true" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view @click="handleLogin" class="w-full h-12 rounded-2xl flex items-center justify-center text-white font-semibold shadow-lg active:scale-[0.98] bg-gradient-to-r from-[#983f19] to-[#ab3500]">
            <text class="text-white font-semibold">{{ loading ? '登录中...' : '登 录' }}</text>
          </view>
        </view>

        <!-- Register Form -->
        <view v-if="activeTab === 'register'" class="space-y-4">
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">用户名</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">person</text>
              <input v-model="registerForm.username" placeholder="3-20个字符" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">邮箱</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">mail</text>
              <input v-model="registerForm.email" placeholder="请输入邮箱" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">验证码</text>
            <view class="flex gap-2">
              <view class="flex-1 flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
                <text class="material-symbols-outlined text-on-surface-variant mr-2">verified_user</text>
                <input v-model="registerForm.code" placeholder="6位验证码" maxlength="6" class="flex-1 bg-transparent text-[16px]" />
              </view>
              <view @click="handleSendCode(registerForm.email)" class="flex-shrink-0 h-12 px-4 rounded-2xl flex items-center justify-center font-semibold" :class="codeSending || countdown > 0 || !registerForm.email ? 'bg-surface-container-low text-on-surface-variant opacity-50' : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm'">
                <text :class="countdown > 0 ? 'text-on-surface-variant' : 'text-white'" class="font-semibold">{{ codeSending ? '发送中' : countdown > 0 ? countdown + 's' : '获取验证码' }}</text>
              </view>
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">密码</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">lock</text>
              <input v-model="registerForm.password" placeholder="6-20个字符" :password="true" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">昵称 <text class="font-normal normal-case opacity-60">(可选)</text></text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">face</text>
              <input v-model="registerForm.nickname" placeholder="请输入昵称" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view @click="handleRegister" class="w-full h-12 rounded-2xl flex items-center justify-center text-white font-semibold shadow-lg active:scale-[0.98] bg-gradient-to-r from-[#983f19] to-[#ab3500]">
            <text class="text-white font-semibold">{{ loading ? '注册中...' : '注 册' }}</text>
          </view>
        </view>

        <!-- Reset Password Form -->
        <view v-if="activeTab === 'reset'" class="space-y-4">
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">邮箱</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">mail</text>
              <input v-model="resetForm.email" placeholder="请输入注册邮箱" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">验证码</text>
            <view class="flex gap-2">
              <view class="flex-1 flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
                <text class="material-symbols-outlined text-on-surface-variant mr-2">verified_user</text>
                <input v-model="resetForm.code" placeholder="6位验证码" maxlength="6" class="flex-1 bg-transparent text-[16px]" />
              </view>
              <view @click="handleSendCode(resetForm.email)" class="flex-shrink-0 h-12 px-4 rounded-2xl flex items-center justify-center font-semibold" :class="codeSending || resetCountdown > 0 || !resetForm.email ? 'bg-surface-container-low text-on-surface-variant opacity-50' : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm'">
                <text :class="resetCountdown > 0 ? 'text-on-surface-variant' : 'text-white'" class="font-semibold">{{ codeSending ? '发送中' : resetCountdown > 0 ? resetCountdown + 's' : '获取验证码' }}</text>
              </view>
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">新密码</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">lock</text>
              <input v-model="resetForm.newPassword" placeholder="6-20个字符" :password="true" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view>
            <text class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">确认密码</text>
            <view class="flex items-center bg-surface-container-low rounded-2xl h-12 px-4">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">lock</text>
              <input v-model="resetForm.confirmPassword" placeholder="再次输入新密码" :password="true" class="flex-1 bg-transparent text-[16px]" />
            </view>
          </view>
          <view @click="handleResetPassword" class="w-full h-12 rounded-2xl flex items-center justify-center text-white font-semibold shadow-lg active:scale-[0.98] bg-gradient-to-r from-[#983f19] to-[#ab3500]">
            <text class="text-white font-semibold">{{ loading ? '重置中...' : '重置密码' }}</text>
          </view>
        </view>
      </view>

      <text class="block text-center text-label-sm text-on-surface-variant/60 mt-6">安全登录 · 数据加密传输</text>
    </view>

    <!-- Slider Captcha -->
    <SliderCaptcha v-model:visible="sliderVisible" @success="onSliderSuccess" />
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { authApi, captchaApi } from '@/api/user/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const authStore = useAuthStore()
const statusBarHeight = ref(0)
uni.getSystemInfoSync && (statusBarHeight.value = uni.getSystemInfoSync().statusBarHeight || 0)

const activeTab = ref('login')
const loading = ref(false)
const tabOptions = [
  { key: 'login', label: '登录' },
  { key: 'register', label: '注册' },
  { key: 'reset', label: '找回密码' }
]

// Forms
const loginForm = reactive({ account: '', password: '' })
const registerForm = reactive({ username: '', email: '', code: '', password: '', nickname: '' })
const resetForm = reactive({ email: '', code: '', newPassword: '', confirmPassword: '' })

// Captcha
const sliderVisible = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
const resetCountdown = ref(0)
const currentEmail = ref('')
let countdownTimer = null
let resetCountdownTimer = null

function switchTab(key) { activeTab.value = key }

function handleSendCode(email) {
  if (!email) { uni.showToast({ title: '请输入邮箱', icon: 'none' }); return }
  currentEmail.value = email
  sliderVisible.value = true
}

async function onSliderSuccess(captchaToken) {
  codeSending.value = true
  try {
    await captchaApi.sendEmailCode({ email: currentEmail.value, captchaToken })
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    if (activeTab.value === 'reset') startResetCountdown()
    else startCountdown()
  } catch (error) {
    console.error('发送验证码失败:', error)
  } finally {
    codeSending.value = false
  }
}

function startCountdown() {
  countdown.value = 60
  countdownTimer = setInterval(() => { countdown.value--; if (countdown.value <= 0) { clearInterval(countdownTimer); countdownTimer = null } }, 1000)
}

function startResetCountdown() {
  resetCountdown.value = 60
  resetCountdownTimer = setInterval(() => { resetCountdown.value--; if (resetCountdown.value <= 0) { clearInterval(resetCountdownTimer); resetCountdownTimer = null } }, 1000)
}

async function handleLogin() {
  if (!loginForm.account || !loginForm.password) { uni.showToast({ title: '请输入用户名和密码', icon: 'none' }); return }
  loading.value = true
  try {
    await authStore.login({ account: loginForm.account, password: loginForm.password })
    await authStore.getUserInfo()
    uni.showToast({ title: '登录成功', icon: 'success' })
    uni.switchTab({ url: '/pages/dashboard/dashboard' })
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.email || !registerForm.code || !registerForm.password) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' }); return
  }
  loading.value = true
  try {
    await authStore.register(registerForm)
    await authStore.getUserInfo()
    uni.showToast({ title: '注册成功', icon: 'success' })
    uni.switchTab({ url: '/pages/dashboard/dashboard' })
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleResetPassword() {
  if (!resetForm.email || !resetForm.code || !resetForm.newPassword) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' }); return
  }
  if (resetForm.newPassword !== resetForm.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' }); return
  }
  loading.value = true
  try {
    await authApi.resetPassword(resetForm)
    uni.showToast({ title: '密码重置成功，请登录', icon: 'success' })
    activeTab.value = 'login'
    loginForm.account = ''
    loginForm.password = ''
  } catch (error) {
    console.error('重置密码失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
</style>
