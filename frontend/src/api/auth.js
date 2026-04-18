import { api } from './index.js';

export function login({ login: username, password }) {
  return api.post('/auth/login', { login: username, password });
}
