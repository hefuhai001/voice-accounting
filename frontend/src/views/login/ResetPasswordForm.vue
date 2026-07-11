<template>
  <a-form
    :model="formState"
    :rules="rules"
    ref="formRef"
    @finish="handleSubmit"
    layout="vertical"
    class="space-y-1"
  >
    <a-form-item name="email" class="!mb-4">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        邮箱
      </label>
      <a-input
        v-model:value="formState.email"
        placeholder="请输入注册邮箱"
        size="large"
        class="login-input"
      >
        <template #prefix>
          <span class="material-symbols-outlined text-on-surface-variant">mail</span>
        </template>
      </a-input>
    </a-form-item>

    <a-form-item name="code" class="!mb-4">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        验证码
      </label>
      <div class="flex gap-2">
        <a-input
          v-model:value="formState.code"
          placeholder="6位验证码"
          size="large"
          :maxlength="6"
          class="login-input flex-1"
        >
          <template #prefix>
            <span class="material-symbols-outlined text-on-surface-variant">verified_user</span>
          </template>
        </a-input>
        <button
          type="button"
          :disabled="codeSending || countdown > 0 || !formState.email"
          @click="handleSendCode"
          class="flex-shrink-0 h-12 px-4 rounded-2xl text-label-md font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          :class="countdown > 0
            ? 'bg-surface-container-low text-on-surface-variant'
            : 'bg-gradient-to-r from-[#983f19] to-[#ab3500] text-white shadow-sm hover:shadow-primary/30'"
        >
          {{ codeSending ? '发送中' : countdown > 0 ? `${countdown}s` : '获取验证码' }}
        </button>
      </div>
    </a-form-item>

    <a-form-item name="newPassword" class="!mb-4">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        新密码
      </label>
      <a-input-password
        v-model:value="formState.newPassword"
        placeholder="6-20个字符"
        size="large"
        class="login-input"
      >
        <template #prefix>
          <span class="material-symbols-outlined text-on-surface-variant">lock</span>
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item name="confirmPassword" class="!mb-6">
      <label class="block text-label-sm text-on-surface-variant mb-1.5 ml-0.5 uppercase tracking-wider font-semibold">
        确认密码
      </label>
      <a-input-password
        v-model:value="formState.confirmPassword"
        placeholder="再次输入新密码"
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
        {{ loading ? '重置中...' : '重置密码' }}
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
  },
  codeSending: {
    type: Boolean,
    default: false
  },
  countdown: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['submit', 'send-code'])

const formRef = ref()

const formState = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = async (_rule, value) => {
  if (value !== formState.newPassword) {
    return Promise.reject('两次密码输入不一致')
  }
  return Promise.resolve()
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

function handleSendCode() {
  emit('send-code', formState.email)
}

function handleSubmit() {
  emit('submit', { ...formState })
}

defineExpose({
  reset: () => {
    Object.assign(formState, {
      email: '',
      code: '',
      newPassword: '',
      confirmPassword: ''
    })
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
