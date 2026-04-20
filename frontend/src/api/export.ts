import { ApiError } from './index';

const baseURL =
  import.meta.env.VITE_API_BASE ||
  (import.meta.env.DEV ? '/api/v1' : 'http://localhost:8080/api/v1');

export async function downloadReport(
  path: string,
  filename: string,
  mimeType: string,
  isPreview: boolean
): Promise<void> {
  const url = `${baseURL.replace(/\/$/, '')}${path.startsWith('/') ? path : `/${path}`}`;
  
  const headers: Record<string, string> = {};
  const token = localStorage.getItem('token');
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let res: Response;
  try {
    res = await fetch(url, { headers });
  } catch (e: unknown) {
    throw new ApiError(
      e instanceof Error ? e.message : 'Мережна помилка',
      0,
      null
    );
  }

  if (!res.ok) {
    throw new ApiError(
      res.statusText || 'Помилка сервера',
      res.status,
      null
    );
  }

  const blob = await res.blob();
  const fileBlob = new Blob([blob], { type: mimeType });
  const objectUrl = window.URL.createObjectURL(fileBlob);

  if (isPreview) {
    window.open(objectUrl, '_blank');
  } else {
    const a = document.createElement('a');
    a.href = objectUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(objectUrl);
  }
}
