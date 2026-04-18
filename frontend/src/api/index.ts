/**
 * Базовий HTTP-клієнт на fetch (без axios — менше залежностей і проблем з lockfile).
 * За замовчуванням — відносний шлях (працює з Vite proxy на бекенд).
 * Повний URL: `VITE_API_BASE=http://localhost:8080/api/v1`
 */
const baseURL =
  import.meta.env.VITE_API_BASE ||
  (import.meta.env.DEV ? '/api/v1' : 'http://localhost:8080/api/v1');

function joinBaseAndPath(path: string): string {
  const b = baseURL.replace(/\/$/, '');
  const p = path.startsWith('/') ? path : `/${path}`;
  return `${b}${p}`;
}

export class ApiError extends Error {
  readonly status: number;
  readonly data: unknown;

  constructor(message: string, status: number, data: unknown) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

type RequestConfig = {
  params?: Record<string, string | number | boolean | undefined | null>;
  body?: unknown;
};

function isRecord(v: unknown): v is Record<string, unknown> {
  return typeof v === 'object' && v !== null;
}

function parseErrorBody(data: unknown): string {
  if (typeof data === 'string') return data;
  if (isRecord(data)) {
    if (typeof data.message === 'string') return data.message;
    if (Array.isArray(data.errors)) return data.errors.map(String).join('; ');
    if (typeof data.error === 'string') return data.error;
  }
  return 'Помилка сервера';
}

export function getApiErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (!error.status) return error.message || 'Мережна помилка';
    return parseErrorBody(error.data) || error.message || 'Помилка сервера';
  }
  if (error instanceof TypeError) return error.message || 'Мережна помилка';
  if (error instanceof Error) return error.message;
  return 'Мережна помилка';
}

async function request<T>(
  method: string,
  path: string,
  config?: RequestConfig
): Promise<{ data: T }> {
  let url = joinBaseAndPath(path);
  if (config?.params) {
    const search = new URLSearchParams();
    for (const [k, v] of Object.entries(config.params)) {
      if (v != null && v !== '') search.set(k, String(v));
    }
    const q = search.toString();
    if (q) url += (url.includes('?') ? '&' : '?') + q;
  }

  const headers: Record<string, string> = { Accept: 'application/json' };
  const token = localStorage.getItem('token');
  if (token) headers.Authorization = `Bearer ${token}`;

  const init: RequestInit = { method, headers };
  if (config?.body != null && method !== 'GET' && method !== 'HEAD') {
    headers['Content-Type'] = 'application/json';
    init.body = JSON.stringify(config.body);
  }

  let res: Response;
  try {
    res = await fetch(url, init);
  } catch (e: unknown) {
    throw new ApiError(
      e instanceof Error ? e.message : 'Мережна помилка',
      0,
      null
    );
  }

  const ct = res.headers.get('content-type') || '';
  const text = await res.text();
  let raw: unknown = text;
  if (
    text &&
    (ct.includes('application/json') ||
      ct.includes('application/problem+json') ||
      /^\s*[\[{]/.test(text))
  ) {
    try {
      raw = JSON.parse(text) as unknown;
    } catch {
      raw = text;
    }
  } else if (!text) {
    raw = null;
  }

  if (!res.ok) {
    throw new ApiError(
      res.statusText || 'Помилка сервера',
      res.status,
      raw
    );
  }

  return { data: raw as T };
}

export const api = {
  get: <T>(path: string, config?: { params?: RequestConfig['params'] }) =>
    request<T>('GET', path, config),

  post: <T>(path: string, body?: unknown) =>
    request<T>('POST', path, { body }),

  put: <T>(path: string, body?: unknown) =>
    request<T>('PUT', path, { body }),

  delete: <T>(path: string) => request<T>('DELETE', path),
};
