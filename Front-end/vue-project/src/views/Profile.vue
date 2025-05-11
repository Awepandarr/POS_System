
<template>
    <div class="bg-white shadow-lg rounded-lg overflow-hidden">
      <div class="p-6 bg-blue-600 text-white">
        <h1 class="text-2xl font-bold">Your Profile</h1>
      </div>
  
      <div class="p-6">
        <div v-if="loading" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
          <p class="mt-4 text-gray-500">Loading profile...</p>
        </div>
  
        <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Profile Overview -->
          <div class="md:col-span-1">
            <div class="bg-gray-50 p-6 rounded-lg shadow">
              <div class="flex flex-col items-center">
                <div class="h-24 w-24 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 text-2xl font-bold mb-4">
                  {{ userInitials }}
                </div>
                
                <h2 class="text-xl font-bold">{{ user.name }}</h2>
                <p class="text-gray-500">{{ user.email }}</p>
                
                <div class="mt-3 px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                  {{ user.role === 'admin' ? 'Administrator' : 'Cashier' }}
                </div>
                
                <div class="w-full mt-6 space-y-2">
                  <div class="flex justify-between px-4 py-2 bg-gray-100 rounded">
                    <span class="text-gray-600">Last login</span>
                    <span class="font-medium">{{ lastLogin }}</span>
                  </div>
                  <div class="flex justify-between px-4 py-2 bg-gray-100 rounded">
                    <span class="text-gray-600">Member since</span>
                    <span class="font-medium">{{ memberSince }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
  
          <!-- Profile Details Form -->
          <div class="md:col-span-2">
            <div class="bg-white p-6 rounded-lg shadow">
              <h3 class="text-lg font-bold mb-6">Personal Information</h3>
              
              <form @submit.prevent="updateProfile">
                <div class="space-y-6">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                      <input 
                        type="text" 
                        v-model="profileForm.firstName" 
                        class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      >
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                      <input 
                        type="text" 
                        v-model="profileForm.lastName" 
                        class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      >
                    </div>
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                    <input 
                      type="email" 
                      v-model="profileForm.email" 
                      class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    >
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                    <input 
                      type="tel" 
                      v-model="profileForm.phone" 
                      class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                  </div>
                  
                  <div>
                    <h3 class="text-lg font-bold mb-4">Change Password</h3>
                    <div class="space-y-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Current Password</label>
                        <input 
                          type="password" 
                          v-model="passwordForm.currentPassword" 
                          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                      </div>
                      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-1">New Password</label>
                          <input 
                            type="password" 
                            v-model="passwordForm.newPassword" 
                            class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          >
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-1">Confirm New Password</label>
                          <input 
                            type="password" 
                            v-model="passwordForm.confirmPassword" 
                            class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          >
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="pt-4 border-t flex justify-end">
                    <button 
                      type="submit" 
                      class="px-6 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                      :disabled="updating"
                    >
                      {{ updating ? 'Saving Changes...' : 'Save Changes' }}
                    </button>
                  </div>
                </div>
              </form>
            </div>
            
            <!-- Activity Section -->
            <div class="mt-6 bg-white p-6 rounded-lg shadow">
              <h3 class="text-lg font-bold mb-4">Recent Activities</h3>
              <div class="space-y-4">
                <div v-for="(activity, index) in recentActivities" :key="index" class="flex items-start pb-4 border-b last:border-0 last:pb-0">
                  <div class="bg-blue-100 p-2 rounded-full text-blue-600 mr-3">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path v-if="activity.type === 'login'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                      <path v-else-if="activity.type === 'sale'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                      <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                  </div>
                  <div class="flex-1">
                    <div class="flex justify-between">
                      <p class="font-medium">{{ activity.message }}</p>
                      <span class="text-sm text-gray-500">{{ activity.time }}</span>
                    </div>
                    <p class="text-sm text-gray-600">{{ activity.details }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted } from 'vue';
  import { useAuth } from '@/services/auth.service';
  
  export default {
    setup() {
      const { user: currentUser } = useAuth();
      const loading = ref(true);
      const updating = ref(false);
      const user = ref({});
      
      // Forms
      const profileForm = ref({
        firstName: '',
        lastName: '',
        email: '',
        phone: ''
      });
      
      const passwordForm = ref({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
      
      // Computed values
      const userInitials = computed(() => {
        if (!user.value.name) return '';
        return user.value.name
          .split(' ')
          .map(n => n[0])
          .join('')
          .toUpperCase()
          .substring(0, 2);
      });
      
      // Demo data
      const lastLogin = "April 15, 2025, 9:42 AM";
      const memberSince = "January 20, 2025";
      
      const recentActivities = ref([
        {
          type: 'login',
          message: 'Logged in successfully',
          details: 'Session started from Manchester, UK',
          time: '2 hours ago'
        },
        {
          type: 'sale',
          message: 'Processed sale #ORD10023',
          details: 'Total: $149.99 - Payment method: Credit Card',
          time: '3 hours ago'
        },
        {
          type: 'login',
          message: 'Logged in successfully',
          details: 'Session started from Manchester, UK',
          time: 'Yesterday at 8:30 AM'
        },
        {
          type: 'update',
          message: 'Updated profile information',
          details: 'Changed email address and phone number',
          time: 'April 14, 2025'
        },
        {
          type: 'sale',
          message: 'Processed sale #ORD10015',
          details: 'Total: $78.50 - Payment method: Cash',
          time: 'April 14, 2025'
        }
      ]);
      
      // Load user data
      onMounted(async () => {
        try {
          // In a real app, this would be an API call to get the user's profile
          await new Promise(resolve => setTimeout(resolve, 800)); // Simulate API delay
          
          // Use the current authenticated user's data
          user.value = { ...currentUser.value };
          
          // Split the name into first and last name for the form
          const nameParts = user.value.name.split(' ');
          profileForm.value.firstName = nameParts[0];
          profileForm.value.lastName = nameParts.slice(1).join(' ');
          profileForm.value.email = user.value.email;
          profileForm.value.phone = user.value.phone || '';
        } finally {
          loading.value = false;
        }
      });
      
      // Update profile
      const updateProfile = async () => {
        if (passwordForm.value.newPassword && passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
          alert('New passwords do not match');
          return;
        }
        
        updating.value = true;
        
        try {
          // In a real app, this would be an API call to update the user's profile
          await new Promise(resolve => setTimeout(resolve, 800)); // Simulate API delay
          
          // Update the user object
          user.value.name = `${profileForm.value.firstName} ${profileForm.value.lastName}`;
          user.value.email = profileForm.value.email;
          user.value.phone = profileForm.value.phone;
          
          // If password was provided, update it (in a real app)
          if (passwordForm.value.currentPassword && passwordForm.value.newPassword) {
            // Reset password fields
            passwordForm.value.currentPassword = '';
            passwordForm.value.newPassword = '';
            passwordForm.value.confirmPassword = '';
          }
          
          // Add a new activity
          recentActivities.value.unshift({
            type: 'update',
            message: 'Updated profile information',
            details: 'Profile information updated successfully',
            time: 'Just now'
          });
          
          alert('Profile updated successfully');
        } catch (error) {
          console.error('Error updating profile:', error);
          alert('Failed to update profile. Please try again.');
        } finally {
          updating.value = false;
        }
      };
      
      return {
        user,
        loading,
        updating,
        profileForm,
        passwordForm,
        userInitials,
        lastLogin,
        memberSince,
        recentActivities,
        updateProfile
      };
    }
  };
  </script>