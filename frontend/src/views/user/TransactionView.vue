<template>
  <div class="px-4 pt-4 pb-6 space-y-5">
    <!-- 金额输入区 -->
    <section class="bg-white rounded-2xl p-5 shadow-sm">
      <!-- 类型切换 -->
      <div class="flex bg-[#f2f2f7] rounded-xl p-1 mb-5">
        <button
          v-for="t in [
            { value: 1, label: '支出' },
            { value: 2, label: '收入' },
          ]"
          :key="t.value"
          @click="formState.type = t.value"
          class="flex-1 py-2 text-sm font-semibold rounded-lg transition-all duration-200"
          :class="
            formState.type === t.value ? 'bg-white text-[#1c1c1e] shadow-sm' : 'text-[#8e8e93]'
          "
        >
          {{ t.label }}
        </button>
      </div>

      <!-- 金额大字显示 -->
      <div class="text-center py-4">
        <span class="text-3xl font-light text-[#8e8e93]">¥</span>
        <input
          v-model.number="amountDisplay"
          type="number"
          inputmode="decimal"
          placeholder="0.00"
          class="text-4xl font-bold text-[#1c1c1e] bg-transparent outline-none text-center w-48 tracking-tight placeholder:text-[#c7c7cc]"
          style="font-variant-numeric: tabular-nums"
        />
      </div>

      <!-- 表单字段 -->
      <div class="space-y-3 mt-4">
        <div class="flex items-center justify-between py-3 border-b border-black/5">
          <span class="text-sm text-[#8e8e93]">分类</span>
          <a-select
            v-model:value="formState.categoryId"
            placeholder="选择分类"
            class="!min-w-[140px]"
            size="large"
            :bordered="false"
            style="background: transparent"
          >
            <a-select-option v-for="item in categories" :key="item.id" :value="item.id">
              {{ item.icon }} {{ item.name }}
            </a-select-option>
          </a-select>
        </div>
        <div class="flex items-center justify-between py-3 border-b border-black/5">
          <span class="text-sm text-[#8e8e93]">账本</span>
          <a-select
            v-model:value="formState.bookId"
            placeholder="选择账本"
            class="!min-w-[140px]"
            size="large"
            :bordered="false"
            style="background: transparent"
          >
            <a-select-option v-for="item in books" :key="item.id" :value="item.id">{{
              item.name
            }}</a-select-option>
          </a-select>
        </div>
        <div class="flex items-center justify-between py-3 border-b border-black/5">
          <span class="text-sm text-[#8e8e93]">日期</span>
          <a-date-picker
            v-model:value="formState.transactionDate"
            size="large"
            :bordered="false"
            style="background: transparent"
          />
        </div>
        <div class="py-3">
          <a-input
            v-model:value="formState.remark"
            placeholder="添加备注..."
            :bordered="false"
            class="!text-[15px] !px-0"
            style="background: transparent"
          />
        </div>
      </div>
    </section>

    <!-- 语音记账 -->
    <section class="bg-white rounded-2xl p-5 shadow-sm">
      <div class="flex items-center gap-2 mb-3">
        <div
          class="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-400 to-indigo-500 flex items-center justify-center"
        >
          <svg
            class="w-4 h-4 text-white"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z"
            />
          </svg>
        </div>
        <span class="text-sm font-semibold text-[#1c1c1e]">语音记账</span>
      </div>
      <p class="text-xs text-[#8e8e93] mb-4">点击开始录音，说出消费内容，AI自动识别并完成记账</p>
      <button
        @click="toggleRecording"
        :disabled="isRecognizing"
        class="w-full py-3.5 rounded-xl text-sm font-semibold transition-all duration-200 flex items-center justify-center gap-2"
        :class="
          isRecording
            ? 'bg-red-500 text-white animate-pulse'
            : isRecognizing
              ? 'bg-violet-100 text-violet-500'
              : 'bg-[#f2f2f7] text-[#1c1c1e] active:bg-[#e5e5ea]'
        "
      >
        <div class="w-3 h-3 rounded-full" :class="isRecording ? 'bg-white' : isRecognizing ? 'bg-violet-500 animate-pulse' : 'bg-red-500'" />
        {{ isRecording ? '点击停止，AI自动记账' : isRecognizing ? 'AI分析记账中...' : '开始录音' }}
      </button>
      <!-- AI记账结果 -->
      <div v-if="aiResult" class="mt-3 p-3 bg-violet-50 rounded-xl text-xs text-violet-700 whitespace-pre-wrap">
        {{ aiResult }}
      </div>
    </section>

    <!-- 保存按钮 -->
    <button
      @click="handleSubmit"
      :disabled="submitting"
      class="w-full py-3.5 rounded-2xl text-white text-base font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50"
      :class="
        formState.type === 1
          ? 'bg-gradient-to-r from-[#ff6b35] to-[#f7931e] shadow-orange-500/30'
          : 'bg-gradient-to-r from-emerald-500 to-green-400 shadow-emerald-500/30'
      "
    >
      {{ submitting ? '保存中...' : '保存记账' }}
    </button>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { transactionApi } from '@/api'
import { categoryApi } from '@/api'
import { bookApi } from '@/api'
import { voiceApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { useVoiceRecorder } from '@/composables/useVoiceRecorder'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)

const submitting = ref(false)
const aiResult = ref('')
const { isRecording, isRecognizing, startRecording, stopRecording } = useVoiceRecorder()

const formState = reactive({
  type: 1,
  amount: null,
  transactionDate: dayjs(),
  categoryId: undefined,
  bookId: undefined,
  remark: '',
})

const amountDisplay = computed({
  get: () => formState.amount ?? null,
  set: (val) => {
    formState.amount = val === '' ? null : Number(val)
  },
})

const categories = ref([])
const books = ref([])

async function loadOptions() {
  if (!userId.value) return
  try {
    const [catRes, bookRes] = await Promise.all([
      categoryApi.getList(userId.value),
      bookApi.getPage({ userId: userId.value, current: 1, size: 100 }),
    ])
    categories.value = catRes.data
    books.value = bookRes.data.records
    if (bookRes.data.records.length > 0 && !formState.bookId) {
      const defaultBook = bookRes.data.records.find((b) => b.isDefault === 1)
      formState.bookId = defaultBook?.id || bookRes.data.records[0].id
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

async function handleSubmit() {
  if (!formState.amount) {
    message.warning('请输入金额')
    return
  }
  if (!formState.categoryId) {
    message.warning('请选择分类')
    return
  }
  if (!formState.bookId) {
    message.warning('请选择账本')
    return
  }

  submitting.value = true
  try {
    await transactionApi.save({
      bookId: formState.bookId,
      categoryId: formState.categoryId,
      type: formState.type,
      amount: formState.amount,
      remark: formState.remark,
      transactionDate: formState.transactionDate?.format('YYYY-MM-DD'),
    })
    message.success('记账成功')
    handleReset()
  } catch (error) {
    console.error('记账失败:', error)
  } finally {
    submitting.value = false
  }
}

function handleReset() {
  Object.assign(formState, {
    type: 1,
    amount: null,
    transactionDate: dayjs(),
    categoryId: undefined,
    remark: '',
  })
}

async function toggleRecording() {
  if (isRecording.value) {
    // 停止录音，获取WAV数据，调用语音智能记账接口
    const wavBlob = await stopRecording()
    if (!wavBlob) {
      message.warning('未录制到音频')
      return
    }

    isRecognizing.value = true
    aiResult.value = ''
    try {
      const res = await voiceApi.bookkeep(wavBlob)
      const { text, result } = res.data
      aiResult.value = `识别: ${text}\n${result}`
      message.success('AI记账完成')
    } catch (error) {
      console.error('语音记账失败:', error)
      message.error('语音记账失败')
    } finally {
      isRecognizing.value = false
    }
  } else {
    // 开始录音
    aiResult.value = ''
    await startRecording()
  }
}

// 监听 userId 变化
watch(userId, (newUserId) => {
  if (newUserId) loadOptions()
})

// 页面加载时检查登录状态
onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadOptions()
})
</script>
