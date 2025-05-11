// src/services/StockManager.js
import { stockService } from './stock.service';

/**
 * Updates the stock quantity for a specific product
 * @param {number} productId - The ID of the product to update
 * @param {number} newQuantity - The new quantity to set
 * @returns {Promise} - Promise that resolves with the API response
 */
export const updateProductStock = async (productId, newQuantity) => {
  try {
    const response = await stockService.updateStock(productId, newQuantity);
    return response.data;
  } catch (error) {
    console.error('Error updating product stock:', error);
    throw error;
  }
};

/**
 * Adds a specified quantity to a product's current stock
 * @param {number} productId - The ID of the product to update
 * @param {number} quantity - The quantity to add
 * @returns {Promise} - Promise that resolves with the API response
 */
export const addToStock = async (productId, quantity) => {
  try {
    const response = await stockService.adjustStock(productId, quantity);
    return response.data;
  } catch (error) {
    console.error('Error adding to stock:', error);
    throw error;
  }
};

/**
 * Removes a specified quantity from a product's current stock
 * @param {number} productId - The ID of the product to update
 * @param {number} quantity - The quantity to remove
 * @returns {Promise} - Promise that resolves with the API response
 */
export const removeFromStock = async (productId, quantity) => {
  try {
    const response = await stockService.adjustStock(productId, -quantity);
    return response.data;
  } catch (error) {
    console.error('Error removing from stock:', error);
    throw error;
  }
};

/**
 * Updates stock quantities for multiple products at once
 * @param {Array} productsData - Array of objects with productId and stockQuantity
 * @returns {Promise} - Promise that resolves with the API response
 */
export const batchUpdateStock = async (productsData) => {
  try {
    const response = await stockService.batchUpdateStock(productsData);
    return response.data;
  } catch (error) {
    console.error('Error updating batch stock:', error);
    throw error;
  }
};

/**
 * Gets products with stock quantities below specified threshold
 * @param {number} threshold - The stock threshold
 * @returns {Promise} - Promise that resolves with the API response
 */
export const getLowStockProducts = async (threshold = 10) => {
  try {
    const response = await stockService.getLowStockProducts(threshold);
    return response.data;
  } catch (error) {
    console.error('Error getting low stock products:', error);
    throw error;
  }
};