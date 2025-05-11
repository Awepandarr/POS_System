import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      // Proxy all barcode scanner endpoints to the Flask service
      '/video_feed': 'http://localhost:5001',
      '/latest_barcode': 'http://localhost:5001',
      '/latest_product': 'http://localhost:5001',
      '/health': 'http://localhost:5001',
      '/scan': 'http://localhost:5001'
    }
  }
})