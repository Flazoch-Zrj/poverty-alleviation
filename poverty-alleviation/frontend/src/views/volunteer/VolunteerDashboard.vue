<template>
  <div class="vol-dashboard">
    <div class="hero">
      <div>
        <h1>👋 {{ userStore.userInfo.realName || '志愿者' }}，上午好</h1>
        <p>{{ impact.totalScore || 0 }} 分 · {{ impact.pairedFamilies || 0 }} 户结对家庭 · {{ impact.totalServices || 0 }} 次服务</p>
      </div>
      <div class="hero-score">
        <span class="score-num">{{ impact.totalScore || 0 }}</span>
        <span class="score-lbl">积分</span>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card" v-for="s in statList" :key="s.label">
        <div class="stat-icon" :style="{background:s.color}">{{ s.icon }}</div>
        <div><div class="stat-val">{{ s.value }}</div><div class="stat-lbl">{{ s.label }}</div></div>
      </div>
    </div>

    <div class="content-row">
      <el-card class="section-card task-section">
        <template #header><span style="font-weight:600;">📋 待办任务 ({{ tasks.length }})</span></template>
        <div v-if="tasks.length" class="task-list">
          <div v-for="t in tasks" :key="t.id" class="task-item">
            <span class="task-dot" :class="t.urgency"></span>
            <div class="task-body">
              <span class="task-title">{{ t.title }}</span>
              <span class="task-family">{{ t.familyName }}</span>
            </div>
            <el-tag size="small" :type="t.type==='need'?'danger':t.type==='visit'?'warning':'info'">
              {{ {need:'需求',measure:'措施',visit:'走访',donation:'物资'}[t.type] || t.type }}
            </el-tag>
          </div>
        </div>
        <el-empty v-else description="暂无待办，继续保持！" />
      </el-card>

      <el-card class="section-card">
        <template #header><span style="font-weight:600;">📊 帮扶影响力</span></template>
        <div class="impact-grid">
          <div class="impact-item"><span class="impact-num">{{ impact.pairedFamilies || 0 }}</span><span class="impact-lbl">结对家庭</span></div>
          <div class="impact-item"><span class="impact-num">{{ impact.totalServices || 0 }}</span><span class="impact-lbl">服务次数</span></div>
          <div class="impact-item"><span class="impact-num">{{ impact.totalHours || '0' }}</span><span class="impact-lbl">服务小时</span></div>
          <div class="impact-item"><span class="impact-num">{{ impact.completedNeeds || 0 }}</span><span class="impact-lbl">完成需求</span></div>
          <div class="impact-item"><span class="impact-num">{{ impact.visitCount || 0 }}</span><span class="impact-lbl">走访次数</span></div>
          <div class="impact-item"><span class="impact-num">{{ impact.certCount || 0 }}</span><span class="impact-lbl">获得证书</span></div>
        </div>
      </el-card>
    </div>

    <el-card class="section-card quick-section">
      <template #header><span style="font-weight:600;">⚡ 快捷操作</span></template>
      <div class="quick-actions">
        <el-button @click="router.push('/volunteer/activity')">📋 志愿活动</el-button>
        <el-button @click="router.push('/volunteer/pairing')">🤝 结对家庭</el-button>
        <el-button @click="router.push('/volunteer/donation')">📦 捐赠管理</el-button>
        <el-button @click="router.push('/volunteer/score')">🏆 我的积分</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMyScore, getVolunteerTasks, getVolunteerImpact } from '@/api/system'

const router = useRouter()
const userStore = useUserStore()
const tasks = ref<any[]>([])
const impact = ref<any>({})
const statList = ref([
  { icon:'🤝', label:'结对家庭', value:'-', color:'#3B82F6' },
  { icon:'📋', label:'服务次数', value:'-', color:'#10B981' },
  { icon:'🕐', label:'服务时长', value:'-', color:'#F59E0B' },
  { icon:'📜', label:'证书', value:'-', color:'#8B5CF6' },
])

onMounted(async () => {
  try { const r = await getVolunteerImpact(); impact.value = r.data || {}; statList.value[0].value = r.data?.pairedFamilies || 0; statList.value[1].value = r.data?.totalServices || 0; statList.value[2].value = (r.data?.totalHours || 0) + 'h'; statList.value[3].value = r.data?.certCount || 0 } catch {}
  try { const r = await getVolunteerTasks(); tasks.value = r.data || [] } catch {}
})
</script>

<style scoped>
.vol-dashboard { padding: 24px; max-width: 1100px; margin: 0 auto; }
.hero { background: linear-gradient(135deg,#0A2647,#2D7A9B); border-radius: 16px; padding: 24px 28px; color:#fff; margin-bottom:24px; display:flex; justify-content:space-between; align-items:center; }
.hero h1 { margin:0 0 4px; font-size:20px; }
.hero p { margin:0; font-size:13px; opacity:.85; }
.hero-score { text-align:center; }
.score-num { font-size:30px; font-weight:800; display:block; line-height:1; }
.score-lbl { font-size:11px; opacity:.7; }
.stats-row { display:grid; grid-template-columns:repeat(4,1fr); gap:14px; margin-bottom:24px; }
.stat-card { background:#fff; border-radius:12px; padding:16px 18px; box-shadow:0 2px 8px rgba(0,0,0,.04); display:flex; align-items:center; gap:12px; }
.stat-icon { width:38px; height:38px; border-radius:10px; display:flex; align-items:center; justify-content:center; font-size:18px; flex-shrink:0; }
.stat-val { font-size:20px; font-weight:800; color:#1E293B; line-height:1.2; }
.stat-lbl { font-size:12px; color:#64748B; }
.content-row { display:grid; grid-template-columns:1.5fr 1fr; gap:20px; margin-bottom:20px; }
.section-card { border-radius:12px; }
.task-section { max-height:420px; overflow-y:auto; }
.task-list { display:flex; flex-direction:column; gap:4px; }
.task-item { display:flex; align-items:center; gap:8px; padding:8px 10px; border-radius:8px; background:#F8FAFC; }
.task-dot { width:8px; height:8px; border-radius:50%; flex-shrink:0; }
.task-dot.high { background:#EF4444; } .task-dot.medium { background:#F59E0B; } .task-dot.low { background:#10B981; }
.task-body { flex:1; min-width:0; }
.task-title { display:block; font-size:12px; font-weight:500; color:#1E293B; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.task-family { font-size:11px; color:#94A3B8; }
.impact-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:10px; }
.impact-item { text-align:center; padding:12px; background:#F8FAFC; border-radius:10px; }
.impact-num { display:block; font-size:22px; font-weight:800; color:#0A2647; }
.impact-lbl { font-size:11px; color:#94A3B8; }
.quick-actions { display:flex; gap:8px; flex-wrap:wrap; }
.quick-actions .el-button { border-radius:8px; }
@media(max-width:768px){ .stats-row { grid-template-columns:repeat(2,1fr); } .content-row { grid-template-columns:1fr; } }
</style>
