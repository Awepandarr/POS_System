<template>
    <div class="border rounded p-4 shadow bg-white">
      <h3 class="text-lg font-bold mb-3">Transaction</h3>
  
      <div class="p-3">
        <p><strong>Transaction ID:</strong> {{ transaction.id }}</p>
        <p><strong>Date Time:</strong> {{ transaction.date }}</p>
        <p><strong>Total Amount:</strong> £{{ transaction.amount }}</p>
        <p><strong>Payment Method:</strong> {{ transaction.paymentMethod }}</p>
      </div>
  
      <div class="mt-4 space-x-2">
        <button class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">Add Product</button>
        <button class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">Calculate Total</button>
        <button class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">Process Payment</button>
        <button class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">Generate Receipt</button>
        <button class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 mt-2">Remove Product</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const transaction = ref({
    id: 'TRN-2854',
    date: '25 Feb 2025, 10:45 AM',
    amount: 86.97,
    paymentMethod: 'Credit Card'
  });
  </script>
  
  <style scoped>
  /* Custom styling if needed */
  </style>
  