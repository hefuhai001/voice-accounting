<template>
  <div class="px-margin-mobile pt-stack-lg pb-8">
    <!-- Dashboard Header -->
    <div class="flex justify-between items-end mb-stack-lg">
      <div class="flex flex-col gap-1">
        <h2 class="font-headline-lg-mobile text-headline-lg-mobile font-bold tracking-tight text-on-surface">我的账本</h2>
        <p class="font-label-sm text-label-sm text-on-surface-variant/60">共 {{ books.length }} 个账本</p>
      </div>
      <button @click="showAddModal"
        class="bg-primary-container text-on-primary-container h-10 px-5 rounded-full flex items-center gap-2 font-label-md text-label-md active:scale-95 transition-transform duration-150 shadow-sm">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
        </svg>
        新建
      </button>
    </div>

    <!-- Ledger Cards List -->
    <section class="flex flex-col gap-stack-md">
      <div v-for="book in books" :key="book.id" @click="handleEdit(book)"
        class="bg-white p-5 rounded-xl shadow-[0_4px_20px_rgba(0,0,0,0.04)] flex gap-4 items-center group relative overflow-hidden active:bg-surface-container-low transition-colors duration-200 border border-transparent hover:border-outline-variant/30 cursor-pointer">
        <!-- Icon -->
        <div class="w-14 h-14 rounded-xl flex items-center justify-center flex-shrink-0"
          :class="book.isDefault === 1 ? 'bg-primary-fixed' : 'bg-surface-gray'">
          <!-- 日常账本 -->
          <svg v-if="book.type === 1" class="w-7 h-7"
            :class="book.isDefault === 1 ? 'text-primary' : 'text-on-surface-variant'" fill="none" viewBox="0 0 24 24"
            stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round"
              d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" />
          </svg>
          <!-- 旅行账本 -->
          <svg v-else-if="book.type === 2" class="w-7 h-7"
            :class="book.isDefault === 1 ? 'text-primary' : 'text-on-surface-variant'" fill="none" viewBox="0 0 24 24"
            stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round"
              d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
          </svg>
          <!-- 共享账本 -->
          <svg v-else class="w-7 h-7" :class="book.isDefault === 1 ? 'text-primary' : 'text-on-surface-variant'"
            fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round"
              d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
          </svg>
        </div>
        <!-- Info -->
        <div class="flex flex-col flex-grow gap-0.5">
          <div class="flex items-center gap-2">
            <span class="font-headline-md text-headline-md text-on-surface">{{ book.name }}</span>
            <span v-if="book.isDefault === 1"
              class="px-2 py-0.5 rounded-full bg-secondary-fixed text-on-secondary-fixed font-label-sm text-[10px] uppercase tracking-wider">默认</span>
          </div>
          <span class="font-body-md text-body-md text-on-surface-variant">{{ getTypeName(book.type) }}</span>
          <span v-if="book.description" class="font-label-sm text-label-sm text-outline">{{ book.description }}</span>
        </div>
        <!-- Delete Button -->
        <button @click.stop="confirmDelete(book)"
          class="opacity-0 group-hover:opacity-100 p-2 text-outline hover:text-danger-red transition-all duration-200">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round"
              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        </button>
      </div>
    </section>

    <!-- Empty State -->
    <div v-if="books.length === 0 && !loading" class="py-16 text-center">
      <BookOutlined class="text-4xl text-outline" />
      <p class="font-body-md text-body-md text-on-surface-variant mt-3">还没有账本</p>
      <button @click="showAddModal" class="mt-3 font-label-md text-primary">
        创建第一个账本
      </button>
    </div>

    <!-- Decorative Illustration Section -->
    <section class="mt-8 rounded-2xl overflow-hidden relative h-48 bg-surface-container">
      <img class="w-full h-full object-cover mix-blend-overlay opacity-30"
        data-alt="A sophisticated macro photograph of textured high-quality paper with subtle debossed patterns and elegant minimalist typography. The lighting is soft and cinematic, highlighting a calm and organized professional atmosphere. The color palette consists of warm whites, charcoal grays, and soft terracotta orange accents, maintaining a modern minimalist aesthetic consistent with a premium financial management app."
        src="../../assets/unnamed.png" />
      <div
        class="absolute inset-0 p-6 flex flex-col justify-end gap-2 bg-gradient-to-t from-background via-transparent to-transparent">
        <p class="font-headline-md text-headline-md font-bold text-on-surface">掌握您的财务未来</p>
        <p class="font-body-md text-body-md text-on-surface-variant">通过语音轻松分类每一笔支出</p>
      </div>
    </section>

    <!-- 新建/编辑弹窗 -->
    <a-modal :open="modalVisible" :title="isEdit ? '编辑账本' : '新建账本'" :footer="null" :width="360" centered
      @cancel="modalVisible = false">
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

const getTypeName = (type) => ({ 1: '日常账本', 2: '旅行账本', 3: '共享账本' })[type] || '未知'

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

// 监听 userId 变化
watch(userId, (newUserId) => {
  if (newUserId) loadData()
})

// 页面加载时检查登录状态
onMounted(async () => {
  if (!authStore.userInfo) {
    await authStore.checkLoginStatus()
  }
  if (userId.value) loadData()
})
</script>
