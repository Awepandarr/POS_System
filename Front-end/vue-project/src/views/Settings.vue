<!-- src/views/SettingsView.vue -->
<template>
    <div class="bg-white shadow-lg rounded-lg overflow-hidden">
      <div class="p-6 bg-blue-600 text-white">
        <h1 class="text-2xl font-bold">System Settings</h1>
      </div>
  
      <div class="p-6">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Navigation Sidebar -->
          <div class="bg-gray-50 rounded-lg p-6">
            <h2 class="text-lg font-semibold mb-4">Settings</h2>
            <ul class="space-y-2">
              <li>
                <button 
                  @click="activeTab = 'general'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'general' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  General
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'company'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'company' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  Company Information
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'taxes'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'taxes' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  Tax Settings
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'receipt'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'receipt' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  Receipt Setup
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'payment'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'payment' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  Payment Methods
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'users'"
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'users' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  User Management
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'backup'" 
                  class="w-full text-left px-4 py-2 rounded-md transition-colors duration-200"
                  :class="activeTab === 'backup' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'"
                >
                  Backup & Restore
                </button>
              </li>
            </ul>
          </div>
  
          <!-- Settings Content -->
          <div class="bg-white rounded-lg shadow p-6 lg:col-span-2">
            <!-- General Settings -->
            <div v-if="activeTab === 'general'">
              <h2 class="text-xl font-bold mb-6">General Settings</h2>
              
              <div class="space-y-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Language</label>
                  <select v-model="settings.language" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                    <option value="en">English</option>
                    <option value="fr">French</option>
                    <option value="es">Spanish</option>
                    <option value="de">German</option>
                  </select>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Currency</label>
                  <select v-model="settings.currency" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                    <option value="USD">US Dollar ($)</option>
                    <option value="EUR">Euro (€)</option>
                    <option value="GBP">British Pound (£)</option>
                    <option value="JPY">Japanese Yen (¥)</option>
                  </select>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Date Format</label>
                  <select v-model="settings.dateFormat" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                    <option value="MM/DD/YYYY">MM/DD/YYYY</option>
                    <option value="DD/MM/YYYY">DD/MM/YYYY</option>
                    <option value="YYYY-MM-DD">YYYY-MM-DD</option>
                  </select>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Time Format</label>
                  <select v-model="settings.timeFormat" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                    <option value="12">12-hour (AM/PM)</option>
                    <option value="24">24-hour</option>
                  </select>
                </div>
                
                <div class="flex items-center">
                  <input id="auto-logout" v-model="settings.autoLogout" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label for="auto-logout" class="ml-2 block text-sm text-gray-900">
                    Auto logout after 30 minutes of inactivity
                  </label>
                </div>
              </div>
            </div>
  
            <!-- Company Information -->
            <div v-if="activeTab === 'company'">
              <h2 class="text-xl font-bold mb-6">Company Information</h2>
              
              <div class="space-y-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Company Name</label>
                  <input type="text" v-model="settings.companyName" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                  <textarea v-model="settings.companyAddress" rows="3" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"></textarea>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                  <input type="text" v-model="settings.companyPhone" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input type="email" v-model="settings.companyEmail" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">VAT/Tax ID</label>
                  <input type="text" v-model="settings.companyTaxId" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Company Logo</label>
                  <div class="mt-1 flex items-center">
                    <div class="h-12 w-12 overflow-hidden rounded-md bg-gray-100">
                      <img v-if="settings.companyLogo" :src="settings.companyLogo" alt="Company logo">
                      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <button type="button" class="ml-5 bg-white py-2 px-3 border border-gray-300 rounded-md shadow-sm text-sm leading-4 font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                      Upload
                    </button>
                  </div>
                </div>
              </div>
            </div>
  
            <!-- Tax Settings -->
            <div v-if="activeTab === 'taxes'">
              <h2 class="text-xl font-bold mb-6">Tax Settings</h2>
              
              <div class="space-y-6">
                <div class="flex items-center justify-between border-b pb-4">
                  <div>
                    <h3 class="text-lg font-medium">Standard VAT</h3>
                    <p class="text-sm text-gray-500">Default tax rate for most products</p>
                  </div>
                  <div class="flex items-center">
                    <input type="number" v-model="settings.taxRate" min="0" max="100" step="0.01" class="mr-2 w-20 focus:ring-blue-500 focus:border-blue-500 shadow-sm sm:text-sm border-gray-300 rounded-md">
                    <span>%</span>
                  </div>
                </div>
                
                <div class="flex items-center justify-between border-b pb-4">
                  <div>
                    <h3 class="text-lg font-medium">Reduced VAT</h3>
                    <p class="text-sm text-gray-500">For food, books, and children's items</p>
                  </div>
                  <div class="flex items-center">
                    <input type="number" v-model="settings.reducedTaxRate" min="0" max="100" step="0.01" class="mr-2 w-20 focus:ring-blue-500 focus:border-blue-500 shadow-sm sm:text-sm border-gray-300 rounded-md">
                    <span>%</span>
                  </div>
                </div>
                
                <div class="flex items-center justify-between border-b pb-4">
                  <div>
                    <h3 class="text-lg font-medium">Zero VAT</h3>
                    <p class="text-sm text-gray-500">For zero-rated items</p>
                  </div>
                  <div class="flex items-center">
                    <input type="number" value="0" disabled class="mr-2 w-20 bg-gray-100 shadow-sm sm:text-sm border-gray-300 rounded-md">
                    <span>%</span>
                  </div>
                </div>
                
                <div class="flex items-center">
                  <input id="prices-include-tax" v-model="settings.pricesIncludeTax" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label for="prices-include-tax" class="ml-2 block text-sm text-gray-900">
                    Product prices include tax
                  </label>
                </div>
              </div>
            </div>
  
            <!-- Receipt Setup -->
            <div v-if="activeTab === 'receipt'">
              <h2 class="text-xl font-bold mb-6">Receipt Setup</h2>
              
              <div class="space-y-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Receipt Header</label>
                  <textarea v-model="settings.receiptHeader" rows="3" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"></textarea>
                  <p class="mt-1 text-sm text-gray-500">Appears at the top of receipts</p>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Receipt Footer</label>
                  <textarea v-model="settings.receiptFooter" rows="3" class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"></textarea>
                  <p class="mt-1 text-sm text-gray-500">Appears at the bottom of receipts (e.g., "Thank you for your purchase")</p>
                </div>
                
                <div class="flex items-center">
                  <input id="show-barcode" v-model="settings.receiptShowBarcode" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label for="show-barcode" class="ml-2 block text-sm text-gray-900">
                    Show barcode on receipt
                  </label>
                </div>
                
                <div class="flex items-center">
                  <input id="print-duplicate" v-model="settings.receiptPrintDuplicate" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label for="print-duplicate" class="ml-2 block text-sm text-gray-900">
                    Print duplicate copy for records
                  </label>
                </div>
                
                <div class="flex items-center">
                  <input id="email-receipt" v-model="settings.receiptEmailOption" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label for="email-receipt" class="ml-2 block text-sm text-gray-900">
                    Offer email receipt option
                  </label>
                </div>
              </div>
            </div>
  
            <!-- Payment Methods -->
            <div v-if="activeTab === 'payment'">
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-bold">Payment Methods</h2>
                <button 
                  class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                  Add Payment Method
                </button>
              </div>
              
              <div class="space-y-4">
                <div v-for="(method, index) in settings.paymentMethods" :key="index" class="flex justify-between items-center p-4 border rounded-md">
                  <div>
                    <h3 class="font-medium">{{ method.name }}</h3>
                    <p class="text-sm text-gray-500">{{ method.description }}</p>
                  </div>
                  <div class="flex items-center">
                    <div class="mr-4">
                      <label class="relative inline-flex items-center cursor-pointer">
                        <input type="checkbox" v-model="method.enabled" class="sr-only peer">
                        <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                      </label>
                    </div>
                    <button class="text-gray-400 hover:text-gray-500">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
  
            <!-- User Management -->
            <div v-if="activeTab === 'users'">
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-bold">User Management</h2>
                <button 
                  class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                  Add User
                </button>
              </div>
              
              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(user, index) in settings.users" :key="index">
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="flex items-center">
                          <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold">
                            {{ user.name.charAt(0) }}
                          </div>
                          <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">{{ user.name }}</div>
                          </div>
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-gray-900">{{ user.email }}</div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" :class="user.role === 'admin' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'">
                          {{ user.role }}
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" :class="user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                          {{ user.active ? 'Active' : 'Inactive' }}
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <button class="text-blue-600 hover:text-blue-900 mr-3">Edit</button>
                        <button class="text-red-600 hover:text-red-900">Delete</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            
            <!-- Backup & Restore -->
            <div v-if="activeTab === 'backup'">
              <h2 class="text-xl font-bold mb-6">Backup & Restore</h2>
              
              <div class="space-y-8">
                <div>
                  <h3 class="text-lg font-medium mb-4">Database Backup</h3>
                  <p class="text-sm text-gray-600 mb-4">
                    Create a backup of your database to prevent data loss. We recommend doing regular backups.
                  </p>
                  <button 
                    class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                  >
                    Create Backup
                  </button>
                </div>
                
                <div>
                  <h3 class="text-lg font-medium mb-4">Automatic Backups</h3>
                  <div class="flex items-center mb-4">
                    <input id="auto-backup" v-model="settings.autoBackup" type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                    <label for="auto-backup" class="ml-2 block text-sm text-gray-900">
                      Enable automatic backups
                    </label>
                  </div>
                  <div class="flex items-center">
                    <label for="backup-frequency" class="block text-sm font-medium text-gray-700 mr-4">Frequency:</label>
                    <select id="backup-frequency" v-model="settings.backupFrequency" class="mt-1 block w-48 pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                      <option value="daily">Daily</option>
                      <option value="weekly">Weekly</option>
                      <option value="monthly">Monthly</option>
                    </select>
                  </div>
                </div>
                
                <div>
                  <h3 class="text-lg font-medium mb-4">Restore Database</h3>
                  <p class="text-sm text-gray-600 mb-4">
                    Restore your database from a previous backup. This will overwrite your current data.
                  </p>
                  <div class="mt-1 flex items-center">
                    <input type="file" class="sr-only" id="backup-file">
                    <label for="backup-file" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 cursor-pointer">
                      Choose Backup File
                    </label>
                    <span class="ml-4 text-sm text-gray-500">No file selected</span>
                  </div>
                  <button 
                    class="mt-4 px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
                  >
                    Restore Backup
                  </button>
                </div>
              </div>
            </div>
  
            <!-- Save Button (always visible) -->
            <div class="mt-8 border-t pt-8">
              <button 
                @click="saveSettings" 
                class="px-6 py-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                :disabled="saving"
              >
                {{ saving ? 'Saving...' : 'Save Settings' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  
  export default {
    setup() {
      const activeTab = ref('general');
      const saving = ref(false);
      
      // Sample settings data
      const settings = ref({
        // General settings
        language: 'en',
        currency: 'USD',
        dateFormat: 'MM/DD/YYYY',
        timeFormat: '12',
        autoLogout: true,
        
        // Company information
        companyName: 'Retail POS System',
        companyAddress: '123 Main Street, Manchester, M1 1AB',
        companyPhone: '0161 123 4567',
        companyEmail: 'contact@retailpos.com',
        companyTaxId: 'GB123456789',
        companyLogo: null,
        
        // Tax settings
        taxRate: 20,
        reducedTaxRate: 5,
        pricesIncludeTax: true,
        
        // Receipt settings
        receiptHeader: 'Retail POS System\n123 Main Street\nManchester, M1 1AB',
        receiptFooter: 'Thank you for your purchase!\nPlease keep this receipt for your records.',
        receiptShowBarcode: true,
        receiptPrintDuplicate: false,
        receiptEmailOption: true,
        
        // Payment methods
        paymentMethods: [
          { name: 'Credit Card', description: 'Visa, Mastercard, Amex', enabled: true },
          { name: 'Cash', description: 'Physical currency', enabled: true },
          { name: 'Mobile Payment', description: 'Apple Pay, Google Pay', enabled: true },
          { name: 'Bank Transfer', description: 'Direct bank transfers', enabled: false }
        ],
        
        // Users
        users: [
          { name: 'Admin User', email: 'admin@example.com', role: 'admin', active: true },
          { name: 'Cashier User', email: 'cashier@example.com', role: 'cashier', active: true },
          { name: 'Manager User', email: 'manager@example.com', role: 'admin', active: false }
        ],
        
        // Backup settings
        autoBackup: true,
        backupFrequency: 'daily'
      });
      
      // Load settings from local storage if available
      onMounted(() => {
        const savedSettings = localStorage.getItem('posSettings');
        if (savedSettings) {
          try {
            settings.value = JSON.parse(savedSettings);
          } catch (e) {
            console.error('Error parsing saved settings:', e);
          }
        }
      });
      
      // Save settings
      const saveSettings = async () => {
        saving.value = true;
        
        try {
          // In a real app, you would make an API call to save settings
          // For this demo, we'll just save to localStorage
          await new Promise(resolve => setTimeout(resolve, 800)); // Simulate API delay
          
          localStorage.setItem('posSettings', JSON.stringify(settings.value));
          
          // Show success notification (in a real app)
          alert('Settings saved successfully!');
        } catch (error) {
          console.error('Error saving settings:', error);
          alert('Failed to save settings. Please try again.');
        } finally {
          saving.value = false;
        }
      };
      
      return {
        activeTab,
        settings,
        saving,
        saveSettings
      };
    }
  };
  </script>