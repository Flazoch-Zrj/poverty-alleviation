<template>
  <div class="poor-need">
    <!-- 顶部操作栏 -->
    <div class="toolbar">
      <span class="page-title">我的需求</span>
      <el-button type="primary" @click="openPublishDialog">
        <el-icon><Plus /></el-icon>发布需求
      </el-button>
    </div>

    <!-- 需求列表 -->
    <el-card shadow="never">
      <el-table :data="needList" border stripe size="small" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="需求标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="needType" label="需求类型" width="100">
          <template #default="{ row }">
            <el-tag :type="needTypeTag(row.needType)" size="small">
              {{ row.needType === '1' ? '资金' : row.needType === '2' ? '物资' : '技术' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetAmount" label="目标金额/数量" width="130" align="right" />
        <el-table-column prop="actualAmount" label="已获金额/数量" width="130" align="right" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除此需求？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 发布/编辑 对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑需求' : '发布需求'"
      width="560px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        size="small"
      >
        <el-form-item label="需求标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入需求标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="需求类型" prop="needType">
          <el-select v-model="form.needType" placeholder="请选择需求类型" style="width: 100%">
            <el-option label="资金" value="1" />
            <el-option label="物资" value="2" />
            <el-option label="技术" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标金额/数量" prop="targetAmount">
          <el-input-number
            v-model="form.targetAmount"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入目标金额或数量"
          />
        </el-form-item>
        <el-form-item label="需求描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的需求"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" @click="handleSubmit" size="small" :loading="submitting">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getNeedPage, createNeed, updateNeed, deleteNeed } from '@/api/system'
import { ElMessage } from 'element-plus'

interface NeedItem {
  id: number
  title: string
  needType: string
  targetAmount: number
  actualAmount: number
  status: string
  createTime: string
  description: string
}

const loading = ref(false)
const needList = ref<NeedItem[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<any>(null)

const form = reactive({
  id: 0,
  title: '',
  needType: '',
  targetAmount: 0,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入需求标题', trigger: 'blur' }],
  needType: [{ required: true, message: '请选择需求类型', trigger: 'change' }],
  targetAmount: [{ required: true, message: '请输入目标金额/数量', trigger: 'blur' }]
}

const needTypeTag = (type: string) => {
  const map: Record<string, string> = { '1': 'danger', '2': 'warning', '3': 'primary' }
  return map[type] || ''
}

const statusTag = (status: string) => {
  const map: Record<string, string> = { '0': 'warning', '1': 'primary', '2': 'success' }
  return map[status] || ''
}

const statusLabel = (status: string) => {
  const map: Record<string, string> = { '0': '待解决', '1': '已对接', '2': '已完成' }
  return map[status] || status
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getNeedPage(queryParams)
    if (res.data) {
      needList.value = res.data.records || res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('获取需求列表失败', e)
  } finally {
    loading.value = false
  }
}

const openPublishDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const openEditDialog = (row: NeedItem) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.needType = row.needType
  form.targetAmount = row.targetAmount
  form.description = row.description
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = 0
  form.title = ''
  form.needType = ''
  form.targetAmount = 0
  form.description = ''
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateNeed(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createNeed(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error('提交失败', e)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteNeed(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    console.error('删除失败', e)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.poor-need {
  padding: 20px;

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18px;

    .page-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 18px;
  }

  :deep(.el-card) {
    border-radius: 8px;
  }
}
</style>
