<template>
  <view class="flex-1 min-h-screen bg-surface">
    <!-- 自定义导航栏 -->
    <view class="flex items-center px-4 h-12 pt-8">
      <view @click="goBack" class="w-10 h-10 flex items-center justify-center">
        <text class="material-symbols-outlined text-on-surface">arrow_back</text>
      </view>
      <text class="flex-1 text-center font-headline-md text-on-surface">我的提醒</text>
      <view class="w-10"></view>
    </view>

    <!-- 新建按钮 -->
    <view class="px-4 mt-4 mb-4">
      <view
        class="w-full py-3 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] text-center"
        @click="openAddModal"
      >
        <text class="text-label-md text-white">新建提醒</text>
      </view>
    </view>

    <!-- 状态筛选 Tabs -->
    <view class="px-4 mb-4">
      <view class="flex gap-2">
        <view
          v-for="tab in statusTabs"
          :key="tab.value"
          class="px-4 py-2 rounded-full"
          :class="activeStatus === tab.value ? 'bg-primary' : 'bg-surface-container'"
          @click="activeStatus = tab.value; loadData()"
        >
          <text class="text-label-md" :class="activeStatus === tab.value ? 'text-white' : 'text-on-surface-variant'">{{ tab.label }}</text>
        </view>
      </view>
    </view>

    <!-- 提醒列表 -->
    <view class="px-4 pb-8">
      <view
        v-for="item in filteredReminders"
        :key="item.id"
        class="glass-panel rounded-2xl p-4 mb-3"
      >
        <!-- 顶部：状态图标 + 标题 + 金额 -->
        <view class="flex items-start mb-3">
          <view class="w-10 h-10 rounded-xl flex items-center justify-center mr-3" :class="getStatusBgClass(item.status)">
            <text class="material-symbols-outlined text-xl" :class="getStatusIconClass(item.status)">alarm</text>
          </view>
          <view class="flex-1">
            <view class="flex items-center justify-between">
              <text class="text-body-lg text-on-surface font-medium flex-1">{{ item.title }}</text>
              <text class="text-body-lg text-primary font-semibold">¥{{ item.amount }}</text>
            </view>
            <text class="text-label-sm mt-0.5" :class="getStatusTextClass(item.status)">{{ getStatusLabel(item.status) }}</text>
          </view>
        </view>

        <!-- 日期和频率 -->
        <view class="flex items-center mb-2 ml-13">
          <text class="material-symbols-outlined text-sm text-on-surface-variant mr-1">calendar_today</text>
          <text class="text-label-md text-on-surface-variant">{{ item.reminderDate }}</text>
          <text class="text-label-md text-on-surface-variant mx-2">·</text>
          <text class="text-label-md text-on-surface-variant">{{ getFrequencyLabel(item.frequency) }}</text>
        </view>

        <!-- 备注 -->
        <view v-if="item.remark" class="ml-13 mb-3">
          <text class="text-body-md text-on-surface-variant">{{ item.remark }}</text>
        </view>

        <!-- 操作按钮 -->
        <view class="flex gap-2 ml-13 flex-wrap">
          <view
            class="px-3 py-1.5 rounded-full bg-surface-container"
            @click="openEditModal(item)"
          >
            <text class="text-label-sm text-on-surface-variant">编辑</text>
          </view>
          <view
            v-if="item.status === 0"
            class="px-3 py-1.5 rounded-full bg-primary/10"
            @click="markRead(item)"
          >
            <text class="text-label-sm text-primary">标记</text>
          </view>
          <view
            v-if="item.status === 0 || item.status === 1"
            class="px-3 py-1.5 rounded-full bg-surface-container"
            @click="closeReminder(item)"
          >
            <text class="text-label-sm text-on-surface-variant">关闭</text>
          </view>
          <view
            v-if="item.status === 2"
            class="px-3 py-1.5 rounded-full bg-success-green/10"
            @click="reopenReminder(item)"
          >
            <text class="text-label-sm text-success-green">开启提醒</text>
          </view>
          <view
            class="px-3 py-1.5 rounded-full bg-error/10"
            @click="deleteReminder(item)"
          >
            <text class="text-label-sm text-error">删除</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="filteredReminders.length === 0" class="flex flex-col items-center justify-center py-20">
        <text class="material-symbols-outlined text-6xl text-on-surface-variant/30 mb-4">notifications_off</text>
        <text class="text-body-lg text-on-surface-variant">暂无提醒</text>
      </view>
    </view>

    <!-- Add/Edit Modal -->
    <view
      v-if="showModal"
      class="fixed inset-0 z-50 flex items-end justify-center"
    >
      <view class="absolute inset-0 bg-black/40" @click="showModal = false"></view>
      <view class="relative w-full max-w-md bg-surface-bright rounded-t-3xl p-6 pb-8 max-h-[85vh] overflow-y-auto">
        <view class="w-10 h-1 bg-outline/30 rounded-full mx-auto mb-6"></view>
        <text class="text-headline-lg text-on-surface mb-6">{{ editingReminder ? '编辑提醒' : '新建提醒' }}</text>

        <!-- Title -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">标题</text>
          <input
            v-model="form.title"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入提醒标题"
          />
        </view>

        <!-- Amount -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">金额</text>
          <input
            v-model="form.amount"
            type="digit"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入金额"
          />
        </view>

        <!-- Date -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">提醒日期</text>
          <picker mode="date" :value="form.reminderDate" @change="onDateChange">
            <view class="h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 flex items-center">
              <text class="material-symbols-outlined text-on-surface-variant mr-2">calendar_today</text>
              <text>{{ form.reminderDate || '请选择日期' }}</text>
            </view>
          </picker>
        </view>

        <!-- Frequency -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1">重复频率</text>
          <view class="flex gap-2 flex-wrap">
            <view
              v-for="freq in frequencyOptions"
              :key="freq.value"
              class="px-4 py-2 rounded-full"
              :class="form.frequency === freq.value ? 'bg-primary' : 'bg-surface-container'"
              @click="form.frequency = freq.value"
            >
              <text class="text-label-md" :class="form.frequency === freq.value ? 'text-white' : 'text-on-surface-variant'">{{ freq.label }}</text>
            </view>
          </view>
        </view>

        <!-- Remark -->
        <view class="mb-6">
          <text class="text-label-md text-on-surface-variant mb-1">备注</text>
          <textarea
            v-model="form.remark"
            class="w-full px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入备注"
          />
        </view>

        <!-- Buttons -->
        <view class="flex gap-3">
          <view
            class="flex-1 py-3 rounded-full bg-surface-container text-center"
            @click="showModal = false"
          >
            <text class="text-label-md text-on-surface-variant">取消</text>
          </view>
          <view
            class="flex-1 py-3 rounded-full bg-gradient-to-r from-[#983f19] to-[#ab3500] text-center"
            @click="saveReminder"
          >
            <text class="text-label-md text-white">保存</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { reminderApi } from '@/api/user/reminder'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const activeStatus = ref('all')
const reminders = ref([])
const showModal = ref(false)
const editingReminder = ref(null)

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '待提醒', value: '0' },
  { label: '已提醒', value: '1' },
  { label: '已关闭', value: '2' },
]

const frequencyOptions = [
  { label: '一次性', value: 0 },
  { label: '每天', value: 1 },
  { label: '每周', value: 2 },
  { label: '每月', value: 3 },
  { label: '每年', value: 4 },
]

const form = reactive({
  title: '',
  amount: '',
  reminderDate: '',
  frequency: 0,
  remark: '',
  userId: null,
  bookId: null,
})

const filteredReminders = computed(() => {
  if (activeStatus.value === 'all') return reminders.value
  return reminders.value.filter(r => String(r.status) === activeStatus.value)
})

function getStatusLabel(status) {
  const map = { 0: '待提醒', 1: '已提醒', 2: '已关闭' }
  return map[status] || '未知'
}

function getStatusBgClass(status) {
  const map = { 0: 'bg-amber-100', 1: 'bg-blue-100', 2: 'bg-gray-100' }
  return map[status] || 'bg-gray-100'
}

function getStatusIconClass(status) {
  const map = { 0: 'text-amber-600', 1: 'text-blue-600', 2: 'text-gray-400' }
  return map[status] || 'text-gray-400'
}

function getStatusTextClass(status) {
  const map = { 0: 'text-amber-600', 1: 'text-blue-600', 2: 'text-gray-400' }
  return map[status] || 'text-gray-400'
}

function getFrequencyLabel(freq) {
  const map = { 0: '一次性', 1: '每天', 2: '每周', 3: '每月', 4: '每年' }
  return map[freq] || '未知'
}

function onDateChange(e) {
  form.reminderDate = e.detail.value
}

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.switchTab({ url: '/pages/dashboard/dashboard' })
  }
}

async function loadData() {
  try {
    const userId = authStore.userInfo?.id
    const res = await reminderApi.getPage({ userId, current: 1, size: 100 })
    reminders.value = res.data?.records || res.data || []
  } catch (e) {
    console.error('加载提醒失败:', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function openAddModal() {
  editingReminder.value = null
  form.title = ''
  form.amount = ''
  form.reminderDate = ''
  form.frequency = 0
  form.remark = ''
  form.userId = authStore.userInfo?.id
  form.bookId = authStore.userInfo?.currentBookId || null
  showModal.value = true
}

function openEditModal(item) {
  editingReminder.value = item
  form.title = item.title
  form.amount = item.amount
  form.reminderDate = item.reminderDate
  form.frequency = item.frequency
  form.remark = item.remark || ''
  form.userId = item.userId
  form.bookId = item.bookId
  showModal.value = true
}

async function saveReminder() {
  if (!form.title.trim()) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }
  if (!form.amount) {
    uni.showToast({ title: '请输入金额', icon: 'none' })
    return
  }
  if (!form.reminderDate) {
    uni.showToast({ title: '请选择日期', icon: 'none' })
    return
  }
  try {
    const data = {
      title: form.title,
      amount: form.amount,
      reminderDate: form.reminderDate,
      frequency: form.frequency,
      remark: form.remark,
      userId: form.userId,
      bookId: form.bookId,
    }
    if (editingReminder.value) {
      await reminderApi.update(editingReminder.value.id, data)
    } else {
      await reminderApi.save(data)
    }
    showModal.value = false
    uni.showToast({ title: '保存成功', icon: 'success' })
    await loadData()
  } catch (e) {
    console.error('保存提醒失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

async function markRead(item) {
  try {
    await reminderApi.markRead(item.id)
    uni.showToast({ title: '已标记', icon: 'success' })
    await loadData()
  } catch (e) {
    console.error('标记失败:', e)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

async function closeReminder(item) {
  try {
    await reminderApi.close(item.id)
    uni.showToast({ title: '已关闭', icon: 'success' })
    await loadData()
  } catch (e) {
    console.error('关闭失败:', e)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

async function reopenReminder(item) {
  try {
    await reminderApi.reopen(item.id)
    uni.showToast({ title: '已开启', icon: 'success' })
    await loadData()
  } catch (e) {
    console.error('开启失败:', e)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

function deleteReminder(item) {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除提醒"${item.title}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await reminderApi.delete(item.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          await loadData()
        } catch (e) {
          console.error('删除失败:', e)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
</style>
