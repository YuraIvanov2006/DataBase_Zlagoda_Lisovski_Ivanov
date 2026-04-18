import { Modal } from './Modal';
import styles from './ConfirmDialog.module.css';

type ConfirmDialogProps = {
  title?: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
};

export function ConfirmDialog({
  title = 'Підтвердження',
  message,
  confirmLabel = 'Так',
  cancelLabel = 'Скасувати',
  danger,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
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
