<!-- src/views/CustomerManagement.vue -->
<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden">
    <div class="p-6 bg-gradient-to-r from-blue-500 to-blue-600 text-white flex justify-between items-center">
      <h1 class="text-2xl font-bold">Customer Management</h1>
      <button 
        @click="showAddCustomerModal = true" 
        class="bg-white text-blue-600 px-4 py-2 rounded-md hover:bg-blue-50"
      >
        Add Customer
      </button>
    </div>

    <!-- Search and Filter -->
    <div class="p-6 border-b">
      <div class="flex items-center space-x-4">
        <div class="flex-1">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search customers..." 
            class="w-full p-3 border rounded-lg"
            @input="searchCustomers"
          >
        </div>
        <select 
          v-model="sortBy" 
          class="p-3 border rounded-lg bg-white"
          @change="sortCustomers"
        >
          <option value="name">Sort by Name</option>
          <option value="email">Sort by Email</option>
          <option value="loyaltyPoints">Sort by Loyalty Points</option>
        </select>
      </div>
    </div>

    <!-- Customer List -->
    <div class="p-6">
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
        <p class="mt-4 text-gray-500">Loading customers...</p>
      </div>

      <div v-else-if="error" class="text-center py-12 bg-red-50 text-red-600 rounded-lg border border-red-200 p-4">
        <p class="text-lg font-semibold">{{ error }}</p>
        <button 
          @click="fetchCustomers" 
          class="mt-4 px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          Try Again
        </button>
      </div>

      <div v-else-if="customers.length === 0" class="text-center py-12">
        <div class="text-gray-400 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
        </div>
        <p class="text-lg text-gray-500">No customers found</p>
        <button 
          @click="showAddCustomerModal = true" 
          class="mt-4 bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
        >
          Add Your First Customer
        </button>
      </div>

      <div v-else>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contact</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loyalty Points</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="customer in displayedCustomers" :key="customer.customerId">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                      {{ getInitials(customer.customerName) }}
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{ customer.customerName }}</div>
                      <div class="text-sm text-gray-500">{{ customer.customerEmail }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ customer.contact }}</div>
                  <div class="text-sm text-gray-500">{{ customer.address || 'No address' }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                    {{ customer.loyaltyPoints }} points
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <button 
                    @click="editCustomer(customer)" 
                    class="text-indigo-600 hover:text-indigo-900 mr-3"
                  >
                    Edit
                  </button>
                  <button 
                    @click="confirmDeleteCustomer(customer)" 
                    class="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="flex justify-between items-center mt-4">
          <div class="text-sm text-gray-500">
            Showing {{ pagination.start + 1 }} to {{ Math.min(pagination.end, filteredCustomers.length) }} of {{ filteredCustomers.length }} customers
          </div>
          <div class="flex space-x-2">
            <button 
              @click="prevPage" 
              class="px-3 py-1 border rounded"
              :disabled="pagination.page === 1"
              :class="pagination.page === 1 ? 'text-gray-400' : 'text-blue-600 hover:bg-blue-50'"
            >
              Previous
            </button>
            <button 
              @click="nextPage" 
              class="px-3 py-1 border rounded"
              :disabled="pagination.page === totalPages"
              :class="pagination.page === totalPages ? 'text-gray-400' : 'text-blue-600 hover:bg-blue-50'"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Customer Modal -->
    <div v-if="showAddCustomerModal || showEditCustomerModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg w-full max-w-lg mx-4">
        <div class="p-6 border-b">
          <h2 class="text-xl font-bold">{{ showEditCustomerModal ? 'Edit Customer' : 'Add New Customer' }}</h2>
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
                  required
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                <input 
                  type="tel" 
                  v-model="customerForm.phoneNumber" 
                  class="w-full p-2 border rounded"
                  required
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
              <div v-if="showEditCustomerModal">
                <label class="block text-sm font-medium text-gray-700 mb-1">Loyalty Points</label>
                <input 
                  type="number" 
                  v-model.number="customerForm.loyaltyPoints" 
                  class="w-full p-2 border rounded"
                  min="0"
                >
              </div>
            </div>
            <div class="mt-6 flex justify-end space-x-3">
              <button 
                type="button"
                @click="closeCustomerModal" 
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

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg w-full max-w-md mx-4">
        <div class="p-6">
          <h2 class="text-xl font-bold mb-4">Confirm Delete</h2>
          <p>Are you sure you want to delete customer <strong>{{ customerToDelete?.customerName }}</strong>? This action cannot be undone.</p>
        </div>
        <div class="px-6 py-4 bg-gray-50 flex justify-end space-x-3 rounded-b-lg">
          <button 
            @click="showDeleteModal = false" 
            class="px-4 py-2 border rounded-md hover:bg-gray-100"
          >
            Cancel
          </button>
          <button 
            @click="deleteCustomer" 
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
            :disabled="submitting"
          >
            {{ submitting ? 'Deleting...' : 'Delete' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script>
import { ref, computed, onMounted, watch } from 'vue';
import { customerService } from '../services/api.service';

export default {
  setup() {
    const customers = ref([]);
    const loading = ref(true);
    const error = ref('');
    const searchQuery = ref('');
    const sortBy = ref('name');
    const showAddCustomerModal = ref(false);
    const showEditCustomerModal = ref(false);
    const showDeleteModal = ref(false);
    const submitting = ref(false);
    const customerToDelete = ref(null);

    const customerForm = ref({
      customerId: null,
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      address: '',
      loyaltyPoints: 0
    });

    const pagination = ref({
      page: 1,
      perPage: 5,
      start: 0,
      end: 5
    });

    const getInitials = (name) => {
      if (!name) return '';
      return name
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .substring(0, 2);
    };

    const fetchCustomers = async () => {
      loading.value = true;
      error.value = '';
      try {
        const response = await customerService.getAllCustomers();
        customers.value = response.data || [];
      } catch (err) {
        console.error('Error fetching customers:', err);
        error.value = 'Failed to load customers. Please try again.';
      } finally {
        loading.value = false;
      }
    };

    const filteredCustomers = computed(() => {
      if (!searchQuery.value) return customers.value;
      const query = searchQuery.value.toLowerCase();
      return customers.value.filter(customer => 
        customer.customerName.toLowerCase().includes(query) ||
        customer.customerEmail.toLowerCase().includes(query) ||
        (customer.contact && customer.contact.includes(query))
      );
    });

    const sortCustomers = () => {
      if (sortBy.value === 'name') {
        customers.value.sort((a, b) => a.customerName.localeCompare(b.customerName));
      } else if (sortBy.value === 'email') {
        customers.value.sort((a, b) => a.customerEmail.localeCompare(b.customerEmail));
      } else if (sortBy.value === 'loyaltyPoints') {
        customers.value.sort((a, b) => b.loyaltyPoints - a.loyaltyPoints);
      }
    };

    const totalPages = computed(() => {
      return Math.ceil(filteredCustomers.value.length / pagination.value.perPage);
    });

    const displayedCustomers = computed(() => {
      return filteredCustomers.value.slice(pagination.value.start, pagination.value.end);
    });

    const updatePagination = () => {
      pagination.value.start = (pagination.value.page - 1) * pagination.value.perPage;
      pagination.value.end = pagination.value.start + pagination.value.perPage;
    };

    const nextPage = () => {
      if (pagination.value.page < totalPages.value) {
        pagination.value.page++;
        updatePagination();
      }
    };

    const prevPage = () => {
      if (pagination.value.page > 1) {
        pagination.value.page--;
        updatePagination();
      }
    };

    watch(searchQuery, () => {
      pagination.value.page = 1;
      updatePagination();
    });

    const searchCustomers = () => {
      pagination.value.page = 1;
      updatePagination();
    };

    const editCustomer = (customer) => {
      const nameParts = customer.customerName.split(' ');
      const firstName = nameParts[0];
      const lastName = nameParts.slice(1).join(' ');
      
      customerForm.value = {
        customerId: customer.customerId,
        firstName,
        lastName,
        email: customer.customerEmail,
        phoneNumber: customer.contact,
        address: customer.address || '',
        loyaltyPoints: customer.loyaltyPoints
      };
      showEditCustomerModal.value = true;
    };

    const confirmDeleteCustomer = (customer) => {
      customerToDelete.value = customer;
      showDeleteModal.value = true;
    };

    const deleteCustomer = async () => {
      if (!customerToDelete.value) return;
      
      submitting.value = true;
      try {
        await customerService.deleteCustomer(customerToDelete.value.customerId);
        customers.value = customers.value.filter(
          c => c.customerId !== customerToDelete.value.customerId
        );
        showDeleteModal.value = false;
        customerToDelete.value = null;
      } catch (error) {
        console.error('Error deleting customer:', error);
        alert('Failed to delete customer. Please try again.');
      } finally {
        submitting.value = false;
      }
    };

    const submitCustomerForm = async () => {
      submitting.value = true;
      try {
        const customerData = {
          firstName: customerForm.value.firstName,
          lastName: customerForm.value.lastName,
          email: customerForm.value.email,
          phoneNumber: customerForm.value.phoneNumber,
          address: customerForm.value.address || '',
          loyaltyPoints: customerForm.value.loyaltyPoints
        };

        if (showEditCustomerModal.value) {
          const response = await customerService.updateCustomer(
            customerForm.value.customerId,
            customerData
          );
          const index = customers.value.findIndex(
            c => c.customerId === customerForm.value.customerId
          );
          if (index !== -1) {
            customers.value[index] = response.data;
          }
        } else {
          const response = await customerService.createCustomer(customerData);
          customers.value.push(response.data);
        }

        closeCustomerModal();
      } catch (error) {
        console.error('Full error details:', error);
        if (error.response) {
          const errorMessage = error.response.data?.message ||
            error.response.data ||
            'Server returned an error while saving the customer.';
          alert(errorMessage);
        } else if (error.request) {
          alert('No response received from the server. Please check your network connection.');
        } else {
          alert('An unexpected error occurred while saving the customer.');
        }
      } finally {
        submitting.value = false;
      }
    };

    const closeCustomerModal = () => {
      showAddCustomerModal.value = false;
      showEditCustomerModal.value = false;
      customerForm.value = {
        customerId: null,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        address: '',
        loyaltyPoints: 0
      };
    };

    onMounted(() => {
      fetchCustomers();
    });

    return {
      customers,
      loading,
      error,
      searchQuery,
      sortBy,
      showAddCustomerModal,
      showEditCustomerModal,
      showDeleteModal,
      submitting,
      customerToDelete,
      customerForm,
      pagination,
      filteredCustomers,
      displayedCustomers,
      totalPages,
      fetchCustomers,
      searchCustomers,
      sortCustomers,
      editCustomer,
      confirmDeleteCustomer,
      deleteCustomer,
      submitCustomerForm,
      closeCustomerModal,
      nextPage,
      prevPage,
      getInitials
    };
  }
};
</script>
