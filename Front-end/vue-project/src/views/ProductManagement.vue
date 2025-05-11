<!-- src/views/ProductManagement.vue -->
<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden">
    <div class="p-8 bg-gradient-to-r from-blue-500 to-blue-600 text-white flex justify-between items-center">
      <h1 class="text-3xl font-bold">Product Management</h1>
      <button 
        @click="showAddProductModal = true" 
        class="bg-white text-blue-600 font-semibold px-6 py-3 rounded-lg hover:bg-blue-50 transition duration-300"
      >
        Add Product
      </button>
    </div>

    <!-- Search and Filter -->
    <div class="p-8 border-b">
      <div class="flex flex-col md:flex-row gap-6">
        <div class="flex-1">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search products..." 
            class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            @input="searchProducts"
          >
        </div>
        <div class="flex gap-4">
          <select 
            v-model="filterCategory" 
            class="px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option value="all">All Categories</option>
            <option value="1">Electronics</option>
            <option value="2">Clothing</option>
            <option value="3">Home & Kitchen</option>
            <option value="4">Books</option>
          </select>
          <select 
            v-model="sortBy" 
            class="px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option value="name">Sort by Name</option>
            <option value="price">Sort by Price</option>
            <option value="stock">Sort by Stock</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Product List -->
    <div class="p-8">
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-600 mx-auto"></div>
        <p class="mt-4 text-xl text-gray-500">Loading products...</p>
      </div>

      <div v-else-if="filteredProducts.length === 0" class="text-center py-12">
        <div class="text-gray-400 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
        <p class="text-2xl text-gray-500">No products found</p>
        <button 
          @click="showAddProductModal = true" 
          class="mt-6 bg-blue-500 text-white font-semibold px-6 py-3 rounded-lg hover:bg-blue-600 transition duration-300"
        >
          Add Your First Product
        </button>
      </div>

      <div v-else>
        <!-- Product table and pagination code remains the same as in previous versions -->
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Barcode</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="product in paginatedProducts" :key="product.productId" class="hover:bg-gray-100 transition duration-300">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="h-12 w-12 rounded-full bg-gray-100 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                      </svg>
                    </div>
                    <div class="ml-4">
                      <div class="text-lg font-medium text-gray-900">{{ product.name }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                    {{ getCategoryName(product.categoryId) }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-lg text-gray-900">
                  £{{ product.price.toFixed(2) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-lg">
                  <span 
                    :class="[
                      'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                      product.stockQuantity > 10 ? 'bg-green-100 text-green-800' : 
                      product.stockQuantity > 5 ? 'bg-yellow-100 text-yellow-800' : 
                      'bg-red-100 text-red-800'
                    ]"
                  >
                    {{ product.stockQuantity }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-lg text-gray-500">
                  {{ product.barcode }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-lg">
                  <button 
                    @click="editProduct(product)" 
                    class="text-blue-600 hover:text-blue-800 font-semibold mr-4 focus:outline-none"
                  >
                    Edit
                  </button>
                  <button 
                    @click="confirmDeleteProduct(product)" 
                    class="text-red-600 hover:text-red-800 font-semibold focus:outline-none"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="flex justify-between items-center mt-8">
          <div class="text-lg text-gray-500">
            Showing {{ pagination.start + 1 }} to {{ Math.min(pagination.end, filteredProducts.length) }} of {{ filteredProducts.length }} products
          </div>
          <div class="flex space-x-4">
            <button 
              @click="prevPage" 
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-300 focus:outline-none"
              :disabled="pagination.page === 1"
            >
              Previous
            </button>
            <button 
              @click="nextPage" 
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-300 focus:outline-none"
              :disabled="pagination.page === totalPages"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Product Modal -->
    <div v-if="showAddProductModal || showEditProductModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4">
        <div class="p-6 border-b">
          <h2 class="text-2xl font-bold">{{ showEditProductModal ? 'Edit Product' : 'Add New Product' }}</h2>
        </div>
        <div class="p-6">
          <form @submit.prevent="submitProductForm">
            <div class="space-y-6">
              <div>
                <label class="block text-lg font-medium text-gray-700 mb-1">Product Name</label>
                <input 
                  type="text" 
                  v-model="productForm.name" 
                  class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                  required
                >
              </div>
              <div>
                <label class="block text-lg font-medium text-gray-700 mb-1">Category</label>
                <select 
                  v-model="productForm.categoryId" 
                  class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                  required
                >
                  <option value="1">Electronics</option>
                  <option value="2">Clothing</option>
                  <option value="3">Home & Kitchen</option>
                  <option value="4">Books</option>
                </select>
              </div>
              <div>
  <label class="block text-lg font-medium text-gray-700 mb-1">Product Image URL</label>
  <input 
    type="text" 
    v-model="productForm.imageUrl" 
    placeholder="Enter image URL here" 
    class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
  >
  
  <!-- Optional Image Preview -->
  <div v-if="productForm.imageUrl" class="mt-4">
    <div class="relative w-32 h-32 border rounded-lg overflow-hidden">
      <img 
        :src="productForm.imageUrl" 
        alt="Product Preview" 
        class="w-full h-full object-cover"
        @error="handleImagePreviewError"
      />
    </div>
  </div>
</div>
              <div>
                <label class="block text-lg font-medium text-gray-700 mb-1">Price</label>
                <input 
                  type="number" 
                  v-model="productForm.price" 
                  step="0.01" 
                  min="0.01" 
                  class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                  required
                >
              </div>
              <div>
                <label class="block text-lg font-medium text-gray-700 mb-1">Stock Quantity</label>
                <input 
                  type="number" 
                  v-model="productForm.stockQuantity" 
                  min="0" 
                  class="w-full px-4 py-3 text-gray-700 bg-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                  required
                >
              </div>
              <div>
                <label class="block text-lg font-medium text-gray-700 mb-1">Barcode</label>
                <div class="flex">
                  <input 
                    id="product-barcode-field"
                    type="text" 
                    v-model="productForm.barcode" 
                    class="flex-1 px-4 py-3 text-gray-700 bg-gray-100 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition-colors duration-300"
                    required
                  >
                  <button 
                    type="button"
                    @click="activateBarcodeScanner" 
                    class="flex items-center justify-center px-4 py-3 bg-blue-500 text-white rounded-r-lg hover:bg-blue-600 transition duration-300 focus:outline-none"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1v-2a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1z" />
                    </svg>
                    <span class="ml-2">Scan</span>
                  </button>
                </div>
                
                <!-- Video Feed and Scanner Status -->
                <div v-if="scannerConnected" class="mt-4">
                  <div class="relative bg-gray-100 rounded-lg overflow-hidden border border-gray-200">
                    <img 
                      :src="videoFeedUrl" 
                      alt="Barcode Scanner Feed" 
                      class="w-full h-auto max-h-64 object-cover"
                      @error="handleVideoError"
                    />
                    
                    <!-- Scanning Guide Overlay -->
                    <div class="border-2 border-red-500 w-3/4 h-1/2 max-w-md flex items-center justify-center relative">
                        <div class="absolute inset-0 border-2 border-red-500 opacity-30 animate-pulse"></div>
                        <div class="text-sm text-white bg-black bg-opacity-60 px-3 py-1 rounded-lg">
                          Position barcode here
                        </div>
                      </div>
                    </div>
                    
                    <div class="mt-2 text-sm text-green-600 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                      Scanner connected. Ready to scan barcode.
                    </div>
                  </div>
                  <div v-else class="mt-4 bg-gray-50 p-4 rounded-lg text-center border border-gray-200">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                    <p class="text-gray-600 mb-4">Camera feed not available</p>
                    <button 
                      @click="activateBarcodeScanner" 
                      class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md flex items-center gap-2 mx-auto"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                      </svg>
                      Connect Scanner
                    </button>
                  </div>
                </div>
              </div>
              <div class="mt-8 flex justify-end space-x-4">
                <button 
                  type="button"
                  @click="closeProductModal" 
                  class="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-300 focus:outline-none"
                >
                  Cancel
                </button>
                <button 
                  type="submit"
                  class="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-300 focus:outline-none"
                  :disabled="submitting"
                >
                  {{ submitting ? 'Saving...' : 'Save Product' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Delete Confirmation Modal -->
      <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-lg w-full max-w-md mx-4">
          <div class="p-6">
            <h2 class="text-2xl font-bold mb-4">Confirm Delete</h2>
            <p class="text-lg">Are you sure you want to delete product <strong>{{ productToDelete?.name }}</strong>? This action cannot be undone.</p>
          </div>
          <div class="px-6 py-4 bg-gray-100 flex justify-end space-x-4 rounded-b-lg">
            <button 
              @click="showDeleteModal = false" 
              class="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-300 focus:outline-none"
            >
              Cancel
            </button>
            <button 
              @click="deleteProduct" 
              class="px-6 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition duration-300 focus:outline-none"
              :disabled="submitting"
            >
              {{ submitting ? 'Deleting...' : 'Delete' }}
            </button>
          </div>
        </div>
      </div>
    </div>
</template>
  
<script>
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { productService } from '../services/api.service';
import axios from 'axios';

// Barcode scanner API client
const barcodeApiClient = axios.create({
  baseURL: 'http://localhost:5173', // Adjust to your barcode scanner service URL
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});

export default {
  setup() {
    // Barcode scanner state
    const scannerConnected = ref(false);
    const scannerPolling = ref(null);
    const lastScannedBarcode = ref(null);
    const videoFeedUrl = ref('');
    // Product management state
    const products = ref([]);
    const loading = ref(true);
    const searchQuery = ref('');
    const filterCategory = ref('all');
    const sortBy = ref('name');
    const showAddProductModal = ref(false);
    const showEditProductModal = ref(false);
    const showDeleteModal = ref(false);
    const submitting = ref(false);
    const productToDelete = ref(null);
    
    const productForm = ref({
      productId: null,
      name: '',
      categoryId: 1,
      price: 0,
      stockQuantity: 0,
      barcode: ''
    });

    // Pagination
    const pagination = ref({
      page: 1,
      perPage: 10,
      start: 0,
      end: 10
    });
    const handleImagePreviewError = (event) => {
  // Replace with a placeholder image if the URL is invalid
  event.target.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="%23CBD5E0" class="w-full h-full"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>';
};
    // Handle video feed error
    const handleVideoError = () => {
      scannerConnected.value = false;
      videoFeedUrl.value = '';
      stopPolling();
    };

    // Set video feed URL 
    const setVideoFeedUrl = () => {
      videoFeedUrl.value = '/video_feed'; // Adjust based on your backend video feed endpoint
    };

    // Barcode scanner methods
    const connectScanner = async () => {
      try {
        const response = await barcodeApiClient.get('/health');
        if (response.data.status === 'OK') {
          scannerConnected.value = true;
          setVideoFeedUrl(); // Set video feed URL when scanner connects
          startPolling();
          return true;
        } else {
          return false;
        }
      } catch (err) {
        console.error('Error connecting to scanner:', err);
        return false;
      }
    };

    const startPolling = () => {
      // Clear any existing polling interval
      if (scannerPolling.value) clearInterval(scannerPolling.value);
      
      scannerPolling.value = setInterval(async () => {
        try {
          const response = await barcodeApiClient.get('/latest_barcode');
          if (response.data && !response.data.error) {
            const newBarcode = response.data;
            
            // Only process if this is a new barcode
            if (!lastScannedBarcode.value || 
                lastScannedBarcode.value.data !== newBarcode.data) {
              lastScannedBarcode.value = newBarcode;
              
              // If product form is open, set barcode value
              if (showAddProductModal.value || showEditProductModal.value) {
                productForm.value.barcode = newBarcode.data;
                
                // Optional: provide visual/audio feedback
                // For example, flash the barcode field to indicate successful scan
                const barcodeField = document.getElementById('product-barcode-field');
                if (barcodeField) {
                  barcodeField.classList.add('bg-green-100');
                  setTimeout(() => {
                    barcodeField.classList.remove('bg-green-100');
                  }, 1000);
                }
              }
            }
          }
        } catch (err) {
          console.error('Error polling for barcode:', err);
        }
      }, 1000); // Poll every second
    };

    // Stop polling (for cleanup)
    const stopPolling = () => {
      if (scannerPolling.value) {
        clearInterval(scannerPolling.value);
        scannerPolling.value = null;
      }
    };

    const activateBarcodeScanner = async () => {
      if (!scannerConnected.value) {
        const connected = await connectScanner();
        if (connected) {
          alert('Barcode scanner connected! Scan a barcode to fill the field.');
        } else {
          alert('Could not connect to barcode scanner. Please check if the scanner is properly connected.');
        }
      } else {
        alert('Barcode scanner is already connected. Scan a barcode to fill the field.');
      }
    };

    // Fetch products from API
    const fetchProducts = async () => {
      loading.value = true;
      try {
        const response = await productService.getAllProducts();
        products.value = response.data || [];
        console.log('Products loaded:', products.value);
      } catch (error) {
        console.error('Error fetching products:', error);
        // Fallback to sample data if API fails
        products.value = [
          { productId: 1, name: 'Laptop', price: 999.99, categoryId: 1, stockQuantity: 10, barcode: '123456789' },
          { productId: 2, name: 'Smartphone', price: 499.99, categoryId: 1, stockQuantity: 15, barcode: '234567890' },
          { productId: 3, name: 'T-Shirt', price: 19.99, categoryId: 2, stockQuantity: 50, barcode: '345678901' }
        ];
      } finally {
        loading.value = false;
      }
    };

    // Category name mapping
    const getCategoryName = (categoryId) => {
      const categories = {
        1: 'Electronics',
        2: 'Clothing',
        3: 'Home & Kitchen',
        4: 'Books'
      };
      return categories[categoryId] || 'Unknown';
    };

    // Filtered and computed products
    const filteredProducts = computed(() => {
      let result = products.value;
      
      // Filter by category
      if (filterCategory.value !== 'all') {
        result = result.filter(product => product.categoryId.toString() === filterCategory.value);
      }
      
      // Filter by search query
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(product => 
          product.name.toLowerCase().includes(query) || 
          (product.barcode && product.barcode.includes(query))
        );
      }
      
      // Sort products
      if (sortBy.value === 'name') {
        result = [...result].sort((a, b) => a.name.localeCompare(b.name));
      } else if (sortBy.value === 'price') {
        result = [...result].sort((a, b) => a.price - b.price);
      } else if (sortBy.value === 'stock') {
        result = [...result].sort((a, b) => a.stockQuantity - b.stockQuantity);
      }
      
      return result;
    });

    // Pagination computations
    const totalPages = computed(() => {
      return Math.ceil(filteredProducts.value.length / pagination.value.perPage);
    });

    const paginatedProducts = computed(() => {
      return filteredProducts.value.slice(pagination.value.start, pagination.value.end);
    });

    // Pagination methods
    const updatePagination = () => {
      pagination.value.start = (pagination.value.page - 1) * pagination.value.perPage;
      pagination.value.end = pagination.value.start + pagination.value.perPage;
    };

    const nextPage = () => {
      if (pagination.value.page < totalPages.value) {
        pagination.value.page++;
        updatePagination();
      }
    };

    const prevPage = () => {
      if (pagination.value.page > 1) {
        pagination.value.page--;
        updatePagination();
      }
    };

    // Reset pagination when filters change
    watch([searchQuery, filterCategory, sortBy], () => {
      pagination.value.page = 1;
      updatePagination();
    });

    // Product CRUD operations
    const searchProducts = () => {
      pagination.value.page = 1;
      updatePagination();
    };

    const editProduct = (product) => {
      productForm.value = { ...product };
      showEditProductModal.value = true;
    };

    const confirmDeleteProduct = (product) => {
      productToDelete.value = product;
      showDeleteModal.value = true;
    };

    const deleteProduct = async () => {
      if (!productToDelete.value) return;
      
      submitting.value = true;
      try {
        console.log(`Attempting to delete product with ID: ${productToDelete.value.productId}`);
        
        // Call the API to delete the product
        await productService.deleteProduct(productToDelete.value.productId);
        console.log('Product deleted successfully, updating UI');
        
        // Update the local state to remove the deleted product
        products.value = products.value.filter(p => p.productId !== productToDelete.value.productId);
        
        // Close the modal and clear the selected product
        showDeleteModal.value = false;
        productToDelete.value = null;
      } catch (error) {
        console.error('Error deleting product:', error);
        
        // Log more detailed error information
        if (error.response) {
          console.error('Server responded with:', {
            status: error.response.status,
            data: error.response.data
          });
        } else if (error.request) {
          console.error('No response received from server');
        } else {
          console.error('Error setting up request:', error.message);
        }
        
        alert('Failed to delete product. Please try again.');
      } finally {
        submitting.value = false;
      }
    };

    const submitProductForm = async () => {
      submitting.value = true;
      
      try {
        const productData = {
          name: productForm.value.name,
          price: parseFloat(productForm.value.price),
          categoryId: parseInt(productForm.value.categoryId),
          stockQuantity: parseInt(productForm.value.stockQuantity),
          barcode: productForm.value.barcode,
          image_url: productForm.value.imageUrl || '' // Include an image URL field if you have one
        };
        
        console.log('Submitting product data:', productData);
        
        if (showEditProductModal.value) {
          // Update existing product
          const response = await productService.updateProduct(
            productForm.value.productId,
            productData
          );
          
          console.log('Update product response:', response);
          
          // Update the local state
          const index = products.value.findIndex(p => p.productId === productForm.value.productId);
          if (index !== -1) {
            // You might need to adapt this based on your API response structure
            if (response.data) {
              products.value[index] = response.data;
            } else {
              // If the response doesn't include the updated product, update with our local data
              products.value[index] = { 
                ...products.value[index],
                ...productData,
                productId: productForm.value.productId
              };
            }
          }
        } else {
          // Create new product
          const response = await productService.createProduct(productData);
          console.log('Create product response:', response);
          
          // Add the new product to the local state
          // You might need to adapt this based on your API response structure
          if (response.data) {
            products.value.push(response.data);
          }
        }
        
        // Close the modal and reset the form
        closeProductModal();
        
        // Refresh the product list to ensure we have the latest data
        fetchProducts();
      } catch (error) {
        console.error('Error saving product:', error);
        
        // Log more detailed error information
        if (error.response) {
          console.error('Server responded with:', {
            status: error.response.status,
            data: error.response.data
          });
        } else if (error.request) {
          console.error('No response received from server');
        } else {
          console.error('Error setting up request:', error.message);
        }
        
        alert('Failed to save product. Please try again.');
      } finally {
        submitting.value = false;
      }
    };

    const closeProductModal = () => {
      showAddProductModal.value = false;
      showEditProductModal.value = false;
      productForm.value = {
        productId: null,
        name: '',
        categoryId: 1,
        price: 0,
        stockQuantity: 0,
        barcode: ''
      };
    };

    // Lifecycle hooks
    onMounted(() => {
      fetchProducts();
      connectScanner().then(connected => {
        console.log('Barcode scanner connected:', connected);
      });
    });

    // Cleanup when component is unmounted
    // onUnmounted(() => {
    //   stopPolling();
    // });

    return {
      // Product management state and methods
      products,
      loading,
      searchQuery,
      filterCategory,
      sortBy,
      showAddProductModal,
      showEditProductModal,
      showDeleteModal,
      submitting,
      productToDelete,
      productForm,
      pagination,
      
      // Computed properties
      filteredProducts,
      paginatedProducts,
      totalPages,
      
      // Methods
      fetchProducts,
      getCategoryName,
      searchProducts,
      editProduct,
      confirmDeleteProduct,
      deleteProduct,
      submitProductForm,
      closeProductModal,
      nextPage,
      prevPage,
      
      // Barcode scanner methods and state
      activateBarcodeScanner,
      connectScanner,
      startPolling,
      stopPolling,
      scannerConnected,
      scannerPolling,
      lastScannedBarcode,
      videoFeedUrl,
      handleVideoError,
      setVideoFeedUrl,
      handleImagePreviewError
    };
  }
};
</script>

<style scoped>
/* Optional: Add any scoped styles specific to this component */
@keyframes scanner {
  0% { top: 0; }
  100% { top: 100%; }
}

.animate-scanner {
  animation: scanner 1.5s infinite linear;
  position: absolute;
}
</style>