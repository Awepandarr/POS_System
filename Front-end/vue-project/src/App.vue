<template>
  <div class="h-full">
    <MainLayout v-if="!isSimplePage && isAuthenticated">
      <router-view />
    </MainLayout>
    <router-view v-else />

    <!-- Global Loading Overlay -->
    <div v-if="globalLoading" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-8 max-w-md w-full">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-500 mx-auto"></div>
        <p class="mt-4 text-center text-lg text-gray-700">Please wait...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, provide } from 'vue';
import { useRoute } from 'vue-router';
import { useAuth } from '@/services/auth.service';
import MainLayout from '@/components/MainLayout.vue';

export default {
  components: {
    MainLayout
  },
  setup() {
    const route = useRoute();
    const { isAuthenticated } = useAuth();
    const globalLoading = ref(false);
    
    // Define which routes don't need the main layout (e.g., login page, confirmation page)
    const isSimplePage = computed(() => {
      const simplePages = ['/login', '/confirmation', '/payment'];
      return simplePages.includes(route.path);
    });
    
    // Provide global loading state that can be injected in any component
    provide('globalLoading', {
      show: () => { globalLoading.value = true; },
      hide: () => { globalLoading.value = false; }
    });
    
    return {
      isSimplePage,
      isAuthenticated,
      globalLoading
    };
  }
};
</script>

<style>
@import './assets/main.css';

html, body {
  height: 100%;
}

#app {
  height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>