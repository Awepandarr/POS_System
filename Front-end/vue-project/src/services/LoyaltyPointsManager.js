// src/services/LoyaltyPointsManager.js
import { customerService } from './api.service';

/**
 * Convert points to discount amount based on points value
 * @param {number} points - Number of points to convert
 * @param {number} pointValue - Value of each point (default: 0.01)
 * @returns {number} Discount amount
 */
export const convertPointsToDiscount = (points, pointValue = 0.01) => {
  if (!points || points <= 0) return 0;
  return Number((points * pointValue).toFixed(2));
};

/**
 * Redeem loyalty points for a customer
 * @param {Object} redemptionData 
 * @param {number} redemptionData.customerId - ID of the customer
 * @param {number} redemptionData.pointsToRedeem - Number of points to redeem
 * @returns {Promise<Object>} Redemption result
 */
export const redeemLoyaltyPoints = async (redemptionData) => {
  try {
    // Validate input
    if (!redemptionData.customerId) {
      throw new Error('Customer ID is required');
    }
    
    if (!redemptionData.pointsToRedeem || redemptionData.pointsToRedeem <= 0) {
      throw new Error('Invalid number of points to redeem');
    }

    // Call customer service API to redeem points
    const response = await customerService.redeemLoyaltyPoints({
      customerId: redemptionData.customerId,
      pointsRedeemed: redemptionData.pointsToRedeem
    });

    // Validate response
    if (!response.data) {
      throw new Error('No response from loyalty points redemption');
    }

    // Return structured redemption result
    return {
      pointsRedeemed: redemptionData.pointsToRedeem,
      discountAmount: convertPointsToDiscount(redemptionData.pointsToRedeem),
      newPointsBalance: response.data.newPointsBalance || 0,
      transactionId: response.data.transactionId || null
    };
  } catch (error) {
    console.error('Loyalty Points Redemption Error:', error);
    
    // Provide more detailed error messaging
    if (error.response) {
      // The request was made and the server responded with a status code
      throw new Error(error.response.data.message || 'Failed to redeem loyalty points');
    } else if (error.request) {
      // The request was made but no response was received
      throw new Error('No response received from server');
    } else {
      // Something happened in setting up the request
      throw error;
    }
  }
};

/**
 * Validate points redemption before API call
 * @param {Object} params - Redemption parameters
 * @param {number} params.availablePoints - Total points available
 * @param {number} params.pointsToRedeem - Points attempting to redeem
 * @returns {Object} Validation result
 */
export const validatePointsRedemption = (params) => {
  const { availablePoints, pointsToRedeem } = params;
  
  if (!pointsToRedeem || pointsToRedeem <= 0) {
    return { 
      isValid: false, 
      message: 'Invalid number of points to redeem' 
    };
  }
  
  if (pointsToRedeem > availablePoints) {
    return { 
      isValid: false, 
      message: `Insufficient points. You have ${availablePoints} points available.` 
    };
  }
  
  return { 
    isValid: true, 
    message: 'Points redemption is valid' 
  };
};

/**
 * Calculate points earned based on purchase amount
 * @param {number} amount - Total purchase amount
 * @param {number} pointRate - Points earned per dollar (default: 1 point per dollar)
 * @returns {number} Points earned
 */
export const calculatePointsEarned = (amount, pointRate = 1) => {
  if (!amount || amount <= 0) return 0;
  return Math.floor(amount * pointRate);
};

/**
 * Determine loyalty tier based on total points
 * @param {number} totalPoints - Total loyalty points
 * @returns {string} Loyalty tier
 */
export const getPointsTier = (totalPoints) => {
  if (!totalPoints || totalPoints < 0) return 'No Tier';
  
  if (totalPoints < 100) return 'Bronze';
  if (totalPoints < 500) return 'Silver';
  if (totalPoints < 1000) return 'Gold';
  return 'Platinum';
};

/**
 * Estimate points needed to reach next tier
 * @param {number} currentPoints - Current loyalty points
 * @returns {Object} Next tier information
 */
export const getNextTierInfo = (currentPoints) => {
  const currentTier = getPointsTier(currentPoints);
  
  const tierThresholds = {
    'No Tier': { next: 'Bronze', pointsToNext: 100 },
    'Bronze': { next: 'Silver', pointsToNext: 500 },
    'Silver': { next: 'Gold', pointsToNext: 1000 },
    'Gold': { next: 'Platinum', pointsToNext: Infinity }
  };

  const tierInfo = tierThresholds[currentTier];
  
  return {
    currentTier,
    nextTier: tierInfo.next,
    pointsToNextTier: Math.max(0, tierInfo.pointsToNext - currentPoints)
  };
};

// Export all functions for individual import
export default {
  convertPointsToDiscount,
  redeemLoyaltyPoints,
  validatePointsRedemption,
  calculatePointsEarned,
  getPointsTier,
  getNextTierInfo
};