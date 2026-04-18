import { api } from './index.js';

export const storeProductsApi = {
  getAll: () => api.get('/store-products'),
  getPromotional: () => api.get('/store-products/promotional'),
  getNotPromotional: () => api.get('/store-products/not-promotional'),
  getByUpc: (upc) => api.get(`/store-products/${encodeURIComponent(upc)}`),
  getByProductId: (productId) =>
    api.get(`/store-products/product/${productId}`),
  create: (body) => api.post('/store-products', body),
  update: (upc, body) =>
    api.put(`/store-products/${encodeURIComponent(upc)}`, body),
  delete: (upc) => api.delete(`/store-products/${encodeURIComponent(upc)}`),
};
