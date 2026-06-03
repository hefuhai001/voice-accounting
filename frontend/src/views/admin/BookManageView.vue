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

          <template v-if="column.key === 'isDefault'">
            <a-tag v-if="record.isDefault === 1" color="red">默认</a-tag>
            <span v-else>-</span>
          </template>

          <template v-if="column.key === 'icon' && record.icon">
            <a-image
              :width="32"
              :height="32"
              :src="record.icon"
              :fallback="undefined"
              style="border-radius: 4px"
            />
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-popconfirm title="确定删除该账本？" @confirm="handleDelete(record.id)">
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
      :title="'账本详情 - ' + (detailRecord?.name || '')"
      :width="520"
      @close="detailVisible = false"
    >
      <a-spin :spinning="detailLoading">
        <a-descriptions :column="1" bordered size="small" v-if="detailRecord">
          <a-descriptions-item label="ID">{{ detailRecord.id }}</a-descriptions-item>
          <a-descriptions-item label="账本名称">{{ detailRecord.name }}</a-descriptions-item>
          <a-descriptions-item label="类型">
            <a-tag color="blue">{{ getTypeName(detailRecord.type) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="描述">{{
            detailRecord.description || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="图标">
            <a-image
              v-if="detailRecord.icon"
              :width="48"
              :height="48"
              :src="detailRecord.icon"
              style="border-radius: 4px"
            />
            <span v-else>-</span>
          </a-descriptions-item>
          <a-descriptions-item label="所属用户ID">{{
            detailRecord.userId || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="是否默认">
            <a-tag :color="detailRecord.isDefault === 1 ? 'red' : 'default'">
              {{ detailRecord.isDefault === 1 ? '是' : '否' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="排序">{{
            detailRecord.sortOrder ?? '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{
            detailRecord.createdAt || '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{
            detailRecord.updatedAt || '-'
          }}</a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-drawer>

    <!-- 编辑弹窗 -->
    <a-modal
      :open="editVisible"
      title="编辑账本"
      :width="500"
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
          label="账本名称"
          name="name"
          :rules="[{ required: true, message: '请输入账本名称' }]"
        >
          <a-input v-model:value="editForm.name" placeholder="请输入账本名称" />
        </a-form-item>
        <a-form-item label="类型" name="type" :rules="[{ required: true, message: '请选择类型' }]">
          <a-select v-model:value="editForm.type" placeholder="请选择类型">
            <a-select-option :value="1">日常账本</a-select-option>
            <a-select-option :value="2">旅行账本</a-select-option>
            <a-select-option :value="3">共享账本</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="editForm.description" :rows="3" placeholder="请输入描述" />
        </a-form-item>
        <a-form-item label="图标" name="icon">
          <a-input v-model:value="editForm.icon" placeholder="请输入图标地址" />
        </a-form-item>
        <a-form-item label="是否默认" name="isDefault">
          <a-switch
            v-model:checked="editFormIsDefault"
            checked-children="是"
            un-checked-children="否"
          />
        </a-form-item>
        <a-form-item label="排序" name="sortOrder">
          <a-input-number v-model:value="editForm.sortOrder" :min="0" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import ProTable from '@/components/ProTable.vue'
import { adminBookApi } from '@/api'

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
    const res = await adminBookApi.getById(record.id)
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
  name: '',
  type: undefined,
  description: '',
  icon: '',
  isDefault: 0,
  sortOrder: 0,
})
const editFormIsDefault = computed({
  get: () => editForm.isDefault === 1,
  set: (val) => {
    editForm.isDefault = val ? 1 : 0
  },
})

function handleEdit(record) {
  Object.assign(editForm, {
    id: record.id,
    name: record.name,
    type: record.type,
    description: record.description || '',
    icon: record.icon || '',
    isDefault: record.isDefault ?? 0,
    sortOrder: record.sortOrder ?? 0,
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
    await adminBookApi.update(id, data)
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
    await adminBookApi.delete(id)
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
  { title: '账本名称', dataIndex: 'name', key: 'name' },
  { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
  { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '图标', dataIndex: 'icon', key: 'icon', width: 80 },
  { title: '所属用户', dataIndex: 'userId', key: 'userId', width: 80 },
  { title: '是否默认', dataIndex: 'isDefault', key: 'isDefault', width: 80 },
  { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 60 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
])

const getTypeName = (type) => {
  return { 1: '日常账本', 2: '旅行账本', 3: '共享账本' }[type] || '未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminBookApi.getPage({
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
