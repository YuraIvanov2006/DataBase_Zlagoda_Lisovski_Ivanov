import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  useEffect,
} from 'react';
import { login as apiLogin } from '../api/auth.js';
import { employeesApi } from '../api/employees.js';
import { getApiErrorMessage } from '../api/index.js';

const AuthContext = createContext(null);

const STORAGE_KEYS = ['token', 'role', 'employeeId', 'login', 'employeeName'];

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [role, setRole] = useState(() => localStorage.getItem('role'));
  const [employeeId, setEmployeeId] = useState(() =>
    localStorage.getItem('employeeId')
  );
  const [accountLogin, setAccountLogin] = useState(() =>
    localStorage.getItem('login')
  );
  const [employeeName, setEmployeeName] = useState(() =>
    localStorage.getItem('employeeName')
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const persist = useCallback((session) => {
    if (session.token) localStorage.setItem('token', session.token);
    else localStorage.removeItem('token');
    if (session.role) localStorage.setItem('role', session.role);
    else localStorage.removeItem('role');
    if (session.employeeId != null)
      localStorage.setItem('employeeId', String(session.employeeId));
    else localStorage.removeItem('employeeId');
    if (session.login) localStorage.setItem('login', session.login);
    else localStorage.removeItem('login');
    if (session.employeeName)
      localStorage.setItem('employeeName', session.employeeName);
    else localStorage.removeItem('employeeName');
  }, []);

  const logout = useCallback(() => {
    STORAGE_KEYS.forEach((k) => localStorage.removeItem(k));
    setToken(null);
    setRole(null);
    setEmployeeId(null);
    setAccountLogin(null);
    setEmployeeName(null);
  }, []);

  const signIn = useCallback(
    async ({ login: username, password, employeeId: empId }) => {
      setLoading(true);
      setError('');
      try {
        const { data } = await apiLogin({ login: username, password });
        const t = data?.token;
        if (!t) throw new Error('Токен не отримано');

        localStorage.setItem('token', t);
        setToken(t);

        const id = empId != null ? Number(empId) : null;
        if (!id || Number.isNaN(id)) {
          throw new Error('Вкажіть коректний ID працівника');
        }

        const empRes = await employeesApi.getById(id);
        const emp = empRes.data;
        const r = (emp.emplRole || '').toLowerCase();
        if (r !== 'manager' && r !== 'cashier') {
          throw new Error('Невідома роль у профілі працівника');
        }

        const session = {
          token: t,
          role: r,
          employeeId: id,
          login: username,
          employeeName: emp.fullName || username,
        };
        persist(session);
        setRole(r);
        setEmployeeId(String(id));
        setAccountLogin(username);
        setEmployeeName(session.employeeName);
        return session;
      } catch (e) {
        const msg = getApiErrorMessage(e);
        setError(msg);
        logout();
        throw e;
      } finally {
        setLoading(false);
      }
    },
    [logout, persist]
  );

  /** Для розробки: одразу встановити сесію після ручного логіну через API */
  const devBootstrap = useCallback(
    async (empId, username) => {
      const id = Number(empId);
      if (!localStorage.getItem('token')) return;
      const empRes = await employeesApi.getById(id);
      const emp = empRes.data;
      const r = (emp.emplRole || '').toLowerCase();
      persist({
        token: localStorage.getItem('token'),
        role: r,
        employeeId: id,
        login: username || String(id),
        employeeName: emp.fullName || '',
      });
      setRole(r);
      setEmployeeId(String(id));
      setAccountLogin(username || String(id));
      setEmployeeName(emp.fullName || '');
    },
    [persist]
  );

  useEffect(() => {
    setToken(localStorage.getItem('token'));
    setRole(localStorage.getItem('role'));
    setEmployeeId(localStorage.getItem('employeeId'));
    setAccountLogin(localStorage.getItem('login'));
    setEmployeeName(localStorage.getItem('employeeName'));
  }, []);

  const value = useMemo(
    () => ({
      token,
      role,
      employeeId: employeeId ? Number(employeeId) : null,
      accountLogin,
      employeeName,
      isAuthenticated: Boolean(token && role && employeeId),
      loading,
      error,
      signIn,
      logout,
      devBootstrap,
      setError,
    }),
    [
      token,
      role,
      employeeId,
      accountLogin,
      employeeName,
      loading,
      error,
      signIn,
      logout,
      devBootstrap,
    ]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
