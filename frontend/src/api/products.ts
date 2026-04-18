import { api } from './index';

export const productsApi = {
  getAll: () => api.get('/products'),
  getOrderedByName: () => api.get('/products/ordered'),
  getById: (id: number) => api.get(`/products/${id}`),
  getByCategory: (categoryNumber: number) =>
    api.get(`/products/category/${categoryNumber}`),
  getByName: (name: string) =>
    api.get(`/products/name/${encodeURIComponent(name)}`),
  create: (body: unknown) => api.post('/products', body),
  update: (id: number, body: unknown) => api.put(`/products/${id}`, body),
  delete: (id: number) => api.delete(`/products/${id}`),
};
