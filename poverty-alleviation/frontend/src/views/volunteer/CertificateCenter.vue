<template>
  <div class="cert-center">
    <el-card>
      <template #header>
        <span style="font-weight:600;">📜 我的证书</span>
      </template>
      <div class="cert-grid" v-loading="loading">
        <div class="cert-card" v-for="cert in certList" :key="cert.certId">
          <div class="cert-icon">🎖️</div>
          <div class="cert-info">
            <span class="cert-name">{{ cert.certName || cert.certType }}</span>
            <span class="cert-number">编号: {{ cert.certNumber }}</span>
            <span class="cert-date">{{ cert.issueDate ? $dayjs(cert.issueDate).format('YYYY年MM月DD日') : '' }}</span>
          </div>
          <div class="cert-badge">
            <el-tag :type="certTypeTag(cert.certType)" size="small">{{ certTypeLabel(cert.certType) }}</el-tag>
          </div>
        </div>
        <el-empty v-if="!loading && certList.length === 0" description="暂无证书" />
      </div>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="total" layout="prev,pager,next" @size-change="fetchData" @current-change="fetchData" small />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCertificatePage } from '@/api/system'

const certList = ref<any[]>([])
const loading = ref(false)
const pn = ref(1)
const ps = ref(10)
const total = ref(0)

const certTypeTag = (t: string) => ({ service_hours: 'success', training: 'primary', activity: 'warning' }[t] || '')
const certTypeLabel = (t: string) => ({ service_hours: '服务时长', training: '技能培训', activity: '活动参与' }[t] || t)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCertificatePage({ page: pn.value, size: ps.value })
    certList.value = res.data?.records ?? res.data?.list ?? []
    total.value = res.data?.total ?? 0
  } catch {} finally { loading.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.cert-center { padding: 24px; max-width: 800px; margin: 0 auto; }
.cert-grid { display: flex; flex-direction: column; gap: 12px; min-height: 100px; }
.cert-card {
  display: flex; align-items: center; gap: 16px; padding: 18px 20px;
  border: 1px solid #e4e7ed; border-radius: 10px; background: #fafafa;
  transition: all .2s;
}
.cert-card:hover { border-color: #2D7A9B; background: #F0F7FF; }
.cert-icon { font-size: 28px; }
.cert-info { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.cert-name { font-weight: 600; font-size: 15px; color: #303133; }
.cert-number { font-size: 12px; color: #909399; }
.cert-date { font-size: 12px; color: #909399; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
