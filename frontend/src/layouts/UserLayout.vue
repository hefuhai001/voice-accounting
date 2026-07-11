<template>
  <div
    class="h-screen flex flex-col overflow-hidden bg-background font-body-md text-on-surface antialiased mesh-bg"
  >
    <!-- 顶部导航栏 -->
    <header
      class="flex-shrink-0 bg-white/70 backdrop-blur-xl sticky top-0 z-30 border-b border-on-surface/5"
    >
      <div class="flex items-center justify-between px-margin-mobile h-16">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full overflow-hidden border border-black/5 ring-2 ring-primary/10">
            <img class="w-full h-full object-cover" src="../assets/logo.svg" alt="">
          </div>
          <h1 class="mt-4 font-headline-lg-mobile text-headline-lg-mobile font-bold tracking-tight bg-gradient-to-r from-[#983f19] to-[#ab3500] bg-clip-text text-transparent">
            FinanceFlow
          </h1>
        </div>
        <div class="flex items-center gap-4">
          <button
            @click="showUserMenu = !showUserMenu"
            class="w-11 h-11 rounded-full flex items-center justify-center text-white hover:opacity-90 active:scale-95 transition-all bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-md shadow-primary/20"
          >
            <div class="w-8 h-8 rounded-full bg-white/20 text-white flex items-center justify-center font-bold text-sm">
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
          class="absolute right-3 top-[72px] w-52 glass-card rounded-3xl shadow-lg shadow-black/10 overflow-hidden border border-black/5"
        >
          <div class="px-4 py-3 border-b border-on-surface/5">
            <p class="font-label-md text-on-surface">
              {{ authStore.userInfo?.nickname || '用户' }}
            </p>
            <p class="font-label-sm text-on-surface-variant mt-0.5">{{ authStore.userInfo?.email || '未设置邮箱' }}</p>
          </div>
          <nav class="py-1">
            <button
              @click="navigate('/profile')"
              class="w-full text-left px-4 py-2.5 font-body-md text-on-surface hover:bg-surface-container/30 active:bg-surface-container/50 transition-colors flex items-center gap-3"
            >
              <span class="material-symbols-outlined text-[20px]">person</span>
              个人中心
            </button>
            <button
              @click="navigate('/reminder')"
              class="w-full text-left px-4 py-2.5 font-body-md text-on-surface hover:bg-surface-container/30 active:bg-surface-container/50 transition-colors flex items-center gap-3"
            >
              <span class="material-symbols-outlined text-[20px]">alarm</span>
              我的提醒
            </button>
            <button
              @click="handleLogout"
              class="w-full text-left px-4 py-2.5 font-body-md text-danger-red hover:bg-error-container/30 active:bg-error-container/50 transition-colors flex items-center gap-3"
            >
              <span class="material-symbols-outlined text-[20px]">logout</span>
              退出登录
            </button>
          </nav>
        </div>
      </div>
    </Transition>

    <!-- 主内容区 -->
    <main class="flex-1 overflow-y-auto overscroll-contain pb-32">
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
          <div class="bg-surface-container rounded-2xl p-4 space-y-3 font-body-md text-on-surface">
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
            class="w-full mt-5 h-12 rounded-2xl bg-surface-container text-on-surface font-label-md active:bg-surface-container-high transition-colors"
          >
            知道了
          </button>
        </div>
      </div>
    </Transition>

    <!-- 底部导航栏 (悬浮胶囊式) -->
    <nav
      class="fixed bottom-8 left-0 right-0 z-50 flex justify-around items-center px-6 py-4 mx-auto max-w-md bg-white/80 backdrop-blur-2xl rounded-full border border-black/5 shadow-2xl shadow-black/10 w-[calc(100%-48px)]"
    >
      <!-- Tab 1: 首页 -->
      <button
        @click.stop="navigate('/dashboard')"
        class="flex flex-col items-center justify-center transition-colors relative z-10"
        :class="isActive('/dashboard') ? 'text-primary' : 'text-on-surface-variant hover:text-primary'"
      >
        <span
          class="material-symbols-outlined text-3xl"
          :style="isActive('/dashboard') ? 'font-variation-settings: \'FILL\' 1' : ''"
        >home</span>
        <span class="font-label-caps text-[10px] mt-1 font-bold">首页</span>
      </button>

      <!-- Tab 2: 账本 -->
      <button
        @click.stop="navigate('/books')"
        class="flex flex-col items-center justify-center transition-colors relative z-10"
        :class="isActive('/books') ? 'text-primary' : 'text-on-surface-variant hover:text-primary'"
      >
        <span
          class="material-symbols-outlined text-3xl"
          :style="isActive('/books') ? 'font-variation-settings: \'FILL\' 1' : ''"
        >account_balance_wallet</span>
        <span class="font-label-caps text-[10px] mt-1 font-bold">账本</span>
      </button>

      <!-- Tab 3: 语音记账 (巨型中央按钮) -->
      <button
        @click.stop="navigate('/transaction')"
        class="flex flex-col items-center justify-center scale-125 bg-primary rounded-full p-3 -translate-y-6 active:scale-110 transition-transform text-white relative shadow-lg shadow-primary/40 z-20"
      >
        <div class="absolute inset-0 rounded-full border-4 border-white/30 animate-ping opacity-20"></div>
        <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1;">mic</span>
      </button>

      <!-- Tab 4: 记录 -->
      <button
        @click.stop="navigate('/records')"
        class="flex flex-col items-center justify-center transition-colors relative z-10"
        :class="isActive('/records') ? 'text-primary' : 'text-on-surface-variant hover:text-primary'"
      >
        <span
          class="material-symbols-outlined text-3xl"
          :style="isActive('/records') ? 'font-variation-settings: \'FILL\' 1' : ''"
        >history</span>
        <span class="font-label-caps text-[10px] mt-1 font-bold">流水</span>
      </button>

      <!-- Tab 5: 分类 -->
      <button
        @click.stop="navigate('/category')"
        class="flex flex-col items-center justify-center transition-colors relative z-10"
        :class="isActive('/category') ? 'text-primary' : 'text-on-surface-variant hover:text-primary'"
      >
        <span
          class="material-symbols-outlined text-3xl"
          :style="isActive('/category') ? 'font-variation-settings: \'FILL\' 1' : ''"
        >grid_view</span>
        <span class="font-label-caps text-[10px] mt-1 font-bold">分类</span>
      </button>
    </nav>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
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
.mesh-bg {
  background-color: #f8f9fc;
  background-image:
    radial-gradient(at 0% 0%, hsla(280, 80%, 98%, 0.6) 0px, transparent 50%),
    radial-gradient(at 100% 0%, hsla(170, 80%, 98%, 0.6) 0px, transparent 50%),
    radial-gradient(at 100% 100%, hsla(20, 80%, 98%, 0.6) 0px, transparent 50%),
    radial-gradient(at 0% 100%, hsla(220, 80%, 98%, 0.6) 0px, transparent 50%);
  background-size: cover;
  background-attachment: fixed;
}

.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.02);
}

.sunset-gradient {
  background: linear-gradient(135deg, #ab3500 0%, #fe9824 100%);
}

.glow-primary {
  box-shadow: 0 12px 32px rgba(255, 107, 53, 0.35);
}

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
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

/* 隐藏滚动条 */
main::-webkit-scrollbar {
  display: none;
}
main {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
