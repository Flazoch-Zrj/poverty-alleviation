<template>
  <div class="needs-manage">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span style="font-weight:600;">困难需求管理</span>
          <el-select v-model="statusFilter" placeholder="全部状态" clearable size="small" style="width:140px" @change="fetchData">
            <el-option label="待解决" value="0" /><el-option label="已对接" value="1" /><el-option label="已完成" value="2" />
          </el-select>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="需求标题" min-width="160" />
        <el-table-column prop="needType" label="类型" width="80" align="center">
          <template #default="{ row }"><el-tag size="small">{{ row.needType }}</el-tag></template>
        </el-table-column>
        <el-table-column label="发布家庭" width="120">
          <template #default="{ row }">家庭 #{{ row.familyId }}</template>
        </el-table-column>
        <el-table-column prop="targetAmount" label="目标金额" width="110" align="right">
          <template #default="{ row }">{{ row.targetAmount?.toLocaleString() ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="已获金额" width="110" align="right">
          <template #default="{ row }">{{ row.actualAmount?.toLocaleString() ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="{0:'warning',1:'primary',2:'success'}[row.status] || ''" size="small">
              {{ {0:'待解决',1:'已对接',2:'已完成'}[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === '0'" size="small" type="primary" link @click="updateStatus(row, '1')">标记对接</el-button>
            <el-button v-if="row.status === '0' || row.status === '1'" size="small" type="success" link @click="updateStatus(row, '2')">标记完成</el-button>
            <span v-else style="color:#909399;font-size:12px;">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="pn" v-model:page-size="ps" :total="total" layout="total,sizes,prev,pager,next" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNeedPage, reviewNeed } from '@/api/system'

const list = ref<any[]>([])
const loading = ref(false)
const pn = ref(1); const ps = ref(10); const total = ref(0)
const statusFilter = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const params: any = { page: pn.value, size: ps.value }
    if (statusFilter.value) params.status = statusFilter.value
    const r = await getNeedPage(params)
    list.value = r.data?.records ?? r.data?.list ?? []
    total.value = r.data?.total ?? 0
  } catch {} finally { loading.value = false }
}

const updateStatus = async (row: any, status: string) => {
  try {
    await reviewNeed(row.needId, { status })
    ElMessage.success('状态已更新')
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

onMounted(() => fetchData())
</script>

<style scoped>
.needs-manage { padding: 20px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
