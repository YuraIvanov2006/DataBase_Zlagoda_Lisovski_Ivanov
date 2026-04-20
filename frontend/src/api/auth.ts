import { api } from "./index";

export function login({
  login: username,
  password,
}: {
  login: string;
  password: string;
}) {
  return api.post("/auth/login", { login: username, password });
}

export function me() {
  return api.get("/auth/me");
}

// Використовується менеджером при створенні працівника
export function register({
  idEmployee,
  login: username,
  password,
}: {
  idEmployee: number;
  login: string;
  password: string;
}) {
  return api.post("/auth/register", { idEmployee, login: username, password });
}
