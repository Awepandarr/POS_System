import axios from 'axios';

// Define API base URL - adjust if you're using a different base URL
const API_URL = process.env.VUE_APP_API_URL || '';

// Create a dedicated API client for invoice service
const invoiceApiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});

export const invoiceService = {
  /**
   * Create a new invoice directly
   * @param {Object} invoiceData - The invoice data
   * @returns {Promise} - The API response
   */
  createInvoice(invoiceData) {
    return invoiceApiClient.post('/invoice', invoiceData);
  },
  
  /**
   * Create an invoice from an existing payment transaction
   * @param {String} transactionId - The payment transaction ID
   * @returns {Promise} - The API response
   */
  createInvoiceFromPayment(transactionId) {
    return invoiceApiClient.post(`/invoice/fromPayment/${transactionId}`);
  },
  
  /**
   * Get an invoice by ID
   * @param {Number} invoiceId - The invoice ID
   * @returns {Promise} - The API response
   */
  getInvoiceById(invoiceId) {
    return invoiceApiClient.get(`/invoice/${invoiceId}`);
  },
  
  /**
   * Get all invoices for today (for end-of-day reporting)
   * @returns {Promise} - The API response
   */
  getTodayInvoices() {
    return invoiceApiClient.get('/invoices/today');
  },
  
  /**
   * Generate a PDF version of an invoice
   * @param {Number} invoiceId - The invoice ID
   * @returns {Promise} - The API response with PDF data
   */
  generateInvoicePDF(invoiceId) {
    return invoiceApiClient.get(`/invoice/${invoiceId}/pdf`, {
      responseType: 'blob'
    });
  },
  
  /**
   * Email an invoice to a customer
   * @param {Number} invoiceId - The invoice ID
   * @param {String} email - The customer's email address
   * @returns {Promise} - The API response
   */
  emailInvoice(invoiceId, email) {
    return invoiceApiClient.post(`/invoice/${invoiceId}/email`, { email });
  }
};

// Export the service as default export
export default invoiceService;