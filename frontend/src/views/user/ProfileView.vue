<template>
  <div class="px-margin-mobile pt-16 pb-28 max-w-md mx-auto">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="font-display-lg text-display-lg text-on-surface tracking-tight">个人中心</h1>
      <p class="text-label-sm text-on-surface-variant uppercase tracking-widest mt-1">MY PROFILE</p>
    </div>

    <!-- 用户头像卡片 -->
    <div class="glass-panel rounded-3xl p-4 mb-4 text-center">
      <div class="relative inline-block">
        <div
          class="w-20 h-20 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] flex items-center justify-center text-white text-[36px] font-bold shadow-lg shadow-primary/30"
        >
          {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) || 'U' }}
        </div>
        <button
          class="absolute -bottom-1 -right-1 w-8 h-8 rounded-full bg-surface-container-low border-2 border-white shadow-sm flex items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors"
          @click="showAvatarModal = true"
        >
          <span class="material-symbols-outlined text-[18px]">edit</span>
        </button>
      </div>
      <h2 class="font-headline-lg text-headline-lg text-on-surface mt-4">{{ userInfo?.nickname || '用户' }}</h2>
      <p class="text-label-sm text-on-surface-variant mt-1">@{{ userInfo?.username }}</p>
    </div>

    <!-- 基本信息 -->
    <div class="glass-panel rounded-3xl p-4 mb-4">
      <div class="flex items-center justify-between mb-3">
        <h3 class="font-headline-md text-on-surface flex items-center gap-2">
          <span class="material-symbols-outlined text-primary">person</span>
          基本信息
        </h3>
        <button
          @click="editMode = !editMode"
          class="text-primary font-label-md flex items-center gap-1"
        >
          <span class="material-symbols-outlined text-[18px]">{{ editMode ? 'close' : 'edit' }}</span>
          {{ editMode ? '取消' : '编辑' }}
        </button>
      </div>

      <a-form v-if="editMode" :model="formState" layout="vertical" @finish="handleUpdateInfo">
        <a-form-item label="昵称" name="nickname">
          <a-input v-model:value="formState.nickname" placeholder="请输入昵称" size="large" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <div class="flex gap-2">
            <a-input v-model:value="formState.email" placeholder="请输入新邮箱" size="large" class="flex-1" />
            <button
              type="button"
              :disabled="codeSending || countdown > 0 || !formState.email || formState.email === userInfo?.email"
              @click="handleSendEmailCode"
              class="flex-shrink-0 h-12 px-4 rounded-2xl text-label-md font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              :class="countdown > 0
                ? 'bg-surface-container-low text-on-surface-variant'
                : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm hover:shadow-primary/30'"
            >
              {{ codeSending ? '发送中' : countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </button>
          </div>
          <p v-if="formState.email !== userInfo?.email" class="text-label-sm text-on-surface-variant mt-2">
            邮箱变更需验证新邮箱
          </p>
        </a-form-item>
        <a-form-item v-if="formState.email !== userInfo?.email" label="验证码" name="code">
          <a-input v-model:value="emailCode" placeholder="请输入6位验证码" size="large" :maxlength="6" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formState.phone" placeholder="请输入手机号" size="large" />
        </a-form-item>
        <div class="flex gap-3">
          <a-button block size="large" @click="cancelEdit">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="saving">
            保存
          </a-button>
        </div>
      </a-form>

      <div v-else class="space-y-4">
        <div class="flex items-center justify-between py-3 border-b border-surface-variant/50">
          <span class="text-label-sm text-on-surface-variant">昵称</span>
          <span class="text-body-md text-on-surface">{{ userInfo?.nickname || '-' }}</span>
        </div>
        <div class="flex items-center justify-between py-3 border-b border-surface-variant/50">
          <span class="text-label-sm text-on-surface-variant">邮箱</span>
          <span class="text-body-md text-on-surface">{{ userInfo?.email || '-' }}</span>
        </div>
        <div class="flex items-center justify-between py-3 border-b border-surface-variant/50">
          <span class="text-label-sm text-on-surface-variant">手机号</span>
          <span class="text-body-md text-on-surface">{{ userInfo?.phone || '-' }}</span>
        </div>
        <div class="flex items-center justify-between py-3">
          <span class="text-label-sm text-on-surface-variant">角色</span>
          <span class="px-3 py-1 rounded-full text-label-sm font-semibold"
            :class="userInfo?.role === 1 ? 'bg-primary/10 text-primary' : 'bg-surface-container-low text-on-surface-variant'">
            {{ userInfo?.role === 1 ? '管理员' : '普通用户' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 安全设置 -->
    <div class="glass-panel rounded-3xl p-4 mb-4">
      <h3 class="font-headline-md text-on-surface flex items-center gap-2 mb-3">
        <span class="material-symbols-outlined text-primary">security</span>
        安全设置
      </h3>
      <button
        @click="showPasswordModal = true"
        class="w-full flex items-center justify-between py-4 px-4 rounded-2xl bg-surface-container-low hover:bg-surface-container transition-colors"
      >
        <div class="flex items-center gap-3">
          <span class="material-symbols-outlined text-on-surface-variant">lock</span>
          <span class="text-body-md text-on-surface">修改密码</span>
        </div>
        <span class="material-symbols-outlined text-on-surface-variant">chevron_right</span>
      </button>
    </div>

    <!-- 账户信息 -->
    <div class="glass-panel rounded-3xl p-4">
      <h3 class="font-headline-md text-on-surface flex items-center gap-2 mb-3">
        <span class="material-symbols-outlined text-primary">info</span>
        账户信息
      </h3>
      <div class="space-y-4">
        <div class="flex items-center justify-between py-3 border-b border-surface-variant/50">
          <span class="text-label-sm text-on-surface-variant">注册时间</span>
          <span class="text-body-md text-on-surface">{{ userInfo?.createdAt || '-' }}</span>
        </div>
        <div class="flex items-center justify-between py-3">
          <span class="text-label-sm text-on-surface-variant">账户状态</span>
          <span class="px-3 py-1 rounded-full text-label-sm font-semibold bg-success-green/10 text-success-green">
            正常
          </span>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <a-modal
      :open="showPasswordModal"
      title="修改密码"
      :footer="null"
      :width="380"
      centered
      @cancel="showPasswordModal = false"
    >
      <a-form :model="passwordForm" layout="vertical" class="mt-4" @finish="handleUpdatePassword">
        <a-form-item label="原密码" name="oldPassword" :rules="[{ required: true, message: '请输入原密码' }]">
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" size="large" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword" :rules="[{ required: true, min: 6, message: '密码至少6位' }]">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" size="large" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword" :rules="[
          { required: true, message: '请确认新密码' },
          { validator: validateConfirmPassword }
        ]">
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请确认新密码" size="large" />
        </a-form-item>
        <div class="flex gap-3">
          <a-button block size="large" @click="showPasswordModal = false">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="saving">
            确认修改
          </a-button>
        </div>
      </a-form>
    </a-modal>

    <!-- 滑块验证弹窗 -->
    <SliderCaptcha
      v-model:visible="sliderVisible"
      @success="onSliderSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'
import { profileApi } from '@/api/user/profile'
import { captchaApi } from '@/api/user/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const authStore = useAuthStore()
const userInfo = ref(authStore.userInfo)
const editMode = ref(false)
const saving = ref(false)
const showPasswordModal = ref(false)
const showAvatarModal = ref(false)

// 验证码相关
const emailCode = ref('')
const codeSending = ref(false)
const countdown = ref(0)
const sliderVisible = ref(false)
let countdownTimer = null

const formState = reactive({
  nickname: '',
  email: '',
  phone: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 初始化表单数据
function initForm() {
  if (userInfo.value) {
    formState.nickname = userInfo.value.nickname || ''
    formState.email = userInfo.value.email || ''
    formState.phone = userInfo.value.phone || ''
  }
}

// 取消编辑，重置验证码
function cancelEdit() {
  editMode.value = false
  emailCode.value = ''
  countdown.value = 0
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  initForm()
}

// 点击获取验证码 -> 弹出滑块验证
function handleSendEmailCode() {
  if (!formState.email) {
    message.warning('请先输入新邮箱')
    return
  }
  if (formState.email === userInfo.value?.email) {
    message.warning('邮箱未变更')
    return
  }
  sliderVisible.value = true
}

// 滑块验证成功 -> 发送邮箱验证码
async function onSliderSuccess(captchaToken) {
  codeSending.value = true
  try {
    await captchaApi.sendEmailCode({
      email: formState.email,
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

// 验证确认密码
const validateConfirmPassword = async (_rule, value) => {
  if (value !== passwordForm.newPassword) {
    return Promise.reject('两次密码输入不一致')
  }
  return Promise.resolve()
}

// 更新用户信息
async function handleUpdateInfo() {
  // 如果邮箱变更，必须验证验证码
  if (formState.email !== userInfo.value?.email) {
    if (!emailCode.value || emailCode.value.length !== 6) {
      message.warning('请输入6位验证码')
      return
    }
  }

  saving.value = true
  try {
    // 如果邮箱变更，传递验证码
    const codeToSend = formState.email !== userInfo.value?.email ? emailCode.value : undefined
    await profileApi.updateUserInfo(userInfo.value.id, formState, codeToSend)
    message.success('更新成功')
    editMode.value = false
    emailCode.value = ''
    countdown.value = 0
    if (countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
    // 更新 authStore
    authStore.userInfo = { ...authStore.userInfo, ...formState }
    userInfo.value = authStore.userInfo
  } catch (error) {
    console.error('更新失败:', error)
    message.error(error.response?.data?.msg || '更新失败')
  } finally {
    saving.value = false
  }
}

// 修改密码
async function handleUpdatePassword() {
  saving.value = true
  try {
    await profileApi.updatePassword(
      userInfo.value.id,
      passwordForm.oldPassword,
      passwordForm.newPassword
    )
    message.success('密码修改成功')
    showPasswordModal.value = false
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    })
  } catch (error) {
    console.error('修改失败:', error)
    message.error(error.response?.data?.msg || '修改失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  initForm()
})
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
