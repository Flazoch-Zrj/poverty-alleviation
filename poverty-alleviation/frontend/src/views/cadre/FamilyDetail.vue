<template>
  <div class="family-detail">
    <div class="back-bar">
      <el-button text @click="router.push('/cadre/dashboard')"><el-icon><ArrowLeft /></el-icon> 返回工作台</el-button>
    </div>

    <div v-if="loading" v-loading="loading" style="height:300px"></div>

    <template v-if="!loading && data.family">
      <!-- 家庭信息头 -->
      <div class="family-header">
        <div>
          <h1>{{ data.family.householderName }} 家庭</h1>
          <p class="family-meta">{{ data.family.familyCode }} · {{ data.family.address }} · {{ data.family.familySize }} 人</p>
        </div>
        <el-tag :type="data.family.status==='建档'?'warning':'success'" size="large">{{ data.family.status }}</el-tag>
      </div>

      <div class="content-grid">
        <!-- 基本信息 -->
        <el-card class="info-card">
          <template #header><span style="font-weight:600;">📋 基本信息</span></template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="户主">{{ data.family.householderName }}</el-descriptions-item>
            <el-descriptions-item label="建档日期">{{ data.family.filingDate }}</el-descriptions-item>
            <el-descriptions-item label="年收入">{{ data.family.annualIncome?.toLocaleString() }} 元</el-descriptions-item>
            <el-descriptions-item label="贫困等级">{{ data.family.povertyLevel }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 家庭成员 -->
        <el-card class="info-card">
          <template #header><span style="font-weight:600;">👨‍👩‍👧‍👦 家庭成员 ({{ data.members?.length || 0 }})</span></template>
          <el-table :data="data.members || []" size="small" border stripe>
            <el-table-column prop="name" label="姓名" width="80" />
            <el-table-column prop="relationship" label="关系" width="80" />
            <el-table-column prop="age" label="年龄" width="50" />
            <el-table-column prop="healthStatus" label="健康状况" width="100" />
            <el-table-column prop="hasJob" label="有工作" width="60"><template #default="{row}">{{ row.hasJob==='1'?'是':'否' }}</template></el-table-column>
          </el-table>
        </el-card>

        <!-- 帮扶措施 -->
        <el-card class="info-card full-width">
          <template #header><span style="font-weight:600;">📌 帮扶措施进度</span></template>
          <div v-if="data.measures?.length" class="measure-list">
            <div v-for="m in data.measures" :key="m.measureId" class="measure-item">
              <div class="measure-header">
                <el-tag size="small">{{ m.measureType }}</el-tag>
                <span class="measure-status" :class="m.progress>=100?'done':''">{{ m.progress || 0 }}%</span>
              </div>
              <div class="measure-bar"><div class="measure-fill" :style="{width:(m.progress||0)+'%'}"></div></div>
              <div class="measure-amount" v-if="m.targetAmount">目标: {{ m.targetAmount.toLocaleString() }} 元</div>
            </div>
          </div>
          <el-empty v-else description="暂无措施" />
        </el-card>

        <!-- 走访记录 -->
        <el-card class="info-card full-width">
          <template #header><span style="font-weight:600;">📝 走访记录</span></template>
          <div v-if="data.visits?.length" class="visit-timeline">
            <div v-for="v in data.visits" :key="v.recordId" class="visit-item">
              <div class="visit-dot"></div>
              <div class="visit-body">
                <span class="visit-date">{{ v.visitDate }}</span>
                <p class="visit-content">{{ v.content }}</p>
                <span class="visit-plan" v-if="v.nextPlan">下一步: {{ v.nextPlan }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无走访记录" />
        </el-card>

        <!-- 时间线 -->
        <el-card class="info-card full-width">
          <template #header><span style="font-weight:600;">📅 帮扶时间线</span></template>
          <div v-if="timeline?.length" class="timeline">
            <div v-for="(e, i) in timeline" :key="i" class="timeline-item">
              <div class="tl-dot" :style="{background: e.color==='primary'?'#2563EB':e.color==='success'?'#10B981':'#F59E0B'}"></div>
              <div class="tl-body">
                <span class="tl-date">{{ e.date }}</span>
                <span class="tl-title">{{ e.title }}</span>
                <span class="tl-desc" v-if="e.description">{{ e.description }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无时间线数据" />
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCadreFamilyOverview, getCadreFamilyTimeline } from '@/api/system'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const data = ref<any>({})
const timeline = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  const id = route.params.familyId as string
  try {
    const [overviewR, timelineR] = await Promise.all([
      getCadreFamilyOverview(Number(id)),
      getCadreFamilyTimeline(Number(id))
    ])
    data.value = overviewR.data || {}
    timeline.value = timelineR.data || []
  } catch {} finally { loading.value = false }
})
</script>

<style scoped>
.family-detail { padding: 24px; max-width: 1200px; margin: 0 auto; }
.back-bar { margin-bottom: 16px; }
.family-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.family-header h1 { font-size: 22px; font-weight: 700; margin: 0; color: #1E293B; }
.family-meta { color: #64748B; font-size: 13px; margin: 4px 0 0; }
.content-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.info-card { border-radius: 12px; }
.info-card.full-width { grid-column: 1 / -1; }
.measure-list { display: flex; flex-direction: column; gap: 16px; }
.measure-item { }
.measure-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.measure-status { font-weight: 700; color: #F59E0B; }
.measure-status.done { color: #10B981; }
.measure-bar { height: 8px; background: #F1F5F9; border-radius: 4px; overflow: hidden; }
.measure-fill { height: 100%; background: linear-gradient(90deg,#2D7A9B,#60A5FA); border-radius: 4px; transition: width .5s; }
.measure-amount { font-size: 11px; color: #94A3B8; margin-top: 4px; }
.visit-timeline { position: relative; padding-left: 20px; }
.visit-timeline::before { content:''; position:absolute; left:6px; top:0; bottom:0; width:2px; background:#E2E8F0; }
.visit-item { position: relative; padding: 0 0 20px 20px; }
.visit-dot { position:absolute; left:-14px; top:4px; width:12px; height:12px; border-radius:50%; background:#2D7A9B; border:2px solid #fff; box-shadow:0 0 0 2px #2D7A9B; }
.visit-date { font-size: 12px; font-weight: 600; color: #64748B; }
.visit-content { font-size: 13px; color: #1E293B; margin: 4px 0; }
.visit-plan { font-size: 11px; color: #94A3B8; }
.timeline { position: relative; padding-left: 24px; }
.timeline::before { content:''; position:absolute; left:9px; top:0; bottom:0; width:2px; background:#E2E8F0; }
.timeline-item { position: relative; padding: 0 0 20px 20px; display: flex; flex-direction: column; }
.tl-dot { position:absolute; left:-15px; top:4px; width:14px; height:14px; border-radius:50%; border:2px solid #fff; box-shadow:0 0 0 2px currentColor; }
.tl-date { font-size: 11px; color: #94A3B8; }
.tl-title { font-size: 14px; font-weight: 600; color: #1E293B; margin: 2px 0; }
.tl-desc { font-size: 12px; color: #64748B; }
@media(max-width:768px){ .content-grid { grid-template-columns: 1fr; } }
</style>
