<template>
  <div
    class="ios-app h-screen flex flex-col overflow-hidden bg-[#F2F2F7] font-[system-ui,_-apple-system,_sans-serif]"
  >
    <!-- 顶部导航栏 -->
    <header
      class="flex-shrink-0 bg-white/80 backdrop-blur-xl sticky top-0 z-30 border-b border-black/5"
    >
      <div class="flex items-center justify-between px-5 h-12">
        <!-- 左侧：页面标题 -->
        <h1 class="text-[17px] font-semibold text-[#1c1c1e] tracking-tight">{{ pageTitle }}</h1>

        <!-- 右侧：用户头像 -->
        <button @click="showUserMenu = !showUserMenu" class="relative">
          <div
            class="w-8 h-8 rounded-full bg-gradient-to-br from-orange-400 to-pink-500 flex items-center justify-center text-white text-sm font-semibold shadow-sm"
          >
            {{ authStore.userInfo?.nickname?.charAt(0) || 'U' }}
          </div>
        </button>
      </div>
    </header>

    <!-- 用户菜单下拉 -->
    <Transition name="fade">
      <div v-if="showUserMenu" class="fixed inset-0 z-40" @click="showUserMenu = false">
        <div
          class="absolute right-3 top-14 w-52 bg-white/95 backdrop-blur-xl rounded-2xl shadow-lg shadow-black/10 overflow-hidden border border-black/5"
        >
          <div class="px-4 py-3 border-b border-black/5">
            <p class="text-sm font-semibold text-[#1c1c1e]">
              {{ authStore.userInfo?.nickname || '用户' }}
            </p>
            <p class="text-xs text-[#8e8e93] mt-0.5">{{ authStore.userInfo?.email || '未设置邮箱' }}</p>
          </div>
          <nav class="py-1">
            <button
              @click="goToReminder"
              class="w-full text-left px-4 py-2.5 text-[15px] text-[#1c1c1e] hover:bg-black/5 active:bg-black/10 transition-colors flex items-center gap-3"
            >
              <BellOutlined class="text-[#8e8e93]" />
              我的提醒
              <span class="ml-auto text-xs text-[#8e8e93]">无待处理</span>
            </button>
            <button
              @click="handleLogoutAndClose"
              class="w-full text-left px-4 py-2.5 text-[15px] text-[#ff3b30] hover:bg-red-50 active:bg-red-100 transition-colors flex items-center gap-3"
            >
              <span
                class="inline-block w-4 h-4 rounded-full bg-[#ff3b30]/10 flex items-center justify-center text-xs"
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

    <!-- 底部标签栏 -->
    <nav
      class="fixed bottom-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-xl border-t border-black/5 safe-area-bottom"
    >
      <div
        class="flex items-center justify-between h-[calc(56px+env(safe-area-inset-bottom))] pb-[env(safe-area-inset-bottom)] px-3"
      >
        <!-- 左侧：前两个标签 -->
        <div class="flex items-center gap-1">
          <button
            v-for="tab in leftTabs"
            :key="tab.path"
            @click="navigateTo(tab.path)"
            class="flex flex-col items-center justify-center gap-0.5 w-16 py-1 transition-all duration-200 relative"
            :class="isActive(tab.path) ? 'text-[#ff6b35]' : 'text-[#8e8e93] active:scale-90'"
          >
            <div
              v-if="isActive(tab.path)"
              class="absolute -top-0 w-8 h-0.5 rounded-full bg-[#ff6b35]"
            />
            <component :is="tab.icon" class="text-[22px]" />
            <span class="text-[10px] font-medium leading-none mt-0.5">{{ tab.label }}</span>
          </button>
        </div>

        <!-- 中央记账按钮 -->
        <button
          @click="goToTransaction"
          class="relative -mt-5 flex flex-col items-center group shrink-0"
        >
          <div
            class="w-12 h-12 rounded-full bg-gradient-to-br from-[#ff6b35] to-[#f7931e] shadow-lg shadow-orange-500/30 flex items-center justify-center transition-transform duration-200 group-active:scale-90 group-hover:shadow-xl group-hover:shadow-orange-500/40"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="w-6 h-6 text-white"
            >
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </div>
          <span class="text-[10px] font-medium text-[#ff6b35] mt-1">记一笔</span>
        </button>

        <!-- 右侧：后两个标签 -->
        <div class="flex items-center gap-1">
          <button
            v-for="tab in rightTabs"
            :key="tab.path"
            @click="navigateTo(tab.path)"
            class="flex flex-col items-center justify-center gap-0.5 w-16 py-1 transition-all duration-200 relative"
            :class="isActive(tab.path) ? 'text-[#ff6b35]' : 'text-[#8e8e93] active:scale-90'"
          >
            <div
              v-if="isActive(tab.path)"
              class="absolute -top-0 w-8 h-0.5 rounded-full bg-[#ff6b35]"
            />
            <component :is="tab.icon" class="text-[22px]" />
            <span class="text-[10px] font-medium leading-none mt-0.5">{{ tab.label }}</span>
          </button>
        </div>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeOutlined,
  BookOutlined,
  UnorderedListOutlined,
  AppstoreOutlined,
  BellOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const showUserMenu = ref(false)

// 底部标签配置（左2 + 右2）
const allTabs = [
  { path: '/dashboard', label: '首页', icon: HomeOutlined },
  { path: '/books', label: '账本', icon: BookOutlined },
  // 中央是"记一笔"FAB按钮
  { path: '/records', label: '记录', icon: UnorderedListOutlined },
  { path: '/category', label: '分类', icon: AppstoreOutlined },
]

const leftTabs = allTabs.slice(0, 2)
const rightTabs = allTabs.slice(2)

// 当前页面标题
const pageTitle = computed(() => {
  const titleMap = {
    '/dashboard': '语音记账',
    '/books': '我的账本',
    '/transaction': '记一笔',
    '/records': '记账记录',
    '/category': '分类管理',
    '/reminder': '我的提醒',
  }
  return titleMap[route.path] || '语音记账'
})

// 判断是否为当前激活 tab
function isActive(path) {
  if (path === '/dashboard') return route.path === '/dashboard' || route.path === '/'
  return route.path.startsWith(path)
}

// 导航（避免重复导航）
function navigateTo(path) {
  if (route.path !== path) {
    router.push(path)
  }
}

// 监听路由变化关闭菜单
watch(
  () => route.path,
  () => {
    showUserMenu.value = false
  },
)

// 跳转到记账页面
function goToTransaction() {
  router.push('/transaction')
}

// 跳转到提醒并关闭菜单
function goToReminder() {
  router.push('/reminder')
  showUserMenu.value = false
}

// 退出登录并关闭菜单
function handleLogoutAndClose() {
  authStore.logout()
  router.push('/login')
  showUserMenu.value = false
}
</script>

<style scoped>
/* iOS 页面切换动画 */
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

/* 隐藏滚动条但保持滚动 */
main::-webkit-scrollbar {
  display: none;
}
main {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
