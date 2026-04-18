import { api } from './index.js';

export const categoriesApi = {
  getAll: () => api.get('/categories'),
  getById: (categoryNumber) => api.get(`/categories/${categoryNumber}`),
  create: (body) => api.post('/categories', body),
  update: (categoryNumber, body) =>
    api.put(`/categories/${categoryNumber}`, body),
  delete: (categoryNumber) => api.delete(`/categories/${categoryNumber}`),
};
