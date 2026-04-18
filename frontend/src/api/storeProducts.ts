import { api } from './index';

export const storeProductsApi = {
  getAll: () => api.get('/store-products'),
  getPromotional: () => api.get('/store-products/promotional'),
  getNotPromotional: () => api.get('/store-products/not-promotional'),
  getByUpc: (upc: string) =>
    api.get(`/store-products/${encodeURIComponent(upc)}`),
  getByProductId: (productId: number) =>
    api.get(`/store-products/product/${productId}`),
  create: (body: unknown) => api.post('/store-products', body),
  update: (upc: string, body: unknown) =>
    api.put(`/store-products/${encodeURIComponent(upc)}`, body),
  delete: (upc: string) =>
    api.delete(`/store-products/${encodeURIComponent(upc)}`),
};
