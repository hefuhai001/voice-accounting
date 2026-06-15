<template>
  <div class="px-margin-mobile pt-stack-lg pb-8 flex flex-col gap-stack-lg">
    <!-- Greeting Section -->
    <section class="mt-4">
      <h1 class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface">
        Hi，{{ authStore.userInfo?.nickname || '用户' }}
      </h1>
      <p class="font-body-md text-on-surface-variant opacity-70">{{ currentDate }}</p>
    </section>

    <!-- Monthly Overview (Bento Style) -->
    <section class="bg-white rounded-3xl p-6 shadow-[0_4px_20px_rgba(0,0,0,0.04)] flex flex-col gap-6">
      <div class="flex justify-between items-center">
        <h2 class="font-headline-md text-headline-md">本月概览</h2>
        <span class="bg-surface-gray px-3 py-1 rounded-full font-label-sm text-on-surface-variant">{{ currentMonth }}</span>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <!-- Expenditure -->
        <div class="bg-[#FFF5F2] p-5 rounded-2xl flex flex-col gap-2">
          <div class="flex items-center gap-2 text-primary">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="9" />
              <path stroke-linecap="round" d="M8 12h8" />
            </svg>
            <span class="font-label-md">支出</span>
          </div>
          <div class="font-headline-md text-headline-md text-primary">
            ¥{{ stats.monthExpense.toLocaleString() }}
          </div>
        </div>
        <!-- Income -->
        <div class="bg-[#F2FAF5] p-5 rounded-2xl flex flex-col gap-2">
          <div class="flex items-center gap-2 text-success-green">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="9" />
              <path stroke-linecap="round" d="M12 8v8m-4-4h8" />
            </svg>
            <span class="font-label-md">收入</span>
          </div>
          <div class="font-headline-md text-headline-md text-success-green">
            ¥{{ stats.monthIncome.toLocaleString() }}
          </div>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div class="bg-surface-gray/50 p-4 rounded-2xl flex items-center gap-3">
          <UnorderedListOutlined class="text-on-surface-variant" />
          <div>
            <p class="text-[10px] uppercase text-on-surface-variant tracking-wider">记账笔数</p>
            <p class="font-headline-md text-[18px]">{{ stats.recordCount }} 笔</p>
          </div>
        </div>
        <div class="bg-surface-gray/50 p-4 rounded-2xl flex items-center gap-3">
          <BellOutlined class="text-secondary" />
          <div>
            <p class="text-[10px] uppercase text-on-surface-variant tracking-wider">待处理</p>
            <p class="font-headline-md text-[18px] text-secondary">{{ stats.pendingReminders }} 个</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Quick Actions Grid -->
    <section class="grid grid-cols-4 gap-4">
      <button
        v-for="action in quickActions"
        :key="action.label"
        @click="navigateTo(action.path)"
        class="flex flex-col items-center gap-2"
      >
        <div class="w-16 h-16 rounded-2xl flex items-center justify-center" :class="action.bgClass">
          <component :is="action.icon" class="text-[32px]" :class="action.iconClass" />
        </div>
        <span class="font-label-sm text-on-surface">{{ action.label }}</span>
      </button>
    </section>

    <!-- Recent Records -->
    <section class="flex flex-col gap-4">
      <div class="flex justify-between items-center">
        <h2 class="font-headline-md text-headline-md">最近记账</h2>
        <button
          @click="navigateTo('/records')"
          class="text-primary font-label-md flex items-center gap-1"
        >
          查看全部
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
      <div class="flex flex-col gap-3">
        <div
          v-for="record in recentRecords"
          :key="record.id"
          class="bg-white p-4 rounded-2xl flex items-center justify-between shadow-[0_4px_20px_rgba(0,0,0,0.04)]"
        >
          <div class="flex items-center gap-4">
            <div
              class="w-12 h-12 rounded-xl flex items-center justify-center text-lg"
              :class="record.type === 1 ? 'bg-[#FFF5F2]' : 'bg-[#F0F4FF]'"
            >
              {{ getCategoryEmoji(record.categoryIcon, record.categoryName) }}
            </div>
            <div>
              <p class="font-headline-md text-[18px]">{{ record.categoryName }}</p>
              <p class="font-body-md text-on-surface-variant text-sm">{{ record.remark || record.transactionDate }}</p>
            </div>
          </div>
          <div class="text-right">
            <p class="font-headline-md text-[18px]" :class="record.type === 1 ? 'text-danger-red' : 'text-success-green'">
              {{ record.type === 1 ? '-' : '+' }}¥{{ Number(record.amount).toFixed(2) }}
            </p>
          </div>
        </div>
      </div>
      <div v-if="recentRecords.length === 0" class="py-12 text-center">
        <p class="font-body-md text-on-surface-variant">暂无记账记录</p>
        <button
          @click="router.push('/transaction')"
          class="mt-3 font-label-md text-primary"
        >
          去记一笔
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  UnorderedListOutlined,
  BellOutlined,
  EditOutlined,
  BookOutlined,
  AppstoreOutlined,
} from '@ant-design/icons-vue'
import { dashboardApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)
const router = useRouter()

// Tab 页用 replace 不产生历史记录，其他页用 push
const tabPaths = new Set(['/dashboard', '/books', '/records', '/category'])
function navigateTo(path) {
  if (tabPaths.has(path)) {
    router.replace(path)
  } else {
    router.push(path)
  }
}

// 当前日期
const currentDate = computed(() => {
  const d = new Date()
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const currentMonth = computed(() => {
  const d = new Date()
  return `${d.getMonth() + 1}月`
})

// 统计数据
const stats = reactive({
  monthExpense: 0,
  monthIncome: 0,
  recordCount: 0,
  pendingReminders: 0,
})

// 最近记录
const recentRecords = ref([])

// 快捷操作
const quickActions = [
  {
    label: '记一笔',
    path: '/transaction',
    icon: EditOutlined,
    bgClass: 'bg-[#FFEFEA]',
    iconClass: 'text-primary-container',
  },
  {
    label: '账本',
    path: '/books',
    icon: BookOutlined,
    bgClass: 'bg-[#EEF2FF]',
    iconClass: 'text-blue-500',
  },
  {
    label: '分类',
    path: '/category',
    icon: AppstoreOutlined,
    bgClass: 'bg-[#F5F3FF]',
    iconClass: 'text-purple-500',
  },
  {
    label: '提醒',
    path: '/reminder',
    icon: BellOutlined,
    bgClass: 'bg-[#FFFBEB]',
    iconClass: 'text-amber-500',
  },
]

// 图标显示：emoji直接显示，英文单词只取首字母大写
function getIconDisplay(icon) {
  if (!icon) return ''
  if (/[\u0080-\uffff]/.test(icon)) return icon
  return icon.charAt(0).toUpperCase()
}

// 分类 emoji 映射
function getCategoryEmoji(icon, name) {
  if (icon) return getIconDisplay(icon)
  const map = { 餐饮: '🍜', 交通: '🚗', 工资: '💰', 购物: '🛒', 娱乐: '🎮', 住房: '🏠', 医疗: '💊', 教育: '📚' }
  return map[name] || '📝'
}

// 加载数据
async function loadData() {
  if (!userId.value) return
  try {
    const [statsRes, recentRes] = await Promise.all([
      dashboardApi.getStats(userId.value),
      dashboardApi.getRecent(userId.value, 5),
    ])
    Object.assign(stats, {
      monthExpense: Number(statsRes.data.monthExpense) || 0,
      monthIncome: Number(statsRes.data.monthIncome) || 0,
      recordCount: statsRes.data.recordCount || 0,
      pendingReminders: statsRes.data.pendingReminders || 0,
    })
    recentRecords.value = recentRes.data || []
  } catch (error) {
    console.error('加载首页数据失败:', error)
  }
}

// 监听 userId 变化
watch(userId, (newUserId) => {
  if (newUserId) loadData()
})

// 页面加载时检查登录状态
onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadData()
})
</script>
