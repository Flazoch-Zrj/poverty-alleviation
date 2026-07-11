<template>
  <div class="login-wrapper">
    <!-- ====== 背景视频（备选图片） ====== -->
    <div class="login-bg" aria-hidden="true">
      <!-- 视频背景 -->
      <video
        autoplay
        muted
        loop
        playsinline
        class="bg-video"
        poster="https://picsum.photos/seed/login-bg/1920/1080"
      >
        <source src="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" type="video/mp4" />
      </video>
      <!-- 降级图片（视频加载失败时显示） -->
      <img
        src="https://picsum.photos/seed/login-bg/1920/1080"
        alt=""
        class="bg-fallback"
      />
      <!-- 渐变遮罩 -->
      <div class="bg-overlay"></div>
      <!-- 装饰光晕 -->
      <div class="bg-orb bg-orb--1"></div>
      <div class="bg-orb bg-orb--2"></div>
    </div>

    <!-- ====== 登录卡片 ====== -->
    <div class="login-card">
      <div class="card-inner">
        <!-- 品牌区 -->
        <div class="card-brand">
          <div class="brand-icon" aria-hidden="true">
            <svg width="40" height="40" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 38L14 20L22 30L32 16L44 38H4Z" fill="currentColor" opacity="0.9"/>
              <path d="M14 38L24 24L30 32L38 22L44 38H14Z" fill="currentColor" opacity="0.5"/>
              <path d="M24 6C24 6 20 14 24 22C28 14 24 6 24 6Z" fill="currentColor" opacity="0.8"/>
              <path d="M24 12V26" stroke="currentColor" stroke-width="2" stroke-linecap="round" opacity="0.5"/>
            </svg>
          </div>
          <h1 class="card-title">志愿服务联动帮扶综合服务平台</h1>
          <p class="card-subtitle">乡村振兴 · 共同富裕</p>
        </div>

        <!-- 表单 -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="UserIcon"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="LockIcon"
              show-password
            />
          </el-form-item>

          <!-- 记住我 + 忘记密码 -->
          <div class="form-options">
            <el-checkbox v-model="rememberMe" size="small">记住我</el-checkbox>
            <a class="forgot-link" @click.prevent="onForgotPassword">忘记密码？</a>
          </div>

          <el-form-item>
            <el-button
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部 -->
        <p class="card-footer">
          <span class="footer-copyright">© {{ year }} 志愿服务联动帮扶综合服务平台</span>
          <span class="footer-tech">技术支持 · 乡村振兴信息中心</span>
        </p>
      </div>
    </div>

    <!-- ====== 左下角版本信息 ====== -->
    <div class="login-version">v1.0 · 企业版</div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)
const year = computed(() => new Date().getFullYear())

const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const UserIcon = {
  render() {
    return h('svg', {
      viewBox: '0 0 24 24',
      width: '18',
      height: '18',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('path', { d: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }),
      h('circle', { cx: '12', cy: '7', r: '4' })
    ])
  }
}

const LockIcon = {
  render() {
    return h('svg', {
      viewBox: '0 0 24 24',
      width: '18',
      height: '18',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('rect', { x: '3', y: '11', width: '18', height: '11', rx: '2', ry: '2' }),
      h('path', { d: 'M7 11V7a5 5 0 0 1 10 0v4' })
    ])
  }
}

const onForgotPassword = () => {
  ElMessage.info('请联系管理员重置密码')
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/system/auth/login', form)
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data)
      ElMessage.success('登录成功')
      await router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (err: any) {
    console.error('登录错误:', err)
    ElMessage.error(err.message || '网络错误，请检查后端是否运行')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
/* ============================================================
   登录页 — 沉浸式品牌门户风格
   主色: #0A2647  辅色: #2D7A9B  强调色: #F9A826
   ============================================================ */

.login-wrapper {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #0A2647;
}

/* ====== 背景层 ====== */
.login-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.bg-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.bg-fallback {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: none;
}

/* 当视频无法加载时显示图片 */
.bg-video:not([src]) + .bg-fallback,
.bg-video:not([src]) ~ .bg-fallback {
  display: block;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(10, 38, 71, 0.88) 0%,
    rgba(10, 38, 71, 0.65) 40%,
    rgba(45, 122, 155, 0.35) 100%
  );
  z-index: 1;
}

/* 装饰光晕 */
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.15;
  z-index: 1;

  &--1 {
    width: 600px;
    height: 600px;
    top: -200px;
    right: -150px;
    background: radial-gradient(circle, #2D7A9B 0%, transparent 70%);
    animation: orbFloat 14s ease-in-out infinite alternate;
  }

  &--2 {
    width: 500px;
    height: 500px;
    bottom: -180px;
    left: -120px;
    background: radial-gradient(circle, #F9A826 0%, transparent 70%);
    animation: orbFloat 12s ease-in-out infinite alternate-reverse;
  }
}

@keyframes orbFloat {
  0% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.08); }
  66% { transform: translate(-25px, 20px) scale(0.92); }
  100% { transform: translate(20px, -15px) scale(1.04); }
}

/* ====== 登录卡片 — 玻璃态 ====== */
.login-card {
  position: relative;
  z-index: 2;
  width: 440px;
  max-width: calc(100vw - 40px);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  box-shadow:
    0 8px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  animation: cardEnter 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
  overflow: hidden;

  // 卡片顶部装饰光条
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: linear-gradient(90deg, #2D7A9B, #F9A826, #2D7A9B);
    background-size: 200% 100%;
    animation: shimmerBar 4s ease-in-out infinite;
    z-index: 0;
  }
}

@keyframes shimmerBar {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(36px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.card-inner {
  position: relative;
  z-index: 1;
  padding: 48px 40px 36px;
}

/* 品牌区 */
.card-brand {
  text-align: center;
  margin-bottom: 36px;
}

.brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  color: #fff;
  background: linear-gradient(135deg, rgba(45, 122, 155, 0.3), rgba(249, 168, 38, 0.15));
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 18px;
  animation: iconEnter 0.6s 0.2s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes iconEnter {
  from {
    opacity: 0;
    transform: scale(0.5) rotate(-15deg);
  }
  to {
    opacity: 1;
    transform: scale(1) rotate(0);
  }
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 6px;
  text-wrap: balance;
  letter-spacing: 0.02em;
}

.card-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
  margin: 0;
  letter-spacing: 0.1em;
}

/* ====== 表单 ====== */
.login-form {
  margin-bottom: 24px;

  :deep(.el-input) {
    --el-input-border-color: rgba(255, 255, 255, 0.15);
    --el-input-hover-border-color: rgba(255, 255, 255, 0.3);
    --el-input-focus-border-color: #2D7A9B;
    --el-input-bg-color: rgba(255, 255, 255, 0.06);
    --el-input-text-color: #fff;
    --el-input-placeholder-color: rgba(255, 255, 255, 0.35);
    --el-input-icon-color: rgba(255, 255, 255, 0.4);

    .el-input__wrapper {
      background: rgba(255, 255, 255, 0.06) !important;
      border-radius: 12px !important;
      padding: 4px 16px !important;
      transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
      box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.12) inset !important;
      position: relative;

      // 聚焦光晕
      &::before {
        content: '';
        position: absolute;
        inset: -2px;
        border-radius: 14px;
        background: linear-gradient(135deg, #2D7A9B, #F9A826);
        opacity: 0;
        z-index: -1;
        transition: opacity 0.4s;
      }

      &:focus-within {
        background: rgba(255, 255, 255, 0.1) !important;
        box-shadow: 0 0 0 1.5px #2D7A9B inset, 0 0 20px rgba(45, 122, 155, 0.15) !important;

        &::before {
          opacity: 1;
        }
      }
    }

    .el-input__prefix {
      margin-right: 8px;
      color: rgba(255, 255, 255, 0.4);
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 22px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  // 错误状态
  :deep(.el-form-item.is-error) {
    .el-input__wrapper {
      background: rgba(239, 68, 68, 0.1) !important;
      box-shadow: 0 0 0 1px #EF4444 inset !important;
    }
  }
}

/* 记住我 + 忘记密码 */
.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;

  :deep(.el-checkbox) {
    --el-checkbox-text-color: rgba(255, 255, 255, 0.65);
    --el-checkbox-checked-text-color: #fff;
    --el-checkbox-checked-bg-color: #2D7A9B;
    --el-checkbox-checked-input-border-color: #2D7A9B;
    --el-checkbox-input-border-color: rgba(255, 255, 255, 0.3);
    --el-checkbox-checked-input-fill: #2D7A9B;

    .el-checkbox__inner {
      border-radius: 4px;
      background: rgba(255, 255, 255, 0.06);
    }
  }
}

.forgot-link {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: #F9A826;
  }
}

/* ====== 登录按钮 ====== */
.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border: none !important;
  border-radius: 14px !important;
  color: #fff !important;
  background: linear-gradient(135deg, #0A2647, #2D7A9B, #0A2647);
  background-size: 200% 100%;
  box-shadow: 0 4px 20px rgba(10, 38, 71, 0.4);
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  letter-spacing: 0.08em;
  position: relative;
  overflow: hidden;

  // 光泽扫光
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(255, 255, 255, 0.12) 50%,
      transparent 100%
    );
    background-size: 200% 100%;
    animation: shimmer 3s ease-in-out infinite;
    pointer-events: none;
  }

  &:hover:not(:disabled) {
    transform: scale(1.02);
    box-shadow: 0 8px 30px rgba(10, 38, 71, 0.5), 0 0 0 1px rgba(45, 122, 155, 0.3);
    background-position: right center;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
    box-shadow: 0 2px 10px rgba(10, 38, 71, 0.3);
  }

  // Loading 状态
  &.is-loading {
    background: #0A2647;
    opacity: 0.85;
    &::before { display: none; }
  }
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

/* ====== 底部 ====== */
.card-footer {
  text-align: center;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.footer-copyright {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

.footer-tech {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.25);
  letter-spacing: 0.04em;
}

/* ====== 版本号 ====== */
.login-version {
  position: absolute;
  bottom: 24px;
  left: 28px;
  z-index: 2;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.2);
  letter-spacing: 0.06em;
}

/* ====== 响应式 ====== */
@media (max-width: 480px) {
  .card-inner {
    padding: 36px 24px 28px;
  }

  .login-card {
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .card-title {
    font-size: 20px;
  }

  .brand-icon {
    width: 54px;
    height: 54px;
  }

  .login-btn {
    height: 46px;
    font-size: 15px;
  }

  .bg-orb {
    filter: blur(80px);

    &--1 {
      width: 300px;
      height: 300px;
      top: -80px;
      right: -80px;
    }

    &--2 {
      width: 250px;
      height: 250px;
      bottom: -80px;
      left: -80px;
    }
  }

  .login-version {
    display: none;
  }
}
</style>
