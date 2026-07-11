import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '工作台', roles: ['admin', 'cadre', 'volunteer', 'poor'] }
      },
      // 管理员模块
      { path: 'admin/user', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue'), meta: { title: '用户管理', roles: ['admin'] } },
      { path: 'admin/role', name: 'RoleManage', component: () => import('@/views/admin/RoleManage.vue'), meta: { title: '角色管理', roles: ['admin'] } },
      { path: 'admin/dict', name: 'DictManage', component: () => import('@/views/admin/DictManage.vue'), meta: { title: '数据字典', roles: ['admin'] } },
      // 帮扶责任人模块
      { path: 'cadre/dashboard', name: 'CadreDashboard', component: () => import('@/views/cadre/CadreDashboard.vue'), meta: { title: '工作台', roles: ['cadre', 'admin'] } },
      { path: 'cadre/family', name: 'FamilyManage', component: () => import('@/views/cadre/FamilyManage.vue'), meta: { title: '贫困户档案', roles: ['cadre', 'admin'] } },
      { path: 'cadre/family/:familyId', name: 'FamilyDetail', component: () => import('@/views/cadre/FamilyDetail.vue'), meta: { title: '家庭详情', roles: ['cadre', 'admin'] } },
      { path: 'cadre/visit', name: 'VisitRecord', component: () => import('@/views/cadre/VisitRecord.vue'), meta: { title: '走访记录', roles: ['cadre', 'admin'] } },
      { path: 'cadre/measure', name: 'MeasureManage', component: () => import('@/views/cadre/MeasureManage.vue'), meta: { title: '帮扶措施', roles: ['cadre', 'admin'] } },
      { path: 'cadre/project', name: 'ProjectManage', component: () => import('@/views/cadre/ProjectManage.vue'), meta: { title: '项目管理', roles: ['cadre', 'admin'] } },
      { path: 'cadre/job', name: 'JobManage', component: () => import('@/views/cadre/JobManage.vue'), meta: { title: '就业管理', roles: ['cadre', 'admin'] } },
      { path: 'cadre/need', name: 'NeedsManage', component: () => import('@/views/cadre/NeedsManage.vue'), meta: { title: '需求管理', roles: ['cadre', 'admin'] } },
      { path: 'cadre/training', name: 'TrainingManage', component: () => import('@/views/cadre/TrainingManage.vue'), meta: { title: '培训管理', roles: ['cadre', 'admin'] } },
      { path: 'cadre/map', name: 'FamilyMap', component: () => import('@/views/cadre/FamilyMap.vue'), meta: { title: '家庭地图', roles: ['cadre', 'admin'] } },
      { path: 'cadre/risk', name: 'RiskDashboard', component: () => import('@/views/cadre/RiskDashboard.vue'), meta: { title: '防返贫预警', roles: ['cadre', 'admin'] } },
      // 贫困户模块
      { path: 'poor/profile', name: 'PoorProfile', component: () => import('@/views/poor/PoorProfile.vue'), meta: { title: '我的档案', roles: ['poor'] } },
      { path: 'poor/need', name: 'PoorNeed', component: () => import('@/views/poor/PoorNeed.vue'), meta: { title: '我的需求', roles: ['poor'] } },
      { path: 'poor/job', name: 'PoorJob', component: () => import('@/views/poor/PoorJob.vue'), meta: { title: '就业申请', roles: ['poor'] } },
      // 志愿者模块
      { path: 'volunteer/dashboard', name: 'VolunteerDashboard', component: () => import('@/views/volunteer/VolunteerDashboard.vue'), meta: { title: '工作台', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/pairing', name: 'VolunteerPairing', component: () => import('@/views/volunteer/VolunteerPairing.vue'), meta: { title: '结对家庭', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/activity', name: 'VolunteerActivity', component: () => import('@/views/volunteer/VolunteerActivity.vue'), meta: { title: '志愿活动', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/score', name: 'VolunteerScore', component: () => import('@/views/volunteer/ScoreCenter.vue'), meta: { title: '我的积分', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/certificate', name: 'VolunteerCert', component: () => import('@/views/volunteer/CertificateCenter.vue'), meta: { title: '我的证书', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/ranking', name: 'VolunteerRanking', component: () => import('@/views/volunteer/RankingBoard.vue'), meta: { title: '志愿者排行', roles: ['volunteer', 'admin'] } },
      { path: 'volunteer/donation', name: 'DonationManage', component: () => import('@/views/volunteer/DonationManage.vue'), meta: { title: '捐赠管理', roles: ['volunteer', 'admin'] } },
      // 公共模块
      { path: 'news', name: 'NewsList', component: () => import('@/views/poor/NewsList.vue'), meta: { title: '信息公开' } },
      { path: 'training', name: 'TrainingList', component: () => import('@/views/poor/TrainingList.vue'), meta: { title: '技能培训' } },
      // 个人中心（各角色通用）
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/Profile.vue'), meta: { title: '个人中心' } },
      // 项目详情页
      { path: 'project-detail', name: 'ProjectDetail', component: () => import('@/views/project/ProjectDetail.vue'), meta: { title: '项目详情' } },
      // 数据大屏
      { path: 'admin/big-screen', name: 'BigScreen', component: () => import('@/views/admin/BigScreen.vue'), meta: { title: '数据驾驶舱', roles: ['admin'] } },
      // 新闻详情页
      { path: 'news/:id', name: 'NewsDetail', component: () => import('@/views/poor/NewsDetail.vue'), meta: { title: '新闻详情' } },
      // AI 助手
      { path: 'ai', name: 'AIChat', component: () => import('@/views/ai/AIChat.vue'), meta: { title: 'AI 助手', roles: ['admin', 'cadre', 'volunteer', 'poor'] } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    document.title = (to.meta.title as string) || '志愿服务联动帮扶综合服务平台'
    next()
  }
})

export default router
