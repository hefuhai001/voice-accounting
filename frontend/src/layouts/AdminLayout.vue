<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      width="240"
      theme="dark"
    >
      <div class="logo">
        <SettingOutlined style="font-size: 24px; color: #52c41a" />
        <span v-if="!collapsed" class="logo-text">管理后台</span>
      </div>

      <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
        <a-menu-item key="/manage/dashboard" @click="$router.push('/manage/dashboard')">
          <template #icon><DashboardOutlined /></template>
          <span>控制台</span>
        </a-menu-item>

        <a-menu-item key="/manage/users" @click="$router.push('/manage/users')">
          <template #icon><UserOutlined /></template>
          <span>用户管理</span>
        </a-menu-item>

        <a-sub-menu key="data-manage">
          <template #icon><DatabaseOutlined /></template>
          <template #title>数据管理</template>
          <a-menu-item key="/manage/books" @click="$router.push('/manage/books')"
            >账本管理</a-menu-item
          >
          <a-menu-item key="/manage/categories" @click="$router.push('/manage/categories')"
            >分类管理</a-menu-item
          >
          <a-menu-item key="/manage/transactions" @click="$router.push('/manage/transactions')"
            >记账记录</a-menu-item
          >
          <a-menu-item key="/manage/reminders" @click="$router.push('/manage/reminders')"
            >提醒管理</a-menu-item
          >
        </a-sub-menu>

        <a-menu-item key="/" @click="$router.push('/')">
          <template #icon><HomeOutlined /></template>
          <span>返回前台</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <!-- 右侧内容区 -->
    <a-layout>
      <!-- 头部 -->
      <a-layout-header class="header">
        <div class="header-left">
          <MenuUnfoldOutlined v-if="collapsed" class="trigger" @click="collapsed = false" />
          <MenuFoldOutlined v-else class="trigger" @click="collapsed = true" />
          <a-breadcrumb style="margin-left: 16px">
            <a-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <a-tag color="blue">管理员</a-tag>
          <a-dropdown>
            <a-space style="cursor: pointer; margin-left: 16px">
              <a-avatar :size="32" style="background-color: #f56a00"> A </a-avatar>
              <span>Admin</span>
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">个人信息</a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>

      <!-- 底部 -->
      <a-layout-footer class="footer">
        Voice Accounting Admin ©{{ new Date().getFullYear() }}
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  SettingOutlined,
  DashboardOutlined,
  UserOutlined,
  DatabaseOutlined,
  HomeOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const collapsed = ref(false)
const selectedKeys = ref(['/admin/dashboard'])

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta && item.meta.title)
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title,
  }))
})

// 监听路由变化更新菜单选中状态
watch(
  () => route.path,
  (path) => {
    if (path.startsWith('/manage')) {
      // 找到最匹配的菜单项
      const menuItems = [
        '/manage/dashboard',
        '/manage/users',
        '/manage/books',
        '/manage/categories',
        '/manage/transactions',
        '/manage/reminders',
      ]
      const matched = menuItems.find((item) => path.startsWith(item))
      if (matched) {
        selectedKeys.value = [matched]
      }
    }
  },
  { immediate: true },
)

// 退出登录
const handleLogout = async () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
  padding: 0 12px;
}

.trigger:hover {
  color: #1890ff;
}

.content {
  margin: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: 280px;
}

.footer {
  text-align: center;
  color: #999;
}
</style>
