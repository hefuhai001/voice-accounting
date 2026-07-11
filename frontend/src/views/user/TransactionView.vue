<template>
  <div class="px-margin-mobile pb-32 flex flex-col gap-8 max-w-md mx-auto">
    <!-- Voice Hero Section -->
    <section class="mt-8">
      <div class="relative flex flex-col items-center justify-center py-12">
        <!-- Decorative Background -->
        <div class="absolute inset-0 flex items-center justify-center">
          <div class="w-48 h-48 rounded-full bg-primary/5 blur-3xl"></div>
        </div>

        <!-- Main Voice Button -->
        <button
          @click="toggleRecording"
          :disabled="isRecognizing"
          class="relative z-10 w-32 h-32 rounded-full bg-primary flex items-center justify-center shadow-2xl shadow-primary/40 transition-all duration-300 active:scale-90"
          :class="isRecording ? 'recording-pulse scale-110' : 'hover:scale-105'"
        >
          <!-- Pulse Rings -->
          <div v-if="isRecording" class="absolute inset-0 rounded-full border-4 border-primary/30 animate-ping"></div>
          <div v-if="isRecording" class="absolute inset-4 rounded-full border-2 border-white/20 animate-ping" style="animation-delay: 0.5s"></div>

          <!-- Mic Icon -->
          <span class="material-symbols-outlined text-white" style="font-size: 48px; font-variation-settings: 'FILL' 1;">
            mic
          </span>
        </button>

        <!-- Status Text -->
        <div class="mt-6 text-center">
          <h3 class="font-headline-md text-headline-md text-on-surface font-bold">
            {{ isRecording ? '正在录音...' : isRecognizing ? 'AI分析中...' : '语音记账' }}
          </h3>
          <p class="text-on-surface-variant font-medium mt-1">
            {{ isRecording ? '请说出您的消费内容' : '点击按钮开始录音' }}
          </p>
        </div>

        <!-- AI Result -->
        <Transition name="fade">
          <div v-if="aiResult" class="mt-6 w-full glass-panel rounded-3xl p-5">
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-tertiary text-2xl">auto_awesome</span>
              <div class="flex-1">
                <p class="font-label-sm text-label-sm text-tertiary uppercase font-bold mb-2">AI识别结果</p>
                <p class="text-on-surface whitespace-pre-wrap">{{ aiResult }}</p>
              </div>
            </div>
          </div>
        </Transition>
      </div>
    </section>

    <!-- Manual Input Section -->
    <section class="glass-panel rounded-3xl p-6 shadow-lg">
      <!-- Header -->
      <div class="flex items-center gap-3 mb-6">
        <span class="material-symbols-outlined text-on-surface-variant text-2xl">edit</span>
        <h3 class="font-headline-md text-headline-md text-on-surface font-bold">手动记账</h3>
      </div>

      <!-- Tabs -->
      <div class="bg-surface-container-low rounded-2xl p-1.5 flex mb-6 relative">
        <div
          class="absolute inset-y-1.5 w-[calc(50%-6px)] bg-primary rounded-xl shadow-lg shadow-primary/20 transition-all duration-300"
          :class="formState.type === 1 ? 'left-1.5' : 'left-[calc(50%+1.5px)]'"
        ></div>
        <button
          @click="formState.type = 1"
          class="flex-1 py-3 text-center z-10 font-bold transition-colors"
          :class="formState.type === 1 ? 'text-white' : 'text-on-surface-variant'"
        >
          支出
        </button>
        <button
          @click="formState.type = 2"
          class="flex-1 py-3 text-center z-10 font-bold transition-colors"
          :class="formState.type === 2 ? 'text-white' : 'text-on-surface-variant'"
        >
          收入
        </button>
      </div>

      <!-- Amount Display -->
      <div class="text-center mb-8 py-6 bg-surface-container-low rounded-2xl relative">
        <span class="absolute left-6 top-1/2 -translate-y-1/2 font-headline-lg text-headline-lg text-on-surface-variant/40">¥</span>
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
      <div class="space-y-1">
        <div class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <div class="flex items-center gap-3">
            <span class="material-symbols-outlined text-on-surface-variant">category</span>
            <span class="font-body-md text-on-surface-variant">分类</span>
          </div>
          <a-select
            v-model:value="formState.categoryId"
            placeholder="选择分类"
            class="!min-w-[140px]"
            size="large"
            :bordered="false"
            style="background: transparent"
          >
            <a-select-option v-for="item in categories" :key="item.id" :value="item.id">
              <span class="material-symbols-outlined text-[16px] mr-2">{{ getCategoryIcon(item.name) }}</span>
              {{ item.name }}
            </a-select-option>
          </a-select>
        </div>

        <div class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <div class="flex items-center gap-3">
            <span class="material-symbols-outlined text-on-surface-variant">account_balance_wallet</span>
            <span class="font-body-md text-on-surface-variant">账本</span>
          </div>
          <a-select
            v-model:value="formState.bookId"
            placeholder="选择账本"
            class="!min-w-[140px]"
            size="large"
            :bordered="false"
            style="background: transparent"
          >
            <a-select-option v-for="item in books" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </div>

        <div class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <div class="flex items-center gap-3">
            <span class="material-symbols-outlined text-on-surface-variant">calendar_today</span>
            <span class="font-body-md text-on-surface-variant">日期</span>
          </div>
          <a-date-picker
            v-model:value="formState.transactionDate"
            size="large"
            :bordered="false"
            style="background: transparent"
            value-format="YYYY-MM-DD"
          />
        </div>

        <div class="flex items-center py-4 gap-3">
          <span class="material-symbols-outlined text-on-surface-variant">edit_note</span>
          <a-input
            v-model:value="formState.remark"
            placeholder="添加备注..."
            :bordered="false"
            class="flex-1 !font-body-md !px-0"
            style="background: transparent"
          />
        </div>
      </div>
    </section>

    <!-- Submit Button -->
    <button
      @click="handleSubmit"
      :disabled="submitting"
      class="w-full bg-primary text-white py-4 rounded-3xl font-headline-md text-headline-md shadow-xl shadow-primary/30 active:scale-95 transition-transform disabled:opacity-50 flex items-center justify-center gap-2"
    >
      <span class="material-symbols-outlined">save</span>
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

function getCategoryIcon(name) {
  const map = {
    餐饮: 'restaurant',
    交通: 'directions_car',
    购物: 'local_mall',
    娱乐: 'movie',
    居住: 'home',
    医疗: 'medical_services',
    教育: 'school',
    工资: 'payments',
  }
  return map[name] || 'category'
}

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
  aiResult.value = ''
}

async function toggleRecording() {
  if (isRecording.value) {
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
    aiResult.value = ''
    await startRecording()
  }
}

watch(userId, (newUserId) => {
  if (newUserId) loadOptions()
})

onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadOptions()
})
</script>

<style scoped>
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.04);
}

.rounded-3xl {
  border-radius: 2.5rem;
}

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

.recording-pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(255, 107, 53, 0.4); }
  70% { box-shadow: 0 0 0 20px rgba(255, 107, 53, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255, 107, 53, 0); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
