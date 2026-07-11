<template>
  <div class="role-manage-container">
    <!-- Top Actions -->
    <div class="search-card">
      <div class="search-card-header">
        <div class="search-card-title">
          <el-icon :size="18" color="var(--p-color-primary)"><Setting /></el-icon>
          <span>角色管理</span>
        </div>
        <div class="search-actions">
          <el-button class="btn-glow-orange" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <el-card shadow="hover" class="table-card">
      <el-table :data="roleList" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="roleName" label="角色名称" min-width="160" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="240" show-overflow-tooltip />
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

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
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
import { Plus, Setting, Edit, Delete } from '@element-plus/icons-vue'
import { getRolePage, createRole, updateRole, deleteRole } from '@/api/system'

// ---------- Query & Data ----------
interface RoleQuery {
  pageNum: number
  pageSize: number
}

const queryParams = reactive<RoleQuery>({
  pageNum: 1,
  pageSize: 10,
})

const roleList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRolePage({
      page: queryParams.pageNum,
      size: queryParams.pageSize,
    })
    const data = res.data || res
    roleList.value = data.records || data.list || []
    total.value = data.total || 0
  } catch {
    roleList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// ---------- Dialog ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

interface RoleForm {
  id?: number
  roleName: string
  roleCode: string
  description: string
}

const defaultForm: RoleForm = {
  roleName: '',
  roleCode: '',
  description: '',
}

const formData = reactive<RoleForm>({ ...defaultForm })

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { ...defaultForm })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description || '',
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const { id, roleName, roleCode, description } = formData
      await updateRole({ id, roleName, roleCode, description })
      ElMessage.success('更新成功')
    } else {
      await createRole({ ...formData })
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
  ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await deleteRole(row.id)
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
.role-manage-container {
  padding: 20px;

  .action-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 14px 20px;
    }
  }

  .table-card {
    :deep(.el-card__body) {
      padding: 12px 20px 20px;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
