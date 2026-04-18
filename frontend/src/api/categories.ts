import { api } from './index';

export const categoriesApi = {
  getAll: () => api.get('/categories'),
  getById: (categoryNumber: number) =>
    api.get(`/categories/${categoryNumber}`),
  create: (body: unknown) => api.post('/categories', body),
  update: (categoryNumber: number, body: unknown) =>
    api.put(`/categories/${categoryNumber}`, body),
  delete: (categoryNumber: number) =>
    api.delete(`/categories/${categoryNumber}`),
};
