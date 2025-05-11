<template>
    <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-lg w-full max-w-xl p-6 mx-4">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold">Invoice Details</h3>
          <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
  
        <div v-if="loading" class="py-12 text-center">
          <svg class="animate-spin h-8 w-8 mx-auto text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="mt-4 text-gray-600">Loading invoice details...</p>
        </div>
  
        <div v-else>
          <!-- Invoice Header -->
          <div class="border-b border-gray-200 pb-4 mb-4">
            <div class="flex justify-between items-start">
              <div>
                <h4 class="text-lg font-bold">Invoice #{{ invoice.invoiceNumber || invoice.invoiceId }}</h4>
                <p class="text-gray-600">{{ formatDate(invoice.date) }}</p>
              </div>
              <div class="px-4 py-2 bg-blue-100 text-blue-800 rounded-lg">
                <span class="font-medium">PAID</span>
              </div>
            </div>
          </div>
  
          <!-- Business & Customer Info -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <h5 class="font-semibold text-gray-600 mb-2">From</h5>
              <p class="font-medium">Retail POS System</p>
              <p class="text-sm text-gray-600">123 Main Street</p>
              <p class="text-sm text-gray-600">Manchester, M1 1AB</p>
              <p class="text-sm text-gray-600">VAT: GB123456789</p>
            </div>
            <div>
              <h5 class="font-semibold text-gray-600 mb-2">To</h5>
              <p class="font-medium">{{ invoice.customer?.name || 'Customer' }}</p>
              <p class="text-sm text-gray-600">{{ invoice.customer?.email || 'Not provided' }}</p>
              <p class="text-sm text-gray-600">{{ invoice.customer?.phone || 'Not provided' }}</p>
            </div>
          </div>
  
          <!-- Invoice Items -->
          <div class="mb-6">
            <h5 class="font-semibold text-gray-600 mb-3">Items</h5>
            <div class="bg-gray-50 rounded-lg p-4">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-gray-200">
                    <th class="text-left pb-2">Item</th>
                    <th class="text-center pb-2">Qty</th>
                    <th class="text-right pb-2">Price</th>
                    <th class="text-right pb-2">Total</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in invoice.items" :key="index" class="border-b border-gray-200 last:border-0">
                    <td class="py-2">{{ item.name }}</td>
                    <td class="py-2 text-center">{{ item.quantity }}</td>
                    <td class="py-2 text-right">£{{ item.price.toFixed(2) }}</td>
                    <td class="py-2 text-right">£{{ (item.price * item.quantity).toFixed(2) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
  
          <!-- Invoice Totals -->
          <div class="border-t border-gray-200 pt-4 mb-6">
            <div class="flex justify-between mb-2">
              <span class="text-gray-600">Subtotal</span>
              <span>£{{ calculateSubtotal().toFixed(2) }}</span>
            </div>
            <div class="flex justify-between mb-2">
              <span class="text-gray-600">Tax ({{ taxRate * 100 }}%)</span>
              <span>£{{ calculateTax().toFixed(2) }}</span>
            </div>
            <div class="flex justify-between text-lg font-bold">
              <span>Total</span>
              <span>£{{ invoice.total.toFixed(2) }}</span>
            </div>
          </div>
  
          <!-- Invoice Actions -->
          <div class="flex gap-4 mt-8">
            <button 
              @click="printInvoice" 
              class="flex-1 py-2 flex items-center justify-center bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
              </svg>
              Print
            </button>
            <button 
              @click="downloadInvoice" 
              class="flex-1 py-2 flex items-center justify-center bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              Download PDF
            </button>
            <button 
              @click="showEmailInput = true" 
              class="flex-1 py-2 flex items-center justify-center bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              Email
            </button>
          </div>
  
          <!-- Email Input Form -->
          <div v-if="showEmailInput" class="mt-4 border-t border-gray-200 pt-4">
            <h5 class="font-semibold mb-2">Email Invoice</h5>
            <div class="flex">
              <input 
                v-model="emailAddress" 
                type="email" 
                placeholder="customer@example.com" 
                class="flex-1 border border-gray-300 rounded-l-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <button 
                @click="sendEmail" 
                :disabled="emailSending || !isValidEmail"
                class="bg-blue-500 text-white py-2 px-4 rounded-r-lg hover:bg-blue-600 transition disabled:bg-gray-400"
              >
                <span v-if="emailSending">Sending...</span>
                <span v-else>Send</span>
              </button>
            </div>
            <p v-if="emailError" class="text-red-500 text-sm mt-1">{{ emailError }}</p>
            <p v-if="emailSuccess" class="text-green-500 text-sm mt-1">{{ emailSuccess }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed } from 'vue';
  import { invoiceService } from '@/services/invoice.service';
  
  export default {
    name: 'InvoiceModal',
    props: {
      show: {
        type: Boolean,
        default: false
      },
      invoice: {
        type: Object,
        required: true
      },
      loading: {
        type: Boolean,
        default: false
      }
    },
    emits: ['close', 'download'],
    setup(props, { emit }) {
      const taxRate = 0.20; // 20% tax rate
      const showEmailInput = ref(false);
      const emailAddress = ref('');
      const emailSending = ref(false);
      const emailError = ref('');
      const emailSuccess = ref('');
  
      const isValidEmail = computed(() => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(emailAddress.value);
      });
  
      const formatDate = (dateString) => {
        if (!dateString) return '';
        
        const options = { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        };
        
        return new Date(dateString).toLocaleDateString('en-GB', options);
      };
  
      const calculateSubtotal = () => {
        if (!props.invoice.items || props.invoice.items.length === 0) {
          return props.invoice.total ? props.invoice.total / 1.2 : 0; // Estimate subtotal from total
        }
        
        return props.invoice.items.reduce((sum, item) => {
          return sum + (item.price * item.quantity);
        }, 0);
      };
  
      const calculateTax = () => {
        const subtotal = calculateSubtotal();
        return subtotal * taxRate;
      };
  
      const printInvoice = () => {
        window.print();
      };
  
      const downloadInvoice = () => {
        emit('download');
      };
  
      const sendEmail = async () => {
        if (!isValidEmail.value) {
          emailError.value = 'Please enter a valid email address';
          return;
        }
  
        emailSending.value = true;
        emailError.value = '';
        emailSuccess.value = '';
  
        try {
          await invoiceService.emailInvoice(props.invoice.invoiceId, emailAddress.value);
          emailSuccess.value = `Invoice sent to ${emailAddress.value}`;
          emailAddress.value = '';
          setTimeout(() => {
            showEmailInput.value = false;
            emailSuccess.value = '';
          }, 3000);
        } catch (error) {
          console.error('Error sending invoice email:', error);
          emailError.value = 'Failed to send invoice. Please try again.';
        } finally {
          emailSending.value = false;
        }
      };
  
      return {
        taxRate,
        showEmailInput,
        emailAddress,
        emailSending,
        emailError,
        emailSuccess,
        isValidEmail,
        formatDate,
        calculateSubtotal,
        calculateTax,
        printInvoice,
        downloadInvoice,
        sendEmail
      };
    }
  };
  </script>
  
  <style scoped>
  @media print {
    button, .fixed {
      display: none !important;
    }
    
    .bg-white {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      width: 100%;
      box-shadow: none !important;
    }
  }
  </style>