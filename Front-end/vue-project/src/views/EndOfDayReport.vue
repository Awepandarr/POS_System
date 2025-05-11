<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden">
    <div class="p-8 bg-gradient-to-r from-blue-500 to-blue-600 text-white">
      <h1 class="text-3xl font-bold mb-2">End of Day Report</h1>
      <p class="text-lg">{{ currentDate }}</p>
    </div>
    <div class="absolute top-4 right-4">
      <button 
        @click="forceRefresh" 
        class="flex items-center space-x-2 bg-white hover:bg-gray-100 text-blue-600 px-3 py-1 rounded-full border border-blue-200 shadow-sm transition duration-300"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        <span class="text-sm">Refresh</span>
      </button>
    </div>

    <div class="p-8">
      <div v-if="loading" class="py-16 text-center">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-600 mx-auto"></div>
        <p class="mt-4 text-xl text-gray-500">Generating report...</p>
      </div>
  
      <div v-else-if="error" class="py-16 text-center">
        <div class="text-red-500 mb-6">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <p class="text-2xl text-red-600">{{ error }}</p>
        <button 
          @click="fetchReport" 
          class="mt-6 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
        >
          Try Again
        </button>
      </div>
  
      <div v-else>
        <!-- Sales Summary -->
        <div class="grid grid-cols-1 md:grid-cols-5 gap-6 mb-12">
          <div class="bg-green-500 text-white p-6 rounded-lg shadow-lg">
            <div class="text-sm font-semibold uppercase tracking-wide">Total Sales (Gross)</div>
            <div class="mt-3 text-3xl font-bold">£{{ formatCurrency(report.total_sales) }}</div>
          </div>
          
          <div class="bg-emerald-600 text-white p-6 rounded-lg shadow-lg">
            <div class="text-sm font-semibold uppercase tracking-wide">Net Sales</div>
            <div class="mt-3 text-3xl font-bold">£{{ formatCurrency(report.net_sales) }}</div>
          </div>
          
          <div class="bg-teal-500 text-white p-6 rounded-lg shadow-lg">
            <div class="text-sm font-semibold uppercase tracking-wide">Tax Payable</div>
            <div class="mt-3 text-3xl font-bold">£{{ formatCurrency(report.tax_amount) }}</div>
            <div class="text-xs mt-1">VAT {{ formatPercentage(report.tax_rate) }}</div>
          </div>
          
          <div class="bg-purple-500 text-white p-6 rounded-lg shadow-lg">
            <div class="text-sm font-semibold uppercase tracking-wide">Total Discounts</div>
            <div class="mt-3 text-3xl font-bold">£{{ formatCurrency(report.total_discounts) }}</div>
          </div>
        </div>
  
        <div class="mb-12">
          <h2 class="text-2xl font-bold mb-6">Top Selling Products</h2>
          <div class="bg-gray-100 rounded-lg shadow-lg p-8">
            <div v-if="topProducts.length > 0">
              <div class="overflow-hidden rounded-lg border border-gray-200">
                <table class="min-w-full divide-y divide-gray-200">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                      <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity Sold</th>
                      <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Gross Revenue</th>
                      <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Net Revenue</th>
                      <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Tax</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(product, index) in topProducts" :key="index" class="hover:bg-gray-50">
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="flex items-center">
                          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
                            <span class="font-semibold">{{ index + 1 }}</span>
                          </div>
                          <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">{{ product.name }}</div>
                            <div class="text-sm text-gray-500">ID: {{ product.product_id }}</div>
                          </div>
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-center">
                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                          {{ product.quantity_sold }} units
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        £{{ formatCurrency(product.revenue) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        £{{ formatCurrency(calculateNetRevenue(product.revenue)) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        £{{ formatCurrency(calculateTax(product.revenue)) }}
                      </td>
                    </tr>
                    <tr class="bg-gray-50 font-bold">
                      <td class="px-6 py-4 whitespace-nowrap">Totals</td>
                      <td class="px-6 py-4 whitespace-nowrap text-center">
                        {{ calculateTotalQuantity() }} units
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right">
                        £{{ formatCurrency(calculateTotalRevenue()) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right">
                        £{{ formatCurrency(calculateTotalNetRevenue()) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right">
                        £{{ formatCurrency(calculateTotalTax()) }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div v-else class="text-xl text-gray-500 text-center py-8">
              No product data available
            </div>
          </div>
        </div>
  
        <!-- Payment Breakdown -->
        <div class="mb-12">
          <h2 class="text-2xl font-bold mb-6">Payment Method Breakdown</h2>
          <div class="bg-gray-100 rounded-lg shadow-lg p-8">
            <div class="flex flex-col md:flex-row">
              <div class="flex-1 mb-8 md:mb-0 md:mr-12">
                <div v-if="paymentBreakdown.length > 0">
                  <div 
                    v-for="(item, index) in paymentBreakdown" 
                    :key="index"
                    class="mb-6 last:mb-0"
                  >
                    <div class="flex justify-between items-center mb-2">
                      <span class="text-xl font-semibold">{{ item.method }}</span>
                      <span class="text-2xl font-bold">£{{ formatCurrency(item.amount) }}</span>
                    </div>
                    <div class="w-full bg-gray-300 rounded-full h-4">
                      <div 
                        class="h-4 rounded-full transition-all duration-500 ease-out" 
                        :class="getPaymentMethodColor(item.method)"
                        :style="`width: ${getPercentage(item.amount, report.total_payments)}%`"
                      ></div>
                    </div>
                  </div>
                </div>
                <div v-else class="text-xl text-gray-500 text-center py-8">
                  No payment data available
                </div>
              </div>
              
              <div class="flex-1">
                <!-- Chart container -->
                <div id="payment-chart" class="h-64 bg-white rounded-lg shadow-inner">
                  <!-- SVG chart will be inserted here -->
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Tax Summary -->
        <div class="mb-12">
          <h2 class="text-2xl font-bold mb-6">Tax Summary</h2>
          <div class="bg-gray-100 rounded-lg shadow-lg p-8">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div>
                <h3 class="text-lg font-semibold mb-4">Tax Breakdown</h3>
                <div class="bg-white rounded-lg p-6 shadow-md">
                  <div class="mb-4">
                    <div class="flex justify-between mb-2">
                      <span class="text-gray-600">Gross Sales (inc. tax):</span>
                      <span class="font-semibold">£{{ formatCurrency(report.total_sales) }}</span>
                    </div>
                    <div class="flex justify-between mb-2">
                      <span class="text-gray-600">Net Sales (exc. tax):</span>
                      <span class="font-semibold">£{{ formatCurrency(report.net_sales) }}</span>
                    </div>
                    <div class="flex justify-between mb-2">
                      <span class="text-gray-600">VAT ({{ formatPercentage(report.tax_rate) }}):</span>
                      <span class="font-semibold">£{{ formatCurrency(report.tax_amount) }}</span>
                    </div>
                  </div>
                  <div class="pt-4 border-t border-gray-200">
                    <div class="flex justify-between font-bold">
                      <span>Tax Payable:</span>
                      <span class="text-teal-600">£{{ formatCurrency(report.tax_amount) }}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div>
                <h3 class="text-lg font-semibold mb-4">Tax Visualization</h3>
                <div class="bg-white rounded-lg p-6 shadow-md h-64 flex items-center justify-center">
                  <!-- Simple tax visualization -->
                  <div class="w-full max-w-xs">
                    <div class="text-center mb-4">
                      <div class="text-2xl font-bold">£{{ formatCurrency(report.total_sales) }}</div>
                      <div class="text-sm text-gray-500">Total Sales (100%)</div>
                    </div>
                    
                    <div class="relative h-16 bg-gray-200 rounded-lg overflow-hidden">
                      <!-- Net Amount -->
                      <div 
                        class="absolute top-0 left-0 h-full bg-emerald-500"
                        :style="`width: ${(report.net_sales / report.total_sales * 100)}%`"
                      ></div>
                      
                      <!-- Tax Amount (positioned after net amount) -->
                      <div 
                        class="absolute top-0 h-full bg-teal-500"
                        :style="`left: ${(report.net_sales / report.total_sales * 100)}%; width: ${(report.tax_amount / report.total_sales * 100)}%`"
                      ></div>
                      
                      <!-- Labels -->
                      <div class="absolute inset-0 flex text-xs text-white">
                        <div 
                          class="flex items-center justify-center"
                          :style="`width: ${(report.net_sales / report.total_sales * 100)}%`"
                        >
                          Net: {{ formatPercentage(report.net_sales / report.total_sales) }}
                        </div>
                        <div 
                          class="flex items-center justify-center"
                          :style="`width: ${(report.tax_amount / report.total_sales * 100)}%`"
                        >
                          Tax: {{ formatPercentage(report.tax_amount / report.total_sales) }}
                        </div>
                      </div>
                    </div>
                    
                    <div class="flex justify-between text-sm mt-2">
                      <div>
                        <div class="font-semibold">£{{ formatCurrency(report.net_sales) }}</div>
                        <div class="text-xs text-gray-500">Net Amount</div>
                      </div>
                      <div class="text-right">
                        <div class="font-semibold">£{{ formatCurrency(report.tax_amount) }}</div>
                        <div class="text-xs text-gray-500">Tax Amount</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Action Buttons -->
        <div class="mb-6">
          <router-link 
            to="/report/history" 
            class="flex items-center space-x-3 bg-gray-700 hover:bg-gray-800 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>View Report History</span>
          </router-link>
        </div>

        <div class="flex flex-wrap gap-6">
          <button 
            @click="printReport" 
            class="flex items-center space-x-3 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
            </svg>
            <span>Print Report</span>
          </button>
          
          <button 
            @click="exportReportCSV" 
            class="flex items-center space-x-3 bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <span>Export CSV</span>
          </button>
          
          <button 
            @click="exportReportPDF" 
            class="flex items-center space-x-3 bg-red-500 hover:bg-red-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <span>Export PDF</span>
          </button>
          
          <button 
            @click="sendReportEmail" 
            class="flex items-center space-x-3 bg-yellow-500 hover:bg-yellow-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            <span>Email Report</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick, watch } from 'vue';
import { reportService } from '../services/api.service';
import * as d3 from 'd3';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

export default {
  setup() {
    const calculateNetRevenue = (grossRevenue) => {
  // Using formula: net = gross / (1 + tax_rate)
  return grossRevenue / (1 + report.value.tax_rate);
};

// Calculate tax amount from gross revenue
const calculateTax = (grossRevenue) => {
  // Tax is the difference between gross and net
  return grossRevenue - calculateNetRevenue(grossRevenue);
};

// Calculate total quantity of items sold
const calculateTotalQuantity = () => {
  return topProducts.value.reduce((sum, product) => sum + product.quantity_sold, 0);
};

// Calculate total gross revenue
const calculateTotalRevenue = () => {
  return topProducts.value.reduce((sum, product) => sum + product.revenue, 0);
};

// Calculate total net revenue (excluding tax)
const calculateTotalNetRevenue = () => {
  return calculateTotalRevenue() / (1 + report.value.tax_rate);
};
    const report = ref({
      total_sales: 0,        // Gross amount (including tax)
      net_sales: 0,          // Net amount (excluding tax)
      tax_amount: 0,         // Tax collected
      tax_rate: 0.20,        // Tax rate (20% VAT)
      total_payments: 0,
      total_discounts: 0,
      payment_breakdown: '',
      top_products: []
    });
    
    const loading = ref(true);
    const error = ref('');
    
    const currentDate = computed(() => {
      const now = new Date();
      return now.toLocaleDateString('en-US', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      });
    });

    
    const paymentBreakdown = computed(() => {
      if (!report.value.payment_breakdown || 
          report.value.payment_breakdown === 'No payment data available') {
        return [];
      }
      
      // Parse the payment breakdown string (format: "Cash: 500, Card: 900")
      const result = [];
      const parts = report.value.payment_breakdown.split(', ');
      
      parts.forEach(part => {
        const [method, amountStr] = part.split(': ');
        if (method && amountStr) {
          result.push({
            method: method.trim(),
            amount: parseFloat(amountStr.trim())
          });
        }
      });
      
      return result;
    });
    
    const topProducts = computed(() => {
      return report.value.top_products || [];
    });
    
    // Create payment method chart
    const createPaymentChart = () => {
      // Clear any existing chart first
      d3.select("#payment-chart").selectAll("*").remove();
      
      // Exit if no data
      if (paymentBreakdown.value.length === 0) {
        d3.select("#payment-chart")
          .append("div")
          .attr("class", "flex items-center justify-center h-full w-full")
          .append("p")
          .attr("class", "text-gray-500 text-lg")
          .text("No chart data available");
        return;
      }
      
      // Get the container dimensions
      const container = document.getElementById('payment-chart');
      if (!container) return;
      
      const width = container.clientWidth;
      const height = container.clientHeight;
      
      // Create SVG
      const svg = d3.select("#payment-chart")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);
        
      // Color scale
      const color = d3.scaleOrdinal()
        .domain(paymentBreakdown.value.map(d => d.method))
        .range(['#10B981', '#3B82F6', '#8B5CF6', '#F59E0B', '#EF4444']);
      
      // Compute position of each group on the pie
      const pie = d3.pie()
        .value(d => d.amount);
      
      const data_ready = pie(paymentBreakdown.value);
      
      // The arc generator
      const arc = d3.arc()
        .innerRadius(Math.min(width, height) / 4)  // This is the size of the donut hole
        .outerRadius(Math.min(width, height) / 2 - 10);
      
      // Another arc for the labels
      const labelArc = d3.arc()
        .innerRadius(Math.min(width, height) / 2 * 0.7)
        .outerRadius(Math.min(width, height) / 2 * 0.7);
      
      // Build the pie chart
      svg.selectAll('allSlices')
        .data(data_ready)
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', d => color(d.data.method))
        .attr("stroke", "white")
        .style("stroke-width", "2px")
        .style("opacity", 0.7);
      
      // Add labels
      svg.selectAll('allLabels')
        .data(data_ready)
        .enter()
        .append('text')
        .text(d => {
          const percent = ((d.data.amount / d3.sum(paymentBreakdown.value, d => d.amount)) * 100).toFixed(0);
          return `${d.data.method} (${percent}%)`;
        })
        .attr('transform', d => `translate(${labelArc.centroid(d)})`)
        .style('text-anchor', 'middle')
        .style('font-size', '12px')
        .style('fill', 'white')
        .style('font-weight', 'bold');
      
      // Add the center text
      svg.append('text')
        .attr('text-anchor', 'middle')
        .attr('dominant-baseline', 'middle')
        .style('font-size', '16px')
        .style('font-weight', 'bold')
        .text(`£${d3.sum(paymentBreakdown.value, d => d.amount).toFixed(2)}`);
    };
    
    // Fetch the report data from the API
    const fetchReport = async () => {
      loading.value = true;
      error.value = '';
      
      try {
        // Add a small delay to ensure UI updates properly
        await new Promise(resolve => setTimeout(resolve, 100));
        
        const response = await reportService.getEndOfDayReport();
        console.log('API Response:', response.data);
        
        // Process each field individually to ensure proper type conversion
        // Create a new object to ensure Vue detects the changes
        const newReport = {};
        
        // Ensure consistent number conversion for all monetary values
        newReport.total_sales = parseFloat(response.data?.total_sales || 0);
        newReport.total_payments = parseFloat(response.data?.total_payments || 0);
        newReport.total_discounts = parseFloat(response.data?.total_discounts || 0);
        newReport.payment_breakdown = response.data?.payment_breakdown || '';
        newReport.top_products = response.data?.top_products || [];
        
        // Calculate tax and net sales
        newReport.tax_rate = 0.20; // 20% VAT
        
        // Calculate net amount (before tax) and tax amount
        // Formula: net_sales = total_sales / (1 + tax_rate)
        newReport.net_sales = newReport.total_sales / (1 + newReport.tax_rate);
        newReport.tax_amount = newReport.total_sales - newReport.net_sales;
        
        console.log('Processed Report:', newReport);
        
        // Force Vue to replace the entire report object rather than updating properties
        report.value = Object.assign({}, newReport);
        
      } catch (err) {
        console.error('Error fetching end of day report:', err);
        error.value = 'Failed to generate the end of day report. Please try again.';
      } finally {
        loading.value = false;
        // Use nextTick to ensure DOM is updated before creating chart
        nextTick(() => {
          createPaymentChart();
        });
      }
    }

    const forceRefresh = () => {
      fetchReport();
    };

    // Watch for changes to report data to update the chart
    watch(() => report.value, (newVal) => {
      console.log('Report data changed:', newVal);
    }, { deep: true });
    
    const formatCurrency = (value) => {
      // Check if value is undefined, null or not a number
      if (value === undefined || value === null || isNaN(parseFloat(value))) {
        return '0.00';
      }
      return parseFloat(value).toFixed(2);
    };

    const formatPercentage = (value) => {
      // Convert decimal to percentage
      if (value === undefined || value === null || isNaN(parseFloat(value))) {
        return '0%';
      }
      return `${(parseFloat(value) * 100).toFixed(0)}%`;
    };

    const calculateTotalTax = () => {
      return calculateTotalRevenue() - calculateTotalNetRevenue();
    };

    const getPercentage = (value, total) => {
      // Ensure both values are numbers
      const numValue = parseFloat(value);
      const numTotal = parseFloat(total);
      
      // Guard against NaN or zero division
      if (isNaN(numValue) || isNaN(numTotal) || numTotal === 0) {
        return 0;
      }
      
      return (numValue / numTotal) * 100;
    };
    
    const getPaymentMethodColor = (method) => {
      const methodMap = {
        'Cash': 'bg-green-500',
        'CASH': 'bg-green-500',
        'Card': 'bg-blue-500',
        'CARD': 'bg-blue-500',
        'Mobile': 'bg-purple-500',
        'Other': 'bg-gray-500'
      };
      
      return methodMap[method] || 'bg-gray-500';
    };
    
    const printReport = () => {
      window.print();
    };
    
    const exportReportCSV = () => {
      // Generate CSV content
      let csvContent = "data:text/csv;charset=utf-8,";
      
      // Add headers for financial summary
      csvContent += "Financial Summary\n";
      csvContent += "Category,Amount\n";
      
      // Add total sales (gross)
      csvContent += `Total Sales (Gross),£${formatCurrency(report.value.total_sales)}\n`;
      
      // Add net sales
      csvContent += `Net Sales (Excluding Tax),£${formatCurrency(report.value.net_sales)}\n`;
      
      // Add tax amount
      csvContent += `Tax Payable (${report.value.tax_rate * 100}%),£${formatCurrency(report.value.tax_amount)}\n`;
      
      // Add total discounts
      csvContent += `Total Discounts,£${formatCurrency(report.value.total_discounts)}\n`;
      
      // Add total payments
      csvContent += `Total Payments,£${formatCurrency(report.value.total_payments)}\n\n`;
      
      // Add payment breakdown
      csvContent += "Payment Method Breakdown\n";
      csvContent += "Payment Method,Amount,Percentage\n";
      if (paymentBreakdown.value.length > 0) {
        paymentBreakdown.value.forEach(item => {
          const percentage = getPercentage(item.amount, report.value.total_payments);
          csvContent += `${item.method},£${formatCurrency(item.amount)},${percentage.toFixed(2)}%\n`;
        });
      } else {
        csvContent += "No payment data available,0.00,0%\n";
      }
      
      csvContent += "\nTop Selling Products\n";
      csvContent += "Rank,Product Name,Product ID,Quantity Sold,Gross Revenue,Net Revenue,Tax\n";
      
      if (topProducts.value.length > 0) {
        topProducts.value.forEach((product, index) => {
          const netRevenue = calculateNetRevenue(product.revenue);
          const tax = calculateTax(product.revenue);
          
          csvContent += `${index + 1},${product.name},${product.product_id},${product.quantity_sold},£${formatCurrency(product.revenue)},£${formatCurrency(netRevenue)},£${formatCurrency(tax)}\n`;
        });
        
        // Add totals row
        csvContent += `Total,,${calculateTotalQuantity()},£${formatCurrency(calculateTotalRevenue())},£${formatCurrency(calculateTotalNetRevenue())},£${formatCurrency(calculateTotalTax())}\n`;
      } else {
        csvContent += "No product data available\n";
      }
      
      // Create download link and trigger download
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `end_of_day_report_${new Date().toISOString().split('T')[0]}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    };
    
    // Export to PDF
    const exportReportPDF = () => {
      try {
        // Create a container for PDF content
        const printContainer = document.createElement('div');
        printContainer.style.fontFamily = 'Arial, sans-serif';
        printContainer.style.maxWidth = '800px';
        printContainer.style.margin = '0 auto';
        printContainer.style.padding = '20px';

        // Header
        const header = document.createElement('h1');
        header.textContent = 'End of Day Report';
        header.style.textAlign = 'center';
        header.style.borderBottom = '2px solid #000';
        header.style.paddingBottom = '10px';
        printContainer.appendChild(header);

        // Date
        const dateEl = document.createElement('p');
        dateEl.textContent = `Date: ${currentDate.value}`;
        dateEl.style.textAlign = 'right';
        dateEl.style.fontStyle = 'italic';
        printContainer.appendChild(dateEl);

        // Financial Summary Section
        const summarySection = document.createElement('div');
        summarySection.innerHTML = `
          <h2>Financial Summary</h2>
          <table style="width:100%; border-collapse: collapse;">
            <tr>
              <td>Total Sales (Gross - including tax)</td>
              <td style="text-align:right;">£${formatCurrency(report.value.total_sales)}</td>
            </tr>
            <tr>
              <td>Net Sales (excluding tax)</td>
              <td style="text-align:right;">£${formatCurrency(report.value.net_sales)}</td>
            </tr>
            <tr>
              <td>Tax Payable (${report.value.tax_rate * 100}%)</td>
              <td style="text-align:right;">£${formatCurrency(report.value.tax_amount)}</td>
            </tr>
            <tr>
              <td>Total Discounts</td>
              <td style="text-align:right;">£${formatCurrency(report.value.total_discounts)}</td>
            </tr>
            <tr>
              <td>Total Payments</td>
              <td style="text-align:right;">£${formatCurrency(report.value.total_payments)}</td>
            </tr>
          </table>
        `;
        printContainer.appendChild(summarySection);

        // Payment Breakdown Section
        const paymentSection = document.createElement('div');
        paymentSection.innerHTML = '<h2>Payment Method Breakdown</h2>';
        
        if (paymentBreakdown.value.length > 0) {
          const paymentTable = document.createElement('table');
          paymentTable.style.width = '100%';
          paymentTable.style.borderCollapse = 'collapse';
          
          paymentTable.innerHTML = `
            <tr>
              <th style="text-align:left; border: 1px solid #ddd; padding: 8px;">Method</th>
              <th style="text-align:right; border: 1px solid #ddd; padding: 8px;">Amount</th>
              <th style="text-align:right; border: 1px solid #ddd; padding: 8px;">Percentage</th>
            </tr>
            ${paymentBreakdown.value.map(item => {
              const percentage = getPercentage(item.amount, report.value.total_payments);
              return `
                <tr>
                  <td style="text-align:left; border: 1px solid #ddd; padding: 8px;">${item.method}</td>
                  <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(item.amount)}</td>
                  <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">${percentage.toFixed(2)}%</td>
                </tr>
              `;
            }).join('')}
          `;
          
          paymentSection.appendChild(paymentTable);
        } else {
          paymentSection.innerHTML += '<p>No payment data available</p>';
        }
        printContainer.appendChild(paymentSection);

        // Top Products Section
        const productsSection = document.createElement('div');
        productsSection.innerHTML = '<h2>Top Selling Products</h2>';
        
        if (topProducts.value.length > 0) {
          const productsTable = document.createElement('table');
          productsTable.style.width = '100%';
          productsTable.style.borderCollapse = 'collapse';
          
          productsTable.innerHTML = `
            <tr>
              <th style="text-align:left; border: 1px solid #ddd; padding: 8px;">Rank</th>
              <th style="text-align:left; border: 1px solid #ddd; padding: 8px;">Product Name</th>
              <th style="text-align:center; border: 1px solid #ddd; padding: 8px;">Quantity</th>
              <th style="text-align:right; border: 1px solid #ddd; padding: 8px;">Gross Revenue</th>
              <th style="text-align:right; border: 1px solid #ddd; padding: 8px;">Net Revenue</th>
              <th style="text-align:right; border: 1px solid #ddd; padding: 8px;">Tax</th>
            </tr>
            ${topProducts.value.map((product, index) => {
              const netRevenue = calculateNetRevenue(product.revenue);
              const tax = calculateTax(product.revenue);
              return `
                <tr>
                  <td style="text-align:left; border: 1px solid #ddd; padding: 8px;">${index + 1}</td>
                  <td style="text-align:left; border: 1px solid #ddd; padding: 8px;">${product.name}</td>
                  <td style="text-align:center; border: 1px solid #ddd; padding: 8px;">${product.quantity_sold}</td>
                  <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(product.revenue)}</td>
                  <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(netRevenue)}</td>
                  <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(tax)}</td>
                </tr>
              `;
            }).join('')}
            <tr style="font-weight: bold; background-color: #f2f2f2;">
              <td style="text-align:left; border: 1px solid #ddd; padding: 8px;" colspan="2">Totals</td>
              <td style="text-align:center; border: 1px solid #ddd; padding: 8px;">${calculateTotalQuantity()}</td>
              <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(calculateTotalRevenue())}</td>
              <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(calculateTotalNetRevenue())}</td>
              <td style="text-align:right; border: 1px solid #ddd; padding: 8px;">£${formatCurrency(calculateTotalTax())}</td>
            </tr>
          `;
          
          productsSection.appendChild(productsTable);
        } else {
          productsSection.innerHTML += '<p>No product data available</p>';
        }
        printContainer.appendChild(productsSection);

        // Tax Summary Section
        const taxSection = document.createElement('div');
        taxSection.innerHTML = `
          <h2>Tax Summary</h2>
          <p>Tax Rate: ${report.value.tax_rate * 100}% (VAT)</p>
          <table style="width:100%; border-collapse: collapse;">
            <tr>
              <td>Gross Sales (including tax)</td>
              <td style="text-align:right;">£${formatCurrency(report.value.total_sales)}</td>
            </tr>
            <tr>
              <td>Net Sales (excluding tax)</td>
              <td style="text-align:right;">£${formatCurrency(report.value.net_sales)}</td>
            </tr>
            <tr style="font-weight: bold;">
              <td>Tax Payable</td>
              <td style="text-align:right;">£${formatCurrency(report.value.tax_amount)}</td>
            </tr>
          </table>
        `;
        printContainer.appendChild(taxSection);

        // Create a new window for printing
        const printWindow = window.open('', '_blank');
        printWindow.document.write('<html><head><title>End of Day Report</title>');
        printWindow.document.write(`
          <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; }
            table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
            table, th, td { border: 1px solid #ddd; padding: 8px; }
            th { background-color: #f2f2f2; }
            h2 { color: #2563eb; margin-top: 25px; border-bottom: 1px solid #e5e7eb; padding-bottom: 5px; }
          </style>
        `);
        printWindow.document.write('</head><body>');
        printWindow.document.write(printContainer.innerHTML);
        printWindow.document.write('</body></html>');
        printWindow.document.close();
        
        // Trigger print
        printWindow.print();
      } catch (error) {
        console.error('Error generating print-friendly report:', error);
        alert('Failed to generate report. Please try again.');
      }
    };
    
    const sendReportEmail = () => {
      const email = prompt("Enter email address to send report to:");
      if (!email) return;
      
      // Validate email format with regex
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        alert("Please enter a valid email address");
        return;
      }
      
      // In a real implementation, you would send the report via API
      alert(`Sending report to ${email}...`);
      
      // You can add an API call here to send the email with report data
      setTimeout(() => {
        alert(`Report successfully sent to ${email}`);
      }, 1500);
    };
    
    onMounted(() => {
      // Call fetchReport immediately when component mounts
      fetchReport();
      
      // Handle window resize
      window.addEventListener('resize', () => {
        createPaymentChart();
      });
      
      // Clean up event listener when component is unmounted
      return () => {
        window.removeEventListener('resize', createPaymentChart);
      };
    });
    
    return {
      report,
      loading,
      error,
      currentDate,
      paymentBreakdown,
      fetchReport,
      formatCurrency,
      formatPercentage,
      getPercentage,
      getPaymentMethodColor,
      printReport,
      exportReportCSV,
      exportReportPDF,
      sendReportEmail,
      topProducts,
      createPaymentChart,
      forceRefresh,
      calculateNetRevenue,
      calculateTax,
      calculateTotalQuantity,
      calculateTotalRevenue,
      calculateTotalNetRevenue,
      calculateTotalTax
    };
  }
};
</script>

<style>
@media print {
  body * {
    visibility: hidden;
  }
  .bg-blue-600, .bg-green-500, .bg-emerald-600, .bg-teal-500, .bg-purple-500 {
    background-color: #f3f4f6 !important;
    color: #000 !important;
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
  #app, #app * {
    visibility: visible;
  }
  #app {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
  
  /* Hide buttons when printing */
  button, a {
    display: none !important;
  }
}
</style>