import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { useAuth } from './services/auth.service'

const app = createApp(App)

// Initialize auth service
const { user } = useAuth()

// Pass router to app
app.use(router)

// Mount the app
app.mount('#app')

// For development only - log initial auth state
console.log('Initial auth state:', user.value ? 'Authenticated' : 'Not authenticated')