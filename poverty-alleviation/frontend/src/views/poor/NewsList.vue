<template>
  <div class="news-portal">
    <!-- ====== 顶部导航栏（站点内） ====== -->
    <div class="portal-topbar">
      <div class="topbar-inner">
        <div class="topbar-logo">
          <div class="logo-icon" aria-hidden="true">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M4 20L12 4L20 20H4Z" fill="#60A5FA" opacity="0.9"/>
              <path d="M8 16L12 8L16 16H8Z" fill="#93C5FD" opacity="0.7"/>
              <circle cx="12" cy="12" r="2" fill="#1E40AF"/>
            </svg>
          </div>
          <span class="logo-text">扶贫资讯</span>
        </div>

        <div class="topbar-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索资讯..."
            clearable
            size="large"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2.5" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            </template>
          </el-input>
        </div>

        <div class="topbar-date">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          <span>{{ todayDate }}</span>
        </div>
      </div>
    </div>

    <!-- ====== 头条区 ====== -->
    <div class="portal-hero">
      <div class="hero-bg">
        <img src="/images/特色农产品丰收.jpg" alt="头条新闻" class="hero-img" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <span class="hero-tag">头条</span>
        <h2 class="hero-title" v-if="featured">{{ featured.title }}</h2>
        <p class="hero-summary" v-if="featured">{{ truncate(featured.content, 120) }}</p>
        <div class="hero-meta" v-if="featured">
          <span class="hero-type">{{ typeLabel(featured.type) }}</span>
          <span class="hero-date">{{ featured.createTime }}</span>
        </div>
      </div>
    </div>

    <!-- ====== 分类筛选胶囊 ====== -->
    <div class="portal-categories">
      <button
        v-for="cat in categories"
        :key="cat.value"
        class="cat-pill"
        :class="{ 'cat-pill--active': activeType === cat.value }"
        @click="activeType = cat.value; handleTabChange()"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- ====== 主区域：左侧列表 + 右侧边栏 ====== -->
    <div class="portal-main">
      <!-- 左侧：要闻列表 -->
      <div class="main-left">
        <div class="section-label">
          <span class="label-dot"></span>
          <span>要闻列表</span>
        </div>

        <div class="news-list" v-loading="loading">
          <article class="news-item" v-for="item in newsList" :key="item.id" @click="openDetail(item)">
            <div class="item-thumb">
              <img :src="thumbImage(item)" :alt="item.title" loading="lazy" />
            </div>
            <div class="item-body">
              <h3 class="item-title">{{ item.title }}</h3>
              <p class="item-summary">{{ truncate(item.content, 100) }}</p>
              <div class="item-footer">
                <span class="item-tag" :class="'tag--' + item.type">{{ typeLabel(item.type) }}</span>
                <span class="item-date">{{ formatDate(item.createTime) }}</span>
                <span class="item-views">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  {{ item.viewCount }}
                </span>
              </div>
            </div>
          </article>

          <!-- 空状态 -->
          <div class="empty-state" v-if="!loading && newsList.length === 0">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            <p>暂无相关资讯</p>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            :page-sizes="[10, 20, 30]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="fetchData"
            @current-change="fetchData"
          />
        </div>
      </div>

      <!-- 右侧：热门/推荐边栏 -->
      <aside class="main-right">
        <!-- 热门阅读 -->
        <div class="sidebar-card">
          <div class="sidebar-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            <span>热门阅读</span>
          </div>
          <div class="sidebar-list">
            <div class="sidebar-item" v-for="(item, idx) in hotList" :key="item.id" @click="openDetail(item)">
              <span class="sidebar-rank" :class="'rank--' + (idx < 3 ? 'top' : 'normal')">{{ idx + 1 }}</span>
              <div class="sidebar-info">
                <span class="sidebar-text">{{ item.title }}</span>
                <span class="sidebar-meta">{{ item.viewCount }} 次阅读</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 资讯分类 -->
        <div class="sidebar-card">
          <div class="sidebar-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0A2647" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
            <span>资讯分类</span>
          </div>
          <div class="category-list">
            <div
              class="category-item"
              v-for="cat in categories.slice(1)"
              :key="cat.value"
              @click="activeType = cat.value; handleTabChange()"
            >
              <span class="cat-name">{{ cat.label }}</span>
              <span class="cat-count" v-if="cat.count">{{ cat.count }}</span>
            </div>
          </div>
        </div>
      </aside>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNewsPage } from '@/api/news'
import type { NewsItem } from '@/api/news'

// ============================================================
// 顶部日间
// ============================================================
const todayDate = computed(() => {
  const d = new Date()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekdays[d.getDay()]}`
})

// ============================================================
// 分类
// ============================================================
const categories = [
  { label: '全部', value: '' },
  { label: '高层', value: '1' },
  { label: '独家', value: '1' },
  { label: '访谈', value: '1' },
  { label: '图片', value: '2' },
  { label: '脱贫案例', value: '2' },
  { label: '故事', value: '2' },
  { label: '报告', value: '1' },
  { label: '公益', value: '4' },
  { label: '中央文件', value: '1' },
  { label: '政策解读', value: '1' },
  { label: '国际合作', value: '2' },
  { label: '扶贫模式', value: '2' },
  { label: '通知公告', value: '3' },
  { label: '捐赠公示', value: '4' },
]

// ============================================================
// 搜索
// ============================================================
const searchKeyword = ref('')
const handleSearch = () => {
  // 实际场景可以扩展搜索参数
  queryParams.pageNum = 1
  fetchData()
}

// ============================================================
// 列表数据
// ============================================================
const loading = ref(false)
const activeType = ref('')
const newsList = ref<NewsItem[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  type: ''
})

const handleTabChange = () => {
  queryParams.type = activeType.value
  queryParams.pageNum = 1
  fetchData()
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (!params.type) delete params.type
    if (searchKeyword.value) (params as any).keyword = searchKeyword.value
    const res = await getNewsPage(params)
    if (res.data) {
      newsList.value = res.data.records || res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('获取资讯列表失败', e)
  } finally {
    loading.value = false
  }
}

// ============================================================
// 头条 — 取列表第一条
// ============================================================
const featured = computed(() => newsList.value[0] || null)

// ============================================================
// 热门 — 按阅读量排序取前5
// ============================================================
const hotList = computed(() => {
  return [...newsList.value]
    .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    .slice(0, 5)
})

// ============================================================
// 辅助函数
// ============================================================
const typeLabel = (type: string) => {
  const map: Record<string, string> = {
    '1': '政策新闻',
    '2': '脱贫案例',
    '3': '通知公告',
    '4': '捐赠公示',
    '5': '中央文件',
  }
  return map[type] || type || '其他'
}

const truncate = (text: string, len: number) => {
  if (!text) return ''
  const plain = text.replace(/<[^>]*>/g, '').replace(/&nbsp;/g, ' ').replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"')
  return plain.length > len ? plain.slice(0, len) + '...' : plain
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 本地图片素材列表（从 图片素材2 目录随机抽取，不重复）
const localImages = [
  'VCG211258445720.jpg', 'VCG211262853370.jpg', 'VCG211352153421.jpg',
  'VCG211357872011.jpg', 'VCG211362769105.jpg', 'VCG211371442594.jpg',
  'VCG211372106364.jpg', 'VCG211376471019.jpg', 'VCG211380214627.jpg',
  'VCG211399201279.jpg', 'VCG211428027248.jpg', 'VCG211498154650.jpg',
  'VCG211527351999.jpg', 'VCG211533171021.jpg', 'VCG21gic20071781.jpg',
  'VCG41N1156370269.jpg',
]

// Fisher-Yates 洗牌算法
const shuffleArray = <T>(arr: T[]): T[] => {
  const shuffled = [...arr]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  return shuffled
}

// 随机打乱的图片顺序（组件实例化时生成一次）
let shuffledImages = shuffleArray(localImages)
let imageIndex = 0

// 为新闻项生成缩略图（从本地图片随机不重复抽取）
const thumbImage = (_item: NewsItem) => {
  const img = shuffledImages[imageIndex % shuffledImages.length]
  imageIndex++
  // 循环一轮后重新洗牌
  if (imageIndex >= shuffledImages.length) {
    shuffledImages = shuffleArray(localImages)
    imageIndex = 0
  }
  return `/images/${img}`
}

// ============================================================
// 详情弹窗
// ============================================================
const openDetail = (item: NewsItem | null) => {
  if (!item) return
  router.push({ path: `/news/${item.id}`, query: { title: item.title, type: item.type, content: item.content, createTime: item.createTime, viewCount: String(item.viewCount), fileUrl: item.fileUrl || '', fileName: item.fileName || '', fileSize: item.fileSize || '' } })
}

const router = useRouter()

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
/* ============================================================
   信息公开 — 大型扶贫资讯门户风格
   主色: #0A2647  背景: #F5F7FA  卡片白
   ============================================================ */

.news-portal {
  background: #F5F7FA;
  min-height: 100vh;
}

/* ====== 顶部导航栏 ====== */
.portal-topbar {
  background: #fff;
  border-bottom: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 24px;
}

.topbar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;

  .logo-icon {
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, #0A2647, #2D7A9B);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .logo-text {
    font-size: 17px;
    font-weight: 700;
    color: #0A2647;
    letter-spacing: 0.02em;
  }
}

.topbar-search {
  flex: 1;
  max-width: 360px;

  .search-input {
    :deep(.el-input__wrapper) {
      background: #F1F3F5 !important;
      border-radius: 10px !important;
      box-shadow: none !important;
      padding: 4px 14px !important;
    }

    :deep(.el-input__inner) {
      font-size: 13px;
    }
  }
}

.topbar-date {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6B7280;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
  flex-shrink: 0;
}

/* ====== 头条区 ====== */
.portal-hero {
  position: relative;
  max-width: 1200px;
  margin: 24px auto 0;
  border-radius: 16px;
  overflow: hidden;
  height: 360px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.portal-hero:hover .hero-img {
  transform: scale(1.04);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(10,38,71,0.85) 0%, rgba(10,38,71,0.3) 50%, rgba(0,0,0,0.1) 100%);
}

.hero-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32px 36px;
  color: #fff;
}

.hero-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 6px;
  background: #F59E0B;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
  margin-bottom: 10px;
}

.hero-title {
  font-size: 24px;
  font-weight: 800;
  margin: 0 0 8px;
  line-height: 1.3;
  text-shadow: 0 1px 4px rgba(0,0,0,0.2);
}

.hero-summary {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  margin: 0 0 12px;
  line-height: 1.6;
  max-width: 700px;
}

.hero-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

/* ====== 分类胶囊 ====== */
.portal-categories {
  max-width: 1200px;
  margin: 20px auto 0;
  padding: 0 24px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cat-pill {
  padding: 7px 18px;
  border-radius: 20px;
  border: 1px solid #E5E7EB;
  background: #fff;
  color: #6B7280;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #2D7A9B;
    color: #2D7A9B;
  }

  &--active {
    background: #0A2647;
    border-color: #0A2647;
    color: #fff;
    font-weight: 600;
  }
}

/* ====== 主区域 ====== */
.portal-main {
  max-width: 1200px;
  margin: 24px auto 48px;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 28px;
  align-items: start;
}

/* 左侧 */
.main-left {
  min-width: 0;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 18px;

  .label-dot {
    width: 4px;
    height: 18px;
    border-radius: 2px;
    background: #0A2647;
  }

  span {
    font-size: 16px;
    font-weight: 700;
    color: #0A2647;
  }
}

/* 新闻列表 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 0;
  min-height: 200px;
}

.news-item {
  display: flex;
  gap: 18px;
  padding: 18px 0;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;
  transition: all 0.2s;

  &:first-child {
    padding-top: 0;
  }

  &:hover {
    .item-title { color: #2D7A9B; }
  }
}

.item-thumb {
  width: 180px;
  min-height: 120px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }

  .news-item:hover & img {
    transform: scale(1.05);
  }
}

.item-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

.item-summary {
  font-size: 13px;
  color: #9CA3AF;
  margin: 0 0 auto;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.item-footer {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 10px;
  font-size: 12px;
  color: #9CA3AF;
}

.item-tag {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;

  &.tag--1 { background: #DBEAFE; color: #2563EB; }
  &.tag--2 { background: #D1FAE5; color: #059669; }
  &.tag--3 { background: #FEF3C7; color: #D97706; }
  &.tag--4 { background: #FEE2E2; color: #DC2626; }
  &.tag-- { display: none; }
}

.item-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px;
  color: #CBD5E1;

  p {
    margin: 12px 0 0;
    font-size: 14px;
  }
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 8px;
}

/* ====== 右侧边栏 ====== */
.main-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #0A2647;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}

/* 热门排行 */
.sidebar-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #F5F7FA;
  }
}

.sidebar-rank {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  flex-shrink: 0;

  &.rank--top {
    background: linear-gradient(135deg, #0A2647, #2D7A9B);
    color: #fff;
  }

  &.rank--normal {
    background: #F1F3F5;
    color: #6B7280;
  }
}

.sidebar-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.sidebar-text {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.sidebar-meta {
  font-size: 11px;
  color: #9CA3AF;
  margin-top: 2px;
}

/* 分类列表 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #F0F7FF;

    .cat-name { color: #2D7A9B; }
  }
}

.cat-name {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  transition: color 0.2s;
}

.cat-count {
  font-size: 11px;
  color: #9CA3AF;
  background: #F1F3F5;
  padding: 1px 8px;
  border-radius: 8px;
}


/* ====== 响应式 ====== */
@media (max-width: 900px) {
  .portal-main {
    grid-template-columns: 1fr;
  }

  .portal-hero {
    height: 260px;
    margin: 16px 16px 0;
    border-radius: 12px;
  }

  .hero-content {
    padding: 24px;
  }

  .hero-title {
    font-size: 20px;
  }

  .topbar-inner {
    padding: 0 16px;
    gap: 12px;
  }

  .topbar-search {
    max-width: 200px;
  }

  .portal-categories {
    padding: 0 16px;
  }

  .portal-main {
    padding: 0 16px;
    margin-top: 20px;
  }
}

@media (max-width: 640px) {
  .topbar-date {
    display: none;
  }

  .topbar-search {
    max-width: none;
  }

  .portal-hero {
    height: 200px;
  }

  .hero-title {
    font-size: 17px;
  }

  .hero-summary {
    display: none;
  }

  .news-item {
    flex-direction: column;
    gap: 12px;
  }

  .item-thumb {
    width: 100%;
    height: 160px;
  }

  .item-title {
    font-size: 15px;
  }
}
</style>
