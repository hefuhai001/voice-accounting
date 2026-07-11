<template>
  <div class="px-margin-mobile pb-32 flex flex-col gap-8 max-w-3xl mx-auto">
    <!-- Header & Search -->
    <section class="flex flex-col gap-6 mt-8">
      <div class="flex items-end justify-between">
        <h2 class="font-headline-lg-mobile text-[28px] font-bold text-on-surface tracking-tight">交易记录</h2>
        <span class="text-on-surface-variant font-medium text-sm">本月支出: ¥{{ monthlyExpense.toFixed(2) }}</span>
      </div>

      <div class="relative w-full group">
        <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors text-[22px]">search</span>
        <input
          v-model="searchText"
          type="text"
          placeholder="查找餐饮、购物或转账..."
          class="w-full bg-white border border-outline rounded-3xl py-3.5 pl-12 pr-4 text-[15px] focus:ring-2 focus:ring-primary/10 focus:border-primary focus:outline-none placeholder:text-on-surface-variant shadow-sm transition-all"
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- Filter Tabs -->
      <div class="flex gap-3 overflow-x-auto pb-1 no-scrollbar">
        <button
          v-for="f in filters"
          :key="f.value"
          @click="setFilter(f.value)"
          class="px-6 py-2.5 rounded-full text-[14px] font-bold transition-all active:scale-95"
          :class="
            activeFilter === f.value
              ? 'bg-primary text-white shadow-lg shadow-primary/20'
              : 'bg-white border border-outline text-on-surface-variant hover:border-primary/30 hover:bg-primary/5'
          "
        >
          {{ f.label }}
        </button>
        <button class="px-4 py-2.5 rounded-full bg-white border border-outline text-on-surface-variant hover:text-primary transition-all">
          <span class="material-symbols-outlined text-[18px]">tune</span>
        </button>
      </div>
    </section>

    <!-- Timeline -->
    <section class="flex flex-col gap-10 relative timeline-line">
      <template v-for="(group, date) in groupedRecords" :key="date">
        <div class="relative flex flex-col gap-5">
          <!-- Date Header -->
          <div class="flex items-center gap-4 z-10">
            <div class="w-10 h-10 rounded-full bg-white border border-outline flex items-center justify-center shrink-0 shadow-sm">
              <span class="material-symbols-outlined text-primary text-[20px]">calendar_today</span>
            </div>
            <div class="font-bold text-[15px] text-on-surface tracking-wide">{{ formatDate(date) }}</div>
          </div>

          <!-- Records -->
          <div class="ml-10 flex flex-col gap-4">
            <div
              v-for="record in group"
              :key="record.id"
              @click="showActions(record)"
              class="custom-card p-5 flex justify-between items-center cursor-pointer"
            >
              <div class="flex items-center gap-4">
                <div
                  class="w-12 h-12 rounded-2xl flex items-center justify-center"
                  :class="getCategoryBgClass(record.type, record.categoryName)"
                >
                  <span class="material-symbols-outlined text-[24px]" :class="getCategoryColorClass(record.type, record.categoryName)">
                    {{ getCategoryIcon(record.categoryName) }}
                  </span>
                </div>
                <div class="flex flex-col">
                  <span class="text-[16px] text-on-surface font-bold">{{ record.categoryName }}</span>
                  <span class="text-[13px] text-on-surface-variant font-medium">
                    {{ formatTime(record.transactionDate || record.date) }} · {{ record.remark || '无备注' }}
                  </span>
                </div>
              </div>
              <div class="text-right">
                <span
                  class="text-[20px] font-bold font-finance-xl"
                  :class="record.type === 1 ? 'text-error' : 'text-secondary'"
                >
                  {{ record.type === 1 ? '-' : '+' }}¥{{ Number(record.amount).toFixed(2) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Load More -->
      <div v-if="hasMore && records.length > 0" class="py-4 text-center">
        <button
          @click="loadMore"
          :disabled="loadingMore"
          class="font-label-md text-primary disabled:opacity-50"
        >
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="records.length === 0 && !loading" class="custom-card rounded-3xl p-12 text-center">
        <span class="material-symbols-outlined text-6xl text-outline mb-4 block">history</span>
        <p class="font-body-md text-body-md text-on-surface-variant">暂无记录</p>
        <button @click="goToTransaction" class="mt-4 bg-primary text-white px-6 py-2 rounded-full font-bold text-sm">
          去记一笔
        </button>
      </div>
    </section>

    <!-- ActionSheet -->
    <Transition name="fade">
      <div v-if="actionSheetVisible" class="fixed inset-0 z-50" @click="actionSheetVisible = false">
        <div class="absolute inset-0 bg-black/30" />
        <div class="absolute bottom-36 left-0 right-0 pb-[env(safe-area-inset-bottom)]">
          <div class="mx-3 mb-2 bg-white/95 backdrop-blur-xl rounded-3xl overflow-hidden">
            <button
              @click.stop="handleEdit"
              class="w-full py-3.5 text-[17px] text-tertiary font-normal border-b border-outline/30 active:bg-surface-container-low"
            >
              编辑记录
            </button>
            <button
              @click.stop="handleDelete"
              class="w-full py-3.5 text-[17px] text-danger-red font-normal active:bg-surface-container-low"
            >
              删除记录
            </button>
          </div>
          <div class="mx-3 bg-white/95 backdrop-blur-xl rounded-3xl overflow-hidden">
            <button
              @click="actionSheetVisible = false"
              class="w-full py-3.5 text-[17px] text-tertiary font-semibold active:bg-surface-container-low"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Edit Modal -->
    <a-modal
      :open="editModalVisible"
      title="编辑记录"
      :footer="null"
      :width="380"
      centered
      @cancel="editModalVisible = false"
    >
      <a-form :model="editForm" layout="vertical" class="mt-3" @finish="handleEditSubmit">
        <div class="bg-surface-container-low p-1 rounded-xl flex mb-4">
          <button
            v-for="t in [{ value: 1, label: '支出' }, { value: 2, label: '收入' }]"
            :key="t.value"
            type="button"
            @click="editForm.type = t.value"
            class="flex-1 py-3 text-center rounded-lg font-label-md text-label-md transition-all duration-300"
            :class="
              editForm.type === t.value
                ? 'bg-white text-primary shadow-[0px_4px_12px_rgba(0,0,0,0.05)]'
                : 'text-on-surface-variant hover:bg-surface-container-high'
            "
          >
            {{ t.label }}
          </button>
        </div>

        <a-form-item label="金额" name="amount" :rules="[{ required: true, message: '请输入金额' }]">
          <a-input-number
            v-model:value="editForm.amount"
            :min="0.01"
            :precision="2"
            style="width: 100%"
            size="large"
            placeholder="请输入金额"
          />
        </a-form-item>

        <a-form-item label="分类" name="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
          <a-select v-model:value="editForm.categoryId" placeholder="选择分类" size="large">
            <a-select-option v-for="item in editCategories" :key="item.id" :value="item.id">
              <span class="material-symbols-outlined text-[16px] mr-2">{{ item.icon }}</span>
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="日期" name="transactionDate" :rules="[{ required: true, message: '请选择日期' }]">
          <a-date-picker
            v-model:value="editForm.transactionDate"
            style="width: 100%"
            size="large"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>

        <a-form-item label="备注">
          <a-textarea v-model:value="editForm.remark" placeholder="添加备注..." :rows="2" size="large" />
        </a-form-item>

        <div class="flex gap-3 mt-2">
          <a-button block size="large" @click="editModalVisible = false">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="editSubmitting">保存</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { transactionApi, categoryApi } from '@/api'
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
const monthlyExpense = ref(0)

const actionSheetVisible = ref(false)
const activeRecord = ref(null)

const editModalVisible = ref(false)
const editSubmitting = ref(false)
const editCategories = ref([])
const editForm = reactive({
  id: null,
  type: 1,
  amount: null,
  categoryId: undefined,
  transactionDate: new Date().toISOString().slice(0, 10),
  remark: '',
})

const filters = [
  { label: '全部', value: 'all' },
  { label: '支出', value: 'expense' },
  { label: '收入', value: 'income' },
]

const hasMore = computed(() => records.value.length < pagination.total)

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

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
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
  }
  return map[name] || 'receipt'
}

function getCategoryBgClass(type, name) {
  if (type === 2) return 'bg-green-50'
  const map = {
    餐饮: 'bg-orange-50',
    交通: 'bg-blue-50',
    购物: 'bg-violet-50',
    娱乐: 'bg-pink-50',
  }
  return map[name] || 'bg-slate-50'
}

function getCategoryColorClass(type, name) {
  if (type === 2) return 'text-green-500'
  const map = {
    餐饮: 'text-orange-500',
    交通: 'text-blue-500',
    购物: 'text-violet-500',
    娱乐: 'text-pink-500',
  }
  return map[name] || 'text-slate-500'
}

function showActions(record) {
  activeRecord.value = record
  actionSheetVisible.value = true
}

async function handleEdit() {
  actionSheetVisible.value = false
  const record = activeRecord.value
  if (!record) return

  try {
    const catRes = await categoryApi.getList(userId.value)
    editCategories.value = catRes.data || []
  } catch (e) {
    console.error('加载分类失败:', e)
  }

  Object.assign(editForm, {
    id: record.id,
    type: record.type,
    amount: Number(record.amount),
    categoryId: record.categoryId,
    transactionDate: record.transactionDate || record.date,
    remark: record.remark || '',
  })
  editModalVisible.value = true
}

async function handleEditSubmit() {
  editSubmitting.value = true
  try {
    await transactionApi.update(editForm.id, {
      type: editForm.type,
      amount: editForm.amount,
      categoryId: editForm.categoryId,
      transactionDate: editForm.transactionDate,
      remark: editForm.remark,
    })
    message.success('修改成功')
    editModalVisible.value = false
    loadData()
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    editSubmitting.value = false
  }
}

function handleDelete() {
  actionSheetVisible.value = false
  const record = activeRecord.value
  if (!record) return

  Modal.confirm({
    title: '确认删除',
    content: `确定要删除这条${record.type === 1 ? '支出' : '收入'}记录（¥${Number(record.amount).toFixed(2)}）吗？`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await transactionApi.delete(record.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        console.error('删除失败:', error)
      }
    },
  })
}

function setFilter(value) {
  activeFilter.value = value
  loadData()
}

function goToTransaction() {
  router.push('/transaction')
}

async function loadData(reset = true) {
  if (!userId.value) return

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

    // 计算本月支出
    const monthExpense = records.value
      .filter(r => r.type === 1)
      .reduce((sum, r) => sum + Number(r.amount), 0)
    monthlyExpense.value = monthExpense
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

.no-scrollbar::-webkit-scrollbar {
  display: none;
}

.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.rounded-3xl {
  border-radius: 2rem;
}

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
