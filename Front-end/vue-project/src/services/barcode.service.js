// src/services/barcode.service.js

import axios from 'axios';

// Create an Axios instance with default config for the barcode scanner API
const barcodeApiClient = axios.create({
  baseURL: 'http://localhost:5173',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});

// Request interceptor for logging
barcodeApiClient.interceptors.request.use(config => {
  console.log('Barcode API Request:', {
    method: config.method,
    url: config.url,
    data: config.data
  });
  return config;
}, error => {
  console.error('Barcode API Request Error:', error);
  return Promise.reject(error);
});

// Response interceptor for logging and error handling
barcodeApiClient.interceptors.response.use(
  response => {
    console.log('Barcode API Response:', {
      status: response.status,
      data: response.data
    });
    return response;
  },
  error => {
    console.error('Barcode API Error:', {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });

    // Customize error messages
    if (error.response) {
      switch (error.response.status) {
        case 404:
          error.message = 'Barcode scanner service not found. Please make sure it\'s running.';
          break;
        case 500:
          error.message = 'Barcode scanner service error. Please check the scanner connection.';
          break;
        default:
          error.message = error.response.data?.error || 'An unexpected error occurred with the barcode scanner.';
      }
    } else if (error.request) {
      error.message = 'No response received from barcode scanner service. Please check if it\'s running.';
    }

    return Promise.reject(error);
  }
);

export const barcodeService = {
  // Get latest scanned barcode
  getLatestBarcode() {
    return barcodeApiClient.get('/latest_barcode');
  },
  
  // Get latest product info from barcode
  getLatestProduct() {
    return barcodeApiClient.get('/latest_product');
  },
  
  // Check if scanner is connected and service is online
  checkHealth() {
    return barcodeApiClient.get('/health');
  },
  
  // Manually trigger a scan with a barcode
  manualScan(barcode) {
    return barcodeApiClient.post('/scan', { barcode });
  }
};

// Export the service
export default barcodeService;