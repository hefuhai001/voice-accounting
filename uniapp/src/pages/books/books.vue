<template>
  <view class="flex-1 bg-background min-h-screen">
    <CustomHeader />

    <view class="px-4 pb-32 flex flex-col gap-4">
      <!-- Header Section -->
      <view class="flex justify-between items-end mt-6">
        <view>
          <text class="font-display-lg text-display-lg text-on-surface tracking-tight mb-1 block">我的账本</text>
          <text class="text-on-surface-variant font-label-caps text-label-caps uppercase" style="letter-spacing: 0.2em;">MY LEDGERS</text>
        </view>
        <view
          class="bg-primary text-white px-8 py-3 rounded-full font-bold text-sm shadow-xl active:scale-95 transition-all flex items-center gap-2"
          style="box-shadow: 0 10px 25px rgba(var(--shadow-primary-rgb, 99,102,241), 0.25);"
          @click="openAddModal"
        >
          <text class="material-symbols-outlined text-lg">add</text>
          <text class="text-white font-bold text-sm">新建账本</text>
        </view>
      </view>

      <!-- Card Flow -->
      <view class="flex flex-col gap-4 mt-4">
        <view
          v-for="book in books"
          :key="book.id"
          class="glass-panel rounded-3xl p-4 relative overflow-hidden"
          :class="[
            book.type === 2 ? 'border-l-8 border-l-secondary' : '',
            book.type === 3 ? 'ml-6' : ''
          ]"
          @click="openEditModal(book)"
        >
          <!-- Decorative blur circle -->
          <view
            class="absolute right-0 top-0 w-48 h-48 rounded-full -mr-24 -mt-24"
            :class="book.type === 1 ? 'bg-primary/5' : book.type === 2 ? 'bg-secondary/5' : 'bg-tertiary/5'"
            style="filter: blur(48px);"
          ></view>

          <view class="relative z-10 flex flex-col justify-between items-start gap-3">
            <view class="flex gap-4 items-center">
              <view
                class="w-12 h-12 rounded-xl flex items-center justify-center border"
                :class="getIconBgClass(book.type)"
              >
                <text class="material-symbols-outlined text-2xl" :class="getIconColorClass(book.type)">{{ getBookIcon(book.type) }}</text>
              </view>
              <view>
                <text class="text-2xl font-bold text-on-surface block">{{ book.name }}</text>
                <text class="text-on-surface-variant text-xs font-bold tracking-widest uppercase mt-1 block">{{ getTypeName(book.type) }}</text>
                <text v-if="book.description" class="text-outline text-sm mt-1 block">{{ book.description }}</text>
              </view>
            </view>

            <view class="w-full flex items-center justify-between gap-4">
              <view class="flex items-center gap-2">
                <view
                  v-if="book.isDefault === 1"
                  class="inline-flex items-center px-3 py-1 rounded-full bg-secondary-fixed/20"
                >
                  <text class="material-symbols-outlined text-secondary mr-1" style="font-size: 14px;">check_circle</text>
                  <text class="text-secondary text-[11px] font-bold">默认</text>
                </view>
              </view>

              <!-- Delete Button -->
              <view
                v-if="book.userId"
                class="text-outline flex items-center gap-1 text-sm"
                @click.stop="handleDelete(book)"
              >
                <text class="material-symbols-outlined text-lg text-outline">delete</text>
                <text class="text-outline text-sm">删除</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="books.length === 0" class="glass-panel rounded-3xl p-12 text-center flex flex-col items-center">
        <text class="material-symbols-outlined text-6xl text-outline mb-4 block">account_balance_wallet</text>
        <text class="font-body-md text-body-md text-on-surface-variant block">还没有账本</text>
        <view
          class="mt-4 bg-primary text-white px-6 py-2 rounded-full font-bold text-sm"
          @click="openAddModal"
        >
          <text class="text-white font-bold text-sm">创建第一个账本</text>
        </view>
      </view>
    </view>

    <!-- Add/Edit Modal -->
    <view v-if="modalVisible" class="fixed inset-0 z-50 flex items-center justify-center">
      <view class="absolute inset-0 bg-black/30" @click="modalVisible = false"></view>
      <view class="relative bg-white rounded-3xl p-6 z-10 w-[85vw] max-w-[340px]">
        <text class="text-headline-lg text-on-surface block">{{ isEdit ? '编辑账本' : '新建账本' }}</text>

        <!-- Name -->
        <view class="mt-5">
          <text class="text-label-md text-on-surface-variant mb-1.5 block">账本名称</text>
          <input
            v-model="form.name"
            type="text"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入账本名称"
          />
        </view>

        <!-- Type -->
        <view class="mt-4">
          <text class="text-label-md text-on-surface-variant mb-1.5 block">账本类型</text>
          <view class="flex gap-2">
            <view
              v-for="t in typeOptions"
              :key="t.value"
              class="flex-1 py-2 rounded-xl text-center transition-all"
              :class="form.type === t.value ? 'bg-primary/10 border border-primary' : 'bg-surface-container border border-transparent'"
              @click="form.type = t.value"
            >
              <text class="text-body-md" :class="form.type === t.value ? 'text-primary font-semibold' : 'text-on-surface-variant'">{{ t.label }}</text>
            </view>
          </view>
        </view>

        <!-- Description -->
        <view class="mt-4">
          <text class="text-label-md text-on-surface-variant mb-1.5 block">描述</text>
          <textarea
            v-model="form.description"
            class="w-full h-20 px-3 py-2 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary resize-none box-border"
            placeholder="请输入账本描述（选填）"
          />
        </view>

        <!-- Default Toggle -->
        <view class="mt-4 flex items-center justify-between">
          <text class="text-body-md text-on-surface">设为默认账本</text>
          <view
            class="w-12 h-7 rounded-full flex items-center px-0.5 transition-colors"
            :class="form.isDefault === 1 ? 'bg-primary justify-end' : 'bg-surface-container-high justify-start'"
            @click="form.isDefault = form.isDefault === 1 ? 0 : 1"
          >
            <view class="w-6 h-6 rounded-full bg-white shadow"></view>
          </view>
        </view>

        <!-- Buttons -->
        <view class="mt-6 flex gap-3">
          <view class="flex-1 py-2.5 rounded-xl bg-surface-container text-on-surface-variant text-center text-body-md font-semibold active:scale-95 transition-transform" @click="modalVisible = false">
            <text>取消</text>
          </view>
          <view class="flex-1 py-2.5 rounded-xl bg-primary text-on-primary text-center text-body-md font-semibold active:scale-95 transition-transform" @click="handleSave">
            <text>保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- TabBar -->
    <CustomTabBar current="/pages/books/books" />
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { bookApi } from '@/api/user/book'
import { useAuthStore } from '@/stores/auth'
import CustomTabBar from '@/components/CustomTabBar.vue'
import CustomHeader from '@/components/CustomHeader.vue'

const authStore = useAuthStore()
const books = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)

const typeOptions = [
  { value: 1, label: '日常' },
  { value: 2, label: '旅行' },
  { value: 3, label: '共享' },
]

const form = reactive({
  name: '',
  type: 1,
  description: '',
  isDefault: 0,
})

function getTypeName(type) {
  const map = { 1: 'DAILY SPENDING', 2: 'JAPAN TRIP', 3: 'INVESTMENT' }
  return map[type] || 'UNKNOWN'
}

function getBookIcon(type) {
  const map = { 1: 'shopping_cart', 2: 'flight_takeoff', 3: 'monitoring' }
  return map[type] || 'book'
}

function getIconBgClass(type) {
  const map = {
    1: 'bg-indigo-50 border-indigo-100',
    2: 'bg-emerald-50 border-emerald-100',
    3: 'bg-orange-50 border-orange-100',
  }
  return map[type] || 'bg-surface-container'
}

function getIconColorClass(type) {
  const map = {
    1: 'text-indigo-500',
    2: 'text-emerald-500',
    3: 'text-orange-600',
  }
  return map[type] || 'text-primary'
}

function openAddModal() {
  isEdit.value = false
  editingId.value = null
  form.name = ''
  form.type = 1
  form.description = ''
  form.isDefault = 0
  modalVisible.value = true
}

function openEditModal(book) {
  isEdit.value = true
  editingId.value = book.id
  form.name = book.name
  form.type = book.type
  form.description = book.description || ''
  form.isDefault = book.isDefault || 0
  modalVisible.value = true
}

async function loadData() {
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    const res = await bookApi.getPage({ userId, current: 1, size: 100 })
    books.value = res.data?.records || res.data || []
  } catch (e) {
    console.error('加载账本失败:', e)
  }
}

async function handleSave() {
  if (!form.name.trim()) {
    uni.showToast({ title: '请输入账本名称', icon: 'none' })
    return
  }
  try {
    const userId = authStore.userInfo?.id
    const data = {
      name: form.name.trim(),
      type: form.type,
      description: form.description.trim(),
      isDefault: form.isDefault,
      userId,
    }
    if (isEdit.value) {
      await bookApi.update(editingId.value, data)
      uni.showToast({ title: '修改成功', icon: 'success' })
    } else {
      await bookApi.save(data)
      uni.showToast({ title: '创建成功', icon: 'success' })
    }
    modalVisible.value = false
    loadData()
  } catch (e) {
    console.error('保存失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

function handleDelete(book) {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除账本「${book.name}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await bookApi.delete(book.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadData()
        } catch (e) {
          console.error('删除失败:', e)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    },
  })
}

onMounted(async () => {
  // 检查登录状态
  if (!authStore.isLoginChecked) {
    await authStore.checkLoginStatus()
  }
  // 如果未登录，跳转到登录页
  if (!authStore.userInfo?.id) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  // 如果已登录，加载数据
  loadData()
})
</script>

<style scoped>
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
.glass-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.glass-panel:active {
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}
.rounded-3xl {
  border-radius: 2.5rem;
}
</style>
