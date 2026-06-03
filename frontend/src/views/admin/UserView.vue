<template>
  <div class="admin-user-page">
    <a-card>
      <template #extra>
        <a-button type="primary" size="small" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增用户
        </a-button>
      </template>

      <ProTable
        :columns="columns"
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="record.role === 1 ? 'red' : 'blue'">
              {{ record.role === 1 ? '管理员' : '普通用户' }}
            </a-tag>
          </template>

          <template v-if="column.key === 'status'">
            <a-badge
              :status="record.status === 1 ? 'success' : 'error'"
              :text="record.status === 1 ? '正常' : '禁用'"
            />
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-dropdown>
                <a-button type="link" size="small">更多</a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item v-if="record.role !== 1" @click="handleAssignRole(record.id, 1)">
                      设为管理员
                    </a-menu-item>
                    <a-menu-item v-else @click="handleAssignRole(record.id, 0)">
                      设为普通用户
                    </a-menu-item>
                    <a-menu-item
                      v-if="record.status === 1"
                      @click="handleUpdateStatus(record.id, 0)"
                    >
                      禁用账号
                    </a-menu-item>
                    <a-menu-item v-else @click="handleUpdateStatus(record.id, 1)">
                      启用账号
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              <a-popconfirm title="确定删除此用户？" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </ProTable>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import ProTable from '@/components/ProTable.vue'
import { adminUserApi } from '@/api'

const loading = ref(false)
const users = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await adminUserApi.getPage({
      current: pagination.current,
      size: pagination.pageSize,
    })
    users.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 编辑
const handleEdit = (record) => {
  message.info('编辑功能开发中...')
}

// 新增
const showAddModal = () => {
  message.info('新增功能开发中...')
}

// 分配角色
const handleAssignRole = async (id, role) => {
  try {
    await adminUserApi.assignRole(id, role)
    message.success(role === 1 ? '已设为管理员' : '已设为普通用户')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 更新状态
const handleUpdateStatus = async (id, status) => {
  try {
    await adminUserApi.updateStatus(id, status)
    message.success(status === 1 ? '已启用' : '已禁用')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 删除
const handleDelete = async (id) => {
  try {
    await adminUserApi.delete(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>
