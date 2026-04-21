import { NavLink } from 'react-router-dom';
import type { UserRole } from '../context/AuthContext';
import styles from './Sidebar.module.css';

const managerLinks = [
  { to: '/manager/employees', label: 'Працівники' },
  { to: '/manager/products', label: 'Товари' },
  { to: '/manager/store-items', label: 'Товар у магазині' },
  { to: '/manager/categories', label: 'Категорії' },
  { to: '/manager/client-cards', label: 'Картки клієнтів' },
  { to: '/manager/receipts', label: 'Чеки' },
  { to: '/manager/reports', label: 'Звіти' },
  { to: '/manager/complex-queries', label: 'Складні запити' },
];

const cashierLinks = [
  { to: '/cashier/products', label: 'Товари' },
  { to: '/cashier/store-items', label: 'Товар у магазині' },
  { to: '/cashier/client-cards', label: 'Картки клієнтів' },
  { to: '/cashier/sale', label: 'Продаж' },
  { to: '/cashier/receipts', label: 'Мої чеки' },
  { to: '/cashier/profile', label: 'Мій профіль' },
];

type SidebarProps = {
  role: UserRole | null;
  open: boolean;
  onClose: () => void;
};

export function Sidebar({ role, open, onClose }: SidebarProps) {
  const links = role === 'manager' ? managerLinks : cashierLinks;

  return (
    <>
      <aside className={`${styles.aside} ${open ? styles.open : ''}`}>
        <div className={styles.brand}>ZLAGODA</div>
        <nav className={styles.nav}>
          {links.map((l) => (
            <NavLink
              key={l.to}
              to={l.to}
              className={({ isActive }) =>
                `${styles.link} ${isActive ? styles.active : ''}`
              }
              onClick={onClose}
            >
              {l.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      {open && (
        <button
          type="button"
          className={styles.scrim}
          aria-label="Закрити меню"
          onClick={onClose}
        />
      )}
    </>
  );
}
