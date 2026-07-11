<template>
  <div class="portal-dashboard">

    <!-- ====== Hero 英雄区 ====== -->
    <section class="hero" aria-label="扶贫工作台概览">
      <div class="hero-bg">
        <img
          src="https://picsum.photos/seed/village/1600/900"
          alt="乡村振兴"
          class="hero-image"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content">
        <div class="hero-badge">乡村振兴 · 共同富裕</div>
        <h1 class="hero-title">让爱精准抵达<br class="hero-break"/>助力乡村振兴</h1>
        <p class="hero-subtitle">志愿服务联动帮扶综合服务平台 · 数字化工作台</p>

        <div class="hero-metrics">
          <div
            class="metric-card"
            v-for="stat in statCards"
            :key="stat.key"
          >
            <div class="metric-icon" :style="{ background: stat.bgGradient }">
              <el-icon :size="20" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="metric-info">
              <span class="metric-value">{{ animatedValue(stat.key, stat.value) }}</span>
              <span class="metric-label">{{ stat.label }}</span>
            </div>
            <span class="metric-trend" v-if="stat.trend && stat.trend !== '-'" :class="{ 'trend-up': stat.trend.startsWith('+') }">
              {{ stat.trend }}
            </span>
          </div>
        </div>
      </div>

      <!-- 底部波浪 -->
      <div class="hero-wave" aria-hidden="true">
        <svg viewBox="0 0 1440 100" preserveAspectRatio="none">
          <path d="M0,50 C360,100 1080,0 1440,50 L1440,100 L0,100 Z" fill="#F8FAFC"/>
        </svg>
      </div>
    </section>

    <!-- ====== 图片卡片网格 ====== -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">精准帮扶项目</h2>
        <p class="section-desc">产业扶持 · 教育助学 · 医疗保障 · 住房改善</p>
      </div>

      <div class="card-grid">
        <article class="image-card" v-for="card in imageCards" :key="card.title">
          <div class="image-card__thumb">
            <img :src="card.image" :alt="card.title" loading="lazy" />
            <div class="image-card__tag">{{ card.tag }}</div>
          </div>
          <div class="image-card__body">
            <h3 class="image-card__title">{{ card.title }}</h3>
            <p class="image-card__desc">{{ card.desc }}</p>
            <a class="image-card__link" @click.prevent="onViewDetail(card)">
              <span>查看详情</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </a>
          </div>
        </article>
      </div>
    </section>

    <!-- ====== 视频专区 ====== -->
    <section class="section section-video">
      <div class="section-header">
        <h2 class="section-title">帮扶成果 · 温暖瞬间</h2>
        <p class="section-desc">每一个改变都值得被看见</p>
      </div>

      <div class="video-wrapper">
        <video
          controls
          muted
          autoplay
          loop
          playsinline
          poster="/images/特色农产品丰收.jpg"
        >
          <source src="/videos/帮扶成果 · 温暖瞬间.mp4" type="video/mp4" />
          您的浏览器不支持视频播放
        </video>
      </div>
    </section>

    <!-- ====== 时间轴（横向滚动） ====== -->
    <section class="section section-timeline">
      <div class="section-header">
        <h2 class="section-title">最近活动</h2>
        <p class="section-desc">实时动态 · 一目了然</p>
      </div>

      <div class="timeline-scroll">
        <div class="timeline-track" ref="timelineRef">
          <div
            class="timeline-item"
            v-for="(item, idx) in recentActivities"
            :key="idx"
            :style="{ '--idx': idx }"
          >
            <div class="timeline-dot" :class="{ 'dot-success': item.status === '成功', 'dot-fail': item.status !== '成功' }"></div>
            <div class="timeline-card">
              <div class="timeline-time">{{ item.time }}</div>
              <div class="timeline-user">{{ item.user }}</div>
              <div class="timeline-action">{{ item.action }}</div>
              <el-tag
                :type="item.status === '成功' ? 'success' : 'danger'"
                size="small"
                effect="plain"
              >
                {{ item.status }}
              </el-tag>
            </div>
          </div>
        </div>
        <!-- 滚动提示 -->
        <div class="timeline-hint" v-if="!timelineScrolled">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          <span>左右滑动查看更多</span>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { HomeFilled, ChatDotSquare, Link, BellFilled } from '@element-plus/icons-vue'
import { getStatistics } from '@/api/system'

const router = useRouter()

// ============================================================
// 统计数据
// ============================================================
interface StatCard {
  key: string
  label: string
  value: number | string
  bgGradient: string
  icon: string
  trend?: string
}

const statCards = ref<StatCard[]>([
  { key: 'totalFamilies', label: '总贫困户', value: 0, bgGradient: 'linear-gradient(135deg, #0A2647, #2D7A9B)', icon: 'HomeFilled', trend: '+12%' },
  { key: 'totalVisits',   label: '本月走访', value: 0, bgGradient: 'linear-gradient(135deg, #2D7A9B, #4ECDC4)', icon: 'ChatDotSquare', trend: '+8%' },
  { key: 'pairedFamilies',label: '已结对',   value: 0, bgGradient: 'linear-gradient(135deg, #0A2647, #2D7A9B)', icon: 'Link', trend: '+5%' },
  { key: 'pendingTasks',  label: '待办事项', value: 0, bgGradient: 'linear-gradient(135deg, #F9A826, #FACC15)', icon: 'BellFilled', trend: '-' },
])

// 数字滚动动画
const displayValues = ref<Record<string, string>>({})
const animatedValue = (key: string, target: number | string) => displayValues.value[key] ?? target

const animateNumbers = () => {
  for (const card of statCards.value) {
    const target = typeof card.value === 'number' ? card.value : parseInt(card.value as string) || 0
    if (target === 0) { displayValues.value[card.key] = '0'; continue }
    const duration = 1200, steps = 40
    const increment = target / steps
    let current = 0
    const timer = setInterval(() => {
      current += increment
      if (current >= target) {
        displayValues.value[card.key] = Math.round(target).toLocaleString()
        clearInterval(timer)
      } else {
        displayValues.value[card.key] = Math.round(current).toLocaleString()
      }
    }, duration / steps)
  }
}

const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    const data = res.data || res
    for (const card of statCards.value) {
      if (data[card.key] !== undefined) card.value = data[card.key]
    }
  } catch {
    statCards.value = [
      { key: 'totalFamilies', label: '总贫困户', value: 1280, bgGradient: 'linear-gradient(135deg, #0A2647, #2D7A9B)', icon: 'HomeFilled', trend: '+12%' },
      { key: 'totalVisits',   label: '本月走访', value: 86,   bgGradient: 'linear-gradient(135deg, #2D7A9B, #4ECDC4)', icon: 'ChatDotSquare', trend: '+8%' },
      { key: 'pairedFamilies',label: '已结对',   value: 976,  bgGradient: 'linear-gradient(135deg, #0A2647, #2D7A9B)', icon: 'Link', trend: '+5%' },
      { key: 'pendingTasks',  label: '待办事项', value: 12,   bgGradient: 'linear-gradient(135deg, #F9A826, #FACC15)', icon: 'BellFilled', trend: '-' },
    ]
  }
}

// ============================================================
// 图片卡片数据
// ============================================================
const imageCards = [
  {
    title: '产业帮扶项目',
    desc: '为68户家庭提供特色种植技术培训与种苗资助，户均增收超3000元。',
    image: '/images/农技专家田间培训.jpg',
    tag: '产业',
  },
  {
    title: '教育助学计划',
    desc: '资助42名贫困学生完成学业，发放助学金累计21.6万元。',
    image: '/images/助学金发放仪式.png',
    tag: '教育',
  },
  {
    title: '健康医疗行动',
    desc: '组织3次下乡义诊活动，覆盖15个行政村，服务群众1200余人次。',
    image: '/images/专家义诊场景.png',
    tag: '医疗',
  },
  {
    title: '危房改造工程',
    desc: '完成28户D级危房重建，36户C级危房修缮，保障居住安全。',
    image: '/images/乡村新建房屋外观.png',
    tag: '住房',
  },
  {
    title: '就业技能培训',
    desc: '开展电工、家政、电商等6期培训班，帮助53人实现稳定就业。',
    image: '/images/培训班上课现场.png',
    tag: '就业',
  },
  {
    title: '小额金融贷款',
    desc: '联合农商行为45户发放免息贷款共计180万元，用于产业发展。',
    image: '/images/办理贷款手续.png',
    tag: '金融',
  },
]

const onViewDetail = (card: any) => {
  // 路由跳转到项目详情页
  router.push({ path: '/project-detail', query: { title: card.title, desc: card.desc, tag: card.tag } })
}

// ============================================================
// 最近活动（横向时间轴）
// ============================================================
const recentActivities = [
  { time: '2025-01-15 09:30', user: '张三', action: '新增贫困家庭记录', status: '成功' },
  { time: '2025-01-15 10:15', user: '李四', action: '更新结对帮扶信息', status: '成功' },
  { time: '2025-01-14 16:40', user: '王五', action: '删除培训课程', status: '成功' },
  { time: '2025-01-14 14:20', user: '赵六', action: '导入捐赠数据', status: '成功' },
  { time: '2025-01-13 11:50', user: '管理员', action: '系统参数配置', status: '成功' },
  { time: '2025-01-13 09:10', user: '张三', action: '导出家庭统计报表', status: '成功' },
  { time: '2025-01-12 15:30', user: '李四', action: '重置用户密码', status: '失败' },
  { time: '2025-01-12 08:45', user: '王五', action: '批量导入家庭数据', status: '成功' },
]

const timelineRef = ref<HTMLDivElement | null>(null)
const timelineScrolled = ref(false)

const onTimelineScroll = () => {
  if (!timelineRef.value) return
  timelineScrolled.value = timelineRef.value.scrollLeft > 10
}

// ============================================================
// 生命周期
// ============================================================
onMounted(async () => {
  await loadStatistics()
  await nextTick()
  animateNumbers()
})

onMounted(() => {
  const el = timelineRef.value
  if (el) {
    el.addEventListener('scroll', onTimelineScroll)
  }
})

onUnmounted(() => {
  const el = timelineRef.value
  if (el) {
    el.removeEventListener('scroll', onTimelineScroll)
  }
})
</script>

<style scoped lang="scss">
/* ============================================================
   扶贫工作台 — 企业级多媒体门户风格
   主色: #0A2647 (深科技蓝)  辅色: #2D7A9B (亮青蓝)
   强调色: #F9A826 (暖橙)    背景: #F8FAFC
   ============================================================ */

.portal-dashboard {
  background: #F8FAFC;
  min-height: 100vh;
}

/* ====== Hero 英雄区 ====== */
.hero {
  position: relative;
  min-height: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: #fff;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(10, 38, 71, 0.92) 0%,
    rgba(10, 38, 71, 0.75) 40%,
    rgba(45, 122, 155, 0.6) 100%
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 60px 24px 80px;
  max-width: 1200px;
  width: 100%;
  animation: heroEnter 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes heroEnter {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-badge {
  display: inline-block;
  padding: 6px 18px;
  border-radius: 20px;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(8px);
  font-size: 12px;
  letter-spacing: 0.12em;
  border: 1px solid rgba(255,255,255,0.15);
  margin-bottom: 20px;
}

.hero-title {
  font-size: clamp(2rem, 4.5vw, 3.2rem);
  font-weight: 800;
  line-height: 1.2;
  margin: 0 0 12px;
  letter-spacing: -0.01em;
}

.hero-break {
  display: none;
  @media (max-width: 480px) { display: block; }
}

.hero-subtitle {
  font-size: 15px;
  color: rgba(255,255,255,0.7);
  margin: 0 0 40px;
  letter-spacing: 0.04em;
}

/* Hero 指标卡片 */
.hero-metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  max-width: 960px;
  margin: 0 auto;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 14px;
  padding: 18px 16px;
  text-align: left;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    background: rgba(255,255,255,0.14);
    transform: translateY(-2px);
  }

  // 顶部强调条
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 2.5px;
    background: linear-gradient(90deg, #F9A826, #FACC15);
    opacity: 0;
    transition: opacity 0.3s;
  }
  &:hover::before { opacity: 1; }
}

.metric-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}

.metric-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.metric-value {
  font-size: 22px;
  font-weight: 800;
  line-height: 1.1;
  color: #fff;
  font-variant-numeric: tabular-nums;
}

.metric-label {
  font-size: 11px;
  color: rgba(255,255,255,0.6);
  margin-top: 1px;
}

.metric-trend {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 6px;
  background: rgba(249, 168, 38, 0.2);
  color: #F9A826;
  flex-shrink: 0;
  align-self: flex-start;

  &.trend-up { color: #4ADE80; background: rgba(74, 222, 128, 0.15); }
}

/* 底部波浪 */
.hero-wave {
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 80px;
  z-index: 2;
  pointer-events: none;

  svg {
    width: 100%;
    height: 100%;
    display: block;
  }
}

/* ====== 通用区块 ====== */
.section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 26px;
  font-weight: 800;
  color: #0A2647;
  margin: 0 0 8px;
  letter-spacing: -0.01em;
}

.section-desc {
  font-size: 14px;
  color: #64748B;
  margin: 0;
}

/* ====== 图片卡片网格 ====== */
.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.image-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 32px rgba(10, 38, 71, 0.12), 0 4px 12px rgba(0,0,0,0.06);
  }

  &__thumb {
    position: relative;
    width: 100%;
    aspect-ratio: 3 / 2;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s cubic-bezier(0.16, 1, 0.3, 1);
    }

    .image-card:hover & img {
      transform: scale(1.06);
    }
  }

  &__tag {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 12px;
    border-radius: 6px;
    background: rgba(10, 38, 71, 0.85);
    backdrop-filter: blur(4px);
    color: #fff;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.04em;
  }

  &__body {
    padding: 18px 20px 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__title {
    font-size: 16px;
    font-weight: 700;
    color: #0A2647;
    margin: 0 0 8px;
  }

  &__desc {
    font-size: 13px;
    color: #64748B;
    line-height: 1.6;
    margin: 0 0 16px;
    flex: 1;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  &__link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    font-weight: 600;
    color: #2D7A9B;
    cursor: pointer;
    transition: color 0.2s;
    text-decoration: none;

    &:hover {
      color: #0A2647;
      gap: 10px;
    }

    svg { transition: transform 0.2s; }
    &:hover svg { transform: translateX(3px); }
  }
}

/* ====== 视频专区 ====== */
.section-video {
  background: linear-gradient(180deg, transparent 0%, #F1F5F9 50%, transparent 100%);
  max-width: 100%;
  padding: 56px 24px;
}

.video-wrapper {
  max-width: 900px;
  margin: 0 auto;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(10, 38, 71, 0.12);
  background: #000;

  video {
    width: 100%;
    display: block;
    aspect-ratio: 16 / 9;
    object-fit: cover;
  }
}

/* ====== 横向时间轴 ====== */
.section-timeline {
  padding-bottom: 80px;
}

.timeline-scroll {
  position: relative;
}

.timeline-track {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 16px 4px 20px;
  scroll-behavior: smooth;
  scrollbar-width: thin;
  scrollbar-color: #CBD5E1 transparent;

  &::-webkit-scrollbar { height: 6px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 3px; }

  // 左边线
  &::before {
    content: '';
    position: absolute;
    left: 20px;
    top: 24px;
    bottom: 24px;
    width: 2px;
    background: linear-gradient(180deg, #2D7A9B, #F9A826);
    border-radius: 1px;
    z-index: 0;

    @media (min-width: 768px) {
      left: 50%;
      transform: translateX(-50%);
    }
  }
}

.timeline-item {
  flex-shrink: 0;
  width: 240px;
  position: relative;
  animation: fadeUp 0.4s ease both;
  animation-delay: calc(var(--idx) * 0.06s);

  &:first-child {
    margin-left: 8px;
  }
}

.timeline-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  margin: 0 auto 12px;
  position: relative;
  z-index: 1;
  box-shadow: 0 0 0 3px #fff;

  &.dot-success { background: #10B981; }
  &.dot-fail { background: #EF4444; }
}

.timeline-card {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 14px 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: box-shadow 0.25s;

  &:hover {
    box-shadow: 0 4px 16px rgba(10, 38, 71, 0.08);
  }
}

.timeline-time {
  font-size: 11px;
  color: #94A3B8;
  margin-bottom: 6px;
  font-variant-numeric: tabular-nums;
}

.timeline-user {
  font-size: 13px;
  font-weight: 600;
  color: #0A2647;
  margin-bottom: 4px;
}

.timeline-action {
  font-size: 12px;
  color: #64748B;
  line-height: 1.5;
  margin-bottom: 8px;
}

.timeline-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  font-size: 12px;
  color: #94A3B8;
  animation: hintPulse 2s ease-in-out infinite;

  svg { animation: hintSwipe 1.5s ease-in-out infinite; }
}

@keyframes hintPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

@keyframes hintSwipe {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(5px); }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ====== 响应式 ====== */
@media (max-width: 1024px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-metrics {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .hero {
    min-height: auto;
  }

  .hero-content {
    padding: 40px 20px 60px;
  }

  .metric-value {
    font-size: 18px;
  }

  .section {
    padding: 40px 20px;
  }

  .section-title {
    font-size: 22px;
  }

  .card-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .timeline-item {
    width: 200px;
  }

  .timeline-track::before {
    left: 16px;
  }
}

@media (max-width: 480px) {
  .hero-metrics {
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }

  .metric-card {
    padding: 12px;
    gap: 8px;
  }

  .metric-icon {
    width: 34px;
    height: 34px;
  }

  .metric-value {
    font-size: 16px;
  }
}
</style>
