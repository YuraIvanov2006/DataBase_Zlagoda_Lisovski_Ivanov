import { api } from './index.js';

export const employeesApi = {
  getAll: () => api.get('/employees'),
  getOrderedBySurname: () => api.get('/employees/ordered'),
  getCashiers: () => api.get('/employees/cashiers'),
  getById: (id) => api.get(`/employees/${id}`),
  searchBySurname: (surname) =>
    api.get('/employees/search/surname', { params: { surname } }),
  create: (body) => api.post('/employees', body),
  update: (id, body) => api.put(`/employees/${id}`, body),
  delete: (id) => api.delete(`/employees/${id}`),
};
