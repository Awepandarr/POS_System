// src/services/stock.service.js
import axios from 'axios';

// Base URL for API
const API_URL = 'http://localhost:8080/api';

// Stock service object
export const stockService = {
  // Get stock information for a product
  getStock(productId) {
    return axios.get(`${API_URL}/stock/${productId}`);
  },
  
  // Update stock for a product (set exact quantity)
  updateStock(productId, stockQuantity) {
    return axios.put(`${API_URL}/stock/${productId}`, {
      stockQuantity
    });
  },
  
  // Adjust stock for a product (add or remove quantity)
  adjustStock(productId, adjustment) {
    return axios.post(`${API_URL}/stock/${productId}/adjust`, {
      adjustment
    });
  },
  
  // Get low stock products
  getLowStockProducts(threshold = 10) {
    return axios.get(`${API_URL}/stock/low-stock?threshold=${threshold}`);
  },
  
  // Batch update stock for multiple products
  batchUpdateStock(productsData) {
    return axios.post(`${API_URL}/stock/batch-update`, {
      products: productsData
    });
  }
};

// Helper functions that can be imported directly
export const updateProductStock = (productId, quantity) => {
  return stockService.updateStock(productId, quantity);
};

export const addToStock = (productId, quantity) => {
  return stockService.adjustStock(productId, quantity);
};

export const removeFromStock = (productId, quantity) => {
  return stockService.adjustStock(productId, -quantity);
};

export const batchUpdateStock = (productsData) => {
  return stockService.batchUpdateStock(productsData);
};

export const getLowStockProducts = (threshold) => {
  return stockService.getLowStockProducts(threshold);
};