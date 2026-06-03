<template>
  <a-table
    :columns="columns"
    :data-source="dataSource"
    :loading="loading"
    :pagination="paginationConfig"
    :row-key="rowKey"
    @change="handleTableChange"
    v-bind="$attrs"
  >
    <template v-for="slot in customSlots" #[slot]="scope" :key="slot">
      <slot :name="slot" v-bind="scope" />
    </template>
  </a-table>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  columns: {
    type: Array,
    default: () => [],
  },
  dataSource: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  pagination: {
    type: [Object, Boolean],
    default: () => ({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条`,
    }),
  },
  rowKey: {
    type: [String, Function],
    default: 'id',
  },
})

const emit = defineEmits(['change', 'paginationChange'])

// 计算分页配置
const paginationConfig = computed(() => {
  if (props.pagination === false) return false
  return {
    ...props.pagination,
    onChange: (page, pageSize) => {
      emit('paginationChange', { page, pageSize })
    },
  }
})

// 获取自定义插槽名称（排除默认插槽）
const customSlots = computed(() => {
  const slots = ['bodyCell']
  props.columns.forEach((col) => {
    if (col.customRender || col.slots?.customRender) {
      slots.push(col.dataIndex)
    }
  })
  return slots
})

// 表格变化事件
const handleTableChange = (pagination, filters, sorter) => {
  emit('change', { pagination, filters, sorter })
}
</script>

<style scoped>
/* 表格样式优化 */
:deep(.ant-table) {
  border-radius: 8px;
}

:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}
</style>
