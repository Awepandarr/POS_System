<!-- src/components/BarcodeVideoFeed.vue -->
<template>
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <div class="p-4 bg-gray-800 text-white flex justify-between items-center">
        <h3 class="text-lg font-semibold">Live Scanner Feed</h3>
        <div 
          class="px-2 py-1 rounded-full text-xs font-medium" 
          :class="isConnected ? 'bg-green-500' : 'bg-red-500'"
        >
          {{ isConnected ? 'Connected' : 'Disconnected' }}
        </div>
      </div>
      
      <div class="p-4">
        <div v-if="isConnected" class="relative">
          <!-- Video feed from Python scanner -->
          <img 
            :src="videoUrl" 
            alt="Barcode Scanner Feed" 
            class="w-full h-auto rounded border border-gray-300"
            @error="handleVideoError"
          />
          
          <!-- Scanning guide overlay -->
          <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
            <div class="border-2 border-red-500 w-3/4 h-1/2 max-w-md flex items-center justify-center">
              <div class="text-xs text-white bg-black bg-opacity-50 px-2 py-1 rounded">
                Position barcode here
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="bg-gray-100 p-8 text-center rounded">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="text-gray-600">
            Video feed not available.
            <button @click="reconnect" class="text-blue-500 underline">Try reconnecting</button>
          </p>
        </div>
        
        <!-- Latest scan results -->
        <div v-if="latestBarcode" class="mt-4 p-3 bg-blue-50 rounded border border-blue-200">
          <div class="font-semibold">Last Scan:</div>
          <div class="flex justify-between items-center">
            <div class="text-lg font-mono">{{ latestBarcode.data }}</div>
            <div class="text-xs text-gray-500">{{ latestBarcode.type }}</div>
          </div>
        </div>
      </div>
      
      <div class="p-4 border-t">
        <button 
          @click="toggleScanner" 
          class="w-full py-2 px-4 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
        >
          {{ isConnected ? 'Disconnect Scanner' : 'Connect Scanner' }}
        </button>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted, onUnmounted } from 'vue';
  import { barcodeService } from '../services/barcode.service';
  
  export default {
    props: {
      autoConnect: {
        type: Boolean,
        default: true
      }
    },
    
    emits: ['barcode-detected', 'connection-status-change'],
    
    setup(props, { emit }) {
      const isConnected = ref(false);
      const videoUrl = ref('');
      const latestBarcode = ref(null);
      const pollingInterval = ref(null);
      
      // Set the video feed URL
      const setVideoUrl = () => {
        // This URL should point to the video feed endpoint in your Python app
        videoUrl.value = 'http://localhost:5173/video_feed';
      };
      
      // Handle video loading error
      const handleVideoError = () => {
        isConnected.value = false;
        emit('connection-status-change', false);
      };
      
      // Start polling for the latest barcode
      const startPolling = () => {
        if (pollingInterval.value) clearInterval(pollingInterval.value);
        
        pollingInterval.value = setInterval(async () => {
          try {
            const response = await barcodeService.getLatestBarcode();
            if (response.data && !response.data.error) {
              const newBarcode = response.data;
              
              // Only emit if we got a new barcode
              if (!latestBarcode.value || 
                  latestBarcode.value.data !== newBarcode.data) {
                latestBarcode.value = newBarcode;
                emit('barcode-detected', newBarcode);
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
      
      // Connect to the scanner
      const connect = async () => {
        try {
          const response = await barcodeService.checkHealth();
          if (response.data.status === 'OK') {
            isConnected.value = true;
            emit('connection-status-change', true);
            setVideoUrl();
            startPolling();
          } else {
            isConnected.value = false;
            emit('connection-status-change', false);
          }
        } catch (err) {
          console.error('Error connecting to scanner:', err);
          isConnected.value = false;
          emit('connection-status-change', false);
        }
      };
      
      // Reconnect to the scanner
      const reconnect = () => {
        connect();
      };
      
      // Toggle scanner connection
      const toggleScanner = () => {
        if (isConnected.value) {
          isConnected.value = false;
          emit('connection-status-change', false);
          stopPolling();
        } else {
          connect();
        }
      };
      
      onMounted(() => {
        if (props.autoConnect) {
          connect();
        }
      });
      
      onUnmounted(() => {
        stopPolling();
      });
      
      return {
        isConnected,
        videoUrl,
        latestBarcode,
        handleVideoError,
        reconnect,
        toggleScanner
      };
    }
  };
  </script>