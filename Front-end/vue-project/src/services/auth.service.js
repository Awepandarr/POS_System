// src/services/auth.service.js
import { ref, computed } from 'vue';
import axios from 'axios';

// Create a reactive state for authentication
const user = ref(null);
const loading = ref(false);
const error = ref(null);

// Initialize auth from localStorage on page load
try {
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    user.value = JSON.parse(savedUser);
  }
} catch (err) {
  console.error('Error loading auth state:', err);
}

// Set the Authorization header for all API requests
if (user.value && user.value.token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${user.value.token}`;
}

export function useAuth() {
  // Mock user data - Replace with real API calls in production
  const mockUsers = [
    { 
      id: 1, 
      email: 'admin@example.com', 
      password: 'admin123', 
      name: 'Admin User',
      role: 'admin',
      token: 'mock-jwt-token-admin'
    },
    { 
      id: 2, 
      email: 'cashier@example.com', 
      password: 'cashier123', 
      name: 'Cashier User',
      role: 'cashier',
      token: 'mock-jwt-token-cashier'
    }
  ];
  
  // Login function
  const login = async (email, password, remember = false) => {
    loading.value = true;
    error.value = null;
    
    try {
      // For demo purposes, we'll simulate a login request
      // In a real app, this would be an API call
      await new Promise(resolve => setTimeout(resolve, 800)); // Simulate API delay
      
      // Find matching user
      const foundUser = mockUsers.find(u => u.email === email && u.password === password);
      
      if (!foundUser) {
        throw new Error('Invalid email or password');
      }
      
      // Create user object (excluding password)
      const { password: _, ...userWithoutPassword } = foundUser;
      user.value = userWithoutPassword;
      
      // Set Authorization header for API requests
      axios.defaults.headers.common['Authorization'] = `Bearer ${user.value.token}`;
      
      // Save to localStorage if remember is true
      if (remember) {
        localStorage.setItem('user', JSON.stringify(user.value));
      }
      
      return user.value;
    } catch (err) {
      error.value = err.message || 'Login failed';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  // Logout function
  const logout = () => {
    user.value = null;
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
  };
  
  // Check if user has required role
  const hasRole = (role) => {
    return user.value && user.value.role === role;
  };
  
  // Check if user is authenticated
  const isAuthenticated = computed(() => {
    return !!user.value;
  });
  
  return {
    user,
    loading,
    error,
    login,
    logout,
    hasRole,
    isAuthenticated
  };
}