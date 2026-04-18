import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  useEffect,
  type ReactNode,
} from 'react';
import { login as apiLogin } from '../api/auth';
import { employeesApi } from '../api/employees';
import { getApiErrorMessage } from '../api/index';

export type UserRole = 'manager' | 'cashier';

type AuthSession = {
  token: string;
  role: UserRole;
  employeeId: number;
  login: string;
  employeeName: string;
};

type AuthContextValue = {
  token: string | null;
  role: UserRole | null;
  employeeId: number | null;
  accountLogin: string | null;
  employeeName: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string;
  signIn: (args: {
    login: string;
    password: string;
    employeeId: number;
  }) => Promise<AuthSession>;
  logout: () => void;
  devBootstrap: (empId: number | string, username?: string) => Promise<void>;
  setError: (s: string) => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

const STORAGE_KEYS = ['token', 'role', 'employeeId', 'login', 'employeeName'];

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem('token')
  );
  const [role, setRole] = useState<UserRole | null>(() => {
    const r = localStorage.getItem('role');
    return r === 'manager' || r === 'cashier' ? r : null;
  });
  const [employeeId, setEmployeeId] = useState<string | null>(() =>
    localStorage.getItem('employeeId')
  );
  const [accountLogin, setAccountLogin] = useState<string | null>(() =>
    localStorage.getItem('login')
  );
  const [employeeName, setEmployeeName] = useState<string | null>(() =>
    localStorage.getItem('employeeName')
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const persist = useCallback((session: Partial<AuthSession> & { token?: string | null }) => {
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
        const t = (data as { token?: string } | undefined)?.token;
        if (!t) throw new Error('Токен не отримано');

        localStorage.setItem('token', t);
        setToken(t);

        const id = empId != null ? Number(empId) : NaN;
        if (!id || Number.isNaN(id)) {
          throw new Error('Вкажіть коректний ID працівника');
        }

        const empRes = await employeesApi.getById(id);
        const emp = empRes.data as {
          emplRole?: string;
          fullName?: string;
        };
        const r = (emp.emplRole || '').toLowerCase();
        if (r !== 'manager' && r !== 'cashier') {
          throw new Error('Невідома роль у профілі працівника');
        }
        const userRole = r as UserRole;

        const session: AuthSession = {
          token: t,
          role: userRole,
          employeeId: id,
          login: username,
          employeeName: emp.fullName || username,
        };
        persist(session);
        setRole(userRole);
        setEmployeeId(String(id));
        setAccountLogin(username);
        setEmployeeName(session.employeeName);
        return session;
      } catch (e: unknown) {
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

  const devBootstrap = useCallback(
    async (empId: number | string, username?: string) => {
      const id = Number(empId);
      if (!localStorage.getItem('token')) return;
      const empRes = await employeesApi.getById(id);
      const emp = empRes.data as { emplRole?: string; fullName?: string };
      const r = (emp.emplRole || '').toLowerCase();
      const userRole = (r === 'manager' || r === 'cashier' ? r : 'cashier') as UserRole;
      persist({
        token: localStorage.getItem('token')!,
        role: userRole,
        employeeId: id,
        login: username || String(id),
        employeeName: emp.fullName || '',
      });
      setRole(userRole);
      setEmployeeId(String(id));
      setAccountLogin(username || String(id));
      setEmployeeName(emp.fullName || '');
    },
    [persist]
  );

  useEffect(() => {
    setToken(localStorage.getItem('token'));
    const r = localStorage.getItem('role');
    setRole(r === 'manager' || r === 'cashier' ? r : null);
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

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
