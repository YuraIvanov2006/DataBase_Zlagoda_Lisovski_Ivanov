import { api } from './index';

export const employeesApi = {
  getAll: () => api.get('/employees'),
  getOrderedBySurname: () => api.get('/employees/ordered'),
  getCashiers: () => api.get('/employees/cashiers'),
  getById: (id: number) => api.get(`/employees/${id}`),
  searchBySurname: (surname: string) =>
    api.get('/employees/search/surname', { params: { surname } }),
  create: (body: unknown) => api.post('/employees', body),
  update: (id: number, body: unknown) => api.put(`/employees/${id}`, body),
  delete: (id: number) => api.delete(`/employees/${id}`),
};
