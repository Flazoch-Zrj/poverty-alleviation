<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-avatar">
            <el-avatar :size="80" icon="UserFilled" />
          </div>
          <h3 class="user-name">{{ userInfo.realName || userInfo.username }}</h3>
          <p class="user-role">
            <el-tag :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
          </p>
          <el-divider />
          <div class="info-item"><span>用户名：</span>{{ userInfo.username }}</div>
          <div class="info-item"><span>手机号：</span>{{ userInfo.phone || '未设置' }}</div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header><span>操作记录</span></template>
          <el-timeline>
            <el-timeline-item timestamp="2024-01-15 10:30" placement="top" type="primary">
              登录系统
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-14 16:20" placement="top" type="success">
              更新个人资料
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-10 09:15" placement="top">
              首次登录系统
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const roleLabel = computed(() => {
  const map: Record<string, string> = { admin: '系统管理员', cadre: '帮扶责任人', poor: '贫困户', volunteer: '志愿者' }
  return map[userInfo.value.roleCode] || userInfo.value.roleCode
})
const roleTagType = computed(() => {
  const map: Record<string, string> = { admin: 'danger', cadre: 'warning', poor: 'success', volunteer: 'info' }
  return map[userInfo.value.roleCode] || 'info'
})
</script>

<style scoped lang="scss">
.profile-page { padding: 0; }
.user-card { text-align: center; }
.user-avatar { margin: 20px 0; }
.user-name { margin: 10px 0 5px; }
.user-role { margin-bottom: 15px; }
.info-item { text-align: left; padding: 8px 0; font-size: 14px; }
.info-item span { color: #999; }
</style>
