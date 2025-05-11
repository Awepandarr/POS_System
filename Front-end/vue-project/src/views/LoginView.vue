<!-- src/views/LoginView.vue -->
<template>
    <div class="min-h-screen bg-gradient-to-r from-blue-400 to-blue-600 flex items-center justify-center p-4">
      <div class="max-w-md w-full bg-white rounded-lg shadow-xl overflow-hidden">
        <div class="p-6 sm:p-8">
          <div class="text-center mb-8">
            <h1 class="text-3xl font-bold text-gray-800">POS System</h1>
            <p class="text-gray-600 mt-2">Please sign in to continue</p>
          </div>
  
          <div v-if="errorMessage" class="mb-6 bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded" role="alert">
            <p>{{ errorMessage }}</p>
          </div>
  
          <form @submit.prevent="handleLogin" class="space-y-6">
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
              <input
                id="email"
                v-model="email"
                type="email"
                autocomplete="email"
                required
                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="you@example.com"
              />
            </div>
  
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Password</label>
              <input
                id="password"
                v-model="password"
                type="password"
                autocomplete="current-password"
                required
                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="••••••••"
              />
            </div>
  
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <input
                  id="remember-me"
                  v-model="rememberMe"
                  type="checkbox"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label for="remember-me" class="ml-2 block text-sm text-gray-700">
                  Remember me
                </label>
              </div>
  
              <div class="text-sm">
                <a href="#" class="font-medium text-blue-600 hover:text-blue-500">
                  Forgot your password?
                </a>
              </div>
            </div>
  
            <div>
              <button
                type="submit"
                class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-lg font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                :disabled="loading"
              >
                <span v-if="loading" class="flex items-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Signing in...
                </span>
                <span v-else>Sign in</span>
              </button>
            </div>
          </form>
  
          <div class="mt-6">
            <div class="relative">
              <div class="absolute inset-0 flex items-center">
                <div class="w-full border-t border-gray-300"></div>
              </div>
              <div class="relative flex justify-center text-sm">
                <span class="px-2 bg-white text-gray-500">Demo Credentials</span>
              </div>
            </div>
  
            <div class="mt-4 grid grid-cols-2 gap-3">
              <button
                @click="fillAdmin"
                class="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Admin User
              </button>
              <button
                @click="fillCashier"
                class="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Cashier User
              </button>
            </div>
          </div>
        </div>
  
        <div class="px-6 py-4 bg-gray-50 border-t border-gray-200 sm:px-8">
          <p class="text-xs text-gray-500">
            © 2025 POS System. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuth } from '@/services/auth.service';
  
  export default {
    setup() {
      const router = useRouter();
      const { login } = useAuth();
  
      const email = ref('');
      const password = ref('');
      const rememberMe = ref(false);
      const loading = ref(false);
      const errorMessage = ref('');
  
      const handleLogin = async () => {
        loading.value = true;
        errorMessage.value = '';
  
        try {
          await login(email.value, password.value, rememberMe.value);
          router.push('/');
        } catch (error) {
          errorMessage.value = error.message || 'Failed to sign in. Please check your credentials.';
        } finally {
          loading.value = false;
        }
      };
  
      // Demo credentials
      const fillAdmin = () => {
        email.value = 'admin@example.com';
        password.value = 'admin123';
      };
  
      const fillCashier = () => {
        email.value = 'cashier@example.com';
        password.value = 'cashier123';
      };
  
      return {
        email,
        password,
        rememberMe,
        loading,
        errorMessage,
        handleLogin,
        fillAdmin,
        fillCashier
      };
    }
  };
  </script>