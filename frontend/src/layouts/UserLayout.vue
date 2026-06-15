<template>
  <div
    class="ios-app h-screen flex flex-col overflow-hidden bg-surface font-body-md text-on-surface antialiased"
  >
    <!-- 顶部导航栏 -->
    <header
      class="flex-shrink-0 bg-surface/80 backdrop-blur-md sticky top-0 z-30"
    >
      <div class="flex items-center justify-between px-margin-mobile h-16">
        <div class="flex items-center gap-2">
          <img class="w-9 h-9 rounded-xl" src="../assets/logo.svg" alt="">
          <h1 class="font-headline-md text-headline-md bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent whitespace-nowrap mt-2">Voice Accounting</h1>
        </div>
        <div class="flex items-center gap-4">
          <button @click="showUserMenu = !showUserMenu" class="relative">
            <div
              class="w-8 h-8 rounded-full bg-primary-container text-white flex items-center justify-center font-bold text-sm"
            >
              {{ authStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </div>
          </button>
        </div>
      </div>
    </header>

    <!-- 用户菜单下拉 -->
    <Transition name="fade">
      <div v-if="showUserMenu" class="fixed inset-0 z-40" @click="showUserMenu = false">
        <div
          class="absolute right-3 top-[72px] w-52 glass-card rounded-2xl shadow-lg shadow-black/10 overflow-hidden border border-white/40"
        >
          <div class="px-4 py-3 border-b border-surface-variant/50">
            <p class="font-label-md text-on-surface">
              {{ authStore.userInfo?.nickname || '用户' }}
            </p>
            <p class="font-label-sm text-on-surface-variant mt-0.5">{{ authStore.userInfo?.email || '未设置邮箱' }}</p>
          </div>
          <nav class="py-1">
            <button
              @click="navigate('/reminder')"
              class="w-full text-left px-4 py-2.5 font-body-md text-on-surface hover:bg-surface-variant/30 active:bg-surface-variant/50 transition-colors flex items-center gap-3"
            >
              <BellOutlined class="text-on-surface-variant" />
              我的提醒
            </button>
            <button
              @click="handleLogout"
              class="w-full text-left px-4 py-2.5 font-body-md text-danger-red hover:bg-error-container/30 active:bg-error-container/50 transition-colors flex items-center gap-3"
            >
              <span
                class="inline-block w-4 h-4 rounded-full bg-danger-red/10 flex items-center justify-center text-xs"
                >←</span
              >
              退出登录
            </button>
          </nav>
        </div>
      </div>
    </Transition>

    <!-- 主内容区 -->
    <main class="flex-1 overflow-y-auto overscroll-contain pb-24">
      <router-view v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" :key="route.path" />
        </Transition>
      </router-view>
    </main>

    <!-- PWA 安装引导弹窗（iOS） -->
    <Transition name="fade">
      <div v-if="showInstallModal" class="fixed inset-0 z-[60] flex items-end justify-center" @click.self="dismissInstall">
        <div class="absolute inset-0 bg-black/40" @click="dismissInstall" />
        <div
          class="relative w-full max-w-md glass-card rounded-t-3xl p-6 pb-10 shadow-xl"
          @click.stop
        >
          <div class="w-10 h-1 rounded-full bg-on-surface-variant/20 mx-auto mb-5" />
          <h3 class="font-headline-md text-headline-md text-on-surface text-center mb-2">添加到主屏幕</h3>
          <p class="font-body-md text-on-surface-variant text-center mb-5">将应用添加到主屏幕，获得全屏体验</p>
          <div class="bg-surface-gray rounded-2xl p-4 space-y-3 font-body-md text-on-surface">
            <div class="flex items-start gap-3">
              <span class="shrink-0 w-6 h-6 rounded-full sunset-gradient text-white flex items-center justify-center text-xs font-bold">1</span>
              <span>点击浏览器底部的 <strong>分享按钮</strong></span>
            </div>
            <div class="flex items-start gap-3">
              <span class="shrink-0 w-6 h-6 rounded-full sunset-gradient text-white flex items-center justify-center text-xs font-bold">2</span>
              <span>在弹出的菜单中选择 <strong>"添加到主屏幕"</strong></span>
            </div>
            <div class="flex items-start gap-3">
              <span class="shrink-0 w-6 h-6 rounded-full sunset-gradient text-white flex items-center justify-center text-xs font-bold">3</span>
              <span>点击 <strong>"添加"</strong> 即可</span>
            </div>
          </div>
          <button
            @click="dismissInstall"
            class="w-full mt-5 h-12 rounded-2xl bg-surface-gray text-on-surface font-label-md active:bg-surface-container-high transition-colors"
          >
            知道了
          </button>
        </div>
      </div>
    </Transition>

    <!-- 底部导航栏 -->
    <nav
      class="fixed bottom-0 left-0 right-0 z-50 bg-white/70 backdrop-blur-xl border-t border-outline-variant rounded-t-3xl shadow-lg safe-area-bottom"
    >
      <div
        class="flex justify-around items-end px-4 py-2 pb-safe h-20"
      >
        <!-- 左侧标签 -->
        <button
          v-for="tab in leftTabs"
          :key="tab.path"
          @click="navigate(tab.path)"
          class="flex flex-col items-center justify-center pb-2 transition-all duration-200"
          :class="isActive(tab.path) ? 'text-primary' : 'text-on-surface-variant'"
        >
          <component :is="tab.icon" class="text-[22px]" />
          <span class="font-label-sm text-label-sm mt-1">{{ tab.label }}</span>
        </button>

        <!-- 中央记账按钮 -->
        <div class="relative -top-6 flex flex-col items-center">
          <button
            @click="navigate('/transaction')"
            class="w-16 h-16 sunset-gradient rounded-full flex items-center justify-center shadow-lg shadow-primary/40 active:scale-95 transition-transform duration-150 ring-4 ring-white"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="w-7 h-7 text-white"
            >
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </button>
          <span class="font-label-sm text-label-sm mt-1 text-primary-container font-bold">记一笔</span>
        </div>

        <!-- 右侧标签 -->
        <button
          v-for="tab in rightTabs"
          :key="tab.path"
          @click="navigate(tab.path)"
          class="flex flex-col items-center justify-center pb-2 transition-all duration-200"
          :class="isActive(tab.path) ? 'text-primary' : 'text-on-surface-variant'"
        >
          <component :is="tab.icon" class="text-[22px]" />
          <span class="font-label-sm text-label-sm mt-1">{{ tab.label }}</span>
        </button>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeOutlined,
  BookOutlined,
  UnorderedListOutlined,
  AppstoreOutlined,
  BellOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { usePwaInstall } from '@/composables/usePwaInstall'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const showUserMenu = ref(false)

// PWA 安装提示
const { showInstallModal, promptInstall, dismissInstall, shouldShowPrompt } = usePwaInstall()

onMounted(() => {
  // 注册后首次进入，弹出安装提示
  if (sessionStorage.getItem('just_registered') && shouldShowPrompt()) {
    sessionStorage.removeItem('just_registered')
    setTimeout(() => {
      promptInstall()
    }, 800)
  }
})

// 底部标签
const allTabs = [
  { path: '/dashboard', label: '首页', icon: HomeOutlined },
  { path: '/books', label: '账本', icon: BookOutlined },
  { path: '/records', label: '记录', icon: UnorderedListOutlined },
  { path: '/category', label: '分类', icon: AppstoreOutlined },
]
const leftTabs = allTabs.slice(0, 2)
const rightTabs = allTabs.slice(2)

// 页面标题
// const pageTitle = computed(() => {
//   const map = {
//     '/dashboard': '语音记账',
//     '/books': '我的账本',
//     '/transaction': '记一笔',
//     '/records': '记账记录',
//     '/category': '分类管理',
//     '/reminder': '我的提醒',
//   }
//   return map[route.path] || '语音记账'
// })

// 判断 Tab 激活
function isActive(path) {
  if (path === '/dashboard') return route.path === '/dashboard' || route.path === '/'
  return route.path.startsWith(path)
}

// Tab 路径集合
const tabPaths = new Set(['/dashboard', '/books', '/records', '/category'])

// 统一导航：Tab 页用 replace（不产生历史记录），其他页用 push
function navigate(path) {
  if (route.path === path) return
  if (tabPaths.has(path)) {
    router.replace(path)
  } else {
    router.push(path)
  }
  showUserMenu.value = false
}

// 退出登录
function handleLogout() {
  authStore.logout()
  router.push('/login')
  showUserMenu.value = false
}

// 路由变化关闭菜单
watch(() => route.path, () => {
  showUserMenu.value = false
})
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.sunset-gradient {
  background: linear-gradient(135deg, #ab3500 0%, #fe9824 100%);
}

/* 页面切换动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-enter-from {
  opacity: 0;
  transform: translateX(16px);
}
.page-leave-to {
  opacity: 0;
  transform: translateX(-16px);
}

/* 菜单淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 安全区域适配 */
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 0px);
}

/* 隐藏滚动条 */
main::-webkit-scrollbar {
  display: none;
}
main {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
