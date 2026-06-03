<template>
  <a-form
    ref="formRef"
    :model="formState"
    :rules="formRules"
    :layout="layout"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    v-bind="$attrs"
  >
    <a-row :gutter="gutter">
      <a-col v-for="item in formItems" :key="item.field" :span="item.span || 24 / columns">
        <a-form-item :label="item.label" :name="item.field">
          <!-- 输入框 -->
          <a-input
            v-if="item.type === 'input'"
            v-model:value="formState[item.field]"
            :placeholder="`请输入${item.label}`"
            :disabled="item.disabled"
            allowClear
          />
          <!-- 密码框 -->
          <a-input-password
            v-else-if="item.type === 'password'"
            v-model:value="formState[item.field]"
            :placeholder="`请输入${item.label}`"
            :disabled="item.disabled"
            allowClear
          />
          <!-- 文本域 -->
          <a-textarea
            v-else-if="item.type === 'textarea'"
            v-model:value="formState[item.field]"
            :placeholder="`请输入${item.label}`"
            :disabled="item.disabled"
            :rows="item.rows || 4"
            allowClear
          />
          <!-- 选择器 -->
          <a-select
            v-else-if="item.type === 'select'"
            v-model:value="formState[item.field]"
            :placeholder="`请选择${item.label}`"
            :disabled="item.disabled"
            :options="item.options"
            allowClear
          />
          <!-- 数字输入 -->
          <a-input-number
            v-else-if="item.type === 'number'"
            v-model:value="formState[item.field]"
            :placeholder="`请输入${item.label}`"
            :disabled="item.disabled"
            style="width: 100%"
          />
          <!-- 日期选择 -->
          <a-date-picker
            v-else-if="item.type === 'date'"
            v-model:value="formState[item.field]"
            :placeholder="`请选择${item.label}`"
            :disabled="item.disabled"
            style="width: 100%"
          />
          <!-- 开关 -->
          <a-switch
            v-else-if="item.type === 'switch'"
            v-model:checked="formState[item.field]"
            :disabled="item.disabled"
          />
          <!-- 默认输入框 -->
          <a-input
            v-else
            v-model:value="formState[item.field]"
            :placeholder="`请输入${item.label}`"
            :disabled="item.disabled"
            allowClear
          />
        </a-form-item>
      </a-col>
    </a-row>

    <div class="form-footer">
      <slot name="footer">
        <a-button type="primary" @click="handleSubmit">提交</a-button>
        <a-button style="margin-left: 10px" @click="handleReset">重置</a-button>
      </slot>
    </div>
  </a-form>
</template>

<script setup>
import { reactive, ref } from 'vue'

const props = defineProps({
  // 表单项配置
  items: {
    type: Array,
    required: true,
    default: () => [],
  },
  // 表单初始值
  initialValues: {
    type: Object,
    default: () => ({}),
  },
  // 表单布局
  layout: {
    type: String,
    default: 'horizontal',
  },
  // 标签宽度
  labelCol: {
    type: Object,
    default: () => ({ span: 6 }),
  },
  // 内容宽度
  wrapperCol: {
    type: Object,
    default: () => ({ span: 18 }),
  },
  // 每行列数
  columns: {
    type: Number,
    default: 1,
  },
  // 栅格间距
  gutter: {
    type: Number,
    default: 16,
  },
})

const emit = defineEmits(['submit', 'reset', 'success'])

const formRef = ref()

// 表单项配置
const formItems = computed ? computed(() => props.items) : props.items

// 表单数据
const formState = reactive({ ...props.initialValues })

// 表单验证规则
const formRules = computed
  ? computed(() => {
      const rules = {}
      formItems.value.forEach((item) => {
        if (item.rules) {
          rules[item.field] = item.rules
        }
      })
      return rules
    })
  : {}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit', { ...formState })
    emit('success', { ...formState })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
  Object.assign(formState, { ...props.initialValues })
  emit('reset')
}

// 暴露方法给父组件
defineExpose({
  validate: () => formRef.value.validate(),
  resetFields: () => formRef.value.resetFields(),
  getFormData: () => ({ ...formState }),
  setFormData: (data) => Object.assign(formState, data),
})
</script>

<script>
import { computed } from 'vue'
export default {
  inheritAttrs: false,
}
</script>

<style scoped>
.form-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}
</style>
