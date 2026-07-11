<template>
  <div class="pairing-page">
    <div class="hero">
      <h1>🤝 我的结对家庭</h1>
      <p>与 {{ pairings.length }} 户家庭结对帮扶 · 查看家庭需求 · 跟踪帮扶进度</p>
    </div>

    <div v-if="loading" v-loading="loading" style="height:200px"></div>

    <div v-else-if="pairings.length === 0" class="empty-state">
      <el-empty description="暂无结对家庭，请等待干部分配">
        <template #extra><el-button type="primary" disabled>申请结对（开发中）</el-button></template>
      </el-empty>
    </div>

    <div v-else class="pairing-grid">
      <div v-for="item in pairings" :key="item.pairing?.pairingId" class="pairing-card" @click="showDetail(item)">
        <div class="card-top">
          <span class="family-avatar">{{ (item.familyName || '?').charAt(0) }}</span>
          <div class="family-info">
            <h3>{{ item.familyName || '家庭 #' + item.pairing?.familyId }}</h3>
            <span class="family-code">{{ item.family?.familyCode }}</span>
          </div>
          <el-tag size="small" type="success">帮扶中</el-tag>
        </div>
        <div class="card-stats">
          <div class="stat"><span class="num">{{ item.serviceCount || 0 }}</span><span class="lbl">服务次数</span></div>
          <div class="stat"><span class="num">{{ item.family?.familySize || '-' }}</span><span class="lbl">家庭人口</span></div>
          <div class="stat"><span class="num">{{ item.family?.annualIncome?.toLocaleString() || '-' }}</span><span class="lbl">年收入</span></div>
        </div>
        <div class="card-addr">{{ item.family?.address || '' }}</div>
      </div>
    </div>

    <!-- 家庭详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="detailData?.family?.householderName + ' 家庭'" width="600px">
      <el-descriptions :column="2" border size="small" v-if="detailData?.family">
        <el-descriptions-item label="档案编号">{{ detailData.family.familyCode }}</el-descriptions-item>
        <el-descriptions-item label="户主">{{ detailData.family.householderName }}</el-descriptions-item>
        <el-descriptions-item label="人口">{{ detailData.family.familySize }} 人</el-descriptions-item>
        <el-descriptions-item label="年收入">{{ detailData.family.annualIncome?.toLocaleString() }} 元</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detailData.family.address }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin:16px 0 8px;">📌 帮扶措施进度</h4>
      <div v-if="detailData?.measures?.length" class="measure-list">
        <div v-for="m in detailData.measures" :key="m.measureId" class="measure-row">
          <el-tag size="small">{{ m.measureType }}</el-tag>
          <div class="m-bar"><div class="m-fill" :style="{width: (m.progress||0)+'%'}"></div></div>
          <span class="m-pct">{{ m.progress || 0 }}%</span>
        </div>
      </div>
      <el-empty v-else description="暂无措施" :image-size="60" />

      <h4 style="margin:16px 0 8px;">📝 我的服务记录</h4>
      <div v-if="detailData?.myRecords?.length" class="record-list">
        <div v-for="r in detailData.myRecords" :key="r.recordId" class="record-row">
          <span class="r-date">{{ r.signInTime ? $dayjs(r.signInTime).format('MM/DD') : '-' }}</span>
          <span class="r-hours">{{ r.serviceHours || 0 }}h</span>
          <el-tag :type="r.status===4?'success':'primary'" size="small">{{ ['','已报名','已签到','已完成','已评价'][r.status] }}</el-tag>
        </div>
      </div>
      <el-empty v-else description="暂无服务记录" :image-size="60" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyPairings, getPairingFamilyDashboard } from '@/api/system'

const pairings = ref<any[]>([])
const loading = ref(true)
const detailVisible = ref(false)
const detailData = ref<any>({})

const fetchData = async () => {
  try {
    const r = await getMyPairings()
    pairings.value = r.data || []
  } catch {} finally { loading.value = false }
}

const showDetail = async (item: any) => {
  const familyId = item.pairing?.familyId
  if (!familyId) return
  try {
    const r = await getPairingFamilyDashboard(familyId)
    detailData.value = r.data || {}
    detailVisible.value = true
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.pairing-page { padding: 24px; max-width: 1100px; margin: 0 auto; }
.hero { background: linear-gradient(135deg,#0A2647,#2D7A9B); border-radius: 16px; padding: 24px 28px; color:#fff; margin-bottom:24px; }
.hero h1 { margin:0; font-size:20px; }
.hero p { margin:4px 0 0; font-size:13px; opacity:.8; }
.pairing-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(300px,1fr)); gap:16px; }
.pairing-card { background:#fff; border-radius:14px; padding:20px; box-shadow:0 2px 8px rgba(0,0,0,.04); cursor:pointer; transition:.25s; border:1px solid #F1F5F9; }
.pairing-card:hover { transform:translateY(-3px); box-shadow:0 8px 24px rgba(0,0,0,.1); border-color:#2D7A9B; }
.card-top { display:flex; align-items:center; gap:12px; margin-bottom:14px; }
.family-avatar { width:44px; height:44px; border-radius:12px; background:linear-gradient(135deg,#2D7A9B,#60A5FA); display:flex; align-items:center; justify-content:center; color:#fff; font-size:18px; font-weight:700; flex-shrink:0; }
.family-info { flex:1; }
.family-info h3 { margin:0; font-size:15px; font-weight:700; color:#1E293B; }
.family-code { font-size:11px; color:#94A3B8; }
.card-stats { display:grid; grid-template-columns:repeat(3,1fr); gap:8px; margin-bottom:10px; }
.stat { text-align:center; background:#F8FAFC; border-radius:8px; padding:8px; }
.stat .num { display:block; font-size:16px; font-weight:700; color:#0A2647; }
.stat .lbl { font-size:10px; color:#94A3B8; }
.card-addr { font-size:11px; color:#94A3B8; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.measure-list { display:flex; flex-direction:column; gap:8px; }
.measure-row { display:flex; align-items:center; gap:8px; }
.m-bar { flex:1; height:6px; background:#F1F5F9; border-radius:3px; overflow:hidden; }
.m-fill { height:100%; background:linear-gradient(90deg,#2D7A9B,#60A5FA); border-radius:3px; }
.m-pct { font-size:11px; font-weight:600; color:#64748B; width:32px; text-align:right; }
.record-list { display:flex; flex-direction:column; gap:6px; }
.record-row { display:flex; align-items:center; gap:10px; padding:6px 10px; background:#F8FAFC; border-radius:6px; font-size:12px; }
.r-date { color:#64748B; width:60px; }
.r-hours { font-weight:600; color:#0A2647; width:40px; }
</style>
