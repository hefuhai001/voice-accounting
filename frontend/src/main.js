import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 导入 Tailwind CSS
import './style.css'
// 导入 Ant Design 基础样式
import 'ant-design-vue/dist/reset.css'

import App from './App.vue'
import router from './router'

// 导入 Access 高级组件
import './plugins/access'
// 导入自定义组件
import { registerComponents } from './components'

const app = createApp(App)

app.use(createPinia().use(piniaPluginPersistedstate))
app.use(router)

// 注册自定义组件
registerComponents(app)

app.mount('#app')

// 生产环境注册 Service Worker（PWA 可安装性必要条件）
if (import.meta.env.PROD && 'serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch((err) => {
      console.error('Service Worker 注册失败:', err)
    })
  })
}
