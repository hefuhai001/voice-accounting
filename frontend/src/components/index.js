/**
 * 全局组件注册
 */
import ProTable from './ProTable.vue'
import ProForm from './ProForm.vue'
import ProModal from './ProModal.vue'

export { ProTable, ProForm, ProModal }

// 组件注册函数
export function registerComponents(app) {
  app.component('ProTable', ProTable)
  app.component('ProForm', ProForm)
  app.component('ProModal', ProModal)
}
