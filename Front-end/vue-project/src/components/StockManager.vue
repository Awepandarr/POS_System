<!-- src/components/StockManager.vue -->
<template>
    <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
      <div class="p-4 bg-gradient-to-r from-green-500 to-green-600 text-white">
        <h3 class="text-lg font-semibold flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
          </svg>
          Stock Management
        </h3>
      </div>
      
      <div class="p-4">
        <!-- Filter and Search Controls -->
        <div class="flex flex-col md:flex-row gap-3 mb-4">
          <div class="relative flex-grow">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Search products..." 
              class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
            <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          
          <div class="flex gap-2">
            <select 
              v-model="categoryFilter"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">All Categories</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
            
            <select 
              v-model="stockFilter"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">All Stock</option>
              <option value="low">Low Stock</option>
              <option value="out">Out of Stock</option>
            </select>
          </div>
        </div>
        
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-green-500"></div>
        </div>
        
        <!-- Products Table -->
        <div v-else-if="filteredProducts.length > 0" class="overflow-x-auto">
          <table class="min-w-full border-collapse">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-200">
                <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Current Stock</th>
                <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="product in filteredProducts" :key="product.productId" :class="{'bg-red-50': product.stockQuantity === 0, 'bg-yellow-50': product.stockQuantity > 0 && product.stockQuantity <= lowStockThreshold}">
                <td class="py-3 px-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="h-10 w-10 flex-shrink-0 mr-3">
                      <img 
                        v-if="product.image_url" 
                        :src="product.image_url" 
                        class="h-10 w-10 rounded-full object-cover"
                        @error="handleImageError($event, product)"
                      />
                      <div 
                        v-else 
                        class="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                      </div>
                    </div>
                    <div>
                      <div class="font-medium text-gray-900">{{ product.name }}</div>
                      <div class="text-sm text-gray-500">ID: {{ product.productId }}</div>
                    </div>
                  </div>
                </td>
                <td class="py-3 px-4 whitespace-nowrap">
                  <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
                    {{ getCategoryName(product.categoryId) }}
                  </span>
                </td>
                <td class="py-3 px-4 whitespace-nowrap text-gray-900">
                  {{ formatCurrency(product.price) }}
                </td>
                <td class="py-3 px-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <span 
                      :class="{
                        'px-2 py-1 text-xs rounded-full': true,
                        'bg-red-100 text-red-800': product.stockQuantity === 0,
                        'bg-yellow-100 text-yellow-800': product.stockQuantity > 0 && product.stockQuantity <= lowStockThreshold,
                        'bg-green-100 text-green-800': product.stockQuantity > lowStockThreshold
                      }"
                    >
                      {{ product.stockQuantity }} in stock
                    </span>
                  </div>
                </td>
                <td class="py-3 px-4 whitespace-nowrap text-right text-sm font-medium">
                  <button 
                    @click="openStockModal(product)" 
                    class="text-green-600 hover:text-green-900 mr-3"
                  >
                    Adjust
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- No Products State -->
        <div v-else class="text-center py-8">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
          </svg>
          <p class="text-gray-500 mb-4">No products found matching your criteria</p>
        </div>
        
        <!-- Batch Stock Update Button -->
        <div class="mt-4 flex justify-end">
          <button
            @click="showLowStockProducts"
            class="px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition mr-2 flex items-center gap-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            Low Stock Report
          </button>
          <button
            @click="openBatchUpdateModal"
            class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition flex items-center gap-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
            </svg>
            Batch Update
          </button>
        </div>
      </div>
      
      <!-- Stock Adjustment Modal -->
      <div v-if="stockModal.show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg w-full max-w-md mx-4">
          <div class="p-6 border-b">
            <h2 class="text-xl font-bold">Adjust Stock for {{ stockModal.product?.name }}</h2>
          </div>
          <div class="p-6">
            <div class="mb-4">
              <div class="flex justify-between items-center mb-1">
                <label class="block text-sm font-medium text-gray-700">Current Stock</label>
                <span 
                  :class="{
                    'px-2 py-1 text-xs rounded-full': true,
                    'bg-red-100 text-red-800': stockModal.product?.stockQuantity === 0,
                    'bg-yellow-100 text-yellow-800': stockModal.product?.stockQuantity > 0 && stockModal.product?.stockQuantity <= lowStockThreshold,
                    'bg-green-100 text-green-800': stockModal.product?.stockQuantity > lowStockThreshold
                  }"
                >
                  {{ stockModal.product?.stockQuantity || 0 }} in stock
                </span>
              </div>
              <input 
                type="number" 
                v-model="stockModal.newQuantity" 
                min="0"
                class="w-full p-2 border rounded"
              >
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">Adjustment Type</label>
              <div class="flex gap-3">
                <label class="flex items-center cursor-pointer">
                  <input 
                    type="radio" 
                    v-model="stockModal.adjustmentType" 
                    value="set" 
                    class="mr-2"
                  >
                  Set Exact Quantity
                </label>
                <label class="flex items-center cursor-pointer">
                  <input 
                    type="radio" 
                    v-model="stockModal.adjustmentType" 
                    value="add"
                    class="mr-2"
                  >
                  Add to Stock
                </label>
              </div>
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">Notes (Optional)</label>
              <textarea 
                v-model="stockModal.notes" 
                rows="2"
                class="w-full p-2 border rounded"
                placeholder="Reason for adjustment"
              ></textarea>
            </div>
            
            <div class="mt-6 flex justify-end space-x-3">
              <button 
                type="button"
                @click="closeStockModal" 
                class="px-4 py-2 border rounded-md hover:bg-gray-50"
              >
                Cancel
              </button>
              <button 
                type="button"
                @click="updateStock" 
                class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                :disabled="updating"
              >
                {{ updating ? 'Updating...' : 'Update Stock' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Batch Update Modal -->
      <div v-if="batchModal.show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg w-full max-w-4xl mx-4">
          <div class="p-6 border-b">
            <h2 class="text-xl font-bold">Batch Stock Update</h2>
          </div>
          <div class="p-6">
            <div class="max-h-96 overflow-y-auto">
              <table class="min-w-full border-collapse">
                <thead>
                  <tr class="bg-gray-50 border-b">
                    <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                    <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Current Stock</th>
                    <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">New Quantity</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="(item, index) in batchModal.items" :key="index">
                    <td class="py-3 px-4">
                      <div class="flex items-center">
                        <div class="h-8 w-8 flex-shrink-0 mr-3">
                          <img 
                            v-if="item.product.image_url" 
                            :src="item.product.image_url" 
                            class="h-8 w-8 rounded-full object-cover"
                            @error="handleImageError($event, item.product)"
                          />
                          <div 
                            v-else 
                            class="h-8 w-8 rounded-full bg-gray-100 flex items-center justify-center"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                          </div>
                        </div>
                        <div>
                          <div class="font-medium text-gray-900">{{ item.product.name }}</div>
                          <div class="text-sm text-gray-500">ID: {{ item.product.productId }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="py-3 px-4">
                      <span 
                        :class="{
                          'px-2 py-1 text-xs rounded-full': true,
                          'bg-red-100 text-red-800': item.product.stockQuantity === 0,
                          'bg-yellow-100 text-yellow-800': item.product.stockQuantity > 0 && item.product.stockQuantity <= lowStockThreshold,
                          'bg-green-100 text-green-800': item.product.stockQuantity > lowStockThreshold
                        }"
                      >
                        {{ item.product.stockQuantity }} in stock
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <input 
                        type="number" 
                        v-model="item.newQuantity" 
                        min="0"
                        class="w-32 p-2 border rounded"
                      >
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="mt-6 flex justify-end space-x-3">
              <button 
                type="button"
                @click="closeBatchModal" 
                class="px-4 py-2 border rounded-md hover:bg-gray-50"
              >
                Cancel
              </button>
              <button 
                type="button"
                @click="updateBatchStock" 
                class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                :disabled="updating"
              >
                {{ updating ? 'Updating...' : 'Update All' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Low Stock Report Modal -->
      <div v-if="lowStockModal.show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg w-full max-w-2xl mx-4">
          <div class="p-6 border-b bg-yellow-50">
            <h2 class="text-xl font-bold text-yellow-800 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              Low Stock Report
            </h2>
          </div>
          <div class="p-6">
            <div class="mb-4">
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">Threshold Level</label>
                <span class="px-2 py-1 text-xs rounded-full bg-yellow-100 text-yellow-800">
                  {{ lowStockThreshold }} units
                </span>
              </div>
              <input 
                type="range" 
                v-model="lowStockThreshold" 
                min="1" 
                max="50" 
                class="w-full"
              >
            </div>
            
            <div class="mb-4">
              <p class="text-gray-700 mb-2">
                <span class="font-medium">{{ lowStockModal.products.length }}</span> products are below the threshold
              </p>
              
              <div class="max-h-96 overflow-y-auto">
                <table class="min-w-full border-collapse">
                  <thead>
                    <tr class="bg-gray-50 border-b">
                      <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                      <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                      <th class="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="product in lowStockModal.products" :key="product.productId">
                      <td class="py-3 px-4">
                        <div class="flex items-center">
                          <div>
                            <div class="font-medium text-gray-900">{{ product.name }}</div>
                            <div class="text-sm text-gray-500">{{ getCategoryName(product.categoryId) }}</div>
                          </div>
                        </div>
                      </td>
                      <td class="py-3 px-4">
                        <span 
                          :class="{
                            'px-2 py-1 text-xs rounded-full': true,
                            'bg-red-100 text-red-800': product.stockQuantity === 0,
                            'bg-yellow-100 text-yellow-800': product.stockQuantity > 0
                          }"
                        >
                          {{ product.stockQuantity }} in stock
                        </span>
                      </td>
                      <td class="py-3 px-4">
                        <button 
                          @click="openStockModal(product); closeLowStockModal();" 
                          class="text-green-600 hover:text-green-900"
                        >
                          Restock
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            
            <div class="mt-4 flex justify-between">
              <button 
                @click="openBatchUpdateModalForLowStock" 
                class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
              >
                Restock All
              </button>
              
              <button 
                type="button"
                @click="closeLowStockModal" 
                class="px-4 py-2 border rounded-md hover:bg-gray-50"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, watch } from 'vue';
  import { productService } from '../services/api.service';
  import {stockService} from '../services/stock.service';
  import { updateProductStock, addToStock, batchUpdateStock, getLowStockProducts } from '../services/StockManager';
  
  export default {
    props: {
      categories: {
        type: Array,
        default: () => [
          { id: 1, name: 'Electronics' },
          { id: 2, name: 'Clothing' },
          { id: 3, name: 'Home & Kitchen' },
          { id: 4, name: 'Books' }
        ]
      },
      initialThreshold: {
        type: Number,
        default: 10
      }
    },
    
    setup(props) {
      const products = ref([]);
      const loading = ref(true);
      const updating = ref(false);
      const searchQuery = ref('');
      const categoryFilter = ref('all');
      const stockFilter = ref('all');
      const lowStockThreshold = ref(props.initialThreshold);
      
      // Stock adjustment modal
      const stockModal = ref({
        show: false,
        product: null,
        newQuantity: 0,
        adjustmentType: 'set',
        notes: ''
      });
      
      // Batch update modal
      const batchModal = ref({
        show: false,
        items: []
      });
      
      // Low stock report modal
      const lowStockModal = ref({
        show: false,
        products: []
      });
      
      // Fetch products
      const fetchProducts = async () => {
        loading.value = true;
        try {
          const response = await productService.getAllProducts();
          products.value = response.data || [];
        } catch (error) {
          console.error('Error fetching products:', error);
        } finally {
          loading.value = false;
        }
      };
      
      // Filter products based on search, category, and stock filters
      const filteredProducts = computed(() => {
        let result = [...products.value];
        
        // Filter by search query
        if (searchQuery.value.trim()) {
          const query = searchQuery.value.toLowerCase();
          result = result.filter(product => 
            product.name.toLowerCase().includes(query) ||
            product.productId.toString().includes(query) ||
            product.barcode?.toLowerCase().includes(query)
          );
        }
        
        // Filter by category
        if (categoryFilter.value !== 'all') {
          result = result.filter(product => product.categoryId === parseInt(categoryFilter.value));
        }
        
        // Filter by stock level
        if (stockFilter.value === 'low') {
          result = result.filter(product => product.stockQuantity <= lowStockThreshold.value && product.stockQuantity > 0);
        } else if (stockFilter.value === 'out') {
          result = result.filter(product => product.stockQuantity === 0);
        }
        
        return result;
      });
      
      // Format currency values
      const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-GB', {
          style: 'currency',
          currency: 'GBP',
        }).format(amount);
      };
      
      // Get category name by ID
      const getCategoryName = (categoryId) => {
        const category = props.categories.find(c => c.id === categoryId);
        return category ? category.name : 'Unknown';
      };
      
      // Handle image loading errors
      const handleImageError = (event, product) => {
        event.target.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
        product.image_url = null;
      };
      
      // Open stock adjustment modal
      const openStockModal = (product) => {
        stockModal.value = {
          show: true,
          product: { ...product },
          newQuantity: product.stockQuantity,
          adjustmentType: 'set',
          notes: ''
        };
      };
      
      // Close stock adjustment modal
      const closeStockModal = () => {
        stockModal.value.show = false;
      };
      
      // Update stock for a single product
      const updateStock = async () => {
        if (!stockModal.value.product) return;
        
        updating.value = true;
        
        try {
          const product = stockModal.value.product;
          const productId = product.productId;
          
          if (stockModal.value.adjustmentType === 'set') {
            // Set exact quantity
            await updateProductStock(productId, stockModal.value.newQuantity);
          } else {
            // Add to stock
            const adjustment = stockModal.value.newQuantity;
            await addToStock(productId, adjustment);
          }
          
          // Refresh product list
          await fetchProducts();
          
          // Close modal
          closeStockModal();
        } catch (error) {
          console.error('Error updating stock:', error);
          alert('Error updating stock. Please try again.');
        } finally {
          updating.value = false;
        }
      };
      
      // Open batch update modal
      const openBatchUpdateModal = () => {
        // Initialize batch update with all products
        batchModal.value = {
          show: true,
          items: products.value.map(product => ({
            product: { ...product },
            newQuantity: product.stockQuantity
          }))
        };
      };
      
      // Open batch update modal with only low stock products
      const openBatchUpdateModalForLowStock = () => {
        // Initialize batch update with low stock products
        batchModal.value = {
          show: true,
          items: lowStockModal.value.products.map(product => ({
            product: { ...product },
            newQuantity: product.stockQuantity + 10 // Default to adding 10 items
          }))
        };
        
        // Close low stock modal
        closeLowStockModal();
      };
      
      // Close batch update modal
      const closeBatchModal = () => {
        batchModal.value.show = false;
      };
      
      // Update stock for multiple products
      const updateBatchStock = async () => {
        updating.value = true;
        
        try {
          // Format data for batch update
          const updateData = batchModal.value.items.map(item => ({
            productId: item.product.productId,
            stockQuantity: item.newQuantity
          }));
          
          // Send batch update request
          await batchUpdateStock(updateData);
          
          // Refresh product list
          await fetchProducts();
          
          // Close modal
          closeBatchModal();
        } catch (error) {
          console.error('Error updating batch stock:', error);
          alert('Error updating stock. Please try again.');
        } finally {
          updating.value = false;
        }
      };
      
      // Show low stock products report
      const showLowStockProducts = async () => {
        try {
          // Get products below threshold
          lowStockModal.value.products = products.value.filter(p => p.stockQuantity <= lowStockThreshold.value);
          
          // Show modal
          lowStockModal.value.show = true;
        } catch (error) {
          console.error('Error fetching low stock products:', error);
        }
      };
      
      // Close low stock modal
      const closeLowStockModal = () => {
        lowStockModal.value.show = false;
      };
      
      // Fetch products on component mount
      onMounted(() => {
        fetchProducts();
      });
      
      // Watch for filter changes and update product list
      watch([searchQuery, categoryFilter, stockFilter], () => {
        // No need to refetch, since we're just filtering
      });
      
      // Watch for threshold changes and update low stock report
      watch(lowStockThreshold, () => {
        if (lowStockModal.value.show) {
          showLowStockProducts();
        }
      });
      
      return {
        products,
        loading,
        updating,
        searchQuery,
        categoryFilter,
        stockFilter,
        lowStockThreshold,
        filteredProducts,
        stockModal,
        batchModal,
        lowStockModal,
        fetchProducts,
        formatCurrency,
        getCategoryName,
        handleImageError,
        openStockModal,
        closeStockModal,
        updateStock,
        openBatchUpdateModal,
        openBatchUpdateModalForLowStock,
        closeBatchModal,
        updateBatchStock,
        showLowStockProducts,
        closeLowStockModal
      };
    }
  };
  </script>