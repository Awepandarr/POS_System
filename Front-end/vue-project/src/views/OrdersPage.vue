<template>
  <div class="container mx-auto px-4 py-8">
    <div class="bg-white shadow-xl rounded-lg overflow-hidden">
      <!-- Header with Refresh and Filters -->
      <div class="px-6 py-4 bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-between">
        <h1 class="text-2xl font-bold text-white">Order Management</h1>
        <div class="flex items-center space-x-4">
          <button 
            @click="filterTodaysOrders" 
            class="bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-md transition-colors flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            Today's Orders
          </button>
          <button 
            @click="refreshOrders" 
            class="bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-md transition-colors flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh
          </button>
        </div>
      </div>

      <!-- Filters and Sorting Section -->
      <div class="px-6 py-4 bg-gray-50 grid grid-cols-1 md:grid-cols-6 gap-4">
        <!-- Date Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Order Date</label>
          <input 
            type="date" 
            v-model="filters.orderDate"
            class="mt-1 block w-full pl-3 pr-3 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
          />
        </div>
        
        <!-- Payment Status Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Status</label>
          <select 
            v-model="filters.paymentStatus" 
            class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
          >
            <option value="">All Statuses</option>
            <option value="PAID">Paid</option>
            <option value="PENDING">Pending</option>
            <option value="FAILED">Failed</option>
          </select>
        </div>

        <!-- Order Type Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Order Type</label>
          <select 
            v-model="filters.orderType" 
            class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
          >
            <option value="">All Types</option>
            <option value="Online">Online</option>
            <option value="In-Store">In-Store</option>
            <option value="Phone">Phone</option>
          </select>
        </div>

        <!-- Customer ID Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Customer ID</label>
          <input 
            type="text" 
            v-model="filters.customerId"
            placeholder="Enter Customer ID" 
            class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
          />
        </div>

        <!-- Amount Range Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Amount Range</label>
          <div class="flex space-x-2">
            <input 
              type="number" 
              v-model.number="filters.minAmount" 
              placeholder="Min" 
              class="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            />
            <input 
              type="number" 
              v-model.number="filters.maxAmount" 
              placeholder="Max" 
              class="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            />
          </div>
        </div>

        <!-- Sorting -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Sort By</label>
          <div class="flex space-x-2">
            <select 
              v-model="sorting.field"
              class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option value="orderId">Order ID</option>
              <option value="customerId">Customer</option>
              <option value="finalAmount">Total Amount</option>
              <option value="orderType">Order Type</option>
              <option value="orderDate">Order Date</option>
            </select>
            <select 
              v-model="sorting.order"
              class="mt-1 block w-24 pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option value="asc">Asc</option>
              <option value="desc">Desc</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
      
      <!-- Error State -->
      <div v-else-if="error" class="p-6 bg-red-50 border-l-4 border-red-500 text-red-700">
        <p class="font-bold">Error</p>
        <p>{{ error }}</p>
      </div>
      
      <!-- Orders Table -->
      <div v-else-if="sortedAndFilteredOrders.length" class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order Date</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Subtotal</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Tax</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Total Amount</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order Type</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr 
              v-for="order in sortedAndFilteredOrders" 
              :key="order.orderId" 
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                #{{ order.orderId }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                Customer {{ order.customerId }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(order.orderDate) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900">
                {{ formatCurrency(order.totalAmount) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900">
                {{ formatCurrency(order.taxAmount) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900 font-semibold">
                {{ formatCurrency(order.finalAmount) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span 
                  :class="getPaymentStatusClass(order.paymentStatus)"
                  class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full"
                >
                  {{ order.paymentStatus }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ order.orderType }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button 
                  @click="viewOrderDetails(order.orderId)"
                  class="text-blue-600 hover:text-blue-900 transition-colors"
                >
                  View Details
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- No Orders State -->
      <div v-else class="text-center py-12 bg-gray-50">
        <svg 
          xmlns="http://www.w3.org/2000/svg" 
          class="h-16 w-16 mx-auto text-gray-400 mb-4" 
          fill="none" 
          viewBox="0 0 24 24" 
          stroke="currentColor"
        >
          <path 
            stroke-linecap="round" 
            stroke-linejoin="round" 
            stroke-width="2" 
            d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" 
          />
        </svg>
        <p class="text-xl text-gray-500">No orders found</p>
        <p class="text-gray-400 mt-2">No orders match the current filters</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import _ from 'lodash';

export default {
  name: 'OrderPage',
  data() {
    return {
      orders: [],
      loading: true,
      error: null,
      filters: {
        orderDate: '',
        paymentStatus: '',
        orderType: '',
        customerId: '',
        minAmount: null,
        maxAmount: null
      },
      sorting: {
        field: 'orderId',
        order: 'desc'
      }
    };
  },
  computed: {
    filteredOrders() {
      return this.orders.filter(order => {
        // Date Filter
        if (this.filters.orderDate) {
          const orderDate = this.parseDate(order.orderDate);
          const filterDate = new Date(this.filters.orderDate);
          
          if (!this.isSameDay(orderDate, filterDate)) {
            return false;
          }
        }

        // Payment Status Filter
        if (this.filters.paymentStatus && 
            order.paymentStatus !== this.filters.paymentStatus) {
          return false;
        }

        // Order Type Filter
        if (this.filters.orderType && 
            order.orderType !== this.filters.orderType) {
          return false;
        }

        // Customer ID Filter
        if (this.filters.customerId && 
            !order.customerId.toString().includes(this.filters.customerId)) {
          return false;
        }

        // Amount Range Filter
        const finalAmount = order.finalAmount;
        if (this.filters.minAmount !== null && 
            finalAmount < this.filters.minAmount) {
          return false;
        }

        if (this.filters.maxAmount !== null && 
            finalAmount > this.filters.maxAmount) {
          return false;
        }

        return true;
      });
    },
    sortedAndFilteredOrders() {
      const filtered = this.filteredOrders;
      
      // Sorting
      const sorted = _.orderBy(
        filtered, 
        [this.sorting.field], 
        [this.sorting.order]
      );

      return sorted;
    }
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      try {
        this.loading = true;
        const response = await axios.get('http://localhost:8080/api/orders');
        this.orders = response.data;
        this.loading = false;
      } catch (error) {
        console.error('Error fetching orders:', error);
        this.error = error.response 
          ? error.response.data.error 
          : 'Failed to load orders. Please try again later.';
        this.loading = false;
      }
    },
    refreshOrders() {
      this.fetchOrders();
      // Reset filters and sorting when refreshing
      this.filters = {
        orderDate: '',
        paymentStatus: '',
        orderType: '',
        customerId: '',
        minAmount: null,
        maxAmount: null
      };
      this.sorting = {
        field: 'orderId',
        order: 'desc'
      };
    },
    filterTodaysOrders() {
      // Set today's date as the filter
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      
      this.filters.orderDate = `${year}-${month}-${day}`;
    },
    parseDate(dateString) {
      // Parse the date string from the API
      if (!dateString) return null;
      
      // Handle different date formats
      if (dateString instanceof Date) {
        return dateString;
      }
      
      return new Date(dateString);
    },
    isSameDay(date1, date2) {
      if (!date1 || !date2) return false;
      
      return date1.getFullYear() === date2.getFullYear() &&
             date1.getMonth() === date2.getMonth() &&
             date1.getDate() === date2.getDate();
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      
      const date = this.parseDate(dateString);
      if (!date) return 'Invalid Date';
      
      return new Intl.DateTimeFormat('en-GB', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      }).format(date);
    },
    viewOrderDetails(orderId) {
      // Ensure we navigate to the order details page
      this.$router.push({ 
        name: 'OrderDetails', 
        params: { id: orderId } 
      });
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('en-GB', {
        style: 'currency',
        currency: 'GBP'
      }).format(amount);
    },
    getPaymentStatusClass(status) {
      switch(status) {
        case 'PAID':
          return 'bg-green-100 text-green-800';
        case 'PENDING':
          return 'bg-yellow-100 text-yellow-800';
        case 'FAILED':
          return 'bg-red-100 text-red-800';
        default:
          return 'bg-gray-100 text-gray-800';
      }
    }
  }
}
</script>