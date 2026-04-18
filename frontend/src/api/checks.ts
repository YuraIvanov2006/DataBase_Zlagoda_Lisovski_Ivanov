import { api } from './index';

export const checksApi = {
  getAll: () => api.get('/checks'),
  getByNumber: (checkNumber: string) =>
    api.get(`/checks/${encodeURIComponent(checkNumber)}`),
  create: (body: unknown) => api.post('/checks', body),
  update: (checkNumber: string, body: unknown) =>
    api.put(`/checks/${encodeURIComponent(checkNumber)}`, body),
  delete: (checkNumber: string) =>
    api.delete(`/checks/${encodeURIComponent(checkNumber)}`),
};
