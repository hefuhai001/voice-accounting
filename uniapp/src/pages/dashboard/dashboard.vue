<template>
  <view class="min-h-screen bg-background pb-28">
    <CustomHeader />
    <!-- 问候区域 -->
    <view class="flex px-4 mb-4 flex-col gap-1 mb-2">
      <text class="text-label-caps text-on-surface-variant uppercase tracking-wider text-[10px]">欢迎回来</text>
      <text class="text-headline-lg text-on-surface text-2xl font-bold mt-1">你好，{{ nickname }}</text>
    </view>

    <!-- Bento 网格财务概览 -->
    <view class="px-4">
      <view class="grid grid-cols-2 gap-4">
        <!-- 本月支出 - 主卡片 -->
        <view class="col-span-2 glass-card rounded-3xl p-6 relative overflow-hidden">
          <view class="absolute -top-10 -right-10 w-40 h-40 bg-primary/10 rounded-full" style="filter: blur(40px);"></view>
          <view class="flex items-center justify-between relative z-10">
            <view class="flex flex-col">
              <text class="text-label-caps text-on-surface-variant uppercase font-semibold text-[10px]">本月支出</text>
              <text class="text-display-lg text-primary text-3xl font-bold mt-2">¥{{ formatAmount(stats.monthExpense) }}</text>
            </view>
            <view class="bg-secondary-container/10 text-secondary border border-secondary/30 px-3 py-1 rounded-full flex items-center">
              <text class="material-symbols-outlined text-[14px] font-bold">trending_down</text>
            </view>
          </view>
        </view>

        <!-- 收入 -->
        <view class="glass-panel rounded-3xl p-6 flex flex-col justify-center relative overflow-hidden border border-on-surface/5">
          <view class="absolute -bottom-5 -left-5 w-24 h-24 bg-secondary/10 rounded-full" style="filter: blur(20px);"></view>
          <view class="flex items-center mb-1 relative z-10 text-on-surface-variant">
            <view class="w-8 h-8 rounded-full bg-secondary/10 flex items-center justify-center">
              <text class="material-symbols-outlined text-secondary text-sm font-bold">arrow_downward</text>
            </view>
            <text class="text-label-caps uppercase font-semibold text-[10px] ml-2">收入</text>
          </view>
          <text class="text-headline-lg-mobile text-on-surface text-xl font-bold relative z-10">¥{{ formatCompact(stats.monthIncome) }}</text>
        </view>

        <!-- 余额 -->
        <view class="glass-panel rounded-3xl p-6 flex flex-col justify-center relative overflow-hidden border border-on-surface/5">
          <view class="absolute -bottom-5 -right-5 w-24 h-24 bg-tertiary/10 rounded-full" style="filter: blur(20px);"></view>
          <view class="flex items-center mb-1 relative z-10 text-on-surface-variant">
            <view class="w-8 h-8 rounded-full bg-tertiary/10 flex items-center justify-center">
              <text class="material-symbols-outlined text-tertiary text-sm font-bold">account_balance_wallet</text>
            </view>
            <text class="text-label-caps uppercase font-semibold text-[10px] ml-2">余额</text>
          </view>
          <text class="text-headline-lg-mobile text-on-surface text-xl font-bold relative z-10">¥{{ formatCompact(balance) }}</text>
        </view>

        <!-- 记录数 -->
        <view class="glass-panel rounded-3xl p-5 flex flex-col justify-center relative overflow-hidden border border-on-surface/5">
          <view class="flex items-center mb-1 relative z-10 text-on-surface-variant">
            <view class="w-7 h-7 rounded-full bg-outline/10 flex items-center justify-center">
              <text class="material-symbols-outlined text-outline text-sm font-bold">receipt_long</text>
            </view>
            <text class="text-label-caps uppercase font-semibold text-[10px] ml-2">记账</text>
          </view>
          <text class="text-headline-lg-mobile text-on-surface text-xl font-bold relative z-10">{{ stats.recordCount }} 笔</text>
        </view>

        <!-- 待提醒数 -->
        <view class="glass-panel rounded-3xl p-5 flex flex-col justify-center relative overflow-hidden border border-on-surface/5">
          <view class="flex items-center mb-1 relative z-10 text-on-surface-variant">
            <view class="w-7 h-7 rounded-full bg-secondary/10 flex items-center justify-center">
              <text class="material-symbols-outlined text-secondary text-sm font-bold">alarm</text>
            </view>
            <text class="text-label-caps uppercase font-semibold text-[10px] ml-2">提醒</text>
          </view>
          <text class="text-headline-lg-mobile text-on-surface text-xl font-bold relative z-10">{{ stats.pendingReminders }} 个</text>
        </view>
      </view>
    </view>

    <!-- 最近记录 -->
    <view class="px-4 mt-6">
      <view class="flex items-center justify-between mb-4">
        <text class="text-body-md text-on-surface text-[15px] font-bold">最近语音记录</text>
        <text class="text-label-caps text-primary text-[10px] font-bold px-3 py-1 rounded-full" @click="goToRecords">查看全部</text>
      </view>

      <!-- 记录列表 -->
      <view v-if="recentList.length > 0" class="space-y-3">
        <view
          v-for="item in recentList"
          :key="item.id"
          class="glass-panel rounded-3xl p-4 flex items-center justify-between border border-on-surface/5"
        >
          <view class="flex items-center">
            <view class="w-14 h-14 rounded-2xl bg-surface-container flex items-center justify-center border border-on-surface/5">
              <text class="material-symbols-outlined text-tertiary text-3xl" style="font-variation-settings: 'FILL' 1;">{{ getCategoryIcon(item.categoryName) }}</text>
            </view>
            <view class="ml-4 flex flex-col">
              <text class="text-body-md text-on-surface text-[15px] font-bold">{{ item.categoryName || '未分类' }}</text>
              <text class="text-label-caps text-on-surface-variant text-[10px]">{{ item.remark || item.transactionDate }}</text>
            </view>
          </view>
          <text class="text-body-md text-[15px] font-bold" :class="item.type === 1 ? 'text-on-surface' : 'text-secondary text-lg'">
            {{ item.type === 1 ? '-' : '+' }}¥{{ Number(item.amount).toFixed(2) }}
          </text>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else class="py-12 flex flex-col items-center justify-center">
        <text class="text-body-md text-on-surface-variant text-[15px]">暂无记账记录</text>
        <text class="mt-3 text-label-md text-primary" @click="goToTransaction">去记一笔</text>
      </view>
    </view>

    <CustomTabBar current="/pages/dashboard/dashboard" />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { dashboardApi } from '@/api/user/dashboard'
import { useAuthStore } from '@/stores/auth'
import CustomTabBar from '@/components/CustomTabBar.vue'
import CustomHeader from '@/components/CustomHeader.vue'

const authStore = useAuthStore()

const stats = reactive({
  monthExpense: 0,
  monthIncome: 0,
  recordCount: 0,
  pendingReminders: 0,
})

const recentList = ref([])

const nickname = computed(() => authStore.userInfo?.nickname || '用户')

const balance = computed(() => stats.monthIncome - stats.monthExpense)

function formatAmount(num) {
  if (num == null) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatCompact(num) {
  if (num == null) return '0'
  const n = Number(num)
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const categoryIconMap = {
  '餐饮': 'restaurant',
  '交通': 'directions_car',
  '购物': 'shopping_bag',
  '娱乐': 'sports_esports',
  '居住': 'home',
  '医疗': 'local_hospital',
  '教育': 'school',
  '通讯': 'phone_android',
  '服饰': 'checkroom',
  '美容': 'spa',
  '运动': 'fitness_center',
  '旅行': 'flight',
  '宠物': 'pets',
  '工资': 'payments',
  '奖金': 'emoji_events',
  '理财': 'trending_up',
  '兼职': 'work',
  '红包': 'redeem',
  '退款': 'assignment_return',
  '其他': 'more_horiz',
}

function getCategoryIcon(name) {
  return categoryIconMap[name] || 'category'
}

function goToRecords() {
  uni.switchTab({ url: '/pages/records/records' })
}

function goToTransaction() {
  uni.switchTab({ url: '/pages/transaction/transaction' })
}

async function loadData() {
  try {
    const userId = authStore.userInfo?.id
    const [statsRes, recentRes] = await Promise.all([
      dashboardApi.getStats(userId),
      dashboardApi.getRecent(userId, 5),
    ])
    if (statsRes?.data) {
      Object.assign(stats, statsRes.data)
    }
    if (recentRes?.data) {
      recentList.value = recentRes.data
    }
  } catch (e) {
    console.error('加载仪表盘数据失败:', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
.glass-panel {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.04);
  border-radius: 3rem;
}
.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.02);
  border-radius: 3rem;
}
.rounded-3xl {
  border-radius: 3rem !important;
}
.rounded-2xl {
  border-radius: 2rem !important;
}
.rounded-full {
  border-radius: 9999px !important;
}
</style>
