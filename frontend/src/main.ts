import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router'

// 导入全局样式
import './assets/styles/global.scss'

// 创建应用实例
const app = createApp(App)

// 创建 Pinia 实例并使用持久化插件
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 使用插件
app.use(pinia)
app.use(router)

// 挂载应用
app.mount('#app')
