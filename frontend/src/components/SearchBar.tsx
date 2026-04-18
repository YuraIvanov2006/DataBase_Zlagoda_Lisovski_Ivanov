import styles from './SearchBar.module.css';

type SearchBarProps = {
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  onSubmit?: () => void;
  buttonLabel?: string;
};

export function SearchBar({
  value,
  onChange,
  placeholder,
  onSubmit,
  buttonLabel = 'Пошук',
}: SearchBarProps) {
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
