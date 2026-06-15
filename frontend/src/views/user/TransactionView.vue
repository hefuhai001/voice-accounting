<template>
  <div class="px-margin-mobile pt-20 pb-32 space-y-stack-lg max-w-md mx-auto">
    <!-- Transaction Input Section -->
    <section class="glass-card rounded-[2rem] p-gutter-md shadow-sm border border-white/40">
      <!-- Tabs -->
      <div class="flex bg-surface-container-low rounded-xl p-1 mb-8">
        <button
          v-for="t in [
            { value: 1, label: '支出' },
            { value: 2, label: '收入' },
          ]"
          :key="t.value"
          @click="formState.type = t.value"
          class="flex-1 py-2 rounded-lg font-label-md transition-all"
          :class="
            formState.type === t.value
              ? 'bg-white shadow-sm text-on-surface'
              : 'text-on-surface-variant hover:bg-surface-variant/50'
          "
        >
          {{ t.label }}
        </button>
      </div>

      <!-- Amount Display -->
      <div class="text-center mb-8 relative">
        <span class="absolute left-4 top-1/2 -translate-y-1/2 font-headline-lg text-headline-lg text-on-surface-variant/40">¥</span>
        <input
          v-model.number="amountDisplay"
          type="number"
          inputmode="decimal"
          placeholder="0.00"
          class="w-full text-center font-display-lg text-display-lg bg-transparent border-none focus:ring-0 text-on-surface tracking-tighter placeholder:text-on-surface-variant/30"
          style="font-variant-numeric: tabular-nums"
        />
      </div>

      <!-- Detail Rows -->
      <div class="space-y-4">
        <div class="flex justify-between items-center py-3 border-b border-surface-variant/50">
          <span class="font-body-md text-on-surface-variant">分类</span>
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
        <div class="flex justify-between items-center py-3 border-b border-surface-variant/50">
          <span class="font-body-md text-on-surface-variant">账本</span>
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
        <div class="flex justify-between items-center py-3 border-b border-surface-variant/50">
          <span class="font-body-md text-on-surface-variant">日期</span>
          <a-date-picker
            v-model:value="formState.transactionDate"
            size="large"
            :bordered="false"
            style="background: transparent"
          />
        </div>
        <div class="pt-2">
          <a-input
            v-model:value="formState.remark"
            placeholder="添加备注..."
            :bordered="false"
            class="!font-body-md !px-0"
            style="background: transparent"
          />
        </div>
      </div>
    </section>

    <!-- Voice Recognition Section -->
    <section class="glass-card rounded-[2rem] p-gutter-md shadow-sm border border-white/40 space-y-4">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-tertiary-container/20 flex items-center justify-center text-tertiary">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
          </svg>
        </div>
        <div>
          <h3 class="font-headline-md text-body-lg font-bold">语音记账</h3>
          <p class="text-label-sm text-on-surface-variant/60 font-medium">点击开始录音，说出消费内容</p>
        </div>
      </div>
      <button
        @click="toggleRecording"
        :disabled="isRecognizing"
        class="w-full bg-surface-container-low hover:bg-surface-container-high py-4 rounded-2xl flex items-center justify-center gap-3 transition-colors active:scale-95 duration-150"
        :class="isRecording ? 'recording-pulse' : ''"
      >
        <div
          class="w-2.5 h-2.5 rounded-full"
          :class="
            isRecording
              ? 'bg-success-green shadow-[0_0_10px_rgba(52,199,89,0.5)]'
              : isRecognizing
                ? 'bg-tertiary animate-pulse'
                : 'bg-danger-red shadow-[0_0_10px_rgba(255,59,48,0.5)]'
          "
        />
        <span class="font-label-md text-on-surface">
          {{ isRecording ? '正在识别...' : isRecognizing ? 'AI分析记账中...' : '开始录音' }}
        </span>
      </button>
      <!-- AI记账结果 -->
      <div v-if="aiResult" class="p-3 bg-tertiary-container/10 rounded-xl text-xs text-tertiary whitespace-pre-wrap">
        {{ aiResult }}
      </div>
    </section>

    <!-- Primary Action Button -->
    <div class="fixed bottom-24 left-margin-mobile right-margin-mobile max-w-md mx-auto">
      <button
        @click="handleSubmit"
        :disabled="submitting"
        class="w-full sunset-gradient text-white py-4 rounded-2xl font-headline-md text-headline-md shadow-xl shadow-primary/20 active:scale-95 transition-transform disabled:opacity-50"
      >
        {{ submitting ? '保存中...' : '保存记账' }}
      </button>
    </div>
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

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.sunset-gradient {
  background: linear-gradient(135deg, #ab3500 0%, #fe9824 100%);
}
.recording-pulse {
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(171, 53, 0, 0.4); }
  70% { transform: scale(1); box-shadow: 0 0 0 10px rgba(171, 53, 0, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(171, 53, 0, 0); }
}
</style>
