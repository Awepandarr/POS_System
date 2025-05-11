<template>
    <div class="spinner">
      <div class="spinner-inner"></div>
    </div>
  </template>
  
  <style scoped>
  .spinner {
    display: inline-block;
    position: relative;
    width: 24px;
    height: 24px;
  }
  
  .spinner-inner {
    width: 100%;
    height: 100%;
    border: 3px solid rgba(0, 0, 0, 0.1);
    border-top: 3px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  </style>
  
  <script>
  export default {
    name: 'Spinner'
  }
  </script>