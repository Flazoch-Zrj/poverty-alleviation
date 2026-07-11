<template>
  <div class="volunteer-hub">
    <!-- ====== 顶部横幅 ====== -->
    <div class="hero">
      <div class="hero-info">
        <h1>🤝 志愿服务中心</h1>
        <p>参与扶贫志愿 · 共建美好乡村</p>
      </div>
      <div class="hero-score">
        <div class="score-badge">
          <span class="score-num">{{ myScore }}</span>
          <span class="score-label">积分</span>
        </div>
        <div class="level-badge">{{ levelName }}</div>
      </div>
    </div>

    <!-- ====== 统计指标卡 ====== -->
    <div class="stats-row">
      <div class="stat-card" v-for="s in statItems" :key="s.label">
        <div class="stat-icon" :style="{background:s.color}">{{ s.icon }}</div>
        <div><div class="stat-val">{{ s.value }}</div><div class="stat-lbl">{{ s.label }}</div></div>
      </div>
    </div>

    <!-- ====== 活动类型筛选 + 发起活动 ====== -->
    <div class="action-bar">
      <div class="filter-pills">
        <button v-for="p in typeFilters" :key="p.value" class="pill" :class="{'pill--active': activeType === p.value}" @click="activeType = p.value; fetchActivities()">{{ p.label }}</button>
      </div>
      <button class="btn-add" @click="openCreateDialog"><el-icon><Plus /></el-icon> 发起活动</button>
    </div>

    <!-- ====== 标签页 ====== -->
    <div class="tab-bar">
      <button v-for="t in tabs" :key="t.key" class="tab" :class="{'tab--active': activeTab === t.key}" @click="activeTab = t.key; fetchData()">
        {{ t.label }}<span v-if="t.badge" class="tab-badge">{{ t.badge }}</span>
      </button>
    </div>

    <!-- ====== 活动卡片网格 ====== -->
    <div class="activity-grid" v-loading="loading" v-show="activeTab === 'all' || activeTab === 'my'">
      <div class="activity-card" v-for="act in activityList" :key="act.activityId">
        <div class="card-cover" :style="{ background: coverColor(act.activityType) }">
          <span class="card-type-tag">{{ act.activityType || '综合' }}</span>
          <span class="card-diff">{{ '⭐'.repeat(act.difficulty || 1) }}</span>
          <span class="card-status-tag" :class="'st-' + (act.status || 1)">{{ statusLabel(act.status) }}</span>
        </div>
        <div class="card-body">
          <h3 class="card-title">{{ act.title }}</h3>
          <div class="card-meta-line"><span>📅 {{ act.startTime ? $dayjs(act.startTime).format('MM/DD HH:mm') : '-' }} ~ {{ act.endTime ? $dayjs(act.endTime).format('MM/DD HH:mm') : '-' }}</span></div>
          <div class="card-meta-line"><span>📍 {{ act.location || '待定' }}</span><span v-if="act.familyName" style="margin-left:10px;">🏠 {{ act.familyName }}</span></div>
          <div class="card-progress">
            <div class="progress-bar"><div class="progress-fill" :style="{width: enrollPercent(act)+'%', background: enrollPercent(act)>=100?'#EF4444':'#10B981'}"></div></div>
            <span class="progress-text">{{ act.registeredCount || 0 }}/{{ act.needVolunteers || '∞' }}</span>
          </div>
          <div class="card-actions">
            <template v-if="activeTab === 'my'">
              <button v-if="myStatus[act.activityId] === 1" class="act-btn primary" @click="handleSignIn(act)">✅ 签到</button>
              <button v-if="myStatus[act.activityId] === 2" class="act-btn warning" @click="handleSignOut(act)">🚪 签退</button>
              <button v-if="myStatus[act.activityId] === 3 && !myFeedback[act.activityId]" class="act-btn" @click="openFeedback(act)">📝 评价</button>
              <span v-if="myStatus[act.activityId] === 3 && myFeedback[act.activityId]" class="done-tag">✅ 已完成</span>
            </template>
            <template v-if="activeTab === 'all'">
              <button v-if="act.status === 1 && (act.registeredCount||0) < (act.needVolunteers||999)" class="act-btn primary" @click="handleEnroll(act)">🙋 报名</button>
            </template>
            <button class="act-btn outline" @click="openEditDialog(act)" v-if="isMyActivity(act)">✏️</button>
            <button class="act-btn danger" @click="handleDeleteActivity(act)" v-if="isMyActivity(act)">🗑️</button>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && activityList.length === 0" description="暂无活动" />
    </div>

    <!-- ====== 积分明细（tab） ====== -->
    <div class="score-section" v-show="activeTab === 'score'">
      <el-card>
        <template #header><span style="font-weight:600;">🏆 积分明细</span></template>
        <el-table :data="scoreList" v-loading="scoreLoading" border stripe size="small">
          <el-table-column prop="createTime" label="时间" width="160">
            <template #default="{row}">{{ row.createTime ? $dayjs(row.createTime).format('YYYY-MM-DD HH:mm') : '-' }}</template>
          </el-table-column>
          <el-table-column prop="scoreType" label="类型" width="120">
            <template #default="{row}"><el-tag :type="scoreTypeTag(row.scoreType)" size="small">{{ scoreTypeLabel(row.scoreType) }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="score" label="积分" width="80" align="center">
            <template #default="{row}"><span style="color:#10B981;font-weight:700;">+{{ row.score }}</span></template>
          </el-table-column>
          <el-table-column prop="remark" label="说明" />
        </el-table>
      </el-card>
    </div>

    <!-- ====== 证书（tab） ====== -->
    <div class="cert-section" v-show="activeTab === 'cert'">
      <el-card>
        <template #header><span style="font-weight:600;">📜 我的证书</span></template>
        <div class="cert-grid" v-loading="certLoading">
          <div v-for="c in certList" :key="c.certId" class="cert-card">
            <div class="cert-icon">🎖️</div>
            <div><div class="cert-name">{{ c.certName || c.certType }}</div><div class="cert-num">{{ c.certNumber }}</div></div>
          </div>
          <el-empty v-if="!certLoading && certList.length===0" description="暂无证书" />
        </div>
      </el-card>
    </div>

    <div class="pagination-wrap">
      <el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="total" layout="total,prev,pager,next" @size-change="fetchActivities" @current-change="fetchActivities" />
    </div>

    <!-- ====== 新增/编辑活动弹窗 ====== -->
    <el-dialog v-model="dlg" :title="isEdit?'编辑活动':'发起活动'" width="620px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="fref" :model="fm" :rules="rules" label-width="110px">
        <el-form-item label="活动名称" prop="title"><el-input v-model="fm.title" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="类型" prop="activityType"><el-select v-model="fm.activityType" style="width:100%"><el-option label="综合" value="综合" /><el-option label="走访慰问" value="走访慰问" /><el-option label="技能培训" value="技能培训" /><el-option label="物资分发" value="物资分发" /><el-option label="环境整治" value="环境整治" /><el-option label="医疗义诊" value="医疗义诊" /><el-option label="教育辅导" value="教育辅导" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="难度"><el-rate v-model="fm.difficulty" :max="5" show-score score-template="{value}星" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述" prop="description"><el-input v-model="fm.description" type="textarea" :rows="2" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="开始" prop="startTime"><el-date-picker v-model="fm.startTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束" prop="endTime"><el-date-picker v-model="fm.endTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="地点"><el-input v-model="fm.location" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="fm.contactPhone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="需人数"><el-input-number v-model="fm.needVolunteers" :min="1" :max="999" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="关联家庭"><el-select v-model="fm.familyId" placeholder="可选" filterable clearable :loading="familyLoading" style="width:100%"><el-option v-for="item in familyOptions" :key="item.familyId" :label="item.householderName + (item.familyCode?' ('+item.familyCode+')':'')" :value="item.familyId" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="dlg=false">取消</el-button><el-button type="primary" :loading="sub" @click="submitForm">确认</el-button></template>
    </el-dialog>

    <!-- ====== 评价弹窗 ====== -->
    <el-dialog v-model="feedbackDlg" title="服务评价" width="420px">
      <div style="text-align:center;padding:16px 0;">
        <div style="margin-bottom:12px;">请为本次服务打分</div>
        <el-rate v-model="feedbackRating" :max="5" show-score score-template="{value}星" style="justify-content:center;font-size:22px;" />
        <el-input v-model="feedbackText" type="textarea" :rows="2" placeholder="评价内容（选填）" style="margin-top:12px;" />
      </div>
      <template #footer><el-button @click="feedbackDlg=false">取消</el-button><el-button type="primary" :loading="fbSub" @click="submitFeedback">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getActivityPage, createActivity, updateActivity, deleteActivity, getFamilyPage, createVolunteerRecord, getVolunteerRecordPage, signInVolunteer, signOutVolunteer, submitVolunteerFeedback, getMyScore, getScorePage, getCertificatePage } from '@/api/system'

const myScore = ref(0), levelName = ref('一星志愿者')

const statItems = ref([
  { icon:'📋', label:'全部活动', value:'-', color:'#3B82F6' },
  { icon:'🙋', label:'我的报名', value:'-', color:'#10B981' },
  { icon:'🕐', label:'累计时长', value:'-', color:'#F59E0B' },
  { icon:'📜', label:'获得证书', value:'-', color:'#8B5CF6' },
])

const typeFilters = [{ label:'全部', value:'' }, { label:'走访慰问', value:'走访慰问' }, { label:'技能培训', value:'技能培训' }, { label:'物资分发', value:'物资分发' }, { label:'医疗义诊', value:'医疗义诊' }, { label:'教育辅导', value:'教育辅导' }]
const activeType = ref('')
const tabs = ref([{ key:'all', label:'全部活动' }, { key:'my', label:'我的志愿' }, { key:'score', label:'积分明细' }, { key:'cert', label:'我的证书' }])
const activeTab = ref('all')

const activityList = ref<any[]>([]), loading = ref(false), pn = ref(1), ps = ref(12), total = ref(0)
const myStatus = ref<Record<number,number>>({}), myFeedback = ref<Record<number,boolean>>({})

const fetchActivities = async () => {
  loading.value = true
  try {
    const params: any = { page: pn.value, size: ps.value }
    if (activeType.value) params.activityType = activeType.value
    const r = await getActivityPage(params)
    activityList.value = r.data?.records ?? r.data?.list ?? []
    total.value = r.data?.total ?? 0
    statItems.value[0].value = String(r.data?.total ?? 0)
  } catch {} finally { loading.value = false }
}

const fetchMyRecords = async () => {
  try {
    const r = await getVolunteerRecordPage({ page: 1, size: 999 })
    const records = r.data?.records ?? r.data?.list ?? []
    statItems.value[1].value = String(records.length)
    const hours = records.filter((rec: any) => rec.status >= 3).reduce((s: number, r: any) => s + parseFloat(r.serviceHours || '0'), 0)
    statItems.value[2].value = hours.toFixed(1) + 'h'
    const sm: Record<number,number> = {}, fm: Record<number,boolean> = {}
    records.forEach((rec: any) => { sm[rec.activityId] = rec.status; if (rec.status === 4) fm[rec.activityId] = true })
    myStatus.value = sm; myFeedback.value = fm
  } catch {}
}

const fetchScore = async () => {
  try { const r = await getMyScore(); myScore.value = r.data?.totalScore || 0; levelName.value = r.data?.levelName || '一星志愿者' } catch {}
}

// Score tab
const scoreList = ref<any[]>([]), scoreLoading = ref(false)
const fetchScoreDetail = async () => {
  if (activeTab.value !== 'score') return
  scoreLoading.value = true
  try { const r = await getScorePage({ page: 1, size: 20 }); scoreList.value = r.data?.records ?? [] } catch {} finally { scoreLoading.value = false }
}
const scoreTypeTag = (t: string) => ({ service_hour:'success', difficulty_bonus:'warning', training:'primary', referral:'info', rating_bonus:'danger' }[t]||'')
const scoreTypeLabel = (t: string) => ({ service_hour:'服务时长', difficulty_bonus:'难度奖励', training:'培训', referral:'推荐', rating_bonus:'评价奖励' }[t]||t)

// Cert tab
const certList = ref<any[]>([]), certLoading = ref(false)
const fetchCerts = async () => {
  if (activeTab.value !== 'cert') return
  certLoading.value = true
  try { const r = await getCertificatePage({ page: 1, size: 20 }); certList.value = r.data?.records ?? []; statItems.value[3].value = String(r.data?.total ?? 0) } catch {} finally { certLoading.value = false }
}

const fetchData = () => { fetchActivities(); fetchMyRecords(); fetchScoreDetail(); fetchCerts() }

const coverColor = (t: string) => {
  const colors: Record<string,string> = { '走访慰问':'#2563EB','技能培训':'#7C3AED','物资分发':'#059669','环境整治':'#D97706','医疗义诊':'#DC2626','教育辅导':'#0891B2' }
  return `linear-gradient(135deg, ${colors[t]||'#4B5563'}, ${colors[t]||'#6B7280'}88)`
}
const statusLabel = (s: number) => ({ 0:'草稿',1:'招募中',2:'进行中',3:'已结束',4:'已取消' }[s]||'未知')
const enrollPercent = (act: any) => act.needVolunteers ? Math.min(((act.registeredCount||0)/act.needVolunteers)*100,100) : 0
const isMyActivity = (act: any) => act.organizerId === Number(localStorage.getItem('userId'))

const dlg = ref(false), isEdit = ref(false), editId = ref<number|null>(null), sub = ref(false), fref = ref<any>(null)
const fm = reactive({ title:'', activityType:'综合', difficulty:1, description:'', startTime:'', endTime:'', location:'', contactPhone:'', needVolunteers:10, familyId:null as any, coverImage:'' })
const familyLoading = ref(false), familyOptions = ref<any[]>([])
const fetchFamilies = async () => { familyLoading.value = true; try { const r = await getFamilyPage({ page:1, size:200 }); familyOptions.value = r.data?.records??[] } catch {} finally { familyLoading.value = false } }
const rules = { title: [{ required:true, message:'请输入名称', trigger:'blur' }], activityType: [{ required:true, message:'请选择类型', trigger:'change' }], startTime: [{ required:true, message:'请选择时间', trigger:'change' }] }
const resetForm = () => { Object.assign(fm, { title:'', activityType:'综合', difficulty:1, description:'', startTime:'', endTime:'', location:'', contactPhone:'', needVolunteers:10, familyId:null, coverImage:'' }); fref.value?.clearValidate() }
const openCreateDialog = () => { isEdit.value = false; editId.value = null; resetForm(); fetchFamilies(); dlg.value = true }
const openEditDialog = (row: any) => { isEdit.value = true; editId.value = row.activityId; Object.assign(fm, { title:row.title, activityType:row.activityType||'综合', difficulty:row.difficulty||1, description:row.description||'', startTime:row.startTime||'', endTime:row.endTime||'', location:row.location||'', contactPhone:row.contactPhone||'', needVolunteers:row.needVolunteers||10, familyId:row.familyId||null, coverImage:row.coverImage||'' }); fetchFamilies(); dlg.value = true }
const submitForm = async () => {
  const ok = await fref.value?.validate().catch(()=>false); if (!ok) return; sub.value = true
  try { if (isEdit.value && editId.value) { await updateActivity(editId.value, { ...fm }); ElMessage.success('更新成功') } else { await createActivity({ ...fm }); ElMessage.success('创建成功') } dlg.value = false; fetchData() } catch {} finally { sub.value = false }
}

const handleEnroll = async (act: any) => { try { await createVolunteerRecord({ activityId: act.activityId }); ElMessage.success('报名成功'); fetchData() } catch {} }
const handleSignIn = async (act: any) => {
  try { const r = await getVolunteerRecordPage({ activityId: act.activityId, page:1, size:1 }); const records = r.data?.records??[]; if (records.length) { await signInVolunteer(records[0].recordId); ElMessage.success('签到成功'); fetchData() } } catch {}
}
const handleSignOut = async (act: any) => {
  try { const r = await getVolunteerRecordPage({ activityId: act.activityId, page:1, size:1 }); const records = r.data?.records??[]; if (records.length) { await signOutVolunteer(records[0].recordId); ElMessage.success('签退成功'); fetchData(); fetchScore() } } catch {}
}
const feedbackDlg = ref(false), feedbackAct = ref<any>(null), feedbackRating = ref(5), feedbackText = ref(''), fbSub = ref(false)
const openFeedback = (act: any) => { feedbackAct.value = act; feedbackRating.value = 5; feedbackText.value = ''; feedbackDlg.value = true }
const submitFeedback = async () => {
  if (!feedbackAct.value) return; fbSub.value = true
  try { const r = await getVolunteerRecordPage({ activityId: feedbackAct.value.activityId, page:1, size:1 }); const records = r.data?.records??[]; if (records.length) { await submitVolunteerFeedback(records[0].recordId, { rating: feedbackRating.value, feedback: feedbackText.value }); ElMessage.success('评价成功') } feedbackDlg.value = false; fetchData(); fetchScore() } catch {} finally { fbSub.value = false }
}
const handleDeleteActivity = async (act: any) => { try { await ElMessageBox.confirm('确定删除「'+act.title+'」？','提示',{confirmButtonText:'确定',cancelButtonText:'取消',type:'warning'}); await deleteActivity(act.activityId); ElMessage.success('删除成功'); fetchData() } catch {} }

onMounted(() => { fetchActivities(); fetchMyRecords(); fetchScore(); fetchScoreDetail(); fetchCerts() })
</script>

<style scoped>
.volunteer-hub { padding: 24px; max-width: 1200px; margin: 0 auto; }
.hero { background: linear-gradient(135deg,#0A2647,#2D7A9B); border-radius: 16px; padding: 24px 28px; color:#fff; display:flex; justify-content:space-between; align-items:center; margin-bottom:24px; }
.hero h1 { margin:0; font-size:22px; }
.hero p { margin:4px 0 0; font-size:13px; opacity:.8; }
.hero-score { display:flex; align-items:center; gap:16px; }
.score-badge { text-align:center; }
.score-num { font-size:28px; font-weight:800; display:block; line-height:1; }
.score-label { font-size:11px; opacity:.7; }
.level-badge { padding:4px 14px; background:rgba(255,255,255,.2); border-radius:20px; font-size:13px; font-weight:600; }
.stats-row { display:grid; grid-template-columns:repeat(4,1fr); gap:14px; margin-bottom:20px; }
.stat-card { background:#fff; border-radius:12px; padding:16px 18px; box-shadow:0 2px 8px rgba(0,0,0,.04); display:flex; align-items:center; gap:12px; }
.stat-icon { width:38px; height:38px; border-radius:10px; display:flex; align-items:center; justify-content:center; font-size:18px; flex-shrink:0; }
.stat-val { font-size:20px; font-weight:800; color:#1E293B; line-height:1.2; }
.stat-lbl { font-size:12px; color:#64748B; }
.action-bar { display:flex; justify-content:space-between; align-items:center; margin-bottom:14px; flex-wrap:wrap; gap:10px; }
.filter-pills { display:flex; gap:6px; flex-wrap:wrap; }
.pill { padding:5px 14px; border:1px solid #E2E8F0; border-radius:16px; background:#fff; color:#64748B; font-size:12px; cursor:pointer; transition:.15s; }
.pill:hover, .pill--active { background:#0A2647; color:#fff; border-color:#0A2647; }
.btn-add { display:inline-flex; align-items:center; gap:4px; padding:7px 18px; border:none; border-radius:10px; background:linear-gradient(135deg,#0A2647,#2D7A9B); color:#fff; font-weight:600; cursor:pointer; font-size:13px; }
.tab-bar { display:flex; gap:2px; margin-bottom:16px; border-bottom:1px solid #E2E8F0; }
.tab { padding:8px 18px; border:none; background:none; color:#64748B; font-size:13px; font-weight:500; cursor:pointer; border-bottom:2px solid transparent; margin-bottom:-1px; transition:.15s; position:relative; }
.tab--active { color:#0A2647; border-bottom-color:#0A2647; font-weight:600; }
.tab-badge { display:inline-flex; align-items:center; justify-content:center; width:18px; height:18px; border-radius:50%; background:#EF4444; color:#fff; font-size:10px; margin-left:6px; }
.activity-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(300px,1fr)); gap:16px; margin-bottom:20px; }
.activity-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,.04); transition:.25s; }
.activity-card:hover { transform:translateY(-3px); box-shadow:0 8px 24px rgba(0,0,0,.1); }
.card-cover { height:100px; display:flex; align-items:flex-start; padding:12px; gap:6px; flex-wrap:wrap; }
.card-type-tag { background:rgba(255,255,255,.9); padding:2px 10px; border-radius:10px; font-size:11px; font-weight:600; }
.card-diff { font-size:11px; }
.card-status-tag { margin-left:auto; padding:2px 8px; border-radius:8px; font-size:10px; font-weight:600; }
.st-0 { background:#F3F4F6; color:#6B7280; } .st-1 { background:#D1FAE5; color:#059669; }
.st-2 { background:#DBEAFE; color:#2563EB; } .st-3 { background:#FEF3C7; color:#D97706; }
.st-4 { background:#FEE2E2; color:#DC2626; }
.card-body { padding:14px; }
.card-title { font-size:15px; font-weight:700; margin:0 0 8px; color:#1E293B; }
.card-meta-line { font-size:12px; color:#64748B; margin-bottom:4px; }
.card-progress { display:flex; align-items:center; gap:8px; margin:10px 0; }
.progress-bar { flex:1; height:5px; background:#F1F5F9; border-radius:3px; overflow:hidden; }
.progress-fill { height:100%; border-radius:3px; transition:width .5s; }
.progress-text { font-size:11px; font-weight:600; color:#64748B; flex-shrink:0; }
.card-actions { display:flex; gap:6px; flex-wrap:wrap; }
.act-btn { padding:4px 10px; border:1px solid #E2E8F0; border-radius:6px; background:#fff; color:#64748B; font-size:11px; cursor:pointer; transition:.15s; }
.act-btn:hover { border-color:#2D7A9B; color:#2D7A9B; }
.act-btn.primary { background:linear-gradient(135deg,#0A2647,#2D7A9B); color:#fff; border-color:transparent; }
.act-btn.warning { background:#F59E0B; color:#fff; border-color:transparent; }
.act-btn.danger { border-color:#EF4444; color:#EF4444; }
.act-btn.danger:hover { background:#FEF2F2; }
.act-btn.outline { }
.done-tag { font-size:11px; color:#10B981; font-weight:600; padding:4px 0; }
.score-section, .cert-section { margin-bottom:20px; }
.cert-grid { display:flex; flex-direction:column; gap:10px; min-height:60px; }
.cert-card { display:flex; align-items:center; gap:12px; padding:12px 16px; border:1px solid #E2E8F0; border-radius:8px; }
.cert-icon { font-size:24px; }
.cert-name { font-weight:600; font-size:14px; color:#1E293B; }
.cert-num { font-size:11px; color:#94A3B8; }
.pagination-wrap { display:flex; justify-content:center; }
@media(max-width:768px){ .stats-row { grid-template-columns:repeat(2,1fr); } }
</style>
