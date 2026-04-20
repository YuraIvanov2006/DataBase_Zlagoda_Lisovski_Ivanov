import { useState } from 'react';
import { getApiErrorMessage } from '../../api/index';
import { downloadReport } from '../../api/export';
import { Spinner } from '../../components/Spinner';

const REPORT_ENTITIES = [
  { id: 'employees', label: 'Працівники' },
  { id: 'customers', label: 'Постійні клієнти' },
  { id: 'categories', label: 'Категорії товарів' },
  { id: 'products', label: 'Товари' },
  { id: 'store-products', label: 'Товар у магазині' },
  { id: 'checks', label: 'Чеки' },
];

export function ManagerReportsPage() {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const handleExport = async (entityId: string, format: 'pdf' | 'excel') => {
    setBusy(true);
    setError('');
    try {
      if (format === 'pdf') {
        await downloadReport(
          `/reports/${entityId}/pdf`,
          `${entityId}.pdf`,
          'application/pdf',
          true
        );
      } else {
        await downloadReport(
          `/reports/${entityId}/excel`,
          `${entityId}.xlsx`,
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          false
        );
      }
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <h1>Звіти та експорт</h1>
      {error && <div className="alert error">{error}</div>}
      {busy && <Spinner label="Формування…" />}
      <p style={{ color: 'var(--muted)', maxWidth: 640 }}>
        Ви можете експортувати дані у формат Excel або попередньо переглянути їх у форматі PDF (для подальшого друку). Звіти містять фірмові колонтитули та відформатовані таблиці без зайвих системних посилань.
      </p>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: 700, marginTop: '1.5rem' }}>
        {REPORT_ENTITIES.map((entity) => (
          <div 
            key={entity.id} 
            style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center',
              padding: '1.25rem',
              border: '1px solid var(--border)',
              borderRadius: '8px',
              backgroundColor: 'var(--bg-elevated)'
            }}
          >
            <h3 style={{ margin: 0, fontSize: '1.1rem' }}>{entity.label}</h3>
            <div className="stack" style={{ gap: '0.75rem' }}>
              <button
                type="button"
                className="btn secondary"
                disabled={busy}
                onClick={() => handleExport(entity.id, 'pdf')}
              >
                📄 Перегляд / Друк (PDF)
              </button>
              <button
                type="button"
                className="btn primary"
                disabled={busy}
                onClick={() => handleExport(entity.id, 'excel')}
              >
                📊 Експорт (Excel)
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
