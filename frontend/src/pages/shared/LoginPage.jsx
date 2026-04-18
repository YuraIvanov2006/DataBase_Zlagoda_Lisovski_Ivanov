import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { getApiErrorMessage } from '../../api/index.js';
import { Spinner } from '../../components/Spinner.jsx';
import styles from './LoginPage.module.css';

export function LoginPage() {
  const { isAuthenticated, signIn, role, loading } = useAuth();
  const navigate = useNavigate();
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [employeeId, setEmployeeId] = useState('');
  const [localError, setLocalError] = useState('');

  const devManagerLogin = import.meta.env.VITE_DEV_MANAGER_LOGIN || '';
  const devManagerPass = import.meta.env.VITE_DEV_MANAGER_PASSWORD || '';
  const devManagerId = import.meta.env.VITE_DEV_MANAGER_ID || '';
  const devCashierLogin = import.meta.env.VITE_DEV_CASHIER_LOGIN || '';
  const devCashierPass = import.meta.env.VITE_DEV_CASHIER_PASSWORD || '';
  const devCashierId = import.meta.env.VITE_DEV_CASHIER_ID || '';

  if (isAuthenticated) {
    return (
      <Navigate
        to={role === 'manager' ? '/manager/employees' : '/cashier/products'}
        replace
      />
    );
  }

  const onSubmit = async (e) => {
    e.preventDefault();
    setLocalError('');
    try {
      const session = await signIn({
        login: login.trim(),
        password,
        employeeId: Number(employeeId),
      });
      navigate(
        session.role === 'manager'
          ? '/manager/employees'
          : '/cashier/products',
        { replace: true }
      );
    } catch (err) {
      setLocalError(getApiErrorMessage(err));
    }
  };

  const fillDev = (preset) => {
    if (preset === 'manager' && devManagerLogin) {
      setLogin(devManagerLogin);
      setPassword(devManagerPass);
      setEmployeeId(devManagerId);
    }
    if (preset === 'cashier' && devCashierLogin) {
      setLogin(devCashierLogin);
      setPassword(devCashierPass);
      setEmployeeId(devCashierId);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <h1 className={styles.title}>ZLAGODA</h1>
        <p className={styles.sub}>Вхід у систему</p>
        {localError && <div className="alert error">{localError}</div>}
        {loading && <Spinner />}
        <form className={styles.form} onSubmit={onSubmit}>
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
            ID працівника
            <input
              className={styles.input}
              value={employeeId}
              onChange={(e) => setEmployeeId(e.target.value)}
              inputMode="numeric"
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
            Увійти
          </button>
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
              <code>VITE_DEV_MANAGER_ID</code> (і аналогічно для касира).
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
