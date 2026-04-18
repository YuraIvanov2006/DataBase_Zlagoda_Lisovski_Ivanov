import styles from './Spinner.module.css';

export function Spinner({ label = 'Завантаження…' }) {
  return (
    <div className={styles.wrap} role="status">
      <div className={styles.spinner} aria-hidden />
      <span className={styles.label}>{label}</span>
    </div>
  );
}
