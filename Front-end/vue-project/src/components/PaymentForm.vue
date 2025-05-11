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

    <!-- Submit Button -->
    <div class="mt-6">
      <button 
        type="submit"
        class="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-200"
      >
        Complete Payment
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue';

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

const processPayment = async () => {
  try {
    // Create payment info object
    const paymentInfo = {
      transactionId: generateTransactionId(),
      amount: props.order.finalAmount,
      method: paymentMethod.value
    };

    // Add card details if necessary
    if (paymentMethod.value === 'CARD') {
      // Validate card details first
      validateCardDetails();
      
      // Add validated card details to payment info
      paymentInfo.cardDetails = {
        cardNumber: cardDetails.value.cardNumber.trim(),
        expiryDate: cardDetails.value.expiryDate.trim(),
        cvv: cardDetails.value.cvv.trim(),
        cardholderName: cardDetails.value.cardholderName.trim()
      };
    }

    console.log('Payment form processing payment:', {
      ...paymentInfo,
      cardDetails: paymentInfo.cardDetails ? '(hidden for security)' : undefined
    });
    
    // Emit success with payment info
    emit('payment-success', paymentInfo);
  } catch (error) {
    console.error('Payment form error:', error);
    emit('payment-error', { message: error.message || 'Payment processing failed' });
  }
};
</script>