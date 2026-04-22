import { useCallback, useEffect, useMemo, useState } from 'react';
import { customerCardsApi } from '../../api/customerCards';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { Modal } from '../../components/Modal';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { Spinner } from '../../components/Spinner';
import { validatePhone } from '../../utils/validators';
import { sortRows, type SortValueType } from '../../utils/sort';
import { formatPhoneInput } from '../../utils/formatters';

type ClientCardRow = {
  cardNumber: string;
  custSurname: string;
  custName: string;
  custPatronymic?: string | null;
  custPhoneNumber: string;
  percent: number;
};

const emptyForm = {
  cardNumber: '',
  custSurname: '',
  custName: '',
  custPatronymic: '',
  custPhoneNumber: '',
  custCity: '',
  custStreet: '',
  custZipCode: '',
  percent: '',
};

type CardForm = typeof emptyForm;

export function ManagerClientCardsPage() {
  const [rows, setRows] = useState<ClientCardRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [discountFilter, setDiscountFilter] = useState('');
  const [modal, setModal] = useState<'create' | 'edit' | null>(null);
  const [form, setForm] = useState<CardForm>(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [deleteTarget, setDeleteTarget] = useState<ClientCardRow | null>(null);
  const [editingNumber, setEditingNumber] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState<TableSortState>({
    key: 'custSurname',
    dir: 'asc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await customerCardsApi.getAll();
      setRows((data as ClientCardRow[]) || []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const filtered = useMemo(() => {
    if (discountFilter === '') return rows;
    const p = Number(discountFilter);
    if (Number.isNaN(p)) return rows;
    return rows.filter((r) => r.percent === p);
  }, [rows, discountFilter]);

  const sortedRows = useMemo(
    () =>
      sortRows(
        filtered,
        sortState.key,
        sortState.dir,
        (sortState.type || 'string') as SortValueType
      ),
    [filtered, sortState]
  );

  const onSort = (key: string, type?: SortValueType) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const validate = (f: CardForm, isEdit: boolean) => {
    const err: Record<string, string> = {};
    if (!isEdit && !f.cardNumber?.trim()) err.cardNumber = "Обов'язково";
    if (!f.custSurname?.trim()) err.custSurname = "Обов'язково";
    if (!f.custName?.trim()) err.custName = "Обов'язково";
    const pe = validatePhone(f.custPhoneNumber);
    if (pe) err.custPhoneNumber = pe;
    const pct = Number(f.percent);
    if (Number.isNaN(pct) || pct < 0) err.percent = '≥ 0';
    return err;
  };

  const openCreate = () => {
    setEditingNumber(null);
    setForm(emptyForm);
    setFormErrors({});
    setModal('create');
  };

  const openEdit = (row: ClientCardRow) => {
    setEditingNumber(row.cardNumber);
    setForm({
      cardNumber: row.cardNumber,
      custSurname: row.custSurname || '',
      custName: row.custName || '',
      custPatronymic: row.custPatronymic || '',
      custPhoneNumber: row.custPhoneNumber || '',
      custCity: '',
      custStreet: '',
      custZipCode: '',
      percent: String(row.percent ?? ''),
    });
    setFormErrors({});
    setModal('edit');
  };

  const submit = async () => {
    const err = validate(form, modal === 'edit');
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    const card =
      modal === 'edit' && editingNumber ? editingNumber : form.cardNumber.trim();
    const body = {
      cardNumber: card,
      custSurname: form.custSurname.trim(),
      custName: form.custName.trim(),
      custPatronymic: form.custPatronymic?.trim() || null,
      custPhoneNumber: form.custPhoneNumber.trim(),
      custCity: form.custCity?.trim() || null,
      custStreet: form.custStreet?.trim() || null,
      custZipCode: form.custZipCode?.trim() || null,
      percent: Number(form.percent),
    };
    try {
      if (modal === 'create') await customerCardsApi.create(body);
      else if (editingNumber) await customerCardsApi.update(editingNumber, body);
      setModal(null);
      setEditingNumber(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const doDelete = async () => {
    if (!deleteTarget) return;
    setSaving(true);
    try {
      await customerCardsApi.delete(deleteTarget.cardNumber);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const discountOptions = useMemo(() => {
    const s = new Set(rows.map((r) => r.percent).filter((x) => x != null));
    return [...s].sort((a, b) => a - b);
  }, [rows]);

  const columns: DataColumn<ClientCardRow>[] = [
    { key: 'cardNumber', label: 'Картка', sortable: true },
    { key: 'custSurname', label: 'Прізвище', sortable: true },
    { key: 'custName', label: "Ім'я", sortable: true },
    { key: 'custPatronymic', label: 'По батькові', sortable: true },
    { key: 'custPhoneNumber', label: 'Телефон', sortable: true },
    {
      key: 'percent',
      label: 'Знижка %',
      sortable: true,
      sortType: 'number',
    },
    {
      key: '_a',
      label: '',
      render: (r) => (
        <div className="stack">
          <button
            type="button"
            className="btn secondary small"
            onClick={() => openEdit(r)}
          >
            Змінити
          </button>
          <button
            type="button"
            className="btn danger small"
            onClick={() => setDeleteTarget(r)}
          >
            Видалити
          </button>
        </div>
      ),
    },
  ];

  if (loading && !rows.length) return <Spinner />;

  return (
    <div>
      <div className="page-head">
        <h1>Картки клієнтів</h1>
        <button type="button" className="btn primary" onClick={openCreate}>
          Додати
        </button>
      </div>
      {error && <div className="alert error">{error}</div>}
      <div className="toolbar">
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          Знижка %
          <select
            value={discountFilter}
            onChange={(e) => setDiscountFilter(e.target.value)}
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
            }}
          >
            <option value="">Усі</option>
            {discountOptions.map((d) => (
              <option key={d} value={d}>
                {d}%
              </option>
            ))}
          </select>
        </label>
      </div>
      <p className="alert info" style={{ fontSize: '0.85rem' }}>
        У відповіді API для картки немає адреси — поля міста/вулиці при
        редагуванні можна залишити порожніми, якщо бекенд це дозволяє.
      </p>
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.cardNumber}
        sortState={sortState}
        onSort={onSort}
      />
      {saving && <Spinner label="Збереження…" />}
      {modal && (
        <Modal
          title={modal === 'create' ? 'Нова картка' : 'Редагування картки'}
          onClose={() => {
            if (!saving) {
              setModal(null);
              setEditingNumber(null);
            }
          }}
          wide
        >
          <div className="form-grid">
            {modal === 'create' && (
              <label>
                Номер картки
                <input
                  value={form.cardNumber}
                  maxLength={13}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, cardNumber: e.target.value }))
                  }
                />
                {formErrors.cardNumber && (
                  <span className="field-error">{formErrors.cardNumber}</span>
                )}
              </label>
            )}
            <label>
              Прізвище
              <input
                value={form.custSurname}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custSurname: e.target.value }))
                }
              />
              {formErrors.custSurname && (
                <span className="field-error">{formErrors.custSurname}</span>
              )}
            </label>
            <label>
              Імʼя
              <input
                value={form.custName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custName: e.target.value }))
                }
              />
              {formErrors.custName && (
                <span className="field-error">{formErrors.custName}</span>
              )}
            </label>
            <label>
              По батькові
              <input
                value={form.custPatronymic}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custPatronymic: e.target.value }))
                }
              />
            </label>
            <label>
              Телефон
              <input
                type="tel"
                value={form.custPhoneNumber}
                maxLength={13}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custPhoneNumber: formatPhoneInput(e.target.value) }))
                }
              />
              {formErrors.custPhoneNumber && (
                <span className="field-error">
                  {formErrors.custPhoneNumber}
                </span>
              )}
            </label>
            <label>
              Місто
              <input
                value={form.custCity}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custCity: e.target.value }))
                }
              />
            </label>
            <label>
              Вулиця
              <input
                value={form.custStreet}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custStreet: e.target.value }))
                }
              />
            </label>
            <label>
              Індекс
              <input
                value={form.custZipCode}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custZipCode: e.target.value }))
                }
              />
            </label>
            <label>
              Знижка %
              <input
                type="number"
                min="0"
                value={form.percent}
                onChange={(e) =>
                  setForm((f) => ({ ...f, percent: e.target.value }))
                }
              />
              {formErrors.percent && (
                <span className="field-error">{formErrors.percent}</span>
              )}
            </label>
          </div>
          <div className="form-actions">
            <button
              type="button"
              className="btn secondary"
              onClick={() => setModal(null)}
              disabled={saving}
            >
              Скасувати
            </button>
            <button
              type="button"
              className="btn primary"
              onClick={submit}
              disabled={saving}
            >
              Зберегти
            </button>
          </div>
        </Modal>
      )}
      {deleteTarget && (
        <ConfirmDialog
          message={`Видалити картку ${deleteTarget.cardNumber}?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
    </div>
  );
}
