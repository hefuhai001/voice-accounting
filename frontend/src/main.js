import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 导入 Tailwind CSS
import './style.css'

import App from './App.vue'
import router from './router'

// 导入 Ant Design Vue
import { Antd, zhCN } from './plugins/antd'
// 导入 Access 高级组件
import './plugins/access'
// 导入自定义组件
import { registerComponents } from './components'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)

// 注册自定义组件
registerComponents(app)

// 全局配置中文
app.provide('locale', zhCN)

app.mount('#app')
