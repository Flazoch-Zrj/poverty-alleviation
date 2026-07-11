<template>
  <div class="big-screen">
    <!-- 顶部标题 -->
    <div class="header">
      <h1>脱贫攻坚数据驾驶舱</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <!-- 核心指标 -->
    <div class="stats-row">
      <div class="stat-item"><div class="stat-icon">🏠</div><div class="stat-body"><span class="stat-num">{{ data.core?.totalFamilies || 0 }}</span><span class="stat-lbl">总帮扶户数</span></div></div>
      <div class="stat-item"><div class="stat-icon">🔗</div><div class="stat-body"><span class="stat-num">{{ data.core?.pairRate || 0 }}%</span><span class="stat-lbl">结对率</span></div></div>
      <div class="stat-item"><div class="stat-icon">📋</div><div class="stat-body"><span class="stat-num">{{ data.core?.visitThisMonth || 0 }}</span><span class="stat-lbl">本月走访</span></div></div>
      <div class="stat-item"><div class="stat-icon">💰</div><div class="stat-body"><span class="stat-num">{{ fmtMoney(data.core?.totalDonation) }}</span><span class="stat-lbl">捐赠总额(元)</span></div></div>
      <div class="stat-item"><div class="stat-icon">✅</div><div class="stat-body"><span class="stat-num">{{ data.needRate || 0 }}%</span><span class="stat-lbl">需求解决率</span></div></div>
      <div class="stat-item"><div class="stat-icon">💼</div><div class="stat-body"><span class="stat-num">{{ data.employmentRate || 0 }}%</span><span class="stat-lbl">就业率</span></div></div>
    </div>

    <div class="charts-row">
      <!-- 脱贫状态分布 - 饼图 -->
      <div class="chart-card">
        <div class="chart-title">脱贫状态分布</div>
        <v-chart :option="pieOption" style="height:260px;" autoresize />
      </div>

      <!-- 月度走访趋势 - 折线图 -->
      <div class="chart-card">
        <div class="chart-title">走访趋势（近6月）</div>
        <v-chart :option="lineOption" style="height:260px;" autoresize />
      </div>

      <!-- 措施类型分布 - 饼图 -->
      <div class="chart-card">
        <div class="chart-title">帮扶措施分布</div>
        <v-chart :option="measureOption" style="height:260px;" autoresize />
      </div>
    </div>

    <div class="charts-row">
      <!-- 实时预警 -->
      <div class="chart-card wide">
        <div class="chart-title">⚠️ 实时预警</div>
        <div class="alert-scroll" v-if="alerts.length">
          <div v-for="a in alerts" :key="a.alertId" class="alert-item">
            <span class="alert-dot" :class="a.riskLevel==='高风险'?'danger':'warning'"></span>
            <span class="alert-text">{{ a.content }}</span>
            <span class="alert-time">{{ a.createTime?.substring(11,16) || '' }}</span>
          </div>
        </div>
        <div v-else class="alert-empty">✅ 暂无预警</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getBigScreenOverview } from '@/api/system'
import { getRiskAlerts } from '@/api/system'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, LineChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, LineChart, TooltipComponent, LegendComponent, GridComponent])

const data = ref<any>({})
const alerts = ref<any[]>([])
const currentTime = ref('')

let timer: ReturnType<typeof setInterval> | null = null

const fmtMoney = (v: number) => {
  if (!v) return '0'
  if (v > 10000) return (v / 10000).toFixed(1) + '万'
  return v.toLocaleString()
}

const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0, textStyle: { color: '#94A3B8', fontSize: 11 } },
  series: [{
    type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
    data: data.value.statusDist || [],
    label: { show: false },
    itemStyle: { borderRadius: 4, borderColor: '#0F172A', borderWidth: 2 },
    color: ['#F59E0B', '#10B981', '#EF4444']
  }]
}))

const lineOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '12%', top: '8%', containLabel: true },
  xAxis: { type: 'category', data: (data.value.visitTrend || []).map((i: any) => i.month), axisLabel: { color: '#94A3B8' }, axisLine: { lineStyle: { color: '#334155' } } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#1E293B' } }, axisLabel: { color: '#94A3B8' } },
  series: [{
    type: 'line', smooth: true, data: (data.value.visitTrend || []).map((i: any) => i.count),
    lineStyle: { color: '#2D7A9B', width: 3 }, areaStyle: { color: 'rgba(45,122,155,0.15)' },
    symbol: 'circle', symbolSize: 6, itemStyle: { color: '#60A5FA' }
  }]
}))

const measureOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c}' },
  legend: { bottom: 0, textStyle: { color: '#94A3B8', fontSize: 11 } },
  series: [{
    type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
    data: data.value.measureDist || [],
    label: { show: false },
    itemStyle: { borderRadius: 4, borderColor: '#0F172A', borderWidth: 2 },
    color: ['#2D7A9B', '#10B981', '#F59E0B', '#EF4444', '#6366F1', '#EC4899']
  }]
}))

const fetchData = async () => {
  try { const r = await getBigScreenOverview(); data.value = r.data || {} } catch (e) { console.error('大屏数据加载失败', e) }
  try { const r = await getRiskAlerts(); alerts.value = (r.data || []).slice(0, 5) } catch (e) { console.error('预警加载失败', e) }
}

const updateTime = () => {
  const n = new Date()
  currentTime.value = `${n.getFullYear()}-${String(n.getMonth()+1).padStart(2,'0')}-${String(n.getDate()).padStart(2,'0')} ${String(n.getHours()).padStart(2,'0')}:${String(n.getMinutes()).padStart(2,'0')}:${String(n.getSeconds()).padStart(2,'0')}`
}

onMounted(() => { fetchData(); updateTime(); timer = setInterval(() => { fetchData(); updateTime() }, 10000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.big-screen {
  background: #0F172A; min-height: 100vh; padding: 24px 32px; color: #fff;
  font-family: 'PingFang SC','Microsoft YaHei',sans-serif;
  position: relative; overflow-x: hidden;
}
.big-screen::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px;
  background: linear-gradient(90deg, #2D7A9B, #60A5FA, #818CF8, #2D7A9B);
  background-size: 200% 100%; animation: gradientMove 3s linear infinite;
}
@keyframes gradientMove { 0%{background-position:0 0} 100%{background-position:200% 0} }

.header { text-align: center; padding: 20px 0 24px; }
.header h1 { margin: 0; font-size: 28px; font-weight: 800; background: linear-gradient(90deg,#2D7A9B,#60A5FA,#818CF8); -webkit-background-clip: text; -webkit-text-fill-color: transparent; letter-spacing: 6px; }
.header-time { font-size: 13px; color: #64748B; margin-top: 8px; font-variant-numeric: tabular-nums; }

.stats-row { display: grid; grid-template-columns: repeat(6,1fr); gap: 14px; margin-bottom: 20px; }
.stat-item { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; padding: 16px; display: flex; align-items: center; gap: 12px; }
.stat-icon { font-size: 28px; }
.stat-body { display: flex; flex-direction: column; }
.stat-num { font-size: 24px; font-weight: 800; color: #fff; line-height: 1.2; }
.stat-lbl { font-size: 11px; color: #64748B; margin-top: 2px; }

.charts-row { display: grid; grid-template-columns: repeat(3,1fr); gap: 14px; margin-bottom: 14px; }
.chart-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; padding: 16px; }
.chart-card.wide { grid-column: span 3; }
.chart-title { font-size: 14px; font-weight: 600; color: #94A3B8; margin-bottom: 12px; }

.alert-scroll { max-height: 200px; overflow-y: auto; }
.alert-item { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid rgba(255,255,255,0.04); font-size: 13px; }
.alert-item:last-child { border-bottom: none; }
.alert-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.alert-dot.danger { background: #EF4444; box-shadow: 0 0 8px rgba(239,68,68,0.5); }
.alert-dot.warning { background: #F59E0B; }
.alert-text { flex: 1; color: #E2E8F0; }
.alert-time { color: #64748B; font-size: 12px; }
.alert-empty { text-align: center; padding: 30px; color: #10B981; font-size: 15px; }

@media(max-width:900px){ .stats-row { grid-template-columns: repeat(3,1fr); } .charts-row { grid-template-columns: 1fr; } .chart-card.wide { grid-column: span 1; } }
</style>
