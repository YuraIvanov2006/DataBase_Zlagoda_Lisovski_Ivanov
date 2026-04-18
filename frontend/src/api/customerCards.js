import { api } from './index.js';

export const customerCardsApi = {
  getAll: () => api.get('/customer-cards'),
  getBySurname: (surname) =>
    api.get(`/customer-cards/surname/${encodeURIComponent(surname)}`),
  getByNumber: (cardNumber) =>
    api.get(`/customer-cards/${encodeURIComponent(cardNumber)}`),
  create: (body) => api.post('/customer-cards', body),
  update: (cardNumber, body) =>
    api.put(`/customer-cards/${encodeURIComponent(cardNumber)}`, body),
  delete: (cardNumber) =>
    api.delete(`/customer-cards/${encodeURIComponent(cardNumber)}`),
};
