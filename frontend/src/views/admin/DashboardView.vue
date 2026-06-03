<template>
  <div class="admin-dashboard">
    <a-row :gutter="[16, 16]">
      <!-- 统计卡片 -->
      <a-col :span="6">
        <a-card>
          <a-statistic title="总用户数" :value="stats.totalUsers" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日新增" :value="stats.todayNewUsers" suffix="人" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="活跃用户" :value="stats.activeUsers" suffix="人" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="记账总数" :value="stats.totalRecords" suffix="笔" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <!-- 最近注册用户 -->
      <a-col :span="12">
        <a-card title="最近注册用户" :bordered="false">
          <a-list :data-source="recentUsers" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta :title="item.username" :description="item.email">
                  <template #avatar>
                    <a-avatar>{{ item.username?.charAt(0) }}</a-avatar>
                  </template>
                </a-list-item-meta>
                <div>{{ item.createdAt }}</div>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <!-- 系统信息 -->
      <a-col :span="12">
        <a-card title="系统信息" :bordered="false">
          <a-descriptions :column="1" bordered size="small">
            <descriptions-item label="系统版本">Voice Accounting v1.0.0</descriptions-item>
            <descriptions-item label="后端框架">Spring Boot 4.x + MyBatis-Plus</descriptions-item>
            <descriptions-item label="前端框架">Vue 3 + Ant Design Vue</descriptions-item>
            <descriptions-item label="认证框架">Sa-Token + Redis</descriptions-item>
            <descriptions-item label="数据库">MySQL 8.x</descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// 统计数据（示例）
const stats = reactive({
  totalUsers: 128,
  todayNewUsers: 5,
  activeUsers: 42,
  totalRecords: 3650,
})

// 最近用户（示例）
const recentUsers = ref([
  { id: 1, username: 'user001', email: 'user001@example.com', createdAt: '2026-06-03' },
  { id: 2, username: 'test_user', email: 'test@example.com', createdAt: '2026-06-02' },
  { id: 3, username: 'newbie', email: 'newbie@test.com', createdAt: '2026-06-02' },
])
</script>
