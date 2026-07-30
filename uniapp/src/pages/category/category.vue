<template>
  <view class="flex-1 min-h-screen bg-surface">
    <CustomHeader />

    <view class="px-4 pb-32">
      <!-- Header Section -->
      <view class="mb-8 mt-8">
        <text class="font-display-lg text-display-lg mb-2 text-primary block">分类管理</text>
        <text class="text-on-surface-variant font-medium">自定义您的财务领域</text>
      </view>

      <!-- Toggle Switch -->
      <view class="bg-white rounded-3xl p-1 flex mb-8 relative border border-black/5 shadow-sm box-border">
        <view
          class="absolute inset-y-1.5 w-[calc(50%-6px)] bg-primary rounded-2xl shadow-lg shadow-primary/20 transition-all duration-300"
          :class="activeTab === 'expense' ? 'left-1.5' : 'left-[calc(50%+1.5px)]'"
        ></view>
        <view
          class="flex-1 py-3 text-center relative z-10 font-bold transition-colors"
          @click="switchTab('expense')"
        >
          <text :class="activeTab === 'expense' ? 'text-white' : 'text-on-surface-variant'">支出</text>
        </view>
        <view
          class="flex-1 py-3 text-center relative z-10 font-bold transition-colors"
          @click="switchTab('income')"
        >
          <text :class="activeTab === 'income' ? 'text-white' : 'text-on-surface-variant'">收入</text>
        </view>
      </view>

      <!-- Category Grid (Bento Style) -->
      <view class="grid grid-cols-2 gap-4 auto-rows-[130px]">
        <view
          v-for="(cat, index) in categories"
          :key="cat.id"
          class="category-card glass-panel rounded-3xl p-4 flex flex-col items-center justify-center gap-2 relative"
          :class="{
            'col-span-2 flex-row justify-between px-6': index === 2,
            'row-span-2': index === 3
          }"
          @click="cat.userId && openEditModal(cat)"
        >
          <!-- Icon -->
          <view
            class="rounded-2xl flex items-center justify-center"
            :class="[
              getIconBgClass(cat.name, index),
              index === 2 ? 'w-14 h-14' : index === 3 ? 'w-16 h-16' : 'w-12 h-12'
            ]"
          >
            <text
              class="material-symbols-outlined"
              :class="getIconColorClass(cat.name, index)"
              :style="{ fontSize: index === 3 ? '36px' : index === 2 ? '32px' : '28px', fontVariationSettings: 'FILL 1' }"
            >{{ getCategoryIcon(cat.name) }}</text>
          </view>

          <!-- Name -->
          <view class="text-center" :class="{ 'text-left': index === 2 }">
            <text class="font-bold text-on-surface block" :class="index === 3 ? 'text-center' : ''">{{ cat.name }}</text>
            <text v-if="index === 2" class="text-xs text-on-surface-variant font-medium">自定义分类</text>
          </view>

          <!-- Badge for row-span-2 -->
          <view v-if="index === 3" class="mt-2 px-3 py-1 bg-surface-container-low border border-black/5 rounded-full text-[10px] text-on-surface-variant font-bold">
            <text class="text-[10px] text-on-surface-variant font-bold">常用</text>
          </view>

          <!-- System Badge -->
          <view
            v-if="!cat.userId"
            class="absolute top-2 right-2 px-2 py-0.5 bg-tertiary-container rounded-full flex items-center justify-center"
          >
            <text class="text-[10px] text-on-tertiary-container font-bold">系统</text>
          </view>

          <!-- Delete Button -->
          <view
            v-if="cat.userId"
            class="absolute top-2 right-2 w-6 h-6 rounded-full bg-error/80 flex items-center justify-center active:bg-error transition-colors"
            @click.stop="deleteCategory(cat)"
          >
            <text class="material-symbols-outlined text-[14px] text-white">close</text>
          </view>
        </view>

        <!-- Add New (Dashed) -->
        <view
          class="col-span-2 mt-4 category-card bg-white border-2 border-dashed border-primary/30 rounded-3xl p-4 flex flex-col items-center justify-center gap-2 active:border-primary transition-all"
          @click="openAddModal"
        >
          <view class="w-10 h-10 rounded-full bg-primary/5 flex items-center justify-center">
            <text class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 0;">add</text>
          </view>
          <text class="text-sm font-bold text-primary">添加自定义分类</text>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="categories.length === 0" class="glass-panel rounded-3xl p-12 text-center mt-4">
        <text class="material-symbols-outlined text-6xl text-outline mb-4 block">grid_view</text>
        <text class="font-body-md text-body-md text-on-surface-variant block">暂无分类</text>
      </view>
    </view>

    <!-- Add/Edit Modal -->
    <view v-if="showModal" class="fixed inset-0 z-50">
      <view class="absolute inset-0 bg-black/40" style="z-index: 1;" @click="showModal = false"></view>
      <view class="absolute top-1/2 left-1/2 w-[85vw] max-w-md bg-surface-bright rounded-3xl p-6 pb-8" style="z-index: 2; transform: translate(-50%, -50%);">
        <text class="text-headline-lg text-on-surface mb-6 block">{{ editingCategory ? '编辑分类' : '添加分类' }}</text>

        <!-- Name -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-1 block">名称</text>
          <input
            v-model="form.name"
            type="text"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-on-surface text-body-md border border-outline/20 focus:border-primary box-border"
            placeholder="请输入分类名称"
          />
        </view>

        <!-- Icon Picker -->
        <view class="mb-4">
          <text class="text-label-md text-on-surface-variant mb-2 block">图标</text>
          <view class="flex flex-wrap gap-2">
            <view
              v-for="icon in iconOptions"
              :key="icon.value"
              class="w-10 h-10 rounded-xl flex items-center justify-center transition-all"
              :class="form.icon === icon.value ? 'bg-primary/15 ring-2 ring-primary' : 'bg-surface-container'"
              @click="form.icon = icon.value"
            >
              <text class="material-symbols-outlined text-xl" :class="form.icon === icon.value ? 'text-primary' : 'text-on-surface-variant'">{{ icon.value }}</text>
            </view>
          </view>
        </view>

        <!-- Sort Order -->
        <view class="mb-6">
          <text class="text-label-md text-on-surface-variant mb-1 block">排序</text>
          <input
            v-model="form.sort"
            type="number"
            class="w-full h-11 px-3 rounded-xl bg-surface-container text-body-md text-on-surface border border-outline/20 focus:border-primary box-border"
            placeholder="排序值（越小越靠前）"
          />
        </view>

        <!-- Buttons -->
        <view class="flex gap-3">
          <view
            class="flex-1 py-3 rounded-full bg-surface-container text-center"
            @click="showModal = false"
          >
            <text class="text-label-md text-on-surface-variant">取消</text>
          </view>
          <view
            class="flex-1 py-3 rounded-full bg-primary text-center"
            @click="saveCategory"
          >
            <text class="text-label-md text-white">保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- TabBar -->
    <CustomTabBar current="/pages/category/category" />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { categoryApi } from '@/api/user/category'
import { useAuthStore } from '@/stores/auth'
import CustomTabBar from '@/components/CustomTabBar.vue'
import CustomHeader from '@/components/CustomHeader.vue'

const authStore = useAuthStore()
const userId = computed(() => authStore.userInfo?.id)

const categories = ref([])
const activeTab = ref('expense')
const showModal = ref(false)
const editingCategory = ref(null)

const iconOptions = [
  { value: 'restaurant', label: '餐饮' },
  { value: 'directions_car', label: '交通' },
  { value: 'local_mall', label: '购物' },
  { value: 'movie', label: '娱乐' },
  { value: 'home', label: '居住' },
  { value: 'medical_services', label: '医疗' },
  { value: 'school', label: '教育' },
  { value: 'payments', label: '工资' },
  { value: 'savings', label: '储蓄' },
  { value: 'card_giftcard', label: '礼物' },
  { value: 'flight', label: '旅行' },
  { value: 'pets', label: '宠物' },
]

const form = reactive({
  name: '',
  icon: 'restaurant',
  sort: 0,
  type: 'expense',
  userId: null
})

function getCategoryIcon(name) {
  const map = {
    餐饮: 'restaurant',
    交通: 'directions_car',
    购物: 'local_mall',
    娱乐: 'movie',
    居住: 'home',
    医疗: 'medical_services',
    教育: 'school',
    工资: 'payments',
    储蓄: 'savings',
    礼物: 'card_giftcard',
    旅行: 'flight',
    宠物: 'pets',
  }
  return map[name] || 'category'
}

function getIconBgClass(name, index) {
  if (index === 3) return 'bg-error/10'
  if (index === 2) return 'bg-tertiary/10'

  const map = {
    餐饮: 'bg-primary/10',
    交通: 'bg-secondary/10',
    购物: 'bg-tertiary/10',
    娱乐: 'bg-error/10',
  }
  return map[name] || 'bg-gray-100'
}

function getIconColorClass(name, index) {
  if (index === 3) return 'text-error'
  if (index === 2) return 'text-tertiary'

  const map = {
    餐饮: 'text-primary',
    交通: 'text-secondary',
    购物: 'text-tertiary',
    娱乐: 'text-error',
  }
  return map[name] || 'text-on-surface'
}

function switchTab(key) {
  activeTab.value = key
  loadData()
}

async function loadData() {
  const uid = userId.value
  if (!uid) return
  try {
    const res = activeTab.value === 'expense'
      ? await categoryApi.getExpenseList(uid)
      : await categoryApi.getIncomeList(uid)
    categories.value = res.data || []
  } catch (e) {
    console.error('加载分类失败:', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function openAddModal() {
  editingCategory.value = null
  form.name = ''
  form.icon = 'restaurant'
  form.sort = categories.value.length
  form.type = activeTab.value
  form.userId = userId.value
  showModal.value = true
}

function openEditModal(cat) {
  editingCategory.value = cat
  form.name = cat.name
  form.icon = cat.icon || getCategoryIcon(cat.name)
  form.sort = cat.sort || 0
  form.type = cat.type || activeTab.value
  form.userId = cat.userId
  showModal.value = true
}

async function saveCategory() {
  if (!form.name.trim()) {
    uni.showToast({ title: '请输入分类名称', icon: 'none' })
    return
  }
  try {
    const data = {
      name: form.name,
      icon: form.icon,
      sort: form.sort,
      type: form.type,
      userId: form.userId
    }
    if (editingCategory.value) {
      await categoryApi.update(editingCategory.value.id, data)
    } else {
      await categoryApi.save(data)
    }
    showModal.value = false
    uni.showToast({ title: '保存成功', icon: 'success' })
    await loadData()
  } catch (e) {
    console.error('保存分类失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

function deleteCategory(cat) {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除分类"${cat.name}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await categoryApi.delete(cat.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          await loadData()
        } catch (e) {
          console.error('删除分类失败:', e)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
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
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  -webkit-font-smoothing: antialiased;
}

.glass-panel {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.category-card:active {
  transform: scale(0.96);
}

.rounded-3xl {
  border-radius: 3.5rem;
}
</style>
