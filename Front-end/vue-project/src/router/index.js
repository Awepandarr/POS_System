import { createRouter, createWebHistory } from 'vue-router';
import { useAuth } from '../services/auth.service';

// Use dynamic imports for better performance, especially in production.
const POSDashboard = () => import('../views/POSDashboard.vue');
const CustomerManagement = () => import('../views/CustomerManagement.vue');
const PaymentPage = () => import('../views/PaymentPage.vue');
const ConfirmationPage = () => import('../views/ConfirmationPage.vue');
const EndOfDayReport = () => import('../views/EndOfDayReport.vue');
const ProductManagement = () => import('../views/ProductManagement.vue');
const OrdersPage = () => import('../views/OrdersPage.vue');
const LoginView = () => import('../views/LoginView.vue');
const Profile = () => import('../views/Profile.vue');
const OrderDetails = () => import('../views/OrderDetails.vue');
const History=()=>import('../views/EndOfDayReportHistory.vue');
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/profile',
      name: 'Profile',
      component: Profile
    },
    {
      path: '/stock',
      name: 'StockManagement',
      component: () => import(/* webpackChunkName: "stock" */ '../views/StockManagerView.vue'),
      meta: { requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/Settings.vue')
    },
    {
      path:'/report/history',
      name:'History',
      component:History,
    },

// Then make sure you have this route defined in your routes array
{
  path: '/orders/:id',
  name: 'OrderDetails',
  component: OrderDetails,
  meta: { title: 'Order Details', requiresAuth: true }
},
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { title: 'Sign In', requiresAuth: false }
    },
    {
      path: '/',
      name: 'dashboard',
      component: POSDashboard,
      meta: { title: 'POS Dashboard', requiresAuth: true }
    },
    {
      path: '/customers',
      name: 'customers',
      component: CustomerManagement,
      meta: { title: 'Customer Management', requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/payment',
      name: 'payment',
      component: PaymentPage,
      meta: { title: 'Payment', requiresAuth: true }
    },
    {
      path: '/confirmation',
      name: 'confirmation',
      component: ConfirmationPage,
      meta: { title: 'Order Confirmation', requiresAuth: true }
    },
    {
      path: '/reports',
      name: 'reports',
      component: EndOfDayReport,
      meta: { title: 'Reports', requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/products',
      name: 'products',
      component: ProductManagement,
      meta: { title: 'Product Management', requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrdersPage,
      meta: { title: 'Orders', requiresAuth: true }
    }
  ]
});

// Navigation guard
router.beforeEach((to, from, next) => {
  // Update page title
  document.title = `${to.meta.title || 'POS System'}`;
  
  const { isAuthenticated, user } = useAuth();
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  
  // Check if route requires authentication
  if (requiresAuth && !isAuthenticated.value) {
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }
  
  // Check if route requires specific role
  if (requiresAuth && to.meta.roles) {
    const userHasRequiredRole = to.meta.roles.includes(user.value.role);
    if (!userHasRequiredRole) {
      // Redirect to dashboard if user doesn't have required role
      next({ name: 'dashboard' });
      return;
    }
  }
  
  // If route is login and user is authenticated, redirect to dashboard
  if (to.name === 'login' && isAuthenticated.value) {
    next({ name: 'dashboard' });
    return;
  }
  
  next();
});

export default router;