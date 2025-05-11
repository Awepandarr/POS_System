<template>
  <div class="bg-gray-100 min-h-screen py-12">
    <div class="container mx-auto px-4">
      <div class="max-w-2xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
        
        <!-- Header -->
        <div class="bg-blue-600 text-white p-6">
          <h1 class="text-2xl font-bold">Complete Your Payment</h1>
        </div>

        <!-- Order Summary -->
        <div v-if="order" class="p-6 border-b">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <h3 class="text-lg font-semibold">Order Details</h3>
              <p>Order ID: {{ order.orderId || 'Will be generated' }}</p>
            </div>
            <div class="text-right">
              <p class="text-2xl font-bold">Total: ${{ order.finalAmount.toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="errorMessage" class="bg-red-100 p-4 text-red-800">
          {{ errorMessage }}
        </div>

        <!-- Payment Form -->
        <div class="p-6">
          <PaymentForm 
            v-if="order"
            :order="order"
            @payment-success="handlePaymentSuccess"
            @payment-error="handlePaymentError"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { paymentService, orderService } from '@/services/api.service';
import PaymentForm from '@/components/PaymentForm.vue';

const router = useRouter();
const order = ref(null);
const errorMessage = ref('');

onMounted(async () => {
  try {
    // Step 1: Check if we have a saved order in localStorage
    const savedOrder = localStorage.getItem('order');
    if (savedOrder) {
      const parsedOrder = JSON.parse(savedOrder);
      console.log('Loaded saved order:', parsedOrder);

      // Check if order already has an ID
      if (parsedOrder.orderId) {
        // Order already created on backend
        order.value = parsedOrder;
        console.log('Using existing order ID:', order.value.orderId);
      } else {
        // Need to create order on backend first
        console.log('Creating new order on backend');
        await createOrder(parsedOrder);
      }
    } else {
      errorMessage.value = 'No order found. Please create an order first.';
    }
  } catch (err) {
    console.error('Error initializing payment page:', err);
    errorMessage.value = err.message || 'Failed to load order. Please return to the cart.';
  }
});

// Updated createOrder method in PaymentPage.vue
const createOrder = async (orderData) => {
  try {
    // Format the order data to match backend expectations
    const formattedOrder = {
      customerId: orderData.customerId || 1,
      totalAmount: Number(orderData.totalAmount.toFixed(2)),
      taxAmount: Number(orderData.taxAmount.toFixed(2)),
      finalAmount: Number(orderData.finalAmount.toFixed(2)),
      discountAmount: Number((orderData.discountAmount || 0).toFixed(2)),
      orderType: orderData.orderType || "IN_STORE",
      orderItems: (orderData.orderItems || []).map(item => ({
        productId: Number(item.productId),
        quantity: Number(item.quantity),
        price: Number(Number(item.price).toFixed(2)),
        subtotal: Number(Number(item.price * item.quantity).toFixed(2))
      }))
    };

    console.log('Creating order on backend:', formattedOrder);
    
    // Call the API to create the order
    const response = await orderService.createOrder(formattedOrder);
    
    console.log('Order created successfully:', response.data);
    
    // Update the order with the returned order ID
    order.value = {
      ...orderData,
      orderId: response.data.orderId
    };
    
    // Save the updated order with ID back to localStorage
    localStorage.setItem('order', JSON.stringify(order.value));
    
    return order.value;
  } catch (error) {
    console.error('Error creating order:', error);
    throw new Error(error.response?.data?.error || 'Failed to create order');
  }
};

const handlePaymentSuccess = async (paymentInfo) => {
  try {
    console.log('Payment successful:', paymentInfo);
    
    // Clear the order from localStorage
    localStorage.removeItem('order');
    localStorage.removeItem('cart');

    // Redirect to confirmation page
    router.push({
      path: '/confirmation',
      query: {
        transactionId: paymentInfo.transactionId,
        amount: paymentInfo.amount,
        method: paymentInfo.method
      }
    });
  } catch (error) {
    console.error('handlePaymentSuccess error:', error);
    errorMessage.value = error.message || 'Error completing payment process.';
  }
};

const handlePaymentError = (error) => {
  console.error('Payment error:', error);
  errorMessage.value = error.message || 'Payment processing failed. Please try again.';
};
</script>