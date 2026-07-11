<template>
  <div class="px-margin-mobile pb-32 max-w-lg mx-auto">
    <!-- Header Section -->
    <section class="mb-8 mt-8">
      <h2 class="font-display-lg text-display-lg mb-2 text-primary">分类管理</h2>
      <p class="text-on-surface-variant font-medium">自定义您的财务领域</p>
    </section>

    <!-- Toggle Switch -->
    <div class="bg-white rounded-3xl p-1.5 flex mb-8 relative border border-black/5 shadow-sm">
      <div
        class="absolute inset-y-1.5 w-[calc(50%-6px)] bg-primary rounded-2xl shadow-lg shadow-primary/20 transition-all duration-300"
        :class="activeTab === 'expense' ? 'left-1.5' : 'left-[calc(50%+1.5px)]'"
      ></div>
      <button
        @click="switchTab('expense')"
        class="flex-1 py-3 text-center z-10 font-bold transition-colors"
        :class="activeTab === 'expense' ? 'text-white' : 'text-on-surface-variant'"
      >
        支出
      </button>
      <button
        @click="switchTab('income')"
        class="flex-1 py-3 text-center z-10 font-bold transition-colors"
        :class="activeTab === 'income' ? 'text-white' : 'text-on-surface-variant'"
      >
        收入
      </button>
    </div>

    <!-- Category Grid (Bento Style) -->
    <div class="grid grid-cols-2 gap-4 auto-rows-[130px]">
      <div
        v-for="(cat, index) in categories"
        :key="cat.id"
        class="category-card glass-panel rounded-3xl p-4 flex flex-col items-center justify-center gap-2 cursor-pointer relative"
        :class="{
          'col-span-2 flex-row justify-between px-6': index === 2,
          'row-span-2': index === 3,
          'cursor-default': !cat.userId
        }"
        @click="cat.userId && handleEdit(cat)"
      >
        <!-- Icon -->
        <div
          class="rounded-2xl flex items-center justify-center"
          :class="[
            getIconBgClass(cat.name, index),
            index === 2 ? 'w-14 h-14' : index === 3 ? 'w-16 h-16' : 'w-12 h-12'
          ]"
        >
          <span
            class="material-symbols-outlined"
            :class="getIconColorClass(cat.name, index)"
            :style="`font-size: ${index === 3 ? '36px' : index === 2 ? '32px' : '28px'}; font-variation-settings: 'FILL' 1;`"
          >
            {{ getCategoryIcon(cat.name) }}
          </span>
        </div>

        <!-- Name -->
        <div class="text-center" :class="{ 'text-left': index === 2 }">
          <span class="font-bold text-on-surface block" :class="index === 3 ? 'text-center' : ''">{{ cat.name }}</span>
          <span v-if="index === 2" class="text-xs text-on-surface-variant font-medium">自定义分类</span>
        </div>

        <!-- Badge for row-span-2 -->
        <div v-if="index === 3" class="mt-2 px-3 py-1 bg-surface-container-low border border-black/5 rounded-full text-[10px] text-on-surface-variant font-bold">
          常用
        </div>

        <!-- System Badge -->
        <span
          v-if="!cat.userId"
          class="absolute top-2 right-2 px-2 py-0.5 bg-tertiary-container text-on-tertiary-container rounded-full text-[10px] font-bold"
        >系统</span>

        <!-- Delete Button -->
        <button
          v-if="cat.userId"
          @click.stop="handleDelete(cat)"
          class="absolute top-2 right-2 w-6 h-6 rounded-full bg-error/80 text-white flex items-center justify-center text-xs hover:bg-error active:bg-error transition-colors"
        >
          <span class="material-symbols-outlined text-[14px]">close</span>
        </button>
      </div>

      <!-- Add New (Dashed) -->
      <div
        @click="showAddModal"
        class="col-span-2 mt-4 category-card bg-white border-2 border-dashed border-primary/30 rounded-3xl p-4 flex flex-col items-center justify-center gap-2 cursor-pointer hover:border-primary transition-all group"
      >
        <div class="w-10 h-10 rounded-full bg-primary/5 flex items-center justify-center group-hover:bg-primary/10 transition-colors">
          <span class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 0;">add</span>
        </div>
        <span class="text-sm font-bold text-primary">添加自定义分类</span>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="categories.length === 0 && !loading" class="glass-panel rounded-3xl p-12 text-center mt-4">
      <span class="material-symbols-outlined text-6xl text-outline mb-4 block">grid_view</span>
      <p class="font-body-md text-body-md text-on-surface-variant">暂无分类</p>
    </div>

    <!-- Add/Edit Modal -->
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
        <a-form-item label="图标">
          <a-select v-model:value="formState.icon" placeholder="选择图标" size="large">
            <a-select-option v-for="icon in iconOptions" :key="icon.value" :value="icon.value">
              <span class="material-symbols-outlined text-[16px] mr-2">{{ icon.value }}</span>
              {{ icon.label }}
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
          <a-button type="primary" block size="large" html-type="submit" :loading="submitting">保存</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { categoryApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
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
    居住: 'bg-gray-100',
    医疗: 'bg-gray-100',
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
    居住: 'text-on-surface',
    医疗: 'text-on-surface',
  }
  return map[name] || 'text-on-surface'
}

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
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px -15px rgba(0, 0, 0, 0.08);
  border-color: theme('colors.primary');
}

.category-card:active {
  transform: scale(0.96);
}

.rounded-3xl {
  border-radius: 3.5rem;
}

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}
</style>
