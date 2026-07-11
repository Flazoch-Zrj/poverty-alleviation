<template>
  <div class="poor-profile">
    <!-- 家庭档案信息 -->
    <el-card shadow="never" class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="title">家庭档案信息</span>
          <div>
            <el-tag v-if="profile.status === '1'" type="success" effect="dark">正常</el-tag>
            <el-tag v-else-if="profile.status === '0'" type="danger" effect="dark">已脱贫</el-tag>
            <el-tag v-else type="info" effect="dark">{{ profile.status }}</el-tag>
            <el-button type="primary" link size="small" style="margin-left:10px" @click="editFamilyDialog = true">编辑</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="家庭编号">{{ profile.familyCode }}</el-descriptions-item>
        <el-descriptions-item label="户主姓名">{{ profile.householderName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ profile.idCard }}</el-descriptions-item>
        <el-descriptions-item label="家庭地址">{{ profile.address }}</el-descriptions-item>
        <el-descriptions-item label="家庭人数">{{ profile.familySize }}</el-descriptions-item>
        <el-descriptions-item label="年收入（元）">{{ profile.annualIncome }}</el-descriptions-item>
        <el-descriptions-item label="贫困等级"><el-tag :type="povertyLevelTag(profile.povertyLevel)" size="small">{{ profile.povertyLevel }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="建档日期">{{ profile.filingDate }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 家庭成员 -->
    <el-card shadow="never" class="member-card">
      <template #header>
        <div class="card-header">
          <span class="title">家庭成员</span>
          <el-button type="primary" size="small" @click="openAddMember">添加成员</el-button>
        </div>
      </template>
      <el-table :data="familyMembers" border stripe size="small" style="width:100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="relationship" label="与户主关系" width="120" />
        <el-table-column prop="idCard" label="身份证号" min-width="170" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">{{ row.gender === '1' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="healthStatus" label="健康状况" width="110" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEditMember(row)">编辑</el-button>
            <el-popconfirm title="确定删除该成员？" @confirm="handleDeleteMember(row)">
              <template #reference><el-button size="small" type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 结对信息 -->
    <el-card shadow="never" class="pair-card">
      <template #header><span class="title">结对帮扶信息</span></template>
      <el-descriptions :column="3" border size="small" v-if="pairInfo">
        <el-descriptions-item label="帮扶责任人">{{ pairInfo.cadreName }}</el-descriptions-item>
        <el-descriptions-item label="结对日期">{{ pairInfo.pairDate }}</el-descriptions-item>
        <el-descriptions-item label="帮扶状态"><el-tag :type="pairInfo.status==='1'?'success':'warning'" size="small">{{ pairInfo.status==='1'?'已结对':'待确认' }}</el-tag></el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无结对帮扶信息" />
    </el-card>

    <!-- 编辑家庭弹窗 -->
    <el-dialog v-model="editFamilyDialog" title="编辑家庭档案" width="560px" :close-on-click-modal="false">
      <el-form :model="familyForm" label-width="100px" size="small">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="户主姓名"><el-input v-model="familyForm.householderName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身份证号"><el-input v-model="familyForm.idCard" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="家庭地址"><el-input v-model="familyForm.address" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="家庭人数"><el-input-number v-model="familyForm.familySize" :min="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年收入"><el-input-number v-model="familyForm.annualIncome" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="editFamilyDialog=false">取消</el-button><el-button type="primary" @click="submitFamilyEdit">保存</el-button></template>
    </el-dialog>

    <!-- 添加/编辑成员弹窗 -->
    <el-dialog v-model="memberDialog" :title="isEditMember?'编辑成员':'添加成员'" width="500px" :close-on-click-modal="false">
      <el-form :model="memberForm" label-width="100px" size="small">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="memberForm.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="与户主关系"><el-input v-model="memberForm.relationship" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="身份证号"><el-input v-model="memberForm.idCard" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="memberForm.phone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="性别"><el-select v-model="memberForm.gender" style="width:100%"><el-option label="男" value="1" /><el-option label="女" value="0" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="年龄"><el-input-number v-model="memberForm.age" :min="0" :max="150" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="健康状况"><el-input v-model="memberForm.healthStatus" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="是否有工作"><el-switch v-model="memberForm.hasJobBool" active-text="是" inactive-text="否" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="memberDialog=false">取消</el-button><el-button type="primary" :loading="memberSub" @click="submitMember">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFamilyById, updateFamily, createFamilyMember, deleteFamilyMember, updateFamilyMember } from '@/api/system'

const profile = ref<any>({})
const familyMembers = ref<any[]>([])
const pairInfo = ref<any>(null)
const familyId = localStorage.getItem('familyId') || '1'

const fetchData = async () => {
  try {
    const res = await getFamilyById(familyId)
    if (res.data) {
      profile.value = res.data.family || res.data
      familyMembers.value = res.data.members || []
      pairInfo.value = res.data.pairInfo || null
    }
  } catch {}
}

// Edit Family
const editFamilyDialog = ref(false)
const familyForm = reactive({ householderName:'', idCard:'', address:'', familySize:1, annualIncome:0 })

const openEditFamily = () => {
  const p = profile.value
  Object.assign(familyForm, { householderName:p.householderName||"", idCard:p.idCard||"", address:p.address||"", familySize:p.familySize||1, annualIncome:p.annualIncome||0 })
  editFamilyDialog.value = true
}
const submitFamilyEdit = async () => {
  try {
    await updateFamily(Number(familyId), { ...familyForm })
    ElMessage.success('保存成功')
    editFamilyDialog.value = false
    fetchData()
  } catch { ElMessage.error('保存失败') }
}

// Members
const memberDialog = ref(false)
const isEditMember = ref(false)
const editMemberId = ref<number | null>(null)
const memberSub = ref(false)
const memberForm = reactive({ name:'', relationship:'', idCard:'', phone:'', gender:'1', age:0, healthStatus:'', hasJobBool:false })

const openAddMember = () => {
  isEditMember.value = false; editMemberId.value = null
  Object.assign(memberForm, { name:'', relationship:'', idCard:'', phone:'', gender:'1', age:0, healthStatus:'', hasJobBool:false })
  memberDialog.value = true
}
const openEditMember = (row: any) => {
  isEditMember.value = true; editMemberId.value = row.memberId
  Object.assign(memberForm, { name:row.name, relationship:row.relationship, idCard:row.idCard||'', phone:row.phone||'', gender:String(row.gender||'1'), age:row.age||0, healthStatus:row.healthStatus||'', hasJobBool:row.hasJob==='1' })
  memberDialog.value = true
}

const getMemberPayload = () => ({
  name: memberForm.name,
  relationship: memberForm.relationship,
  idCard: memberForm.idCard,
  phone: memberForm.phone,
  gender: memberForm.gender,
  age: memberForm.age,
  healthStatus: memberForm.healthStatus,
  hasJob: memberForm.hasJobBool ? '1' : '0'
})

const submitMember = async () => {
  memberSub.value = true
  try {
    if (isEditMember.value && editMemberId.value) {
      await updateFamilyMember(editMemberId.value, getMemberPayload())
      ElMessage.success('更新成功')
    } else {
      await createFamilyMember(Number(familyId), getMemberPayload())
      ElMessage.success('添加成功')
    }
    memberDialog.value = false; fetchData()
  } catch { ElMessage.error('操作失败') } finally { memberSub.value = false }
}

const handleDeleteMember = async (row: any) => {
  try { await deleteFamilyMember(row.memberId); ElMessage.success('删除成功'); fetchData() } catch { ElMessage.error('删除失败') }
}

const povertyLevelTag = (level: string) => ({ '一般贫困户':'warning', '低保贫困户':'danger', '特困供养':'info' }[level] || '')

onMounted(() => fetchData())
</script>

<style scoped lang="scss">
.poor-profile { padding: 20px; max-width: 1100px; margin: 0 auto; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.title { font-size: 16px; font-weight: 600; color: #303133; }
.profile-card, .member-card, .pair-card { margin-bottom: 20px; border-radius: 8px; }
</style>
