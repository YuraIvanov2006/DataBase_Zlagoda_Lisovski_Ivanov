import { api } from './index';

export const customerCardsApi = {
  getAll: () => api.get('/customer-cards'),
  getBySurname: (surname: string) =>
    api.get(`/customer-cards/surname/${encodeURIComponent(surname)}`),
  getByNumber: (cardNumber: string) =>
    api.get(`/customer-cards/${encodeURIComponent(cardNumber)}`),
  create: (body: unknown) => api.post('/customer-cards', body),
  update: (cardNumber: string, body: unknown) =>
    api.put(`/customer-cards/${encodeURIComponent(cardNumber)}`, body),
  delete: (cardNumber: string) =>
    api.delete(`/customer-cards/${encodeURIComponent(cardNumber)}`),
};
