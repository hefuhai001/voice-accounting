<template>
  <div class="min-h-screen bg-[#F2F2F7] flex flex-col font-[system-ui,_-apple-system,_sans-serif]">
    <!-- 顶部装饰区 -->
    <div class="flex-shrink-0 pt-12 pb-8 px-6 text-center relative overflow-hidden">
      <!-- 背景装饰圆 -->
      <div
        class="absolute -top-20 -left-20 w-64 h-64 rounded-full bg-gradient-to-br from-orange-200/40 to-pink-200/30 blur-3xl"
      />
      <div
        class="absolute -top-10 -right-16 w-48 h-48 rounded-full bg-gradient-to-br from-amber-200/30 to-yellow-200/20 blur-3xl"
      />

      <!-- Logo / 图标 -->
      <div
        class="relative inline-flex items-center justify-center w-20 h-20 rounded-3xl bg-gradient-to-br from-[#ff6b35] to-[#f7931e] shadow-lg shadow-orange-500/25 mb-5"
      >
        <svg
          class="w-10 h-10 text-white"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path
            d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z"
          />
        </svg>
      </div>

      <h1 class="text-[28px] font-bold text-[#1c1c1e] tracking-tight">语音记账</h1>
      <p class="text-sm text-[#8e8e93] mt-1 tracking-wide">Voice Accounting</p>
    </div>

    <!-- 表单卡片 -->
    <div class="flex-1 px-5 pb-8">
      <div class="bg-white rounded-3xl shadow-sm px-6 pt-6 pb-8 max-w-md mx-auto">
        <!-- iOS 分段控制器 -->
        <div class="flex bg-[#f2f2f7] rounded-xl p-1 mb-7">
          <button
            v-for="tab in tabOptions"
            :key="tab.key"
            @click="activeTab = tab.key"
            class="flex-1 py-2.5 text-[15px] font-semibold rounded-lg transition-all duration-300"
            :class="activeTab === tab.key ? 'bg-white text-[#1c1c1e] shadow-sm' : 'text-[#8e8e93]'"
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
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >用户名</label
            >
            <a-input
              v-model:value="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              class="!rounded-xl !h-12 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><UserOutlined class="text-[#c7c7cc]" /></template>
            </a-input>
          </a-form-item>

          <a-form-item name="password" class="!mb-6">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >密码</label
            >
            <a-input-password
              v-model:value="loginForm.password"
              placeholder="请输入密码"
              size="large"
              class="!rounded-xl !h-12 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><LockOutlined class="text-[#c7c7cc]" /></template>
            </a-input-password>
          </a-form-item>

          <a-form-item class="!mb-0">
            <button
              type="submit"
              :disabled="loading"
              class="w-full h-12 rounded-2xl text-white text-base font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-[#ff6b35] to-[#f7931e] shadow-orange-500/30 hover:shadow-orange-500/40"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </a-form-item>
        </a-form>

        <!-- 注册表单 -->
        <a-form
          v-else
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          @finish="handleRegister"
          layout="vertical"
          class="space-y-1"
        >
          <a-form-item name="username" class="!mb-4">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >用户名</label
            >
            <a-input
              v-model:value="registerForm.username"
              placeholder="3-20个字符"
              size="large"
              class="!rounded-xl !h-11 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><UserOutlined class="text-[#c7c7cc]" /></template>
            </a-input>
          </a-form-item>

          <a-form-item name="email" class="!mb-4">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >邮箱</label
            >
            <a-input
              v-model:value="registerForm.email"
              placeholder="请输入邮箱"
              size="large"
              class="!rounded-xl !h-11 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><MailOutlined class="text-[#c7c7cc]" /></template>
            </a-input>
          </a-form-item>

          <a-form-item name="code" class="!mb-4">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >验证码</label
            >
            <div class="flex gap-2">
              <a-input
                v-model:value="registerForm.code"
                placeholder="6位验证码"
                size="large"
                :maxlength="6"
                class="!rounded-xl !h-11 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px] flex-1"
              >
                <template #prefix><SafetyOutlined class="text-[#c7c7cc]" /></template>
              </a-input>
              <button
                type="button"
                :disabled="codeSending || countdown > 0 || !registerForm.email"
                @click="handleSendCode"
                class="flex-shrink-0 h-11 px-4 rounded-xl text-sm font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :class="countdown > 0
                  ? 'bg-[#f2f2f7] text-[#8e8e93]'
                  : 'bg-gradient-to-r from-[#ff6b35] to-[#f7931e] text-white shadow-sm hover:shadow-orange-500/30'"
              >
                {{ codeSending ? '发送中' : countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </a-form-item>

          <a-form-item name="password" class="!mb-4">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >密码</label
            >
            <a-input-password
              v-model:value="registerForm.password"
              placeholder="6-20个字符"
              size="large"
              class="!rounded-xl !h-11 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><LockOutlined class="text-[#c7c7cc]" /></template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="nickname" class="!mb-6">
            <label
              class="block text-xs font-semibold text-[#8e8e93] mb-1.5 ml-0.5 uppercase tracking-wider"
              >昵称 <span class="font-normal normal-case opacity-60">(可选)</span></label
            >
            <a-input
              v-model:value="registerForm.nickname"
              placeholder="请输入昵称"
              size="large"
              class="!rounded-xl !h-11 !bg-[#f9fafb] !border-transparent focus:!border-[#ff6b35]/30 focus:!shadow-none !px-4 !text-[15px]"
            >
              <template #prefix><SmileOutlined class="text-[#c7c7cc]" /></template>
            </a-input>
          </a-form-item>

          <a-form-item class="!mb-0">
            <button
              type="submit"
              :disabled="loading"
              class="w-full h-12 rounded-2xl text-white text-base font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-emerald-500 to-green-400 shadow-emerald-500/30 hover:shadow-emerald-500/40"
            >
              {{ loading ? '注册中...' : '注 册' }}
            </button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 底部提示 -->
      <p class="text-center text-xs text-[#c7c7cc] mt-6">安全登录 · 数据加密传输</p>
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
import { UserOutlined, LockOutlined, MailOutlined, SmileOutlined, SafetyOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { captchaApi } from '@/api/user/auth'
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

// 验证码相关
const sliderVisible = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
let countdownTimer = null

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

// 点击获取验证码 -> 弹出滑块验证
const handleSendCode = () => {
  if (!registerForm.email) {
    message.warning('请先输入邮箱')
    return
  }
  sliderVisible.value = true
}

// 滑块验证成功 -> 发送邮箱验证码
const onSliderSuccess = async (captchaToken) => {
  codeSending.value = true
  try {
    await captchaApi.sendEmailCode({
      email: registerForm.email,
      captchaToken,
    })
    message.success('验证码已发送')
    startCountdown()
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
</script>
