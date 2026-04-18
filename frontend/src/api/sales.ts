import { api } from './index';

export const salesApi = {
  getAll: () => api.get('/sales'),
  getByCheck: (checkNumber: string) =>
    api.get(`/sales/check/${encodeURIComponent(checkNumber)}`),
  getByUpc: (upc: string) => api.get(`/sales/upc/${encodeURIComponent(upc)}`),
  getByEmployee: (employeeId: number) =>
    api.get(`/sales/employee/${employeeId}`),
  totalSumByCheck: (checkNumber: string) =>
    api.get(`/sales/check/${encodeURIComponent(checkNumber)}/total-sum`),
  totalSoldByUpc: (upc: string) =>
    api.get(`/sales/upc/${encodeURIComponent(upc)}/total-sold`),
};
