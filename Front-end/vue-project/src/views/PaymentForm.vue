<template>
    <form @submit.prevent="processPayment">
      <!-- Payment Method Selection -->
      <div class="mb-4">
        <label class="block text-gray-700 mb-2">Payment Method</label>
        <div class="flex space-x-4">
          <button 
            type="button"
            @click="paymentMethod = 'CARD'"
            :class="[
              'px-4 py-2 border rounded', 
              paymentMethod === 'CARD' 
                ? 'bg-blue-500 text-white border-blue-500' 
                : 'bg-white text-gray-700 border-gray-300'
            ]"
          >
            Credit Card
          </button>
          <button 
            type="button"
            @click="paymentMethod = 'CASH'"
            :class="[
              'px-4 py-2 border rounded', 
              paymentMethod === 'CASH' 
                ? 'bg-blue-500 text-white border-blue-500' 
                : 'bg-white text-gray-700 border-gray-300'
            ]"
          >
            Cash
          </button>
        </div>
      </div>
  
      <!-- Card Details (visible only when Card payment is selected) -->
      <div v-if="paymentMethod === 'CARD'" class="space-y-4">
        <div class="mb-4">
          <label class="block text-gray-700 mb-2">Card Number</label>
          <input 
            type="text" 
            v-model="cardDetails.cardNumber"
            class="w-full px-3 py-2 border rounded" 
            placeholder="1234 5678 9012 3456"
            required
          />
          <p class="text-xs text-gray-500 mt-1">
            For testing, use any valid format card number except 4000000000000002 and 4000000000009995
          </p>
        </div>
  
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-gray-700 mb-2">Expiry Date (MM/YY)</label>
            <input 
              type="text" 
              v-model="cardDetails.expiryDate"
              class="w-full px-3 py-2 border rounded" 
              placeholder="MM/YY"
              maxlength="5"
              required
            />
          </div>
          <div>
            <label class="block text-gray-700 mb-2">CVV</label>
            <input 
              type="text" 
              v-model="cardDetails.cvv"
              class="w-full px-3 py-2 border rounded" 
              placeholder="123"
              maxlength="4"
              required
            />
          </div>
        </div>
  
        <div>
          <label class="block text-gray-700 mb-2">Cardholder Name</label>
          <input 
            type="text" 
            v-model="cardDetails.cardholderName"
            class="w-full px-3 py-2 border rounded" 
            placeholder="John Doe"
            required
          />
        </div>
      </div>
  
      <!-- Cash payment options if needed -->
      <div v-if="paymentMethod === 'CASH'" class="my-4">
        <p class="text-gray-700">Please pay cash at the counter upon collection.</p>
      </div>
  
      <!-- Order Summary -->
      <div class="mt-6 bg-gray-50 p-4 rounded-lg">
        <h3 class="text-lg font-bold mb-4">Order Summary</h3>
        <div class="space-y-2">
          <div class="flex justify-between">
            <span>Subtotal</span>
            <span>${{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between">
            <span>Tax</span>
            <span>${{ order.taxAmount.toFixed(2) }}</span>
          </div>
          <div v-if="order.discountAmount > 0" class="flex justify-between">
            <span>Discount</span>
            <span>-${{ order.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between font-bold text-xl border-t pt-2 mt-2">
            <span>Total</span>
            <span>${{ order.finalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
  
      <!-- Order Items -->
      <div class="mt-6 bg-gray-50 p-4 rounded-lg">
        <h3 class="text-lg font-bold mb-4">Order Items</h3>
        <div v-for="(item, index) in order.orderItems" :key="index" class="flex justify-between py-2 border-b last:border-b-0">
          <div>
            <span>Product ID: {{ item.productId }}</span>
            <span class="ml-4">Qty: {{ item.quantity }}</span>
          </div>
          <span>${{ item.subtotal.toFixed(2) }}</span>
        </div>
      </div>
  
      <!-- Debug Info -->
      <div class="mt-6 bg-gray-50 p-4 rounded-lg">
        <h3 class="text-sm font-bold mb-2 text-gray-500">Debug Information</h3>
        <div class="text-xs text-gray-500">
          <p>Order ID: {{ order.orderId || 'Not set' }}</p>
        </div>
      </div>
  
      <!-- Submit Button -->
      <div class="mt-6">
        <button 
          type="submit"
          class="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-200"
          :disabled="processing"
        >
          {{ processing ? 'Processing...' : 'Complete Payment' }}
        </button>
      </div>
    </form>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { paymentService } from '@/services/api.service';
  
  const props = defineProps({
    order: {
      type: Object,
      required: true
    }
  });
  
  const emit = defineEmits(['payment-success', 'payment-error']);
  
  const paymentMethod = ref('CARD');
  const cardDetails = ref({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardholderName: ''
  });
  const processing = ref(false);
  
  const generateTransactionId = () => {
    return 'TXN-' + Date.now() + '-' + Math.floor(Math.random() * 1000);
  };
  
  // Validate card details
  const validateCardDetails = () => {
    // Validate card number format
    const cardNumber = cardDetails.value.cardNumber.replaceAll(' ', '');
    if (cardNumber.length < 13 || cardNumber.length > 19 || !/^\d+$/.test(cardNumber)) {
      throw new Error('Invalid card number');
    }
    
    // Validate expiry date format
    const expiryDate = cardDetails.value.expiryDate;
    if (!expiryDate.match(/^\d{2}\/\d{2}$/)) {
      throw new Error('Expiry date must be in MM/YY format');
    }
    
    // Check expiry date is not in the past
    const [month, year] = expiryDate.split('/');
    const expiry = new Date(2000 + parseInt(year), parseInt(month) - 1);
    const now = new Date();
    if (expiry < now) {
      throw new Error('Card has expired');
    }
    
    // Validate CVV
    const cvv = cardDetails.value.cvv;
    if (cvv.length < 3 || cvv.length > 4 || !/^\d+$/.test(cvv)) {
      throw new Error('CVV must be 3 or 4 digits');
    }
    
    return true;
  };
  
// Updated processPayment method in PaymentForm.vue
const processPayment = async () => {
  if (processing.value) return;
  
  processing.value = true;
  
  try {
    // Check if order has an ID
    if (!props.order.orderId) {
      throw new Error("Order ID is missing. Order must be created first.");
    }

    // Create a simple payment request with ONLY the required fields
    const paymentRequest = {
      transactionId: generateTransactionId(),
      orderId: props.order.orderId,
      amount: Number(props.order.finalAmount.toFixed(2)),
      paymentMethod: paymentMethod.value
    };

    // Add card details if necessary
    if (paymentMethod.value === 'CARD') {
      validateCardDetails();
      
      paymentRequest.cardDetails = {
        cardNumber: cardDetails.value.cardNumber.trim(),
        expiryDate: cardDetails.value.expiryDate.trim(),
        cvv: cardDetails.value.cvv.trim(),
        cardholderName: cardDetails.value.cardholderName.trim()
      };
    }

    console.log('Payment request:', {
      ...paymentRequest,
      cardDetails: paymentRequest.cardDetails ? '(hidden for security)' : undefined
    });
    
    // Process payment
    const response = await paymentService.processPayment(paymentRequest);
    console.log('Payment response:', response.data);
    
    // Emit success with payment info
    emit('payment-success', {
      transactionId: paymentRequest.transactionId,
      amount: paymentRequest.amount,
      method: paymentRequest.paymentMethod
    });
  } catch (error) {
    console.error('Payment error:', error);
    
    // Extract the most specific error message
    const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          error.message || 
                          'Payment processing failed';
    
    emit('payment-error', { message: errorMessage });
  } finally {
    processing.value = false;
  }
};

  </script>