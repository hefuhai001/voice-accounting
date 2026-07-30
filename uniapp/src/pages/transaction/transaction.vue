<template>
  <view class="min-h-screen pb-32 px-4 flex flex-col gap-8 overflow-x-hidden">
    <CustomHeader />

    <!-- Voice Hero Section -->
    <view class="mt-8">
      <view class="relative flex flex-col items-center justify-center py-12">
        <!-- Decorative Background -->
        <view class="absolute inset-0 flex items-center justify-center">
          <view class="w-48 h-48 rounded-full bg-primary/5" style="filter: blur(60px);"></view>
        </view>

        <!-- Main Voice Button -->
        <view
          class="relative z-10 w-32 h-32 rounded-full bg-primary flex items-center justify-center shadow-2xl shadow-primary/40 transition-all duration-300"
          :class="isRecording ? 'recording-pulse' : 'active:scale-90'"
          @click="toggleRecording"
        >
          <!-- Pulse Rings -->
          <view v-if="isRecording" class="absolute inset-0 rounded-full border-4 border-primary/30 ping-ring"></view>
          <view v-if="isRecording" class="absolute inset-4 rounded-full border-2 border-white/20 ping-ring" style="animation-delay: 0.5s"></view>
          <!-- Mic Icon -->
          <text class="material-symbols-outlined text-white" style="font-size: 48px; font-variation-settings: 'FILL' 1;">mic</text>
        </view>

        <!-- Status Text -->
        <view class="mt-6 text-center">
          <text class="font-headline-md text-headline-md text-on-surface font-bold block">
            {{ isRecording ? '正在录音...' : isRecognizing ? 'AI分析中...' : '语音记账' }}
          </text>
          <text class="text-on-surface-variant font-medium mt-1 block">
            {{ isRecording ? '请说出您的消费内容' : '点击按钮开始录音' }}
          </text>
        </view>

        <!-- AI Result -->
        <view v-if="aiResult" class="mt-6 w-full glass-panel rounded-3xl p-5">
          <view class="flex items-start gap-3">
            <text class="material-symbols-outlined text-tertiary text-2xl">auto_awesome</text>
            <view class="flex-1">
              <text class="font-label-sm text-label-sm text-tertiary uppercase font-bold mb-2 block">AI识别结果</text>
              <text class="text-on-surface block" style="white-space: pre-wrap;">{{ aiResult }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- Manual Input Section -->
    <view class="glass-panel rounded-3xl p-6 shadow-lg">
      <!-- Header -->
      <view class="flex items-center gap-3 mb-6">
        <text class="material-symbols-outlined text-on-surface-variant text-2xl">edit</text>
        <text class="font-headline-md text-headline-md text-on-surface font-bold">手动记账</text>
      </view>

      <!-- Tabs -->
      <view class="bg-surface-container-low rounded-2xl p-1.5 flex mb-6 relative">
        <view
          class="absolute bg-primary rounded-xl shadow-lg shadow-primary/20 transition-all duration-300"
          :style="{ top: '6px', bottom: '6px', width: 'calc(50% - 6px)', left: formState.type === 1 ? '6px' : 'calc(50% + 1.5px)' }"
        ></view>
        <view
          class="flex-1 py-3 text-center z-10 font-bold transition-colors"
          @click="formState.type = 1"
        >
          <text class="font-bold" :class="formState.type === 1 ? 'text-white' : 'text-on-surface-variant'">支出</text>
        </view>
        <view
          class="flex-1 py-3 text-center z-10 font-bold transition-colors"
          @click="formState.type = 2"
        >
          <text class="font-bold" :class="formState.type === 2 ? 'text-white' : 'text-on-surface-variant'">收入</text>
        </view>
      </view>

      <!-- Amount Display -->
      <view class="text-center mb-8 py-6 bg-surface-container-low rounded-2xl relative">
        <text class="absolute left-6 font-headline-lg text-headline-lg text-on-surface-variant/40" style="top: 50%; transform: translateY(-50%);">¥</text>
        <input
          v-model="formState.amount"
          type="digit"
          placeholder="0.00"
          class="w-full text-center font-display-lg text-display-lg bg-transparent text-on-surface tracking-tighter placeholder:text-on-surface-variant/30 box-border"
          style="font-variant-numeric: tabular-nums; border: none; outline: none;"
        />
      </view>

      <!-- Detail Rows -->
      <view>
        <!-- Category -->
        <view class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <view class="flex items-center gap-3">
            <text class="material-symbols-outlined text-on-surface-variant">category</text>
            <text class="font-body-md text-on-surface-variant">分类</text>
          </view>
          <picker :range="categoryList" range-key="name" @change="onCategoryChange">
            <view class="flex items-center">
              <text class="text-sm" :class="selectedCategoryName ? 'text-on-surface' : 'text-on-surface-variant'">
                {{ selectedCategoryName || '选择分类' }}
              </text>
              <text class="material-symbols-outlined text-on-surface-variant text-lg ml-1">chevron_right</text>
            </view>
          </picker>
        </view>

        <!-- Book -->
        <view class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <view class="flex items-center gap-3">
            <text class="material-symbols-outlined text-on-surface-variant">account_balance_wallet</text>
            <text class="font-body-md text-on-surface-variant">账本</text>
          </view>
          <picker :range="bookList" range-key="name" @change="onBookChange">
            <view class="flex items-center">
              <text class="text-sm" :class="selectedBookName ? 'text-on-surface' : 'text-on-surface-variant'">
                {{ selectedBookName || '选择账本' }}
              </text>
              <text class="material-symbols-outlined text-on-surface-variant text-lg ml-1">chevron_right</text>
            </view>
          </picker>
        </view>

        <!-- Date -->
        <view class="flex justify-between items-center py-4 border-b border-outline-variant/30">
          <view class="flex items-center gap-3">
            <text class="material-symbols-outlined text-on-surface-variant">calendar_today</text>
            <text class="font-body-md text-on-surface-variant">日期</text>
          </view>
          <picker mode="date" :value="formState.transactionDate" @change="onDateChange">
            <view class="flex items-center">
              <text class="text-on-surface text-sm">{{ formState.transactionDate }}</text>
              <text class="material-symbols-outlined text-on-surface-variant text-lg ml-1">chevron_right</text>
            </view>
          </picker>
        </view>

        <!-- Remark -->
        <view class="flex items-center py-4 gap-3">
          <text class="material-symbols-outlined text-on-surface-variant">edit_note</text>
          <input
            v-model="formState.remark"
            type="text"
            placeholder="添加备注..."
            class="flex-1 text-sm text-on-surface bg-transparent"
            style="border: none; outline: none; background: transparent;"
          />
        </view>
      </view>
    </view>

    <!-- Submit Button -->
    <view
      class="w-full bg-primary text-white py-4 rounded-3xl font-headline-md shadow-xl shadow-primary/30 active:scale-95 transition-transform flex items-center justify-center gap-2"
      :class="submitting ? 'opacity-50' : ''"
      @click="handleSubmit"
    >
      <text class="material-symbols-outlined text-white">save</text>
      <text class="text-white font-bold">{{ submitting ? '保存中...' : '保存记账' }}</text>
    </view>

    <CustomTabBar current="/pages/transaction/transaction" />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { transactionApi } from '@/api/user/transaction'
import { categoryApi } from '@/api/user/category'
import { bookApi } from '@/api/user/book'
import { voiceApi } from '@/api/user/voice'
import { useAuthStore } from '@/stores/auth'
import CustomTabBar from '@/components/CustomTabBar.vue'
import CustomHeader from '@/components/CustomHeader.vue'

const authStore = useAuthStore()

const isRecording = ref(false)
const isRecognizing = ref(false)
const aiResult = ref('')
const submitting = ref(false)

const categoryList = ref([])
const bookList = ref([])

function getToday() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const formState = reactive({
  type: 1,
  amount: '',
  categoryId: undefined,
  bookId: undefined,
  transactionDate: getToday(),
  remark: '',
})

const selectedCategoryName = computed(() => {
  if (!formState.categoryId) return ''
  const cat = categoryList.value.find(c => c.id === formState.categoryId)
  return cat ? cat.name : ''
})

const selectedBookName = computed(() => {
  if (!formState.bookId) return ''
  const book = bookList.value.find(b => b.id === formState.bookId)
  return book ? book.name : ''
})

// H5录音：使用MediaRecorder API
let mediaRecorder = null
let audioChunks = []

async function toggleRecording() {
  if (isRecording.value) {
    // 停止录音
    if (mediaRecorder && mediaRecorder.state === 'recording') {
      mediaRecorder.stop()
    }
    isRecording.value = false
    return
  }

  // 开始录音
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    audioChunks = []
    mediaRecorder = new MediaRecorder(stream)

    mediaRecorder.ondataavailable = (e) => {
      audioChunks.push(e.data)
    }

    mediaRecorder.onstop = async () => {
      stream.getTracks().forEach(t => t.stop())
      const blob = new Blob(audioChunks, { type: 'audio/wav' })
      await handleVoiceResult(blob)
    }

    mediaRecorder.start()
    isRecording.value = true
    aiResult.value = ''
  } catch (e) {
    console.error('录音启动失败:', e)
    uni.showToast({ title: '无法启动录音，请检查权限', icon: 'none' })
  }
}

async function handleVoiceResult(blob) {
  isRecognizing.value = true
  aiResult.value = ''
  try {
    // H5: 用FormData上传blob
    const authStore = useAuthStore()
    const baseUrl = import.meta.env.DEV ? '' : 'https://www.hfh.asia'
    const formData = new FormData()
    formData.append('file', blob, 'recording.wav')

    const res = await fetch(`${baseUrl}/api/voice/bookkeep`, {
      method: 'POST',
      headers: { 'Authorization': authStore.accessToken },
      body: formData,
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      const result = data.data
      aiResult.value = typeof result === 'string' ? result : JSON.stringify(result)
      if (result.type) formState.type = result.type
      if (result.amount) formState.amount = String(result.amount)
      if (result.categoryId) formState.categoryId = result.categoryId
      if (result.remark) formState.remark = result.remark
    }
  } catch (e) {
    console.error('语音识别失败:', e)
    uni.showToast({ title: '识别失败，请重试', icon: 'none' })
  } finally {
    isRecognizing.value = false
  }
}

function onCategoryChange(e) {
  const idx = e.detail.value
  formState.categoryId = categoryList.value[idx]?.id
}

function onBookChange(e) {
  const idx = e.detail.value
  formState.bookId = bookList.value[idx]?.id
}

function onDateChange(e) {
  formState.transactionDate = e.detail.value
}

async function handleSubmit() {
  if (!formState.amount || Number(formState.amount) <= 0) {
    uni.showToast({ title: '请输入金额', icon: 'none' })
    return
  }
  if (!formState.categoryId) {
    uni.showToast({ title: '请选择分类', icon: 'none' })
    return
  }
  if (!formState.bookId) {
    uni.showToast({ title: '请选择账本', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await transactionApi.save({
      type: formState.type,
      amount: Number(formState.amount),
      categoryId: formState.categoryId,
      bookId: formState.bookId,
      transactionDate: formState.transactionDate,
      remark: formState.remark,
    })
    uni.showToast({ title: '记账成功', icon: 'success' })
    formState.amount = ''
    formState.categoryId = undefined
    formState.remark = ''
    aiResult.value = ''
  } catch (e) {
    console.error('保存失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

async function loadCategories() {
  try {
    const userId = authStore.userInfo?.id
    const apiFn = formState.type === 1 ? categoryApi.getExpenseList : categoryApi.getIncomeList
    const res = await apiFn(userId)
    if (res?.data) {
      categoryList.value = res.data
    }
  } catch (e) {
    console.error('加载分类失败:', e)
  }
}

async function loadBooks() {
  try {
    const userId = authStore.userInfo?.id
    const [pageRes, defaultRes] = await Promise.all([
      bookApi.getPage({ userId, current: 1, size: 100 }),
      bookApi.getDefault(userId).catch(() => null),
    ])
    if (pageRes?.data?.records) {
      bookList.value = pageRes.data.records
    }
    if (defaultRes?.data?.id) {
      formState.bookId = defaultRes.data.id
    } else if (bookList.value.length > 0) {
      formState.bookId = bookList.value[0].id
    }
  } catch (e) {
    console.error('加载账本失败:', e)
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
  loadCategories()
  loadBooks()
})
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  -webkit-font-smoothing: antialiased;
}
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
.recording-pulse {
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(255, 107, 53, 0.4); }
  70% { box-shadow: 0 0 0 20px rgba(255, 107, 53, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255, 107, 53, 0); }
}
.ping-ring {
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
}
@keyframes ping {
  0% { transform: scale(1); opacity: 1; }
  75% { transform: scale(1.5); opacity: 0; }
  100% { transform: scale(1.5); opacity: 0; }
}
</style>
