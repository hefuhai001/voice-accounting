<template>
  <div class="admin-data-page">
    <a-card>
      <ProTable
        :columns="columns"
        :data-source="dataList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'frequency'">
            <a-tag>{{ getFrequencyName(record.frequency) }}</a-tag>
          </template>

          <template v-if="column.key === 'status'">
            <a-tag
              :color="record.status === 0 ? 'orange' : record.status === 1 ? 'blue' : 'default'"
            >
              {{ getStatusName(record.status) }}
            </a-tag>
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-popconfirm title="确定删除该提醒？" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </ProTable>
    </a-card>

    <!-- 详情抽屉 -->
    <a-drawer
      :open="detailVisible"
      :title="'提醒详情 - ' + (detailRecord?.title || '')"
      :width="520"
      @close="detailVisible = false"
    >
      <a-spin :spinning="detailLoading">
        <a-descriptions :column="1" bordered size="small" v-if="detailRecord">
          <a-descriptions-item label="ID">{{ detailRecord.id }}</a-descriptions-item>
          <a-descriptions-item label="用户ID">{{ detailRecord.userId || '-' }}</a-descriptions-item>
          <a-descriptions-item label="提醒标题">{{ detailRecord.title }}</a-descriptions-item>
          <a-descriptions-item label="提醒金额">
            <strong style="color: #1890ff; font-size: 16px">¥{{ detailRecord.amount }}</strong>
          </a-descriptions-item>
          <a-descriptions-item label="提醒日期">{{
            detailRecord.remindDate || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="重复频率">
            <a-tag>{{ getFrequencyName(detailRecord.frequency) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag
              :color="
                detailRecord.status === 0
                  ? 'orange'
                  : detailRecord.status === 1
                    ? 'blue'
                    : 'default'
              "
            >
              {{ getStatusName(detailRecord.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="是否已重复">
            <a-tag :color="detailRecord.isRepeated === 1 ? 'blue' : 'default'">
              {{ detailRecord.isRepeated === 1 ? '是' : '否' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="备注">{{ detailRecord.remark || '-' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{
            detailRecord.createdAt || '-'
          }}</a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-drawer>

    <!-- 编辑弹窗 -->
    <a-modal
      :open="editVisible"
      title="编辑提醒"
      :width="520"
      :confirm-loading="editLoading"
      ok-text="保存"
      cancel-text="取消"
      @ok="handleSubmit"
      @cancel="editVisible = false"
    >
      <a-form
        ref="editFormRef"
        :model="editForm"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item
          label="提醒标题"
          name="title"
          :rules="[{ required: true, message: '请输入标题' }]"
        >
          <a-input v-model:value="editForm.title" placeholder="请输入提醒标题" />
        </a-form-item>
        <a-form-item
          label="提醒金额"
          name="amount"
          :rules="[{ required: true, message: '请输入金额' }]"
        >
          <a-input-number
            v-model:value="editForm.amount"
            :min="0"
            :precision="2"
            style="width: 100%"
            prefix="¥"
          />
        </a-form-item>
        <a-form-item label="提醒日期" name="remindDate">
          <a-date-picker
            v-model:value="editRemindDate"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="重复频率" name="frequency">
          <a-select v-model:value="editForm.frequency" placeholder="请选择频率" allow-clear>
            <a-select-option :value="1">一次性</a-select-option>
            <a-select-option :value="2">每天</a-select-option>
            <a-select-option :value="3">每周</a-select-option>
            <a-select-option :value="4">每月</a-select-option>
            <a-select-option :value="5">每年</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="editForm.status" placeholder="请选择状态">
            <a-select-option :value="0">待提醒</a-select-option>
            <a-select-option :value="1">已提醒</a-select-option>
            <a-select-option :value="2">已关闭</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="editForm.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import ProTable from '@/components/ProTable.vue'
import { adminReminderApi } from '@/api'

const loading = ref(false)
const dataList = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
})

// ====== 详情 ======
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailRecord = ref(null)

async function handleDetail(record) {
  detailVisible.value = true
  detailLoading.value = true
  try {
    const res = await adminReminderApi.getById(record.id)
    detailRecord.value = res.data
  } catch (error) {
    console.error('获取详情失败:', error)
    message.error('获取详情失败')
  } finally {
    detailLoading.value = false
  }
}

// ====== 编辑 ======
const editVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: null,
  title: '',
  amount: undefined,
  remindDate: null,
  frequency: undefined,
  status: undefined,
  remark: '',
})
const editRemindDate = computed({
  get: () => (editForm.remindDate ? dayjs(editForm.remindDate) : null),
  set: (val) => {
    editForm.remindDate = val
  },
})

function handleEdit(record) {
  Object.assign(editForm, {
    id: record.id,
    title: record.title,
    amount: record.amount,
    remindDate: record.remindDate || null,
    frequency: record.frequency,
    status: record.status,
    remark: record.remark || '',
  })
  editVisible.value = true
}

async function handleSubmit() {
  try {
    await editFormRef.value?.validateFields()
  } catch {
    return
  }
  editLoading.value = true
  try {
    const { id, ...data } = editForm
    await adminReminderApi.update(id, data)
    message.success('修改成功')
    editVisible.value = false
    loadData()
  } catch (error) {
    console.error('修改失败:', error)
    message.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

// ====== 删除 ======
async function handleDelete(id) {
  try {
    await adminReminderApi.delete(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

// ====== 列配置 & 加载 ======
const columns = computed(() => [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 80 },
  { title: '提醒标题', dataIndex: 'title', key: 'title' },
  { title: '提醒金额', dataIndex: 'amount', key: 'amount', width: 120 },
  { title: '提醒日期', dataIndex: 'remindDate', key: 'remindDate', width: 130 },
  { title: '重复频率', dataIndex: 'frequency', key: 'frequency', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '备注', dataIndex: 'remark', key: 'remark', ellipsis: true },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
])

const getFrequencyName = (frequency) => {
  return { 1: '一次性', 2: '每天', 3: '每周', 4: '每月', 5: '每年' }[frequency] || '未知'
}

const getStatusName = (status) => {
  return { 0: '待提醒', 1: '已提醒', 2: '已关闭' }[status] || '未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminReminderApi.getPage({
      current: pagination.current,
      size: pagination.pageSize,
    })
    dataList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<script>
export default {
  inheritAttrs: false,
}
</script>
