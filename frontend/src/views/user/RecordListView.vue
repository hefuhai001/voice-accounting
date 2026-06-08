<template>
  <div class="px-4 pt-4 pb-6 space-y-4">
    <!-- 搜索栏 -->
    <div class="relative">
      <svg
        class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-[#c7c7cc]"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        stroke-width="2"
      >
        <circle cx="11" cy="11" r="8" />
        <path d="M21 21l-4.35-4.35" />
      </svg>
      <input
        v-model="searchText"
        type="text"
        placeholder="搜索备注..."
        class="w-full pl-10 pr-4 py-2.5 bg-white rounded-xl text-[15px] outline-none border border-transparent focus:border-[#ff6b35]/30 focus:shadow-sm transition-all placeholder:text-[#c7c7cc]"
        @keyup.enter="handleSearch"
      />
    </div>

    <!-- 筛选标签 -->
    <div class="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
      <button
        v-for="f in filters"
        :key="f.value"
        @click="setFilter(f.value)"
        class="shrink-0 px-3.5 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all duration-200"
        :class="
          activeFilter === f.value
            ? 'bg-[#ff6b35] text-white shadow-sm'
            : 'bg-white text-[#8e8e93] active:bg-[#f2f2f7]'
        "
      >
        {{ f.label }}
      </button>
    </div>

    <!-- 记录列表 -->
    <div class="space-y-3">
      <!-- 按日期分组 -->
      <template v-for="(group, date) in groupedRecords" :key="date">
        <!-- 日期标题 -->
        <p class="text-xs font-semibold text-[#8e8e93] px-1 sticky top-0 bg-[#F2F2F7] py-1 z-10">
          {{ formatDate(date) }}
        </p>

        <!-- 记录卡片 -->
        <div
          v-for="record in group"
          :key="record.id"
          class="bg-white rounded-2xl p-4 shadow-sm flex items-center justify-between active:bg-black/[0.02] transition-colors cursor-pointer"
        >
          <div class="flex items-center gap-3 min-w-0 flex-1">
            <div
              class="w-11 h-11 rounded-xl flex items-center justify-center text-sm font-bold shrink-0"
              :class="record.type === 1 ? 'bg-red-50 text-red-500' : 'bg-emerald-50 text-emerald-500'"
            >
              {{ record.type === 1 ? '支' : '收' }}
            </div>
            <div class="min-w-0 flex items-center gap-2">
              <span
                class="text-[17px] font-bold tabular-nums"
                :class="record.type === 1 ? 'text-red-500' : 'text-emerald-500'"
              >
                {{ record.type === 1 ? '-' : '+' }}¥{{ Number(record.amount).toFixed(2) }}
              </span>
              <span v-if="record.remark" class="text-xs text-[#8e8e93] truncate">{{ record.remark }}</span>
            </div>
          </div>
          <div class="shrink-0 mt-4">
            <p class="text-xs text-[#aeaeb2]">{{ formatDateShort(record.transactionDate || record.date) }}</p>
          </div>
        </div>
      </template>

      <!-- 加载更多 -->
      <div v-if="hasMore && records.length > 0" class="py-4 text-center">
        <button
          @click="loadMore"
          :disabled="loadingMore"
          class="text-sm text-[#ff6b35] font-medium disabled:opacity-50"
        >
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>

      <!-- 空状态 -->
      <div v-if="records.length === 0 && !loading" class="py-16 text-center">
        <div
          class="w-16 h-16 mx-auto mb-3 rounded-2xl bg-[#f2f2f7] flex items-center justify-center"
        >
          <UnorderedListOutlined class="text-2xl text-[#c7c7cc]" />
        </div>
        <p class="text-sm text-[#8e8e93]">暂无记录</p>
        <button @click="goToTransaction" class="mt-3 text-sm text-[#ff6b35] font-medium">
          去记一笔
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { UnorderedListOutlined } from '@ant-design/icons-vue'
import { transactionApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)

const loading = ref(false)
const loadingMore = ref(false)
const records = ref([])
const searchText = ref('')
const activeFilter = ref('all')
const pagination = reactive({ current: 1, pageSize: 20, total: 0 })

const filters = [
  { label: '全部', value: 'all' },
  { label: '支出', value: 'expense' },
  { label: '收入', value: 'income' },
]

const hasMore = computed(() => records.value.length < pagination.total)

// 按日期分组
const groupedRecords = computed(() => {
  const map = {}
  for (const r of records.value) {
    const date = r.transactionDate || r.date
    if (!map[date]) map[date] = []
    map[date].push(r)
  }
  return map
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const today = new Date()
  const d = new Date(dateStr)
  const isToday = d.toDateString() === today.toDateString()
  if (isToday) return '今天'
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  if (d.toDateString() === yesterday.toDateString()) return '昨天'
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

function formatDateShort(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// 切换筛选条件
function setFilter(value) {
  activeFilter.value = value
  loadData()
}

// 跳转到记账页
function goToTransaction() {
  router.push('/transaction')
}

async function loadData(reset = true) {
  // 确保 userId 有值
  if (!userId.value) {
    console.warn('userId 未加载，等待用户信息...')
    return
  }

  if (reset) {
    records.value = []
    pagination.current = 1
  }
  loading.value = true
  try {
    const params = {
      userId: userId.value,
      current: pagination.current,
      size: pagination.pageSize,
    }
    if (searchText.value) params.keyword = searchText.value
    if (activeFilter.value !== 'all') params.type = activeFilter.value === 'expense' ? 1 : 2

    const res = await transactionApi.getPage(params)
    const newRecords = res.data.records
    if (reset) {
      records.value = newRecords
    } else {
      records.value.push(...newRecords)
    }
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function handleSearch() {
  loadData(true)
}

function loadMore() {
  loadingMore.value = true
  pagination.current++
  loadData(false)
}

// 监听 userId 变化，当用户信息加载完成后自动加载数据
watch(userId, (newUserId) => {
  if (newUserId) {
    loadData()
  }
})

// 页面加载时检查登录状态
onMounted(async () => {
  // 如果还没有用户信息，先检查登录状态
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  // 如果已有用户信息，直接加载数据
  if (userId.value) {
    loadData()
  }
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.tabular-nums {
  font-variant-numeric: tabular-nums;
}
</style>
