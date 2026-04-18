import styles from './SearchBar.module.css';

export function SearchBar({
  value,
  onChange,
  placeholder,
  onSubmit,
  buttonLabel = 'Пошук',
}) {
  return (
    <form
      className={styles.form}
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit?.();
      }}
    >
      <input
        className={styles.input}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
      />
      {onSubmit && (
        <button type="submit" className="btn secondary">
          {buttonLabel}
        </button>
      )}
    </form>
  );
}
