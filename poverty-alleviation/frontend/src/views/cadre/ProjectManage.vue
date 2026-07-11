<template>
  <div class="project-manage">
    <!-- Search & Actions -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索项目名称 / 家庭信息"
        clearable
        style="width: 260px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="项目状态"
        clearable
        style="width: 150px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已驳回" :value="2" />
        <el-option label="进行中" :value="3" />
        <el-option label="已完成" :value="4" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="openCreateDialog">
        <el-icon><Plus /></el-icon> 新增项目
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="projectName" label="项目名称" min-width="180">
        <template #default="{ row }">
          <span class="project-name">{{ row.projectName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="家庭信息" min-width="160">
        <template #default="{ row }">
          <div>
            <span>{{ row.family?.householderName || row.familyName || '-' }}</span>
            <el-tag size="small" type="info" style="margin-left: 6px">
              {{ row.family?.familyCode || '-' }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="budget" label="预算(元)" width="130" align="right">
        <template #default="{ row }">
          {{ row.budget?.toLocaleString() ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="raisedAmount" label="已筹集(元)" width="130" align="right">
        <template #default="{ row }">
          {{ row.raisedAmount?.toLocaleString() ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="{ row }">
          <div class="status-dot-tag">
            <span class="status-dot" :style="{ background: statusColor(row.status) }"></span>
            <span :style="{ color: statusColor(row.status) }">{{ statusLabel(row.status) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170">
        <template #default="{ row }">
          {{ row.createTime ? $dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-button
            type="warning"
            link
            size="small"
            v-if="row.status === 0"
            @click="openAuditDialog(row)"
          >
            审核
          </el-button>
          <el-button type="info" link size="small" @click="openViewLogs(row)">
            日志
          </el-button>
          <el-popconfirm title="确定删除该项目吗？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button type="danger" link size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- Form Dialog (Create / Edit) -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑项目' : '新增项目'"
      width="620px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        label-position="right"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="formData.projectName" placeholder="请输入项目名称" />
        </el-form-item>

        <el-form-item label="帮扶家庭" prop="familyId">
          <el-select
            v-model="formData.familyId"
            placeholder="请选择帮扶家庭"
            filterable
            clearable
            :loading="familyLoading"
            style="width: 100%"
          >
            <el-option
              v-for="item in familyOptions"
              :key="item.familyId"
              :label="item.householderName + (item.familyCode ? ` (${item.familyCode})` : '')"
              :value="item.familyId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预算(元)" prop="budget">
          <el-input-number
            v-model="formData.budget"
            :min="0"
            :step="1000"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- Audit Dialog -->
    <el-dialog
      v-model="auditDialogVisible"
      title="项目审核"
      width="500px"
      :close-on-click-modal="false"
      @close="resetAuditForm"
    >
      <el-form
        ref="auditFormRef"
        :model="auditForm"
        :rules="auditRules"
        label-width="100px"
      >
        <el-form-item label="审核结果" prop="action">
          <el-select v-model="auditForm.action" placeholder="请选择审核结果" style="width: 100%">
            <el-option label="通过" :value="1" />
            <el-option label="驳回" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="handleAudit">
          确定提交
        </el-button>
      </template>
    </el-dialog>

    <!-- Audit Logs Dialog -->
    <el-dialog
      v-model="logDialogVisible"
      title="审核日志"
      width="600px"
    >
      <el-timeline v-if="logList.length > 0">
        <el-timeline-item
          v-for="(log, idx) in logList"
          :key="idx"
          :timestamp="log.createTime ? $dayjs(log.createTime).format('YYYY-MM-DD HH:mm:ss') : ''"
          :color="log.action === 1 ? '#67c23a' : log.action === 2 ? '#f56c6c' : '#909399'"
        >
          <div class="log-item">
            <div>
              <span class="log-action-tag">
                <el-tag
                  :type="log.action === 1 ? 'success' : log.action === 2 ? 'danger' : 'info'"
                  size="small"
                >
                  {{ log.action === 1 ? '通过' : log.action === 2 ? '驳回' : '操作' }}
                </el-tag>
              </span>
              <span style="margin-left: 8px; font-weight: 500">
                {{ log.operatorName || log.operator?.realName || '-' }}
              </span>
            </div>
            <div v-if="log.comment" style="margin-top: 6px; color: #606266; font-size: 13px">
              {{ log.comment }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审核日志" />

      <template #footer>
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import {
  getProjectPage,
  createProject,
  updateProject,
  deleteProject,
  auditProject,
  getProjectAuditLogs,
  getFamilyPage
} from '@/api/system'

// ---------- Interfaces ----------
interface QueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: number
}

interface FormData {
  id?: number
  projectName: string
  familyId: number | undefined
  budget: number
  description: string
}

interface AuditForm {
  projectId?: number
  action: number | undefined
  comment: string
}

// ---------- State ----------
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const queryParams = reactive<QueryParams>({
  pageNum: 1,
  pageSize: 10
})

const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined
})

// Form Dialog
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<any>(null)
const formData = reactive<FormData>({
  projectName: '',
  familyId: undefined,
  budget: 0,
  description: ''
})

// Families
const familyLoading = ref(false)
const familyOptions = ref<any[]>([])

// Audit
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<any>(null)
const auditForm = reactive<AuditForm>({
  projectId: undefined,
  action: undefined,
  comment: ''
})

// Logs
const logDialogVisible = ref(false)
const logList = ref<any[]>([])

// ---------- Helpers ----------
const statusColor = (status: number) => {
  const map: Record<number, string> = { 0: '#F5A623', 1: '#1E8E5E', 2: '#E74C3C', 3: '#409EFF', 4: '#909399' }
  return map[status] ?? '#909399'
}

const statusLabel = (status: number) => {
  const map: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已驳回', 3: '进行中', 4: '已完成' }
  return map[status] ?? '未知'
}

// ---------- Methods ----------
const fetchFamilies = async (keyword?: string) => {
  familyLoading.value = true
  try {
    const res = await getFamilyPage({ page: 1, size: 200, keyword })
    familyOptions.value = res.data?.rows ?? res.data?.records ?? res.data ?? []
  } catch {
    // silent
  } finally {
    familyLoading.value = false
  }
}

const searchFamilies = (query: string) => {
  fetchFamilies(query)
}

const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: queryParams.pageNum,
      size: queryParams.pageSize
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.status !== undefined) params.status = searchForm.status

    const res = await getProjectPage(params)
    tableData.value = res.data?.rows ?? res.data?.records ?? res.data ?? []
    total.value = res.data?.total ?? 0
  } catch (e: any) {
    ElMessage.error(e?.message || '获取项目列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const resetForm = () => {
  formData.id = undefined
  formData.projectName = ''
  formData.familyId = undefined
  formData.budget = 0
  formData.description = ''
  formRef.value?.clearValidate()
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  fetchFamilies()
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  formData.id = row.projectId
  formData.projectName = row.projectName ?? ''
  formData.familyId = row.familyId ?? row.family?.familyId
  formData.budget = row.budget ?? 0
  formData.description = row.description ?? ''
  fetchFamilies()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateProject(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createProject(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await deleteProject(row.projectId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

// Audit
const resetAuditForm = () => {
  auditForm.projectId = undefined
  auditForm.action = undefined
  auditForm.comment = ''
  auditFormRef.value?.clearValidate()
}

const openAuditDialog = (row: any) => {
  auditForm.projectId = row.projectId
  auditForm.action = undefined
  auditForm.comment = ''
  auditDialogVisible.value = true
}

const handleAudit = async () => {
  const valid = await auditFormRef.value?.validate().catch(() => false)
  if (!valid) return

  auditLoading.value = true
  try {
    await auditProject(auditForm.projectId, auditForm)
    ElMessage.success('审核完成')
    auditDialogVisible.value = false
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '审核失败')
  } finally {
    auditLoading.value = false
  }
}

// Audit Logs
const openViewLogs = async (row: any) => {
  logDialogVisible.value = true
  logList.value = []
  try {
    const res = await getProjectAuditLogs(row.projectId)
    logList.value = res.data ?? []
  } catch (e: any) {
    ElMessage.error(e?.message || '获取审核日志失败')
  }
}

// Form Rules
const formRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  familyId: [{ required: true, message: '请选择帮扶家庭', trigger: 'change' }],
  budget: [{ required: true, message: '请输入预算金额', trigger: 'change' }]
}

const auditRules = {
  action: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
}

// ---------- Lifecycle ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.project-manage {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 8px;
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    padding: 12px 0;
  }

  .project-name {
    font-weight: 500;
    color: #303133;
  }

  .log-item {
    padding: 4px 0;
  }

  .log-action-tag {
    display: inline-block;
  }
}
</style>
