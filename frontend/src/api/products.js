import { api } from './index.js';

export const productsApi = {
  getAll: () => api.get('/products'),
  getOrderedByName: () => api.get('/products/ordered'),
  getById: (id) => api.get(`/products/${id}`),
  getByCategory: (categoryNumber) =>
    api.get(`/products/category/${categoryNumber}`),
  getByName: (name) => api.get(`/products/name/${encodeURIComponent(name)}`),
  create: (body) => api.post('/products', body),
  update: (id, body) => api.put(`/products/${id}`, body),
  delete: (id) => api.delete(`/products/${id}`),
};
