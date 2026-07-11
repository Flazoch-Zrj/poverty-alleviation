<template>
  <div class="family-manage">
    <!-- Search & Actions -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索户主姓名 / 身份证号"
        clearable
        style="width: 260px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="家庭状态"
        clearable
        style="width: 150px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="正常" :value="0" />
        <el-option label="已脱贫" :value="1" />
        <el-option label="监测户" :value="2" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="openCreateDialog">
        <el-icon><Plus /></el-icon> 新增家庭
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="familyCode" label="档案编号" min-width="140" />
      <el-table-column prop="householderName" label="户主姓名" width="110" />
      <el-table-column prop="idCard" label="身份证号" min-width="170" />
      <el-table-column prop="familySize" label="家庭人口" width="90" align="center" />
      <el-table-column prop="annualIncome" label="年收入(元)" width="120" align="right">
        <template #default="{ row }">
          {{ row.annualIncome?.toLocaleString() ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="povertyLevel" label="贫困级别" width="100" align="center">
        <template #default="{ row }">
          <el-tag
            :type="povertyLevelTag(row.povertyLevel)"
            size="small"
          >
            {{ dictLabel(row.povertyLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : row.status === 1 ? 'warning' : 'info'" size="small">
            {{ row.status === 0 ? '正常' : row.status === 1 ? '已脱贫' : '监测户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="建档时间" width="170">
        <template #default="{ row }">
          {{ row.createTime ? $dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-button type="success" link size="small" @click="openViewMembers(row)">
            成员
          </el-button>
          <el-popconfirm title="确定删除该家庭吗？" @confirm="handleDelete(row)">
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

    <!-- Family Form Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑贫困家庭' : '新增贫困家庭'"
      width="720px"
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="户主姓名" prop="householderName">
              <el-input v-model="formData.householderName" placeholder="请输入户主姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="城市" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="区/县" prop="district">
              <el-input v-model="formData.district" placeholder="区/县" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="乡镇" prop="town">
              <el-input v-model="formData.town" placeholder="乡镇" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="村/社区" prop="village">
              <el-input v-model="formData.village" placeholder="村/社区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家庭人口" prop="familySize">
              <el-input-number v-model="formData.familySize" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" type="textarea" :rows="2" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年收入(元)" prop="annualIncome">
              <el-input-number
                v-model="formData.annualIncome"
                :min="0"
                :step="1000"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="贫困原因编码" prop="povertyCauseCode">
              <el-input v-model="formData.povertyCauseCode" placeholder="如：01-因病" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="贫困级别" prop="povertyLevel">
              <el-select v-model="formData.povertyLevel" placeholder="请选择" style="width: 100%">
                <el-option label="一般贫困户" :value="0" />
                <el-option label="低保贫困户" :value="1" />
                <el-option label="特困供养" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
                <el-option label="正常" :value="0" />
                <el-option label="已脱贫" :value="1" />
                <el-option label="监测户" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="建档日期" prop="filingDate">
          <el-date-picker
            v-model="formData.filingDate"
            type="date"
            placeholder="选择建档日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
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

    <!-- View Members Dialog -->
    <el-dialog
      v-model="memberDialogVisible"
      title="家庭成员管理"
      width="760px"
      :close-on-click-modal="false"
    >
      <div style="margin-bottom: 16px; text-align: right">
        <el-button type="primary" size="small" @click="openAddMember">
          <el-icon><Plus /></el-icon> 添加成员
        </el-button>
      </div>

      <el-table :data="memberList" border stripe v-loading="memberLoading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="relationship" label="与户主关系" width="110">
          <template #default="{ row }">
            {{ row.relationship || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证号" min-width="170" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="gender" label="性别" width="60" align="center">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="60" align="center" />
        <el-table-column prop="healthStatus" label="健康状况" width="100">
          <template #default="{ row }">
            {{ row.healthStatus || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该成员吗？" @confirm="handleDeleteMember(row)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="memberDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Add Member Dialog -->
    <el-dialog
      v-model="addMemberDialogVisible"
      title="添加家庭成员"
      width="520px"
      :close-on-click-modal="false"
      @close="resetMemberForm"
    >
      <el-form
        ref="memberFormRef"
        :model="memberForm"
        :rules="memberRules"
        label-width="110px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="memberForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="与户主关系" prop="relationship">
              <el-input v-model="memberForm.relationship" placeholder="如：配偶/子女" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="memberForm.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="memberForm.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="memberForm.gender" placeholder="请选择" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="memberForm.age" :min="0" :max="150" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="memberForm.healthStatus" placeholder="如：健康/残疾" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="addMemberDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="memberSubmitLoading" @click="handleAddMember">
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
import {
  getFamilyPage,
  createFamily,
  updateFamily,
  deleteFamily,
  getFamilyMembers,
  createFamilyMember,
  deleteFamilyMember
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
  householderName: string
  idCard: string
  province: string
  city: string
  district: string
  town: string
  village: string
  address: string
  familySize: number
  annualIncome: number
  povertyCauseCode: string
  povertyLevel: number
  status: number
  filingDate: string
}

interface MemberForm {
  familyId?: number
  name: string
  relationship: string
  idCard: string
  phone: string
  gender: number
  age: number
  healthStatus: string
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
  householderName: '',
  idCard: '',
  province: '',
  city: '',
  district: '',
  town: '',
  village: '',
  address: '',
  familySize: 1,
  annualIncome: 0,
  povertyCauseCode: '',
  povertyLevel: 0,
  status: 0,
  filingDate: ''
})

// Members
const memberDialogVisible = ref(false)
const memberLoading = ref(false)
const memberList = ref<any[]>([])
const currentFamilyId = ref<number | undefined>(undefined)

const addMemberDialogVisible = ref(false)
const memberSubmitLoading = ref(false)
const memberFormRef = ref<any>(null)
const memberForm = reactive<MemberForm>({
  name: '',
  relationship: '',
  idCard: '',
  phone: '',
  gender: 1,
  age: 0,
  healthStatus: ''
})

// ---------- Form Rules ----------
const formRules = {
  householderName: [{ required: true, message: '请输入户主姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  familySize: [{ required: true, message: '请设置家庭人口', trigger: 'change' }],
  povertyLevel: [{ required: true, message: '请选择贫困级别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const memberRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  relationship: [{ required: true, message: '请输入与户主关系', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
}

// ---------- Methods ----------
const povertyLevelTag = (level: number) => {
  const map: Record<number, string> = { 0: 'warning', 1: 'danger', 2: 'info' }
  return map[level] ?? ''
}

const dictLabel = (level: number) => {
  const map: Record<number, string> = { 0: '一般贫困户', 1: '低保贫困户', 2: '特困供养' }
  return map[level] ?? '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: queryParams.pageNum,
      size: queryParams.pageSize
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.status !== undefined) params.status = String(searchForm.status)

    const res = await getFamilyPage(params)
    if (res.code === 200) {
      tableData.value = res.data?.records ?? res.data ?? []
      total.value = res.data?.total ?? 0
    } else {
      ElMessage.error(res.message || '获取失败')
    }
  } catch (e: any) {
    console.error('获取家庭列表失败:', e)
    ElMessage.error('获取数据失败: ' + (e.message || '请求超时'))
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
  formData.householderName = ''
  formData.idCard = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.town = ''
  formData.village = ''
  formData.address = ''
  formData.familySize = 1
  formData.annualIncome = 0
  formData.povertyCauseCode = ''
  formData.povertyLevel = 0
  formData.status = 0
  formData.filingDate = ''
  formRef.value?.clearValidate()
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  formData.id = row.familyId
  formData.householderName = row.householderName ?? ''
  formData.idCard = row.idCard ?? ''
  formData.province = row.province ?? ''
  formData.city = row.city ?? ''
  formData.district = row.district ?? ''
  formData.town = row.town ?? ''
  formData.village = row.village ?? ''
  formData.address = row.address ?? ''
  formData.familySize = row.familySize ?? 1
  formData.annualIncome = row.annualIncome ?? 0
  formData.povertyCauseCode = row.povertyCauseCode ?? ''
  formData.povertyLevel = row.povertyLevel ?? 0
  formData.status = row.status ?? 0
  formData.filingDate = row.filingDate ?? ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateFamily(formData)
      ElMessage.success('更新成功')
    } else {
      await createFamily(formData)
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
    await deleteFamily(row.familyId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

// ---------- Members ----------
const openViewMembers = async (row: any) => {
  currentFamilyId.value = row.familyId
  memberDialogVisible.value = true
  await fetchMembers(row.familyId)
}

const fetchMembers = async (familyId: number) => {
  memberLoading.value = true
  try {
    const res = await getFamilyMembers(familyId)
    memberList.value = res.data ?? []
  } catch (e: any) {
    ElMessage.error(e?.message || '获取成员列表失败')
  } finally {
    memberLoading.value = false
  }
}

const resetMemberForm = () => {
  memberForm.name = ''
  memberForm.relationship = ''
  memberForm.idCard = ''
  memberForm.phone = ''
  memberForm.gender = 1
  memberForm.age = 0
  memberForm.healthStatus = ''
  memberFormRef.value?.clearValidate()
}

const openAddMember = () => {
  memberForm.familyId = currentFamilyId.value
  resetMemberForm()
  addMemberDialogVisible.value = true
}

const handleAddMember = async () => {
  const valid = await memberFormRef.value?.validate().catch(() => false)
  if (!valid) return

  memberSubmitLoading.value = true
  try {
    await createFamilyMember(currentFamilyId.value, memberForm)
    ElMessage.success('添加成功')
    addMemberDialogVisible.value = false
    if (currentFamilyId.value) {
      await fetchMembers(currentFamilyId.value)
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '添加失败')
  } finally {
    memberSubmitLoading.value = false
  }
}

const handleDeleteMember = async (row: any) => {
  try {
    await deleteFamilyMember(row.memberId)
    ElMessage.success('删除成功')
    if (currentFamilyId.value) {
      await fetchMembers(currentFamilyId.value)
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

// ---------- Lifecycle ----------
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.family-manage {
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
}
</style>
