import axios, { type AxiosError } from 'axios';

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

function isRecord(v: unknown): v is Record<string, unknown> {
  return typeof v === 'object' && v !== null;
}

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const ax = error as AxiosError<string | Record<string, unknown>>;
    if (!ax.response) return ax.message || 'Мережна помилка';
    const d = ax.response.data;
    if (typeof d === 'string') return d;
    if (isRecord(d)) {
      if (typeof d.message === 'string') return d.message;
      if (Array.isArray(d.errors))
        return d.errors.map(String).join('; ');
      if (typeof d.error === 'string') return d.error;
    }
    return ax.response.statusText || 'Помилка сервера';
  }
  if (error instanceof Error) return error.message;
  return 'Мережна помилка';
}
