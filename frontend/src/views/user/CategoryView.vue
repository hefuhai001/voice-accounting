<template>
  <div class="px-4 pt-4 pb-6 space-y-4">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between px-1">
      <h2 class="text-[22px] font-bold text-[#1c1c1e]">分类管理</h2>
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

    <!-- 类型切换 Tab -->
    <div class="flex bg-[#f2f2f7] rounded-xl p-1">
      <button
        v-for="t in [
          { key: 'expense', label: '支出分类' },
          { key: 'income', label: '收入分类' },
        ]"
        :key="t.key"
        @click="switchTab(t.key)"
        class="flex-1 py-2 text-sm font-semibold rounded-lg transition-all duration-200"
        :class="activeTab === t.key ? 'bg-white text-[#1c1c1e] shadow-sm' : 'text-[#8e8e93]'"
      >
        {{ t.label }}
      </button>
    </div>

    <!-- 分类网格 -->
    <div class="grid grid-cols-4 gap-2.5">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="rounded-2xl p-2.5 pb-2 flex flex-col items-center gap-1.5 relative active:scale-95 transition-transform duration-150"
        :class="[
          cat.userId ? 'cursor-pointer' : '',
          activeTab === 'expense' ? 'bg-gradient-to-br from-orange-100 to-orange-50/80' : 'bg-gradient-to-br from-emerald-100 to-green-50/80'
        ]"
        @click="cat.userId && handleEdit(cat)"
      >
        <!-- 图标 -->
        <div
          class="w-11 h-11 rounded-2xl flex items-center justify-center text-2xl shadow-sm"
          :class="activeTab === 'expense' ? 'bg-white/80' : 'bg-white/80'"
        >
          <span class="leading-none">{{ getIconDisplay(cat.icon) }}</span>
        </div>
        <!-- 名称 -->
        <span
          class="text-[12px] font-semibold text-[#1c1c1e] text-center leading-tight line-clamp-1 w-full"
          >{{ cat.name }}</span
        >
        <!-- 类型小字 -->
        <span class="text-[10px] text-[#aeaeb2] -mt-0.5">{{ activeTab === 'expense' ? '支出' : '收入' }}</span>
        <!-- 系统标签 -->
        <span
          v-if="!cat.userId"
          class="absolute top-1 right-1 text-[8px] text-blue-400 bg-blue-50/80 px-1 py-px rounded"
          >系统</span
        >
        <!-- 删除按钮 -->
        <button
          v-if="cat.userId"
          @click.stop="handleDelete(cat)"
          class="absolute top-0.5 right-0.5 w-4.5 h-4.5 rounded-full bg-red-400/80 text-white flex items-center justify-center text-[9px] hover:bg-red-500 active:bg-red-600 transition-colors"
          style="width:18px;height:18px"
        >
          x
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="categories.length === 0 && !loading" class="py-16 text-center">
      <AppstoreOutlined class="text-4xl text-[#c7c7cc]" />
      <p class="text-sm text-[#8e8e93] mt-3">暂无分类</p>
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
