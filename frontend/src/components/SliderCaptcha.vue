<template>
  <a-modal
    :open="visible"
    title="请完成安全验证"
    :footer="null"
    :width="320"
    :mask-closable="false"
    @cancel="handleCancel"
  >
    <div class="slider-captcha">
      <!-- 图片区域 -->
      <div class="captcha-image" ref="imageRef">
        <div v-if="loading" class="captcha-loading">
          <LoadingOutlined spin class="text-2xl text-[#ff6b35]" />
        </div>
        <template v-else>
          <img v-if="backgroundImage" :src="backgroundImage" class="bg-img" draggable="false" />
          <img
            v-if="sliderImage"
            :src="sliderImage"
            class="piece-img"
            :style="{ top: sliderY + 'px', left: sliderLeft + 'px' }"
            draggable="false"
          />
        </template>
      </div>

      <!-- 滑动条 -->
      <div class="slider-track" ref="trackRef">
        <div class="slider-fill" :style="{ width: sliderLeft + 'px' }"></div>
        <div
          class="slider-btn"
          :style="{ left: sliderLeft + 'px' }"
          @mousedown="startDrag"
          @touchstart.prevent="startDrag"
        >
          <DoubleRightOutlined v-if="!verifying" />
          <LoadingOutlined v-else spin />
        </div>
        <span class="slider-hint">{{ dragHint }}</span>
      </div>

      <!-- 刷新 -->
      <div class="captcha-footer">
        <a @click="refreshCaptcha"><ReloadOutlined /> 换一张</a>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { DoubleRightOutlined, LoadingOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { captchaApi } from '@/api/user/auth'

const props = defineProps({
  visible: { type: Boolean, default: false },
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
const trackRef = ref()
const imageRef = ref()
const dragHint = ref('向右滑动滑块填充拼图')

// 获取验证码
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

// 刷新
function refreshCaptcha() {
  fetchCaptcha()
}

// 拖拽开始
function startDrag(e) {
  if (loading.value || verifying.value) return
  dragging.value = true
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  startX.value = clientX - sliderLeft.value
  dragHint.value = ''
}

// 拖拽移动
function onDrag(e) {
  if (!dragging.value) return
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  let newLeft = clientX - startX.value
  const maxLeft = 280 - 50
  newLeft = Math.max(0, Math.min(newLeft, maxLeft))
  sliderLeft.value = newLeft
}

// 拖拽结束
async function endDrag() {
  if (!dragging.value) return
  dragging.value = false

  if (sliderLeft.value < 10) {
    sliderLeft.value = 0
    dragHint.value = '向右滑动滑块填充拼图'
    return
  }

  // 验证
  verifying.value = true
  try {
    const res = await captchaApi.verifySlider({
      token: token.value,
      position: Math.round(sliderLeft.value),
    })
    // 验证成功
    emit('success', res.data)
    handleCancel()
  } catch {
    // 验证失败，重置
    sliderLeft.value = 0
    dragHint.value = '验证失败，请重试'
    // 自动刷新
    setTimeout(() => fetchCaptcha(), 1000)
  } finally {
    verifying.value = false
  }
}

function handleCancel() {
  emit('update:visible', false)
}

// 监听弹窗打开
import { watch } from 'vue'
watch(
  () => props.visible,
  (val) => {
    if (val) fetchCaptcha()
  },
)

onMounted(() => {
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('touchend', endDrag)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', endDrag)
})
</script>

<style scoped>
.slider-captcha {
  user-select: none;
}

.captcha-image {
  position: relative;
  width: 280px;
  height: 155px;
  margin: 0 auto 12px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.captcha-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.bg-img {
  width: 280px;
  height: 155px;
  display: block;
}

.piece-img {
  position: absolute;
  height: 50px;
  width: 50px;
  cursor: grab;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.35));
}

.slider-track {
  position: relative;
  width: 280px;
  height: 40px;
  margin: 0 auto;
  background: #f2f2f7;
  border-radius: 20px;
  overflow: hidden;
}

.slider-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(to right, #ff6b3520, #ff6b3540);
  border-radius: 20px;
  transition: none;
}

.slider-btn {
  position: absolute;
  top: 2px;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  cursor: grab;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.4);
  transition: box-shadow 0.2s;
  z-index: 1;
}

.slider-btn:hover {
  box-shadow: 0 2px 12px rgba(255, 107, 53, 0.6);
}

.slider-btn:active {
  cursor: grabbing;
}

.slider-hint {
  position: absolute;
  width: 100%;
  text-align: center;
  line-height: 40px;
  font-size: 13px;
  color: #999;
  pointer-events: none;
}

.captcha-footer {
  text-align: right;
  margin-top: 8px;
}

.captcha-footer a {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.captcha-footer a:hover {
  color: #ff6b35;
}
</style>
