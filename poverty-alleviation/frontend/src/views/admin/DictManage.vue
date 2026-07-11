<template>
  <div class="dict-manage-container">
    <!-- Search Bar -->
    <div class="search-card">
      <div class="search-card-header">
        <div class="search-card-title">
          <el-icon :size="18" color="var(--p-color-primary)"><List /></el-icon>
          <span>数据字典</span>
        </div>
        <div class="search-actions">
          <el-button class="btn-glow-orange" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增字典
          </el-button>
        </div>
      </div>
      <el-form :inline="true" @keyup.enter="handleQuery" class="search-fields">
        <el-form-item label="字典类型" style="margin-bottom:0;">
          <el-select v-model="dictType" placeholder="请选择字典类型" style="width: 240px" clearable size="large">
            <el-option label="贫困原因 (poverty_cause)" value="poverty_cause" />
            <el-option label="贫困等级 (poverty_level)" value="poverty_level" />
            <el-option label="帮扶措施 (measure_type)" value="measure_type" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom:0; margin-left:8px;">
          <el-button type="primary" size="large" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <el-card shadow="hover" class="table-card">
      <el-table :data="dictList" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="dictLabel" label="字典标签" min-width="140" />
        <el-table-column prop="dictValue" label="字典值" width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字典' : '新增字典'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="110px"
        label-position="right"
      >
        <el-form-item label="字典类型" prop="dictType">
          <el-select
            v-model="formData.dictType"
            placeholder="请选择字典类型"
            style="width: 100%"
            :disabled="isEdit"
          >
            <el-option label="贫困原因" value="poverty_cause" />
            <el-option label="贫困等级" value="poverty_level" />
            <el-option label="帮扶措施" value="measure_type" />
          </el-select>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="formData.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="formData.dictValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { Plus, List, Search, Edit, Delete } from '@element-plus/icons-vue'
import { getDictByType, createDict, updateDict, deleteDict } from '@/api/system'

// ---------- State ----------
const dictType = ref<string>('poverty_cause')
const dictList = ref<any[]>([])
const loading = ref(false)

const fetchData = async () => {
  if (!dictType.value) {
    dictList.value = []
    return
  }
  loading.value = true
  try {
    const res = await getDictByType(dictType.value)
    const data = res.data || res
    dictList.value = Array.isArray(data) ? data : data.records || data.list || []
  } catch {
    dictList.value = []
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  fetchData()
}

// ---------- Dialog ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

interface DictForm {
  id?: number
  dictType: string
  dictLabel: string
  dictValue: string
  sortOrder: number
  status: number
  remark: string
}

const defaultForm: DictForm = {
  dictType: 'poverty_cause',
  dictLabel: '',
  dictValue: '',
  sortOrder: 0,
  status: 1,
  remark: '',
}

const formData = reactive<DictForm>({ ...defaultForm })

const formRules = {
  dictType: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序号', trigger: 'blur' }],
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { ...defaultForm, dictType: dictType.value || 'poverty_cause' })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    dictType: row.dictType,
    dictLabel: row.dictLabel,
    dictValue: row.dictValue,
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1,
    remark: row.remark || '',
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const { id, dictType, dictLabel, dictValue, sortOrder, status, remark } = formData
      await updateDict({ id, dictType, dictLabel, dictValue, sortOrder, status, remark })
      ElMessage.success('更新成功')
    } else {
      await createDict({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

// ---------- Delete ----------
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除字典「${row.dictLabel}」吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await deleteDict(row.id)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

// ---------- Init ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.dict-manage-container {
  padding: 20px;

  .search-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 18px 20px 0;
    }
  }

  .table-card {
    :deep(.el-card__body) {
      padding: 12px 20px 20px;
    }
  }
}
</style>
