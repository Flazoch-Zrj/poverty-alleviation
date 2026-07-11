<template>
  <div class="cadre-dashboard">
    <!-- 欢迎横幅 -->
    <div class="hero">
      <div class="hero-text">
        <h1>👋 {{ userStore.userInfo.realName || '同志' }}，上午好</h1>
        <p>您负责 <strong>{{ dash.totalFamilies || 0 }}</strong> 户帮扶家庭，本月已走访 <strong>{{ dash.monthVisits || 0 }}</strong> 次</p>
      </div>
    </div>

    <!-- 指标卡 -->
    <div class="stats-row">
      <div class="stat-card"><div class="stat-value">{{ dash.totalFamilies || 0 }}</div><div class="stat-label">帮扶家庭</div></div>
      <div class="stat-card"><div class="stat-value">{{ dash.pendingMeasures || 0 }}</div><div class="stat-label">待办措施</div></div>
      <div class="stat-card"><div class="stat-value">{{ dash.monthVisits || 0 }}</div><div class="stat-label">本月走访</div></div>
      <div class="stat-card"><div class="stat-value">{{ dash.pendingReviews || 0 }}</div><div class="stat-label">待审核</div></div>
    </div>

    <div class="content-row">
      <!-- 我的家庭 -->
      <el-card class="section-card">
        <template #header><span style="font-weight:600;">🏠 我的帮扶家庭</span></template>
        <div v-if="dash.families?.length" class="family-list">
          <div v-for="f in dash.families" :key="f.familyId" class="family-item" @click="router.push('/cadre/family/' + f.familyId)">
            <div class="family-left">
              <span class="family-name">{{ f.householderName }}</span>
              <span class="family-code">{{ f.familyCode }}</span>
              <el-tag :type="f.status==='建档'?'warning':'success'" size="small">{{ f.status }}</el-tag>
            </div>
            <div class="family-center">
              <div class="progress-bar"><div class="progress-fill" :style="{width: (f.avgProgress||0)+'%'}"></div></div>
              <span class="progress-text">{{ f.avgProgress || 0 }}%</span>
            </div>
            <div class="family-right">
              <span v-if="f.daysSinceVisit === null" class="visit-badge">未走访</span>
              <span v-else-if="f.daysSinceVisit > 30" class="visit-badge danger">{{ f.daysSinceVisit }}天未走访</span>
              <span v-else class="visit-badge ok">已走访</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无帮扶家庭" />
      </el-card>

      <!-- 待办事项 -->
      <el-card class="section-card">
        <template #header><span style="font-weight:600;">📋 待办事项</span></template>
        <div v-if="dash.tasks?.length" class="task-list">
          <div v-for="(t, i) in dash.tasks" :key="i" class="task-item">
            <span class="task-dot" :class="t.urgency"></span>
            <div class="task-body">
              <span class="task-family">{{ t.familyName }}</span>
              <span class="task-desc">{{ t.content }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无待办事项，继续保持！" />
      </el-card>
    </div>

    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header><span style="font-weight:600;">⚡ 快捷操作</span></template>
      <div class="quick-actions">
        <el-button @click="router.push('/cadre/family')">📋 家庭档案</el-button>
        <el-button @click="router.push('/cadre/visit')">📝 新增走访</el-button>
        <el-button @click="router.push('/cadre/measure')">📌 制定措施</el-button>
        <el-button @click="router.push('/cadre/project')">📊 项目管理</el-button>
        <el-button @click="router.push('/cadre/job')">💼 就业管理</el-button>
        <el-button @click="router.push('/cadre/need')">📢 需求管理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCadreDashboard } from '@/api/system'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const dash = ref<any>({})

onMounted(async () => {
  try { const r = await getCadreDashboard(); dash.value = r.data || {} } catch {}
})
</script>

<style scoped>
.cadre-dashboard { padding: 24px; max-width: 1200px; margin: 0 auto; }
.hero { background: linear-gradient(135deg,#0A2647,#2D7A9B); border-radius: 16px; padding: 28px 32px; color: #fff; margin-bottom: 24px; }
.hero h1 { font-size: 22px; margin: 0 0 6px; }
.hero p { font-size: 14px; opacity: 0.85; margin: 0; }
.stats-row { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,.04); }
.stat-value { font-size: 28px; font-weight: 800; color: #0A2647; }
.stat-label { font-size: 12px; color: #64748B; margin-top: 4px; }
.content-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 24px; }
.section-card { border-radius: 12px; }
.family-list { display: flex; flex-direction: column; gap: 8px; }
.family-item { display: flex; align-items: center; gap: 12px; padding: 14px; border-radius: 10px; border: 1px solid #F1F5F9; cursor: pointer; transition: all .2s; }
.family-item:hover { background: #F8FAFC; border-color: #2D7A9B; }
.family-left { display: flex; align-items: center; gap: 8px; min-width: 0; flex: 1; }
.family-name { font-weight: 600; color: #1E293B; }
.family-code { font-size: 11px; color: #94A3B8; }
.family-center { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.progress-bar { width: 80px; height: 6px; background: #F1F5F9; border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg,#2D7A9B,#60A5FA); border-radius: 3px; transition: width .5s; }
.progress-text { font-size: 12px; font-weight: 600; color: #64748B; width: 30px; text-align: right; }
.family-right { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.visit-badge { padding: 2px 8px; border-radius: 8px; font-size: 11px; font-weight: 500; background: #D1FAE5; color: #059669; }
.visit-badge.danger { background: #FEE2E2; color: #DC2626; }
.visit-badge.ok { background: #DBEAFE; color: #2563EB; }
.task-list { display: flex; flex-direction: column; gap: 6px; }
.task-item { display: flex; align-items: center; gap: 10px; padding: 10px 12px; border-radius: 8px; background: #F8FAFC; }
.task-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.task-dot.high { background: #EF4444; } .task-dot.medium { background: #F59E0B; }
.task-body { display: flex; flex-direction: column; }
.task-family { font-size: 12px; font-weight: 600; color: #1E293B; }
.task-desc { font-size: 11px; color: #64748B; }
.quick-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.quick-actions .el-button { border-radius: 8px; }
@media(max-width:768px){ .content-row { grid-template-columns: 1fr; } .stats-row { grid-template-columns: repeat(2,1fr); } }
</style>
