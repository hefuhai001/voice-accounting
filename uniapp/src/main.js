import { createSSRApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
// import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  // 暂时禁用持久化插件，排查 APP 端兼容性问题
  // pinia.use(piniaPluginPersistedstate, {
  //   storage: uniStorage
  // })

  app.use(pinia)
  return { app }
}
