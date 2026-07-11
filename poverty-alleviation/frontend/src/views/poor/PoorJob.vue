<template>
  <div class="poor-job">
    <!-- 就业岗位浏览 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">就业岗位</span>
          <div class="search-bar">
            <el-input
              v-model="keyword"
              placeholder="搜索岗位名称/公司"
              clearable
              size="small"
              style="width: 260px"
              @keyup.enter="searchJobs"
            >
              <template #append>
                <el-button @click="searchJobs" :icon="Search">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="jobList" border stripe size="small" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="岗位名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="company" label="公司名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="salaryRange" label="薪资范围" width="120" align="center" />
        <el-table-column prop="workplace" label="工作地点" width="130" show-overflow-tooltip />
        <el-table-column prop="contact" label="联系方式" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="openApplyDialog(row)">申请</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="jobTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchJobs"
          @current-change="fetchJobs"
        />
      </div>
    </el-card>

    <!-- 我的申请 -->
    <el-card shadow="never" class="apply-card">
      <template #header>
        <span class="title">我的申请记录</span>
      </template>

      <el-table :data="applyList" border stripe size="small" style="width: 100%" v-loading="applyLoading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="jobTitle" label="岗位名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="applyStatus" label="申请状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="applyStatusTag(row.applyStatus)" size="small">
              {{ applyStatusLabel(row.applyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="applyQuery.pageNum"
          v-model:page-size="applyQuery.pageSize"
          :total="applyTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchApplications"
          @current-change="fetchApplications"
        />
      </div>
    </el-card>

    <!-- 申请对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请岗位"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="apply-info">
        <p><strong>岗位：</strong>{{ currentJob?.title }}</p>
        <p><strong>公司：</strong>{{ currentJob?.company }}</p>
      </div>
      <el-form ref="formRef" :model="applyForm" label-width="80px" size="small">
        <el-form-item label="备注说明" prop="remark">
          <el-input
            v-model="applyForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（如个人优势、求职意向等）"
            maxlength="300"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" @click="submitApply" size="small" :loading="submitting">确认申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getJobPage, createJobApplication, getJobApplicationPage } from '@/api/system'
import { ElMessage } from 'element-plus'

interface JobItem {
  jobId: number
  title: string
  company: string
  salaryRange: string
  workplace: string
  contact: string
  publishTime: string
}

interface ApplyItem {
  jobTitle: string
  applyStatus: string
  applyTime: string
  remark: string
}

const loading = ref(false)
const jobList = ref<JobItem[]>([])
const jobTotal = ref(0)
const keyword = ref('')
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const applyLoading = ref(false)
const applyList = ref<ApplyItem[]>([])
const applyTotal = ref(0)
const applyQuery = reactive({
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const submitting = ref(false)
const currentJob = ref<JobItem | null>(null)
const formRef = ref<any>(null)
const applyForm = reactive({
  jobId: 0,
  remark: ''
})

const applyStatusTag = (status: string) => {
  const map: Record<string, string> = { '1': 'warning', '2': 'success', '3': 'danger' }
  return map[status] || ''
}

const applyStatusLabel = (status: string) => {
  const map: Record<string, string> = { '1': '申请中', '2': '通过', '3': '拒绝' }
  return map[status] || status
}

const fetchJobs = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (keyword.value) params.keyword = keyword.value
    const res = await getJobPage(params)
    if (res.data) {
      jobList.value = res.data.records || res.data.list || []
      jobTotal.value = res.data.total || 0
    }
  } catch (e) {
    console.error('获取岗位列表失败', e)
  } finally {
    loading.value = false
  }
}

const searchJobs = () => {
  queryParams.pageNum = 1
  fetchJobs()
}

const fetchApplications = async () => {
  applyLoading.value = true
  try {
    const res = await getJobApplicationPage(applyQuery)
    if (res.data) {
      applyList.value = res.data.records || res.data.list || []
      applyTotal.value = res.data.total || 0
    }
  } catch (e) {
    console.error('获取申请记录失败', e)
  } finally {
    applyLoading.value = false
  }
}

const openApplyDialog = (row: JobItem) => {
  currentJob.value = row
  applyForm.jobId = row.jobId
  applyForm.remark = ''
  dialogVisible.value = true
}

const submitApply = async () => {
  submitting.value = true
  try {
    await createJobApplication(applyForm)
    ElMessage.success('申请已提交')
    dialogVisible.value = false
    fetchApplications()
  } catch (e) {
    console.error('申请失败', e)
    ElMessage.error('提交申请失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchJobs()
  fetchApplications()
})
</script>

<style scoped lang="scss">
.poor-job {
  padding: 20px;

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .apply-card {
    margin-top: 20px;
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .apply-info {
    padding: 12px 16px;
    background: #f5f7fa;
    border-radius: 6px;
    margin-bottom: 16px;

    p {
      margin: 4px 0;
      font-size: 14px;
      color: #606266;
    }
  }

  :deep(.el-card) {
    border-radius: 8px;
  }
}
</style>
