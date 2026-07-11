<template>
  <div class="px-margin-mobile pt-20 pb-28 flex flex-col gap-6 max-w-md mx-auto">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="font-display-lg text-display-lg text-on-surface tracking-tight">我的提醒</h1>
        <p class="text-label-sm text-on-surface-variant uppercase tracking-widest mt-1">MY REMINDERS</p>
      </div>
      <button
        @click="showAddModal"
        class="flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white font-label-md rounded-full active:scale-95 transition-transform shadow-lg shadow-primary/30"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        新建
      </button>
    </div>

    <!-- 状态筛选 -->
    <div class="flex gap-3 overflow-x-auto pb-1 scrollbar-hide">
      <button
        v-for="s in statusFilters"
        :key="s.value"
        @click="setStatusFilter(s.value)"
        class="shrink-0 px-5 py-2 rounded-full font-label-md whitespace-nowrap transition-all duration-200"
        :class="
          statusFilter === s.value
            ? 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-lg shadow-primary/20'
            : 'bg-surface-container-low text-on-surface-variant hover:bg-surface-container'
        "
      >
        {{ s.label }}
      </button>
    </div>

    <!-- 提醒列表 -->
    <div class="flex flex-col gap-4">
      <div
        v-for="item in reminders"
        :key="item.id"
        class="glass-panel rounded-3xl p-5 shadow-sm relative overflow-hidden group hover:shadow-xl transition-all duration-300"
      >
        <!-- 装饰性光圈 -->
        <div
          class="absolute -right-10 -bottom-10 w-32 h-32 rounded-full blur-3xl transition-colors"
          :class="{
            'bg-primary/10 group-hover:bg-primary/15': item.status === 0,
            'bg-tertiary/10 group-hover:bg-tertiary/15': item.status === 1,
            'bg-surface-variant/50': item.status === 2,
          }"
        ></div>

        <div class="relative z-10">
          <!-- 标题和状态 -->
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <div
                class="w-10 h-10 rounded-2xl flex items-center justify-center"
                :class="{
                  'bg-primary/10 text-primary': item.status === 0,
                  'bg-tertiary/10 text-tertiary': item.status === 1,
                  'bg-surface-container text-on-surface-variant': item.status === 2,
                }"
              >
                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">alarm</span>
              </div>
              <div>
                <h3 class="font-headline-md text-[18px] text-on-surface">{{ item.title }}</h3>
                <span
                  class="text-label-sm font-semibold"
                  :class="{
                    'text-primary': item.status === 0,
                    'text-tertiary': item.status === 1,
                    'text-on-surface-variant': item.status === 2,
                  }"
                >{{ getStatusText(item.status) }}</span>
              </div>
            </div>
            <div class="text-right">
              <p
                class="font-finance-xl text-finance-xl"
                :class="item.status === 2 ? 'text-on-surface-variant/40' : 'text-primary'"
              >
                ¥{{ Number(item.amount).toFixed(2) }}
              </p>
            </div>
          </div>

          <!-- 详情信息 -->
          <div class="flex items-center gap-4 text-label-sm text-on-surface-variant">
            <span class="inline-flex items-center gap-1">
              <span class="material-symbols-outlined text-[16px]">calendar_today</span>
              {{ item.remindDate }}
            </span>
            <span class="inline-flex items-center gap-1">
              <span class="material-symbols-outlined text-[16px]">repeat</span>
              {{ getFrequencyName(item.frequency) }}
            </span>
          </div>

          <!-- 备注 -->
          <p v-if="item.remark" class="text-body-md text-on-surface-variant/60 text-sm mt-2 line-clamp-2">
            {{ item.remark }}
          </p>

          <!-- 操作按钮 -->
          <div class="flex gap-2 mt-4 pt-4 border-t border-surface-variant/50">
            <button
              @click="handleEdit(item)"
              class="flex-1 py-2.5 font-label-md rounded-2xl bg-surface-container-low text-on-surface hover:bg-surface-container active:scale-95 transition-all flex items-center justify-center gap-1"
            >
              <span class="material-symbols-outlined text-[18px]">edit</span>
              编辑
            </button>
            <button
              v-if="item.status === 0"
              @click="handleMarkRead(item.id)"
              class="flex-1 py-2.5 font-label-md rounded-2xl bg-tertiary/10 text-tertiary hover:bg-tertiary/20 active:scale-95 transition-all flex items-center justify-center gap-1"
            >
              <span class="material-symbols-outlined text-[18px]">check_circle</span>
              标记
            </button>
            <button
              v-if="item.status === 2"
              @click="handleReopen(item.id)"
              class="flex-1 py-2.5 font-label-md rounded-2xl bg-primary/10 text-primary hover:bg-primary/20 active:scale-95 transition-all flex items-center justify-center gap-1"
            >
              <span class="material-symbols-outlined text-[18px]">play_circle</span>
              开启提醒
            </button>
            <button
              v-if="item.status !== 2"
              @click="handleClose(item.id)"
              class="flex-1 py-2.5 font-label-md rounded-2xl bg-surface-container-low text-on-surface-variant hover:bg-surface-container active:scale-95 transition-all flex items-center justify-center gap-1"
            >
              <span class="material-symbols-outlined text-[18px]">cancel</span>
              关闭
            </button>
            <button
              @click="confirmDelete(item.id)"
              class="flex-1 py-2.5 font-label-md rounded-2xl bg-error/10 text-error hover:bg-error/20 active:scale-95 transition-all flex items-center justify-center gap-1"
            >
              <span class="material-symbols-outlined text-[18px]">delete</span>
              删除
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="reminders.length === 0 && !loading" class="py-20 text-center">
        <div class="w-20 h-20 rounded-full bg-surface-container-low mx-auto flex items-center justify-center mb-4">
          <span class="material-symbols-outlined text-4xl text-on-surface-variant/40">alarm</span>
        </div>
        <p class="font-headline-md text-on-surface-variant">暂无提醒</p>
        <p class="text-body-md text-on-surface-variant/60 mt-1">点击上方按钮创建第一个提醒</p>
      </div>
    </div>

    <!-- 新建/编辑弹窗 -->
    <a-modal
      :open="modalVisible"
      :title="editingId ? '编辑提醒' : '新建提醒'"
      :footer="null"
      :width="380"
      centered
      @cancel="modalVisible = false"
    >
      <a-form :model="formState" layout="vertical" class="mt-4" @finish="handleSubmit">
        <a-form-item
          label="提醒标题"
          name="title"
          :rules="[{ required: true, message: '请输入标题' }]"
        >
          <a-input
            v-model:value="formState.title"
            placeholder="如：信用卡还款、房租缴纳等"
            size="large"
            class="modal-input"
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
        <div class="flex gap-3 mt-4">
          <a-button block size="large" @click="modalVisible = false">取消</a-button>
          <a-button
            type="primary"
            block
            size="large"
            html-type="submit"
            :loading="submitting"
            class="!bg-gradient-to-r !from-[#983f19] !to-[#ab3500] !border-none"
          >
            {{ editingId ? '保存修改' : '创建' }}
          </a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
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
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-panel:hover {
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
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

/* Modal input styles */
.modal-input :deep(.ant-input),
.modal-input :deep(.ant-input-affix-wrapper) {
  border-radius: 1rem !important;
  border-color: #e4e2e4 !important;
}

.modal-input :deep(.ant-input:focus),
.modal-input :deep(.ant-input-affix-wrapper-focused) {
  border-color: #983f19 !important;
  box-shadow: 0 0 0 2px rgba(152, 63, 25, 0.1) !important;
}
</style>
