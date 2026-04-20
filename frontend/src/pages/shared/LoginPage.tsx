import { useState, type FormEvent } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getApiErrorMessage } from "../../api/index";
import { Spinner } from "../../components/Spinner";
import styles from "./LoginPage.module.css";

export function LoginPage() {
  const { isAuthenticated, signIn, role, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [localError, setLocalError] = useState("");
  const [localLoading, setLocalLoading] = useState(false);

  const loading = authLoading || localLoading;


  if (isAuthenticated) {
    return (
      <Navigate
        to={role === "manager" ? "/manager/employees" : "/cashier/products"}
        replace
      />
    );
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLocalError("");
    setLocalLoading(true);
    try {
      const session = await signIn({ login: login.trim(), password });
      navigate(
        session.role === "manager" ? "/manager/employees" : "/cashier/products",
        { replace: true },
      );
    } catch (err: unknown) {
      setLocalError(getApiErrorMessage(err));
    } finally {
      setLocalLoading(false);
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
            Логін
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
            Увійти
          </button>
        </form>


      </div>
    </div>
  );
}
