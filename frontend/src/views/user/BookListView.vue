<template>
  <div class="px-4 pt-4 pb-6 space-y-4">
    <!-- 页面标题栏 -->
    <div class="flex items-center justify-between px-1">
      <h2 class="text-[22px] font-bold text-[#1c1c1e]">我的账本</h2>
      <button
        @click="showAddModal"
        class="flex items-center gap-1 px-3 py-1.5 bg-[#ff6b35] text-white text-xs font-semibold rounded-full active:scale-95 transition-transform shadow-sm shadow-orange-500/20"
      >
        <svg
          class="w-3.5 h-3.5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2.5"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
        </svg>
        新建
      </button>
    </div>

    <!-- 账本列表 -->
    <div class="space-y-3">
      <div
        v-for="book in books"
        :key="book.id"
        @click="handleEdit(book)"
        class="bg-white rounded-2xl p-4 shadow-sm active:scale-[0.98] transition-all duration-150 cursor-pointer"
      >
        <div class="flex items-start justify-between">
          <div class="flex items-center gap-3 min-w-0">
            <div
              class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl shrink-0"
              :class="
                book.type === 1 ? 'bg-blue-50' : book.type === 2 ? 'bg-teal-50' : 'bg-purple-50'
              "
            >
              {{ book.type === 1 ? '' : book.type === 2 ? '' : '' }}
            </div>
            <div class="min-w-0">
              <div class="flex items-center gap-2">
                <p class="text-[15px] font-semibold text-[#1c1c1e] truncate">{{ book.name }}</p>
                <a-tag
                  v-if="book.isDefault === 1"
                  color="orange"
                  class="!text-[10px] !px-1.5 !py-0 !rounded-md !m-0 !leading-none !h-4 !flex !items-center"
                  >默认</a-tag
                >
              </div>
              <p class="text-xs text-[#8e8e93] mt-1">{{ getTypeName(book.type) }}</p>
              <p v-if="book.description" class="text-xs text-[#aeaeb2] mt-0.5 truncate">
                {{ book.description }}
              </p>
            </div>
          </div>
          <button
            @click.stop="confirmDelete(book)"
            class="shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-[#c7c7cc] hover:bg-red-50 hover:text-red-500 active:bg-red-100 transition-colors"
          >
            <svg
              class="w-4 h-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="books.length === 0 && !loading" class="py-16 text-center">
        <div
          class="w-16 h-16 mx-auto mb-3 rounded-2xl bg-[#f2f2f7] flex items-center justify-center"
        >
          <BookOutlined class="text-2xl text-[#c7c7cc]" />
        </div>
        <p class="text-sm text-[#8e8e93]">还没有账本</p>
        <button @click="showAddModal" class="mt-3 text-sm text-[#ff6b35] font-medium">
          创建第一个账本
        </button>
      </div>
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
        <a-form-item
          label="账本名称"
          name="name"
          :rules="[{ required: true, message: '请输入名称' }]"
        >
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
          <a-switch
            v-model:checked="formState.isDefault"
            checked-children="是"
            un-checked-children="否"
          />
        </a-form-item>
        <div class="flex gap-3 mt-2">
          <a-button block size="large" @click="modalVisible = false">取消</a-button>
          <a-button type="primary" block size="large" html-type="submit" :loading="submitting"
            >保存</a-button
          >
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { BookOutlined } from '@ant-design/icons-vue'
import { bookApi } from '@/api'

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
})

const getTypeName = (type) => ({ 1: '日常账本', 2: '旅行账本', 3: '共享账本' })[type] || '未知'

async function loadData() {
  loading.value = true
  try {
    const res = await bookApi.getPage({ userId: 1, current: 1, size: 100 })
    books.value = res.data.records
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  isEdit.value = false
  Object.assign(formState, { name: '', type: 1, description: '', isDefault: false })
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
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await bookApi.update(editId.value, formState)
      message.success('修改成功')
    } else {
      await bookApi.save({ ...formState, userId: 1 })
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

async function confirmDelete(book) {
  try {
    await bookApi.delete(book.id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => loadData())
</script>
