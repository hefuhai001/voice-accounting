<template>
  <a-form
    :model="formState"
    :rules="rules"
    ref="formRef"
    @finish="handleSubmit"
    layout="vertical"
    class="space-y-1"
  >
    <a-form-item name="account" class="!mb-5">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        用户名 / 邮箱
      </label>
      <a-input
        v-model:value="formState.account"
        placeholder="请输入用户名或邮箱"
        size="large"
        class="login-input"
      >
        <template #prefix>
          <span class="material-symbols-outlined text-on-surface-variant">person</span>
        </template>
      </a-input>
    </a-form-item>

    <a-form-item name="password" class="!mb-6">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        密码
      </label>
      <a-input-password
        v-model:value="formState.password"
        placeholder="请输入密码"
        size="large"
        class="login-input"
      >
        <template #prefix>
          <span class="material-symbols-outlined text-on-surface-variant">lock</span>
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item class="!mb-0">
      <button
        type="submit"
        :disabled="loading"
        class="w-full h-12 rounded-2xl text-white text-headline-md font-semibold shadow-lg transition-all duration-200 active:scale-[0.98] disabled:opacity-50 disabled:active:scale-100 bg-gradient-to-r from-[#983f19] to-[#ab3500] shadow-primary/30 hover:shadow-primary/40"
      >
        {{ loading ? '登录中...' : '登 录' }}
      </button>
    </a-form-item>
  </a-form>
</template>

<script setup>
import { ref, reactive } from 'vue'

defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit'])

const formRef = ref()

const formState = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleSubmit() {
  emit('submit', { ...formState })
}

defineExpose({
  reset: () => {
    formState.account = ''
    formState.password = ''
  }
})
</script>

<style scoped>
.login-input :deep(.ant-input-affix-wrapper) {
  background-color: #f8f9fc !important;
  border-radius: 1rem !important;
  border: none !important;
  height: 48px !important;
  padding: 0 16px !important;
  display: flex;
  align-items: center;
}

.login-input :deep(.ant-input) {
  background-color: transparent !important;
  height: auto !important;
  font-size: 16px !important;
}

.login-input :deep(.ant-input-affix-wrapper-focused) {
  border: 1px solid rgba(152, 63, 25, 0.3) !important;
  box-shadow: none !important;
}

.login-input :deep(.ant-input::placeholder) {
  color: #64748b !important;
}
</style>
