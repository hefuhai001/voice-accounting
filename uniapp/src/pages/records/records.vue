<template>
  <view class="flex-1 bg-background min-h-screen overflow-x-hidden">
    <CustomHeader />

    <view class="px-4 pb-32 flex flex-col gap-8">
      <!-- Header & Search -->
      <view class="flex flex-col gap-6 mt-8">
        <view class="flex items-end justify-between">
          <text class="font-headline-lg-mobile text-[28px] font-bold text-on-surface tracking-tight">交易记录</text>
          <text class="text-on-surface-variant font-medium text-sm">本月支出: ¥{{ monthlyExpense.toFixed(2) }}</text>
        </view>

        <view class="relative w-full">
          <text class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant text-[22px]">search</text>
          <input
            v-model="searchText"
            type="text"
            placeholder="查找餐饮、购物或转账..."
            class="w-full h-12 bg-white border-2 border-outline/50 rounded-3xl pl-12 pr-4 text-[15px] placeholder:text-on-surface-variant shadow-sm box-border outline-none"
            confirm-type="search"
            @confirm="onSearch"
          />
        </view>

        <!-- Filter Tabs -->
        <scroll-view scroll-x class="flex gap-3 overflow-x-auto pb-1 no-scrollbar">
          <view class="flex gap-3">
            <view
              v-for="f in filters"
              :key="f.value"
              @click="setFilter(f.value)"
              class="px-6 py-2.5 rounded-full text-[14px] font-bold transition-all active:scale-95"
              :class="activeFilter === f.value ? 'bg-primary text-white shadow-lg shadow-primary/20' : 'bg-white border border-outline text-on-surface-variant'"
            >
              <text class="text-[14px] font-bold" :class="activeFilter === f.value ? 'text-white' : 'text-on-surface-variant'">{{ f.label }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- Timeline -->
      <view class="flex flex-col gap-10 relative timeline-line">
        <template v-for="(group, gIdx) in groupedRecords" :key="group.date">
          <view class="relative flex flex-col gap-5">
            <!-- Date Header -->
            <view class="flex items-center gap-4 z-10">
              <view class="w-10 h-10 rounded-full bg-white border border-outline flex items-center justify-center shrink-0 shadow-sm">
                <text class="material-symbols-outlined text-primary text-[20px]">calendar_today</text>
              </view>
              <text class="font-bold text-[15px] text-on-surface tracking-wide">{{ group.dateLabel }}</text>
            </view>

            <!-- Records -->
            <view class="ml-10 flex flex-col gap-4">
              <view
                v-for="record in group.records"
                :key="record.id"
                @click="openActionSheet(record)"
                class="custom-card p-5 flex justify-between items-center cursor-pointer"
              >
                <view class="flex items-center gap-4">
                  <view class="w-12 h-12 rounded-2xl flex items-center justify-center" :class="getCategoryBgClass(record.type, record.categoryName)">
                    <text class="material-symbols-outlined text-[24px]" :class="getCategoryColorClass(record.type, record.categoryName)">{{ getCategoryIcon(record.categoryName) }}</text>
                  </view>
                  <view class="flex flex-col">
                    <text class="text-[16px] text-on-surface font-bold">{{ record.categoryName || '未分类' }}</text>
                    <text class="text-[13px] text-on-surface-variant font-medium">{{ formatTime(record.transactionDate || record.transactionTime) }}{{ record.remark ? ' · ' + record.remark : '' }}</text>
                  </view>
                </view>
                <view class="text-right">
                  <text class="text-[20px] font-bold" :class="record.type === 1 ? 'text-error' : 'text-secondary'">
                    {{ record.type === 1 ? '-' : '+' }}¥{{ Number(record.amount).toFixed(2) }}
                  </text>
                </view>
              </view>
            </view>
          </view>
        </template>

        <!-- Empty State -->
        <view v-if="groupedRecords.length === 0" class="flex flex-col items-center justify-center pt-32">
          <text class="material-symbols-outlined text-6xl text-outline/40">receipt_long</text>
          <text class="text-on-surface-variant text-body-lg mt-4">暂无交易记录</text>
          <view class="mt-3 bg-primary/10 rounded-full px-4 py-2" @click="uni.reLaunch({ url: '/pages/transaction/transaction' })">
            <text class="text-primary text-label-md font-semibold">去记一笔</text>
          </view>
        </view>
      </view>

      <!-- Load More -->
      <view v-if="pagination.hasMore && groupedRecords.length > 0" class="flex justify-center mt-4">
        <view class="bg-surface-container rounded-full px-6 py-2 active:scale-95 transition-transform" @click="loadMore">
          <text class="text-label-md text-on-surface-variant">加载更多</text>
        </view>
      </view>
    </view>

    <!-- Action Sheet -->
    <view v-if="actionSheetVisible" class="fixed inset-0 z-50" @click="actionSheetVisible = false">
      <view class="absolute inset-0 bg-black/30"></view>
      <view class="absolute bottom-20 left-0 right-0">
        <view class="mx-3 mb-2 bg-white/95 backdrop-blur-xl rounded-3xl overflow-hidden">
          <view @click.stop="handleEdit" class="w-full py-3.5 text-center text-tertiary border-b border-outline/30">
            <text class="text-body-md text-tertiary">编辑记录</text>
          </view>
          <view @click.stop="handleDeleteRecord" class="w-full py-3.5 text-center">
            <text class="text-body-md text-danger-red">删除记录</text>
          </view>
        </view>
        <view class="mx-3 bg-white/95 backdrop-blur-xl rounded-3xl overflow-hidden">
          <view @click="actionSheetVisible = false" class="w-full py-3.5 text-center">
            <text class="text-body-md text-on-surface-variant font-semibold">取消</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Edit Modal -->
    <view v-if="editModalVisible" class="fixed inset-0 z-50 flex items-center justify-center">
      <view class="absolute inset-0 bg-black/30" @click="editModalVisible = false"></view>
      <view class="relative bg-white rounded-3xl p-6 z-10 w-[85vw] max-w-[340px]">
        <text class="text-headline-lg text-on-surface">编辑记录</text>

        <!-- Type Toggle -->
        <view class="mt-5 flex gap-2">
          <view
            class="flex-1 py-2 rounded-xl text-center transition-all"
            :class="editForm.type === 1 ? 'bg-danger-red/10 border border-danger-red' : 'bg-surface-container border border-transparent'"
            @click="editForm.type = 1"
          >
            <text class="text-body-md" :class="editForm.type === 1 ? 'text-danger-red font-semibold' : 'text-on-surface-variant'">支出</text>
          </view>
          <view
            class="flex-1 py-2 rounded-xl text-center transition-all"
            :class="editForm.type === 2 ? 'bg-success-green/10 border border-success-green' : 'bg-surface-container border border-transparent'"
            @click="editForm.type = 2"
          >
            <text class="text-body-md" :class="editForm.type === 2 ? 'text-success-green font-semibold' : 'text-on-surface-variant'">收入</text>
          </view>
        </view>

        <!-- Amount -->
        <view class="mt-4">
          <text class="text-label-md text-on-surface-variant mb-1.5">金额</text>
          <input
            v-model="editForm.amount"
            type="digit"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入金额"
          />
        </view>

        <!-- Category -->
        <view class="mt-4 overflow-hidden">
          <text class="text-label-md text-on-surface-variant mb-1.5">分类</text>
          <picker :range="categoryNames" @change="onCategoryChange">
            <view class="h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 flex items-center">
              <text class="truncate flex-1" :class="editForm.categoryId ? 'text-on-surface' : 'text-outline'">{{ editForm.categoryName || '请选择分类' }}</text>
            </view>
          </picker>
        </view>

        <!-- Date -->
        <view class="mt-4 overflow-hidden">
          <text class="text-label-md text-on-surface-variant mb-1.5">日期</text>
          <picker mode="date" :value="editForm.date" @change="editForm.date = $event.detail.value">
            <view class="h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 flex items-center">
              <text class="truncate flex-1">{{ editForm.date || '请选择日期' }}</text>
            </view>
          </picker>
        </view>

        <!-- Remark -->
        <view class="mt-4">
          <text class="text-label-md text-on-surface-variant mb-1.5">备注</text>
          <input
            v-model="editForm.remark"
            type="text"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入备注（选填）"
          />
        </view>

        <!-- Buttons -->
        <view class="mt-6 flex gap-3">
          <view class="flex-1 py-2.5 rounded-xl bg-surface-container text-on-surface-variant text-center text-body-md font-semibold active:scale-95 transition-transform" @click="editModalVisible = false">
            <text>取消</text>
          </view>
          <view class="flex-1 py-2.5 rounded-xl bg-primary text-on-primary text-center text-body-md font-semibold active:scale-95 transition-transform" @click="handleSaveEdit">
            <text>保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- TabBar -->
    <CustomTabBar current="/pages/records/records" />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { transactionApi, categoryApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import CustomTabBar from '@/components/CustomTabBar.vue'
import CustomHeader from '@/components/CustomHeader.vue'

const authStore = useAuthStore()
const records = ref([])
const searchText = ref('')
const activeFilter = ref('all')
const monthlyExpense = ref(0)
const actionSheetVisible = ref(false)
const editModalVisible = ref(false)
const selectedRecord = ref(null)
const categories = ref([])

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
  hasMore: true,
})

const filters = [
  { value: 'all', label: '全部' },
  { value: 'expense', label: '支出' },
  { value: 'income', label: '收入' },
]

const editForm = reactive({
  type: 1,
  amount: '',
  categoryId: null,
  categoryName: '',
  date: '',
  remark: '',
})

const categoryNames = computed(() => categories.value.map(c => c.name))

const groupedRecords = computed(() => {
  const groups = {}
  records.value.forEach(record => {
    const raw = record.transactionDate || record.transactionTime || ''
    const dateStr = raw ? raw.split(' ')[0] : '未知日期'
    if (!groups[dateStr]) {
      groups[dateStr] = []
    }
    groups[dateStr].push(record)
  })
  return Object.keys(groups).sort((a, b) => b.localeCompare(a)).map(date => ({
    date,
    dateLabel: formatDate(date),
    records: groups[date],
  }))
})

function formatDate(dateStr) {
  if (!dateStr || dateStr === '未知日期') return dateStr
  const today = new Date()
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  const yesterdayStr = `${yesterday.getFullYear()}-${String(yesterday.getMonth() + 1).padStart(2, '0')}-${String(yesterday.getDate()).padStart(2, '0')}`
  if (dateStr === todayStr) return '今天'
  if (dateStr === yesterdayStr) return '昨天'
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const raw = timeStr.split(' ')
  if (raw.length >= 2) return raw[1].substring(0, 5)
  // transactionDate 只含日期不含时间
  return ''
}

function getCategoryIcon(name) {
  const map = {
    '餐饮': 'restaurant',
    '交通': 'directions_car',
    '工资': 'payments',
    '购物': 'shopping_bag',
    '娱乐': 'sports_esports',
    '住房': 'home',
    '医疗': 'local_hospital',
    '教育': 'school',
  }
  return map[name] || 'category'
}

function getCategoryBgClass(type, name) {
  if (type === 2) return 'bg-green-50'
  const map = {
    '餐饮': 'bg-orange-50',
    '交通': 'bg-blue-50',
    '购物': 'bg-violet-50',
    '娱乐': 'bg-pink-50',
  }
  return map[name] || 'bg-slate-50'
}

function getCategoryColorClass(type, name) {
  if (type === 2) return 'text-green-500'
  const map = {
    '餐饮': 'text-orange-500',
    '交通': 'text-blue-500',
    '购物': 'text-violet-500',
    '娱乐': 'text-pink-500',
  }
  return map[name] || 'text-slate-500'
}

function setFilter(value) {
  activeFilter.value = value
  resetAndLoad()
}

function onCategoryChange(e) {
  const idx = e.detail.value
  const cat = categories.value[idx]
  if (cat) {
    editForm.categoryId = cat.id
    editForm.categoryName = cat.name
  }
}

function openActionSheet(record) {
  selectedRecord.value = record
  actionSheetVisible.value = true
}

function handleEdit() {
  actionSheetVisible.value = false
  const record = selectedRecord.value
  if (!record) return
  editForm.type = record.type || 1
  editForm.amount = String(record.amount || '')
  editForm.categoryId = record.categoryId
  editForm.categoryName = record.categoryName || ''
  const dateStr = (record.transactionDate || record.transactionTime || '').split(' ')[0] || ''
  editForm.date = dateStr
  editForm.remark = record.remark || ''
  editModalVisible.value = true
}

async function handleSaveEdit() {
  if (!editForm.amount || Number(editForm.amount) <= 0) {
    uni.showToast({ title: '请输入有效金额', icon: 'none' })
    return
  }
  try {
    const userId = authStore.userInfo?.id
    const data = {
      type: editForm.type,
      amount: Number(editForm.amount),
      categoryId: editForm.categoryId,
      transactionTime: editForm.date,
      remark: editForm.remark,
      userId,
    }
    await transactionApi.update(selectedRecord.value.id, data)
    uni.showToast({ title: '修改成功', icon: 'success' })
    editModalVisible.value = false
    resetAndLoad()
  } catch (e) {
    console.error('修改失败:', e)
    uni.showToast({ title: '修改失败', icon: 'none' })
  }
}

function handleDeleteRecord() {
  actionSheetVisible.value = false
  const record = selectedRecord.value
  if (!record) return
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await transactionApi.delete(record.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          resetAndLoad()
        } catch (e) {
          console.error('删除失败:', e)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    },
  })
}

function onSearch() {
  resetAndLoad()
}

function resetAndLoad() {
  pagination.current = 1
  pagination.hasMore = true
  records.value = []
  loadData()
}

async function loadData() {
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    const params = {
      userId,
      current: pagination.current,
      size: pagination.size,
    }
    if (searchText.value) params.keyword = searchText.value
    if (activeFilter.value === 'expense') params.type = 1
    if (activeFilter.value === 'income') params.type = 2
    const res = await transactionApi.getPage(params)
    const pageData = res.data?.records || res.data || []
    if (pagination.current === 1) {
      records.value = pageData
    } else {
      records.value = [...records.value, ...pageData]
    }
    pagination.total = res.data?.total || 0
    pagination.hasMore = records.value.length < pagination.total

    // 从已加载记录中计算本月支出（与frontend一致）
    const monthExpense = records.value
      .filter(r => r.type === 1)
      .reduce((sum, r) => sum + Number(r.amount), 0)
    monthlyExpense.value = monthExpense
  } catch (e) {
    console.error('加载记录失败:', e)
  }
}

function loadMore() {
  pagination.current++
  loadData()
}

async function loadCategories() {
  try {
    const userId = authStore.userInfo?.id
    const res = await categoryApi.getList(userId)
    categories.value = res.data || []
  } catch (e) {
    console.error('加载分类失败:', e)
  }
}

onMounted(async () => {
  // 检查登录状态
  if (!authStore.isLoginChecked) {
    await authStore.checkLoginStatus()
  }
  // 如果未登录，跳转到登录页
  if (!authStore.userInfo?.id) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  // 如果已登录，加载数据
  loadData()
  loadCategories()
})
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
.custom-card {
  background: #ffffff;
  border-radius: 2rem;
  border: 1px solid rgba(241, 245, 249, 0.8);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03), 0 2px 4px -2px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.custom-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}
.timeline-line::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
  opacity: 0.5;
}
.rounded-3xl {
  border-radius: 2rem;
}
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
