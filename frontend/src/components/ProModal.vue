<template>
  <a-modal
    :open="visible"
    :title="title"
    :width="width"
    :confirm-loading="loading"
    :mask-closable="false"
    @ok="handleOk"
    @cancel="handleCancel"
    v-bind="$attrs"
  >
    <slot></slot>
  </a-modal>
</template>

<script setup>
import { watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '标题',
  },
  width: {
    type: [String, Number],
    default: 520,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:visible', 'ok', 'cancel'])

const handleOk = () => {
  emit('ok')
}

const handleCancel = () => {
  emit('update:visible', false)
  emit('cancel')
}

// 监听 ESC 键关闭弹窗
watch(
  () => props.visible,
  (val) => {
    if (val) {
      document.addEventListener('keydown', handleEscKey)
    } else {
      document.removeEventListener('keydown', handleEscKey)
    }
  },
)

const handleEscKey = (e) => {
  if (e.key === 'Escape') {
    handleCancel()
  }
}
</script>
