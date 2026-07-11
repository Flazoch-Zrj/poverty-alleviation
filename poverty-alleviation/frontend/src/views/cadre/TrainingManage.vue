<template>
  <div class="training-manage">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="课程管理" name="course">
        <div class="toolbar"><el-button type="primary" @click="openAddDialog">新增课程</el-button></div>
        <el-table :data="courseList" v-loading="loading" border stripe style="width:100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="title" label="课程名称" min-width="160" />
          <el-table-column prop="trainer" label="讲师" width="90" />
          <el-table-column prop="maxEnroll" label="名额" width="70" align="center" />
          <el-table-column prop="enrolledCount" label="已报" width="70" align="center" />
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{row}"><el-tag :type="{1:'info',2:'warning',3:'success',4:''}[row.status]" size="small">{{ {1:'预告',2:'报名中',3:'进行中',4:'已结束'}[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="startTime" label="时间" width="130">
            <template #default="{row}">{{ row.startTime ? $dayjs(row.startTime).format('MM/DD') : '' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="{row}">
              <el-button size="small" type="primary" link @click="openEditDialog(row)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="handleDelete(row)"><template #reference><el-button size="small" type="danger" link>删除</el-button></template></el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap"><el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="pt" layout="total,sizes,prev,pager,next" @size-change="fetchCourses" @current-change="fetchCourses" /></div>
      </el-tab-pane>

      <el-tab-pane label="报名审核" name="review">
        <el-table :data="enrollList" v-loading="enrollLoading" border stripe style="width:100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="courseTitle" label="课程" min-width="160" />
          <el-table-column label="学员ID" width="90" prop="userId" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{row}">
              <el-tag :type="{0:'warning',1:'success',2:'primary',3:'danger'}[row.status]" size="small">{{ ['待审核','已通过','已签到','已拒绝'][row.status] || '未知' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="enrollTime" label="报名时间" width="160" />
          <el-table-column label="操作" width="140" fixed="right" align="center">
            <template #default="{row}">
              <el-button v-if="row.status === 0" size="small" type="success" link @click="review(row, 1)">通过</el-button>
              <el-button v-if="row.status === 0" size="small" type="danger" link @click="review(row, 3)">拒绝</el-button>
              <span v-else style="color:#909399;font-size:12px;">已处理</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap"><el-pagination v-model:current-page="apn" v-model:page-size="aps" :total="apt" layout="total,prev,pager,next" @size-change="fetchEnrollments" @current-change="fetchEnrollments" /></div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dlg" :title="isEdit?'编辑课程':'新增课程'" width="540px" :close-on-click-modal="false">
      <el-form ref="fref" :model="fm" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="title"><el-input v-model="fm.title" /></el-form-item>
        <el-form-item label="课程内容" prop="content"><el-input v-model="fm.content" type="textarea" :rows="3" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="讲师" prop="trainer"><el-input v-model="fm.trainer" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="地点" prop="location"><el-input v-model="fm.location" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="开始" prop="startTime"><el-date-picker v-model="fm.startTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束" prop="endTime"><el-date-picker v-model="fm.endTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="名额" prop="maxEnroll"><el-input-number v-model="fm.maxEnroll" :min="1" :max="999" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="fm.status" style="width:100%"><el-option label="预告" :value="1" /><el-option label="报名中" :value="2" /><el-option label="进行中" :value="3" /><el-option label="已结束" :value="4" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="dlg=false">取消</el-button><el-button type="primary" :loading="sub" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTrainingCoursePage, createTrainingCourse, updateTrainingCourse, deleteTrainingCourse, getEnrollmentPage, reviewEnrollment } from '@/api/system'

const activeTab = ref('course')

// Courses
const courseList = ref<any[]>([]); const loading = ref(false)
const pn = ref(1); const ps = ref(10); const pt = ref(0)
const fetchCourses = async () => {
  loading.value = true
  try { const r = await getTrainingCoursePage({ page: pn.value, size: ps.value }); courseList.value = r.data?.records ?? []; pt.value = r.data?.total ?? 0 } catch {} finally { loading.value = false }
}

// Enrollments
const enrollList = ref<any[]>([]); const enrollLoading = ref(false)
const apn = ref(1); const aps = ref(10); const apt = ref(0)
const fetchEnrollments = async () => {
  enrollLoading.value = true
  try { const r = await getEnrollmentPage({ page: apn.value, size: aps.value }); enrollList.value = r.data?.records ?? []; apt.value = r.data?.total ?? 0 } catch {} finally { enrollLoading.value = false }
}

const review = async (row: any, status: number) => {
  try { await reviewEnrollment(row.enrollId, status); ElMessage.success(status===1?'已通过':'已拒绝'); fetchEnrollments() } catch { ElMessage.error('操作失败') }
}

// Dialog
const dlg = ref(false); const isEdit = ref(false); const sub = ref(false); const editId = ref<number|null>(null); const fref = ref<any>(null)
const fm = reactive({ title:'', content:'', trainer:'', startTime:'', endTime:'', location:'', maxEnroll:30, status:1 })
const rules = { title:[{required:true,message:'请输入课程名称',trigger:'blur'}], startTime:[{required:true,message:'请选择开始时间',trigger:'change'}], endTime:[{required:true,message:'请选择结束时间',trigger:'change'}] }
const openAddDialog = () => { isEdit.value = false; editId.value = null; Object.assign(fm,{title:'',content:'',trainer:'',startTime:'',endTime:'',location:'',maxEnroll:30,status:1}); dlg.value = true }
const openEditDialog = (row: any) => { isEdit.value = true; editId.value = row.courseId; Object.assign(fm,{title:row.title,content:row.content||'',trainer:row.trainer||'',startTime:row.startTime||'',endTime:row.endTime||'',location:row.location||'',maxEnroll:row.maxEnroll||30,status:row.status||1}); dlg.value = true }
const submitForm = async () => {
  const ok = await fref.value?.validate().catch(()=>false); if (!ok) return; sub.value = true
  try { if (isEdit.value && editId.value) { await updateTrainingCourse(editId.value, { ...fm }); ElMessage.success('更新成功') } else { await createTrainingCourse({ ...fm }); ElMessage.success('创建成功') } dlg.value = false; fetchCourses() } catch { ElMessage.error('操作失败') } finally { sub.value = false }
}
const handleDelete = async (row: any) => { try { await deleteTrainingCourse(row.courseId); ElMessage.success('删除成功'); fetchCourses() } catch { ElMessage.error('删除失败') } }

onMounted(() => { fetchCourses(); fetchEnrollments() })
</script>
<style scoped>
.training-manage { padding: 20px; }
.toolbar { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
