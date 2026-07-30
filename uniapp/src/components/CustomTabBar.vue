<template>
  <view class="fixed bottom-4 left-0 right-0 z-50 flex justify-around items-center px-2 py-2 mx-auto max-w-md bg-white/80 rounded-full border border-black/5 shadow-2xl" style="width: calc(100% - 38px);">
    <view v-for="tab in tabs" :key="tab.path" class="flex flex-col items-center justify-center relative" :class="tab.center ? 'scale-125 -translate-y-6 z-20' : 'z-10'" @click="switchTab(tab.path)">
      <view v-if="tab.center" class="flex flex-col items-center justify-center bg-gradient-to-r from-[#983f19] to-[#ab3500] rounded-full px-2 py-1 text-white shadow-lg active:scale-110 transition-transform">
        <text class="text-3xl" style="font-family: 'Material Symbols Outlined'; font-variation-settings: 'FILL' 1;">{{ tab.icon }}</text>
      </view>
      <template v-else>
        <text class="text-3xl transition-colors" :style="isActive(tab.path) ? 'font-family: Material Symbols Outlined; font-variation-settings: FILL 1;' : 'font-family: Material Symbols Outlined;'" :class="isActive(tab.path) ? 'text-primary' : 'text-on-surface-variant'">{{ tab.icon }}</text>
        <text class="text-[10px] mt-1 font-bold" :class="isActive(tab.path) ? 'text-primary' : 'text-on-surface-variant'">{{ tab.label }}</text>
      </template>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  current: { type: String, default: '' }
})

const tabs = [
  { path: '/pages/dashboard/dashboard', icon: 'home', label: '首页' },
  { path: '/pages/books/books', icon: 'account_balance_wallet', label: '账本' },
  { path: '/pages/transaction/transaction', icon: 'mic', label: '', center: true },
  { path: '/pages/records/records', icon: 'history', label: '流水' },
  { path: '/pages/category/category', icon: 'grid_view', label: '分类' },
]

function isActive(path) {
  return props.current === path
}

function switchTab(path) {
  if (isActive(path)) return
  uni.reLaunch({ url: path })
}
</script>
