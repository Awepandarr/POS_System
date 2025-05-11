<template>
  <div class="bg-white shadow-lg rounded-lg p-6 mb-4">
    <h3 class="text-xl font-bold mb-4">Product List</h3>
    <div class="grid grid-cols-2 gap-4">
      <div v-for="product in products" :key="product.productId" class="border p-4 rounded-md">
        <!-- Product Image -->
        <div class="mb-3">
          <img 
            v-if="product.image_url" 
            :src="product.image_url" 
            :alt="product.name"
            class="w-full h-48 object-cover rounded-md"
          />
          <div 
            v-else 
            class="w-full h-48 bg-gray-200 flex items-center justify-center rounded-md"
          >
            <span class="text-gray-500">No image available</span>
          </div>
        </div>
        
        <h4 class="text-lg font-semibold">{{ product.name }}</h4>
        <p>Price: ${{ product.price }}</p>
        <p>Stock: {{ product.stockQuantity }}</p>
        <button 
          @click="addToOrder(product)" 
          class="mt-4 bg-blue-500 text-white p-2 rounded-md"
        >
          Add to Order
        </button>
      </div>
    </div>
    
    <!-- Cart Section -->
    <div class="mt-6 bg-gray-100 p-4 rounded-md">
      <h4 class="text-lg font-semibold">Your Cart</h4>
      <div v-if="cart.length === 0">Your cart is empty.</div>
      <ul v-else class="divide-y">
        <li v-for="(item, index) in cart" :key="index" class="py-2 flex items-center">
          <img 
            v-if="item.image_url" 
            :src="item.image_url" 
            :alt="item.name"
            class="w-12 h-12 object-cover rounded-md mr-3"
          />
          <div class="flex-grow">
            {{ item.name }} - ${{ item.price }}
          </div>
          <button @click="removeFromCart(index)" class="ml-2 text-red-500">Remove</button>
        </li>
      </ul>
      <div v-if="cart.length > 0" class="mt-4 text-right">
        <p class="font-bold">Total: ${{ cartTotal.toFixed(2) }}</p>
      </div>
      <button 
        @click="proceedToOrder"
        class="mt-4 bg-blue-500 text-white p-2 rounded-md w-full"
        :disabled="cart.length === 0"
      >
        Place Order
      </button>
    </div>
  </div>
</template>

<script>
import { productService } from '@/services/api.service';

export default {
  data() {
    return {
      products: [],
      cart: JSON.parse(localStorage.getItem('cart')) || [],
    };
  },
  computed: {
    cartTotal() {
      return this.cart.reduce((sum, item) => sum + parseFloat(item.price), 0);
    }
  },
  methods: {
    async fetchProducts() {
      try {
        const response = await productService.getAllProducts();
        this.products = response.data;
        
        // Ensure all products have image_url property
        this.products = this.products.map(product => {
          if (!product.image_url) {
            return {
              ...product,
              image_url: null // Set default to null if not present
            };
          }
          return product;
        });
      } catch (error) {
        console.error("Error fetching products:", error);
        alert("There was an error fetching products. Please try again later.");
      }
    },
    addToOrder(product) {
      this.cart.push(product);
      localStorage.setItem('cart', JSON.stringify(this.cart));
    },
    removeFromCart(index) {
      this.cart.splice(index, 1);
      localStorage.setItem('cart', JSON.stringify(this.cart));
    },
    async proceedToOrder() {
      const orderData = {
        customerId: 1,  // Replace with actual logged-in user ID
        totalAmount: this.cartTotal,
        orderType: "ONLINE",
        finalAmount: this.cartTotal,
        products: this.cart.map(item => ({
          productId: item.productId,
          quantity: 1, // Default quantity
          price: parseFloat(item.price)
        })),
      };

      try {
        const response = await axios.post('http://localhost:8080/api/order', orderData);
        console.log("Order created successfully:", response);  // Log the response
        const order = response.data;

        // Store order in local storage
        localStorage.setItem('order', JSON.stringify(order));

        // Navigate to order confirmation page
        this.$router.push({ name: "ConfirmationPage" })
          .catch(err => {
            console.error("Navigation error:", err);
          });
      } catch (error) {
        console.error("Error creating order:", error.response || error);
        alert("There was an error creating your order. Please try again later.");
      }
    },
  },
  created() {
    this.fetchProducts();
  },
};
</script>