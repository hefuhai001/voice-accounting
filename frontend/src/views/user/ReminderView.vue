<template>
  <div class="px-margin-mobile pt-stack-lg pb-8 flex flex-col gap-stack-lg">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between mt-4">
      <h1 class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface">我的提醒</h1>
      <button
        @click="showAddModal"
        class="flex items-center gap-1 px-4 py-2 sunset-gradient text-white font-label-md rounded-full active:scale-95 transition-transform shadow-sm shadow-primary/20"
      >
        <svg
          class="w-3.5 h-3.5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2.5"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
        </svg>
        新建
      </button>
    </div>

    <!-- 状态筛选 -->
    <div class="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
      <button
        v-for="s in statusFilters"
        :key="s.value"
        @click="setStatusFilter(s.value)"
        class="shrink-0 px-4 py-1.5 rounded-full font-label-sm whitespace-nowrap transition-all duration-200"
        :class="
          statusFilter === s.value
            ? 'sunset-gradient text-white shadow-sm shadow-primary/20'
            : 'bg-surface-gray text-on-surface-variant active:bg-surface-container-high'
        "
      >
        {{ s.label }}
      </button>
    </div>

    <!-- 提醒列表 -->
    <div class="flex flex-col gap-3">
      <div
        v-for="item in reminders"
        :key="item.id"
        class="glass-card rounded-2xl p-5 shadow-sm border border-white/40 border-l-4"
        :class="{
          'border-l-primary-container': item.status === 0,
          'border-l-tertiary': item.status === 1,
          'border-l-outline-variant': item.status === 2,
        }"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <p class="font-headline-md text-[18px] text-on-surface truncate">{{ item.title }}</p>
              <a-badge
                :status="getStatusBadge(item.status)"
                :text="getStatusText(item.status)"
                class="!text-[10px]"
              />
            </div>
            <div class="flex items-center gap-3 mt-2 flex-wrap">
              <span class="inline-flex items-center gap-1 font-label-sm text-on-surface-variant">
                <svg
                  class="w-3.5 h-3.5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                  <line x1="16" y1="2" x2="16" y2="6" />
                  <line x1="8" y1="2" x2="8" y2="6" />
                  <line x1="3" y1="10" x2="21" y2="10" />
                </svg>
                {{ item.remindDate }}
              </span>
              <span class="inline-flex items-center gap-1 font-label-sm text-on-surface-variant">
                <svg
                  class="w-3.5 h-3.5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <polyline points="23 4 23 10 17 10" />
                  <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
                </svg>
                {{ getFrequencyName(item.frequency) }}
              </span>
              <span
                class="font-headline-md text-[16px]"
                :class="item.status === 2 ? 'text-on-surface-variant/40' : 'text-tertiary'"
              >
                ¥{{ item.amount }}
              </span>
            </div>
            <p v-if="item.remark" class="font-body-md text-on-surface-variant/60 text-sm mt-1.5 line-clamp-2">
              {{ item.remark }}
            </p>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-2 mt-3 pt-3 border-t border-surface-variant/50">
          <button
            @click="handleEdit(item)"
            class="flex-1 py-2 font-label-sm rounded-xl bg-primary-container/15 text-primary active:bg-primary-container/30 transition-colors"
          >
            编辑
          </button>
          <button
            v-if="item.status === 0"
            @click="handleMarkRead(item.id)"
            class="flex-1 py-2 font-label-sm rounded-xl bg-tertiary-container/15 text-tertiary active:bg-tertiary-container/30 transition-colors"
          >
            标记已提醒
          </button>
          <button
            v-if="item.status === 2"
            @click="handleReopen(item.id)"
            class="flex-1 py-2 font-label-sm rounded-xl bg-tertiary-container/15 text-tertiary active:bg-tertiary-container/30 transition-colors"
          >
            开启提醒
          </button>
          <button
            v-if="item.status !== 2"
            @click="handleClose(item.id)"
            class="flex-1 py-2 font-label-sm rounded-xl bg-surface-gray text-on-surface-variant active:bg-surface-container-high transition-colors"
          >
            关闭提醒
          </button>
          <button
            @click="confirmDelete(item.id)"
            class="flex-1 py-2 font-label-sm rounded-xl bg-error-container/50 text-danger-red active:bg-error-container transition-colors"
          >
            删除
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="reminders.length === 0 && !loading" class="py-16 text-center">
        <BellOutlined class="text-4xl text-on-surface-variant/30" />
        <p class="font-body-md text-on-surface-variant mt-3">暂无提醒</p>
        <button @click="showAddModal" class="mt-3 font-label-md text-primary">
          创建第一个提醒
        </button>
      </div>
    </div>

    <!-- 新建/编辑弹窗 -->
    <a-modal
      :open="modalVisible"
      :title="editingId ? '编辑提醒' : '新建提醒'"
      :footer="null"
      :width="360"
      centered
      @cancel="modalVisible = false"
    >
      <a-form :model="formState" layout="vertical" class="mt-2" @finish="handleSubmit">
        <a-form-item
          label="提醒标题"
          name="title"
          :rules="[{ required: true, message: '请输入标题' }]"
        >
          <a-input
            v-model:value="formState.title"
            placeholder="如：信用卡还款、房租缴纳等"
            size="large"
          />
        </a-form-item>
        <a-form-item
          label="提醒金额"
          name="amount"
          :rules="[{ required: true, message: '请输入金额' }]"
        >
          <a-input-number
            v-model:value="formState.amount"
            :min="0"
            :precision="2"
            style="width: 100%"
            prefix="¥"
            size="large"
          />
        </a-form-item>
        <a-form-item
          label="首次提醒日期"
          name="remindDate"
          :rules="[{ required: true, message: '请选择日期' }]"
        >
          <a-date-picker v-model:value="formState.remindDate" style="width: 100%" size="large" />
        </a-form-item>
        <a-form-item label="重复频率">
          <a-select v-model:value="formState.frequency" size="large">
            <a-select-option :value="1">一次性</a-select-option>
            <a-select-option :value="2">每天</a-select-option>
            <a-select-option :value="3">每周</a-select-option>
            <a-select-option :value="4">每月</a-select-option>
            <a-select-option :value="5">每年</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="formState.remark" placeholder="可选备注信息" :rows="2" />
        </a-form-item>
        <div class="flex gap-3 mt-2">
          <a-button block size="large" @click="modalVisible = false">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="submitting"
            >{{ editingId ? '保存修改' : '创建' }}</a-button
          >
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { BellOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { reminderApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)

const loading = ref(false)
const reminders = ref([])
const statusFilter = ref('')
const modalVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)

const statusFilters = [
  { label: '全部', value: '' },
  { label: '待提醒', value: '0' },
  { label: '已提醒', value: '1' },
  { label: '已关闭', value: '2' },
]

const formState = reactive({
  title: '',
  amount: null,
  remindDate: null,
  frequency: 1,
  remark: '',
})

function getStatusBadge(status) {
  return { 0: 'warning', 1: 'processing', 2: 'default' }[status] || 'default'
}
function getStatusText(status) {
  return { 0: '待提醒', 1: '已提醒', 2: '已关闭' }[status] || '未知'
}
function getFrequencyName(freq) {
  return { 1: '一次性', 2: '每天', 3: '每周', 4: '每月', 5: '每年' }[freq] || '未知'
}

// 切换状态筛选
function setStatusFilter(value) {
  statusFilter.value = value
  loadData()
}

async function loadData() {
  if (!userId.value) return
  loading.value = true
  try {
    const params = { userId: userId.value, current: 1, size: 50 }
    if (statusFilter.value !== '') params.status = Number(statusFilter.value)
    const res = await reminderApi.getPage(params)
    reminders.value = res.data.records
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  editingId.value = null
  Object.assign(formState, { title: '', amount: null, remindDate: null, frequency: 1, remark: '' })
  modalVisible.value = true
}

function handleEdit(item) {
  editingId.value = item.id
  Object.assign(formState, {
    title: item.title,
    amount: item.amount,
    remindDate: item.remindDate ? dayjs(item.remindDate) : null,
    frequency: item.frequency,
    remark: item.remark || '',
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    const data = {
      ...formState,
      remindDate: formState.remindDate?.format('YYYY-MM-DD'),
      userId: userId.value,
    }
    if (editingId.value) {
      await reminderApi.update(editingId.value, data)
      message.success('修改成功')
    } else {
      await reminderApi.save(data)
      message.success('创建成功')
    }
    modalVisible.value = false
    loadData()
  } catch (error) {
    console.error(editingId.value ? '修改失败:' : '创建失败:', error)
  } finally {
    submitting.value = false
  }
}

async function handleMarkRead(id) {
  try {
    await reminderApi.markRead(id)
    message.success('标记成功')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function handleClose(id) {
  try {
    await reminderApi.close(id)
    message.success('关闭成功')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function handleReopen(id) {
  try {
    await reminderApi.reopen(id)
    message.success('已重新开启提醒')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function confirmDelete(id) {
  try {
    await reminderApi.delete(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
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

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.sunset-gradient {
  background: linear-gradient(135deg, #ab3500 0%, #fe9824 100%);
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
