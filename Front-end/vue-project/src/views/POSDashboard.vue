<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white">
    <div class="container mx-auto px-4 py-6">
      <!-- Header Section -->
      <header class="mb-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-800">POS Dashboard</h1>
          <div class="flex items-center gap-3">
            <span class="text-sm text-gray-500">{{ currentDate }}</span>
            <span class="h-6 w-px bg-gray-300"></span>
            <button class="flex items-center gap-2 text-gray-600 hover:text-blue-600 transition">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span><router-link  to="/settings">Settings</router-link></span>
            </button>
          </div>
        </div>
      </header>

      <!-- Barcode Scanner Section -->
      <div class="bg-white rounded-xl shadow-lg overflow-hidden mb-8 border border-gray-100 transform transition hover:shadow-xl">
        <div class="p-4 bg-gradient-to-r from-blue-600 to-blue-800 text-white flex justify-between items-center">
          <h3 class="text-lg font-semibold flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
            </svg>
            Barcode Scanner
          </h3>
          <div 
            class="px-3 py-1 rounded-full text-xs font-medium flex items-center gap-1" 
            :class="scannerConnected ? 'bg-green-500' : 'bg-red-500'"
          >
            <span class="h-2 w-2 rounded-full bg-white animate-pulse"></span>
            {{ scannerConnected ? 'Connected' : 'Disconnected' }}
          </div>
        </div>
        
        <div class="p-6">
          <!-- Video feed or manual entry form -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Left side: Video feed (if available) or placeholder -->
            <div class="relative">
              <div v-if="scannerConnected" class="relative">
                <!-- Video feed from scanner service -->
                <img 
                  :src="videoFeedUrl" 
                  alt="Barcode Scanner Feed" 
                  class="w-full h-auto rounded-lg border border-gray-200 shadow-inner"
                  @error="handleVideoError"
                />
                
                <!-- Scanning guide overlay with animation -->
                <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
                  <div class="border-2 border-red-500 w-3/4 h-1/2 max-w-md flex items-center justify-center relative">
                    <div class="absolute inset-0 border-2 border-red-500 opacity-30 animate-pulse"></div>
                    <div class="text-sm text-white bg-black bg-opacity-60 px-3 py-1 rounded-lg">
                      Position barcode here
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="bg-gray-50 p-8 text-center rounded-lg border border-gray-200 flex flex-col items-center justify-center h-full min-h-[200px]">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <p class="text-gray-600 mb-4">
                  Camera feed not available
                </p>
                <button @click="connectScanner" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                  </svg>
                  Connect Scanner
                </button>
              </div>
              
              <!-- Latest scan results (if available) -->
              <div v-if="lastScannedBarcode" class="mt-4 p-4 bg-blue-50 rounded-lg border border-blue-200 shadow-sm">
                <div class="font-semibold text-blue-800 mb-1">Last Scan:</div>
                <div class="flex justify-between items-center">
                  <div class="text-lg font-mono bg-white px-3 py-1 rounded border border-blue-100">{{ lastScannedBarcode.data }}</div>
                  <div class="text-xs font-medium px-2 py-1 bg-blue-600 text-white rounded">{{ lastScannedBarcode.type }}</div>
                </div>
              </div>
            </div>
            
            <!-- Right side: Manual barcode entry -->
            <div>
              <div class="relative mb-6">
                <input 
                  type="text" 
                  ref="barcodeInput"
                  v-model="manualBarcodeValue" 
                  placeholder="Scan or enter barcode" 
                  class="w-full p-4 pl-12 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                  @keyup.enter="searchByBarcode"
                  @focus="inputFocused = true"
                  @blur="inputFocused = false"
                >
                <div class="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                  </svg>
                </div>
              </div>
              
              <div class="flex space-x-3">
                <button 
                  @click="searchByBarcode" 
                  class="flex-1 bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 transition shadow-md flex items-center justify-center gap-2"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                  Search
                </button>
                <button 
                  @click="clearBarcodeInput" 
                  class="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition flex items-center justify-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              
              <!-- Scanner Animation -->
              <div 
                v-if="inputFocused" 
                class="mt-6 h-10 bg-gray-100 rounded-lg overflow-hidden relative"
              >
                <div 
                  class="h-full bg-red-500 w-1 absolute animate-scanner"
                  style="animation: scanner 1.5s infinite linear; box-shadow: 0 0 8px #ef4444;"
                ></div>
              </div>
              
              <!-- Recent Scans -->
              <div v-if="recentScans.length > 0" class="mt-6">
                <h4 class="font-medium text-sm text-gray-500 mb-3 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Recent Scans
                </h4>
                <div class="max-h-48 overflow-y-auto pr-2 rounded-lg border border-gray-200 divide-y divide-gray-100 bg-white shadow-sm">
                  <div 
                    v-for="(scan, index) in recentScans" 
                    :key="index"
                    class="p-3 flex justify-between items-center hover:bg-gray-50 transition"
                  >
                    <div>
                      <span class="font-medium text-gray-800">{{ scan.barcode }}</span>
                      <p class="text-xs text-gray-500 mt-1">{{ scan.timestamp }}</p>
                    </div>
                    <span 
                      class="text-blue-600 font-medium cursor-pointer hover:text-blue-800 transition px-3 py-1 rounded-full text-sm hover:bg-blue-50"
                      @click="manualBarcodeValue = scan.barcode; searchByBarcode()"
                    >
                      Use
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Toggle Connection Button -->
          <div class="mt-6">
            <button 
              @click="toggleScanner" 
              class="w-full py-3 px-4 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition shadow-md flex items-center justify-center gap-2"
            >
              <svg v-if="scannerConnected" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
              {{ scannerConnected ? 'Disconnect Scanner' : 'Connect Scanner' }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- Main POS Section with repositioned cart -->
      <div class="flex flex-col md:flex-row gap-8">
        <!-- Left Panel: Product Categories and List -->
        <div class="w-full md:w-2/3">
          <!-- Category Tabs -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden mb-8 border border-gray-100">
            <div class="flex overflow-x-auto bg-gradient-to-r from-blue-600 to-blue-800 shadow-inner">
              <button 
                v-for="category in categories" 
                :key="category.id"
                @click="activeCategory = category.id"
                :class="[
                  'px-6 py-4 text-lg font-medium whitespace-nowrap focus:outline-none transition',
                  activeCategory === category.id 
                    ? 'bg-white text-blue-800 border-t-4 border-blue-600 -mt-1 rounded-t-lg shadow-sm' 
                    : 'text-white hover:bg-blue-700 hover:text-white'
                ]"
              >
                {{ category.name }}
              </button>
            </div>

            <!-- Product Grid -->
            <div class="p-8">
              <div class="mb-6 relative">
                <input 
                  type="text" 
                  v-model="searchQuery" 
                  placeholder="Search products..." 
                  class="w-full px-5 py-4 pl-12 text-lg border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm transition"
                >
                <div class="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
              </div>
              
              <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
                <div 
                  v-for="product in filteredProducts" 
                  :key="product.productId"
                  @click="addToCart(product)"
                  class="bg-white rounded-xl shadow-md overflow-hidden cursor-pointer transition-transform duration-300 ease-in-out transform hover:-translate-y-2 hover:shadow-xl border border-gray-100"
                >
                  <!-- Product Image -->
                  <div class="h-48 bg-gradient-to-br from-gray-50 to-gray-100 overflow-hidden relative">
                    <img 
                      v-if="product.image_url" 
                      :src="product.image_url" 
                      :alt="product.name"
                      class="w-full h-full object-cover"
                      @error="handleImageError($event, product)"
                    />
                    <div 
                      v-else 
                      class="h-full flex items-center justify-center bg-gray-100"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <!-- Stock badge -->
                    <div class="absolute top-3 right-3 px-2 py-1 bg-gray-800 bg-opacity-70 text-white text-xs font-medium rounded-lg">
                      Stock: {{ product.stockQuantity }}
                    </div>
                  </div>
                  
                  <div class="p-5">
                    <h4 class="text-xl font-semibold mb-2 text-gray-800 truncate">{{ product.name }}</h4>
                    <p class="text-blue-600 text-2xl font-bold mb-1">£{{ product.price.toFixed(2) }}</p>
                    
                    <!-- Add to cart button -->
                    <button class="mt-2 w-full py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition text-sm font-medium flex items-center justify-center gap-1">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                      </svg>
                      Add to Cart
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- Right Panel: Current Order and Actions -->
        <div class="w-full md:w-1/3">
          <!-- Current Order -->
          <div class="bg-white rounded-xl shadow-lg p-6 sticky top-8 border border-gray-100">
            <div class="flex justify-between items-center mb-6">
              <h3 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                Current Order
              </h3>
              <button 
                @click="clearCart" 
                class="text-red-500 hover:text-red-600 text-sm font-medium flex items-center gap-1 px-3 py-1 rounded-lg hover:bg-red-50 transition"
                :disabled="cart.length === 0"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Clear All
              </button>
            </div>
            <div class="mb-6">
              <customer-selector :initialCustomerId="customerId" @select-customer="setCustomer"/>
            </div>
            <loyalty-points-redeemer
      :customer="selectedCustomer"
      :orderItems="cart"
      :pointsValue="0.01"
      @apply-discount="applyLoyaltyDiscount"
    />

            <!-- Cart Items Section -->
            <div class="max-h-[calc(100vh-500px)] overflow-y-auto mb-6 pr-1">
              <div v-if="cart.length === 0" class="text-center py-16 px-8">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <p class="text-xl text-gray-500">No items in cart</p>
                <p class="text-sm text-gray-400 mt-2">Add items from the product list</p>
              </div>
              <div v-else>
                <div 
                  v-for="(item, index) in cart" 
                  :key="index"
                  class="flex items-center p-4 mb-3 rounded-lg border border-gray-100 hover:border-blue-100 hover:bg-blue-50 transition"
                >
                  <!-- Product Image -->
                  <div class="w-16 h-16 mr-4 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0 border border-gray-200">
                    <img 
                      v-if="item.image_url" 
                      :src="item.image_url" 
                      :alt="item.name"
                      class="w-full h-full object-cover"
                      @error="handleImageError($event, item)"
                    />
                    <div 
                      v-else 
                      class="w-full h-full flex items-center justify-center bg-gray-100"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                  </div>
                  
                  <div class="flex-grow">
                    <p class="text-lg font-medium text-gray-800">{{ item.name }}</p>
                    <div class="flex items-center mt-2">
                      <button 
                        @click.stop="decrementQuantity(index)" 
                        class="bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded-l-lg text-gray-600 focus:outline-none transition"
                      >
                        -
                      </button>
                      <span class="px-4 py-1 bg-white text-gray-800 border-t border-b border-gray-200">{{ item.quantity }}</span>
                      <button 
                        @click.stop="incrementQuantity(index)" 
                        class="bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded-r-lg text-gray-600 focus:outline-none transition"
                      >
                        +
                      </button>
                    </div>
                  </div>
                  
                  <div class="text-right ml-4">
                    <p class="text-lg font-semibold text-gray-800">£{{ (item.price * item.quantity).toFixed(2) }}</p>
                    <button 
                      @click.stop="removeFromCart(index)" 
                      class="text-red-500 hover:text-red-600 text-sm mt-2 font-medium flex items-center gap-1 hover:underline"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Order Summary -->
            <div class="border-t-2 border-gray-100 pt-6 mt-4">
              <div class="flex justify-between mb-3 text-lg">
                <span class="text-gray-600">Subtotal</span>
                <span class="font-medium">£{{ subtotal.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between mb-3 text-lg">
                <span class="text-gray-600">Discount</span>
                <span class="font-medium text-green-600">-£{{ discount.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between mb-3 text-lg">
                <span class="text-gray-600">Tax (20%)</span>
                <span class="font-medium">£{{ tax.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-2xl font-bold mt-4 pt-4 border-t-2 border-gray-100">
                <span class="text-gray-800">Total</span>
                <span class="text-blue-700">£{{ total.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Payment Actions -->
            <div class="mt-8">
              <h3 class="text-xl font-bold mb-4 text-gray-800 flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                Payment Options
              </h3>
              <div class="grid grid-cols-2 gap-4 mb-6">
                <button 
                  @click="processPayment('CASH')" 
                  class="bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white text-lg font-semibold py-4 rounded-xl focus:outline-none transition shadow-md flex items-center justify-center gap-2"
                  :disabled="cart.length === 0"
                  :class="{'opacity-50 cursor-not-allowed': cart.length === 0}"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  Cash
                </button>
                <button 
                  @click="processPayment('CARD')" 
                  class="bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white text-lg font-semibold py-4 rounded-xl focus:outline-none transition shadow-md flex items-center justify-center gap-2"
                  :disabled="cart.length === 0"
                  :class="{'opacity-50 cursor-not-allowed': cart.length === 0}"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                  Card
                </button>
              </div>
              <button 
                @click="holdOrder" 
                class="w-full bg-gradient-to-r from-yellow-400 to-yellow-500 hover:from-yellow-500 hover:to-yellow-600 text-white text-lg font-semibold py-4 rounded-xl mb-4 focus:outline-none transition shadow-md flex items-center justify-center gap-2"
                :disabled="cart.length === 0"
                :class="{'opacity-50 cursor-not-allowed': cart.length === 0}"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Hold Order
              </button>
              <button 
                @click="cancelOrder" 
                class="w-full bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white text-lg font-semibold py-4 rounded-xl focus:outline-none transition shadow-md flex items-center justify-center gap-2"
                :disabled="cart.length === 0"
                :class="{'opacity-50 cursor-not-allowed': cart.length === 0}"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
                Cancel Order
              </button>
            </div>
          </div>
        </div>
      </div>
<div class="bg-white rounded-xl shadow-lg overflow-hidden mb-8 border border-gray-100">
      <div class="p-4 bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <h3 class="text-lg font-semibold flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          Cash Drawer
        </h3>
      </div>
  <div class="p-6">
    <div class="grid grid-cols-2 gap-4 mb-6">
      <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
        <p class="text-sm text-gray-500 mb-1">Starting Amount</p>
        <p class="text-xl font-bold">£{{ cashDrawer.startingAmount.toFixed(2) }}</p>
      </div>
      <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
        <p class="text-sm text-gray-500 mb-1">Current Amount</p>
        <p class="text-xl font-bold">£{{ cashDrawer.currentAmount.toFixed(2) }}</p>
      </div>
    </div>
    
    <div class="flex gap-3">
      <button 
        @click="openCashDrawerModal('open')" 
        class="flex-1 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md flex items-center justify-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Open Drawer
      </button>
      <button 
        @click="openCashDrawerModal('add')" 
        class="flex-1 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-md flex items-center justify-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Add Cash
      </button>
      <button 
        @click="openCashDrawerModal('remove')" 
        class="flex-1 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition shadow-md flex items-center justify-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
        </svg>
        Remove Cash
      </button>
    </div>
  </div>
  
  <!-- Transaction History -->
  <div class="px-6 pb-6">
    <div class="mt-6 border-t border-gray-100 pt-4">
      <h4 class="font-medium text-sm text-gray-500 mb-3 flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        Recent Transactions
      </h4>
      
      <div v-if="cashDrawer.transactions.length === 0" class="text-center py-4 text-gray-500 bg-gray-50 rounded-lg">
        <p>No recent transactions</p>
      </div>
      
      <div v-else class="max-h-60 overflow-y-auto rounded-lg border border-gray-200 divide-y divide-gray-100 bg-white shadow-sm">
        <div 
          v-for="(transaction, index) in cashDrawer.transactions.slice().reverse().slice(0, 5)"
          :key="index"
          class="p-3 flex justify-between items-center hover:bg-gray-50 transition"
          :class="{'bg-green-50': transaction.type === 'ADD', 'bg-red-50': transaction.type === 'REMOVE'}"
        >
          <div>
            <span 
              class="font-medium rounded-full px-2 py-1 text-xs"
              :class="{'bg-green-100 text-green-800': transaction.type === 'ADD', 'bg-red-100 text-red-800': transaction.type === 'REMOVE'}"
            >
              {{ transaction.type === 'ADD' ? 'ADDED' : 'REMOVED' }}
            </span>
            <p class="text-sm text-gray-500 mt-1">{{ new Date(transaction.timestamp).toLocaleTimeString() }}</p>
          </div>
          <span 
            class="font-bold text-lg"
            :class="{'text-green-600': transaction.type === 'ADD', 'text-red-600': transaction.type === 'REMOVE'}"
          >
            {{ transaction.type === 'ADD' ? '+' : '-' }}£{{ transaction.amount.toFixed(2) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Cash Drawer Modal -->
<div 
  v-if="showCashDrawerModal" 
  class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
>
  <div class="bg-white rounded-xl shadow-2xl w-full max-w-md mx-4 p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold text-gray-800">
        {{ cashDrawerModalTitle }}
      </h2>
      <button 
        @click="closeCashDrawerModal" 
        class="text-gray-500 hover:text-gray-700 transition"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <div class="mb-6">
      <label for="cashAmount" class="block text-sm font-medium text-gray-700 mb-2">
        Amount
      </label>
      <div class="relative">
        <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-500">£</span>
        <input 
          id="cashAmount"
          type="number" 
          v-model.number="cashAmount"
          min="0"
          step="0.01"
          placeholder="Enter amount"
          class="w-full pl-7 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-lg"
        />
      </div>
    </div>

    <div class="flex gap-4">
      <button 
        @click="processCashDrawerAction"
        class="flex-1 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md text-lg"
      >
        Confirm
      </button>
      <button 
        @click="closeCashDrawerModal"
        class="flex-1 py-3 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition shadow-md text-lg"
      >
        Cancel
      </button>
    </div>
  </div>
</div>
      
      <!-- Card Payment Modal -->
      <div v-if="showCardPaymentModal" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 backdrop-blur-sm">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md p-6 mx-4 transform transition-all duration-300 scale-100">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
              Credit Card Payment
            </h3>
            <button @click="showCardPaymentModal = false" class="text-gray-500 hover:text-gray-700 transition">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="bg-blue-50 rounded-lg p-4 mb-6 border border-blue-100">
            <p class="text-blue-700 text-sm mb-1">Order Total</p>
            <p class="text-2xl font-bold text-blue-800">£{{ total.toFixed(2) }}</p>
          </div>

          <!-- Card Form -->
          <form @submit.prevent="submitCardPayment" class="space-y-5">
            <!-- Card Number -->
            <div>
              <label class="block text-gray-700 text-sm font-medium mb-2" for="cardNumber">
                Card Number
              </label>
              <div class="relative">
                <input 
                  id="cardNumber" 
                  v-model="cardDetails.number" 
                  type="text" 
                  placeholder="1234 5678 9012 3456" 
                  class="w-full px-4 py-3 pl-11 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                  maxlength="19"
                  @input="formatCardNumber"
                  required
                >
                <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                </div>
              </div>
            </div>

            <div class="flex gap-4">
              <!-- Expiration Date -->
              <div class="flex-1">
                <label class="block text-gray-700 text-sm font-medium mb-2" for="expDate">
                  Expiration (MM/YY)
                </label>
                <div class="relative">
                  <input 
                    id="expDate" 
                    v-model="cardDetails.expiry" 
                    type="text" 
                    placeholder="MM/YY" 
                    class="w-full px-4 py-3 pl-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                    maxlength="5"
                    @input="formatExpiry"
                    required
                  >
                  <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
              </div>

              <!-- CVV -->
              <div class="w-1/3">
                <label class="block text-gray-700 text-sm font-medium mb-2" for="cvv">
                  CVV
                </label>
                <div class="relative">
                  <input 
                    id="cvv" 
                    v-model="cardDetails.cvv" 
                    type="text" 
                    placeholder="123" 
                    class="w-full px-4 py-3 pl-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                    maxlength="4"
                    @input="numericOnly('cvv')"
                    required
                  >
                  <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <!-- Cardholder Name -->
            <div>
              <label class="block text-gray-700 text-sm font-medium mb-2" for="cardName">
                Cardholder Name
              </label>
              <div class="relative">
                <input 
                  id="cardName" 
                  v-model="cardDetails.name" 
                  type="text" 
                  placeholder="John Doe" 
                  class="w-full px-4 py-3 pl-11 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                  required
                >
                <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
              </div>
            </div>

            <!-- Credit Card Animation -->
            <div class="relative h-48 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl shadow-lg overflow-hidden p-6 text-white mb-2">
              <div class="absolute top-4 left-6">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-white opacity-80" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                </svg>
              </div>
              
              <div class="absolute bottom-6 left-6 right-6">
                <div class="text-xl font-mono tracking-widest mb-4">
                  {{ cardDetails.number || '**** **** **** ****' }}
                </div>
                <div class="flex justify-between">
                  <div>
                    <div class="text-xs opacity-80 mb-1">Card Holder</div>
                    <div class="font-medium truncate">{{ cardDetails.name || 'YOUR NAME' }}</div>
                  </div>
                  <div>
                    <div class="text-xs opacity-80 mb-1">Expires</div>
                    <div>{{ cardDetails.expiry || 'MM/YY' }}</div>
                  </div>
                </div>
              </div>
              
              <!-- Animated chip -->
              <div class="absolute top-16 left-6">
                <div class="h-10 w-12 bg-yellow-300 bg-opacity-80 rounded-md flex items-center justify-center overflow-hidden">
                  <div class="w-full h-full grid grid-cols-3 gap-px bg-yellow-600">
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                    <div class="bg-yellow-300"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Submit and Cancel Buttons -->
            <div class="flex gap-4 pt-2">
              <button 
                type="button" 
                @click="showCardPaymentModal = false" 
                class="flex-1 py-3 px-4 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition flex items-center justify-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
                Cancel
              </button>
              <button 
                type="submit" 
                class="flex-1 py-3 px-4 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition shadow-md flex items-center justify-center gap-2"
                :disabled="processingPayment"
              >
                <svg v-if="!processingPayment" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 9l3 3m0 0l-3 3m3-3H8m13 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span v-if="processingPayment">Processing...</span>
                <span v-else>Pay £{{ total.toFixed(2) }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
      
      <!-- Order Confirmation Modal -->
      <div v-if="showOrderConfirmation" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 backdrop-blur-sm">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md mx-4 transform transition-all duration-300 scale-100 overflow-hidden">
          <div class="bg-gradient-to-r from-green-500 to-green-600 text-white p-4">
            <div class="flex justify-between items-center">
              <h3 class="text-xl font-bold flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Order Complete
              </h3>
              <div class="grid grid-cols-2 gap-2 text-sm">
  <!-- Existing fields -->
  <span class="font-semibold text-gray-700">Customer:</span>
  <span class="text-right">{{ selectedCustomer ? selectedCustomer.customerName : 'Guest Customer' }}</span>
  <!-- Other fields -->
</div>
              <button @click="closeOrderConfirmation" class="text-white hover:text-gray-200 transition">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <div class="p-6">
            <div class="text-center mb-6">
              <div class="bg-green-100 rounded-full p-4 w-20 h-20 mx-auto mb-4 flex items-center justify-center shadow-inner">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <h4 class="text-2xl font-bold mb-2 text-gray-800">Thank You!</h4>
              <p class="text-gray-600">Your order has been successfully processed.</p>
            </div>
            
            <!-- Order Confirmation Modal Items Section -->
            <div class="bg-gray-50 rounded-lg p-4 mb-4 max-h-48 overflow-y-auto">
              <h5 class="font-semibold mb-2 text-gray-700">Order Items:</h5>
              <div v-for="(item, index) in completedOrder.items" :key="index" class="flex items-center py-2 border-b border-gray-200 last:border-0">
                <!-- Item Image -->
                <div class="w-10 h-10 bg-white rounded overflow-hidden mr-3 border border-gray-200">
                  <img 
                    v-if="item.image_url" 
                    :src="item.image_url" 
                    :alt="item.name"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gray-100">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
                
                <!-- Item Details -->
                <div class="flex-grow">
                  <div class="text-sm font-medium text-gray-800">{{ item.name }}</div>
                  <div class="text-xs text-gray-500">Qty: {{ item.quantity }}</div>
                </div>
                
                <!-- Item Price -->
                <div class="text-right">
                  <div class="text-sm font-semibold text-gray-800">{{ (item.price * item.quantity).toFixed(2) }}</div>
                  <div class="text-xs text-gray-500">£{{ item.price.toFixed(2) }} each</div>
                </div>
              </div>
            </div>

            <div class="bg-gray-100 rounded-lg p-4 mb-6">
              <div class="grid grid-cols-2 gap-2 text-sm">
                <span class="font-semibold text-gray-700">Order #:</span>
                <span class="text-right">{{ completedOrder.orderId }}</span>
                
                <span class="font-semibold text-gray-700">Date:</span>
                <span class="text-right">{{ new Date().toLocaleDateString() }}</span>
                
                <span class="font-semibold text-gray-700">Time:</span>
                <span class="text-right">{{ new Date().toLocaleTimeString() }}</span>
                
                <span class="font-semibold text-gray-700">Payment Method:</span>
                <span class="text-right">{{ completedOrder.paymentMethod }}</span>
              </div>
              <div class="flex justify-between mt-3 pt-3 border-t border-gray-200">
                <span class="font-semibold text-gray-800">Total Amount:</span>
                <span class="font-bold text-blue-700">£{{ completedOrder.total.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Receipt actions -->
            <div class="flex gap-3 mb-4">
              <button 
                @click="printReceipt" 
                class="flex-1 py-3 flex items-center justify-center bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                </svg>
                Print Receipt
              </button>
              <button 
                @click="emailReceipt" 
                class="flex-1 py-3 flex items-center justify-center bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-md"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                Email Receipt
              </button>
            </div>

            <!-- New order button -->
            <button 
              @click="closeOrderConfirmation" 
              class="w-full py-3 px-4 bg-gradient-to-r from-gray-800 to-gray-900 text-white rounded-lg hover:from-gray-900 hover:to-black transition shadow-md flex items-center justify-center gap-2"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              Start New Order
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { productService, orderService, paymentService } from '@/services/api.service';
import axios from 'axios';
import CustomerSelector from '../components/CustomerSelector.vue';
import LoyaltyPointsRedeemer from '../components/EnhancedLoyaltyPointsReedemer.vue';
import {stockService} from '../services/stock.service.js';
import {customerService} from '../services/api.service';
import { calculatePointsEarned } from '@/services/LoyaltyPointsManager';
// Create a dedicated API client for barcode scanner service
const barcodeApiClient = axios.create({
  baseURL: 'http://localhost:5173', // This will use the proxy if configured properly
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});


export default {
  components: {
    CustomerSelector,
    LoyaltyPointsRedeemer,
  },
  setup() {
    const POINTS_PER_TEN_POUNDS = 1; // 1 point for every £10 spent
const POINTS_VALUE = 0.01;
const isMobileView = ref(window.innerWidth < 768);
// Add this function to your setup() in POSDashboard.vue
const reduceStockAfterPurchase = async (orderItems) => {
  try {
    // For each item in the cart, reduce the stock
    for (const item of orderItems) {
      // Calculate the quantity to reduce
      const productId = item.productId;
      const quantityToReduce = item.quantity;
      
      // Get current product stock
      const product = products.value.find(p => p.productId === productId);
      
      if (product) {
        // Calculate new stock level
        const newStockQuantity = Math.max(0, product.stockQuantity - quantityToReduce);
        
        // Update the stock in the database
        await stockService.updateStock(productId, newStockQuantity);
        
        console.log(`Stock reduced for product ${product.name}: ${product.stockQuantity} → ${newStockQuantity}`);
      }
    }
    
    // Refresh product list to reflect new stock levels
    await fetchProducts();
  } catch (error) {
    console.error('Error reducing stock after purchase:', error);
  }
};
    const customerId = ref(null);
    const selectedCustomer = ref(null);
const setCustomer = (customer) => {
      selectedCustomer.value = customer;
      customerId.value = customer ? customer.customerId : null;
      
      // Save to localStorage
      if (customer) {
        localStorage.setItem('selectedCustomerId', customer.customerId);
      } else {
        localStorage.removeItem('selectedCustomerId');
      }
    };
    
    const router = useRouter();
    const cart = ref([]);
    const products = ref([]);
    const categories = ref([
      { id: 'all', name: 'All Products' },
      { id: 1, name: 'Electronics' },
      { id: 2, name: 'Clothing' },
      { id: 3, name: 'Home & Kitchen' },
      { id: 4, name: 'Books' }
    ]);
    const activeCategory = ref('all');
    const searchQuery = ref('');
    const taxRate = 0.20; 
    const discountAmount = ref(0);

const applyLoyaltyDiscount = (discountData) => {
  // Update discount amount
  const maxDiscount = subtotal.value;
  // Update discount amount, capping at the maximum allowed value
  discountAmount.value = Math.min(discountData.discountAmount, maxDiscount);
  
  // If applying to specific items, you might want to adjust cart
  if (discountData.applyTo === 'item') {
    discountData.selectedItems.forEach(item => {
      // Logic to apply discount to specific cart items
    });
  }
};

const updateCustomerPoints = (updatedCustomer) => {
  // Update the selected customer's points
  selectedCustomer.value = updatedCustomer;
};

    // Barcode scanner state
    const scannerConnected = ref(false);
    const lastScannedBarcode = ref(null);
    const pollingInterval = ref(null);
    const videoFeedUrl = ref('');
    const manualBarcodeValue = ref('');
    const inputFocused = ref(false);
    const recentScans = ref([]);
    const barcodeInput = ref(null);
    // Card payment state
    const showCardPaymentModal = ref(false);
    const processingPayment = ref(false);
    const cardDetails = ref({
      number: '',
      expiry: '',
      cvv: '',
      name: ''
    });
    
    // Order confirmation state
    const showOrderConfirmation = ref(false);
    const completedOrder = ref({
      orderId: '',
      paymentMethod: '',
      total: 0,
      items: []
    });
    const currentDate = computed(() => {
  const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  return new Date().toLocaleDateString('en-GB', options);
});
    
    // Format card number with spaces every 4 digits
    const formatCardNumber = () => {
      // Remove all non-digits
      let value = cardDetails.value.number.replace(/\D/g, '');
      
      // Add a space after every 4 digits
      let formatted = '';
      for (let i = 0; i < value.length; i++) {
        if (i > 0 && i % 4 === 0) {
          formatted += ' ';
        }
        formatted += value[i];
      }
      
      // Update the value
      cardDetails.value.number = formatted;
    };

    // Format expiry date as MM/YY
    const formatExpiry = () => {
      // Remove all non-digits
      let value = cardDetails.value.expiry.replace(/\D/g, '');
      
      // Format as MM/YY
      if (value.length > 2) {
        cardDetails.value.expiry = value.substring(0, 2) + '/' + value.substring(2);
      } else {
        cardDetails.value.expiry = value;
      }
    };

    // Allow only numeric input for certain fields
    const numericOnly = (field) => {
      cardDetails.value[field] = cardDetails.value[field].replace(/\D/g, '');
    };
    
    // Update video feed URL
    const setVideoFeedUrl = () => {
      videoFeedUrl.value = '/video_feed';
    };
    
    // Handle video loading error
    const handleVideoError = () => {
      scannerConnected.value = false;
      stopPolling();
    };
    
    // Connect to the scanner
    const connectScanner = async () => {
      try {
        const response = await barcodeApiClient.get('/health');
        if (response.data.status === 'OK') {
          scannerConnected.value = true;
          setVideoFeedUrl();
          startPolling();
        } else {
          scannerConnected.value = false;
        }
      } catch (err) {
        console.error('Error connecting to scanner:', err);
        scannerConnected.value = false;
      }
    };
    
    // Disconnect from the scanner
    const disconnectScanner = () => {
      scannerConnected.value = false;
      stopPolling();
    };
    
    // Toggle scanner connection
    const toggleScanner = () => {
      if (scannerConnected.value) {
        disconnectScanner();
      } else {
        connectScanner();
      }
    };
    
    // Start polling for the latest barcode
    const startPolling = () => {
      if (pollingInterval.value) clearInterval(pollingInterval.value);
      
      pollingInterval.value = setInterval(async () => {
        try {
          const response = await barcodeApiClient.get('/latest_barcode');
          if (response.data && !response.data.error) {
            const newBarcode = response.data;
            
            // Only process if we got a new barcode
            if (!lastScannedBarcode.value || 
                lastScannedBarcode.value.data !== newBarcode.data) {
              lastScannedBarcode.value = newBarcode;
              
              // Find product by barcode
              const product = products.value.find(p => p.barcode === newBarcode.data);
              if (product) {
                addToCart(product);
                
                // Add to recent scans
                addToRecentScans(newBarcode.data);
              } else {
                console.log('Product not found for barcode:', newBarcode.data);
              }
            }
          }
        } catch (err) {
          console.error('Error polling for barcode:', err);
        }
      }, 1000); // Poll every second
    };
    
    // Stop polling
    const stopPolling = () => {
      if (pollingInterval.value) {
        clearInterval(pollingInterval.value);
        pollingInterval.value = null;
      }
    };
    
    // Search by manually entered barcode
    const searchByBarcode = async () => {
      if (!manualBarcodeValue.value) return;
      
      try {
        // Try to find product locally first
        const product = products.value.find(p => p.barcode === manualBarcodeValue.value);
        
        if (product) {
          addToCart(product);
          addToRecentScans(manualBarcodeValue.value);
        } else {
          // If not found locally, try to get from API
          try {
            const response = await barcodeApiClient.post('/scan', { barcode: manualBarcodeValue.value });
            if (response.data && !response.data.error) {
              const product = response.data;
              addToCart(product);
              addToRecentScans(manualBarcodeValue.value);
            } else {
              console.log('Product not found for barcode:', manualBarcodeValue.value);
            }
          } catch (err) {
            console.error('Error searching by barcode:', err);
          }
        }
      } finally {
        clearBarcodeInput();
      }
    };
    
    // Clear barcode input
    const clearBarcodeInput = () => {
      manualBarcodeValue.value = '';
      nextTick(() => {
        barcodeInput.value?.focus();
      });
    };
    
    // Add to recent scans
    const addToRecentScans = (barcode) => {
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

    const fetchProducts = async () => {
      try {
        const response = await productService.getAllProducts();
        products.value = response.data || [];
        console.log('Products loaded:', products.value);
      } catch (error) {
        console.error('Error fetching products:', error);
        // For demo purposes, let's add some sample products if the API fails
        products.value = [
          { productId: 1, name: 'Laptop', price: 999.99, categoryId: 1, stockQuantity: 10, barcode: '123456789' },
          { productId: 2, name: 'Smartphone', price: 499.99, categoryId: 1, stockQuantity: 15, barcode: '234567890' },
          { productId: 3, name: 'T-Shirt', price: 19.99, categoryId: 2, stockQuantity: 50, barcode: '345678901' },
          { productId: 4, name: 'Coffee Maker', price: 89.99, categoryId: 3, stockQuantity: 8, barcode: '456789012' },
          { productId: 5, name: 'Novel', price: 12.99, categoryId: 4, stockQuantity: 30, barcode: '567890123' }
        ];
      }
    };

    const filteredProducts = computed(() => {
      let result = products.value;
      
      // Filter by category
      if (activeCategory.value !== 'all') {
        result = result.filter(product => product.categoryId === activeCategory.value);
      }
      
      // Filter by search query
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(product => 
          product.name.toLowerCase().includes(query) || 
          product.barcode?.includes(query)
        );
      }
      
      return result;
    });

    const addToCart = (product) => {
  const existingItem = cart.value.find(item => item.productId === product.productId);
  
  if (existingItem) {
    // Check if we can increment quantity based on available stock
    if (existingItem.quantity < product.stockQuantity) {
      existingItem.quantity += 1;
    } else {
      // Alert the user that there's not enough stock
      alert(`Sorry, only ${product.stockQuantity} units of ${product.name} are available.`);
      return; // Stop execution if stock limit reached
    }
  } else {
    // Check if the product has any stock before adding
    if (product.stockQuantity > 0) {
      cart.value.push({
        ...product,
        quantity: 1
      });
    } else {
      alert(`Sorry, ${product.name} is out of stock.`);
      return; // Stop execution if no stock
    }
  }
  
  // Save cart to localStorage
  localStorage.setItem('cart', JSON.stringify(cart.value));
};

    const removeFromCart = (index) => {
      cart.value.splice(index, 1);
      localStorage.setItem('cart', JSON.stringify(cart.value));
    };

    const incrementQuantity = (index) => {
  const item = cart.value[index];
  const product = products.value.find(p => p.productId === item.productId);
  
  if (product && item.quantity < product.stockQuantity) {
    item.quantity += 1;
  } else {
    alert(`Sorry, only ${product ? product.stockQuantity : 0} units of ${item.name} are available.`);
    return;
  }
  
  localStorage.setItem('cart', JSON.stringify(cart.value));
};

    const decrementQuantity = (index) => {
      if (cart.value[index].quantity > 1) {
        cart.value[index].quantity -= 1;
      } else {
        removeFromCart(index);
      }
      localStorage.setItem('cart', JSON.stringify(cart.value));
    };

    const clearCart = () => {
      cart.value = [];
      discountAmount.value = 0; // Reset discount
      localStorage.removeItem('cart');
    };

    const subtotal = computed(() => {
      return cart.value.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    });

    const tax = computed(() => {
  // Calculate tax on the amount AFTER discount is applied
  const taxableAmount = Math.max(0, subtotal.value - discount.value);
  return taxableAmount * taxRate;
});


const discount = computed(() => {
  return discountAmount.value; // Use the loyalty discount
});
const total = computed(() => {
  // Apply discount to subtotal, ensure it doesn't go below zero
  const discountedSubtotal = Math.max(0, subtotal.value - discount.value);
  // Then add tax
  return discountedSubtotal + tax.value;
});

    // Process payment - Updated version that shows order confirmation
  const cashDrawer = ref({
  startingAmount: 100, // Starting float
  currentAmount: 100,
  transactions: []
});
// Add these variables to your setup() function
const showCashDrawerModal = ref(false);
const cashDrawerModalTitle = ref('');
const cashAmount = ref(0);
const cashDrawerAction = ref('');

// Add these methods to your setup() function
const openCashDrawerModal = (action) => {
  cashDrawerAction.value = action;
  cashAmount.value = 0;
  
  switch(action) {
    case 'open':
      cashDrawerModalTitle.value = 'Open Cash Drawer';
      break;
    case 'add':
      cashDrawerModalTitle.value = 'Add Cash to Drawer';
      break;
    case 'remove':
      cashDrawerModalTitle.value = 'Remove Cash from Drawer';
      break;
  }
  
  showCashDrawerModal.value = true;
};

const closeCashDrawerModal = () => {
  showCashDrawerModal.value = false;
};

const processCashDrawerAction = () => {
  const amount = parseFloat(cashAmount.value);
  
  if (isNaN(amount) || amount <= 0) {
    alert('Please enter a valid amount');
    return;
  }
  
  switch(cashDrawerAction.value) {
    case 'open':
      // Just physically open the drawer in a real implementation
      alert('Cash drawer opened');
      break;
    case 'add':
      addCashToDrawer(amount);
      break;
    case 'remove':
      removeCashFromDrawer(amount);
      break;
  }
  
  // Save to localStorage for persistence
  localStorage.setItem('cashDrawer', JSON.stringify(cashDrawer.value));
  
  // Close modal
  closeCashDrawerModal();
};

const openCashDrawer = () => {
  // In a real system, this would trigger the physical cash drawer
  console.log('Cash drawer opened');
  alert('Cash drawer opened');
};

const addCashToDrawer = (amount) => {
  if (!amount) return;
  const parsedAmount = parseFloat(amount);
  if (isNaN(parsedAmount) || parsedAmount <= 0) {
    alert('Please enter a valid amount');
    return;
  }
  
  cashDrawer.value.currentAmount += parsedAmount;
  cashDrawer.value.transactions.push({
    type: 'ADD',
    amount: parsedAmount,
    timestamp: new Date()
  });
  
  alert(`${parsedAmount.toFixed(2)} added to cash drawer`);
};

const removeCashFromDrawer = (amount) => {
  if (!amount) return;
  const parsedAmount = parseFloat(amount);
  if (isNaN(parsedAmount) || parsedAmount <= 0) {
    alert('Please enter a valid amount');
    return;
  }
  
  if (parsedAmount > cashDrawer.value.currentAmount) {
    alert(`Not enough cash in drawer. Current amount: £${cashDrawer.value.currentAmount.toFixed(2)}`);
    return false;
  }
  
  cashDrawer.value.currentAmount -= parsedAmount;
  cashDrawer.value.transactions.push({
    type: 'REMOVE',
    amount: parsedAmount,
    timestamp: new Date()
  });
  
  alert(`£${parsedAmount.toFixed(2)} removed from cash drawer`);
  return true;
};

// 2. Update the processPayment method to handle cash drawer operations
// Replace the current processPayment method with this updated version:
const validateStockForCart = () => {
  const invalidItems = [];
  
  for (const cartItem of cart.value) {
    // Find the current product to get latest stock
    const product = products.value.find(p => p.productId === cartItem.productId);
    
    if (!product || cartItem.quantity > product.stockQuantity) {
      invalidItems.push({
        name: cartItem.name,
        requestedQuantity: cartItem.quantity,
        availableQuantity: product ? product.stockQuantity : 0
      });
    }
  }
  
  return invalidItems;
};
const processPayment = async (paymentMethod) => {
  if (cart.value.length === 0) return;
  const stockValidation = validateStockForCart();
  if (stockValidation.length > 0) {
    // Format error message showing each item with insufficient stock
    let errorMessage = "Not enough stock available for the following items:\n\n";
    stockValidation.forEach(item => {
      errorMessage += `- ${item.name}: Requested: ${item.requestedQuantity}, Available: ${item.availableQuantity}\n`;
    });
    errorMessage += "\nPlease adjust your cart before proceeding.";
    
    alert(errorMessage);
    return; // Stop payment processing
  }
  if (!customerId.value && !confirm('No customer is selected. Continue as guest?')) {
    return;
  }
  // Handle card payment separately
  if (paymentMethod === 'CARD') {
    showCardPaymentModal.value = true;
    return;
  }

  // Handle cash payment
  if (paymentMethod === 'CASH') {
    try {
      processingPayment.value = true;
      
      // Prepare order data
      const orderData = {
        customerId: customerId.value, // Default customer for walk-ins
        totalAmount: Number(subtotal.value),
        taxAmount: Number(tax.value),
        discountAmount: Number(discount.value || 0),
        finalAmount: Number(total.value),
        orderType: "In-Store",
        paymentStatus: "PENDING",
        deliveryType: "",
        orderItems: cart.value.map(item => ({
          productId: Number(item.productId),
          quantity: Number(item.quantity),
          price: Number(item.price),
          subtotal: Number(item.price * item.quantity)
        }))
      };

      // Create order
      const orderResponse = await orderService.createOrder(orderData);
      const order = orderResponse.data;

      // Process cash payment
      const paymentData = {
        transactionId: generateTransactionId(),
        orderId: order.orderId,
        amount: Number(total.value),
        paymentMethod: "CASH",
        paymentType: "CASH"
      };

      await paymentService.processPayment(paymentData);

      // Show cash payment dialog
      const cashReceived = prompt(`Order Total: £${total.value.toFixed(2)}\n\nEnter amount received:`);
      
      if (cashReceived === null) {
        // User cancelled
        return;
      }
      
      const cashAmount = parseFloat(cashReceived);
      
      if (isNaN(cashAmount)) {
        alert('Please enter a valid number');
        return;
      }
      
      if (cashAmount < total.value) {
        alert(`Amount received (£${cashAmount.toFixed(2)}) is less than total (£${total.value.toFixed(2)}). Please collect the correct amount.`);
        return;
      }
      
      // Calculate change
      const change = cashAmount - total.value;
      
      // Open cash drawer
      openCashDrawer();
      
      // Add the received amount to the cash drawer
      addCashToDrawer(total.value);
      
      // If there's change, remove it from the drawer
      if (change > 0) {
        removeCashFromDrawer(change);
      }
      await reduceStockAfterPurchase(cart.value);
      // Complete the order
      completedOrder.value = {
        customerName: selectedCustomer.value ? selectedCustomer.value.customerName : 'Guest Customer',
        customerId: customerId.value || 1,
        orderId: order.orderId,
        paymentMethod: 'Cash',
        total: total.value,
        cashReceived: cashAmount,
        changeGiven: change,
        discount:discountAmount.value,
        items: [...cart.value]
      };
 
      // Show confirmation
      showOrderConfirmation.value = true;
      const pointsEarned = calculatePointsEarned(total.value);

      if (selectedCustomer.value && pointsEarned > 0) {
        try {
          await customerService.addLoyaltyPoints(selectedCustomer.value.customerId, pointsEarned);
          
          // Optional: Show a notification about points earned
          alert(`You've earned ${pointsEarned} loyalty points!`);
        } catch (error) {
          console.error('Error adding loyalty points:', error);
        }
      }

      clearCart();
    } catch (error) {
      console.error('Payment error:', error);
      alert('There was an error processing your payment. Please try again.');
    } finally {
      processingPayment.value = false;
    }
  }
};

// Replace the submitCardPayment function with this corrected version
const submitCardPayment = async () => {
  const stockValidation = validateStockForCart();
  if (stockValidation.length > 0) {
    let errorMessage = "Not enough stock available for the following items:\n\n";
    stockValidation.forEach(item => {
      errorMessage += `- ${item.name}: Requested: ${item.requestedQuantity}, Available: ${item.availableQuantity}\n`;
    });
    errorMessage += "\nPlease adjust your cart before proceeding.";
    
    alert(errorMessage);
    processingPayment.value = false;
    return; // Stop payment processing
  }
  processingPayment.value = true;
  
  try {
    // Validate card details
    const cardNumber = cardDetails.value.number.replace(/\s/g, '');
    if (!/^\d{13,19}$/.test(cardNumber)) {
      alert('Please enter a valid 13-19 digit card number');
      return;
    }
    
    if (!/^\d{2}\/\d{2}$/.test(cardDetails.value.expiry)) {
      alert('Please enter a valid expiration date in MM/YY format');
      return;
    }
    
    if (!/^\d{3,4}$/.test(cardDetails.value.cvv)) {
      alert('Please enter a valid 3 or 4 digit CVV code');
      return;
    }
    
    if (cardDetails.value.name.trim().length < 3) {
      alert('Please enter the full cardholder name');
      return;
    }

    // Create order data with proper types - FIX: Use customerId.value instead of hardcoding 1
    const orderData = {
      customerId: customerId.value || 1, // Use selected customer or default to 1 for guests
      totalAmount: Number(subtotal.value),
      taxAmount: Number(tax.value),
      discountAmount: Number(discount.value || 0),
      finalAmount: Number(total.value),
      orderType: "In-Store",
      paymentStatus: "PENDING",
      deliveryType: "",
      orderItems: cart.value.map(item => ({
        productId: Number(item.productId),
        quantity: Number(item.quantity),
        price: Number(item.price),
        subtotal: Number(item.price * item.quantity)
      }))
    };

    console.log('Submitting order:', JSON.stringify(orderData, null, 2));

    // Create order
    const orderResponse = await orderService.createOrder(orderData);
    const order = orderResponse.data;

    // Process payment
    const paymentData = {
      transactionId: generateTransactionId(),
      orderId: order.orderId,
      amount: Number(total.value),
      paymentMethod: "CARD",
      cardDetails: {
        cardNumber: cardNumber,
        expiryDate: cardDetails.value.expiry,
        cvv: cardDetails.value.cvv,
        cardholderName: cardDetails.value.name.trim()
      }
    };

    console.log('Processing payment:', JSON.stringify(paymentData, null, 2));
    await paymentService.processPayment(paymentData);
    await reduceStockAfterPurchase(cart.value);
    
    // Update UI state - FIX: Include customer information
    completedOrder.value = {
      orderId: order.orderId,
      paymentMethod: 'Credit Card',
      total: total.value,
      discount: discountAmount.value,
      items: [...cart.value],
      // Include customer information
      customerName: selectedCustomer.value ? selectedCustomer.value.customerName : 'Guest Customer',
      customerId: customerId.value || 1
    };

    // Reset everything
    showCardPaymentModal.value = false;
    cardDetails.value = { number: '', expiry: '', cvv: '', name: '' };
    showOrderConfirmation.value = true;
    const pointsEarned = calculatePointsEarned(total.value);

    if (selectedCustomer.value && pointsEarned > 0) {
      try {
        await customerService.addLoyaltyPoints(selectedCustomer.value.customerId, pointsEarned);
        
        // Optional: Show a notification about points earned
        alert(`You've earned ${pointsEarned} loyalty points!`);
      } catch (error) {
        console.error('Error adding loyalty points:', error);
      }
    }
    clearCart();
    
  } catch (error) {
    console.error('Payment error:', {
      message: error.message,
      response: error.response?.data,
      request: error.config?.data
    });
    
    let errorMessage = 'There was an error processing your payment.';
    if (error.response?.data?.error) {
      errorMessage += `\n\nError: ${error.response.data.error}`;
    } else if (error.message) {
      errorMessage += `\n\n${error.message}`;
    }
    
    alert(errorMessage);
  } finally {
    processingPayment.value = false;
  }
};
    
    // Submit card payment with confirmation
    // Submit card payment with confirmation


    // Close the order confirmation modal and reset for a new order
    const closeOrderConfirmation = () => {
      showOrderConfirmation.value = false;
      discountAmount.value = 0;
      // Reset any other order-related state if needed
    };

    // Print receipt functionality
// Print receipt functionality with PDF generation
const printReceipt = () => {
  const receiptWindow = window.open('', '_blank');
  
  const now = new Date();
  const dateStr = now.toLocaleDateString('en-GB');
  const timeStr = now.toLocaleTimeString('en-GB');
  
  const itemsHtml = completedOrder.value.items.map(item => `
    <tr>
      <td style="padding: 4px 8px; text-align: left;">${item.name}</td>
      <td style="padding: 4px 8px; text-align: center;">${item.quantity}</td>
      <td style="padding: 4px 8px; text-align: right;">£${item.price.toFixed(2)}</td>
      <td style="padding: 4px 8px; text-align: right;">£${(item.price * item.quantity).toFixed(2)}</td>
    </tr>
  `).join('');
  
  const itemsSubtotal = completedOrder.value.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const taxAmount = itemsSubtotal * taxRate;
  
  // Ensure discount is a number, defaulting to 0 if undefined or not a number
  const discountAmount = parseFloat(completedOrder.value.discount) || 0;
  
  // Write the receipt HTML
  receiptWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>Receipt #${completedOrder.value.orderId}</title>
      <style>
        body {
          font-family: 'Arial', sans-serif;
          margin: 0;
          padding: 20px;
          font-size: 12px;
          color: #333;
        }
        .receipt {
          max-width: 300px;
          margin: 0 auto;
          border: 1px solid #ddd;
          padding: 20px;
        }
        .header {
          text-align: center;
          margin-bottom: 20px;
          border-bottom: 1px dashed #ddd;
          padding-bottom: 10px;
        }
        .store-name {
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 5px;
        }
        .store-info {
          font-size: 10px;
          margin-bottom: 5px;
        }
        .receipt-details {
          margin-bottom: 15px;
          font-size: 10px;
        }
        .order-id {
          font-weight: bold;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          margin-bottom: 15px;
        }
        th {
          border-bottom: 1px solid #ddd;
          padding: 5px;
          text-align: left;
          font-size: 11px;
        }
        .totals {
          border-top: 1px dashed #ddd;
          padding-top: 10px;
          margin-top: 10px;
        }
        .total-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 5px;
        }
        .grand-total {
          font-weight: bold;
          font-size: 14px;
          margin-top: 5px;
          border-top: 1px solid #333;
          padding-top: 5px;
        }
        .footer {
          margin-top: 20px;
          text-align: center;
          font-size: 10px;
          border-top: 1px dashed #ddd;
          padding-top: 10px;
        }
        @media print {
          body {
            padding: 0;
          }
          .receipt {
            border: none;
          }
          .print-btn {
            display: none;
          }
        }
      </style>
    </head>
    <body>
      <div class="receipt">
        <div class="header">
          <div class="store-name">Retail POS System</div>
          <div class="store-info">123 Main Street</div>
          <div class="store-info">Manchester, M1 1AB</div>
          <div class="store-info">Tel: 0161 123 4567</div>
          <div class="store-info">VAT Reg: GB123456789</div>
        </div>
        
        <div class="receipt-details">
          <div><span class="order-id">Customer:</span> ${completedOrder.value.customerName || 'Guest Customer'}</div>
          <div><span class="order-id">Order #:</span> ${completedOrder.value.orderId}</div>
          <div><span class="order-id">Date:</span> ${dateStr}</div>
          <div><span class="order-id">Time:</span> ${timeStr}</div>
          <div><span class="order-id">Payment Method:</span> ${completedOrder.value.paymentMethod}</div>
        </div>
        
        <table>
          <thead>
            <tr>
              <th style="text-align: left;">Item</th>
              <th style="text-align: center;">Qty</th>
              <th style="text-align: right;">Price</th>
              <th style="text-align: right;">Total</th>
            </tr>
          </thead>
          <tbody>
            ${itemsHtml}
          </tbody>
        </table>
        
        <div class="totals">
          <div class="total-row">
            <span>Subtotal:</span>
            <span>£${itemsSubtotal.toFixed(2)}</span>
          </div>
          <div class="total-row">
            <span>Discount:</span>
            <span>-£${discountAmount.toFixed(2)}</span>
          </div>
          <div class="total-row">
            <span>VAT (${(taxRate * 100).toFixed(0)}%):</span>
            <span>£${taxAmount.toFixed(2)}</span>
          </div>
          <div class="total-row grand-total">
            <span>TOTAL:</span>
            <span>£${completedOrder.value.total.toFixed(2)}</span>
          </div>
        </div>
        
        <div class="footer">
          <div>Thank you for your purchase!</div>
          <div>Please keep this receipt for your records</div>
          <div>www.retailpos.com</div>
        </div>
      </div>
      
      <div style="text-align: center; margin-top: 20px;" class="print-btn">
        <button onclick="window.print();return false;" style="padding: 8px 16px; background: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;">
          Print Receipt / Save as PDF
        </button>
      </div>
    </body>
    </html>
  `);
  
  receiptWindow.document.close();
};

// 3. Similarly update the emailReceipt function:
const emailReceipt = async () => {
  const email = prompt('Please enter email address for receipt:');
  if (!email) return;
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    alert('Please enter a valid email address');
    return;
  }
  
  try {
    alert('Preparing to send receipt to ' + email + '...');
    
    const subtotalAmount = completedOrder.value.items.reduce(
      (sum, item) => sum + (item.price * item.quantity), 0
    );
    
    const taxAmount = subtotalAmount * taxRate;
    
    // Ensure discount is a number, defaulting to 0 if undefined or not a number
    const discountAmount = parseFloat(completedOrder.value.discount) || 0;
    
    const emailData = {
      to: email,
      subject: `Receipt for Order #${completedOrder.value.orderId}`,
      storeInfo: {
        name: 'Retail POS System',
        address: '123 Main Street',
        city: 'Manchester',
        postcode: 'M1 1AB',
        phone: '0161 123 4567',
        vatNumber: 'GB123456789'
      },
      orderInfo: {
        orderId: completedOrder.value.orderId,
        date: new Date().toLocaleDateString('en-GB'),
        time: new Date().toLocaleTimeString('en-GB'),
        paymentMethod: completedOrder.value.paymentMethod
      },
      items: completedOrder.value.items.map(item => ({
        name: item.name,
        quantity: item.quantity,
        price: item.price,
        total: item.price * item.quantity
      })),
      totals: {
        subtotal: subtotalAmount,
        discount: discountAmount,
        tax: taxAmount,
        total: completedOrder.value.total
      }
    };
    
    console.log('Email receipt data:', emailData);
    
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    alert(`Receipt has been sent to ${email}. Thank you for your purchase!`);
    
  } catch (error) {
    console.error('Error sending email receipt:', error);
    alert('There was an error sending the receipt. Please try again later.');
  }
};


    const generateTransactionId = () => {
      return 'TXN' + Date.now().toString();
    };

    const holdOrder = () => {
      if (cart.value.length === 0) return;
      alert('Order has been put on hold.');
    };

    const cancelOrder = () => {
  if (cart.value.length === 0) return;
  if (confirm('Are you sure you want to cancel this order?')) {
    clearCart();
    discountAmount.value = 0;
    
    // Also clear customer selection
    customerId.value = null;
    selectedCustomer.value = null;
    localStorage.removeItem('selectedCustomerId');
  }
};

    onMounted(() => {
      fetchProducts();
      window.addEventListener('resize', () => {
    isMobileView.value = window.innerWidth < 768;
  });
      
      // Load cart from localStorage if available
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        try {
          cart.value = JSON.parse(savedCart);
        } catch (e) {
          console.error('Error parsing saved cart:', e);
          localStorage.removeItem('cart');
        }
      }
      const savedCustomerId = localStorage.getItem('selectedCustomerId');
if (savedCustomerId) {
  try {
    customerId.value = parseInt(savedCustomerId);
  } catch (err) {
    console.error('Error parsing saved customerId:', err);
  }
}
      // Load recent scans from localStorage if available
      const savedScans = localStorage.getItem('recentScans');
      if (savedScans) {
        try {
          recentScans.value = JSON.parse(savedScans);
        } catch (e) {
          console.error('Error parsing recent scans:', e);
          localStorage.removeItem('recentScans');
        }
      }
      
      // Try to connect to the scanner
      connectScanner();
      
      // Focus barcode input
      nextTick(() => {
        barcodeInput.value?.focus();
      });
    });
    
    onUnmounted(() => {
      // Clean up
      stopPolling();
    });

    return {
      customerId,
  selectedCustomer,
  setCustomer,
      // POS state
      products,
      categories,
      activeCategory,
      searchQuery,
      cart,
      filteredProducts,
      
      // Barcode scanner state
      scannerConnected,
      videoFeedUrl,
      lastScannedBarcode,
      manualBarcodeValue,
      inputFocused,
      recentScans,
      barcodeInput,
      
      // Card payment state
      showCardPaymentModal,
      processingPayment,
      cardDetails,
      currentDate,
      
      // Order confirmation state
      showOrderConfirmation,
      completedOrder,
      reduceStockAfterPurchase,
      // Barcode methods
      handleVideoError,
      connectScanner,
      toggleScanner,
      searchByBarcode,
      clearBarcodeInput,
      
      // Card payment methods
      formatCardNumber,
      formatExpiry,
      numericOnly,
      submitCardPayment,
      
      // Order confirmation methods
      closeOrderConfirmation,
      printReceipt,
      emailReceipt,
      
      // Cart methods
      addToCart,
      removeFromCart,
      incrementQuantity,
      decrementQuantity,
      clearCart,
      
      // Payment calculations and methods
      subtotal,
      tax,
      discount,
      total,
      processPayment,
      holdOrder,
      cancelOrder,
      cashDrawer,
  openCashDrawer,
  addCashToDrawer,
  removeCashFromDrawer,
  showCashDrawerModal,
  cashDrawerModalTitle,
  cashAmount,
  openCashDrawerModal,
  closeCashDrawerModal,
  processCashDrawerAction,
  applyLoyaltyDiscount
    };
  }
};
</script>