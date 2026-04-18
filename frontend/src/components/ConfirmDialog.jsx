import { Modal } from './Modal.jsx';
import styles from './ConfirmDialog.module.css';

export function ConfirmDialog({
  title = 'Підтвердження',
  message,
  confirmLabel = 'Так',
  cancelLabel = 'Скасувати',
  danger,
  onConfirm,
  onCancel,
}) {
  return (
    <Modal title={title} onClose={onCancel}>
      <p className={styles.msg}>{message}</p>
      <div className={styles.actions}>
        <button type="button" className="btn secondary" onClick={onCancel}>
          {cancelLabel}
        </button>
        <button
          type="button"
          className={`btn ${danger ? 'danger' : 'primary'}`}
          onClick={onConfirm}
        >
          {confirmLabel}
        </button>
      </div>
    </Modal>
  );
}
