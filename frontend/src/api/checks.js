import { api } from './index.js';

export const checksApi = {
  getAll: () => api.get('/checks'),
  getByNumber: (checkNumber) =>
    api.get(`/checks/${encodeURIComponent(checkNumber)}`),
  create: (body) => api.post('/checks', body),
  update: (checkNumber, body) =>
    api.put(`/checks/${encodeURIComponent(checkNumber)}`, body),
  delete: (checkNumber) =>
    api.delete(`/checks/${encodeURIComponent(checkNumber)}`),
};
