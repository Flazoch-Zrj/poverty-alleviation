<template>
  <div class="news-detail">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
      返回列表
    </button>

    <!-- 标题区 -->
    <article class="detail-article">
      <div class="article-header">
        <span class="article-tag" :class="'tag--' + detail.type">{{ typeLabel(detail.type) }}</span>
        <h1 class="article-title">{{ detail.title }}</h1>
        <div class="article-meta">
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            {{ detail.createTime || '未知日期' }}
          </span>
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
            来源：志愿服务联动帮扶综合服务平台
          </span>
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ detail.viewCount || 0 }} 次阅读
          </span>
        </div>
      </div>

      <!-- 附件下载区（红头文件） -->
      <div class="attachment-area" v-if="detail.fileUrl">
        <div class="attachment-header">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--p-color-primary)" stroke-width="2" stroke-linecap="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
          <span>附件下载</span>
        </div>
        <div class="attachment-body">
          <div class="file-icon">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#DC2626" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><text x="6" y="18" font-size="7" fill="#DC2626" font-weight="bold">PDF</text></svg>
          </div>
          <div class="file-info">
            <span class="file-name">{{ detail.fileName }}</span>
            <span class="file-size">{{ detail.fileSize }}</span>
          </div>
          <div class="file-actions">
            <a :href="detail.fileUrl" target="_blank" class="file-btn file-btn--view">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              在线阅读
            </a>
            <a :href="detail.fileUrl" download class="file-btn file-btn--download">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              下载
            </a>
          </div>
        </div>
      </div>

      <!-- 正文 -->
      <div class="article-body" v-html="detail.content || mockContent"></div>

      <!-- 上一篇 / 下一篇 -->
      <div class="article-nav">
        <button class="nav-btn nav-prev" :disabled="!prevItem" @click="goTo(prevItem)">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="15 18 9 12 15 6"/></svg>
          <span class="nav-label">上一篇</span>
          <span class="nav-title" v-if="prevItem">{{ prevItem.title }}</span>
        </button>
        <div class="nav-spacer"></div>
        <button class="nav-btn nav-next" :disabled="!nextItem" @click="goTo(nextItem)">
          <span class="nav-label">下一篇</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>
          <span class="nav-title" v-if="nextItem">{{ nextItem.title }}</span>
        </button>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsPage, getNewsById } from '@/api/news'
import type { NewsItem } from '@/api/news'

const route = useRoute()
const router = useRouter()


// 从 query 获取文章内容（由 NewsList 传递）
const detail = reactive<NewsItem>({
  id: Number(route.params.id) || 0,
  title: (route.query.title as string) || '资讯详情',
  type: (route.query.type as string) || '',
  content: (route.query.content as string) || '',
  createTime: (route.query.createTime as string) || '',
  viewCount: Number(route.query.viewCount) || 0,
  fileUrl: (route.query.fileUrl as string) || '',
  fileName: (route.query.fileName as string) || '',
  fileSize: (route.query.fileSize as string) || '',
})

// 当类型为中央文件且无 fileUrl 时，使用演示 PDF
if (!detail.fileUrl && detail.type === '5') {
  detail.fileUrl = '/pdfs/关于开展2024年防止返贫监测帮扶集中排查工作的通知.pdf'
  detail.fileName = detail.fileName || '关于开展2024年防止返贫监测帮扶集中排查工作的通知.pdf'
  detail.fileSize = detail.fileSize || '2.3 MB'
}

const typeLabel = (type: string) => {
  const map: Record<string, string> = { '1': '政策新闻', '2': '脱贫案例', '3': '通知公告', '4': '捐赠公示', '5': '中央文件' }
  return map[type] || type || '其他'
}

// 模拟正文（当 API 返回正文为空时的后备内容）
const mockContent = `
  <p style="font-size:16px;line-height:1.9;color:#374151;margin-bottom:20px;">
    本文为志愿服务联动帮扶综合服务平台资讯示例内容。在实际项目中，此处将展示从后端 API 获取的完整文章正文，可能包含文字、图片、表格等多种富文本元素。
  </p>
  <p style="font-size:16px;line-height:1.9;color:#374151;margin-bottom:20px;">
    扶贫工作是一项长期而艰巨的任务，需要全社会共同努力。通过产业帮扶、教育助学、健康医疗、危房改造等多种措施，我们正在帮助越来越多的困难群众摆脱贫困，走向共同富裕。
  </p>
  <figure style="margin:24px 0;text-align:center;">
    <img src="/images/特色农产品丰收.jpg" alt="扶贫成果" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
    <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">特色农产品丰收景象</figcaption>
  </figure>
  <p style="font-size:16px;line-height:1.9;color:#374151;margin-bottom:20px;">
    乡村振兴战略为新时代脱贫攻坚工作指明了方向。我们要以更加坚定的决心、更加有力的举措，确保脱贫成果得到巩固，让脱贫群众生活更上一层楼。
  </p>
`

// 上一篇/下一篇（从 API 获取列表后定位）
const allNews = ref<NewsItem[]>([])
const currentIndex = computed(() => allNews.value.findIndex(n => n.id === detail.id))
const prevItem = computed(() => currentIndex.value > 0 ? allNews.value[currentIndex.value - 1] : null)
const nextItem = computed(() => currentIndex.value < allNews.value.length - 1 ? allNews.value[currentIndex.value + 1] : null)

const goTo = (item: NewsItem | null) => {
  if (!item) return
  router.push({ path: `/news/${item.id}`, query: { title: item.title, type: item.type, content: item.content, createTime: item.createTime, viewCount: String(item.viewCount), fileUrl: item.fileUrl || '', fileName: item.fileName || '', fileSize: item.fileSize || '' } })
}

const goBack = () => {
  router.push('/news')
}

onMounted(async () => {
  // 加载新闻列表用于上一篇/下一篇定位
  try {
    const res = await getNewsPage({ page: 1, size: 100 })
    allNews.value = res.data?.records || res.data?.list || []
  } catch {}
})
</script>

<style scoped lang="scss">
.news-detail {
  max-width: 820px;
  margin: 0 auto;
  padding: 32px 24px 64px;
  animation: fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ====== 返回按钮 ====== */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid #E5E7EB;
  border-radius: 10px;
  background: #fff;
  color: #6B7280;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  margin-bottom: 28px;

  &:hover {
    border-color: #2D7A9B;
    color: #2D7A9B;
    gap: 10px;
  }
}

/* ====== 文章主体 ====== */
.detail-article {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 16px;
  padding: 40px 44px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

/* 标题区 */
.article-header {
  margin-bottom: 32px;
  padding-bottom: 28px;
  border-bottom: 1px solid #F0F0F0;
}

.article-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  margin-bottom: 14px;

  &.tag--1 { background: #DBEAFE; color: #2563EB; }
  &.tag--2 { background: #D1FAE5; color: #059669; }
  &.tag--3 { background: #FEF3C7; color: #D97706; }
  &.tag--4 { background: #FEE2E2; color: #DC2626; }
  &.tag--5 { background: #FEE2E2; color: #991B1B; }
}

.article-title {
  font-size: 26px;
  font-weight: 800;
  color: #0A2647;
  margin: 0 0 16px;
  line-height: 1.35;
  letter-spacing: -0.01em;
}

.article-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #9CA3AF;
  font-variant-numeric: tabular-nums;
}

/* ====== 附件下载区 ====== */
.attachment-area {
  margin-bottom: 28px;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  overflow: hidden;
  background: #FAFBFC;
}

.attachment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px;
  background: #F0F4FF;
  border-bottom: 1px solid #E5E7EB;
  font-size: 13px;
  font-weight: 600;
  color: var(--p-color-primary);
}

.attachment-body {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 18px;
}

.file-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FEF2F2;
  border-radius: 10px;
  flex-shrink: 0;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.file-name {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.file-size {
  font-size: 12px;
  color: #9CA3AF;
  margin-top: 2px;
}

.file-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.file-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;

  &--view {
    background: #0A2647;
    color: #fff;
    border: none;

    &:hover {
      background: #1A3A5C;
    }
  }

  &--download {
    background: #fff;
    color: #374151;
    border: 1px solid #E5E7EB;

    &:hover {
      border-color: #2D7A9B;
      color: #2D7A9B;
    }
  }
}

/* ====== 正文 ====== */
.article-body {
  font-size: 16px;
  line-height: 1.9;
  color: #374151;
  margin-bottom: 36px;

  :deep(p) {
    margin: 0 0 18px;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  }

  :deep(figure) {
    margin: 24px 0;
    text-align: center;
  }

  :deep(figcaption) {
    margin-top: 8px;
    font-size: 13px;
    color: #9CA3AF;
  }

  :deep(blockquote) {
    margin: 20px 0;
    padding: 16px 20px;
    border-left: 3px solid #2D7A9B;
    background: #F8FAFC;
    border-radius: 0 8px 8px 0;
    color: #6B7280;
    font-style: italic;
  }

  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 20px 0;

    th, td {
      padding: 10px 14px;
      border: 1px solid #E5E7EB;
      text-align: left;
    }
    th {
      background: #F9FAFB;
      font-weight: 600;
    }
  }
}

/* ====== 上一篇/下一篇导航 ====== */
.article-nav {
  display: flex;
  gap: 16px;
  border-top: 1px solid #F0F0F0;
  padding-top: 24px;
}

.nav-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 20px;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  background: #F9FAFB;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  align-items: flex-start;
  min-height: 72px;

  &:hover:not(:disabled) {
    border-color: #2D7A9B;
    background: #F0F7FF;
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  &:first-child {
    align-items: flex-start;
  }
}

.nav-next {
  align-items: flex-end;
  text-align: right;
}

.nav-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #9CA3AF;
  font-weight: 500;
}

.nav-title {
  font-size: 14px;
  font-weight: 600;
  color: #0A2647;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.nav-spacer {
  flex: 0;
}

/* ====== 响应式 ====== */
@media (max-width: 768px) {
  .news-detail {
    padding: 20px 16px 40px;
  }

  .detail-article {
    padding: 24px 20px;
    border-radius: 12px;
  }

  .article-title {
    font-size: 20px;
  }

  .article-meta {
    gap: 12px;
    flex-direction: column;
  }

  .article-nav {
    flex-direction: column;
  }

  .nav-btn {
    min-height: auto;
    padding: 14px 16px;
  }
}
</style>
