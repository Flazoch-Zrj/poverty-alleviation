<template>
  <div class="training-page">

    <!-- ====== 顶部统计指标卡 ====== -->
    <div class="stats-row">
      <div class="stat-mini" v-for="s in statItems" :key="s.label">
        <div class="stat-mini__icon" :style="{ background: s.bg }">
          <el-icon :size="18" color="#fff"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-mini__info">
          <span class="stat-mini__value">{{ s.value }}</span>
          <span class="stat-mini__label">{{ s.label }}</span>
        </div>
      </div>
    </div>

    <!-- ====== 头部：标题 + 新增按钮 ====== -->
    <div class="page-header">
      <div>
        <h2 class="page-title">技能培训</h2>
        <p class="page-subtitle">职业技能提升 · 助力稳定就业</p>
      </div>
      <button class="btn-add" @click="openDialog(false)" v-if="isManager">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新增课程
      </button>
    </div>

    <!-- ====== 胶囊式状态筛选标签 ====== -->
    <div class="filter-pills">
      <button
        v-for="pill in filterPills"
        :key="pill.value"
        class="pill"
        :class="{ 'pill--active': activeFilter === pill.value }"
        @click="activeFilter = pill.value; fetchCourses()"
      >
        {{ pill.label }}
      </button>
    </div>

    <!-- ====== 课程卡片网格 ====== -->
    <div class="course-grid" v-loading="loading">
      <div class="course-card" v-for="course in courseList" :key="course.courseId">
        <!-- 卡片顶部：名称 + 状态 -->
        <div class="card-top">
          <h3 class="card-title">{{ course.title }}</h3>
          <span class="card-status" :class="statusClass(course.status)">{{ statusLabel(course.status) }}</span>
        </div>

        <!-- 卡片中部：讲师 + 时间 + 地点 -->
        <div class="card-meta">
          <div class="meta-row">
            <span class="meta-icon avatar-circle">{{ course.trainer?.charAt(0) || '师' }}</span>
            <span class="meta-text">{{ course.trainer || '待定' }}</span>
          </div>
          <div class="meta-row">
            <svg class="meta-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            <span class="meta-text">{{ formatTime(course.startTime) }} ~ {{ formatTime(course.endTime) }}</span>
          </div>
          <div class="meta-row">
            <svg class="meta-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            <span class="meta-text location-text">{{ course.location || '待定' }}</span>
          </div>
        </div>

        <!-- 卡片底部：名额进度 + 操作 -->
        <div class="card-bottom">
          <div class="enroll-section">
            <div class="enroll-bar-wrap">
              <div
                class="enroll-bar-fill"
                :style="{ width: enrollPercent(course) + '%', background: enrollPercent(course) >= 100 ? '#EF4444' : '#10B981' }"
              ></div>
            </div>
            <span class="enroll-text" :class="{ 'enroll-full': (course.enrolledCount||0) >= (course.maxEnroll||999) }">
              {{ course.enrolledCount || 0 }} / {{ course.maxEnroll || '∞' }}
            </span>
          </div>
          <div class="card-actions">
            <!-- 报名按钮 -->
            <button
              v-if="course.status === 2 && (course.enrolledCount||0) < (course.maxEnroll||999)"
              class="act-btn act-btn--primary"
              @click="handleEnroll(course)"
            >
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="20 6 9 17 4 12"/></svg>
              报名
            </button>
            <!-- 编辑按钮 -->
            <button class="act-btn" @click="openDialog(true, course)" v-if="isManager">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              编辑
            </button>
            <!-- 删除按钮 -->
            <button class="act-btn act-btn--danger" @click="handleDelete(course)" v-if="isManager">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              删除
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && courseList.length === 0">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
        <p>暂无培训课程</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pn"
        v-model:page-size="ps"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchCourses"
        @current-change="fetchCourses"
      />
    </div>

    <!-- ====== 我的报名 ====== -->
    <div class="my-section">
      <div class="my-header">
        <h3 class="my-title">我的报名</h3>
        <span class="my-count">{{ mt }} 门课程</span>
      </div>

      <div class="my-grid" v-loading="myLoading">
        <div class="my-card" v-for="item in myEnrollList" :key="item.enrollId">
          <div class="my-card-left">
            <div class="my-dot" :class="enrollDotClass(item.status)"></div>
            <div class="my-info">
              <span class="my-course-name">{{ item.courseTitle }}</span>
              <span class="my-enroll-time">{{ item.enrollTime ? $dayjs(item.enrollTime).format('MM月DD日 HH:mm') : '-' }} 报名</span>
            </div>
          </div>
          <div class="my-card-right">
            <span class="my-status-tag" :class="enrollTagClass(item.status)">
              {{ enrollStatusLabel(item.status) }}
            </span>
            <button
              v-if="item.status === 1"
              class="act-btn act-btn--primary act-btn--sm"
              @click="handleSignIn(item)"
            >
              签到
            </button>
          </div>
        </div>
        <div class="empty-state" v-if="!myLoading && myEnrollList.length === 0">
          <p>暂无报名记录</p>
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="mpn"
          v-model:page-size="mps"
          :total="mt"
          layout="total, prev, pager, next"
          @size-change="fetchMyEnroll"
          @current-change="fetchMyEnroll"
          small
        />
      </div>
    </div>

    <!-- ====== 弹窗：新增 / 编辑课程 ====== -->
    <el-dialog v-model="dlg" :title="isEdit ? '编辑课程' : '新增课程'" width="580px" :close-on-click-modal="false" class="custom-dialog">
      <el-form ref="fref" :model="fm" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="title">
          <el-input v-model="fm.title" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程内容" prop="content">
          <el-input v-model="fm.content" type="textarea" :rows="3" placeholder="请输入课程内容描述" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="讲师" prop="trainer">
              <el-input v-model="fm.trainer" placeholder="讲师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地点" prop="location">
              <el-input v-model="fm.location" placeholder="地点/线上链接" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="fm.startTime" type="datetime" placeholder="选择开始" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="fm.endTime" type="datetime" placeholder="选择结束" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="名额" prop="maxEnroll">
              <el-input-number v-model="fm.maxEnroll" :min="1" :max="999" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="fm.status" style="width:100%">
                <el-option label="预告" :value="1" />
                <el-option label="报名中" :value="2" />
                <el-option label="进行中" :value="3" />
                <el-option label="已结束" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <button class="dialog-btn dialog-btn--cancel" @click="dlg=false">取消</button>
        <button class="dialog-btn dialog-btn--confirm" :loading="sub" @click="submitForm">确认</button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Notebook, Reading, UserFilled, TrophyBase } from '@element-plus/icons-vue'
import { getTrainingCoursePage, createTrainingCourse, updateTrainingCourse, deleteTrainingCourse, createEnrollment, signInEnrollment, getEnrollmentPage } from '@/api/system'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isManager = computed(() => ['admin','cadre'].includes(userStore.userInfo?.roleCode || ''))

// ============================================================
// 统计指标
// ============================================================
const statItems = ref([
  { label: '总课程数', value: '-', icon: Notebook, bg: 'linear-gradient(135deg, #0A2647, #2D7A9B)' },
  { label: '进行中',   value: '-', icon: Reading, bg: 'linear-gradient(135deg, #10B981, #34D399)' },
  { label: '总报名',   value: '-', icon: UserFilled, bg: 'linear-gradient(135deg, #F59E0B, #FBBF24)' },
  { label: '我的课程', value: '-', icon: TrophyBase, bg: 'linear-gradient(135deg, #3B82F6, #60A5FA)' },
])

// ============================================================
// 筛选胶囊
// ============================================================
const filterPills = [
  { label: '全部', value: undefined },
  { label: '预告', value: 1 },
  { label: '报名中', value: 2 },
  { label: '进行中', value: 3 },
  { label: '已结束', value: 4 },
]
const activeFilter = ref<number | undefined>(undefined)

// ============================================================
// 课程列表 — 卡片网格
// ============================================================
const courseList = ref<any[]>([])
const loading = ref(false)
const pn = ref(1)
const ps = ref(12)
const total = ref(0)

const fetchCourses = async () => {
  loading.value = true
  try {
    const params: any = { page: pn.value, size: ps.value }
    if (activeFilter.value !== undefined) params.status = activeFilter.value
    const r = await getTrainingCoursePage(params)
    const rows = r.data?.records || r.data?.list || []
    courseList.value = rows
    total.value = r.data?.total || 0
    // 更新统计
    statItems.value[0].value = String(r.data?.total || rows.length)
    statItems.value[1].value = String(rows.filter((c: any) => c.status === 3).length)
    statItems.value[2].value = String(rows.reduce((a: number, c: any) => a + (c.enrolledCount || 0), 0))
  } catch {} finally { loading.value = false }
}

// ============================================================
// 课程卡片辅助函数
// ============================================================
const statusLabel = (s: number) => ({ 1: '预告', 2: '报名中', 3: '进行中', 4: '已结束' }[s] || '未知')
const statusClass = (s: number) => ({
  1: 'status--info',
  2: 'status--warning',
  3: 'status--success',
  4: 'status--default',
}[s] || 'status--default')

const formatTime = (t: string) => {
  if (!t) return '-'
  const d = new Date(t)
  return `${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

const enrollPercent = (course: any) => {
  if (!course.maxEnroll) return 0
  return Math.min(Math.round(((course.enrolledCount || 0) / course.maxEnroll) * 100), 100)
}

// ============================================================
// 弹窗
// ============================================================
const dlg = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const sub = ref(false)
const fref = ref<FormInstance>()

const fm = reactive({ title: '', content: '', trainer: '', startTime: '', endTime: '', location: '', maxEnroll: 30, status: 1 })

const rules: FormRules = {
  title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
}

const openDialog = (edit: boolean, row?: any) => {
  isEdit.value = edit
  editId.value = null
  if (edit && row) {
    editId.value = row.courseId
    Object.assign(fm, { title: row.title, content: row.content, trainer: row.trainer, startTime: row.startTime, endTime: row.endTime, location: row.location, maxEnroll: row.maxEnroll, status: row.status })
  } else {
    Object.assign(fm, { title: '', content: '', trainer: '', startTime: '', endTime: '', location: '', maxEnroll: 30, status: 1 })
  }
  dlg.value = true
}

const submitForm = async () => {
  const ok = await fref.value?.validate().catch(() => false)
  if (!ok) return
  sub.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateTrainingCourse(editId.value, { ...fm })
      ElMessage.success('更新成功')
    } else {
      await createTrainingCourse({ ...fm })
      ElMessage.success('新增成功')
    }
    dlg.value = false
    fetchCourses()
  } catch {} finally { sub.value = false }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该课程吗？', '提示')
    await deleteTrainingCourse(row.courseId)
    ElMessage.success('已删除')
    fetchCourses()
  } catch {}
}

// ============================================================
// 报名
// ============================================================
const handleEnroll = async (row: any) => {
  try {
    await createEnrollment({ courseId: row.courseId })
    ElMessage.success('报名成功')
    fetchCourses()
    fetchMyEnroll()
  } catch (e: any) {
    ElMessage.error(e?.message || '报名失败')
  }
}

const handleSignIn = async (item: any) => {
  try {
    await signInEnrollment(item.enrollId)
    ElMessage.success('签到成功')
    fetchMyEnroll()
  } catch (e: any) {
    ElMessage.error(e?.message || '签到失败')
  }
}

// ============================================================
// 我的报名 — 紧凑型卡片列表
// ============================================================
const myEnrollList = ref<any[]>([])
const myLoading = ref(false)
const mpn = ref(1)
const mps = ref(10)
const mt = ref(0)

const fetchMyEnroll = async () => {
  myLoading.value = true
  try {
    const r = await getEnrollmentPage({ page: mpn.value, size: mps.value })
    myEnrollList.value = r.data?.records || r.data?.list || []
    mt.value = r.data?.total || 0
    statItems.value[3].value = String(r.data?.total || 0)
  } catch {} finally { myLoading.value = false }
}

const enrollDotClass = (s: number) => ({
  1: 'dot--pending',
  2: 'dot--success',
  3: 'dot--fail',
}[s] || 'dot--pending')

const enrollTagClass = (s: number) => ({
  1: 'tag--pending',
  2: 'tag--success',
  3: 'tag--fail',
}[s] || 'tag--pending')

const enrollStatusLabel = (s: number) => ({
  0: '审核中',
  1: '已通过',
  2: '已签到',
  3: '已拒绝',
}[s] || '未知')

// ============================================================
// 生命周期
// ============================================================
onMounted(() => {
  fetchCourses()
  fetchMyEnroll()
})
</script>

<style scoped lang="scss">
/* ============================================================
   技能培训 — 企业级教育管理面板
   主色: #0A2647  辅色: #2D7A9B  强调色: #F9A826
   状态色: 绿 #10B981 / 橙 #F59E0B / 蓝 #3B82F6 / 灰 #9CA3AF
   ============================================================ */

.training-page {
  padding: 28px;
  animation: fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ====== 统计指标卡 ====== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-mini {
  display: flex;
  align-items: center;
  gap: 14px;
  background: var(--p-bg-card);
  border: 1px solid var(--p-border-color);
  border-radius: var(--p-radius-lg);
  padding: 18px 20px;
  box-shadow: var(--p-shadow-sm);

  &__icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 3px 8px rgba(0,0,0,0.08);
  }

  &__info {
    display: flex;
    flex-direction: column;
  }

  &__value {
    font-size: 20px;
    font-weight: 800;
    color: var(--p-text-primary);
    line-height: 1.1;
    font-variant-numeric: tabular-nums;
  }

  &__label {
    font-size: 11px;
    color: var(--p-text-secondary);
    margin-top: 2px;
  }
}

/* ====== 页面头部 ====== */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;

  .page-title {
    font-size: 22px;
    font-weight: 800;
    color: var(--p-text-primary);
    margin: 0 0 4px;
    letter-spacing: -0.01em;
  }

  .page-subtitle {
    font-size: 13px;
    color: var(--p-text-secondary);
    margin: 0;
  }
}

.btn-add {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #0A2647, #2D7A9B);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 4px 14px rgba(10, 38, 71, 0.3);
  white-space: nowrap;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(10, 38, 71, 0.4);
  }

  &:active {
    transform: translateY(0);
  }

  svg {
    transition: transform 0.25s;
  }

  &:hover svg {
    transform: rotate(90deg);
  }
}

/* ====== 胶囊筛选 ====== */
.filter-pills {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.pill {
  padding: 7px 18px;
  border: 1px solid var(--p-border-color);
  border-radius: 20px;
  background: var(--p-bg-card);
  color: var(--p-text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  user-select: none;

  &:hover {
    border-color: #2D7A9B;
    color: #2D7A9B;
  }

  &--active {
    background: #0A2647;
    border-color: #0A2647;
    color: #fff;
    font-weight: 600;

    &:hover {
      background: #1A3A5C;
      border-color: #1A3A5C;
      color: #fff;
    }
  }
}

/* ====== 课程卡片网格 ====== */
.course-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  min-height: 200px;
}

.course-card {
  background: var(--p-bg-card);
  border: 1px solid var(--p-border-color);
  border-radius: 16px;
  padding: 22px 22px 18px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(10, 38, 71, 0.1), 0 0 0 1px rgba(45, 122, 155, 0.15);
    border-color: rgba(45, 122, 155, 0.3);
  }
}

/* 卡片顶部 */
.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--p-text-primary);
  margin: 0;
  line-height: 1.3;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-status {
  flex-shrink: 0;
  padding: 3px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.status--info { background: #DBEAFE; color: #2563EB; }
.status--warning { background: #FEF3C7; color: #D97706; }
.status--success { background: #D1FAE5; color: #059669; }
.status--default { background: #F3F4F6; color: #6B7280; }

/* 卡片中部 */
.card-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
  flex: 1;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  color: var(--p-text-muted);
}

.avatar-circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0A2647, #2D7A9B);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
}

.meta-text {
  font-size: 13px;
  color: var(--p-text-regular);
  line-height: 1.3;
}

.location-text {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 卡片底部 */
.card-bottom {
  border-top: 1px solid var(--p-border-light);
  padding-top: 14px;
}

.enroll-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.enroll-bar-wrap {
  flex: 1;
  height: 6px;
  background: #F1F5F9;
  border-radius: 3px;
  overflow: hidden;
}

.enroll-bar-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.enroll-text {
  font-size: 12px;
  font-weight: 600;
  color: var(--p-text-secondary);
  flex-shrink: 0;
  font-variant-numeric: tabular-nums;

  &.enroll-full {
    color: #EF4444;
  }
}

.card-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.act-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border: 1px solid var(--p-border-color);
  border-radius: 8px;
  background: var(--p-bg-card);
  color: var(--p-text-regular);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  white-space: nowrap;

  &:hover {
    border-color: #2D7A9B;
    color: #2D7A9B;
    background: #F0F7FF;
  }

  &--primary {
    background: linear-gradient(135deg, #0A2647, #2D7A9B);
    border-color: transparent;
    color: #fff;

    &:hover {
      background: linear-gradient(135deg, #1A3A5C, #3D8A9B);
      border-color: transparent;
      color: #fff;
    }
  }

  &--danger:hover {
    border-color: #EF4444;
    color: #EF4444;
    background: #FEF2F2;
  }

  &--sm {
    padding: 4px 10px;
    font-size: 11px;
  }
}

/* ====== 空状态 ====== */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  color: var(--p-text-muted);

  p {
    margin: 12px 0 0;
    font-size: 14px;
  }
}

/* ====== 分页 ====== */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 32px;
}

/* ====== 我的报名 ====== */
.my-section {
  background: var(--p-bg-card);
  border: 1px solid var(--p-border-color);
  border-radius: var(--p-radius-lg);
  padding: 24px;
  box-shadow: var(--p-shadow-sm);
  margin-bottom: 24px;
}

.my-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--p-border-color);
}

.my-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--p-text-primary);
  margin: 0;
}

.my-count {
  font-size: 12px;
  color: var(--p-text-muted);
}

.my-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 60px;
}

.my-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: var(--p-bg-page);
  border-radius: 10px;
  border: 1px solid transparent;
  transition: all 0.2s;

  &:hover {
    border-color: var(--p-border-color);
  }
}

.my-card-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.my-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;

  &.dot--pending { background: #F59E0B; }
  &.dot--success { background: #10B981; }
  &.dot--fail { background: #EF4444; }
}

.my-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.my-course-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--p-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.my-enroll-time {
  font-size: 11px;
  color: var(--p-text-muted);
  margin-top: 2px;
}

.my-card-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.my-status-tag {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;

  &.tag--pending { background: #FEF3C7; color: #D97706; }
  &.tag--success { background: #D1FAE5; color: #059669; }
  &.tag--fail { background: #FEE2E2; color: #DC2626; }.tag--fail { background: #FEE2E2; color: #DC2626; }
  &.tag--fail { background: #FEE2E2; color: #DC2626; }.tag--primary { background: #DBEAFE; color: #2563EB; }
}

/* ====== 自定义弹窗按钮 ====== */
.dialog-btn {
  padding: 8px 22px;
  border-radius: 10px;
  border: 1px solid var(--p-border-color);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &--cancel {
    background: var(--p-bg-card);
    color: var(--p-text-regular);
    &:hover { border-color: #D1D5DB; }
  }

  &--confirm {
    background: linear-gradient(135deg, #0A2647, #2D7A9B);
    border-color: transparent;
    color: #fff;
    box-shadow: 0 3px 10px rgba(10, 38, 71, 0.2);

    &:hover {
      box-shadow: 0 6px 18px rgba(10, 38, 71, 0.3);
    }
  }
}

/* ====== 响应式 ====== */
@media (max-width: 1200px) {
  .course-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .course-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    gap: 12px;
  }

  .btn-add {
    width: 100%;
    justify-content: center;
  }

  .training-page {
    padding: 20px 16px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }

  .stat-mini {
    padding: 14px;
  }

  .stat-mini__value {
    font-size: 17px;
  }

  .card-actions {
    flex-wrap: wrap;
  }
}
</style>
