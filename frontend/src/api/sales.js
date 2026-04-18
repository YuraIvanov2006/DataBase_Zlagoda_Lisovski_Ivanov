import { api } from './index.js';

export const salesApi = {
  getAll: () => api.get('/sales'),
  getByCheck: (checkNumber) =>
    api.get(`/sales/check/${encodeURIComponent(checkNumber)}`),
  getByUpc: (upc) => api.get(`/sales/upc/${encodeURIComponent(upc)}`),
  getByEmployee: (employeeId) => api.get(`/sales/employee/${employeeId}`),
  totalSumByCheck: (checkNumber) =>
    api.get(`/sales/check/${encodeURIComponent(checkNumber)}/total-sum`),
  totalSoldByUpc: (upc) =>
    api.get(`/sales/upc/${encodeURIComponent(upc)}/total-sold`),
};
