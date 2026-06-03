<template>
  <div class="px-4 pt-4 pb-6 space-y-4">
    <!-- 问候语 + 日期 -->
    <section class="px-1">
      <p class="text-[22px] font-bold text-[#1c1c1e]">
        Hi，{{ authStore.userInfo?.nickname || '用户' }}
      </p>
      <p class="text-sm text-[#8e8e93] mt-0.5">{{ currentDate }}</p>
    </section>

    <!-- 收支概览卡片 -->
    <section class="bg-white rounded-2xl p-5 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-base font-semibold text-[#1c1c1e]">本月概览</h2>
        <span class="text-xs text-[#8e8e93] bg-[#f2f2f7] px-2.5 py-1 rounded-full">{{
          currentMonth
        }}</span>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <!-- 支出 -->
        <div class="bg-red-50 rounded-xl p-4 border border-red-100/60">
          <div class="flex items-center gap-1.5 mb-2">
            <div class="w-5 h-5 rounded-full bg-red-500/10 flex items-center justify-center">
              <svg
                class="w-3 h-3 text-red-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
              </svg>
            </div>
            <span class="text-xs text-red-600/80 font-medium">支出</span>
          </div>
          <p class="text-2xl font-bold text-red-600 tracking-tight">
            ¥{{ stats.monthExpense.toLocaleString() }}
          </p>
        </div>
        <!-- 收入 -->
        <div class="bg-emerald-50 rounded-xl p-4 border border-emerald-100/60">
          <div class="flex items-center gap-1.5 mb-2">
            <div class="w-5 h-5 rounded-full bg-emerald-500/10 flex items-center justify-center">
              <svg
                class="w-3 h-3 text-emerald-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
            </div>
            <span class="text-xs text-emerald-600/80 font-medium">收入</span>
          </div>
          <p class="text-2xl font-bold text-emerald-600 tracking-tight">
            ¥{{ stats.monthIncome.toLocaleString() }}
          </p>
        </div>
      </div>

      <!-- 次要统计 -->
      <div class="flex gap-3 mt-3">
        <div class="flex-1 flex items-center gap-2 bg-[#f9f9fb] rounded-lg px-3 py-2.5">
          <UnorderedListOutlined class="text-[#8e8e93] text-sm" />
          <div>
            <p class="text-[10px] text-[#8e8e93] leading-none">记账笔数</p>
            <p class="text-sm font-semibold text-[#1c1c1e] leading-tight mt-0.5">
              {{ stats.recordCount }} 笔
            </p>
          </div>
        </div>
        <div class="flex-1 flex items-center gap-2 bg-[#f9f9fb] rounded-lg px-3 py-2.5">
          <BellOutlined class="text-[#8e8e93] text-sm" />
          <div>
            <p class="text-[10px] text-[#8e8e93] leading-none">待处理</p>
            <p class="text-sm font-semibold text-[#ff9500] leading-tight mt-0.5">
              {{ stats.pendingReminders }} 个
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 快捷操作 -->
    <section class="grid grid-cols-4 gap-3">
      <button
        v-for="action in quickActions"
        :key="action.label"
        @click="$router.push(action.path)"
        class="flex flex-col items-center gap-2 py-4 bg-white rounded-2xl shadow-sm active:scale-95 transition-transform duration-150"
      >
        <div class="w-11 h-11 rounded-xl flex items-center justify-center" :class="action.bgClass">
          <component :is="action.icon" class="text-lg" :class="action.iconClass" />
        </div>
        <span class="text-[11px] font-medium text-[#1c1c1e]">{{ action.label }}</span>
      </button>
    </section>

    <!-- 最近记账记录 -->
    <section class="bg-white rounded-2xl shadow-sm overflow-hidden">
      <div class="flex items-center justify-between px-5 py-4 border-b border-black/5">
        <h2 class="text-base font-semibold text-[#1c1c1e]">最近记账</h2>
        <button
          @click="$router.push('/records')"
          class="text-xs text-[#ff6b35] font-medium active:opacity-60 transition-opacity"
        >
          查看全部 →
        </button>
      </div>
      <div class="divide-y divide-black/[0.05]">
        <div
          v-for="record in recentRecords"
          :key="record.id"
          class="flex items-center justify-between px-5 py-3.5 active:bg-black/[0.02] transition-colors"
        >
          <div class="flex items-center gap-3 min-w-0">
            <div
              class="w-10 h-10 rounded-xl flex items-center justify-center text-lg shrink-0"
              :class="record.type === 1 ? 'bg-orange-50' : 'bg-emerald-50'"
            >
              {{ getCategoryEmoji(record.category) }}
            </div>
            <div class="min-w-0">
              <p class="text-[15px] font-medium text-[#1c1c1e] truncate">{{ record.category }}</p>
              <p class="text-xs text-[#8e8e93] mt-0.5">{{ record.remark || record.date }}</p>
            </div>
          </div>
          <span
            class="text-[15px] font-bold shrink-0 ml-3"
            :class="record.type === 1 ? 'text-red-500' : 'text-emerald-500'"
          >
            {{ record.type === 1 ? '-' : '+' }}¥{{ record.amount }}
          </span>
        </div>
      </div>
      <div v-if="recentRecords.length === 0" class="py-12 text-center">
        <p class="text-sm text-[#8e8e93]">暂无记账记录</p>
        <button
          @click="$router.push('/transaction')"
          class="mt-3 text-sm text-[#ff6b35] font-medium"
        >
          去记一笔
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import {
  UnorderedListOutlined,
  BellOutlined,
  EditOutlined,
  BookOutlined,
  AppstoreOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

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
  monthExpense: 3560.5,
  monthIncome: 12000.0,
  recordCount: 28,
  pendingReminders: 3,
})

// 快捷操作
const quickActions = [
  {
    label: '记一笔',
    path: '/transaction',
    icon: EditOutlined,
    bgClass: 'bg-gradient-to-br from-[#ff6b35] to-[#f7931e]',
    iconClass: 'text-white',
  },
  {
    label: '账本',
    path: '/books',
    icon: BookOutlined,
    bgClass: 'bg-blue-50',
    iconClass: 'text-blue-500',
  },
  {
    label: '分类',
    path: '/category',
    icon: AppstoreOutlined,
    bgClass: 'bg-violet-50',
    iconClass: 'text-violet-500',
  },
  {
    label: '提醒',
    path: '/reminder',
    icon: BellOutlined,
    bgClass: 'bg-amber-50',
    iconClass: 'text-amber-500',
  },
]

// 最近记录
const recentRecords = ref([
  { id: 1, date: '2026-06-03', category: '餐饮', remark: '午餐', amount: '35.00', type: 1 },
  { id: 2, date: '2026-06-03', category: '交通', remark: '地铁', amount: '5.00', type: 1 },
  { id: 3, date: '2026-06-02', category: '工资', remark: '月薪', amount: '12,000.00', type: 2 },
  { id: 4, date: '2026-06-02', category: '购物', remark: '日用品', amount: '128.50', type: 1 },
  { id: 5, date: '2026-06-01', category: '娱乐', remark: '电影票', amount: '60.00', type: 1 },
])

// 分类 emoji 映射
function getCategoryEmoji(category) {
  const map = { 餐饮: '', 交通: '', 工资: '', 购物: '', 娱乐: '' }
  return map[category] || ''
}
</script>
