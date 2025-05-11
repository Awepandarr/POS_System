<template>
    <div>
      <h1 class="text-2xl font-bold mb-6">Stock Management</h1>
      <StockManager :categories="categories" :initialThreshold="10" />
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import StockManager from '../components/StockManager.vue';
  
  export default {
    components: {
      StockManager
    },
    setup() {
      const categories = ref([
        { id: 1, name: 'Electronics' },
        { id: 2, name: 'Clothing' },
        { id: 3, name: 'Home & Kitchen' },
        { id: 4, name: 'Books' }
      ]);
  
      return {
        categories
      };
    }
  }
  </script>