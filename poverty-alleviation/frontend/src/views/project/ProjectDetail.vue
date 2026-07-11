<template>
  <div class="pd-page">
    <!-- 返回按钮 -->
    <button class="pd-back" @click="router.back()">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
      返回
    </button>

    <!-- 顶部标题区 -->
    <div class="pd-hero">
      <span class="pd-tag" :style="{ background: tagColor }">{{ project.tag }}</span>
      <h1 class="pd-title">{{ project.title }}</h1>
    </div>

    <!-- 三个板块 -->
    <div class="pd-body">

      <!-- 板块 1：项目背景 -->
      <section class="pd-section">
        <div class="section-header">
          <span class="section-number">01</span>
          <h2 class="section-title">项目背景</h2>
        </div>
        <div class="section-content with-image">
          <p class="section-text">{{ project.background }}</p>
          <div class="section-image">
            <img :src="localImage(project.images[0])" alt="项目背景图片" loading="lazy" />
            <span class="image-caption">{{ project.images[0] }}</span>
          </div>
        </div>
      </section>

      <!-- 板块 2：实施成效 -->
      <section class="pd-section">
        <div class="section-header">
          <span class="section-number">02</span>
          <h2 class="section-title">实施成效</h2>
        </div>
        <div class="section-content with-image reverse">
          <p class="section-text">{{ project.effect }}</p>
          <div class="section-image">
            <img :src="localImage(project.images[1])" alt="实施成效图片" loading="lazy" />
            <span class="image-caption">{{ project.images[1] }}</span>
          </div>
        </div>
      </section>

      <!-- 板块 3：典型案例 -->
      <section class="pd-section pd-section--case">
        <div class="section-header">
          <span class="section-number">03</span>
          <h2 class="section-title">典型案例</h2>
        </div>
        <div class="section-content">
          <div class="case-block">
            <div class="case-quote">
              <svg class="quote-mark" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 21c3 0 7-1 7-8V5c0-1.25-.756-2.017-2-2H4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 0 1 1v1c0 1-1 2-2 2s-1 .008-1 1.031V20c0 1 0 1 1 1z"/><path d="M15 21c3 0 7-1 7-8V5c0-1.25-.757-2.017-2-2h-4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 0 1 1v1c0 1-1 2-2 2s-1 .008-1 1.031V20c0 1 0 1 1 1z"/></svg>
              <p class="case-story">{{ project.case }}</p>
            </div>
          </div>
          <div class="section-image">
            <img :src="localImage(project.images[2])" alt="典型案例图片" loading="lazy" />
            <span class="image-caption">{{ project.images[2] }}</span>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// ============================================================
// 项目数据（与用户提供的 JSON 完全一致）
// ============================================================
const projects: Record<string, any> = {
  industry: {
    id: 'industry',
    title: '产业帮扶项目',
    tag: '产业',
    background: '项目所在地区农业产业基础薄弱，农户长期以传统种植为主，缺乏科学的种植技术和管理经验，产量和品质极不稳定。同时，优质种苗获取渠道有限、农业生产投入成本较高，加之农户对市场信息掌握不足，"好产品难卖好价钱"的问题长期存在。为切实帮助68户家庭突破产业发展瓶颈，项目立足本地资源禀赋，聚焦特色种植产业，通过"技术培训+种苗资助+保价回收"的组合帮扶模式，推动传统种植从粗放式向标准化转型。',
    effect: '项目累计为68户家庭提供特色种植技术培训与种苗资助。在技术层面，组织农技专家围绕种子处理、整地播种、田间管理、病虫害防治等核心环节开展系统培训，把专业农业技术转化为通俗易懂、简便易行的田间操作办法；在物资层面，统一供给优质种苗，企业推行标准化种植管理，从整地移栽到田间管护全程免费开展技术指导。在销售保障方面，依托订单保底收购模式，实行市场价与保护价双轨收购，彻底打消农户缺技术、愁销路、怕价低的顾虑。通过项目实施，68户家庭户均增收超3000元，有效拓宽了村民增收门路。',
    case: '农户张大姐的"致富账"：张大姐是项目首批受益农户之一，过去家中几亩地主要种植传统作物，年收入仅万元出头。去年，她参加了项目组织的特色种植技术培训，从选种、育苗到田间管理全程接受专家指导，并免费领取了优质种苗。收获后，企业按照保护价统一收购，刨除各项种植成本后，当年增收超过3500元。"以前种地全靠老天爷吃饭，凭老经验种地，收入很不稳定。现在帮我们引良种、教技术，种地终于有了底气和盼头。"如今，张大姐正计划扩大种植面积，带动更多乡亲一起发展特色种植。',
    images: ['农技专家田间培训', '农户领取种苗', '特色农产品丰收']
  },
  education: {
    id: 'education',
    title: '教育助学计划',
    tag: '教育',
    background: '项目所在地区部分家庭因经济困难，子女完成学业面临较大压力。一些品学兼优的学生因家庭收入有限，面临辍学风险。教育是阻断贫困代际传递的根本途径。为切实帮助困难家庭减轻教育负担，项目发起教育助学计划，通过发放助学金的方式，资助贫困学生顺利完成学业。',
    effect: '项目累计资助42名贫困学生完成学业，发放助学金累计21.6万元。根据学生所在学校消费水平实施差异化资助，确保每一名受助学生都能获得稳定的经济保障。项目实施以来，受助学生无一人因家庭经济原因辍学，部分品学兼优的毕业生还获得了继续深造的机会。项目的持续开展，不仅缓解了困难家庭的经济压力，更让受助学生感受到了社会的温暖与关爱。',
    case: '小李的求学之路：小李是项目首批受助学生之一，父母均在家务农，家庭收入微薄。考上高中后，学费和生活费让全家犯了难。正当一家人一筹莫展时，助学计划将他纳入资助范围，每学年提供助学金，帮助他顺利完成了高中学业。高考中，小李以优异成绩考入大学。"是这笔助学金让我没有因为家庭困难而掉队，我一定会好好学习，将来回报社会。"小李说。如今，他已进入大学深造，正朝着自己的梦想不断前行。',
    images: ['助学金发放仪式', '受助学生课堂学习', '录取通知书']
  },
  health: {
    id: 'health',
    title: '健康医疗行动',
    tag: '医疗',
    background: '项目所在地区部分行政村地处偏远，群众就医不便，优质医疗资源难以覆盖。一些常见病、慢性病因得不到及时诊治而延误，群众"看病难、看病远"的问题较为突出。为切实解决基层群众看病就医的实际困难，项目组织医疗专家团队深入乡村开展义诊活动，将优质医疗服务送到群众"家门口"。',
    effect: '项目累计组织3次下乡义诊活动，覆盖15个行政村，服务群众1200余人次。每次义诊均组织涵盖内科、外科、中医科、妇科等多个科室的医疗专家团队，携带B超、心电图等基础设备，为村民提供免费诊疗服务。专家们仔细为村民测量血压血糖、查看检查报告、详细询问身体状况，针对不同病症提出具体诊疗建议，并普及健康防护和日常养生等实用知识。通过义诊活动，许多群众的健康问题得到及时发现和指导，有效缓解了基层群众看病难的实际困难。',
    case: '王大爷的"心病"治好了：王大爷今年68岁，家住偏远山村，多年来一直被高血压和关节疼痛困扰，但由于村里没有卫生院、去县城路途遥远，一直拖着没有正规检查。义诊活动来到村里后，医疗专家为他进行了全面检查，调整了用药方案，并耐心讲解了日常饮食和锻炼的注意事项。"专家大老远跑到村里来给我们看病，真是太感谢了！"王大爷激动地说。如今，王大爷按照医嘱定期服药、注意饮食，身体状况明显改善。',
    images: ['专家义诊场景', '村民排队等候', '测量血压特写']
  },
  housing: {
    id: 'housing',
    title: '危房改造工程',
    tag: '住房',
    background: '项目所在地区部分农户住房年久失修，存在严重安全隐患。经专业鉴定，28户房屋达到D级危房标准（整体危险，需拆除重建），36户达到C级危房标准（局部危险，需修缮加固）。这些危房不仅影响群众基本居住安全，也制约着群众对美好生活的向往。为切实保障群众住房安全，项目启动危房改造工程，通过精准摸排、分类施策，帮助困难群众实现"忧居"变"优居"。',
    effect: '项目累计完成28户D级危房重建、36户C级危房修缮，全面保障了64户家庭的居住安全。在实施过程中，通过"农户申报+村级排查+专业鉴定"三级联动机制精准摸排房屋状况，对符合条件的及时纳入改造范围，做到"应纳尽纳"。重建房屋严格按照安全标准建设，修缮房屋全面消除结构安全隐患。曾经住了几十年的老旧房屋被崭新的平房、楼房取代，群众的居住条件得到根本改善，安全感和幸福感显著提升。',
    case: '赵大叔搬进"安心房"：赵大叔家的老屋已有40多年历史，墙体开裂、屋顶漏雨，被鉴定为D级危房。由于家庭经济困难，翻建新房一度是遥不可及的梦想。危房改造工程启动后，赵大叔家被纳入首批重建名单，在政策支持下，一栋安全敞亮的新房拔地而起。"住了几十年的危房，做梦都没想到能住上这么好的新房子！"赵大叔眼眶湿润。如今，赵大叔一家住进了安全舒适的"安心房"，日子越过越有盼头。',
    images: ['改造前后对比', '新建房屋外观', '农户在新房前合影']
  },
  employment: {
    id: 'employment',
    title: '就业技能培训',
    tag: '就业',
    background: '项目所在地区部分劳动力因缺乏专业技能，就业渠道狭窄、收入水平偏低。一方面，传统岗位竞争激烈；另一方面，电工、电商运营等技能型岗位却人才紧缺，劳动者技能与产业需求脱节的问题较为突出。为帮助群众掌握一技之长、实现稳定就业，项目整合优质培训资源，开展电工、家政、电商等多工种职业技能培训。',
    effect: '项目累计开展电工、家政、电商等6期技能培训班，帮助53人实现稳定就业。培训采用"理论+实操"相结合的教学模式，大幅降低学习门槛。对培训成本较高的项目积极争取政策补贴，努力让更多困难群众迈过技能学习的"门槛"。培训后通过后续就业帮扶，帮助学员对接用工企业。参训学员掌握实用技能后，就业竞争力和收入水平均得到显著提升。',
    case: '宝妈小刘的"逆袭"：小刘是一名全职宝妈，因照顾孩子多年未参加工作，家庭经济压力较大。得知村里开办电商技能培训班后，她毫不犹豫地报了名。经过系统培训，小刘掌握了电商运营、直播带货等实用技能。培训结束后，在项目帮扶下她成功对接了一家本地农产品电商企业，如今已稳定就业，月收入达到4000多元。"没想到在家门口学到了真本事，既能顾家又能挣钱，日子越来越有奔头了。"小刘笑着说。',
    images: ['培训班上课现场', '学员实操练习', '学员上岗工作']
  },
  finance: {
    id: 'finance',
    title: '小额金融贷款',
    tag: '金融',
    background: '项目所在地区部分农户有发展生产的意愿和计划，但缺乏启动资金。传统的信贷渠道门槛高、手续繁，农户"贷款难"问题较为突出。为帮助群众突破资金瓶颈、实现产业增收，项目联合金融机构推出小额金融贷款服务，为有产业发展需求的农户提供免息贷款支持。',
    effect: '项目联合农商行为45户发放免息贷款共计180万元，户均贷款4万元，全部用于产业发展。贷款办理流程简便快捷，金融机构主动将服务送到田间地头，利用农闲时间带着政策走进农户院落。资金到位后，农户用于扩大种植规模、购置生产设备、发展特色养殖等产业项目，有效解决了发展生产的资金难题。通过金融"活水"的精准滴灌，农户的产业发展能力和增收致富信心显著增强。',
    case: '老周的"创业梦"：老周一直想扩大自家的特色种植规模，但苦于没有启动资金，计划一拖再拖。小额金融贷款项目启动后，他第一时间申请了5万元免息贷款，用于购置种苗和农资、扩大种植面积。"以前想扩大生产但缺钱，银行贷款门槛高、利息重，想都不敢想。现在有了免息贷款，心里踏实多了！"老周充满干劲。在资金支持下，老周的种植规模扩大了一倍，预计当年收入可实现翻番。',
    images: ['办理贷款手续', '投入生产场景', '政策宣讲活动']
  }
}

// 通过路由查询参数 title 查找项目
const titleKey = computed(() => {
  const t = (route.query.title as string) || ''
  const map: Record<string, string> = {
    '产业帮扶项目': 'industry',
    '教育助学计划': 'education',
    '健康医疗行动': 'health',
    '危房改造工程': 'housing',
    '就业技能培训': 'employment',
    '小额金融贷款': 'finance'
  }
  return map[t] || 'industry'
})

const project = computed(() => projects[titleKey.value] || projects.industry)

const tagColor = computed(() => {
  const map: Record<string, string> = {
    '产业': '#10B981',
    '教育': '#2563EB',
    '医疗': '#EF4444',
    '住房': '#F59E0B',
    '就业': '#6B7280',
    '金融': '#8B5CF6'
  }
  return map[project.value.tag] || '#6B7280'
})

// 根据图片提示词返回本地图片路径（支持全部6个项目）
const localImage = (caption: string) => {
  // 文件名与提示词不完全一致的映射
  const aliasMap: Record<string, string> = {
    '新建房屋外观': '乡村新建房屋外观',
  }
  const actualName = aliasMap[caption] || caption
  const extMap: Record<string, string> = {
    '农技专家田间培训': 'jpg',
    '农户领取种苗': 'jpg',
    '特色农产品丰收': 'jpg',
    '助学金发放仪式': 'png',
    '受助学生课堂学习': 'png',
    '录取通知书': 'png',
    '专家义诊场景': 'png',
    '村民排队等候': 'png',
    '测量血压特写': 'png',
    '改造前后对比': 'png',
    '新建房屋外观': 'png',
    '乡村新建房屋外观': 'png',
    '农户在新房前合影': 'png',
    '培训班上课现场': 'png',
    '学员实操练习': 'png',
    '学员上岗工作': 'png',
    '办理贷款手续': 'png',
    '投入生产场景': 'png',
    '政策宣讲活动': 'png',
  }
  const ext = extMap[actualName] || extMap[caption] || 'jpg'
  return `/images/${actualName}.${ext}`
}
</script>

<style scoped lang="scss">
/* ============================================================
   帮扶项目详情页 — 三板块布局
   配色：深科技蓝 #0A2647 / 亮青蓝 #2D7A9B / 暖橙 #F9A826
   ============================================================ */

.pd-page {
  max-width: 1060px;
  margin: 0 auto;
  padding: 32px 28px 64px;
  animation: pdEnter 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes pdEnter {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ====== 返回按钮 ====== */
.pd-back {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid var(--p-border-color);
  border-radius: 8px;
  background: var(--p-bg-card);
  color: var(--p-text-regular);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  margin-bottom: 28px;

  &:hover {
    border-color: var(--p-color-primary);
    color: var(--p-color-primary);
    gap: 10px;
  }
}

/* ====== 顶部标题区 ====== */
.pd-hero {
  margin-bottom: 36px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--p-border-color);
}

.pd-tag {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 6px;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  margin-bottom: 12px;
}

.pd-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--p-text-primary);
  margin: 0;
  letter-spacing: -0.01em;
}

/* ====== 主体内容 ====== */
.pd-body {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

/* ====== 板块卡片 ====== */
.pd-section {
  background: var(--p-bg-card);
  border: 1px solid var(--p-border-color);
  border-radius: var(--p-radius-lg);
  padding: 28px 32px;
  box-shadow: var(--p-shadow-sm);
  transition: box-shadow 0.25s;

  &:hover {
    box-shadow: var(--p-shadow-md);
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--p-border-color);
}

.section-number {
  font-size: 20px;
  font-weight: 800;
  color: var(--p-color-primary);
  opacity: 0.3;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--p-text-primary);
  margin: 0;
}

.section-content {
  font-size: 14.5px;
  color: var(--p-text-regular);
  line-height: 1.85;
}

/* 带图片的布局（右侧图片） */
.with-image {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 28px;
  align-items: start;

  &.reverse {
    direction: rtl;

    .section-text {
      direction: ltr;
    }
    .section-image {
      direction: ltr;
    }
  }

  .section-text { margin: 0; }
}

.section-image {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: sticky;
  top: 80px;

  img {
    width: 100%;
    aspect-ratio: 4 / 3;
    object-fit: cover;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    transition: transform 0.3s;

    &:hover {
      transform: scale(1.02);
    }
  }
}

.image-caption {
  font-size: 12px;
  color: var(--p-text-muted);
  text-align: center;
  letter-spacing: 0.03em;
}

/* 典型案例特殊布局 */
.section-content:not(.with-image) {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 28px;
  align-items: start;
}

.pd-section--case {
  .section-content {
    display: grid;
    grid-template-columns: 1fr 280px;
    gap: 28px;
    align-items: start;
  }

  .case-block {
    position: relative;
  }

  .case-quote {
    position: relative;
    padding-left: 20px;
    border-left: 3px solid #2D7A9B;
  }

  .quote-mark {
    position: absolute;
    top: -8px;
    left: -28px;
    color: #2D7A9B;
    opacity: 0.15;
  }

  .case-story {
    margin: 0;
    font-size: 14.5px;
    color: var(--p-text-regular);
    line-height: 1.85;
  }
}

/* ====== 响应式 ====== */
@media (max-width: 900px) {
  .with-image,
  .section-content:not(.with-image),
  .pd-section--case .section-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .with-image.reverse {
    direction: ltr;
  }

  .section-image {
    position: static;
    max-width: 400px;

    img {
      aspect-ratio: 16 / 9;
    }
  }

  .pd-page {
    padding: 20px 16px 40px;
  }

  .pd-title {
    font-size: 22px;
  }

  .pd-section {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .pd-hero {
    margin-bottom: 24px;
  }

  .pd-title {
    font-size: 20px;
  }

  .section-title {
    font-size: 16px;
  }
}
</style>
