<!-- src/components/EnhancedBarcodeScanner.vue -->
<template>
    <div class="bg-white shadow-md rounded-lg p-4">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-bold">Barcode Scanner</h3>
        <div 
          class="px-2 py-1 rounded-full text-xs font-medium" 
          :class="scannerStatus ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
        >
          {{ scannerStatus ? 'Connected' : 'Disconnected' }}
        </div>
      </div>
  
      <div class="relative mb-4">
        <input 
          type="text" 
          ref="barcodeInput"
          v-model="barcodeValue" 
          placeholder="Scan or enter barcode" 
          class="w-full p-3 pl-10 border rounded-lg"
          @keyup.enter="searchProduct"
          @focus="inputFocused = true"
          @blur="inputFocused = false"
          autofocus
        >
        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
          </svg>
        </div>
      </div>
  
      <div class="flex space-x-2">
        <button 
          @click="searchProduct" 
          class="flex-1 bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition"
        >
          Search
        </button>
        <button 
          @click="clearInput" 
          class="px-4 py-2 border rounded-md hover:bg-gray-100 transition"
        >
          Clear
        </button>
      </div>
  
      <!-- Live Scan Button -->
      <div class="flex mt-3">
        <button 
          @click="toggleLiveScan" 
          :class="[
            'w-full py-2 rounded-md transition',
            isLiveScanning ? 'bg-yellow-500 hover:bg-yellow-600 text-white' : 'bg-green-500 hover:bg-green-600 text-white'
          ]"
        >
          {{ isLiveScanning ? 'Stop Live Scanning' : 'Start Live Scanning' }}
        </button>
      </div>
  
      <!-- Scanner Animation -->
      <div 
        v-if="inputFocused || isLiveScanning" 
        class="mt-4 h-8 bg-gray-200 rounded-md overflow-hidden relative"
      >
        <div 
          class="h-full bg-red-500 w-1 absolute animate-scanner"
        ></div>
      </div>
  
      <!-- Recent Scans -->
      <div v-if="recentScans.length > 0" class="mt-4">
        <h4 class="font-medium text-sm text-gray-500 mb-2">Recent Scans</h4>
        <div class="max-h-40 overflow-y-auto">
          <div 
            v-for="(scan, index) in recentScans" 
            :key="index"
            class="text-sm py-1 border-b last:border-0 flex justify-between items-center"
          >
            <div>
              <span class="font-medium">{{ scan.barcode }}</span>
              <p class="text-xs text-gray-500">{{ scan.timestamp }}</p>
            </div>
            <span 
              class="text-blue-500 cursor-pointer"
              @click="barcodeValue = scan.barcode; searchProduct()"
            >
              Use
            </span>
          </div>
        </div>
      </div>
  
      <!-- Loading and Error States -->
      <div v-if="loading" class="mt-4 text-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto"></div>
        <p class="text-sm text-gray-500 mt-2">Searching...</p>
      </div>
  
      <div v-if="error" class="mt-4 bg-red-100 text-red-800 p-3 rounded-md">
        {{ error }}
      </div>
  
      <!-- Product Result -->
      <div v-if="foundProduct" class="mt-4 bg-green-50 p-4 rounded-lg border border-green-200">
        <div class="flex justify-between">
          <h4 class="font-bold">{{ foundProduct.name }}</h4>
          <span class="text-green-600 font-bold">${{ foundProduct.price.toFixed(2) }}</span>
        </div>
        <p class="text-sm text-gray-600">Product ID: {{ foundProduct.productId }}</p>
        <p class="text-sm text-gray-600">Stock: {{ foundProduct.stockQuantity }}</p>
        <button 
          @click="addToCart(foundProduct)" 
          class="mt-2 w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
        >
          Add to Cart
        </button>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
  import { productService } from '../services/api.service';
  import { barcodeService } from '../services/barcode.service';
  
  export default {
    emits: ['product-found', 'add-to-cart'],
    
    setup(props, { emit }) {
      const barcodeInput = ref(null);
      const barcodeValue = ref('');
      const scannerStatus = ref(false);
      const inputFocused = ref(false);
      const loading = ref(false);
      const error = ref('');
      const foundProduct = ref(null);
      const recentScans = ref([]);
      const isLiveScanning = ref(false);
      let scanInterval = null;
  
      // Check scanner connectivity
      const checkScannerConnection = async () => {
        try {
          const response = await barcodeService.checkHealth();
          scannerStatus.value = response.data.status === 'OK';
        } catch (err) {
          console.error('Error checking scanner connection:', err);
          scannerStatus.value = false;
        }
      };
  
      // Start live scanning
      const startLiveScan = () => {
        if (scanInterval) clearInterval(scanInterval);
        
        scanInterval = setInterval(async () => {
          try {
            // Poll for the latest barcode
            const response = await barcodeService.getLatestBarcode();
            if (response.data && response.data.data) {
              const newBarcode = response.data.data;
              
              if (barcodeValue.value !== newBarcode) {
                barcodeValue.value = newBarcode;
                searchProduct();
              }
            }
          } catch (error) {
            console.error('Error in live scanning:', error);
          }
        }, 1000); // Poll every second
      };
  
      // Stop live scanning
      const stopLiveScan = () => {
        if (scanInterval) {
          clearInterval(scanInterval);
          scanInterval = null;
        }
      };
  
      const toggleLiveScan = () => {
        isLiveScanning.value = !isLiveScanning.value;
        if (isLiveScanning.value) {
          startLiveScan();
        } else {
          stopLiveScan();
        }
      };
  
      onMounted(() => {
        // Focus the input on component mount
        nextTick(() => {
          barcodeInput.value.focus();
        });
        
        // Check scanner connection
        checkScannerConnection();
        
        // Schedule periodic connection checks
        const connectionInterval = setInterval(checkScannerConnection, 5000);
        
        // Load recent scans from localStorage if available
        const savedScans = localStorage.getItem('recentScans');
        if (savedScans) {
          try {
            recentScans.value = JSON.parse(savedScans);
          } catch (err) {
            console.error('Error parsing recent scans:', err);
          }
        }
  
        // Listen for keyboard events to simulate barcode scanner input
        document.addEventListener('keydown', handleKeyDown);
        
        // Clean up on unmount
        onUnmounted(() => {
          clearInterval(connectionInterval);
          stopLiveScan();
          document.removeEventListener('keydown', handleKeyDown);
        });
      });
  
      const handleKeyDown = (e) => {
        // Only process if not in an input field (except our barcode input)
        const activeElement = document.activeElement;
        if (activeElement !== barcodeInput.value && 
            activeElement.tagName.toLowerCase() === 'input') {
          return;
        }
  
        // Focus barcode input when any key is pressed and input is not already focused
        if (activeElement !== barcodeInput.value) {
          barcodeInput.value.focus();
        }
      };
  
      const searchProduct = async () => {
        if (!barcodeValue.value) return;
        
        loading.value = true;
        error.value = '';
        foundProduct.value = null;
        
        try {
          // First try to get the product from the Python scanner service
          let product = null;
          
          if (scannerStatus.value) {
            try {
              // Try to get from Python barcode scanner service
              const response = await barcodeService.manualScan(barcodeValue.value);
              if (response.data && !response.data.error) {
                product = response.data;
              }
            } catch (err) {
              console.log('Python scanner service failed, falling back to Java service:', err);
            }
          }
          
          // If no product from Python service, try the Java backend
          if (!product) {
            const response = await productService.getProductByBarcode(barcodeValue.value);
            product = response.data;
          }
          
          if (product) {
            foundProduct.value = product;
            emit('product-found', foundProduct.value);
            
            // Add to recent scans
            addToRecentScans(barcodeValue.value);
          } else {
            error.value = 'Product not found';
          }
        } catch (err) {
          console.error('Error fetching product by barcode:', err);
          error.value = err.response?.data || 'Error searching for product';
          
          // For demo purposes, simulate finding a product even if API fails
          if (barcodeValue.value === '123456789') {
            foundProduct.value = {
              productId: 1,
              name: 'Demo Product',
              price: 29.99,
              stockQuantity: 10,
              barcode: barcodeValue.value
            };
            emit('product-found', foundProduct.value);
            addToRecentScans(barcodeValue.value);
          }
        } finally {
          loading.value = false;
        }
      };
  
      const addToRecentScans = (barcode) => {
        // Add to recent scans with timestamp
        const scan = {
          barcode,
          timestamp: new Date().toLocaleTimeString()
        };
        
        // Remove duplicates and keep only the last 5 scans
        recentScans.value = [
          scan,
          ...recentScans.value.filter(s => s.barcode !== barcode)
        ].slice(0, 5);
        
        // Save to localStorage
        try {
          localStorage.setItem('recentScans', JSON.stringify(recentScans.value));
        } catch (err) {
          console.error('Error saving recent scans:', err);
        }
      };
  
      const clearInput = () => {
        barcodeValue.value = '';
        error.value = '';
        foundProduct.value = null;
        nextTick(() => {
          barcodeInput.value.focus();
        });
      };
  
      const addToCart = (product) => {
        emit('add-to-cart', product);
        clearInput();
      };
  
      // Watch for changes in isLiveScanning
      watch(isLiveScanning, (newValue) => {
        if (newValue) {
          startLiveScan();
        } else {
          stopLiveScan();
        }
      });
  
      return {
        barcodeInput,
        barcodeValue,
        scannerStatus,
        inputFocused,
        loading,
        error,
        foundProduct,
        recentScans,
        isLiveScanning,
        searchProduct,
        clearInput,
        addToCart,
        toggleLiveScan
      };
    }
  };
  </script>
  
  <style scoped>
  .animate-scanner {
    animation: scan 1.5s infinite linear;
  }
  
  @keyframes scan {
    0% { left: 0%; }
    100% { left: 100%; }
  }
  </style>