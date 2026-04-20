import { useState, type FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getApiErrorMessage } from '../../api/index';
import { register } from '../../api/auth';
import { Spinner } from '../../components/Spinner';
import styles from './LoginPage.module.css';

export function LoginPage() {
  const { isAuthenticated, signIn, role, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const [isRegistering, setIsRegistering] = useState(false);
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [idEmployee, setIdEmployee] = useState('');
  const [localError, setLocalError] = useState('');
  const [localLoading, setLocalLoading] = useState(false);

  const loading = authLoading || localLoading;

  const devManagerLogin = import.meta.env.VITE_DEV_MANAGER_LOGIN || '';
  const devManagerPass = import.meta.env.VITE_DEV_MANAGER_PASSWORD || '';
  const devCashierLogin = import.meta.env.VITE_DEV_CASHIER_LOGIN || '';
  const devCashierPass = import.meta.env.VITE_DEV_CASHIER_PASSWORD || '';

  if (isAuthenticated) {
    return (
      <Navigate
        to={role === 'manager' ? '/manager/employees' : '/cashier/products'}
        replace
      />
    );
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLocalError('');
    
    if (isRegistering) {
      setLocalLoading(true);
      try {
        await register({
          idEmployee: Number(idEmployee),
          login: login.trim(),
          password,
        });
        // After successful registration, log in automatically
        const session = await signIn({
          login: login.trim(),
          password,
        });
        navigate(
          session.role === 'manager'
            ? '/manager/employees'
            : '/cashier/products',
          { replace: true }
        );
      } catch (err: unknown) {
        setLocalError(getApiErrorMessage(err));
      } finally {
        setLocalLoading(false);
      }
    } else {
      try {
        const session = await signIn({
          login: login.trim(),
          password,
        });
        navigate(
          session.role === 'manager'
            ? '/manager/employees'
            : '/cashier/products',
          { replace: true }
        );
      } catch (err: unknown) {
        setLocalError(getApiErrorMessage(err));
      }
    }
  };

  const fillDev = (preset: 'manager' | 'cashier') => {
    if (preset === 'manager' && devManagerLogin) {
      setLogin(devManagerLogin);
      setPassword(devManagerPass);
    }
    if (preset === 'cashier' && devCashierLogin) {
      setLogin(devCashierLogin);
      setPassword(devCashierPass);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <h1 className={styles.title}>ZLAGODA</h1>
        <p className={styles.sub}>{isRegistering ? 'Реєстрація' : 'Вхід у систему'}</p>
        {localError && <div className="alert error">{localError}</div>}
        {loading && <Spinner />}
        <form className={styles.form} onSubmit={onSubmit}>
          {isRegistering && (
            <label className={styles.label}>
              ID працівника
              <input
                className={styles.input}
                type="number"
                value={idEmployee}
                onChange={(e) => setIdEmployee(e.target.value)}
                required
              />
            </label>
          )}
          <label className={styles.label}>
            Логін облікового запису
            <input
              className={styles.input}
              value={login}
              onChange={(e) => setLogin(e.target.value)}
              autoComplete="username"
              required
            />
          </label>
          <label className={styles.label}>
            Пароль
            <input
              className={styles.input}
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </label>
          <button type="submit" className="btn primary" disabled={loading}>
            {isRegistering ? 'Зареєструватися' : 'Увійти'}
          </button>
          
          <div className={styles.toggleText}>
            {isRegistering ? 'Вже маєте обліковий запис? ' : 'Немає облікового запису? '}
            <button 
              type="button" 
              className={styles.toggleBtn}
              onClick={() => {
                setIsRegistering(!isRegistering);
                setLocalError('');
              }}
            >
              {isRegistering ? 'Увійти' : 'Зареєструватися'}
            </button>
          </div>
        </form>
        {import.meta.env.DEV && (
          <div className={styles.dev}>
            <div className={styles.devTitle}>Швидкий вибір (dev, .env)</div>
            <div className={styles.devBtns}>
              <button
                type="button"
                className="btn secondary small"
                onClick={() => fillDev('manager')}
                disabled={!devManagerLogin}
              >
                Менеджер
              </button>
              <button
                type="button"
                className="btn secondary small"
                onClick={() => fillDev('cashier')}
                disabled={!devCashierLogin}
              >
                Касир
              </button>
            </div>
            <p className={styles.hint}>
              Задайте у <code>.env.local</code> змінні{' '}
              <code>VITE_DEV_MANAGER_LOGIN</code>,{' '}
              <code>VITE_DEV_MANAGER_PASSWORD</code>,{' '}
              <code>VITE_DEV_CASHIER_LOGIN</code>,{' '}
              <code>VITE_DEV_CASHIER_PASSWORD</code>.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
