<template>
  <div class="px-4 pt-4 pb-6 space-y-4">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between px-1">
      <h2 class="text-[22px] font-bold text-[#1c1c1e]">我的提醒</h2>
      <button
        @click="showAddModal"
        class="flex items-center gap-1 px-3 py-1.5 bg-[#ff6b35] text-white text-xs font-semibold rounded-full active:scale-95 transition-transform shadow-sm shadow-orange-500/20"
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
        class="shrink-0 px-3.5 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all duration-200"
        :class="
          statusFilter === s.value
            ? 'bg-[#ff6b35] text-white shadow-sm'
            : 'bg-white text-[#8e8e93] active:bg-[#f2f2f7]'
        "
      >
        {{ s.label }}
      </button>
    </div>

    <!-- 提醒列表 -->
    <div class="space-y-3">
      <div
        v-for="item in reminders"
        :key="item.id"
        class="bg-white rounded-2xl p-4 shadow-sm border-l-4"
        :class="{
          'border-l-orange-400': item.status === 0,
          'border-l-blue-400': item.status === 1,
          'border-l-gray-300': item.status === 2,
        }"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <p class="text-[15px] font-semibold text-[#1c1c1e] truncate">{{ item.title }}</p>
              <a-badge
                :status="getStatusBadge(item.status)"
                :text="getStatusText(item.status)"
                class="!text-[10px]"
              />
            </div>
            <div class="flex items-center gap-3 mt-2 flex-wrap">
              <span class="inline-flex items-center gap-1 text-xs text-[#8e8e93]">
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
              <span class="inline-flex items-center gap-1 text-xs text-[#8e8e93]">
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
                class="font-bold text-sm"
                :class="item.status === 2 ? 'text-[#c7c7cc]' : 'text-[#007AFF]'"
              >
                ¥{{ item.amount }}
              </span>
            </div>
            <p v-if="item.remark" class="text-xs text-[#aeaeb2] mt-1.5 line-clamp-2">
              {{ item.remark }}
            </p>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-2 mt-3 pt-3 border-t border-black/[0.05]">
          <button
            v-if="item.status === 0"
            @click="handleMarkRead(item.id)"
            class="flex-1 py-2 text-xs font-medium rounded-lg bg-blue-50 text-blue-600 active:bg-blue-100 transition-colors"
          >
            标记已提醒
          </button>
          <button
            v-if="item.status !== 2"
            @click="handleClose(item.id)"
            class="flex-1 py-2 text-xs font-medium rounded-lg bg-[#f2f2f7] text-[#8e8e93] active:bg-[#e5e5ea] transition-colors"
          >
            关闭提醒
          </button>
          <button
            @click="confirmDelete(item.id)"
            class="flex-1 py-2 text-xs font-medium rounded-lg bg-red-50 text-red-500 active:bg-red-100 transition-colors"
          >
            删除
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="reminders.length === 0 && !loading" class="py-16 text-center">
        <BellOutlined class="text-4xl text-[#c7c7cc]" />
        <p class="text-sm text-[#8e8e93] mt-3">暂无提醒</p>
        <button @click="showAddModal" class="mt-3 text-sm text-[#ff6b35] font-medium">
          创建第一个提醒
        </button>
      </div>
    </div>

    <!-- 新建弹窗 -->
    <a-modal
      :open="modalVisible"
      title="新建提醒"
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
            >创建</a-button
          >
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { BellOutlined } from '@ant-design/icons-vue'
import { reminderApi } from '@/api'

const loading = ref(false)
const reminders = ref([])
const statusFilter = ref('')
const modalVisible = ref(false)
const submitting = ref(false)

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
  loading.value = true
  try {
    const params = { userId: 1, current: 1, size: 50 }
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
  Object.assign(formState, { title: '', amount: null, remindDate: null, frequency: 1, remark: '' })
  modalVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    await reminderApi.save({
      ...formState,
      remindDate: formState.remindDate?.format('YYYY-MM-DD'),
      userId: 1,
    })
    message.success('创建成功')
    modalVisible.value = false
    loadData()
  } catch (error) {
    console.error('创建失败:', error)
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

async function confirmDelete(id) {
  try {
    await reminderApi.delete(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => loadData())
</script>

<style scoped>
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
