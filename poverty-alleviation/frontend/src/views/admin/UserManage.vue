<template>
  <div class="user-manage-container">
    <!-- Search & Actions Bar - 卡片式 -->
    <div class="search-card">
      <div class="search-card-header">
        <div class="search-card-title">
          <el-icon :size="18" color="var(--p-color-primary)"><Search /></el-icon>
          <span>用户筛选</span>
          <el-button text size="small" @click="showFilter = !showFilter" style="margin-left:4px;">
            <el-icon><ArrowDown v-if="showFilter" /><ArrowRight v-else /></el-icon>
            {{ showFilter ? '收起筛选' : '展开筛选' }}
          </el-button>
        </div>
        <div class="search-actions">
          <el-button class="btn-breathe" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </div>

      <el-form :inline="true" :model="queryParams" @keyup.enter="handleSearch" v-show="showFilter">
        <el-form-item label="关键词" style="margin-bottom:0;">
          <el-input
            v-model="queryParams.keyword"
            placeholder="用户名 / 姓名 / 手机号"
            clearable
            style="width: 260px"
            size="large"
            prefix-icon="Search"
          />
        </el-form-item>
        <el-form-item style="margin-bottom:0; margin-left:8px;">
          <el-button type="primary" size="large" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button class="btn-ghost" size="large" @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <el-card shadow="hover" class="table-card">
      <el-table :data="userList" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleCode" label="角色" width="110">
          <template #default="{ row }">
            <el-tag
              :type="roleTagType(row.roleCode)"
              size="small"
              effect="dark"
            >
              {{ roleLabel(row.roleCode) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'danger'"
              size="small"
              class="is-status"
              effect="plain"
            >
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right" header-align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" class="btn-action" link @click="handleEdit(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" class="btn-delete" link @click="handleDelete(row)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
              <el-button size="small" class="btn-action" link @click="handleResetPwd(row)">
                <el-icon><Key /></el-icon> 重置
              </el-button>
              <el-button size="small" class="btn-action" link @click="handleToggleStatus(row)">
                <el-icon><Switch /></el-icon>
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
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
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="formData.roleCode" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="帮扶干部" value="cadre" />
            <el-option label="贫困用户" value="poor" />
            <el-option label="志愿者" value="volunteer" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { Search, Plus, ArrowDown, ArrowRight, Edit, Delete, Key, Switch, Refresh } from '@element-plus/icons-vue'
import {
  getUserPage,
  createUser,
  updateUser,
  deleteUser,
  toggleUserStatus,
  resetPassword,
} from '@/api/system'

// ---------- Role Helpers ----------
const roleTagType = (code: string): string => {
  const map: Record<string, string> = {
    admin: 'danger',
    cadre: 'warning',
    poor: 'success',
    volunteer: 'info',
  }
  return map[code] || 'info'
}

const roleLabel = (code: string): string => {
  const map: Record<string, string> = {
    admin: '管理员',
    cadre: '帮扶干部',
    poor: '贫困用户',
    volunteer: '志愿者',
  }
  return map[code] || code
}

// ---------- Query & Data ----------
interface UserQuery {
  keyword: string
  pageNum: number
  pageSize: number
}

const queryParams = reactive<UserQuery>({
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

const userList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const showFilter = ref(true)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserPage({
      keyword: queryParams.keyword,
      page: queryParams.pageNum,
      size: queryParams.pageSize,
    })
    const data = res.data || res
    userList.value = data.records || data.list || []
    total.value = data.total || 0
  } catch {
    userList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.pageNum = 1
  fetchData()
}

// ---------- Dialog ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

interface UserForm {
  userId?: number
  username: string
  password: string
  realName: string
  idCard: string
  phone: string
  roleCode: string
  status: number
}

const defaultForm: UserForm = {
  username: '',
  password: '',
  realName: '',
  idCard: '',
  phone: '',
  roleCode: '',
  status: 1,
}

const formData = reactive<UserForm>({ ...defaultForm })

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { ...defaultForm, password: '' })
  delete formData.userId     // 清除编辑时残留的 userId，避免新增时误传
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(formData, {
    userId: row.userId,
    username: row.username,
    password: '',
    realName: row.realName,
    idCard: row.idCard || '',
    phone: row.phone,
    roleCode: row.roleCode,
    status: row.status,
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const { userId, username, realName, idCard, phone, roleCode, status } = formData
      await updateUser(userId, { username, realName, idCard, phone, roleCode, status })
      ElMessage.success('更新成功')
    } else {
      await createUser({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

// ---------- Actions ----------
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteUser(row.userId)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleResetPwd = (row: any) => {
  ElMessageBox.confirm(`确定重置用户「${row.username}」的密码吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await resetPassword(row.userId)
    ElMessage.success('密码已重置')
  }).catch(() => {})
}

const handleToggleStatus = (row: any) => {
  const label = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定${label}用户「${row.username}」吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await toggleUserStatus(row.userId)
    ElMessage.success(`${label}成功`)
    fetchData()
  }).catch(() => {})
}

// ---------- Init ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.user-manage-container {
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

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
