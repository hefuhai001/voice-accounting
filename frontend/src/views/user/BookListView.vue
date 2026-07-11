<template>
  <div class="px-margin-mobile pb-32 flex flex-col gap-section-gap max-w-md mx-auto">
    <!-- Header Section -->
    <section class="flex justify-between items-end mt-8">
      <div>
        <h2 class="font-display-lg text-display-lg text-on-surface tracking-tight mb-1">我的账本</h2>
        <p class="text-on-surface-variant font-label-caps text-label-caps uppercase tracking-[0.2em]">MY LEDGERS</p>
      </div>
      <button
        @click="showAddModal"
        class="bg-primary text-white px-8 py-3 rounded-full font-bold text-sm shadow-xl shadow-primary/25 hover:shadow-primary/40 hover:scale-105 active:scale-95 transition-all flex items-center gap-2 border-none"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        新建账本
      </button>
    </section>

    <!-- Card Flow -->
    <section class="flex flex-col gap-8 mt-8">
      <div
        v-for="(book) in books"
        :key="book.id"
        @click="handleEdit(book)"
        class="glass-panel rounded-3xl p-8 relative overflow-hidden group cursor-pointer"
        :class="{ 'ml-0 md:ml-12 border-l-8 border-l-secondary': book.type === 2, 'ml-0 md:ml-24': book.type === 3 }"
      >
        <div class="absolute right-0 top-0 w-48 h-48 rounded-full blur-3xl -mr-24 -mt-24 transition-colors"
          :class="book.type === 1 ? 'bg-primary/5 group-hover:bg-primary/10' : book.type === 2 ? 'bg-secondary/5 group-hover:bg-secondary/10' : 'bg-tertiary/5 group-hover:bg-tertiary/10'"></div>

        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
          <div class="flex gap-6 items-center">
            <div class="w-16 h-16 rounded-2xl flex items-center justify-center border group-hover:rotate-6 transition-transform"
              :class="getIconBgClass(book.type)">
              <span class="material-symbols-outlined text-3xl" :class="getIconColorClass(book.type)">{{ getBookIcon(book.type) }}</span>
            </div>
            <div>
              <h3 class="text-2xl font-bold text-on-surface">{{ book.name }}</h3>
              <p class="text-on-surface-variant text-xs font-bold tracking-widest uppercase mt-1">{{ getTypeName(book.type) }}</p>
              <p v-if="book.description" class="text-outline text-sm mt-1">{{ book.description }}</p>
            </div>
          </div>

          <div class="text-left md:text-right w-full md:w-auto flex items-center justify-between gap-4">
            <div class="flex items-center gap-2">
              <span v-if="book.isDefault === 1"
                class="inline-flex items-center px-3 py-1 rounded-full text-[11px] font-bold bg-secondary-fixed/20 text-secondary">
                <span class="material-symbols-outlined text-[14px] mr-1">check_circle</span>
                默认
              </span>
            </div>

            <!-- Delete Button -->
            <button
              v-if="book.userId"
              @click.stop="confirmDelete(book)"
              class="text-outline hover:text-danger-red transition-colors flex items-center gap-1 text-sm"
            >
              <span class="material-symbols-outlined text-lg">delete</span>
              删除
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Empty State -->
    <div v-if="books.length === 0 && !loading" class="glass-panel rounded-3xl p-12 text-center">
      <span class="material-symbols-outlined text-6xl text-outline mb-4 block">account_balance_wallet</span>
      <p class="font-body-md text-body-md text-on-surface-variant">还没有账本</p>
      <button @click="showAddModal" class="mt-4 bg-primary text-white px-6 py-2 rounded-full font-bold text-sm">
        创建第一个账本
      </button>
    </div>

    <!-- 新建/编辑弹窗 -->
    <a-modal
      :open="modalVisible"
      :title="isEdit ? '编辑账本' : '新建账本'"
      :footer="null"
      :width="360"
      centered
      @cancel="modalVisible = false"
    >
      <a-form :model="formState" layout="vertical" class="mt-2" @finish="handleSubmit">
        <a-form-item label="账本名称" name="name" :rules="[{ required: true, message: '请输入名称' }]">
          <a-input v-model:value="formState.name" placeholder="给账本起个名字" size="large" />
        </a-form-item>
        <a-form-item label="类型">
          <a-select v-model:value="formState.type" size="large">
            <a-select-option :value="1">日常账本</a-select-option>
            <a-select-option :value="2">旅行账本</a-select-option>
            <a-select-option :value="3">共享账本</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="formState.description" :rows="2" placeholder="简单描述一下" />
        </a-form-item>
        <a-form-item label="设为默认">
          <a-switch v-model:checked="formState.isDefault" checked-children="是" un-checked-children="否" />
        </a-form-item>
        <div class="flex gap-3 mt-2">
          <a-button block size="large" @click="modalVisible = false">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="submitting">保存</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { bookApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)

const loading = ref(false)
const books = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)

const formState = reactive({
  name: '',
  type: 1,
  description: '',
  isDefault: false,
  userId: null,
})

function getTypeName(type) {
  const map = { 1: 'DAILY SPENDING', 2: 'JAPAN TRIP', 3: 'INVESTMENT' }
  return map[type] || 'UNKNOWN'
}

function getBookIcon(type) {
  const map = { 1: 'shopping_cart', 2: 'flight_takeoff', 3: 'monitoring' }
  return map[type] || 'account_balance_wallet'
}

function getIconBgClass(type) {
  const map = {
    1: 'bg-indigo-50 border-indigo-100',
    2: 'bg-emerald-50 border-emerald-100',
    3: 'bg-orange-50 border-orange-100'
  }
  return map[type] || 'bg-surface-container border-outline'
}

function getIconColorClass(type) {
  const map = { 1: 'text-indigo-500', 2: 'text-emerald-500', 3: 'text-orange-600' }
  return map[type] || 'text-on-surface-variant'
}

async function loadData() {
  if (!userId.value) return
  loading.value = true
  try {
    const res = await bookApi.getPage({ userId: userId.value, current: 1, size: 100 })
    books.value = res.data.records
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  isEdit.value = false
  Object.assign(formState, { name: '', type: 1, description: '', isDefault: false, userId: userId.value })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  editId.value = record.id
  Object.assign(formState, {
    name: record.name,
    type: record.type,
    description: record.description,
    isDefault: record.isDefault === 1,
    userId: record.userId,
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    const data = { ...formState, isDefault: formState.isDefault ? 1 : 0 }
    if (isEdit.value) {
      await bookApi.update(editId.value, data)
      message.success('修改成功')
    } else {
      await bookApi.save(data)
      message.success('创建成功')
    }
    modalVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitting.value = false
  }
}

function confirmDelete(book) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除账本「${book.name}」吗？删除后不可恢复。`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await bookApi.delete(book.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        console.error('删除失败:', error)
      }
    },
  })
}

watch(userId, (newUserId) => {
  if (newUserId) loadData()
})

onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadData()
})
</script>

<style scoped>
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-panel:hover {
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.rounded-3xl {
  border-radius: 2.5rem;
}

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
</style>
