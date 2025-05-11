// src/services/api.service.js

import axios from 'axios';
// Create an Axios instance with default config
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  },
  // Add timeout to handle network issues
  timeout: 10000
});

// Request interceptor to log requests
apiClient.interceptors.request.use(config => {
  console.log('API Request:', {
    method: config.method,
    url: config.url,
    data: config.data
  });
  return config;
}, error => {
  console.error('Request Error:', error);
  return Promise.reject(error);
});

// Response interceptor to log responses and handle errors
apiClient.interceptors.response.use(
  response => {
    console.log('API Response:', {
      status: response.status,
      data: response.data
    });
    return response;
  },
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);


// Customer service
export const customerService = {
  getAllCustomers() {
    return apiClient.get('/api/customers');
  },
  
  getCustomerByID(id) {
    return apiClient.get(`/api/customer/${id}`);
  },
  
  createCustomer(customerData) {
    // Ensure all fields are strings, even if undefined
    const formattedData = {
      firstName: customerData.firstName || '',
      lastName: customerData.lastName || '',
      email: customerData.email || '',
      phoneNumber: customerData.phoneNumber || '',
      address: customerData.address || ''
    };
    
    return apiClient.post('/api/customer', formattedData);
  },
  
  updateCustomer(id, customerData) {
    // The API expects customerName as one field, but our form has firstName and lastName
    // If we're receiving a customerName directly, use it, otherwise format it
    let formattedData = { ...customerData };
    
    // Make sure we're using the expected field names
    if (!formattedData.customerName && (formattedData.firstName || formattedData.lastName)) {
      formattedData.firstName = formattedData.firstName || '';
      formattedData.lastName = formattedData.lastName || '';
    }
    
    return apiClient.put(`/api/customer/${id}`, formattedData);
  },
  
// In your api.service.js file, update the deleteCustomer function:

deleteCustomer(id) {
  if (!id) {
    console.error('Customer ID is required for deletion');
    return Promise.reject(new Error('Customer ID is required'));
  }
  
  console.log(`Attempting to delete customer with ID: ${id}`);
  
  // Make sure this URL pattern matches your Java backend route
  // Your backend has: delete("/api/customer/:customerId", ...)
  return apiClient.delete(`/api/customer/${id}`)
    .then(response => {
      console.log('Delete customer response:', response);
      return response;
    })
    .catch(error => {
      console.error('Error deleting customer:', error);
      
      // Log more detailed error information
      if (error.response) {
        console.error('Server responded with:', {
          status: error.response.status,
          data: error.response.data
        });
      } else if (error.request) {
        console.error('No response received from server');
      } else {
        console.error('Error setting up request:', error.message);
      }
      
      throw error; // Re-throw to handle in the component
    });
},
getLoyaltyPoints(customerId) {
  return apiClient.get(`/api/loyalty-points/${customerId}`);
},

// Add loyalty points to a customer
addLoyaltyPoints(customerId, pointsToAdd) {
  return apiClient.post(`/api/loyalty-points/${customerId}/add`, {
    pointsToAdd: pointsToAdd
  });
},

// Redeem loyalty points
redeemLoyaltyPoints(redemptionData) {
  const { customerId, pointsRedeemed } = redemptionData;
  return apiClient.post(`/api/loyalty-points/${customerId}/redeem`, {
    pointsToRedeem: pointsRedeemed
  });
}
};
export const stockService = {
  // Get stock for a specific product
  getStock(productId) {
    return apiClient.get(`/api/stock/${productId}`);
  },
  
  // Update stock quantity
  updateStock(productId, stockQuantity) {
    return apiClient.put(`/api/stock/${productId}`, {
      stockQuantity: stockQuantity
    });
  },
  
  // Adjust stock (increment or decrement)
  adjustStock(productId, adjustment) {
    return apiClient.post(`/api/stock/${productId}/adjust`, {
      adjustment: adjustment
    });
  },
  
  // Get low stock items
  getLowStockItems(threshold = 10) {
    return apiClient.get(`/api/stock/low-stock?threshold=${threshold}`);
  },
  
  // Batch update stock
  batchUpdateStock(products) {
    return apiClient.post('/api/stock/batch-update', {
      products: products
    });
  }
};
// Add these methods to your existing reportService in services/api.service.js

// Add these methods to your existing reportService in src/services/api.service.js

// Report service
export const reportService = {
  // Get the current end of day report
  getEndOfDayReport() {
    return apiClient.get('/endOfDayReport');
  },
  
  // Get reports within a date range
  getReportHistory() {
    return apiClient.get('/endOfDayReport/history');
  },
  
  // Get a report for a specific date
  getReportByDate(date) {
    return apiClient.get(`/endOfDayReport/date/${date}`);
  }
};

// Product service
export const productService = {
  getAllProducts() {
    return apiClient.get('/api/products');
  },
  getProductByID(id) {
    return apiClient.get(`/api/product/id/${id}`);
  },
  getProductByBarcode(barcode) {
    return apiClient.get(`/api/product/barcode/${barcode}`);
  },
  createProduct(productData) {
    return apiClient.post('/api/product', productData);
  },
  updateProduct(id, productData) {
    return apiClient.put(`/api/product/${id}`, productData);
  },
  deleteProduct(id) {
    return apiClient.delete(`/api/product/${id}`);
  },
  // New method to get product image URL
  getProductImageUrl(id) {
    return apiClient.get(`/api/product/image/${id}`);
  },
  // New method to update product image URL
  updateProductImageUrl(id, imageUrl) {
    return apiClient.patch(`/api/product/image/${id}`, { image_url: imageUrl });
  }
}

// Order service
export const orderService = {
  getAllOrders() {
    return apiClient.get('/api/orders');
  },
  
  getOrderByID(id) {
    return apiClient.get(`/api/order/${id}`);
  },
  
  createOrder(orderData) {
    // Format the order data to match exactly what your Java backend expects
    const formattedOrderData = {
      customerId: parseInt(orderData.customerId, 10) || 1,
      totalAmount: typeof orderData.totalAmount === 'string' ? 
        orderData.totalAmount : parseFloat(orderData.totalAmount).toFixed(2),
      taxAmount: typeof orderData.taxAmount === 'string' ? 
        orderData.taxAmount : parseFloat(orderData.taxAmount).toFixed(2),
      finalAmount: typeof orderData.finalAmount === 'string' ? 
        orderData.finalAmount : parseFloat(orderData.finalAmount).toFixed(2),
      discountAmount: typeof orderData.discountAmount === 'string' ? 
        orderData.discountAmount : parseFloat(orderData.discountAmount || 0).toFixed(2),
      orderType: orderData.orderType || "In-Store",
      deliveryType: orderData.deliveryType || "",
      orderItems: Array.isArray(orderData.orderItems) ? 
        orderData.orderItems.map(item => ({
          productId: parseInt(item.productId, 10),
          quantity: parseInt(item.quantity, 10),
          price: typeof item.price === 'string' ? item.price : parseFloat(item.price).toFixed(2),
          subtotal: typeof item.subtotal === 'string' ? item.subtotal : parseFloat(item.subtotal).toFixed(2)
        })) : []
    };
    
    console.log('Sending formatted order data:', formattedOrderData);
    return apiClient.post('/api/order', formattedOrderData);
  },
  
  updateOrder(id, orderData) {
    return apiClient.put(`/api/order/${id}`, orderData);
  },
  
  deleteOrder(id) {
    return apiClient.delete(`/api/order/${id}`);
  }
};
// Payment service
// src/services/api.service.js
// Update your payment service implementation to match your Java backend

// Update payment service method in api.service.js
// Update your payment service implementation to match your Java backend

export const paymentService = {
  processPayment(paymentData) {
    console.log('Processing payment with data:', {
      ...paymentData,
      cardDetails: paymentData.cardDetails ? '(sensitive data hidden)' : undefined
    });

    // Format the payment data to match exactly what your Java backend expects
    const formattedPaymentData = {
      transactionId: paymentData.transactionId,
      orderId: parseInt(paymentData.orderId, 10),
      amount: typeof paymentData.amount === 'string' ? 
        paymentData.amount : parseFloat(paymentData.amount).toFixed(2),
      paymentMethod: paymentData.paymentMethod.toUpperCase()
    };

    // Add card details if it's a card payment
    if (paymentData.paymentMethod === 'CARD' && paymentData.cardDetails) {
      formattedPaymentData.cardDetails = {
        cardNumber: paymentData.cardDetails.cardNumber.replace(/\s+/g, ''),
        expiryDate: paymentData.cardDetails.expiryDate,
        cvv: paymentData.cardDetails.cvv,
        cardholderName: paymentData.cardDetails.cardholderName
      };
    }

    console.log('Sending formatted payment data:', {
      ...formattedPaymentData,
      cardDetails: formattedPaymentData.cardDetails ? '(sensitive data hidden)' : undefined
    });
    
    return apiClient.post('/api/payment', formattedPaymentData);
  },
  
  getPaymentByID(id) {
    return apiClient.get(`/api/payment/${id}`);
  }
};



// Invoice service
export const invoiceService = {
  generateInvoice(invoiceData) {
    return apiClient.post('/invoice', invoiceData);
  },
  getInvoiceByID(id) {
    return apiClient.get(`/invoice/${id}`);
  }
};

// Export the configured axios instance and all services
export default {
  apiClient,
  customerService,
  productService,
  orderService,
  paymentService,
  reportService,
  invoiceService,
  stockService
}