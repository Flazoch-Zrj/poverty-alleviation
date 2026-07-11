<template>
  <div class="family-map">
    <div class="map-header">
      <h2>📍 贫困户分布地图</h2>
      <div class="map-actions">
        <el-select v-model="selectedFamilyId" placeholder="搜索家庭..." filterable clearable size="small" style="width:200px" @change="focusFamily">
          <el-option v-for="f in families" :key="f.familyId" :label="f.householderName + ' - ' + f.address" :value="f.familyId" />
        </el-select>
        <el-button size="small" @click="centerAll">全部显示</el-button>
        <el-button size="small" @click="planRoute" :disabled="routeFamilies.length < 2" type="primary">📋 路线规划</el-button>
        <el-button size="small" @click="clearRoute" v-if="showRoute">清除路线</el-button>
      </div>
    </div>

    <div class="map-tips" v-if="routeFamilies.length > 0">
      已选 <strong>{{ routeFamilies.length }}</strong> 户家庭，点击标记可切换选择
    </div>

    <div class="map-layout">
      <div ref="mapContainer" class="map-container"></div>
      <div class="map-sidebar">
        <h4>标记管理</h4>
        <p class="sidebar-hint">在地图上右键点击，可为家庭设置位置</p>
        <div class="family-list-side">
          <div v-for="f in families" :key="f.familyId" class="fam-item" :class="{ 'has-loc': f.latitude }" @click="focusFamily(f.familyId)">
            <span class="fam-dot" :style="{background: f.latitude ? '#10B981' : '#EF4444'}"></span>
            <span class="fam-name">{{ f.householderName }}</span>
            <span class="fam-badge">{{ f.latitude ? '已定位' : '未定位' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右键菜单 -->
    <div v-show="ctxMenu.visible" class="ctx-menu" :style="{ left: ctxMenu.x + 'px', top: ctxMenu.y + 'px' }">
      <div class="ctx-title">设置家庭位置</div>
      <div v-for="f in families" :key="f.familyId" class="ctx-item" @click="setFamilyLocation(f)">
        <span class="fam-dot" :style="{background: f.latitude ? '#10B981' : '#EF4444'}"></span>
        {{ f.householderName }}
      </div>
    </div>

    <el-dialog v-model="detailVisible" :title="selectedFamily?.householderName + ' 家庭'" width="400px">
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="档案编号">{{ selectedFamily?.familyCode }}</el-descriptions-item>
        <el-descriptions-item label="户主">{{ selectedFamily?.householderName }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ selectedFamily?.address }}</el-descriptions-item>
        <el-descriptions-item label="家庭人数">{{ selectedFamily?.familySize }} 人</el-descriptions-item>
        <el-descriptions-item label="年收入">{{ selectedFamily?.annualIncome?.toLocaleString() }} 元</el-descriptions-item>
        <el-descriptions-item label="经纬度" v-if="selectedFamily?.latitude">{{ selectedFamily.latitude }}, {{ selectedFamily.longitude }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="selectedFamily?.status==='建档'?'warning':'success'" size="small">{{ selectedFamily?.status }}</el-tag></el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="router.push('/cadre/family/' + selectedFamily?.familyId)">查看详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFamilyPage, updateFamily } from '@/api/system'
import AMapLoader from '@amap/amap-jsapi-loader'

const router = useRouter()
const mapContainer = ref<HTMLDivElement>()
const detailVisible = ref(false)
const selectedFamily = ref<any>(null)
const families = ref<any[]>([])
const routeFamilies = ref<any[]>([])
const showRoute = ref(false)
const selectedFamilyId = ref<number>()
const ctxMenu = reactive({ visible: false, x: 0, y: 0, lng: 0, lat: 0 })

let map: any = null; let markers: any[] = []; let routeLine: any = null; let AMap: any = null

const initMap = async () => {
  if (!mapContainer.value) return
  AMap = await AMapLoader.load({ key: '99cee623739565a655368935beb42624', version: '2.0', plugins: ['AMap.Driving', 'AMap.ToolBar', 'AMap.Scale'] })

  map = new AMap.Map(mapContainer.value, { zoom: 13, center: [112.10, 34.13], viewMode: '2D', mapStyle: 'amap://styles/light' })
  map.addControl(new AMap.ToolBar())
  map.addControl(new AMap.Scale())

  // Right-click context menu
  (map as any).on('rightclick', (e: any) => {
    ctxMenu.lng = e.lnglat.lng
    ctxMenu.lat = e.lnglat.lat
    ctxMenu.x = e.pixel.x + 10
    ctxMenu.y = e.pixel.y + 10
    ctxMenu.visible = true
  })

  // Click anywhere to hide context menu
  (map as any).on('click', () => { ctxMenu.visible = false })

  await loadFamilies()
  addMarkers()
}

const loadFamilies = async () => {
  try {
    const r = await getFamilyPage({ page: 1, size: 100 })
    families.value = r.data?.records || r.data?.list || []
  } catch {}
}

const addMarkers = () => {
  if (!map || !AMap) return
  markers.forEach(m => map.remove(m))
  markers = []
  const hasLoc = families.value.filter(f => f.latitude && f.longitude)

  hasLoc.forEach((family) => {
    const marker = new AMap.Marker({
      position: [family.longitude, family.latitude],
      title: family.householderName,
      label: { content: family.householderName, direction: 'top', offset: [0, -30] }
    })
    marker.on('click', () => {
      const idx = routeFamilies.value.findIndex(f => f.familyId === family.familyId)
      if (idx >= 0) routeFamilies.value.splice(idx, 1)
      else routeFamilies.value.push(family)
      selectedFamily.value = family
      detailVisible.value = true
    })
    map.add(marker)
    markers.push(marker)
  })
  if (hasLoc.length > 0) map.setFitView(markers, false, [50, 50, 50, 50])
}

const setFamilyLocation = async (family: any) => {
  ctxMenu.visible = false
  try {
    await updateFamily(family.familyId, { latitude: ctxMenu.lat, longitude: ctxMenu.lng })
    family.latitude = ctxMenu.lat
    family.longitude = ctxMenu.lng
    ElMessage.success(family.householderName + ' 位置已更新')
    addMarkers()
  } catch { ElMessage.error('更新失败') }
}

const focusFamily = (val: number) => {
  if (!val || !map) return
  const family = families.value.find(f => f.familyId === val)
  if (family && family.latitude) { map.setZoomAndCenter(15, [family.longitude, family.latitude]); selectedFamily.value = family; detailVisible.value = true }
  else if (family) { ElMessage.info('该家庭尚未设置位置，请在地图上右键点击设置') }
}

const centerAll = () => { if (map && markers.length > 0) map.setFitView(markers, false, [50, 50, 50, 50]) }

const planRoute = () => {
  if (routeFamilies.value.length < 2 || !map || !AMap) return
  showRoute.value = true; clearRouteLine()
  const pts = routeFamilies.value.filter(f => f.latitude).map(f => [f.longitude, f.latitude])
  if (pts.length < 2) return
  const path = pts.map((p: number[]) => new AMap.LngLat(p[0], p[1]))
  routeLine = new AMap.Polyline({ path, strokeColor: '#EF4444', strokeWeight: 4, strokeStyle: 'dashed', strokeDasharray: [10, 6], strokeOpacity: 0.8 })
  map.add(routeLine); map.setFitView([routeLine, ...markers], false, [50, 50, 50, 50])

  const driving = new AMap.Driving({ policy: AMap.DrivingPolicy.LEAST_TIME })
  driving.search(new AMap.LngLat(pts[0][0], pts[0][1]), new AMap.LngLat(pts[pts.length-1][0], pts[pts.length-1][1]),
    { waypoints: pts.slice(1, -1).map((p: number[]) => new AMap.LngLat(p[0], p[1])) },
    (status: string, result: any) => {
      if (status === 'complete') {
        const km = (result.routes[0].distance / 1000).toFixed(1)
        const min = Math.round(result.routes[0].time / 60)
        map?.setInfoWindow(new AMap.InfoWindow({
          content: `<div style="font-size:14px;font-weight:600;padding:4px;">全程约 ${km} 公里 · 预计 ${min} 分钟</div>`,
          position: path[Math.floor(path.length / 2)], offset: new AMap.Pixel(0, -20)
        }))
        map?.getInfoWindow()?.open(map)
      }
    })
}

const clearRouteLine = () => { if (routeLine) { map?.remove(routeLine); routeLine = null } }
const clearRoute = () => { showRoute.value = false; routeFamilies.value = []; clearRouteLine(); map?.clearInfoWindow() }

onMounted(() => { nextTick(initMap) })
onUnmounted(() => { if (map) { map.destroy(); map = null } })
</script>

<style scoped>
.family-map { padding: 20px; }
.map-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; flex-wrap: wrap; gap: 8px; }
.map-header h2 { margin: 0; font-size: 18px; }
.map-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.map-tips { background: #EFF6FF; border: 1px solid #DBEAFE; border-radius: 8px; padding: 8px 14px; font-size: 12px; color: #2563EB; margin-bottom: 12px; }
.map-layout { display: flex; gap: 16px; }
.map-container { flex: 1; height: 620px; border-radius: 12px; border: 1px solid #E2E8F0; overflow: hidden; }
.map-sidebar { width: 200px; background: #fff; border-radius: 12px; border: 1px solid #E2E8F0; padding: 14px; flex-shrink: 0; }
.map-sidebar h4 { margin: 0 0 4px; font-size: 14px; }
.sidebar-hint { font-size: 11px; color: #94A3B8; margin: 0 0 12px; }
.family-list-side { display: flex; flex-direction: column; gap: 6px; max-height: 520px; overflow-y: auto; }
.fam-item { display: flex; align-items: center; gap: 6px; padding: 6px 8px; border-radius: 6px; cursor: pointer; font-size: 12px; transition: background .15s; }
.fam-item:hover { background: #F1F5F9; }
.fam-item.has-loc { }
.fam-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.fam-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.fam-badge { font-size: 10px; color: #94A3B8; }
.ctx-menu { position: fixed; z-index: 9999; background: #fff; border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.15); padding: 4px; min-width: 160px; }
.ctx-title { padding: 6px 10px; font-size: 11px; color: #94A3B8; font-weight: 600; border-bottom: 1px solid #F1F5F9; margin-bottom: 2px; }
.ctx-item { display: flex; align-items: center; gap: 6px; padding: 6px 10px; font-size: 12px; border-radius: 4px; cursor: pointer; }
.ctx-item:hover { background: #F1F5F9; }
@media(max-width:900px){ .map-layout { flex-direction: column; } .map-sidebar { width: 100%; max-height: 200px; } }
</style>
