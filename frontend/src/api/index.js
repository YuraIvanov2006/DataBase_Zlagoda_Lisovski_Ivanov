import axios from 'axios';

/**
 * Базовий інстанс. За замовчуванням — відносний шлях (працює з Vite proxy на бекенд).
 * Можна задати повний URL: `VITE_API_BASE=http://localhost:8080/api/v1`
 * У завданні згадувалось `http://localhost:3000/api` — підлаштуйте .env під свій порт.
 */
const baseURL =
  import.meta.env.VITE_API_BASE ||
  (import.meta.env.DEV ? '/api/v1' : 'http://localhost:8080/api/v1');

export const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export function getApiErrorMessage(error) {
  if (!error?.response) return error?.message || 'Мережна помилка';
  const d = error.response.data;
  if (typeof d === 'string') return d;
  if (d?.message) return d.message;
  if (Array.isArray(d?.errors)) return d.errors.join('; ');
  if (d?.error) return d.error;
  return error.response.statusText || 'Помилка сервера';
}
