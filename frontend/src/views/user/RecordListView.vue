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
          class="bg-white rounded-2xl shadow-sm overflow-hidden transition-colors"
        >
          <!-- 主内容行 -->
          <div
            class="p-4 flex items-center justify-between cursor-pointer active:bg-black/[0.02]"
            @click="showActions(record)"
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
            <div class="shrink-0 flex items-center gap-2">
              <span class="text-xs text-[#aeaeb2]">{{ formatDateShort(record.transactionDate || record.date) }}</span>
              <svg class="w-4 h-4 text-[#c7c7cc]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
              </svg>
            </div>
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

    <!-- 操作菜单 ActionSheet -->
    <Transition name="fade">
      <div v-if="actionSheetVisible" class="fixed inset-0 z-50" @click="actionSheetVisible = false">
        <div class="absolute inset-0 bg-black/30" />
        <div class="absolute bottom-0 left-0 right-0 pb-[env(safe-area-inset-bottom)]">
          <div class="mx-3 mb-2 bg-white/95 backdrop-blur-xl rounded-2xl overflow-hidden">
            <button
              @click.stop="handleEdit"
              class="w-full py-3.5 text-[17px] text-[#007aff] font-normal border-b border-black/5 active:bg-black/5"
            >
              编辑记录
            </button>
            <button
              @click.stop="handleDelete"
              class="w-full py-3.5 text-[17px] text-[#ff3b30] font-normal active:bg-black/5"
            >
              删除记录
            </button>
          </div>
          <div class="mx-3 bg-white/95 backdrop-blur-xl rounded-2xl overflow-hidden">
            <button
              @click="actionSheetVisible = false"
              class="w-full py-3.5 text-[17px] text-[#007aff] font-semibold active:bg-black/5"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 编辑弹窗 -->
    <a-modal
      :open="editModalVisible"
      title="编辑记录"
      :footer="null"
      :width="380"
      centered
      @cancel="editModalVisible = false"
    >
      <a-form :model="editForm" layout="vertical" class="mt-3" @finish="handleEditSubmit">
        <!-- 类型切换 -->
        <div class="flex bg-[#f2f2f7] rounded-xl p-1 mb-4">
          <button
            v-for="t in [{ value: 1, label: '支出' }, { value: 2, label: '收入' }]"
            :key="t.value"
            type="button"
            @click="editForm.type = t.value"
            class="flex-1 py-2 text-sm font-semibold rounded-lg transition-all duration-200"
            :class="editForm.type === t.value ? 'bg-white text-[#1c1c1e] shadow-sm' : 'text-[#8e8e93]'"
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
              {{ item.icon }} {{ item.name }}
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
import { UnorderedListOutlined } from '@ant-design/icons-vue'
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

// ActionSheet
const actionSheetVisible = ref(false)
const activeRecord = ref(null)

// 编辑弹窗
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

// 显示操作菜单
function showActions(record) {
  activeRecord.value = record
  actionSheetVisible.value = true
}

// 编辑
async function handleEdit() {
  actionSheetVisible.value = false
  const record = activeRecord.value
  if (!record) return

  // 加载分类列表
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

// 提交编辑
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

// 删除
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

/* ActionSheet 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
