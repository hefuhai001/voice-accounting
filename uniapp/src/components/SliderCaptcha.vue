<template>
  <view v-if="visible" class="fixed inset-0 z-[999] flex items-center justify-center" @click.self="handleCancel">
    <view class="absolute inset-0 bg-black/40"></view>
    <view class="relative bg-white rounded-3xl p-5 z-10 w-[320px]" @click.stop>
      <text class="text-headline-md text-on-surface font-bold block mb-4">请完成安全验证</text>

      <!-- Image area -->
      <view class="relative w-[280px] h-[155px] mx-auto mb-3 rounded-lg overflow-hidden bg-surface-container">
        <view v-if="loading" class="absolute inset-0 flex items-center justify-center">
          <text class="text-primary text-2xl">⏳</text>
        </view>
        <template v-else>
          <image v-if="backgroundImage" :src="backgroundImage" class="w-[280px] h-[155px]" mode="aspectFill" />
          <image v-if="sliderImage" :src="sliderImage" class="absolute w-[50px] h-[50px]" :style="{ top: sliderY + 'px', left: sliderLeft + 'px' }" />
        </template>
      </view>

      <!-- Slider track -->
      <view
        class="relative w-[280px] h-10 mx-auto bg-surface-container-low rounded-full overflow-hidden mb-2"
        @touchmove.prevent="onDrag"
        @touchend="endDrag"
      >
        <view
          class="absolute left-0 top-0 h-full rounded-full"
          :style="{ width: sliderLeft + 'px', background: 'linear-gradient(to right, #ff6b3520, #ff6b3540)' }"
        ></view>
        <view
          class="absolute top-[2px] w-9 h-9 rounded-full flex items-center justify-center text-white z-10"
          :style="{ left: sliderLeft + 'px', background: 'linear-gradient(135deg, #ff6b35, #f7931e)', boxShadow: '0 2px 8px rgba(255,107,53,0.4)' }"
          @touchstart.prevent="startDrag"
          @touchmove.prevent="onDrag"
          @touchend="endDrag"
        >
          <text class="text-sm">▶▶</text>
        </view>
        <text v-if="!dragging" class="absolute w-full text-center leading-10 text-sm text-on-surface-variant/50">{{ dragHint }}</text>
      </view>

      <!-- Refresh -->
      <view class="text-right mt-2">
        <text class="text-xs text-on-surface-variant" @click="refreshCaptcha">🔄 换一张</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { captchaApi } from '@/api/user/auth'

const props = defineProps({
  visible: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const verifying = ref(false)
const backgroundImage = ref('')
const sliderImage = ref('')
const sliderY = ref(0)
const sliderLeft = ref(0)
const token = ref('')
const dragging = ref(false)
const startX = ref(0)
const dragHint = ref('向右滑动滑块填充拼图')

async function fetchCaptcha() {
  loading.value = true
  sliderLeft.value = 0
  dragHint.value = '向右滑动滑块填充拼图'
  try {
    const res = await captchaApi.getSliderCaptcha()
    token.value = res.data.token
    backgroundImage.value = res.data.backgroundImage
    sliderImage.value = res.data.sliderImage
    sliderY.value = res.data.sliderY
  } catch {
    dragHint.value = '加载失败，请重试'
  } finally {
    loading.value = false
  }
}

function refreshCaptcha() {
  fetchCaptcha()
}

function startDrag(e) {
  if (loading.value || verifying.value) return
  dragging.value = true
  const touch = e.touches[0]
  startX.value = touch.clientX - sliderLeft.value
  dragHint.value = ''
}

function onDrag(e) {
  if (!dragging.value) return
  const touch = e.touches[0]
  let newLeft = touch.clientX - startX.value
  newLeft = Math.max(0, Math.min(newLeft, 230))
  sliderLeft.value = newLeft
}

async function endDrag() {
  if (!dragging.value) return
  dragging.value = false
  if (sliderLeft.value < 10) {
    sliderLeft.value = 0
    dragHint.value = '向右滑动滑块填充拼图'
    return
  }
  verifying.value = true
  try {
    const res = await captchaApi.verifySlider({ token: token.value, position: Math.round(sliderLeft.value) })
    emit('success', res.data)
    handleCancel()
  } catch {
    sliderLeft.value = 0
    dragHint.value = '验证失败，请重试'
    setTimeout(() => fetchCaptcha(), 1000)
  } finally {
    verifying.value = false
  }
}

function handleCancel() {
  emit('update:visible', false)
}

watch(() => props.visible, (val) => {
  if (val) fetchCaptcha()
})
</script>
