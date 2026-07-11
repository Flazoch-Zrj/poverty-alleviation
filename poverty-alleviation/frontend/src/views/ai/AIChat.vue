<template>
  <div class="ai-chat">
    <div class="chat-header">
      <div class="chat-header-left">
        <span class="ai-icon">🤖</span>
        <div>
          <h3>AI 扶贫助手</h3>
          <p>智能解答扶贫政策、帮扶措施等问题</p>
        </div>
      </div>
      <el-button text @click="clearChat" size="small">清除对话</el-button>
    </div>

    <div class="chat-messages" ref="msgContainer">
      <div v-for="(msg, i) in messages" :key="i" class="msg-row" :class="msg.role">
        <div class="msg-avatar">{{ msg.role === 'ai' ? '🤖' : '👤' }}</div>
        <div class="msg-bubble">
          <div class="msg-text">{{ msg.content }}</div>
          <div class="msg-time">{{ msg.time }}</div>
        </div>
      </div>
      <div v-if="loading" class="msg-row ai">
        <div class="msg-avatar">🤖</div>
        <div class="msg-bubble typing">
          <span class="dot"></span><span class="dot"></span><span class="dot"></span>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMsg"
        type="textarea"
        :rows="2"
        placeholder="输入您的问题，如：低保申请条件是什么？"
        @keydown.enter.prevent="sendMessage"
        :disabled="loading"
      />
      <el-button type="primary" :loading="loading" @click="sendMessage" class="send-btn">
        <el-icon><Promotion /></el-icon>
      </el-button>
    </div>

    <div class="chat-hints">
      <span v-for="hint in hints" :key="hint" class="hint-tag" @click="inputMsg = hint; sendMessage()">{{ hint }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { aiChat } from '@/api/system'
import { Promotion } from '@element-plus/icons-vue'

const inputMsg = ref('')
const loading = ref(false)
const msgContainer = ref<HTMLDivElement>()
const messages = ref<Array<{ role: string; content: string; time: string }>>([
  { role: 'ai', content: '您好！我是 AI 扶贫助手，可以为您解答关于扶贫政策、帮扶措施、补贴标准等方面的问题。请问有什么可以帮助您的？', time: getTime() }
])

const hints = ['低保申请条件', '贫困户认定标准', '小额贷款怎么申请', '雨露计划补助', '危房改造补贴']

function getTime() {
  const n = new Date()
  return `${String(n.getHours()).padStart(2,'0')}:${String(n.getMinutes()).padStart(2,'0')}`
}

const scrollBottom = () => {
  nextTick(() => {
    if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  })
}

const sendMessage = async () => {
  const msg = inputMsg.value.trim()
  if (!msg || loading.value) return

  messages.value.push({ role: 'user', content: msg, time: getTime() })
  inputMsg.value = ''
  loading.value = true
  scrollBottom()

  try {
    const res = await aiChat(msg)
    const reply = res.data?.reply || '抱歉，我没有理解您的问题，请重新描述。'
    messages.value.push({ role: 'ai', content: reply, time: getTime() })
  } catch {
    messages.value.push({ role: 'ai', content: 'AI 服务暂时不可用，请稍后重试。', time: getTime() })
  } finally {
    loading.value = false
    scrollBottom()
  }
}

const clearChat = () => {
  messages.value = [
    { role: 'ai', content: '您好！我是 AI 扶贫助手，可以为您解答关于扶贫政策、帮扶措施、补贴标准等方面的问题。请问有什么可以帮助您的？', time: getTime() }
  ]
}

onMounted(scrollBottom)
</script>

<style scoped>
.ai-chat { display: flex; flex-direction: column; height: calc(100vh - 120px); max-width: 800px; margin: 0 auto; padding: 20px; }
.chat-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; background: #fff; border-radius: 16px 16px 0 0; border-bottom: 1px solid #E2E8F0; }
.chat-header-left { display: flex; align-items: center; gap: 12px; }
.ai-icon { font-size: 32px; }
.chat-header h3 { margin: 0; font-size: 16px; font-weight: 700; color: #1E293B; }
.chat-header p { margin: 2px 0 0; font-size: 12px; color: #94A3B8; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; background: #F8FAFC; display: flex; flex-direction: column; gap: 16px; }
.msg-row { display: flex; gap: 10px; max-width: 80%; }
.msg-row.ai { align-self: flex-start; }
.msg-row.user { align-self: flex-end; flex-direction: row-reverse; }
.msg-avatar { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; background: #F1F5F9; }
.msg-bubble { padding: 12px 16px; border-radius: 14px; font-size: 14px; line-height: 1.6; color: #1E293B; }
.msg-row.ai .msg-bubble { background: #fff; border: 1px solid #E2E8F0; border-radius: 14px 14px 14px 4px; }
.msg-row.user .msg-bubble { background: linear-gradient(135deg, #0A2647, #2D7A9B); color: #fff; border-radius: 14px 14px 4px 14px; }
.msg-text { white-space: pre-wrap; word-break: break-word; }
.msg-time { font-size: 11px; color: #94A3B8; margin-top: 4px; }
.msg-row.user .msg-time { color: rgba(255,255,255,0.6); }
.typing { display: flex; gap: 4px; padding: 16px 20px; }
.dot { width: 8px; height: 8px; background: #94A3B8; border-radius: 50%; animation: bounce 1.4s infinite ease-in-out both; }
.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes bounce { 0%,80%,100% { transform: scale(0); } 40% { transform: scale(1); } }
.chat-input { display: flex; gap: 8px; padding: 16px; background: #fff; border-top: 1px solid #E2E8F0; }
.chat-input .el-textarea { flex: 1; }
.send-btn { align-self: flex-end; height: 40px; width: 48px; border-radius: 10px; }
.chat-hints { display: flex; gap: 6px; padding: 10px 16px; background: #fff; border-radius: 0 0 16px 16px; flex-wrap: wrap; }
.hint-tag { padding: 3px 10px; background: #F1F5F9; border-radius: 12px; font-size: 11px; color: #64748B; cursor: pointer; transition: all .15s; }
.hint-tag:hover { background: #DBEAFE; color: #0A2647; }
</style>
