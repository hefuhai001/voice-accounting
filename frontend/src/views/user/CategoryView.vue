<template>
  <div class="px-margin-mobile pt-base pb-8 relative">
    <!-- Section Header -->
    <div class="flex justify-between items-center py-stack-lg">
      <h2 class="font-headline-lg-mobile text-headline-lg-mobile text-on-surface">全部分类</h2>
      <button
        @click="showAddModal"
        class="bg-primary-container text-on-primary-container px-6 py-2 rounded-full flex items-center gap-2 transition-all active:scale-95 shadow-sm"
      >
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
        </svg>
        <span class="font-label-md text-label-md">新建</span>
      </button>
    </div>

    <!-- Tab Switcher -->
    <div class="bg-surface-container-low p-1 rounded-xl flex mb-stack-lg">
      <button
        v-for="t in [
          { key: 'expense', label: '支出分类' },
          { key: 'income', label: '收入分类' },
        ]"
        :key="t.key"
        @click="switchTab(t.key)"
        class="flex-1 py-3 text-center rounded-lg font-label-md text-label-md transition-all duration-300"
        :class="
          activeTab === t.key
            ? 'bg-white text-primary shadow-[0px_4px_12px_rgba(0,0,0,0.05)]'
            : 'text-on-surface-variant hover:bg-surface-container-high'
        "
      >
        {{ t.label }}
      </button>
    </div>

    <!-- Category Grid (Bento Style) -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-gutter-md">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="bg-white p-5 rounded-2xl flex flex-col items-center justify-center gap-3 group hover:shadow-xl transition-all duration-300 relative"
        :class="cat.userId ? 'cursor-pointer' : ''"
        @click="cat.userId && handleEdit(cat)"
      >
        <!-- Icon -->
        <div
          class="w-16 h-16 rounded-full flex items-center justify-center text-[32px]"
          :class="activeTab === 'expense' ? 'bg-orange-100 text-orange-600' : 'bg-emerald-100 text-emerald-600'"
        >
          <span class="leading-none">{{ getIconDisplay(cat.icon) }}</span>
        </div>
        <!-- Name -->
        <div class="text-center">
          <p class="font-headline-md text-[18px] text-on-surface">{{ cat.name }}</p>
          <p class="font-label-sm text-label-sm text-on-surface-variant opacity-60">
            {{ activeTab === 'expense' ? '支出' : '收入' }}
          </p>
        </div>
        <!-- System Badge -->
        <span
          v-if="!cat.userId"
          class="absolute top-2 right-2 px-2 py-0.5 bg-tertiary-container text-on-tertiary-container rounded-full text-[10px] font-bold"
          >系统</span
        >
        <!-- Delete Button -->
        <button
          v-if="cat.userId"
          @click.stop="handleDelete(cat)"
          class="absolute top-1 right-1 w-5 h-5 rounded-full bg-danger-red/80 text-white flex items-center justify-center text-[10px] hover:bg-danger-red active:bg-danger-red transition-colors"
        >
          x
        </button>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="categories.length === 0 && !loading" class="py-16 text-center">
      <AppstoreOutlined class="text-4xl text-outline" />
      <p class="font-body-md text-body-md text-on-surface-variant mt-3">暂无分类</p>
    </div>

    <!-- Background Decoration -->
    <div class="fixed top-0 left-0 w-full h-full pointer-events-none -z-10 overflow-hidden">
      <div class="absolute top-[10%] -right-20 w-64 h-64 bg-primary-fixed-dim/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-[20%] -left-20 w-80 h-80 bg-tertiary-fixed-dim/10 rounded-full blur-3xl"></div>
    </div>

    <!-- 新建/编辑弹窗 -->
    <a-modal
      :open="modalVisible"
      :title="isEdit ? '编辑分类' : '添加分类'"
      :footer="null"
      :width="340"
      centered
      @cancel="modalVisible = false"
    >
      <a-form :model="formState" layout="vertical" class="mt-2" @finish="handleSubmit">
        <a-form-item
          label="分类名称"
          name="name"
          :rules="[{ required: true, message: '请输入名称' }]"
        >
          <a-input v-model:value="formState.name" placeholder="如：餐饮、交通等" size="large" />
        </a-form-item>
        <a-form-item label="图标（emoji）">
          <a-select v-model:value="formState.icon" placeholder="选择图标" size="large">
            <a-select-option v-for="e in emojiOptions" :key="e" :value="e">
              <span class="text-xl">{{ e }}</span>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排序序号">
          <a-input-number
            v-model:value="formState.sortOrder"
            :min="0"
            style="width: 100%"
            size="large"
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { AppstoreOutlined } from '@ant-design/icons-vue'
import { categoryApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 预设 emoji 图标
const emojiOptions = [
  '🍔', '🍜', '🍕', '🍣', '🍰', '☕', '🍺', '🧃',
  '🚗', '🚌', '🚇', '✈️', '🚲', '⛽', '🅿️', '🚕',
  '🛒', '👕', '👗', '👟', '💄', '💍', '🎒', '🧥',
  '🏠', '💡', '🛏️', '🚿', '🔧', '🧹', '🔑', '📺',
  '💊', '🏥', '🏃', '🧘', '💊', '🩺', '💪', '🦷',
  '📚', '✏️', '🎓', '💻', '📱', '🎮', '🎵', '🎨',
  '💰', '💳', '🏦', '📈', '💵', '🪙', '💎', '🎁',
  '🎬', '🎭', '🎪', '🏖️', '🎡', '🎯', '🎲', '🎳',
  '👶', '🐱', '🐶', '💐', '🎂', '🎆', '🧧', '❤️',
]
const userId = computed(() => authStore.userInfo?.id)

const loading = ref(false)
const categories = ref([])
const activeTab = ref('expense')
const modalVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)

const formState = reactive({
  name: '',
  icon: '',
  sortOrder: 0,
})

// 图标显示：emoji直接显示，英文单词只取首字母大写
function getIconDisplay(icon) {
  if (!icon) return '?'
  // 如果是emoji（非ASCII字符），直接返回
  if (/[\u0080-\uffff]/.test(icon)) return icon
  // 英文单词取首字母大写
  return icon.charAt(0).toUpperCase()
}

// 切换分类类型 Tab
function switchTab(key) {
  activeTab.value = key
  loadData()
}

async function loadData() {
  if (!userId.value) return
  loading.value = true
  try {
    let res
    if (activeTab.value === 'expense') {
      res = await categoryApi.getExpenseList(userId.value)
    } else {
      res = await categoryApi.getIncomeList(userId.value)
    }
    categories.value = res.data
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  isEdit.value = false
  Object.assign(formState, { name: '', icon: '', sortOrder: 0 })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  editId.value = record.id
  Object.assign(formState, { name: record.name, icon: record.icon, sortOrder: record.sortOrder })
  modalVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await categoryApi.update(editId.value, formState)
      message.success('修改成功')
    } else {
      await categoryApi.save({
        ...formState,
        type: activeTab.value === 'expense' ? 1 : 2,
        userId: userId.value,
      })
      message.success('添加成功')
    }
    modalVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitting.value = false
  }
}

function handleDelete(cat) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除分类「${cat.name}」吗？`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await categoryApi.delete(cat.id)
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
