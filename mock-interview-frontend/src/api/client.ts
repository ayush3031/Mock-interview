// src/api/client.ts
import axios from 'axios';

// Single configured instance — used everywhere in the app.
// baseURL comes from VITE_API_URL in .env (dev) / .env.production (prod).
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  timeout: 15000,
});

// Optional but useful: log server errors in one place
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error(
      'API error:',
      error.response?.status,
      error.response?.data ?? error.message
    );
    return Promise.reject(error);
  }
);