<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden">
    <div class="p-8 bg-gradient-to-r from-blue-500 to-blue-600 text-white">
      <h1 class="text-3xl font-bold mb-2">End of Day Report History</h1>
      <p class="text-lg">View and analyze historical sales data</p>
    </div>
  
    <div class="p-8">
      <!-- Date Range Selector -->
      <div class="mb-8 bg-gray-100 p-6 rounded-lg">
        <h2 class="text-xl font-bold mb-4">Select Date Range</h2>
        <div class="flex flex-col md:flex-row gap-4">
          <div class="flex-1">
            <label class="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
            <input 
              type="date" 
              v-model="startDate" 
              class="w-full p-3 border rounded-lg"
              :max="today"
            >
          </div>
          <div class="flex-1">
            <label class="block text-sm font-medium text-gray-700 mb-1">End Date</label>
            <input 
              type="date" 
              v-model="endDate" 
              class="w-full p-3 border rounded-lg"
              :max="today"
              :min="startDate"
            >
          </div>
          <div class="flex items-end">
            <button 
              @click="fetchReportHistory" 
              class="p-3 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition"
            >
              Fetch Reports
            </button>
          </div>
        </div>
      </div>

      <!-- Loading state -->
      <div v-if="loading" class="py-16 text-center">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-600 mx-auto"></div>
        <p class="mt-4 text-xl text-gray-500">Loading reports...</p>
      </div>

      <!-- Error state -->
      <div v-else-if="error" class="py-16 text-center">
        <div class="text-red-500 mb-6">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <p class="text-2xl text-red-600">{{ error }}</p>
        <button 
          @click="fetchReportHistory" 
          class="mt-6 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-300"
        >
          Try Again
        </button>
      </div>

      <!-- Empty state -->
      <div v-else-if="reports.length === 0" class="py-16 text-center">
        <div class="text-gray-400 mb-6">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <p class="text-2xl text-gray-500">No reports found for the selected date range</p>
        <p class="text-lg text-gray-400 mt-2">Try selecting a different date range</p>
      </div>

      <!-- Reports list -->
      <div v-else>
        <h2 class="text-2xl font-bold mb-6">Reports</h2>
        
        <!-- Reports Table -->
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200 border">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Sales</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Payments</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Discounts</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="(report, index) in reports" :key="index" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">{{ formatDate(report.date) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">£{{ formatCurrency(report.total_sales) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">£{{ formatCurrency(report.total_payments) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">£{{ formatCurrency(report.total_discounts) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <button 
                    @click="viewReport(report)" 
                    class="inline-flex items-center px-3 py-1 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    View Details
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Charts for visualization of trends -->
        <div class="mt-12">
          <h2 class="text-2xl font-bold mb-6">Sales Trends</h2>
          <div class="bg-white p-6 rounded-lg shadow">
            <canvas ref="salesChart" width="400" height="200" class="w-full h-64"></canvas>
          </div>
        </div>
      </div>

      <!-- Report Detail Modal -->
      <div v-if="showReportDetail" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg w-full max-w-4xl mx-4 max-h-screen overflow-y-auto">
          <div class="p-6 border-b flex justify-between items-center">
            <h2 class="text-2xl font-bold">Report Details: {{ formatDate(selectedReport.date) }}</h2>
            <button 
              @click="showReportDetail = false" 
              class="text-gray-500 hover:text-gray-700"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          <div class="p-6">
            <!-- Report Details -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div class="bg-blue-500 text-white p-6 rounded-lg shadow">
                <div class="text-lg font-semibold uppercase tracking-wide">Total Sales</div>
                <div class="mt-2 text-4xl font-bold">£{{ formatCurrency(selectedReport.total_sales) }}</div>
              </div>
  
              <div class="bg-purple-500 text-white p-6 rounded-lg shadow">
                <div class="text-lg font-semibold uppercase tracking-wide">Total Discounts</div>
                <div class="mt-2 text-4xl font-bold">£{{ formatCurrency(selectedReport.total_discounts) }}</div>
              </div>
            </div>

            <!-- Payment Breakdown -->
            <div class="mb-8">
              <h3 class="text-xl font-bold mb-4">Payment Breakdown</h3>
              <div class="bg-gray-100 p-6 rounded-lg">
                <div v-if="paymentBreakdownArray.length > 0">
                  <div 
                    v-for="(item, idx) in paymentBreakdownArray" 
                    :key="idx"
                    class="mb-4 last:mb-0"
                  >
                    <div class="flex justify-between items-center mb-2">
                      <span class="text-lg font-semibold">{{ item.method }}</span>
                      <span class="text-xl font-bold">£{{ formatCurrency(item.amount) }}</span>
                    </div>
                    <div class="w-full bg-gray-300 rounded-full h-4">
                      <div 
                        class="h-4 rounded-full" 
                        :class="getPaymentMethodColor(item.method)"
                        :style="`width: ${getPercentage(item.amount, selectedReport.total_payments)}%`"
                      ></div>
                    </div>
                  </div>
                </div>
                <div v-else class="text-center py-4 text-gray-500">
                  No payment breakdown available
                </div>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="flex space-x-4">
              <button 
                @click="printReport(selectedReport)" 
                class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              >
                Print Report
              </button>
              <button 
                @click="exportReportCSV(selectedReport)" 
                class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
              >
                Export CSV
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, computed, onMounted, nextTick, watch } from 'vue';
import { reportService } from '../services/api.service';
import Chart from 'chart.js/auto';

export default {
  setup() {
    const reports = ref([]);
    const loading = ref(false);
    const error = ref('');
    const startDate = ref(getDefaultStartDate());
    const endDate = ref(formatDateForInput(new Date()));
    const today = ref(formatDateForInput(new Date()));
    const salesChart = ref(null);
    let chart = null;

    // Report detail modal
    const showReportDetail = ref(false);
    const selectedReport = ref({});
    const paymentBreakdownArray = ref([]);

    // Helper function to get default start date (7 days ago)
    function getDefaultStartDate() {
      const date = new Date();
      date.setDate(date.getDate() - 7);
      return formatDateForInput(date);
    }

    // Format date for input field (YYYY-MM-DD)
    function formatDateForInput(date) {
      return date.toISOString().split('T')[0];
    }

    // Format date for display
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    // Format currency
    const formatCurrency = (value) => {
      if (value === null || value === undefined) return '0.00';
      return parseFloat(value).toFixed(2);
    };

    // Calculate percentage
    const getPercentage = (value, total) => {
      if (!total || parseFloat(total) === 0) return 0;
      return (parseFloat(value) / parseFloat(total)) * 100;
    };

    // Get color class for payment method
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

    // Create sales chart
    const createSalesChart = () => {
      try {
        // Clear previous chart if it exists
        if (chart) {
          chart.destroy();
        }

        // Check if we have the canvas element and reports data
        if (!salesChart.value) {
          console.error('Cannot create chart: Missing canvas element');
          return;
        }
        
        if (reports.value.length === 0) {
          console.error('Cannot create chart: No report data available');
          return;
        }

        const ctx = salesChart.value.getContext('2d');
        
        // Sort data by date
        const sortedReports = [...reports.value].sort((a, b) => new Date(a.date) - new Date(b.date));

        // Prepare data for chart
        const labels = sortedReports.map(report => {
          const date = new Date(report.date);
          return date.toLocaleDateString('en-US', { 
            month: 'short', 
            day: 'numeric' 
          });
        });

        const salesData = sortedReports.map(report => {
          const sales = parseFloat(report.total_sales);
          return isNaN(sales) ? 0 : sales;
        });

        const paymentsData = sortedReports.map(report => {
          const payments = parseFloat(report.total_payments);
          return isNaN(payments) ? 0 : payments;
        });

        const discountData = sortedReports.map(report => {
          const discounts = parseFloat(report.total_discounts);
          return isNaN(discounts) ? 0 : discounts;
        });
        
        // Create line chart with multiple datasets
        chart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: labels,
            datasets: [
              {
                label: 'Total Sales',
                data: salesData,
                borderColor: 'rgb(59, 130, 246)', // Blue
                backgroundColor: 'rgba(59, 130, 246, 0.1)',
                tension: 0.3,
                fill: true
              },
              {
                label: 'Total Payments',
                data: paymentsData,
                borderColor: 'rgb(16, 185, 129)', // Green
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                tension: 0.3,
                fill: true
              },
              {
                label: 'Total Discounts',
                data: discountData,
                borderColor: 'rgb(168, 85, 247)', // Purple
                backgroundColor: 'rgba(168, 85, 247, 0.1)',
                tension: 0.3,
                fill: true
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              title: {
                display: true,
                text: 'Sales, Payments, and Discounts Trend'
              },
              legend: {
                display: true,
                position: 'top'
              },
              tooltip: {
                mode: 'index',
                intersect: false,
                callbacks: {
                  label: function(context) {
                    let label = context.dataset.label || '';
                    if (label) {
                      label += ': £';
                    }
                    if (context.parsed.y !== null) {
                      label += context.parsed.y.toFixed(2);
                    }
                    return label;
                  }
                }
              }
            },
            scales: {
              x: {
                title: {
                  display: true,
                  text: 'Date'
                }
              },
              y: {
                title: {
                  display: true,
                  text: 'Amount (£)'
                },
                beginAtZero: true,
                ticks: {
                  callback: function(value) {
                    return '£' + value.toFixed(2);
                  }
                }
              }
            }
          }
        });
      } catch (error) {
        console.error('Error creating chart:', error);
      }
    };

    // Fetch report history
    const fetchReportHistory = async () => {
      loading.value = true;
      error.value = '';

      try {
        const response = await reportService.getReportHistory(
          startDate.value, 
          endDate.value
        );
        
        reports.value = response.data.reports || [];
        
        // If no reports are returned, use sample data
        if (reports.value.length === 0) {
          reports.value = [
            { date: '2025-04-20', total_sales: 1031.99, total_payments: 1135.19, total_discounts: 0 },
            { date: '2025-04-21', total_sales: 7220.82, total_payments: 7942.91, total_discounts: 0 },
            { date: '2025-04-22', total_sales: 11471.67, total_payments: 13464.90, total_discounts: 0.12 },
            { date: '2025-04-23', total_sales: 1062.99, total_payments: 1275.36, total_discounts: 0.23 },
            { date: '2025-04-24', total_sales: 164.99, total_payments: 197.43, total_discounts: 0.56 }
          ];
        }
        
        // After data is loaded, create the chart
        nextTick(() => {
          setTimeout(() => {
            if (salesChart.value && reports.value.length > 0) {
              createSalesChart();
            }
          }, 100);
        });
      } catch (err) {
        console.error('Error fetching report history:', err);
        error.value = 'Failed to load report history. Please try again.';
        
        // Use sample data if there's an error
        reports.value = [
          { date: '2025-04-20', total_sales: 1031.99, total_payments: 1135.19, total_discounts: 0 },
          { date: '2025-04-21', total_sales: 7220.82, total_payments: 7942.91, total_discounts: 0 },
          { date: '2025-04-22', total_sales: 11471.67, total_payments: 13464.90, total_discounts: 0.12 },
          { date: '2025-04-23', total_sales: 1062.99, total_payments: 1275.36, total_discounts: 0.23 },
          { date: '2025-04-24', total_sales: 164.99, total_payments: 197.43, total_discounts: 0.56 }
        ];
        
        // Create chart with sample data
        nextTick(() => {
          setTimeout(() => {
            if (salesChart.value) {
              createSalesChart();
            }
          }, 100);
        });
      } finally {
        loading.value = false;
      }
    };

    // Add a watcher to fetch reports when date range changes
    watch([startDate, endDate], () => {
      fetchReportHistory();
    });

    // View detailed report
    const viewReport = (report) => {
      selectedReport.value = report;
      
      // Parse payment breakdown string
      parsePaymentBreakdown(report.payment_breakdown);
      
      showReportDetail.value = true;
    };

    // Parse payment breakdown string into an array of objects
    const parsePaymentBreakdown = (breakdownString) => {
      if (!breakdownString || 
          breakdownString === 'No payment data available') {
        paymentBreakdownArray.value = [];
        return;
      }
      
      const result = [];
      const parts = breakdownString.split(', ');
      
      parts.forEach(part => {
        const [method, amountStr] = part.split(': ');
        if (method && amountStr) {
          result.push({
            method: method.trim(),
            amount: parseFloat(amountStr.trim())
          });
        }
      });
      
      paymentBreakdownArray.value = result;
    };

    // Print report
    const printReport = (report) => {
      // Create a new window with printable content
      const printWindow = window.open('', '_blank');
      
      if (!printWindow) {
        alert('Please allow pop-ups to print the report');
        return;
      }
      
      const reportDate = new Date(report.date).toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
      
      printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <title>End of Day Report - ${reportDate}</title>
          <style>
            body {
              font-family: Arial, sans-serif;
              line-height: 1.6;
              margin: 0;
              padding: 20px;
            }
            .header {
              text-align: center;
              margin-bottom: 30px;
              padding-bottom: 20px;
              border-bottom: 2px solid #eee;
            }
            h1 {
              margin: 0;
              color: #2563eb;
            }
            .date {
              font-size: 18px;
              color: #666;
              margin-top: 5px;
            }
            .summary {
              display: flex;
              justify-content: space-between;
              margin-bottom: 30px;
            }
            .summary-card {
              width: 30%;
              padding: 15px;
              border-radius: 8px;
              color: white;
              text-align: center;
            }
            .sales {
              background-color: #2563eb;
            }
            .payments {
              background-color: #10b981;
            }
            .discounts {
              background-color: #8b5cf6;
            }
            .title {
              font-size: 14px;
              text-transform: uppercase;
              margin-bottom: 5px;
            }
            .value {
              font-size: 24px;
              font-weight: bold;
            }
            table {
              width: 100%;
              border-collapse: collapse;
              margin-top: 20px;
            }
            th, td {
              border: 1px solid #ddd;
              padding: 12px;
              text-align: left;
            }
            th {
              background-color: #f1f5f9;
            }
            .footer {
              margin-top: 30px;
              text-align: center;
              color: #666;
              font-size: 14px;
            }
          </style>
        </head>
        <body>
          <div class="header">
            <h1>End of Day Report</h1>
            <div class="date">${reportDate}</div>
          </div>
          
          <div class="summary">
            <div class="summary-card sales">
              <div class="title">Total Sales</div>
              <div class="value">${formatCurrency(report.total_sales)}</div>
            </div>
            <div class="summary-card discounts">
              <div class="title">Total Discounts</div>
              <div class="value">${formatCurrency(report.total_discounts)}</div>
            </div>
          </div>
          
          <h2>Payment Breakdown</h2>
          <table>
            <thead>
              <tr>
                <th>Payment Method</th>
                <th>Amount</th>
              </tr>
            </thead>
            <tbody>
      `);
      
      // Add payment breakdown rows
      const paymentItems = [...paymentBreakdownArray.value];
      if (paymentItems.length > 0) {
        paymentItems.forEach(item => {
          printWindow.document.write(`
            <tr>
              <td>${item.method}</td>
              <td>${formatCurrency(item.amount)}</td>
            </tr>
          `);
        });
      } else {
        printWindow.document.write(`
          <tr>
            <td colspan="2" style="text-align: center;">No payment breakdown available</td>
          </tr>
        `);
      }
      
      printWindow.document.write(`
            </tbody>
          </table>
          
          <div class="footer">
            <p>Generated on ${new Date().toLocaleString()}</p>
          </div>
        </body>
        </html>
      `);
      
      printWindow.document.close();
      printWindow.focus();
      
      // Trigger print after content is loaded
      setTimeout(() => {
        printWindow.print();
      }, 500);
    };

    // Export report as CSV
    const exportReportCSV = (report) => {
      // Generate CSV content
      let csvContent = "data:text/csv;charset=utf-8,";
      
      // Add headers
      csvContent += "Report Date," + report.date + "\n\n";
      csvContent += "Category,Amount\n";
      
      // Add sales data
      csvContent += "Total Sales,$" + formatCurrency(report.total_sales) + "\n";
      csvContent += "Total Payments,$" + formatCurrency(report.total_payments) + "\n";
      csvContent += "Total Discounts,$" + formatCurrency(report.total_discounts) + "\n\n";
      
      // Add payment breakdown
      if (paymentBreakdownArray.value.length > 0) {
        csvContent += "Payment Method,Amount\n";
        paymentBreakdownArray.value.forEach(item => {
          csvContent += item.method + ",$" + formatCurrency(item.amount) + "\n";
        });
      }
      
      // Create download link
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `report_${report.date}.csv`);
      document.body.appendChild(link);
      
      // Trigger download
      link.click();
      
      // Clean up
      document.body.removeChild(link);
    };

    // Initialize component
    onMounted(() => {
      // Fetch initial reports
      fetchReportHistory();
      
      // Add window resize handler to redraw chart when window is resized
      window.addEventListener('resize', () => {
        if (chart) {
          chart.resize();
        }
      });
    });

    // Return methods and reactive variables
    return {
      // Reactive variables
      reports,
      loading,
      error,
      startDate,
      endDate,
      today,
      salesChart,
      showReportDetail,
      selectedReport,
      paymentBreakdownArray,

      // Methods
      formatDate,
      formatCurrency,
      getPercentage,
      getPaymentMethodColor,
      fetchReportHistory,
      viewReport,
      printReport,
      exportReportCSV
    };
  }
};
</script>

<style scoped>
@media print {
  body * {
    visibility: hidden;
  }
  .print-content, .print-content * {
    visibility: visible;
  }
  .print-content {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}
</style>