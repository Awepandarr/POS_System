<!-- src/components/CustomerSelector.vue -->
<template>
    <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
      <div class="p-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white">
        <h3 class="text-lg font-semibold flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          Customer Selection
        </h3>
      </div>
      
      <div class="p-4">
        <!-- Search Input -->
        <div class="relative mb-4">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search customers..." 
            class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            @input="searchCustomers"
          >
          <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>
  
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
        </div>
  
        <!-- Customer List -->
        <div v-else-if="customers.length > 0" class="max-h-64 overflow-y-auto">
          <div 
            v-for="customer in customers" 
            :key="customer.customerId"
            class="flex items-center p-3 mb-2 rounded-lg border border-gray-100 cursor-pointer transition-colors"
            :class="selectedCustomer && selectedCustomer.customerId === customer.customerId ? 'bg-blue-50 border-blue-200' : 'hover:bg-gray-50'"
            @click="selectCustomer(customer)"
          >
            <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold mr-3">
              {{ getInitials(customer.customerName || 'Unknown Customer') }}
            </div>
            <div class="flex-1">
              <div class="font-medium">{{ customer.customerName || 'Unknown Customer' }}</div>
              <div class="text-sm text-gray-500">{{ customer.customerEmail || 'No email provided' }}</div>
            </div>
            <div v-if="selectedCustomer && selectedCustomer.customerId === customer.customerId" class="text-blue-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
        </div>
        
        <!-- No Results -->
        <div v-else-if="searchQuery" class="text-center py-8">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 mx-auto text-gray-300 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <p class="text-gray-500">No customers found matching "{{ searchQuery }}"</p>
        </div>
        
        <!-- No Customers -->
        <div v-else class="text-center py-8">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
          <p class="text-gray-500 mb-4">No customers available</p>
        </div>
  
        <!-- Create New Customer Button and Modal -->
        <div class="mt-4 flex justify-between">
          <button
            v-if="selectedCustomer"
            @click="clearSelection"
            class="px-3 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
          >
            Clear Selection
          </button>
          <button
            @click="showAddCustomerModal = true"
            class="ml-auto px-3 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition flex items-center gap-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            New Customer
          </button>
        </div>
      </div>
  
      <!-- Selected Customer Card -->
      <div v-if="selectedCustomer" class="bg-blue-50 p-4 border-t border-blue-200">
        <div class="flex justify-between items-center">
          <div>
            <h4 class="font-semibold text-blue-700">Selected Customer</h4>
            <div class="mt-1">{{ selectedCustomer.customerName }}</div>
            <div class="text-sm text-gray-600">{{ selectedCustomer.customerEmail || 'No email' }} • {{ selectedCustomer.contact || 'No phone' }}</div>
          </div>
          <div class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">
            Customer #{{ selectedCustomer.customerId }}
          </div>
        </div>
      </div>
  
      <!-- Add Customer Modal -->
      <div v-if="showAddCustomerModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg w-full max-w-md mx-4">
          <div class="p-6 border-b">
            <h2 class="text-xl font-bold">Add New Customer</h2>
          </div>
          <div class="p-6">
            <form @submit.prevent="submitCustomerForm">
              <div class="space-y-4">
                <!-- First Name and Last Name -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                    <input 
                      type="text" 
                      v-model="customerForm.firstName" 
                      class="w-full p-2 border rounded"
                      required
                    >
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                    <input 
                      type="text" 
                      v-model="customerForm.lastName" 
                      class="w-full p-2 border rounded"
                      required
                    >
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input 
                    type="email" 
                    v-model="customerForm.email" 
                    class="w-full p-2 border rounded"
                  >
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                  <input 
                    type="tel" 
                    v-model="customerForm.phoneNumber" 
                    class="w-full p-2 border rounded"
                  >
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                  <textarea 
                    v-model="customerForm.address" 
                    rows="2"
                    class="w-full p-2 border rounded"
                  ></textarea>
                </div>
              </div>
              <div class="mt-6 flex justify-end space-x-3">
                <button 
                  type="button"
                  @click="showAddCustomerModal = false" 
                  class="px-4 py-2 border rounded-md hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button 
                  type="submit"
                  class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                  :disabled="submitting"
                >
                  {{ submitting ? 'Saving...' : 'Save Customer' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, watch, onMounted } from 'vue';
  import { customerService } from '../services/api.service';
  
  export default {
    props: {
      initialCustomerId: {
        type: Number,
        default: null
      }
    },
    
    emits: ['select-customer'],
    
    setup(props, { emit }) {
      const customers = ref([]);
      const loading = ref(true);
      const searchQuery = ref('');
      const selectedCustomer = ref(null);
      const submitting = ref(false);
      const showAddCustomerModal = ref(false);
      
      const customerForm = ref({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        address: ''
      });
      
      // Fetch initial customers
      const fetchCustomers = async () => {
        loading.value = true;
        try {
          const response = await customerService.getAllCustomers();
          customers.value = response.data || [];
          
          // If initialCustomerId is provided, select that customer
          if (props.initialCustomerId) {
            const customer = customers.value.find(c => c.customerId === props.initialCustomerId);
            if (customer) {
              selectedCustomer.value = customer;
            }
          }
        } catch (err) {
          console.error('Error fetching customers:', err);
          // For testing, provide some dummy data if API fails
          customers.value = [
            { customerId: 1, customerName: 'John Smith', customerEmail: 'john@example.com', contact: '555-1234' },
            { customerId: 2, customerName: 'Sarah Johnson', customerEmail: 'sarah@example.com', contact: '555-5678' },
            { customerId: 3, customerName: 'David Brown', customerEmail: 'david@example.com', contact: '555-9012' }
          ];
        } finally {
          loading.value = false;
        }
      };
      
      // Search customers
      const searchCustomers = () => {
        if (!searchQuery.value.trim()) {
          fetchCustomers();
          return;
        }
        
        loading.value = true;
        
        // In a real implementation, this would be an API call
        // For now, we'll filter the existing customers
        setTimeout(() => {
          const query = searchQuery.value.toLowerCase();
          const filteredCustomers = customers.value.filter(customer => 
            customer.customerName?.toLowerCase().includes(query) ||
            customer.customerEmail?.toLowerCase().includes(query) ||
            customer.contact?.includes(query)
          );
          
          customers.value = filteredCustomers;
          loading.value = false;
        }, 500);
      };
      
      // Get customer initials for the avatar
      const getInitials = (name) => {
        return name
          .split(' ')
          .map(n => n[0])
          .join('')
          .toUpperCase()
          .substring(0, 2);
      };
      
      // Select a customer
      const selectCustomer = (customer) => {
        console.log('Selecting customer:', customer);
        selectedCustomer.value = customer;
        emit('select-customer', customer);
      };
      
      // Clear the selected customer
      const clearSelection = () => {
        selectedCustomer.value = null;
        emit('select-customer', null);
      };
      
      // Submit the customer form
      const submitCustomerForm = async () => {
        submitting.value = true;
        try {
          const customerData = {
            firstName: customerForm.value.firstName,
            lastName: customerForm.value.lastName,
            email: customerForm.value.email,
            phoneNumber: customerForm.value.phoneNumber,
            address: customerForm.value.address || ''
          };

          // Adding the customer name from first and last name
          customerData.customerName = `${customerData.firstName} ${customerData.lastName}`;
          customerData.customerEmail = customerData.email;
          customerData.contact = customerData.phoneNumber;

          const response = await customerService.createCustomer(customerData);
          customers.value.push(response.data);
          
          // Select the newly created customer
          selectCustomer(response.data);
          
          // Close the modal and reset the form
          showAddCustomerModal.value = false;
          customerForm.value = {
            firstName: '',
            lastName: '',
            email: '',
            phoneNumber: '',
            address: ''
          };
        } catch (err) {
          console.error('Error creating customer:', err);
          // You could add error handling UI here
        } finally {
          submitting.value = false;
        }
      };
      
      // Watch for changes in searchQuery
      watch(searchQuery, (newQuery, oldQuery) => {
        if (!newQuery && oldQuery) {
          fetchCustomers();
        }
      });
      
      // Fetch customers on mount
      onMounted(() => {
        fetchCustomers();
      });
      
      return {
        customers,
        loading,
        searchQuery,
        selectedCustomer,
        showAddCustomerModal,
        customerForm,
        submitting,
        getInitials,
        searchCustomers,
        selectCustomer,
        clearSelection,
        submitCustomerForm
      };
    }
  };
  </script>