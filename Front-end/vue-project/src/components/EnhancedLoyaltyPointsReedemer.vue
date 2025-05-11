<!-- src/components/EnhancedLoyaltyPointsRedeemer.vue -->
<template>
    <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200 mt-4">
      <div class="p-4 bg-gradient-to-r from-purple-500 to-purple-600 text-white">
        <h3 class="text-lg font-semibold flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          Loyalty Points Redemption
        </h3>
      </div>
  
      <div v-if="!customer" class="p-5 text-center text-gray-500">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 mx-auto text-gray-300 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <p>Please select a customer to view loyalty points</p>
      </div>
  
      <div v-else class="p-5">
        <div class="flex justify-between items-center mb-4">
          <div>
            <div class="text-sm text-gray-500">Available Points</div>
            <div class="text-2xl font-bold text-purple-600">{{ customer.loyaltyPoints || 0 }}</div>
          </div>
  
          <div class="px-4 py-2 bg-purple-100 rounded-full">
            <span class="text-purple-700 font-medium">£{{ pointsValue }} per point</span>
          </div>
        </div>
  
        <div class="border-t border-gray-100 pt-4">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">Points to Redeem</label>
            <div class="flex">
              <input 
                type="number" 
                v-model.number="pointsToRedeem" 
                :max="customer.loyaltyPoints || 0"
                min="0"
                class="w-full p-2 border rounded-l focus:ring-2 focus:ring-purple-500 focus:outline-none"
              >
              <button 
                @click="setMaxPoints" 
                class="bg-gray-100 border border-l-0 border-gray-300 px-3 text-gray-700 rounded-r hover:bg-gray-200"
              >
                Max
              </button>
            </div>
            <div class="text-sm text-gray-500 mt-1">
              Equivalent to {{ calculateDiscount }} discount
            </div>
          </div>
  
          <div class="mt-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Apply to</label>
            <select 
              v-model="applyTo" 
              class="w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:outline-none"
            >
              <option value="order">Entire Order</option>
              <option value="item">Specific Items</option>
            </select>
          </div>
  
          <div v-if="applyTo === 'item' && orderItems.length > 0" class="mt-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Select Items</label>
            <div class="border rounded max-h-40 overflow-y-auto">
              <div 
                v-for="(item, index) in orderItems" 
                :key="index"
                class="p-2 border-b last:border-b-0 flex items-center"
              >
                <input 
                  type="checkbox" 
                  :id="'item-' + index" 
                  v-model="selectedItems" 
                  :value="index" 
                  class="mr-2"
                >
                <label :for="'item-' + index" class="flex-1 cursor-pointer">
                  {{ item.name }} - {{ formatCurrency(item.price) }}
                </label>
              </div>
            </div>
          </div>
  
          <div class="flex justify-between items-center mt-4">
            <div>
              <div class="text-sm font-medium">Total Discount</div>
              <div class="text-lg font-bold text-purple-600">{{ formatCurrency(totalDiscount) }}</div>
            </div>
  
            <div>
              <button 
                @click="redeemPoints"
                :disabled="!canRedeem"
                class="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span v-if="processing">
                  <svg class="animate-spin h-5 w-5 text-white inline mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Processing...
                </span>
                <span v-else>Apply Discount</span>
              </button>
            </div>
          </div>
        </div>
  
        <div v-if="redeemMessage" class="mt-4 p-3 rounded-md" :class="messageClass">
          {{ redeemMessage }}
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, watch } from 'vue';
  import LoyaltyPointsManager from '../services/LoyaltyPointsManager';
  
  export default {
    props: {
      customer: {
        type: Object,
        default: null
      },
      orderItems: {
        type: Array,
        default: () => []
      },
      pointsValue: {
        type: Number,
        default: 0.01 // $0.01 per point by default
      }
    },
  
    emits: ['apply-discount', 'update:customer'],
  
    setup(props, { emit }) {
      const pointsToRedeem = ref(0);
      const applyTo = ref('order');
      const selectedItems = ref([]);
      const redeemMessage = ref('');
      const processing = ref(false);
      const messageType = ref('success'); // 'success' or 'error'
  
      // Calculate the discount amount based on points
      const calculateDiscount = computed(() => {
        const discount = LoyaltyPointsManager.convertPointsToDiscount(pointsToRedeem.value, props.pointsValue);
        return formatCurrency(discount);
      });
  
      // Calculate the total discount amount
      const totalDiscount = computed(() => {
        return LoyaltyPointsManager.convertPointsToDiscount(pointsToRedeem.value, props.pointsValue);
      });
  
      // CSS class for the message box
      const messageClass = computed(() => {
        return messageType.value === 'success' 
          ? 'bg-green-100 text-green-800'
          : 'bg-red-100 text-red-800';
      });
  
      // Check if redemption is possible
      const canRedeem = computed(() => {
        // Ensure a customer is selected and points are valid
        if (!props.customer || pointsToRedeem.value <= 0 || processing.value) {
          return false;
        }
  
        // Validate points redemption
        const validationResult = LoyaltyPointsManager.validatePointsRedemption({
          availablePoints: props.customer.loyaltyPoints || 0,
          pointsToRedeem: pointsToRedeem.value
        });
  
        // Additional check for item-specific redemption
        if (!validationResult.isValid || 
            (applyTo.value === 'item' && selectedItems.value.length === 0)) {
          return false;
        }
  
        return true;
      });
  
      // Format currency for display
      const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-GB', {
          style: 'currency',
          currency: 'GBP',
        }).format(amount);
      };
  
      // Set maximum available points
      const setMaxPoints = () => {
        pointsToRedeem.value = props.customer?.loyaltyPoints || 0;
      };
  
      // Redeem points for discount with API call
      const redeemPoints = async () => {
        // Additional check before processing
        if (!canRedeem.value) return;
        
        processing.value = true;
        messageType.value = 'success';
        redeemMessage.value = '';
        
        try {
    const redemptionData = {
      customerId: props.customer.customerId,
      pointsToRedeem: pointsToRedeem.value
    };

  
          // Call API to redeem points
          const result = await LoyaltyPointsManager.redeemLoyaltyPoints(redemptionData);

  
          // Update the customer with new points balance
          const updatedCustomer = { ...props.customer };
          updatedCustomer.loyaltyPoints = result.newPointsBalance;
          emit('update:customer', updatedCustomer);
  
          // Build discount data for parent component
          const discountData = {
      discountAmount: result.discountAmount,
      pointsRedeemed: result.pointsRedeemed,
      applyTo: applyTo.value,
      selectedItems: applyTo.value === 'item' 
        ? selectedItems.value.map(index => props.orderItems[index]) 
        : []
    };
          
          // Emit event to update the parent component
          emit('apply-discount', discountData);
  
          // Show success message
          messageType.value = 'success';
          redeemMessage.value = `Successfully redeemed ${result.pointsRedeemed} points for a ${formatCurrency(result.discountAmount)} discount!`;
          
          // Reset form
          pointsToRedeem.value = 0;
          if (applyTo.value === 'item') {
            selectedItems.value = [];
          }
        } catch (error) {
          console.error('Error redeeming points:', error);
          messageType.value = 'error';
          redeemMessage.value = error.message || 'Failed to redeem points. Please try again.';
        } finally {
          processing.value = false;
        }
      };
  
      // Reset selected items when changing apply-to mode
      watch(applyTo, () => {
        selectedItems.value = [];
      });
  
      // Reset form when customer changes
      watch(() => props.customer, () => {
        pointsToRedeem.value = 0;
        selectedItems.value = [];
        redeemMessage.value = '';
      });
  
      return {
        pointsToRedeem,
        applyTo,
        selectedItems,
        redeemMessage,
        processing,
        messageType,
        messageClass,
        calculateDiscount,
        totalDiscount,
        canRedeem,
        formatCurrency,
        setMaxPoints,
        redeemPoints
      };
    }
  };
  </script>