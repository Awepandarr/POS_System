<template>
  <div class="container mx-auto px-4 py-8">
    <div class="bg-white shadow-xl rounded-lg overflow-hidden">
      <!-- Header -->
      <div class="px-6 py-4 bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-between">
        <h1 class="text-2xl font-bold text-white flex items-center">
          <router-link 
            :to="{ name: 'orders' }" 
            class="mr-2 hover:text-white/80 transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </router-link>
          Order Details 
          <span v-if="order" class="text-sm ml-2">(Order #{{ order.orderId }})</span>
        </h1>
        <div class="flex items-center space-x-4">
          <!-- Refresh Button -->
          <button 
            @click="fetchOrderDetails" 
            class="bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-md transition-colors flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh
          </button>
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
      
      <!-- Order Details -->
      <div v-else-if="order" class="p-6">
        <!-- Order Information Card -->
        <div class="bg-gray-50 p-6 rounded-lg shadow-sm mb-8">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <h3 class="text-lg font-semibold mb-2">Order Information</h3>
              <p class="text-sm text-gray-600"><strong>Order ID:</strong> #{{ order.orderId }}</p>
              <p class="text-sm text-gray-600"><strong>Customer ID:</strong> {{ order.customerId }}</p>
              <p class="text-sm text-gray-600"><strong>Order Type:</strong> {{ order.orderType }}</p>
              <p class="text-sm text-gray-600" v-if="order.deliveryType"><strong>Delivery Type:</strong> {{ order.deliveryType }}</p>
            </div>
            
            <div>
              <h3 class="text-lg font-semibold mb-2">Payment Details</h3>
              <p class="text-sm text-gray-600"><strong>Payment Status:</strong> 
                <span 
                  :class="getPaymentStatusClass(order.paymentStatus)"
                  class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full"
                >
                  {{ order.paymentStatus }}
                </span>
              </p>
              <p class="text-sm text-gray-600"><strong>Payment Method:</strong> {{ paymentMethod || 'Unknown' }}</p>
              <p class="text-sm text-gray-600"><strong>Transaction ID:</strong> {{ transactionId || 'Not Available' }}</p>
              <p class="text-sm text-gray-600"><strong>Order Status:</strong> {{ order.orderStatus || 'Processing' }}</p>
            </div>
            
            <div>
              <h3 class="text-lg font-semibold mb-2">Amount Summary</h3>
              <p class="text-sm text-gray-600"><strong>Subtotal:</strong> {{ formatCurrency(order.totalAmount) }}</p>
              <p class="text-sm text-gray-600"><strong>Tax:</strong> {{ formatCurrency(order.taxAmount) }}</p>
              <p class="text-sm text-gray-600" v-if="order.discountAmount && order.discountAmount > 0">
                <strong>Discount:</strong> {{ formatCurrency(order.discountAmount) }}
              </p>
              <p class="text-lg font-semibold text-gray-800 mt-2">
                <strong>Total:</strong> {{ formatCurrency(order.finalAmount) }}
              </p>
            </div>
          </div>
        </div>
        
        <!-- Order Items Table -->
        <h3 class="text-xl font-bold mb-4">Order Items</h3>
        <div v-if="orderItems.length" class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50 border-b">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product ID</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product Name</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Unit Price</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Subtotal</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr 
                v-for="item in orderItems" 
                :key="item.orderItemId" 
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ item.productId }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {{ item.productName || 'Unknown Product' }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-center text-gray-500">
                  {{ item.quantity }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-500">
                  {{ formatCurrency(item.price) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-right font-semibold text-gray-900">
                  {{ formatCurrency(item.subtotal) }}
                </td>
              </tr>
            </tbody>
            <!-- Table Footer with Totals -->
            <tfoot class="bg-gray-50">
              <tr>
                <td colspan="4" class="px-6 py-4 text-right text-sm font-medium text-gray-500">Subtotal:</td>
                <td class="px-6 py-4 text-right text-sm font-medium text-gray-900">{{ formatCurrency(order.totalAmount) }}</td>
              </tr>
              <tr>
                <td colspan="4" class="px-6 py-4 text-right text-sm font-medium text-gray-500">Tax:</td>
                <td class="px-6 py-4 text-right text-sm font-medium text-gray-900">{{ formatCurrency(order.taxAmount) }}</td>
              </tr>
              <tr v-if="order.discountAmount && order.discountAmount > 0">
                <td colspan="4" class="px-6 py-4 text-right text-sm font-medium text-gray-500">Discount:</td>
                <td class="px-6 py-4 text-right text-sm font-medium text-green-600">-{{ formatCurrency(order.discountAmount) }}</td>
              </tr>
              <tr class="bg-gray-100">
                <td colspan="4" class="px-6 py-4 text-right text-base font-bold text-gray-700">Total:</td>
                <td class="px-6 py-4 text-right text-base font-bold text-gray-900">{{ formatCurrency(order.finalAmount) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
        
        <!-- No Order Items State -->
        <div v-else class="text-center py-12 bg-gray-50 rounded-lg">
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
          <p class="text-xl text-gray-500">No order items found</p>
          <p class="text-gray-400 mt-2">There are no items for this order</p>
        </div>
        
        <!-- Action Buttons -->
        <div class="mt-8 flex flex-wrap gap-4">
          <button 
            @click="generateInvoice" 
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Generate Invoice
          </button>
          
          <button 
            @click="printOrderDetails" 
            class="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 transition-colors flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2z" />
            </svg>
            Print
          </button>
          
          <router-link 
            :to="{ name: 'orders' }" 
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Back to Orders
          </router-link>
        </div>
      </div>
      
      <!-- No Order Found State -->
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
        <p class="text-xl text-gray-500">Order not found</p>
        <p class="text-gray-400 mt-2">The requested order does not exist</p>
        
        <router-link 
          :to="{ name: 'orders' }" 
          class="mt-6 inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
        >
          View All Orders
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
// Import jsPDF library
import { jsPDF } from 'jspdf';
// Optional: If you want to add auto-tables for better PDF layout
// import 'jspdf-autotable';

export default {
  name: 'OrderDetails',
  data() {
    return {
      order: null,
      orderItems: [],
      loading: true,
      error: null,
      paymentMethod: null,
      transactionId: null
    };
  },
  created() {
    this.fetchOrderDetails();
  },
  methods: {
    async fetchOrderDetails() {
      try {
        this.loading = true;
        const orderId = this.$route.params.id;
        
        if (!orderId) {
          this.error = 'Invalid order ID';
          this.loading = false;
          return;
        }
        
        // Fetch order details
        const orderResponse = await axios.get(`http://localhost:8080/api/order/${orderId}`);
        this.order = orderResponse.data;
        
        // Check if order items are included in the response
        if (this.order.orderItems) {
          this.orderItems = this.order.orderItems;
        } else {
          // If not, fetch order items separately
          try {
            const itemsResponse = await axios.get(`http://localhost:8080/api/order/${orderId}/items`);
            this.orderItems = Array.isArray(itemsResponse.data) ? itemsResponse.data : [];
          } catch (itemsError) {
            console.error('Error fetching order items:', itemsError);
            // Don't fail if just the items can't be fetched
            this.orderItems = [];
          }
        }
        
        // Fetch payment details with method and transaction ID
        try {
          const paymentResponse = await axios.get(`http://localhost:8080/api/payment/details/${orderId}`);
          if (paymentResponse.data) {
            // Get the payment method from the response
            if (paymentResponse.data.paymentMethod) {
              this.paymentMethod = this.formatPaymentMethod(paymentResponse.data.paymentMethod);
            } else {
              this.paymentMethod = 'Unknown';
            }
            
            // Store the transaction ID
            this.transactionId = paymentResponse.data.transactionId || 'Not Available';
          } else {
            this.paymentMethod = 'Unknown';
            this.transactionId = 'Not Available';
          }
        } catch (paymentError) {
          console.error('Error fetching payment details:', paymentError);
          // Don't fail if payment details can't be fetched
          this.paymentMethod = 'Unknown';
          this.transactionId = 'Not Available';
        }
        
        this.loading = false;
      } catch (err) {
        console.error('Error fetching order details:', err);
        this.error = err.response?.data?.error || 'Failed to load order details. Please try again later.';
        this.loading = false;
      }
    },
    formatCurrency(amount, forPDF = false) {
      if (amount === undefined || amount === null) return '$0.00';
      const formatted = new Intl.NumberFormat('en-GB', {
        style: 'currency',
        currency: 'GBP'
      }).format(amount);
      return forPDF ? formatted.replace('$', '') : formatted;
    },
    formatPaymentMethod(method) {
      if (!method) return 'Unknown';
      
      // Format payment method for display
      switch(method.toUpperCase()) {
        case 'CARD':
          return 'Credit/Debit Card';
        case 'CASH':
          return 'Cash';
        default:
          // If it's already formatted nicely, just return it
          return method.charAt(0).toUpperCase() + method.slice(1).toLowerCase();
      }
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
    },
    generateInvoice() {
      try {
        if (!this.order) return;
        
        // Create a new jsPDF instance
        const doc = new jsPDF();
        
        // Set font
        doc.setFont('helvetica');
        
        // Add logo or header
        doc.setFontSize(22);
        doc.setTextColor(40);
        doc.text('INVOICE', 105, 20, { align: 'center' });
        
        // Company info
        doc.setFontSize(12);
        doc.text('Your Company Name', 105, 30, { align: 'center' });
        doc.text('123 Business St, City, Country', 105, 36, { align: 'center' });
        doc.text('Phone: (123) 456-7890 | Email: info@company.com', 105, 42, { align: 'center' });
        
        // Divider line
        doc.setDrawColor(200);
        doc.line(20, 50, 190, 50);
        
        // Invoice info
        doc.text(`Invoice #: ${this.order.orderId}`, 20, 60);
        doc.text(`Date: ${new Date().toLocaleDateString()}`, 20, 66);
        doc.text(`Customer ID: ${this.order.customerId}`, 20, 72);
        doc.text(`Payment Method: ${this.paymentMethod || 'Not specified'}`, 20, 78);
        doc.text(`Transaction ID: ${this.transactionId || 'Not Available'}`, 20, 84);
        
        // Order items table header
        doc.setFontSize(14);
        doc.text('Description', 20, 100); // Adjusted position to make room for transaction ID
        doc.text('Qty', 100, 100);
        doc.text('Price', 130, 100);
        doc.text('Amount', 170, 100, { align: 'right' });
        
        // Order items
        doc.setFontSize(12);
        let y = 110; // Adjusted starting position for items
        
        this.orderItems.forEach(item => {
          // Check if we need a new page
          if (y > 250) {
            doc.addPage();
            y = 20;
            // Re-add headers if needed
          }
          
          doc.text(item.productName?.substring(0, 30) || 'Unknown Product', 20, y); // Limit product name length
          doc.text(item.quantity.toString(), 100, y);
          doc.text(this.formatCurrency(item.price, true), 130, y);
          doc.text(this.formatCurrency(item.subtotal, true), 170, y, { align: 'right' });
          y += 10;
        });
        
        // Totals
        y += 10;
        doc.text(`Subtotal: ${this.formatCurrency(this.order.totalAmount, true)}`, 170, y, { align: 'right' });
        y += 10;
        doc.text(`Tax: ${this.formatCurrency(this.order.taxAmount, true)}`, 170, y, { align: 'right' });
        if (this.order.discountAmount > 0) {
          y += 10;
          doc.text(`Discount: -${this.formatCurrency(this.order.discountAmount, true)}`, 170, y, { align: 'right' });
        }
        y += 10;
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text(`Total: ${this.formatCurrency(this.order.finalAmount, true)}`, 170, y, { align: 'right' });
        
        // Footer
        doc.setFontSize(10);
        doc.setTextColor(150);
        doc.text('Thank you for your business!', 105, 280, { align: 'center' });
        
        // Save the PDF
        doc.save(`invoice_${this.order.orderId}.pdf`);
      } catch (error) {
        console.error('Error generating PDF:', error);
        alert('Failed to generate invoice. Please try again.');
      }
    },
    printOrderDetails() {
      window.print();
    }
  }
}
</script>

<style>
@media print {
  /* Hide navigation elements when printing */
  button, a {
    display: none !important;
  }
  
  /* Ensure full width for the printable content */
  .container {
    width: 100% !important;
    max-width: 100% !important;
  }
  
  /* Remove shadows and backgrounds for better printing */
  .shadow-xl, .bg-gradient-to-r {
    box-shadow: none !important;
    background: white !important;
    color: black !important;
  }
  
  /* Add border to tables for better visual separation when printed */
  table, th, td {
    border-collapse: collapse;
    border: 1px solid #ddd;
  }
}
</style>