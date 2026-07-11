<template>
  <div class="visit-record">
    <!-- Search & Actions -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索家庭名称"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
      />
      <el-date-picker
        v-model="searchForm.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 280px; margin-left: 12px"
        @change="handleSearch"
      />
      <el-button type="primary" style="margin-left: 12px" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="openCreateDialog">
        <el-icon><Plus /></el-icon> 新增走访
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="cadreUserId" label="走访干部" width="120">
        <template #default="{ row }">
          <span>{{ row.cadreUserName || row.cadreUser?.realName || '干部ID:' + row.cadreUserId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="familyId" label="走访家庭" min-width="160">
        <template #default="{ row }">
          <span>{{ row.familyName || '家庭ID:' + row.familyId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="visitDate" label="走访日期" width="120" align="center">
        <template #default="{ row }">
          {{ row.visitDate ? $dayjs(row.visitDate).format('YYYY-MM-DD') : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="走访内容" min-width="220">
        <template #default="{ row }">
          <el-tooltip :content="row.content || ''" placement="top" :show-after="300">
            <span class="text-ellipsis">{{ row.content || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="记录时间" width="170">
        <template #default="{ row }">
          {{ row.createTime ? $dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openDetail(row)">
            详情
          </el-button>
          <el-popconfirm title="确定删除该走访记录吗？" @confirm="handleDelete(row)">
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

    <!-- Create / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      title="新增走访记录"
      width="650px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="110px"
        label-position="right"
      >
        <el-form-item label="走访家庭" prop="familyId">
          <el-select
            v-model="formData.familyId"
            placeholder="请选择走访家庭"
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

        <el-form-item label="走访日期" prop="visitDate">
          <el-date-picker
            v-model="formData.visitDate"
            type="date"
            placeholder="选择走访日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="走访内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入走访内容"
          />
        </el-form-item>

        <el-form-item label="走访照片" prop="photos">
          <el-upload
            v-model:file-list="formData.photos"
            list-type="picture-card"
            :auto-upload="false"
            multiple
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="后续计划" prop="nextPlan">
          <el-input
            v-model="formData.nextPlan"
            type="textarea"
            :rows="3"
            placeholder="请输入后续帮扶计划（选填）"
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

    <!-- Detail Dialog -->
    <el-dialog
      v-model="detailDialogVisible"
      title="走访详情"
      width="600px"
    >
      <template v-if="detailData">
        <div class="detail-item">
          <span class="detail-label">走访干部：</span>
          <span>{{ detailData.cadreUserName || detailData.cadreUser?.realName || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">走访家庭：</span>
          <span>{{ detailData.familyName || detailData.family?.householderName || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">走访日期：</span>
          <span>{{ detailData.visitDate ? $dayjs(detailData.visitDate).format('YYYY-MM-DD') : '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">走访内容：</span>
          <div class="detail-value">{{ detailData.content || '-' }}</div>
        </div>
        <div class="detail-item" v-if="detailData.photos && detailData.photos.length">
          <span class="detail-label">走访照片：</span>
          <div class="photo-list">
            <el-image
              v-for="(photo, idx) in detailData.photos"
              :key="idx"
              :src="photo.url || photo"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px; margin-right: 8px"
              :preview-src-list="[photo.url || photo]"
            />
          </div>
        </div>
        <div class="detail-item" v-if="detailData.nextPlan">
          <span class="detail-label">后续计划：</span>
          <div class="detail-value">{{ detailData.nextPlan }}</div>
        </div>
        <div class="detail-item">
          <span class="detail-label">记录时间：</span>
          <span>{{ detailData.createTime ? $dayjs(detailData.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}</span>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getFamilyPage, getVisitPage, createVisit, deleteVisit } from '@/api/system'

// ---------- Interfaces ----------
interface QueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  startDate?: string
  endDate?: string
}

interface FormData {
  familyId: number | undefined
  cadreUserId: number | undefined
  visitDate: string
  content: string
  photos: any[]
  nextPlan: string
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
  dateRange: [] as string[]
})

// Dialog
const dialogVisible = ref(false)
const formRef = ref<any>(null)
const formData = reactive<FormData>({
  familyId: undefined,
  cadreUserId: undefined,
  visitDate: '',
  content: '',
  photos: [],
  nextPlan: ''
})

// Families for select
const familyLoading = ref(false)
const familyOptions = ref<any[]>([])

// Detail
const detailDialogVisible = ref(false)
const detailData = ref<any>(null)

// ---------- Form Rules ----------
const formRules = {
  familyId: [{ required: true, message: '请选择走访家庭', trigger: 'change' }],
  visitDate: [{ required: true, message: '请选择走访日期', trigger: 'change' }],
  content: [{ required: true, message: '请输入走访内容', trigger: 'blur' }]
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
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const res = await getVisitPage(params)
    tableData.value = res.data?.rows ?? res.data?.records ?? res.data ?? []
    total.value = res.data?.total ?? 0
  } catch (e: any) {
    ElMessage.error(e?.message || '获取走访记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const resetForm = () => {
  formData.familyId = undefined
  formData.cadreUserId = undefined
  formData.visitDate = ''
  formData.content = ''
  formData.photos = []
  formData.nextPlan = ''
  formRef.value?.clearValidate()
}

const openCreateDialog = () => {
  resetForm()
  // Set current user id from localStorage / store
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      const parsed = JSON.parse(userInfo)
      formData.cadreUserId = parsed.id || parsed.userId
    } catch {
      // ignore
    }
  }
  fetchFamilies()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    // photos 后端是 VARCHAR(2000) JSON 数组字符串，需将 el-upload 的数组序列化
    const requestData = { ...formData }
    if (Array.isArray(requestData.photos)) {
      const urls = requestData.photos
        .map((p: any) => p.url || (p.response && p.response.url) || '')
        .filter(Boolean)
      requestData.photos = urls.length > 0 ? JSON.stringify(urls) : ''
    }
    await createVisit(requestData)
    ElMessage.success('创建成功')
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
    await deleteVisit(row.recordId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

const openDetail = (row: any) => {
  detailData.value = row
  detailDialogVisible.value = true
}

// ---------- Lifecycle ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.visit-record {
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

  .text-ellipsis {
    display: inline-block;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .detail-item {
    display: flex;
    margin-bottom: 14px;
    line-height: 1.6;

    .detail-label {
      min-width: 100px;
      font-weight: 500;
      color: #606266;
      flex-shrink: 0;
    }

    .detail-value {
      flex: 1;
      color: #303133;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }

  .photo-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
