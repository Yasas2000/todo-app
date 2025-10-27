import axios from 'axios';

/**
 * Axios instance for API communication.
 * Uses different base URL for development vs production.
 */
const api = axios.create({
  // In production (Docker), nginx proxies /api to backend
  // In development, use direct backend URL
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request interceptor for adding authentication tokens if needed
 */
api.interceptors.request.use(
  (config) => {
    // config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor for global error handling
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;