<template>
  <div class="ranking-board">
    <el-card>
      <template #header>
        <div class="ranking-header">
          <span style="font-weight:600;">🏅 志愿者积分排行榜</span>
          <el-tag type="info" size="small">实时更新</el-tag>
        </div>
      </template>
      <div v-loading="loading">
        <div class="rank-item" v-for="(item, idx) in rankingList" :key="item.user_id || idx">
          <div class="rank-num">
            <span v-if="idx < 3" class="rank-medal">{{ ['🥇','🥈','🥉'][idx] }}</span>
            <span v-else class="rank-plain">{{ idx + 1 }}</span>
          </div>
          <div class="rank-user">
            <el-avatar :size="36" icon="UserFilled" />
            <span class="rank-name">志愿者 #{{ item.user_id }}</span>
          </div>
          <div class="rank-score">
            <span class="rank-score-value">{{ item.total_score }}</span>
            <span class="rank-score-label">积分</span>
          </div>
        </div>
        <el-empty v-if="!loading && rankingList.length === 0" description="暂无排行数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getScoreRanking } from '@/api/system'

const rankingList = ref<any[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getScoreRanking({ limit: 50 })
    rankingList.value = res.data ?? []
  } catch {} finally { loading.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.ranking-board { padding: 24px; max-width: 640px; margin: 0 auto; }
.ranking-header { display: flex; align-items: center; justify-content: space-between; }
.rank-item {
  display: flex; align-items: center; gap: 16px; padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0; transition: background .2s;
}
.rank-item:hover { background: #F9FAFB; }
.rank-num { width: 36px; text-align: center; }
.rank-medal { font-size: 24px; }
.rank-plain { font-size: 16px; font-weight: 700; color: #909399; }
.rank-user { flex: 1; display: flex; align-items: center; gap: 10px; }
.rank-name { font-weight: 500; color: #303133; }
.rank-score { text-align: right; }
.rank-score-value { font-size: 20px; font-weight: 800; color: #F59E0B; }
.rank-score-label { font-size: 12px; color: #909399; margin-left: 4px; }
</style>
