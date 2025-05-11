<!-- src/components/MainLayout.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <!-- Top Navigation -->
    <header class="bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg">
      <div class="container mx-auto px-4 py-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center space-x-4">
            <button 
              @click="toggleSidebar" 
              class="lg:hidden p-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
            <div class="text-2xl font-bold tracking-wide">POS System</div>
          </div>
          
          <div class="flex items-center space-x-4">
            <div class="relative" ref="notificationDropdown">
              <button 
                @click="toggleNotifications" 
                class="p-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300 relative"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
                <span v-if="unreadNotifications > 0" class="absolute top-0 right-0 block h-2 w-2 rounded-full bg-red-500 ring-2 ring-white"></span>
              </button>
              
              <div 
                v-if="showNotifications" 
                class="absolute right-0 mt-2 w-80 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 z-50"
              >
                <div class="p-4 border-b">
                  <h3 class="text-lg font-semibold text-gray-800">Notifications</h3>
                </div>
                <div class="max-h-96 overflow-y-auto">
                  <div v-if="notifications.length === 0" class="p-4 text-center text-gray-500">
                    No new notifications
                  </div>
                  <div 
                    v-for="(notification, index) in notifications" 
                    :key="notification.id || index"
                    class="p-4 border-b last:border-0 hover:bg-gray-50 cursor-pointer"
                    @click="markNotificationAsRead(notification)"
                  >
                    <div class="flex">
<!-- Inside the notification block in MainLayout.vue -->
<div class="flex-shrink-0 mr-4">
  <div class="h-10 w-10 rounded-full flex items-center justify-center"
       :class="{
         'bg-red-100': notification.type === 'out-of-stock',
         'bg-yellow-100': notification.type === 'low-stock',
         'bg-green-100': notification.type === 'stock-replenished',
         'bg-blue-100': notification.type === 'stock-update' || notification.type === 'order'
       }">
    <!-- Out of stock icon -->
    <svg v-if="notification.type === 'out-of-stock'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
    
    <!-- Low stock icon -->
    <svg v-else-if="notification.type === 'low-stock'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
    </svg>
    
    <!-- Stock replenished icon -->
    <svg v-else-if="notification.type === 'stock-replenished'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
    
    <!-- Default stock update icon -->
    <svg v-else-if="notification.type === 'stock-update'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
    </svg>
    
    <!-- Keep your existing icons for other notification types -->
    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
  </div>
</div>
                      <div>
                        <div class="flex items-center">
                          <div class="text-sm font-medium text-gray-900">{{ notification.title }}</div>
                          <div v-if="!notification.read" class="ml-2 w-2 h-2 bg-blue-600 rounded-full"></div>
                        </div>
                        <div class="text-sm text-gray-500">{{ notification.message }}</div>
                        <div class="text-xs text-gray-400 mt-1">{{ notification.time }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="p-2 bg-gray-50">
                  <button 
                    @click="markAllAsRead"
                    class="w-full py-2 text-sm font-medium text-blue-600 hover:text-blue-800 focus:outline-none"
                  >
                    Mark all as read
                  </button>
                </div>
              </div>
            </div>
            
            <div class="relative" ref="userDropdown">
              <button 
                @click="toggleUserMenu" 
                class="flex items-center space-x-2 p-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
              >
                <div class="h-8 w-8 rounded-full bg-blue-400 flex items-center justify-center">
                  <span class="text-lg font-semibold text-white">{{ userInitials }}</span>
                </div>
                <span class="hidden sm:inline-block text-sm font-medium">{{ currentUser.name }}</span>
                <span class="hidden sm:inline-block text-xs font-medium bg-blue-700 px-2 py-1 rounded-full">{{ currentUser.role }}</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              
              <div 
                v-if="showUserMenu" 
                class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 z-50"
              >
                <div class="py-1">
                  <router-link 
                    to="/profile" 
                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                  >
                    Your Profile
                  </router-link>
                  <router-link 
                    to="/settings" 
                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                  >
                    Settings
                  </router-link>
                  <button 
                    @click="logout" 
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                  >
                    Sign out
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
    
    <div class="flex flex-1">
      <!-- Sidebar -->
      <aside 
        :class="[
          'bg-gray-800 text-gray-300 w-64 min-h-screen flex-shrink-0 transition-transform duration-300 ease-in-out transform',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        ]"
      >
        <div class="py-4 px-6">
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold">Navigation</h2>
            <button 
              @click="toggleSidebar" 
              class="p-2 rounded-md hover:bg-gray-700 lg:hidden focus:outline-none focus:ring-2 focus:ring-gray-400"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <nav class="mt-8">
            <div class="space-y-2">
              <router-link 
                to="/" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                Dashboard
              </router-link>
              
              <router-link 
                v-if="hasRole('admin')"
                to="/customers" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/customers' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                Customers
              </router-link>
              
              <router-link 
                v-if="hasRole('admin')"
                to="/products" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/products' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
                Products
              </router-link>
              <router-link 
                v-if="hasRole('admin')"
                to="/stock" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/stock' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
                </svg>
                Stock Management
              </router-link>
              <router-link 
                to="/orders" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/orders' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
                Orders
              </router-link>
              
              <router-link 
                v-if="hasRole('admin')"
                to="/reports" 
                class="flex items-center px-4 py-2 rounded-md hover:bg-gray-700 focus:outline-none focus:bg-gray-700"
                :class="{ 'bg-gray-700': $route.path === '/reports' }"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                Reports
              </router-link>
            </div>
          </nav>
        </div>
      </aside>
      
      <!-- Main Content -->
      <main 
        :class="[
          'flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 transition-all duration-300 ease-in-out',
          sidebarOpen ? 'lg:ml-64' : 'lg:ml-0'
        ]"
      >
        <div class="container mx-auto px-4 py-8">
          <slot></slot>
        </div>
      </main>
    </div>
  </div>
</template>
  
<script>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuth } from '@/services/auth.service';

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    const { user: currentUser, logout: signOut, hasRole } = useAuth();
    
    // Sidebar state
    const sidebarOpen = ref(window.innerWidth >= 1024); // Default open on large screens
    
    const toggleSidebar = () => {
      sidebarOpen.value = !sidebarOpen.value;
    };
    
    // User dropdown menu
    const showUserMenu = ref(false);
    const userDropdown = ref(null);
    
    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value;
      if (showUserMenu.value) {
        showNotifications.value = false;
      }
    };
    
    // Notifications
    const notifications = ref([
      {
        id: 1,
        title: 'Low Stock Alert',
        message: 'Product "Laptop" is running low on stock (2 remaining).',
        time: '5 minutes ago',
        type: 'stock',
        read: false
      },
      {
        id: 2,
        title: 'New Order',
        message: 'Order #1234 has been placed successfully.',
        time: '1 hour ago',
        type: 'order',
        read: false
      }
    ]);
    
    // Computed property to count unread notifications
    const unreadNotifications = computed(() => {
      return notifications.value.filter(notification => !notification.read).length;
    });
    
    // Make notifications globally accessible for other components
    if (typeof window !== 'undefined') {
      window.appNotifications = notifications.value;
    }
    
    // Method to add a new notification
    const addNotification = (notification) => {
      // Create a fully formed notification object
      const newNotification = {
        id: Date.now(),
        title: notification.title,
        message: notification.message,
        time: notification.time || new Date().toLocaleTimeString(),
        type: notification.type || 'info',
        read: false
      };
      
      // Add to beginning of array
      notifications.value.unshift(newNotification);
      
      // Limit to 50 notifications
      if (notifications.value.length > 50) {
        notifications.value.pop();
      }
      
      // Try to save to localStorage for persistence
      try {
        localStorage.setItem('notifications', JSON.stringify(notifications.value));
      } catch (error) {
        console.error('Failed to save notifications to localStorage', error);
      }
      
      return newNotification;
    };
    
    // Method to mark a notification as read
    const markNotificationAsRead = (notification) => {
      const index = notifications.value.findIndex(n => n.id === notification.id);
      if (index !== -1) {
        notifications.value[index].read = true;
        
        // Update localStorage
        try {
          localStorage.setItem('notifications', JSON.stringify(notifications.value));
        } catch (error) {
          console.error('Failed to save notifications to localStorage', error);
        }
      }
    };
    
    // Method to mark all notifications as read
    const markAllAsRead = () => {
      notifications.value.forEach(notification => {
        notification.read = true;
      });
      
      // Update localStorage
      try {
        localStorage.setItem('notifications', JSON.stringify(notifications.value));
      } catch (error) {
        console.error('Failed to save notifications to localStorage', error);
      }
    };
    
    // Listen for custom notification events from other components
    const handleNotificationEvent = (event) => {
      if (event.detail) {
        addNotification(event.detail);
      }
    };
    
    const showNotifications = ref(false);
    const notificationDropdown = ref(null);
    
    const toggleNotifications = () => {
      showNotifications.value = !showNotifications.value;
      if (showNotifications.value) {
        showUserMenu.value = false;
      }
    };
    
    // Handle clicks outside dropdowns
    const handleClickOutside = (event) => {
      if (
        userDropdown.value && 
        !userDropdown.value.contains(event.target) && 
        showUserMenu.value
      ) {
        showUserMenu.value = false;
      }
      
      if (
        notificationDropdown.value && 
        !notificationDropdown.value.contains(event.target) && 
        showNotifications.value
      ) {
        showNotifications.value = false;
      }
    };
    
    // Handle window resize for responsive sidebar
    const handleResize = () => {
      if (window.innerWidth >= 1024) {
        sidebarOpen.value = true;
      } else {
        sidebarOpen.value = false;
      }
    };
    
    // User initials for avatar
    const userInitials = computed(() => {
      if (!currentUser.value || !currentUser.value.name) return 'NA';
      return currentUser.value.name
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .substring(0, 2);
    });
    
    // Logout function
    const logout = () => {
      signOut();
      router.push('/login');
    };
    
    onMounted(() => {
      document.addEventListener('click', handleClickOutside);
      window.addEventListener('resize', handleResize);
      
      // Add custom event listener for notifications
      window.addEventListener('stock-notification', handleNotificationEvent);
      
      // Load saved notifications from localStorage
      try {
        const savedNotifications = localStorage.getItem('notifications');
        if (savedNotifications) {
          notifications.value = JSON.parse(savedNotifications);
        }
      } catch (error) {
        console.error('Failed to load notifications from localStorage', error);
      }
      
      // Expose addNotification method globally for other components
      if (typeof window !== 'undefined') {
        window.addStockNotification = (notificationData) => {
  // Only set type to 'stock' if no type is provided
  const notification = addNotification({
    ...notificationData,
    type: notificationData.type || 'stock'
  });
  
  return notification;
};
      }
    });
    
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside);
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('stock-notification', handleNotificationEvent);
      
      // Remove global references
      if (typeof window !== 'undefined') {
        delete window.addStockNotification;
        delete window.appNotifications;
      }
    });
    
    return {
      currentUser,
      userInitials,
      sidebarOpen,
      toggleSidebar,
      showUserMenu,
      userDropdown,
      toggleUserMenu,
      notifications,
      unreadNotifications,
      showNotifications,
      notificationDropdown,
      toggleNotifications,
      markNotificationAsRead,
      markAllAsRead,
      hasRole,
      logout
    };
  }
};
</script>