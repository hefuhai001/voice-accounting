<template>
  <view class="flex-1 min-h-screen bg-surface">
    <!-- 自定义导航栏 -->
    <view class="flex items-center px-4 h-12 pt-8">
      <view @click="goBack" class="w-10 h-10 flex items-center justify-center">
        <text class="material-symbols-outlined text-on-surface">arrow_back</text>
      </view>
      <text class="flex-1 text-center font-headline-md text-on-surface">个人中心</text>
      <view class="w-10"></view>
    </view>

    <!-- 用户头像卡片 -->
    <view class="px-4 mt-4 mb-6">
      <view class="glass-panel rounded-2xl p-6 flex items-center">
        <view class="w-16 h-16 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] flex items-center justify-center mr-4">
          <text class="text-2xl text-white font-bold">{{ avatarLetter }}</text>
        </view>
        <view class="flex-1">
          <text class="text-headline-md text-on-surface">{{ userInfo.nickname || '用户' }}</text>
          <text class="text-label-md text-on-surface-variant mt-1">{{ roleLabel }}</text>
        </view>
        <view
          v-if="!isEditing"
          class="w-10 h-10 rounded-full bg-surface-container flex items-center justify-center"
          @click="startEdit"
        >
          <text class="material-symbols-outlined text-on-surface-variant">edit</text>
        </view>
      </view>
    </view>

    <!-- 基本信息 - 查看模式 -->
    <view v-if="!isEditing" class="px-4 mb-6">
      <text class="text-label-caps text-on-surface-variant mb-3 block">基本信息</text>
      <view class="glass-panel rounded-2xl overflow-hidden">
        <view class="flex items-center px-4 py-3.5 border-b border-outline-variant/30">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">person</text>
          <text class="text-label-md text-on-surface-variant w-16">昵称</text>
          <text class="text-body-md text-on-surface flex-1 text-right">{{ userInfo.nickname || '-' }}</text>
        </view>
        <view class="flex items-center px-4 py-3.5 border-b border-outline-variant/30">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">email</text>
          <text class="text-label-md text-on-surface-variant w-16">邮箱</text>
          <text class="text-body-md text-on-surface flex-1 text-right">{{ userInfo.email || '-' }}</text>
        </view>
        <view class="flex items-center px-4 py-3.5 border-b border-outline-variant/30">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">phone</text>
          <text class="text-label-md text-on-surface-variant w-16">手机</text>
          <text class="text-body-md text-on-surface flex-1 text-right">{{ userInfo.phone || '-' }}</text>
        </view>
        <view class="flex items-center px-4 py-3.5">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">badge</text>
          <text class="text-label-md text-on-surface-variant w-16">角色</text>
          <text class="text-body-md text-on-surface flex-1 text-right">{{ roleLabel }}</text>
        </view>
      </view>
    </view>

    <!-- 基本信息 - 编辑模式 -->
    <view v-else class="px-4 mb-6">
      <text class="text-label-caps text-on-surface-variant mb-3 block">编辑信息</text>
      <view class="glass-panel rounded-2xl p-4">
        <!-- 昵称 -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">昵称</text>
          <input
            v-model="editForm.nickname"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请输入昵称"
          />
        </view>

        <!-- 邮箱 -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">邮箱</text>
          <input
            v-model="editForm.email"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请输入邮箱"
          />
          <!-- 邮箱变更时显示验证码 -->
          <view v-if="isEmailChanged" class="mt-3">
            <view class="flex gap-2 mb-2">
              <view
                class="px-4 py-2 rounded-xl bg-primary/10 flex items-center justify-center"
                @click="sendEmailCode"
              >
                <text class="text-label-md text-primary">{{ emailCodeCountdown > 0 ? `${emailCodeCountdown}s` : '发送验证码' }}</text>
              </view>
            </view>
            <input
              v-model="editForm.emailCode"
              class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
              placeholder="请输入邮箱验证码"
            />
          </view>
        </view>

        <!-- 手机号 -->
        <view class="mb-6">
          <text class="text-label-md text-on-surface-variant mb-1">手机</text>
          <input
            v-model="editForm.phone"
            type="number"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请输入手机号"
          />
        </view>

        <!-- 取消/保存 -->
        <view class="flex gap-3">
          <view
            class="flex-1 py-3 rounded-full bg-surface-container text-center"
            @click="cancelEdit"
          >
            <text class="text-label-md text-on-surface-variant">取消</text>
          </view>
          <view
            class="flex-1 py-3 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] text-center"
            @click="saveUserInfo"
          >
            <text class="text-label-md text-white">保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 安全设置 -->
    <view class="px-4 mb-6">
      <text class="text-label-caps text-on-surface-variant mb-3 block">安全设置</text>
      <view class="glass-panel rounded-2xl overflow-hidden">
        <view
          class="flex items-center px-4 py-3.5"
          @click="showPasswordModal = true"
        >
          <text class="material-symbols-outlined text-on-surface-variant mr-3">lock</text>
          <text class="text-body-md text-on-surface flex-1">修改密码</text>
          <text class="material-symbols-outlined text-on-surface-variant">chevron_right</text>
        </view>
      </view>
    </view>

    <!-- 账号信息 -->
    <view class="px-4 mb-6">
      <text class="text-label-caps text-on-surface-variant mb-3 block">账号信息</text>
      <view class="glass-panel rounded-2xl overflow-hidden">
        <view class="flex items-center px-4 py-3.5 border-b border-outline-variant/30">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">calendar_today</text>
          <text class="text-body-md text-on-surface flex-1">注册时间</text>
          <text class="text-body-md text-on-surface-variant">{{ userInfo.createTime || '-' }}</text>
        </view>
        <view class="flex items-center px-4 py-3.5">
          <text class="material-symbols-outlined text-on-surface-variant mr-3">check_circle</text>
          <text class="text-body-md text-on-surface flex-1">账号状态</text>
          <text class="text-body-md text-success-green">正常</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="px-4 mb-10">
      <view
        class="w-full py-3 rounded-full bg-error/10 text-center"
        @click="handleLogout"
      >
        <text class="text-label-md text-error">退出登录</text>
      </view>
    </view>

    <!-- 修改密码弹窗 -->
    <view
      v-if="showPasswordModal"
      class="fixed inset-0 z-50 flex items-end justify-center"
    >
      <view class="absolute inset-0 bg-black/40" @click="showPasswordModal = false"></view>
      <view class="relative w-full max-w-md bg-surface-bright rounded-t-3xl p-6 pb-8">
        <view class="w-10 h-1 bg-outline/30 rounded-full mx-auto mb-6"></view>
        <text class="text-headline-lg text-on-surface mb-6">修改密码</text>

        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">旧密码</text>
          <input
            v-model="passwordForm.oldPassword"
            password
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请输入旧密码"
          />
        </view>

        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">新密码</text>
          <input
            v-model="passwordForm.newPassword"
            password
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请输入新密码"
          />
        </view>

        <view class="mb-6">
          <text class="text-label-md text-on-surface-variant mb-1">确认新密码</text>
          <input
            v-model="passwordForm.confirmPassword"
            password
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="请再次输入新密码"
          />
        </view>

        <view class="flex gap-3">
          <view
            class="flex-1 py-3 rounded-full bg-surface-container text-center"
            @click="showPasswordModal = false"
          >
            <text class="text-label-md text-on-surface-variant">取消</text>
          </view>
          <view
            class="flex-1 py-3 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] text-center"
            @click="updatePassword"
          >
            <text class="text-label-md text-white">保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 滑块验证弹窗 -->
    <view
      v-if="showCaptchaModal"
      class="fixed inset-0 z-[60] flex items-center justify-center"
    >
      <view class="absolute inset-0 bg-black/40" @click="showCaptchaModal = false"></view>
      <view class="relative w-[85%] max-w-sm bg-surface-bright rounded-2xl p-6">
        <text class="text-headline-md text-on-surface mb-4">安全验证</text>
        <SliderCaptcha @success="onCaptchaSuccess" @fail="onCaptchaFail" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { profileApi } from '@/api/user/profile'
import { captchaApi } from '@/api/user/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const authStore = useAuthStore()
const isEditing = ref(false)
const showPasswordModal = ref(false)
const showCaptchaModal = ref(false)
const emailCodeCountdown = ref(0)
let countdownTimer = null

const userInfo = computed(() => authStore.userInfo || {})

const avatarLetter = computed(() => {
  const name = userInfo.value.nickname || '用'
  return name.charAt(0).toUpperCase()
})

const roleLabel = computed(() => {
  return userInfo.value.role === 1 ? '管理员' : '普通用户'
})

const editForm = reactive({
  nickname: '',
  email: '',
  phone: '',
  emailCode: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const isEmailChanged = computed(() => {
  return editForm.email !== userInfo.value.email
})

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.navigateTo({ url: '/pages/dashboard/dashboard' })
  }
}

function startEdit() {
  editForm.nickname = userInfo.value.nickname || ''
  editForm.email = userInfo.value.email || ''
  editForm.phone = userInfo.value.phone || ''
  editForm.emailCode = ''
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function saveUserInfo() {
  if (!editForm.nickname.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  if (isEmailChanged.value && !editForm.emailCode.trim()) {
    uni.showToast({ title: '邮箱已变更，请输入验证码', icon: 'none' })
    return
  }
  try {
    const userId = userInfo.value.id
    const data = {
      nickname: editForm.nickname,
      email: editForm.email,
      phone: editForm.phone,
    }
    const emailCode = isEmailChanged.value ? editForm.emailCode : undefined
    await profileApi.updateUserInfo(userId, data, emailCode)
    uni.showToast({ title: '保存成功', icon: 'success' })
    isEditing.value = false
    await authStore.getUserInfo()
  } catch (e) {
    console.error('保存用户信息失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

async function updatePassword() {
  if (!passwordForm.oldPassword) {
    uni.showToast({ title: '请输入旧密码', icon: 'none' })
    return
  }
  if (!passwordForm.newPassword) {
    uni.showToast({ title: '请输入新密码', icon: 'none' })
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  if (passwordForm.newPassword.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  try {
    const userId = userInfo.value.id
    await profileApi.updatePassword(userId, passwordForm.oldPassword, passwordForm.newPassword)
    showPasswordModal.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    uni.showToast({ title: '密码修改成功', icon: 'success' })
  } catch (e) {
    console.error('修改密码失败:', e)
    uni.showToast({ title: '修改失败', icon: 'none' })
  }
}

function sendEmailCode() {
  if (emailCodeCountdown.value > 0) return
  showCaptchaModal.value = true
}

async function onCaptchaSuccess(captchaData) {
  showCaptchaModal.value = false
  try {
    await captchaApi.sendEmailCode({
      email: editForm.email,
      captchaVerification: captchaData,
    })
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    startCountdown()
  } catch (e) {
    console.error('发送验证码失败:', e)
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

function onCaptchaFail() {
  uni.showToast({ title: '验证失败，请重试', icon: 'none' })
}

function startCountdown() {
  emailCodeCountdown.value = 60
  if (countdownTimer) clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    emailCodeCountdown.value--
    if (emailCodeCountdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

function handleLogout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        authStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

onMounted(() => {
  if (!authStore.userInfo) {
    authStore.getUserInfo()
  }
})
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
