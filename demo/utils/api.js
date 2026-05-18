import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Change this to your actual backend URL
// For iPhone: Use your machine's IP address (e.g., http://192.168.x.x:5001)
// For Android emulator: 'http://10.0.2.2:5001'
// For production: 'https://your-api-domain.com'
const BASE_URL = 'http://192.168.100.155:5001'; // Replace 192.168.100.155 with your actual IP if needed

const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
});

// Add token to requests
apiClient.interceptors.request.use(async (config) => {
  try {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch (error) {
    console.error('Error getting token:', error);
  }
  return config;
});

// Handle response errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired, clear storage
      await AsyncStorage.removeItem('token');
      await AsyncStorage.removeItem('userId');
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  register: (data) => apiClient.post('/api/auth/register', data),
  login: (data) => apiClient.post('/api/auth/login', data),
};

export const heartRateAPI = {
  create: (data) => apiClient.post('/api/heart-rates', data),
  getAll: () => apiClient.get('/api/heart-rates'),
  getStats: (period) => apiClient.get('/api/heart-rates/stats', { params: { period } }),
};

export const profileAPI = {
  get: (userId) => apiClient.get(`/api/profile/${userId}`),
  update: (userId, data) => apiClient.put(`/api/profile/${userId}`, data),
};

export const insightAPI = {
  get: () => apiClient.get('/api/insight'),
};

export default apiClient;
