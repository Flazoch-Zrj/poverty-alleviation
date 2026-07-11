<template>
  <div class="donation-manage">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="s in statCards" :key="s.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__inner">
            <div class="stat-card__icon" :style="{ background: s.bg }">
              <el-icon :size="22" color="#fff"><component :is="s.icon" /></el-icon>
            </div>
            <div class="stat-card__info">
              <p class="stat-card__label">{{ s.label }}</p>
              <p class="stat-card__value" :style="{ color: s.color }">{{ s.value }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Tab -->
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="资金捐赠" name="money" />
      <el-tab-pane label="物资捐赠" name="goods" />
      <el-tab-pane label="待分发" name="distribute" />
    </el-tabs>

    <!-- ═══ 资金捐赠 ═══ -->
    <template v-if="activeTab === 'money'">
      <div class="search-card" style="margin-bottom:16px">
        <div class="search-card-header">
          <div class="search-card-title">资金捐赠管理</div>
          <div class="search-actions">
            <el-button class="btn-glow-orange" @click="openMoneyDialog(false)"><el-icon><Plus /></el-icon> 新增捐赠</el-button>
          </div>
        </div>
        <div class="search-fields">
          <el-input v-model="moneyKeyword" placeholder="请输入捐赠方名称" clearable style="width:200px" size="default" />
          <el-button type="primary" @click="fetchMoneyList">搜索</el-button>
        </div>
      </div>

      <el-table :data="moneyList" v-loading="moneyLoading" style="width:100%">
        <el-table-column label="捐赠方" width="150">
          <template #default="{ row }">
            <span>{{ isOrg(row.donorName) ? '🏢' : '👤' }} {{ row.donorName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="捐赠金额(元)" width="150" align="right">
          <template #default="{ row }">¥{{ (row.amount || 0).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120" />
        <el-table-column label="收款状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" effect="dark" class="is-status">已到账</el-tag>
            <el-tag v-else type="warning" effect="plain" class="is-status" style="border:1px dashed #F5A623">待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="捐赠时间" width="170">
          <template #default="{ row }">{{ row.donateTime ? $dayjs(row.donateTime).format('YYYY-MM-DD HH:mm') : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" class="btn-action" link @click="openMoneyDialog(true, row)">编辑</el-button>
            <el-button size="small" class="btn-delete" link @click="handleMoneyDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="mpn" v-model:page-size="mps" :total="mt" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="fetchMoneyList" @current-change="fetchMoneyList" style="margin-top:16px" />
    </template>

    <!-- ═══ 物资捐赠 ═══ -->
    <template v-if="activeTab === 'goods'">
      <div class="search-card" style="margin-bottom:16px">
        <div class="search-card-header">
          <div class="search-card-title">物资捐赠管理</div>
          <div class="search-actions">
            <el-button class="btn-glow-orange" @click="openGoodsDialog(false)"><el-icon><Plus /></el-icon> 新增物资</el-button>
          </div>
        </div>
      </div>

      <el-table :data="goodsList" v-loading="goodsLoading" style="width:100%">
        <el-table-column label="捐赠方" width="150">
          <template #default="{ row }"><span>{{ isOrg(row.donorName) ? '🏢' : '👤' }} {{ row.donorName }}</span></template>
        </el-table-column>
        <el-table-column prop="goodsName" label="物资名称" width="140" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="primary" class="is-status">已接收</el-tag>
            <el-tag v-else-if="row.status === 2" type="success" class="is-status">已发放</el-tag>
            <el-tag v-else-if="row.status === 3" type="warning" class="is-status">已反馈</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="捐赠时间" width="170">
          <template #default="{ row }">{{ row.donateTime ? $dayjs(row.donateTime).format('YYYY-MM-DD HH:mm') : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" class="btn-action" link @click="openGoodsDialog(true, row)">编辑</el-button>
            <el-button size="small" class="btn-delete" link @click="handleGoodsDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="gpn" v-model:page-size="gps" :total="gt" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="fetchGoodsList" @current-change="fetchGoodsList" style="margin-top:16px" />
    </template>

    <!-- ═══ 待分发 ═══ -->
    <template v-if="activeTab === 'distribute'">
      <el-alert title="以下物资已入库待分发，确认分发后自动加 30 积分" type="info" :closable="false" show-icon style="margin-bottom:16px" />
      <el-table :data="distributeList" v-loading="distLoading" style="width:100%">
        <el-table-column prop="donorName" label="捐赠方" width="140" />
        <el-table-column prop="goodsName" label="物资名称" width="140" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column prop="donateTime" label="入库时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleDistribute(row)" :loading="distSubId === row.goodsDonationId">📦 确认分发</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!distLoading && distributeList.length === 0" description="暂无待分发的物资" />
    </template>

    <!-- ═══ 资金弹窗 ═══ -->
    <el-dialog v-model="mdlg" :title="mEdit ? '编辑捐赠' : '新增捐赠'" width="520px" :close-on-click-modal="false">
      <el-form ref="mf" :model="mfm" :rules="mrl" label-width="100px">
        <el-form-item label="捐赠方" prop="donorName"><el-input v-model="mfm.donorName" placeholder="请输入捐赠方名称" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="mfm.amount" :min="0.01" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="mfm.paymentMethod" placeholder="请选择" style="width:100%">
            <el-option label="支付宝" value="支付宝" /><el-option label="微信支付" value="微信支付" />
            <el-option label="银行转账" value="银行转账" /><el-option label="现金" value="现金" /><el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="捐赠时间" prop="donateTime"><el-date-picker v-model="mfm.donateTime" type="datetime" placeholder="选择时间" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="匿名">
          <el-switch v-model="mfm.isAnonymous" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="mdlg=false">取消</el-button><el-button type="primary" :loading="mSub" @click="submitMoney">确认</el-button></template>
    </el-dialog>

    <!-- ═══ 物资弹窗 ═══ -->
    <el-dialog v-model="gdlg" :title="gEdit ? '编辑物资' : '新增物资'" width="520px" :close-on-click-modal="false">
      <el-form ref="gf" :model="gfm" :rules="grl" label-width="100px">
        <el-form-item label="捐赠方" prop="donorName"><el-input v-model="gfm.donorName" placeholder="请输入捐赠方名称" /></el-form-item>
        <el-form-item label="物资名称" prop="goodsName"><el-input v-model="gfm.goodsName" placeholder="请输入物资名称" /></el-form-item>
        <el-form-item label="数量" prop="quantity"><el-input-number v-model="gfm.quantity" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="单位" prop="unit"><el-input v-model="gfm.unit" placeholder="如：箱、袋、件" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="gfm.status" style="width:100%">
            <el-option label="已接收" :value="1" /><el-option label="已发放" :value="2" /><el-option label="已反馈" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="gdlg=false">取消</el-button><el-button type="primary" :loading="gSub" @click="submitGoods">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getDonationMoneyPage, createDonationMoney, updateDonationMoney, deleteDonationMoney,
  getDonationGoodsPage, createDonationGoods, updateDonationGoods, deleteDonationGoods,
  distributeDonationGoods
} from '@/api/system'

const activeTab = ref('money')
const onTabChange = () => {}

/* ─── 统计卡片 ─── */
const statCards = computed(() => {
  const total = moneyList.value.reduce((s: number, r: any) => s + (r.amount || 0), 0)
  const received = moneyList.value.filter((r: any) => r.status === 1).reduce((s: number, r: any) => s + (r.amount || 0), 0)
  const pending = moneyList.value.filter((r: any) => r.status === 0).reduce((s: number, r: any) => s + (r.amount || 0), 0)
  return [
    { key: 'count', label: '捐赠总笔数', value: moneyList.value.length + ' 笔', bg: 'linear-gradient(135deg,#1E8E5E,#2EBF7A)', icon: 'Coin', color: '#1E8E5E' },
    { key: 'total', label: '捐赠总金额', value: '¥' + total.toLocaleString(), bg: 'linear-gradient(135deg,#F5A623,#FFC04D)', icon: 'Money', color: '#F5A623' },
    { key: 'received', label: '已到账', value: '¥' + received.toLocaleString(), bg: 'linear-gradient(135deg,#409EFF,#79bbff)', icon: 'CircleCheck', color: '#409EFF' },
    { key: 'pending', label: '待确认', value: '¥' + pending.toLocaleString(), bg: 'linear-gradient(135deg,#E74C3C,#F1948A)', icon: 'Clock', color: '#E74C3C' },
  ]
})

function isOrg(name: string) { return /公司|集团|协会|中心|局|院|校/.test(name || '') }

/* ─── 资金 ─── */
const moneyList = ref<any[]>([]), moneyLoading = ref(false), mpn = ref(1), mps = ref(10), mt = ref(0), moneyKeyword = ref('')
const fetchMoneyList = async () => {
  moneyLoading.value = true
  try {
    const r = await getDonationMoneyPage({ page: mpn.value, size: mps.value })
    moneyList.value = r.data?.records || r.data?.list || []
    mt.value = r.data?.total || 0
  } catch {} finally { moneyLoading.value = false }
}

const mdlg = ref(false), mEdit = ref(false), mEditId = ref<number|null>(null), mSub = ref(false), mf = ref<FormInstance>()
const mfm = reactive({ donorName:'', amount:0.01, paymentMethod:'', isAnonymous:0, donateTime:'' })
const mrl: FormRules = { donorName:[{required:true,message:'请输入捐赠方名称',trigger:'blur'}], amount:[{required:true,message:'请输入金额',trigger:'blur'}], paymentMethod:[{required:true,message:'请选择支付方式',trigger:'change'}] }
const openMoneyDialog = (edit: boolean, row?: any) => {
  mEdit.value = edit; mEditId.value = null
  if (edit && row) {
    mEditId.value = row.moneyDonationId
    Object.assign(mfm, { donorName:row.donorName, amount:row.amount, paymentMethod:row.paymentMethod, isAnonymous:row.isAnonymous ?? 0, donateTime:row.donateTime })
  } else { Object.assign(mfm, { donorName:'', amount:0.01, paymentMethod:'', isAnonymous:0, donateTime:'' }) }
  mdlg.value = true
}
const submitMoney = async () => {
  const ok = await mf.value?.validate().catch(()=>false)
  if (!ok) return; mSub.value = true
  try {
    if (mEdit.value && mEditId.value) { await updateDonationMoney(mEditId.value, { ...mfm }); ElMessage.success('更新成功') }
    else { await createDonationMoney({ ...mfm }); ElMessage.success('新增成功') }
    mdlg.value = false; fetchMoneyList()
  } catch{} finally { mSub.value = false }
}
const handleMoneyDelete = async (row: any) => {
  try { await ElMessageBox.confirm('确定删除这条捐赠记录吗？','提示'); await deleteDonationMoney(row.moneyDonationId); ElMessage.success('已删除'); fetchMoneyList() } catch{}
}

/* ─── 物资 ─── */
const goodsList = ref<any[]>([]), goodsLoading = ref(false), gpn = ref(1), gps = ref(10), gt = ref(0)
const fetchGoodsList = async () => {
  goodsLoading.value = true
  try { const r = await getDonationGoodsPage({ page: gpn.value, size: gps.value }); goodsList.value = r.data?.records||r.data?.list||[]; gt.value = r.data?.total||0 } catch{} finally { goodsLoading.value = false }
}
const gdlg = ref(false), gEdit = ref(false), gEditId = ref<number|null>(null), gSub = ref(false), gf = ref<FormInstance>()
const gfm = reactive({ donorName:'', goodsName:'', quantity:1, unit:'', status:1 })
const grl: FormRules = { donorName:[{required:true,message:'请输入捐赠方名称',trigger:'blur'}], goodsName:[{required:true,message:'请输入物资名称',trigger:'blur'}], quantity:[{required:true,message:'请输入数量',trigger:'blur'}] }
const openGoodsDialog = (edit: boolean, row?: any) => {
  gEdit.value = edit; gEditId.value = null
  if (edit && row) { gEditId.value = row.goodsDonationId; Object.assign(gfm, { donorName:row.donorName, goodsName:row.goodsName, quantity:row.quantity, unit:row.unit, status:row.status }) }
  else { Object.assign(gfm, { donorName:'', goodsName:'', quantity:1, unit:'', status:1 }) }
  gdlg.value = true
}
const submitGoods = async () => {
  const ok = await gf.value?.validate().catch(()=>false); if(!ok)return; gSub.value=true
  try {
    if (gEdit.value && gEditId.value) { await updateDonationGoods(gEditId.value, { ...gfm }); ElMessage.success('更新成功') }
    else { await createDonationGoods({ ...gfm }); ElMessage.success('新增成功') }
    gdlg.value = false; fetchGoodsList()
  } catch{} finally { gSub.value = false }
}
const handleGoodsDelete = async (row: any) => {
  try { await ElMessageBox.confirm('确定删除这条物资记录吗？','提示'); await deleteDonationGoods(row.goodsDonationId); ElMessage.success('已删除'); fetchGoodsList() } catch{}
}

onMounted(() => { fetchMoneyList(); fetchGoodsList(); fetchDistributeList() })

/* ─── 待分发 ─── */
const distributeList = ref<any[]>([]), distLoading = ref(false), distSubId = ref<number|null>(null)
const fetchDistributeList = async () => {
  distLoading.value = true
  try { const r = await getDonationGoodsPage({ page: 1, size: 200 }); distributeList.value = (r.data?.records||r.data?.list||[]).filter((g:any) => g.status === 1) } catch{} finally { distLoading.value = false }
}
const handleDistribute = async (row: any) => {
  distSubId.value = row.goodsDonationId
  try {
    await distributeDonationGoods(row.goodsDonationId)
    ElMessage.success('分发确认成功！已加 30 积分')
    fetchDistributeList(); fetchGoodsList()
  } catch { ElMessage.error('操作失败') } finally { distSubId.value = null }
}
</script>

<style scoped lang="scss">
.donation-manage {
  .stat-row { margin-bottom:20px; }
  .stat-card {
    border-radius:12px !important; overflow:hidden;
    &__inner { display:flex; align-items:center; gap:14px; }
    &__icon { width:44px; height:44px; border-radius:12px; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
    &__label { margin:0 0 2px; font-size:13px; color:var(--p-text-secondary); }
    &__value { margin:0; font-size:20px; font-weight:700; }
  }
}
</style>
