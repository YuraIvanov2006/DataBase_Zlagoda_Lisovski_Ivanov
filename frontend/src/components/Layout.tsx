import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sidebar } from './Sidebar';
import styles from './Layout.module.css';

export function Layout() {
  const { employeeName, role, employeeId, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className={styles.root}>
      <Sidebar
        role={role}
        open={menuOpen}
        onClose={() => setMenuOpen(false)}
      />
      <div className={styles.main}>
        <header className={styles.header}>
          <button
            type="button"
            className={styles.menuBtn}
            aria-label="Меню"
            onClick={() => setMenuOpen(true)}
          >
            ☰
          </button>
          <div className={styles.user}>
            <span className={styles.name}>{employeeName || '—'}</span>
            <span className={styles.meta}>
              {role === 'manager' ? 'Менеджер' : 'Касир'} · ID {employeeId}
            </span>
          </div>
          <button type="button" className="btn secondary" onClick={logout}>
            Вийти
          </button>
        </header>
        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
