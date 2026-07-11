<template>
  <div class="layout-wrapper">
    <!-- ====== 左侧边栏 ====== -->
    <aside class="sidebar" :class="{ 'is-collapse': isCollapse }">
      <!-- Logo -->
      <div class="sidebar-logo" @click="router.push('/dashboard')">
        <div class="logo-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M4 20L12 4L20 20H4Z" fill="#60A5FA" opacity="0.9"/>
            <path d="M8 16L12 8L16 16H8Z" fill="#93C5FD" opacity="0.7"/>
            <circle cx="12" cy="12" r="2" fill="#1E40AF"/>
          </svg>
        </div>
        <span class="logo-text" v-show="!isCollapse">帮扶平台</span>
      </div>

      <!-- 菜单 -->
      <el-scrollbar class="sidebar-menu">
        <div v-for="item in navItems" :key="item.key">
          <!-- 有子菜单 -->
          <div v-if="item.children && hasAccess(item)">
            <div class="menu-group-title" v-show="!isCollapse">{{ item.label }}</div>
            <div
              v-for="child in item.children"
              :key="child.path"
              class="menu-item"
              :class="{ 'is-active': route.path === child.path }"
              @click="router.push(child.path)"
            >
              <el-tooltip :content="child.label" placement="right" :disabled="!isCollapse">
                <span class="menu-icon"><component :is="item.icon" /></span>
              </el-tooltip>
              <span class="menu-label" v-show="!isCollapse">{{ child.label }}</span>
            </div>
          </div>
          <!-- 无子菜单 -->
          <div
            v-else-if="hasAccess(item)"
            class="menu-item"
            :class="{ 'is-active': route.path === item.path }"
            @click="router.push(item.path!)"
          >
            <el-tooltip :content="item.label" placement="right" :disabled="!isCollapse">
              <span class="menu-icon"><component :is="item.icon" /></span>
            </el-tooltip>
            <span class="menu-label" v-show="!isCollapse">{{ item.label }}</span>
          </div>
        </div>
      </el-scrollbar>

      <!-- 折叠按钮 -->
      <div class="sidebar-footer" @click="isCollapse = !isCollapse">
        <el-icon :size="18" :style="{ transform: isCollapse ? 'rotate(180deg)' : '' }"><Fold /></el-icon>
        <span v-show="!isCollapse">收起侧栏</span>
      </div>
    </aside>

    <!-- ====== 右侧内容区 ====== -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentParentLabel">{{ currentParentLabel }}</el-breadcrumb-item>
            <el-breadcrumb-item v-if="!isParentPage">{{ route.meta?.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <div class="topbar-time">
            <el-icon :size="14"><Clock /></el-icon>
            <span>{{ currentTime }}</span>
          </div>
          <el-dropdown trigger="click" @command="handleNotifCmd">
            <el-badge :value="unreadCount" :hidden="unreadCount===0" class="notif-badge">
              <el-icon :size="20" style="cursor:pointer;color:#64748B;"><Bell /></el-icon>
            </el-badge>
            <template #dropdown>
              <el-dropdown-menu style="width:320px;">
                <el-dropdown-item v-if="notifList.length===0" disabled style="color:#94A3B8;font-size:12px;">暂无通知</el-dropdown-item>
                <el-dropdown-item v-for="n in notifList" :key="n.notifId" :command="'read-'+n.notifId" :class="{'notif-unread':!n.isRead}">
                  <div style="display:flex;flex-direction:column;gap:2px;max-width:280px;">
                    <span style="font-size:12px;font-weight:500;">{{ n.title }}</span>
                    <span style="font-size:11px;color:#94A3B8;">{{ n.content || '' }}</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item v-if="unreadCount>0" divided command="read-all" style="text-align:center;color:#2D7A9B;font-size:12px;">全部标记已读</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown @command="handleCommand">
            <span class="topbar-user">
              <el-avatar :size="30" class="user-avatar">{{ userStore.userInfo.realName?.charAt(0) || '管' }}</el-avatar>
              <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon> 个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 子页面标签 -->
      <div class="sub-tabs" v-if="currentChildren.length > 0">
        <div
          v-for="child in currentChildren" :key="child.path"
          class="sub-tab" :class="{ 'is-active': route.path === child.path }"
          @click="router.push(child.path)"
        >{{ child.label }}</div>
      </div>

      <!-- 主内容 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount, getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/system'
import {
  Odometer, Setting, UserFilled, HomeFilled, Help,
  Reading, Notebook, Bell, CaretBottom, User, SwitchButton,
  Clock, Fold, Promotion
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

interface NavChild { path: string; label: string }
interface NavItem { key: string; label: string; icon: any; path?: string; roles?: string[]; children?: NavChild[] }

const navItems = computed<NavItem[]>(() => [
  { key: 'dashboard', label: '工作台', icon: Odometer, path: '/dashboard', roles: ['admin', 'cadre', 'volunteer', 'poor'] },
  { key: 'admin',   label: '系统管理', icon: Setting,    roles: ['admin'], children: [{ path: '/admin/user', label: '用户管理' }, { path: '/admin/role', label: '角色管理' }, { path: '/admin/dict', label: '数据字典' }, { path: '/admin/big-screen', label: '数据驾驶舱' }] },
  { key: 'cadre',   label: '帮扶管理', icon: UserFilled, roles: ['cadre', 'admin'], children: [{ path: '/cadre/dashboard', label: '工作台' }, { path: '/cadre/family', label: '贫困户档案' }, { path: '/cadre/visit', label: '走访记录' }, { path: '/cadre/measure', label: '帮扶措施' }, { path: '/cadre/project', label: '项目管理' }, { path: '/cadre/job', label: '就业管理' }, { path: '/cadre/need', label: '需求管理' }, { path: '/cadre/training', label: '培训管理' }, { path: '/cadre/map', label: '家庭地图' }, { path: '/cadre/risk', label: '防返贫预警' }] },
  { key: 'poor',    label: '我的服务', icon: HomeFilled, roles: ['poor'], children: [{ path: '/poor/profile', label: '我的档案' }, { path: '/poor/need', label: '我的需求' }, { path: '/poor/job', label: '就业申请' }] },
  { key: 'volunteer', label: '志愿服务', icon: Help, roles: ['volunteer', 'admin'], children: [{ path: '/volunteer/dashboard', label: '工作台' }, { path: '/volunteer/pairing', label: '结对家庭' }, { path: '/volunteer/activity', label: '志愿活动' }, { path: '/volunteer/score', label: '我的积分' }, { path: '/volunteer/certificate', label: '我的证书' }, { path: '/volunteer/ranking', label: '志愿者排行' }, { path: '/volunteer/donation', label: '捐赠管理' }] },
  { key: 'news',     label: '信息公开', icon: Reading, path: '/news', roles: ['admin', 'cadre', 'volunteer', 'poor'] },
  { key: 'training', label: '技能培训', icon: Notebook, path: '/training', roles: ['admin', 'cadre', 'volunteer', 'poor'] },
  { key: 'ai', label: 'AI 助手', icon: Promotion, path: '/ai', roles: ['admin', 'cadre', 'volunteer', 'poor'] },
])

const currentRole = computed(() => userStore.userInfo?.roleCode || '')
const hasAccess = (item: NavItem) => !item.roles || item.roles.includes(currentRole.value)

const currentParentKey = computed(() => {
  const p = route.path
  if (p === '/dashboard') return 'dashboard'
  if (p.startsWith('/admin')) return 'admin'
  if (p.startsWith('/cadre')) return 'cadre'
  if (p.startsWith('/poor')) return 'poor'
  if (p.startsWith('/volunteer')) return 'volunteer'
  if (p.startsWith('/news')) return 'news'
  if (p.startsWith('/training')) return 'training'
  if (p.startsWith('/ai')) return 'ai'
  return null
})
const currentParentLabel = computed(() => navItems.value.find(n => n.key === currentParentKey.value)?.label || '')
const isParentPage = computed(() => ['/dashboard', '/news', '/training', '/ai', '/volunteer/dashboard', '/volunteer/pairing', '/profile'].includes(route.path))
const currentChildren = computed(() => { const item = navItems.value.find(n => n.key === currentParentKey.value); return item?.children || [] })

const currentTime = ref('')
const unreadCount = ref(0)
const notifList = ref<any[]>([])
let timeInterval: ReturnType<typeof setInterval> | null = null
const updateTime = () => { const n = new Date(); currentTime.value = `${String(n.getHours()).padStart(2,'0')}:${String(n.getMinutes()).padStart(2,'0')}` }

const fetchNotifications = async () => {
  try { const r = await getUnreadCount(); unreadCount.value = r.data?.count || 0 } catch {}
  try { const r = await getNotifications({ limit: 5 }); notifList.value = r.data || [] } catch {}
}

const handleNotifCmd = async (cmd: string) => {
  if (cmd === 'read-all') { await markAllNotificationsRead(); fetchNotifications() }
  else if (cmd.startsWith('read-')) { await markNotificationRead(Number(cmd.replace('read-',''))); fetchNotifications() }
}

onMounted(() => { userStore.loadUserInfo(); updateTime(); timeInterval = setInterval(updateTime, 60000); fetchNotifications() })
onUnmounted(() => { if (timeInterval) clearInterval(timeInterval) })

const handleCommand = (cmd: string) => {
  if (cmd === 'logout') { userStore.logout(); router.push('/login') }
  else if (cmd === 'profile') router.push('/profile')
}
</script>

<style scoped lang="scss">
.layout-wrapper {
  height: 100vh;
  display: flex;
  background: #F8FAFC;
}

/* ====== 侧边栏 ====== */
.sidebar {
  width: 220px;
  background: #0A2647;
  display: flex;
  flex-direction: column;
  transition: width 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  flex-shrink: 0;
  overflow: hidden;

  &.is-collapse {
    width: 64px;
  }

  .sidebar-logo {
    height: 60px;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 16px;
    cursor: pointer;
    flex-shrink: 0;
    border-bottom: 1px solid rgba(255,255,255,0.06);

    .logo-icon {
      width: 34px;
      height: 34px;
      background: linear-gradient(135deg, #2D7A9B, #60A5FA);
      border-radius: 9px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .logo-text {
      font-size: 16px;
      font-weight: 700;
      color: #fff;
      white-space: nowrap;
    }
  }

  .sidebar-menu {
    flex: 1;
    padding: 8px 0;
  }

  .menu-group-title {
    font-size: 10px;
    font-weight: 600;
    color: rgba(255,255,255,0.35);
    text-transform: uppercase;
    letter-spacing: 1px;
    padding: 16px 16px 6px;
    white-space: nowrap;
  }

  .menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 16px;
    margin: 1px 8px;
    border-radius: 8px;
    color: rgba(255,255,255,0.6);
    font-size: 13.5px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.15s;
    white-space: nowrap;

    .menu-icon { display: flex; align-items: center; font-size: 18px; flex-shrink: 0; width: 20px; justify-content: center; }
    .menu-label { overflow: hidden; text-overflow: ellipsis; }

    &:hover { color: #fff; background: rgba(255,255,255,0.08); }
    &.is-active { color: #fff; background: rgba(45,122,155,0.35); .menu-icon { color: #60A5FA; } }
  }

  .sidebar-footer {
    height: 48px;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 16px;
    border-top: 1px solid rgba(255,255,255,0.06);
    color: rgba(255,255,255,0.4);
    font-size: 12px;
    cursor: pointer;
    transition: color 0.15s;
    flex-shrink: 0;
    &:hover { color: #fff; }
  }
}

/* ====== 右侧主区域 ====== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ====== 顶部栏 ====== */
.topbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #E2E8F0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;

  .topbar-left {
    :deep(.el-breadcrumb__inner) { color: #64748B; font-size: 13px; &.is-link { color: #1E293B; font-weight: 500; &:hover { color: #0A2647; } } }
    :deep(.el-breadcrumb__separator) { color: #94A3B8; }
  }

  .topbar-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .topbar-time { display: flex; align-items: center; gap: 5px; color: #94A3B8; font-size: 13px; font-variant-numeric: tabular-nums; }

    .topbar-user {
      display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 3px 10px 3px 3px; border-radius: 20px;
      transition: background 0.15s;
      &:hover { background: #F1F5F9; }
      .user-avatar { background: linear-gradient(135deg, #0A2647, #2D7A9B) !important; color: #fff !important; font-size: 12px; font-weight: 700; }
      .user-name { font-size: 13px; color: #1E293B; font-weight: 500; max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    }
  }
}

/* ====== 子页面标签 ====== */
.sub-tabs {
  height: 44px;
  background: #fff;
  border-bottom: 1px solid #E2E8F0;
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 0 24px;
  flex-shrink: 0;
  overflow-x: auto;

  .sub-tab {
    padding: 6px 14px;
    border-radius: 6px;
    color: #64748B;
    font-size: 12.5px;
    font-weight: 500;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.15s;
    &:hover { color: #1E293B; background: #F1F5F9; }
    &.is-active { color: #0A2647; background: #EFF6FF; font-weight: 600; }
  }
}

/* ====== 主内容 ====== */
.content {
  flex: 1;
  overflow-y: auto;
  background: #F8FAFC;
}
</style>
