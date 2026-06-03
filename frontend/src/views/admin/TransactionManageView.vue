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
          <template v-if="column.key === 'type'">
            <a-tag color="blue">{{ getTypeName(record.type) }}</a-tag>
          </template>

          <template v-if="column.key === 'imageUrl' && record.imageUrl">
            <a-image :width="48" :height="48" :src="record.imageUrl" style="border-radius: 4px" />
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-popconfirm title="确定删除该记账记录？" @confirm="handleDelete(record.id)">
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
      :title="'记账记录详情'"
      :width="520"
      @close="detailVisible = false"
    >
      <a-spin :spinning="detailLoading">
        <a-descriptions :column="1" bordered size="small" v-if="detailRecord">
          <a-descriptions-item label="ID">{{ detailRecord.id }}</a-descriptions-item>
          <a-descriptions-item label="账本ID">{{ detailRecord.bookId }}</a-descriptions-item>
          <a-descriptions-item label="分类ID">{{ detailRecord.categoryId }}</a-descriptions-item>
          <a-descriptions-item label="金额">
            <strong style="color: #f5222d; font-size: 16px">¥{{ detailRecord.amount }}</strong>
          </a-descriptions-item>
          <a-descriptions-item label="类型">
            <a-tag :color="detailRecord.type === 1 ? 'red' : 'green'">
              {{ getTypeName(detailRecord.type) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="备注">{{ detailRecord.remark || '-' }}</a-descriptions-item>
          <a-descriptions-item label="语音原文">{{
            detailRecord.voiceText || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="交易日期">{{
            detailRecord.transactionDate || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="凭证图片">
            <a-image
              v-if="detailRecord.imageUrl"
              :width="80"
              :height="80"
              :src="detailRecord.imageUrl"
              style="border-radius: 4px"
            />
            <span v-else>-</span>
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">{{
            detailRecord.createdAt || '-'
          }}</a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-drawer>

    <!-- 编辑弹窗 -->
    <a-modal
      :open="editVisible"
      title="编辑记账记录"
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
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 17 }"
      >
        <a-form-item
          label="金额"
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
        <a-form-item label="类型" name="type" :rules="[{ required: true, message: '请选择类型' }]">
          <a-select v-model:value="editForm.type" placeholder="请选择类型">
            <a-select-option :value="1">支出</a-select-option>
            <a-select-option :value="2">收入</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="交易日期" name="transactionDate">
          <a-date-picker
            v-model:value="editTransactionDate"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="editForm.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
        <a-form-item label="语音原文" name="voiceText">
          <a-textarea v-model:value="editForm.voiceText" :rows="2" placeholder="语音识别原文" />
        </a-form-item>
        <a-form-item label="凭证图片" name="imageUrl">
          <a-input v-model:value="editForm.imageUrl" placeholder="请输入图片地址" />
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
import { adminTransactionApi } from '@/api'

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
    const res = await adminTransactionApi.getById(record.id)
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
  amount: undefined,
  type: undefined,
  transactionDate: null,
  remark: '',
  voiceText: '',
  imageUrl: '',
})
const editTransactionDate = computed({
  get: () => (editForm.transactionDate ? dayjs(editForm.transactionDate) : null),
  set: (val) => {
    editForm.transactionDate = val
  },
})

function handleEdit(record) {
  Object.assign(editForm, {
    id: record.id,
    amount: record.amount,
    type: record.type,
    transactionDate: record.transactionDate || null,
    remark: record.remark || '',
    voiceText: record.voiceText || '',
    imageUrl: record.imageUrl || '',
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
    await adminTransactionApi.update(id, data)
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
    await adminTransactionApi.delete(id)
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
  { title: '账本ID', dataIndex: 'bookId', key: 'bookId', width: 80 },
  { title: '分类ID', dataIndex: 'categoryId', key: 'categoryId', width: 80 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 120 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 80 },
  { title: '备注', dataIndex: 'remark', key: 'remark', ellipsis: true },
  { title: '语音原文', dataIndex: 'voiceText', key: 'voiceText', ellipsis: true },
  { title: '交易日期', dataIndex: 'transactionDate', key: 'transactionDate', width: 130 },
  { title: '凭证图片', dataIndex: 'imageUrl', key: 'imageUrl', width: 100 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
])

const getTypeName = (type) => {
  return { 1: '支出', 2: '收入' }[type] || '未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminTransactionApi.getPage({
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
