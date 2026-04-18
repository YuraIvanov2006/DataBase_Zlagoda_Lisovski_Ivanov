import styles from './Spinner.module.css';

type SpinnerProps = {
  label?: string;
};

export function Spinner({ label = 'Завантаження…' }: SpinnerProps) {
  return (
    <div className={styles.wrap} role="status">
      <div className={styles.spinner} aria-hidden />
      <span className={styles.label}>{label}</span>
    </div>
  );
}
