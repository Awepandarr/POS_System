<template>
  <div class="bg-gray-100 min-h-screen py-12">
    <div class="container mx-auto px-4">
      <div class="max-w-3xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
        <!-- Success Header -->
        <div class="bg-green-500 text-white p-8 text-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h1 class="text-3xl font-bold">Payment Successful!</h1>
          <p class="mt-2 text-lg">Thank you for your purchase</p>
        </div>

        <!-- Transaction Details -->
        <div class="p-8">
          <div class="grid grid-cols-2 gap-6 mb-8">
            <div>
              <h3 class="text-lg font-semibold mb-2">Transaction Details</h3>
              <div class="bg-gray-50 p-4 rounded-lg">
                <p class="mb-2"><strong>Transaction ID:</strong> {{ transactionId }}</p>
                <p class="mb-2"><strong>Amount:</strong> ${{ amount.toFixed(2) }}</p>
                <p class="mb-2"><strong>Payment Method:</strong> {{ paymentMethod }}</p>
                <p><strong>Date:</strong> {{ formatDate(new Date()) }}</p>
              </div>
            </div>

            <div>
              <h3 class="text-lg font-semibold mb-2">Order Summary</h3>
              <div class="bg-gray-50 p-4 rounded-lg">
                <div v-if="order">
                  <p class="mb-2"><strong>Order ID:</strong> {{ order.orderId }}</p>
                  <p class="mb-2"><strong>Subtotal:</strong> ${{ order.totalAmount.toFixed(2) }}</p>
                  <p class="mb-2"><strong>Tax:</strong> ${{ order.taxAmount.toFixed(2) }}</p>
                  <p class="mb-2"><strong>Discount:</strong> ${{ order.discountAmount ? order.discountAmount.toFixed(2) : '0.00' }}</p>
                  <p class="font-bold"><strong>Total:</strong> ${{ order.finalAmount.toFixed(2) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div v-if="order && order.orderItems" class="mb-8">
            <h3 class="text-lg font-semibold mb-4">Order Items</h3>
            <div class="border rounded-lg overflow-hidden">
              <table class="w-full">
                <thead class="bg-gray-100">
                  <tr>
                    <th class="p-3 text-left">Product</th>
                    <th class="p-3 text-center">Quantity</th>
                    <th class="p-3 text-right">Price</th>
                    <th class="p-3 text-right">Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in order.orderItems" :key="index" class="border-b">
                    <td class="p-3">{{ item.productName || `Product ${item.productId}` }}</td>
                    <td class="p-3 text-center">{{ item.quantity }}</td>
                    <td class="p-3 text-right">${{ item.price.toFixed(2) }}</td>
                    <td class="p-3 text-right">${{ (item.price * item.quantity).toFixed(2) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-between">
            <button 
              @click="generateInvoice" 
              class="bg-blue-500 text-white px-6 py-3 rounded-lg hover:bg-blue-600 transition"
            >
              Generate Invoice
            </button>
            <button 
              @click="returnToHome" 
              class="bg-gray-500 text-white px-6 py-3 rounded-lg hover:bg-gray-600 transition"
            >
              Return to Home
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { invoiceService } from '@/services/api.service';

const route = useRoute();
const router = useRouter();

const transactionId = ref(route.query.transactionId || 'N/A');
const amount = ref(parseFloat(route.query.amount) || 0);
const paymentMethod = ref(route.query.method || 'N/A');
const order = ref(null);

onMounted(() => {
  // Try to load order from localStorage
  const savedOrder = localStorage.getItem('order');
  if (savedOrder) {
    try {
      order.value = JSON.parse(savedOrder);
    } catch (error) {
      console.error('Error parsing order:', error);
    }
  }
});

const formatDate = (date) => {
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const generateInvoice = async () => {
  if (!order.value) return;

  try {
    const invoiceData = {
      orderId: order.value.orderId,
      customerId: order.value.customerId || 1,
      invoiceDate: new Date().toISOString(),
      subtotal: order.value.totalAmount,
      discount: order.value.discountAmount || 0,
      taxAmount: order.value.taxAmount,
      serviceCharge: 0,
      finalAmount: order.value.finalAmount
    };

    await invoiceService.generateInvoice(invoiceData);
    
    // Optionally, trigger a download or show a success message
    alert('Invoice generated successfully!');
  } catch (error) {
    console.error('Invoice generation error:', error);
    alert('Failed to generate invoice. Please try again.');
  }
};

const returnToHome = () => {
  // Clear order and cart from localStorage
  localStorage.removeItem('order');
  localStorage.removeItem('cart');
  
  // Navigate back to home
  router.push('/');
};
</script>

<style scoped>
@media print {
  body * {
    visibility: hidden;
  }
  #app, #app > div {
    visibility: visible;
  }
  #app > div {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}
</style>