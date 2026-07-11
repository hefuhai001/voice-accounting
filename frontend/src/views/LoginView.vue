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
            @click="activeTab = tab.key"
            class="flex-1 py-2.5 text-label-md font-semibold rounded-xl transition-all duration-300"
            :class="activeTab === tab.key ? 'bg-white text-on-surface shadow-sm' : 'text-on-surface-variant'"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 登录表单 -->
        <a-form
          v-if="activeTab === 'login'"
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          @finish="handleLogin"
          layout="vertical"
          class="space-y-1"
        >
          <a-form-item name="username" class="!mb-5">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >用户名</label
            >
            <a-input
              v-model:value="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">person</span></template>
            </a-input>
          </a-form-item>

          <a-form-item name="password" class="!mb-6">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >密码</label
            >
            <a-input-password
              v-model:value="loginForm.password"
              placeholder="请输入密码"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">lock</span></template>
            </a-input-password>
          </a-form-item>

          <a-form-item class="!mb-0">
            <button
              type="submit"
              :disabled="loading"
              class="w-full h-12 rounded-2xl text-white text-headline-md font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-primary/30 hover:shadow-primary/40"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </a-form-item>
        </a-form>

        <!-- 注册表单 -->
        <a-form
          v-else-if="activeTab === 'register'"
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          @finish="handleRegister"
          layout="vertical"
          class="space-y-1"
        >
          <a-form-item name="username" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >用户名</label
            >
            <a-input
              v-model:value="registerForm.username"
              placeholder="3-20个字符"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">person</span></template>
            </a-input>
          </a-form-item>

          <a-form-item name="email" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >邮箱</label
            >
            <a-input
              v-model:value="registerForm.email"
              placeholder="请输入邮箱"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">mail</span></template>
            </a-input>
          </a-form-item>

          <a-form-item name="code" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >验证码</label
            >
            <div class="flex gap-2">
              <a-input
                v-model:value="registerForm.code"
                placeholder="6位验证码"
                size="large"
                :maxlength="6"
                class="login-input flex-1"
              >
                <template #prefix><span class="material-symbols-outlined text-on-surface-variant">verified_user</span></template>
              </a-input>
              <button
                type="button"
                :disabled="codeSending || countdown > 0 || !registerForm.email"
                @click="handleSendCode"
                class="flex-shrink-0 h-12 px-4 rounded-2xl text-label-md font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :class="countdown > 0
                  ? 'bg-surface-container-low text-on-surface-variant'
                  : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm hover:shadow-primary/30'"
              >
                {{ codeSending ? '发送中' : countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </a-form-item>

          <a-form-item name="password" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >密码</label
            >
            <a-input-password
              v-model:value="registerForm.password"
              placeholder="6-20个字符"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">lock</span></template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="nickname" class="!mb-6">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >昵称 <span class="font-normal normal-case opacity-60">(可选)</span></label
            >
            <a-input
              v-model:value="registerForm.nickname"
              placeholder="请输入昵称"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">face</span></template>
            </a-input>
          </a-form-item>

          <a-form-item class="!mb-0">
            <button
              type="submit"
              :disabled="loading"
              class="w-full h-12 rounded-2xl text-white text-headline-md font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-primary/30 hover:shadow-primary/40"
            >
              {{ loading ? '注册中...' : '注 册' }}
            </button>
          </a-form-item>
        </a-form>

        <!-- 找回密码表单 -->
        <a-form
          v-else-if="activeTab === 'reset'"
          :model="resetForm"
          :rules="resetRules"
          ref="resetFormRef"
          @finish="handleResetPassword"
          layout="vertical"
          class="space-y-1"
        >
          <a-form-item name="email" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >邮箱</label
            >
            <a-input
              v-model:value="resetForm.email"
              placeholder="请输入注册邮箱"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">mail</span></template>
            </a-input>
          </a-form-item>

          <a-form-item name="code" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >验证码</label
            >
            <div class="flex gap-2">
              <a-input
                v-model:value="resetForm.code"
                placeholder="6位验证码"
                size="large"
                :maxlength="6"
                class="login-input flex-1"
              >
                <template #prefix><span class="material-symbols-outlined text-on-surface-variant">verified_user</span></template>
              </a-input>
              <button
                type="button"
                :disabled="codeSending || resetCountdown > 0 || !resetForm.email"
                @click="handleSendResetCode"
                class="flex-shrink-0 h-12 px-4 rounded-2xl text-label-md font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :class="resetCountdown > 0
                  ? 'bg-surface-container-low text-on-surface-variant'
                  : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm hover:shadow-primary/30'"
              >
                {{ codeSending ? '发送中' : resetCountdown > 0 ? `${resetCountdown}s` : '获取验证码' }}
              </button>
            </div>
          </a-form-item>

          <a-form-item name="newPassword" class="!mb-4">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >新密码</label
            >
            <a-input-password
              v-model:value="resetForm.newPassword"
              placeholder="6-20个字符"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">lock</span></template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="confirmPassword" class="!mb-6">
            <label
              class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold"
              >确认密码</label
            >
            <a-input-password
              v-model:value="resetForm.confirmPassword"
              placeholder="再次输入新密码"
              size="large"
              class="login-input"
            >
              <template #prefix><span class="material-symbols-outlined text-on-surface-variant">lock</span></template>
            </a-input-password>
          </a-form-item>

          <a-form-item class="!mb-0">
            <button
              type="submit"
              :disabled="loading"
              class="w-full h-12 rounded-2xl text-white text-headline-md font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-primary/30 hover:shadow-primary/40"
            >
              {{ loading ? '重置中...' : '重置密码' }}
            </button>
          </a-form-item>
        </a-form>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'
import { authApi, captchaApi } from '@/api/user/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('login')
const loading = ref(false)
const loginFormRef = ref()
const registerFormRef = ref()

const tabOptions = [
  { key: 'login', label: '登录' },
  { key: 'register', label: '注册' },
  { key: 'reset', label: '找回密码' },
]

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  code: '',
  password: '',
  nickname: '',
})

// 找回密码表单
const resetFormRef = ref()
const resetForm = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: '',
})

// 验证码相关
const sliderVisible = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
const resetCountdown = ref(0)
let countdownTimer = null
let resetCountdownTimer = null

// 验证规则
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' },
  ],
}

// 找回密码确认密码验证
const validateResetConfirmPassword = async (_rule, value) => {
  if (value !== resetForm.newPassword) {
    return Promise.reject('两次密码输入不一致')
  }
  return Promise.resolve()
}

const resetRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateResetConfirmPassword, trigger: 'blur' },
  ],
}

// 点击获取验证码 -> 弹出滑块验证
const handleSendCode = () => {
  if (!registerForm.email) {
    message.warning('请先输入邮箱')
    return
  }
  sliderVisible.value = true
}

// 点击获取找回密码验证码 -> 弹出滑块验证
const handleSendResetCode = () => {
  if (!resetForm.email) {
    message.warning('请先输入邮箱')
    return
  }
  sliderVisible.value = true
}

// 滑块验证成功 -> 发送邮箱验证码
const onSliderSuccess = async (captchaToken) => {
  codeSending.value = true
  const email = activeTab.value === 'reset' ? resetForm.email : registerForm.email
  try {
    await captchaApi.sendEmailCode({
      email,
      captchaToken,
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
const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

const startResetCountdown = () => {
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
const handleLogin = async () => {
  loading.value = true
  try {
    await authStore.login(loginForm)
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
const handleRegister = async () => {
  loading.value = true
  try {
    await authStore.register(registerForm)
    message.success('注册成功')
    // 标记刚注册，用于在首页弹出 PWA 安装提示
    sessionStorage.setItem('just_registered', 'true')
    // 注册成功后自动获取用户信息并跳转
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
const handleResetPassword = async () => {
  loading.value = true
  try {
    await authApi.resetPassword({
      email: resetForm.email,
      code: resetForm.code,
      newPassword: resetForm.newPassword,
    })
    message.success('密码重置成功，请登录')
    activeTab.value = 'login'
    loginForm.username = ''
    loginForm.password = ''
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

/* 统一输入框样式 */
.login-input :deep(.ant-input-affix-wrapper) {
  background-color: #f8f9fc !important;
  border-radius: 1rem !important;
  border: none !important;
  height: 48px !important;
  padding: 0 16px !important;
  display: flex;
  align-items: center;
}

.login-input :deep(.ant-input) {
  background-color: transparent !important;
  height: auto !important;
  font-size: 16px !important;
}

.login-input :deep(.ant-input-affix-wrapper-focused) {
  border: 1px solid rgba(152, 63, 25, 0.3) !important;
  box-shadow: none !important;
}

.login-input :deep(.ant-input::placeholder) {
  color: #64748b !important;
}
</style>
