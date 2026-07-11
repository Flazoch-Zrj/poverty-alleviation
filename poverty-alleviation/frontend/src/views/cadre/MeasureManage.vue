<template>
  <div class="measure-manage">
    <!-- Search & Actions -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索家庭信息"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="措施状态"
        clearable
        style="width: 150px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="未启动" :value="0" />
        <el-option label="进行中" :value="1" />
        <el-option label="已完成" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="openCreateDialog">
        <el-icon><Plus /></el-icon> 新增措施
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column label="家庭信息" min-width="180">
        <template #default="{ row }">
          <div>
            <span class="family-label">{{ row.family?.householderName || row.familyName || '-' }}</span>
            <el-tag size="small" type="info" style="margin-left: 6px">
              {{ row.family?.familyCode || '-' }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="measureType" label="措施类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="measureTypeTag(row.measureType)" size="small">
            {{ measureTypeLabel(row.measureType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="措施内容" min-width="200">
        <template #default="{ row }">
          <el-tooltip :content="row.content || ''" placement="top" :show-after="300">
            <span class="text-ellipsis">{{ row.content || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度" width="180" align="center">
        <template #default="{ row }">
          <el-progress
            :percentage="row.progress ?? 0"
            :status="row.progress >= 100 ? 'success' : ''"
            :stroke-width="16"
            :text-inside="true"
            style="width: 140px; margin: 0 auto"
          />
        </template>
      </el-table-column>
      <el-table-column prop="targetAmount" label="目标金额(元)" width="130" align="right">
        <template #default="{ row }">
          {{ row.targetAmount?.toLocaleString() ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="actualAmount" label="实际金额(元)" width="130" align="right">
        <template #default="{ row }">
          {{ row.actualAmount?.toLocaleString() ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="110" align="center">
        <template #default="{ row }">
          <div class="status-dot-tag">
            <span class="status-dot" :style="{ background: statusColor(row.status) }"></span>
            <span :style="{ color: statusColor(row.status) }">{{ statusLabel(row.status) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-popconfirm title="确定删除该措施吗？" @confirm="handleDelete(row)">
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

    <!-- Form Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑帮扶措施' : '新增帮扶措施'"
      width="680px"
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

        <el-form-item label="措施类型" prop="measureType">
          <el-select v-model="formData.measureType" placeholder="请选择措施类型" style="width: 100%">
            <el-option label="产业" value="产业" />
            <el-option label="教育" value="教育" />
            <el-option label="医疗" value="医疗" />
            <el-option label="住房" value="住房" />
            <el-option label="就业" value="就业" />
            <el-option label="金融" value="金融" />
          </el-select>
        </el-form-item>

        <el-form-item label="措施内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="3"
            placeholder="请输入措施内容"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标金额(元)" prop="targetAmount">
              <el-input-number
                v-model="formData.targetAmount"
                :min="0"
                :step="1000"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际金额(元)" prop="actualAmount">
              <el-input-number
                v-model="formData.actualAmount"
                :min="0"
                :step="1000"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="完成进度" prop="progress">
          <div class="slider-wrap">
            <el-slider
              v-model="formData.progress"
              :min="0"
              :max="100"
              show-input
              style="width: 100%"
            />
          </div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="未启动" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getMeasurePage, createMeasure, updateMeasure, deleteMeasure, getFamilyPage } from '@/api/system'

// ---------- Interfaces ----------
interface QueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: number
}

interface FormData {
  id?: number
  familyId: number | undefined
  measureType: string
  content: string
  targetAmount: number
  actualAmount: number
  progress: number
  status: number
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

// Dialog
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<any>(null)
const formData = reactive<FormData>({
  familyId: undefined,
  measureType: '',
  content: '',
  targetAmount: 0,
  actualAmount: 0,
  progress: 0,
  status: 0
})

// Families
const familyLoading = ref(false)
const familyOptions = ref<any[]>([])

// ---------- Helpers ----------
const measureTypeTag = (type: string) => {
  const map: Record<string, string> = {
    '产业': 'success',
    '教育': 'primary',
    '医疗': 'warning',
    '住房': 'danger',
    '就业': 'info',
    '金融': 'success'
  }
  return map[type] ?? ''
}

const measureTypeLabel = (type: string) => type || '-'

const statusColor = (status: number) => {
  const map: Record<number, string> = { 0: '#9CA3AF', 1: '#2563EB', 2: '#10B981', 3: '#EF4444' }
  return map[status] ?? '#9CA3AF'
}

const statusLabel = (status: number) => {
  const map: Record<number, string> = { 0: '未启动', 1: '进行中', 2: '已完成', 3: '已取消' }
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

    const res = await getMeasurePage(params)
    tableData.value = res.data?.rows ?? res.data?.records ?? res.data ?? []
    total.value = res.data?.total ?? 0
  } catch (e: any) {
    ElMessage.error(e?.message || '获取措施列表失败')
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
  formData.familyId = undefined
  formData.measureType = ''
  formData.content = ''
  formData.targetAmount = 0
  formData.actualAmount = 0
  formData.progress = 0
  formData.status = 0
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
  formData.id = row.measureId
  formData.familyId = row.familyId ?? row.family?.familyId
  formData.measureType = row.measureType ?? ''
  formData.content = row.content ?? ''
  formData.targetAmount = row.targetAmount ?? 0
  formData.actualAmount = row.actualAmount ?? 0
  formData.progress = row.progress ?? 0
  formData.status = row.status ?? 0
  fetchFamilies()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateMeasure(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createMeasure(formData)
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
    await deleteMeasure(row.measureId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

// Form Rules
const formRules = {
  familyId: [{ required: true, message: '请选择帮扶家庭', trigger: 'change' }],
  measureType: [{ required: true, message: '请选择措施类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入措施内容', trigger: 'blur' }]
}

// ---------- Lifecycle ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.measure-manage {
  padding: 28px;
  background: var(--p-bg-card);
  border: 1px solid var(--p-border-color);
  border-radius: var(--p-radius-lg);
  box-shadow: var(--p-shadow-sm);
  animation: fadeUp 0.35s cubic-bezier(0.16, 1, 0.3, 1) both;

  .search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 10px;
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
    padding: 8px 0;
  }

  .text-ellipsis {
    display: inline-block;
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .family-label {
    font-weight: 500;
  }

  .slider-wrap {
    display: flex;
    align-items: center;
    width: 100%;
  }
}
</style>
