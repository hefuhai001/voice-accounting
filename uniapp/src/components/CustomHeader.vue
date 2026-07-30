<template>
  <view class="flex-shrink-0 bg-white/70 border-b border-on-surface/5" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="flex items-center justify-between px-4 h-14">
      <!-- 左侧：logo + 标题 -->
      <view class="flex items-center">
        <view class="w-9 h-9 rounded-full overflow-hidden border border-black/5 flex items-center justify-center bg-gradient-to-br from-[#983f19] to-[#ab3500]">
          <text class="text-white text-lg font-bold">F</text>
        </view>
        <text class="ml-2 text-xl font-bold tracking-tight text-transparent bg-gradient-to-r from-[#983f19] to-[#ab3500] bg-clip-text">FinanceFlow</text>
      </view>

      <!-- 右侧：用户头像按钮 -->
      <view @click="showUserMenu = !showUserMenu" class="w-10 h-10 rounded-full flex items-center justify-center active:scale-95 transition-transform bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-md relative z-50">
        <view class="w-7 h-7 rounded-full bg-white/20 flex items-center justify-center">
          <text class="text-white font-bold text-sm">{{ nickname.charAt(0) }}</text>
        </view>
      </view>
    </view>

    <!-- 用户菜单下拉 -->
    <view v-if="showUserMenu" class="fixed inset-0 z-40" @click="showUserMenu = false">
      <view class="absolute inset-0 bg-black/20"></view>
      <view class="absolute right-3 glass-card rounded-3xl shadow-lg overflow-hidden border border-black/5" :style="{ top: (statusBarHeight + 56 + 4) + 'px', width: '210px' }" @click.stop>
        <!-- 用户信息 -->
        <view class="px-4 py-3 border-b border-on-surface/5">
          <text class="text-sm text-on-surface font-medium block">{{ nickname }}</text>
          <text class="text-xs text-on-surface-variant mt-0.5 block">{{ email }}</text>
        </view>
        <!-- 菜单项 -->
        <view class="py-1">
          <view @click="navigateTo('/pages/profile/profile')" class="w-full flex items-center px-4 py-2.5 active:bg-surface-container/50">
            <text class="material-symbols-outlined text-[20px] text-on-surface mr-3">person</text>
            <text class="text-sm text-on-surface">个人中心</text>
          </view>
          <view @click="navigateTo('/pages/reminder/reminder')" class="w-full flex items-center px-4 py-2.5 active:bg-surface-container/50">
            <text class="material-symbols-outlined text-[20px] text-on-surface mr-3">alarm</text>
            <text class="text-sm text-on-surface">我的提醒</text>
          </view>
          <view @click="handleLogout" class="w-full flex items-center px-4 py-2.5 active:bg-surface-container/50">
            <text class="material-symbols-outlined text-[20px] text-danger-red mr-3">logout</text>
            <text class="text-sm text-danger-red">退出登录</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const showUserMenu = ref(false)

const statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 0
const nickname = computed(() => authStore.userInfo?.nickname || '用户')
const email = computed(() => authStore.userInfo?.email || '未设置邮箱')

function navigateTo(url) {
  showUserMenu.value = false
  uni.navigateTo({ url })
}

function handleLogout() {
  showUserMenu.value = false
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        authStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  -webkit-font-smoothing: antialiased;
}
.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.02);
}
.bg-clip-text {
  -webkit-background-clip: text;
  background-clip: text;
}
.text-transparent {
  color: transparent;
}
</style>
