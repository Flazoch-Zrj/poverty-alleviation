<template>
  <div class="risk-dashboard">
    <div class="hero grad-primary">
      <h1>🛡️ 防返贫预警中心</h1>
      <p>数据驱动 · 智能预警 · 主动干预</p>
    </div>

    <div class="stats-row">
      <div class="stat-card danger"><div class="stat-num">{{ stats.high || 0 }}</div><div class="stat-lbl">🔴 高风险</div></div>
      <div class="stat-card warning"><div class="stat-num">{{ stats.medium || 0 }}</div><div class="stat-lbl">🟡 中风险</div></div>
      <div class="stat-card"><div class="stat-num">{{ stats.total || 0 }}</div><div class="stat-lbl">📋 待处理</div></div>
      <div class="stat-card success"><div class="stat-num">{{ riskCount || 0 }}</div><div class="stat-lbl">📊 已评估</div></div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="预警列表" name="alert">
        <el-table :data="alertList" v-loading="alertLoading" border stripe style="width:100%">
          <el-table-column prop="familyId" label="家庭ID" width="80" align="center" />
          <el-table-column prop="riskLevel" label="等级" width="90" align="center">
            <template #default="{row}">
              <el-tag :type="row.riskLevel==='高风险'?'danger':row.riskLevel==='中风险'?'warning':'success'" size="small">{{ row.riskLevel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="预警内容" min-width="300" />
          <el-table-column prop="createTime" label="预警时间" width="160" />
          <el-table-column label="操作" width="100" align="center">
            <template #default="{row}">
              <el-button v-if="!row.isHandled" size="small" type="success" @click="handleAlert(row)">已处理</el-button>
              <span v-else style="color:#909399;">已处理</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="pt" layout="total,prev,pager,next" @size-change="fetchAlerts" @current-change="fetchAlerts" /></div>
      </el-tab-pane>

      <el-tab-pane label="风险评估" name="assess">
        <div class="toolbar">
          <el-button @click="batchAssess">批量评估所有家庭</el-button>
        </div>
        <el-table :data="assessList" v-loading="assessLoading" border stripe style="width:100%">
          <el-table-column prop="familyId" label="家庭ID" width="80" align="center" />
          <el-table-column prop="riskScore" label="风险评分" width="100" align="center">
            <template #default="{row}">
              <span :style="{color: row.riskScore>=80?'#EF4444':row.riskScore>=50?'#F59E0B':'#10B981',fontWeight:700}">{{ row.riskScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="riskLevel" label="等级" width="90" align="center">
            <template #default="{row}"><el-tag :type="row.riskLevel==='高风险'?'danger':row.riskLevel==='中风险'?'warning':'success'" size="small">{{ row.riskLevel }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="incomeStability" label="收入" width="70" align="center"><template #default="{row}"><el-progress :percentage="row.incomeStability" :stroke-width="16" :show-text="false" :color="row.incomeStability>=60?'#10B981':'#EF4444'" /></template></el-table-column>
          <el-table-column prop="healthStatus" label="健康" width="70" align="center"><template #default="{row}"><el-progress :percentage="row.healthStatus" :stroke-width="16" :show-text="false" :color="row.healthStatus>=60?'#10B981':'#EF4444'" /></template></el-table-column>
          <el-table-column prop="employmentStatus" label="就业" width="70" align="center"><template #default="{row}"><el-progress :percentage="row.employmentStatus" :stroke-width="16" :show-text="false" :color="row.employmentStatus>=60?'#10B981':'#EF4444'" /></template></el-table-column>
          <el-table-column prop="assessDate" label="评估日期" width="120" />
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="apn" v-model:page-size="aps" :total="apt" layout="total,prev,pager,next" @size-change="fetchAssess" @current-change="fetchAssess" /></div>
      </el-tab-pane>

      <el-tab-pane label="智能匹配" name="match">
        <div class="toolbar">
          <el-select v-model="matchFamilyId" placeholder="选择家庭" filterable clearable style="width:240px" @change="fetchMatches">
            <el-option v-for="f in familyOptions" :key="f.familyId" :label="f.householderName" :value="f.familyId" />
          </el-select>
          <el-button @click="generateMatch" :disabled="!matchFamilyId">生成匹配建议</el-button>
        </div>
        <el-table :data="matchList" v-loading="matchLoading" border stripe style="width:100%">
          <el-table-column prop="matchType" label="匹配类型" width="100" align="center">
            <template #default="{row}"><el-tag :type="row.matchType==='training'?'primary':row.matchType==='job'?'success':row.matchType==='project'?'warning':'info'" size="small">{{ {training:'培训',job:'就业',project:'项目',donation:'捐赠'}[row.matchType]||row.matchType }}</el-tag></template>
          </el-table-column>
          <el-table-column label="匹配分数" width="100" align="center">
            <template #default="{row}"><span :style="{color:row.matchScore>=80?'#10B981':'#F59E0B',fontWeight:700}">{{ row.matchScore }}</span></template>
          </el-table-column>
          <el-table-column prop="matchReason" label="匹配理由" min-width="300" />
          <el-table-column prop="createTime" label="生成时间" width="150" />
          <el-table-column label="状态" width="80" align="center">
            <template #default="{row}"><el-tag :type="row.isAccepted?'success':'warning'" size="small">{{ row.isAccepted?'已采纳':'待处理' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{row}"><el-button v-if="!row.isAccepted" size="small" type="primary" @click="handleAccept(row)">采纳</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRiskAlertPage, getRiskAssessPage, getRiskStats, handleRiskAlert, getRiskMatches, generateMatches, acceptMatch, getFamilyPage } from '@/api/system'

const activeTab = ref('alert')

// Stats
const stats = ref<any>({})
const riskCount = ref(0)

// Alerts
const alertList = ref<any[]>([]), alertLoading = ref(false)
const pn = ref(1), ps = ref(10), pt = ref(0)
const fetchAlerts = async () => {
  alertLoading.value = true
  try { const r = await getRiskAlertPage({ page: pn.value, size: ps.value }); alertList.value = r.data?.records ?? []; pt.value = r.data?.total ?? 0 } catch {} finally { alertLoading.value = false }
}

// Assessments
const assessList = ref<any[]>([]), assessLoading = ref(false)
const apn = ref(1), aps = ref(10), apt = ref(0)
const fetchAssess = async () => {
  assessLoading.value = true
  try { const r = await getRiskAssessPage({ page: apn.value, size: aps.value }); assessList.value = r.data?.records ?? []; apt.value = r.data?.total ?? 0; riskCount.value = r.data?.total ?? 0 } catch {} finally { assessLoading.value = false }
}

// Actions
const handleAlert = async (row: any) => {
  try { await handleRiskAlert(row.alertId); ElMessage.success('已处理'); fetchAlerts(); fetchStats() } catch {}
}
const batchAssess = async () => {
  ElMessage.info('批量评估功能开发中，当前可在家庭档案中逐户评估')
}

// Match
const matchFamilyId = ref<number>()
const matchList = ref<any[]>([])
const matchLoading = ref(false)
const familyOptions = ref<any[]>([])
const fetchFamilies = async () => {
  try { const r = await getFamilyPage({ page: 1, size: 200 }); familyOptions.value = r.data?.records ?? [] } catch {}
}
const fetchMatches = async () => {
  if (!matchFamilyId.value) { matchList.value = []; return }
  matchLoading.value = true
  try { const r = await getRiskMatches(matchFamilyId.value); matchList.value = r.data ?? [] } catch {} finally { matchLoading.value = false }
}
const generateMatch = async () => {
  if (!matchFamilyId.value) return
  try { await generateMatches(matchFamilyId.value); ElMessage.success('匹配建议已生成'); fetchMatches() } catch {}
}
const handleAccept = async (row: any) => {
  try { await acceptMatch(row.matchId); ElMessage.success('已采纳'); fetchMatches() } catch {}
}

const fetchStats = async () => {
  try { const r = await getRiskStats(); stats.value = r.data || {} } catch {}
}

onMounted(() => { fetchAlerts(); fetchAssess(); fetchStats(); fetchFamilies() })
</script>

<style scoped>
.risk-dashboard { padding: 24px; max-width: 1200px; margin: 0 auto; }
.hero { border-radius: 16px; padding: 28px 32px; color: #fff; margin-bottom: 24px; }
.hero h1 { margin: 0; font-size: 22px; }
.hero p { margin: 6px 0 0; font-size: 13px; opacity: .8; }
.stats-row { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,.04); }
.stat-card.danger { border-left: 4px solid #EF4444; }
.stat-card.warning { border-left: 4px solid #F59E0B; }
.stat-card.success { border-left: 4px solid #10B981; }
.stat-num { font-size: 28px; font-weight: 800; }
.stat-lbl { font-size: 12px; color: #64748B; margin-top: 4px; }
.toolbar { margin-bottom: 16px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
