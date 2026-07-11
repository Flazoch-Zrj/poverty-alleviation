<template>
  <div class="score-center">
    <div class="score-banner">
      <div class="score-card">
        <div class="score-icon">🏆</div>
        <div class="score-info">
          <span class="score-label">我的总积分</span>
          <span class="score-value">{{ scoreData.totalScore ?? '-' }}</span>
        </div>
      </div>
      <div class="level-card">
        <div class="score-icon">⭐</div>
        <div class="score-info">
          <span class="score-label">当前等级</span>
          <span class="level-badge">{{ scoreData.levelName || '-' }}</span>
        </div>
      </div>
    </div>

    <el-card class="score-table-card">
      <template #header>
        <span style="font-weight:600;">积分明细</span>
      </template>
      <el-table :data="scoreList" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="createTime" label="时间" width="170">
          <template #default="{ row }">{{ row.createTime ? $dayjs(row.createTime).format('YYYY-MM-DD HH:mm') : '-' }}</template>
        </el-table-column>
        <el-table-column prop="scoreType" label="类型" width="140">
          <template #default="{ row }">
            <el-tag :type="scoreTypeTag(row.scoreType)" size="small">{{ scoreTypeLabel(row.scoreType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="积分" width="100" align="center">
          <template #default="{ row }"><span style="color:#10B981;font-weight:700;">+{{ row.score }}</span></template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" />
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyScore, getScorePage } from '@/api/system'

const scoreData = ref<any>({ totalScore: 0, levelName: '' })
const scoreList = ref<any[]>([])
const loading = ref(false)
const pn = ref(1)
const ps = ref(10)
const total = ref(0)

const scoreTypeTag = (t: string) => ({ service_hour: 'success', difficulty_bonus: 'warning', training: 'primary', referral: 'info', rating_bonus: 'danger' }[t] || '')
const scoreTypeLabel = (t: string) => ({ service_hour: '服务时长', difficulty_bonus: '难度奖励', training: '培训完成', referral: '推荐奖励', rating_bonus: '评价奖励' }[t] || t)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getScorePage({ page: pn.value, size: ps.value })
    scoreList.value = res.data?.records ?? res.data?.list ?? []
    total.value = res.data?.total ?? 0
  } catch {} finally { loading.value = false }
}

const fetchScore = async () => {
  try {
    const res = await getMyScore()
    scoreData.value = res.data ?? scoreData.value
  } catch {}
}

onMounted(() => { fetchScore(); fetchData() })
</script>

<style scoped>
.score-center { padding: 24px; max-width: 960px; margin: 0 auto; }
.score-banner { display: flex; gap: 20px; margin-bottom: 24px; }
.score-card, .level-card {
  flex: 1; background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,.06);
  display: flex; align-items: center; gap: 16px;
}
.score-icon { font-size: 36px; }
.score-info { display: flex; flex-direction: column; }
.score-label { font-size: 13px; color: #909399; }
.score-value { font-size: 32px; font-weight: 800; color: #303133; }
.level-badge {
  display: inline-block; padding: 4px 14px; border-radius: 20px;
  background: linear-gradient(135deg, #F59E0B, #F97316); color: #fff;
  font-size: 14px; font-weight: 700; margin-top: 4px;
}
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
