<template>
  <div class="px-margin-mobile pb-32 flex flex-col gap-stack-lg max-w-md mx-auto">
    <!-- Greeting Section -->
    <section class="flex flex-col gap-1 mb-2">
      <span class="font-label-caps text-label-caps text-on-surface-variant uppercase tracking-wider">欢迎回来</span>
      <h2 class="font-headline-lg text-headline-lg text-on-surface">你好，{{ authStore.userInfo?.nickname || '用户' }}</h2>
    </section>

    <!-- Bento Grid: Financial Overview -->
    <section class="grid grid-cols-2 gap-4">
      <!-- Main Spend Card -->
      <div class="col-span-2 glass-card rounded-3xl p-6 flex flex-col gap-4 relative overflow-hidden group">
        <div class="absolute -top-10 -right-10 w-40 h-40 bg-primary/10 rounded-full blur-3xl group-hover:bg-primary/15 transition-colors duration-500"></div>
        <div class="flex justify-between items-start z-10">
          <div class="flex flex-col gap-1">
            <span class="font-label-caps text-label-caps text-on-surface-variant uppercase font-semibold">本月支出</span>
            <div class="font-display-lg text-display-lg text-primary mt-2">
              ¥{{ formatAmount(stats.monthExpense) }}
            </div>
          </div>
          <div class="bg-secondary-container/10 text-secondary border border-secondary/30 px-3 py-1 rounded-full flex items-center gap-1 backdrop-blur-md">
            <span class="material-symbols-outlined text-[14px] font-bold">trending_down</span>
            <span class="font-label-caps text-label-caps font-bold">较上月 -12%</span>
          </div>
        </div>
      </div>

      <!-- Income Block -->
      <div class="glass-panel rounded-3xl p-6 flex flex-col gap-2 justify-center relative overflow-hidden group border border-on-surface/5">
        <div class="absolute -bottom-5 -left-5 w-24 h-24 bg-secondary/10 rounded-full blur-2xl group-hover:bg-secondary/15 transition-colors"></div>
        <div class="flex items-center gap-2 z-10 text-on-surface-variant mb-1">
          <div class="w-8 h-8 rounded-full bg-secondary/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-secondary text-sm font-bold">arrow_downward</span>
          </div>
          <span class="font-label-caps text-label-caps uppercase font-semibold">收入</span>
        </div>
        <div class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface z-10 font-bold">
          ¥{{ formatCompact(stats.monthIncome) }}
        </div>
      </div>

      <!-- Balance Block -->
      <div class="glass-panel rounded-3xl p-6 flex flex-col gap-2 justify-center relative overflow-hidden group border border-on-surface/5">
        <div class="absolute -bottom-5 -right-5 w-24 h-24 bg-tertiary/10 rounded-full blur-2xl group-hover:bg-tertiary/15 transition-colors"></div>
        <div class="flex items-center gap-2 z-10 text-on-surface-variant mb-1">
          <div class="w-8 h-8 rounded-full bg-tertiary/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-tertiary text-sm font-bold">account_balance_wallet</span>
          </div>
          <span class="font-label-caps text-label-caps uppercase font-semibold">余额</span>
        </div>
        <div class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface z-10 font-bold">
          ¥{{ formatCompact(stats.monthIncome - stats.monthExpense) }}
        </div>
      </div>

      <!-- Record Count Block -->
      <div class="glass-panel rounded-3xl p-5 flex flex-col gap-2 justify-center relative overflow-hidden group border border-on-surface/5">
        <div class="flex items-center gap-2 z-10 text-on-surface-variant mb-1">
          <div class="w-7 h-7 rounded-full bg-outline/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-outline text-sm font-bold">receipt_long</span>
          </div>
          <span class="font-label-caps text-label-caps uppercase font-semibold">记账</span>
        </div>
        <div class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface z-10 font-bold">
          {{ stats.recordCount }} 笔
        </div>
      </div>

      <!-- Reminder Block -->
      <div class="glass-panel rounded-3xl p-5 flex flex-col gap-2 justify-center relative overflow-hidden group border border-on-surface/5">
        <div class="flex items-center gap-2 z-10 text-on-surface-variant mb-1">
          <div class="w-7 h-7 rounded-full bg-secondary/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-secondary text-sm font-bold">alarm</span>
          </div>
          <span class="font-label-caps text-label-caps uppercase font-semibold">提醒</span>
        </div>
        <div class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface z-10 font-bold">
          {{ stats.pendingReminders }} 个
        </div>
      </div>
    </section>

    <!-- Recent Records Section -->
    <section class="flex flex-col gap-4">
      <div class="flex justify-between items-center">
        <h3 class="font-body-md text-body-md text-on-surface font-bold">最近语音记录</h3>
        <button @click="navigateTo('/records')" class="font-label-caps text-label-caps text-primary hover:bg-primary/5 px-3 py-1 rounded-full transition-colors font-bold">查看全部</button>
      </div>
      <div class="flex flex-col gap-3">
        <div
          v-for="record in recentRecords"
          :key="record.id"
          class="glass-panel rounded-3xl p-4 flex items-center justify-between hover:bg-white transition-all cursor-pointer group border border-on-surface/5"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-2xl bg-surface-container flex items-center justify-center border border-on-surface/5 group-hover:scale-105 transition-transform">
              <span class="material-symbols-outlined text-tertiary text-3xl" style="font-variation-settings: 'FILL' 1;">{{ getCategoryIcon(record.categoryName) }}</span>
            </div>
            <div class="flex flex-col">
              <span class="font-body-md text-body-md font-bold text-on-surface">{{ record.categoryName }}</span>
              <span class="font-label-caps text-label-caps text-on-surface-variant">{{ record.remark || record.transactionDate }}</span>
            </div>
          </div>
          <span
            class="font-body-md text-body-md font-bold"
            :class="record.type === 1 ? 'text-on-surface' : 'text-secondary text-lg'"
          >
            {{ record.type === 1 ? '-' : '+' }}¥{{ Number(record.amount).toFixed(2) }}
          </span>
        </div>
      </div>
      <div v-if="recentRecords.length === 0" class="py-12 text-center">
        <p class="font-body-md text-on-surface-variant">暂无记账记录</p>
        <button @click="router.push('/transaction')" class="mt-3 font-label-md text-primary">去记一笔</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { dashboardApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)
const router = useRouter()

const loading = ref(false)
const recentRecords = ref([])
const stats = reactive({
  monthExpense: 0,
  monthIncome: 0,
  recordCount: 0,
  pendingReminders: 0,
})

const tabPaths = new Set(['/dashboard', '/books', '/records', '/category'])
function navigateTo(path) {
  if (tabPaths.has(path)) {
    router.replace(path)
  } else {
    router.push(path)
  }
}

function formatAmount(value) {
  return Number(value || 0).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
}

function formatCompact(value) {
  const num = Number(value || 0)
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toFixed(0)
}

function getCategoryIcon(name) {
  const map = {
    餐饮: 'restaurant',
    交通: 'directions_car',
    工资: 'payments',
    购物: 'shopping_bag',
    娱乐: 'sports_esports',
    住房: 'home',
    医疗: 'local_hospital',
    教育: 'school',
    旅行: 'flight',
  }
  return map[name] || 'receipt'
}

async function loadData() {
  if (!userId.value) return
  loading.value = true
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
  } finally {
    loading.value = false
  }
}

watch(userId, (newUserId) => {
  if (newUserId) loadData()
})

onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadData()
})
</script>

<style scoped>
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

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

/* 强制大圆角 */
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
