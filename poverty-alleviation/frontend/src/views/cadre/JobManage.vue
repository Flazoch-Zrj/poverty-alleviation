<template>
  <div class="job-manage">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="岗位管理" name="position">
        <div class="toolbar">
          <el-button type="primary" @click="openAddDialog">新增岗位</el-button>
        </div>
        <el-table :data="positionList" v-loading="loading" border stripe style="width:100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="title" label="岗位名称" min-width="140" />
          <el-table-column prop="company" label="公司" min-width="120" />
          <el-table-column prop="salaryRange" label="薪资" width="110" />
          <el-table-column prop="workplace" label="地点" width="120" />
          <el-table-column prop="contact" label="联系方式" width="120" />
          <el-table-column prop="publishTime" label="发布时间" width="160" />
          <el-table-column label="操作" width="140" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openEditDialog(row)">编辑</el-button>
              <el-popconfirm title="确定删除该岗位吗？" @confirm="handleDelete(row)">
                <template #reference><el-button size="small" type="danger" link>删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap">
          <el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="pt" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="fetchPositions" @current-change="fetchPositions" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="申请审核" name="application">
        <el-table :data="applyList" v-loading="applyLoading" border stripe style="width:100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="jobTitle" label="岗位名称" min-width="140" />
          <el-table-column label="申请人ID" width="100" prop="applicantUserId" />
          <el-table-column prop="applyStatus" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTag(row.applyStatus)" size="small">{{ statusLabel(row.applyStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" />
          <el-table-column prop="applyTime" label="申请时间" width="160" />
          <el-table-column label="操作" width="140" fixed="right" align="center">
            <template #default="{ row }">
              <el-button v-if="row.applyStatus === 1" size="small" type="success" link @click="updateStatus(row, 2)">通过</el-button>
              <el-button v-if="row.applyStatus === 1" size="small" type="danger" link @click="updateStatus(row, 3)">拒绝</el-button>
              <span v-else style="color:#909399;font-size:12px;">已处理</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap">
          <el-pagination v-model:current-page="apn" v-model:page-size="aps" :total="apt" layout="total,prev,pager,next" @size-change="fetchApplications" @current-change="fetchApplications" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑岗位弹窗 -->
    <el-dialog v-model="dlg" :title="isEdit?'编辑岗位':'新增岗位'" width="560px" :close-on-click-modal="false">
      <el-form ref="fref" :model="fm" :rules="rules" label-width="100px">
        <el-form-item label="岗位名称" prop="title"><el-input v-model="fm.title" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="公司名称" prop="company"><el-input v-model="fm.company" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系方式" prop="contact"><el-input v-model="fm.contact" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="薪资范围" prop="salaryRange"><el-input v-model="fm.salaryRange" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="工作地点" prop="workplace"><el-input v-model="fm.workplace" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="岗位要求" prop="requirements"><el-input v-model="fm.requirements" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg=false">取消</el-button><el-button type="primary" :loading="sub" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getJobPage, createJob, updateJob, deleteJob, getJobApplicationPage, updateJobApplicationStatus } from '@/api/system'

const activeTab = ref('position')

// Positions
const positionList = ref<any[]>([])
const loading = ref(false)
const pn = ref(1); const ps = ref(10); const pt = ref(0)

const fetchPositions = async () => {
  loading.value = true
  try { const r = await getJobPage({ page: pn.value, size: ps.value }); positionList.value = r.data?.records ?? r.data?.list ?? []; pt.value = r.data?.total ?? 0 } catch {} finally { loading.value = false }
}

// Applications
const applyList = ref<any[]>([])
const applyLoading = ref(false)
const apn = ref(1); const aps = ref(10); const apt = ref(0)

const fetchApplications = async () => {
  applyLoading.value = true
  try { const r = await getJobApplicationPage({ page: apn.value, size: aps.value }); applyList.value = r.data?.records ?? r.data?.list ?? []; apt.value = r.data?.total ?? 0 } catch {} finally { applyLoading.value = false }
}

const statusTag = (s: number) => ({ 1:'warning', 2:'success', 3:'danger' }[s] || '')
const statusLabel = (s: number) => ({ 1:'申请中', 2:'已通过', 3:'已拒绝' }[s] || '未知')

const updateStatus = async (row: any, status: number) => {
  try { await updateJobApplicationStatus(row.applyId, status); ElMessage.success('操作成功'); fetchApplications() } catch { ElMessage.error('操作失败') }
}

// Dialog
const dlg = ref(false); const isEdit = ref(false); const sub = ref(false); const editId = ref<number|null>(null); const fref = ref<any>(null)
const fm = reactive({ title:'', company:'', contact:'', salaryRange:'', workplace:'', requirements:'' })
const rules = { title: [{ required:true, message:'请输入岗位名称', trigger:'blur' }], company: [{ required:true, message:'请输入公司名称', trigger:'blur' }], contact: [{ required:true, message:'请输入联系方式', trigger:'blur' }] }

const openAddDialog = () => { isEdit.value = false; editId.value = null; Object.assign(fm, { title:'', company:'', contact:'', salaryRange:'', workplace:'', requirements:'' }); dlg.value = true }
const openEditDialog = (row: any) => { isEdit.value = true; editId.value = row.jobId; Object.assign(fm, { title:row.title, company:row.company, contact:row.contact, salaryRange:row.salaryRange||'', workplace:row.workplace||'', requirements:row.requirements||'' }); dlg.value = true }

const submitForm = async () => {
  const ok = await fref.value?.validate().catch(() => false); if (!ok) return; sub.value = true
  try {
    if (isEdit.value && editId.value) { await updateJob(editId.value, { ...fm }); ElMessage.success('更新成功') }
    else { await createJob({ ...fm }); ElMessage.success('创建成功') }
    dlg.value = false; fetchPositions()
  } catch { ElMessage.error('操作失败') } finally { sub.value = false }
}

const handleDelete = async (row: any) => {
  try { await deleteJob(row.jobId); ElMessage.success('删除成功'); fetchPositions() } catch { ElMessage.error('删除失败') }
}

onMounted(() => { fetchPositions(); fetchApplications() })
</script>

<style scoped>
.job-manage { padding: 20px; }
.toolbar { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
