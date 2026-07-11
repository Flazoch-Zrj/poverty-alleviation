import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import dayjs from 'dayjs'
import './styles/global.scss'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
// 全局注册 dayjs，模板中可用 $dayjs()
app.config.globalProperties.$dayjs = dayjs
app.mount('#app')
