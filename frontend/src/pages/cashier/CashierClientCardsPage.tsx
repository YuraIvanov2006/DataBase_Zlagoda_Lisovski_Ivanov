import { useCallback, useEffect, useMemo, useState } from 'react';
import { customerCardsApi } from '../../api/customerCards';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { Modal } from '../../components/Modal';
import { SearchBar } from '../../components/SearchBar';
import { Spinner } from '../../components/Spinner';
import { validatePhone } from '../../utils/validators';
import { sortRows, type SortValueType } from '../../utils/sort';

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

export function CashierClientCardsPage() {
  const [rows, setRows] = useState<ClientCardRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [surnameQ, setSurnameQ] = useState('');
  const [cardNumberQ, setCardNumberQ] = useState('');
  const [modal, setModal] = useState<'create' | 'edit' | null>(null);
  const [form, setForm] = useState<CardForm>(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
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
    let res = rows;
    if (surnameQ.trim()) {
      const q = surnameQ.trim().toLowerCase();
      res = res.filter((r) => (r.custSurname || '').toLowerCase().includes(q));
    }
    if (cardNumberQ.trim()) {
      const q = cardNumberQ.trim().toLowerCase();
      res = res.filter((r) => (r.cardNumber || '').toLowerCase().includes(q));
    }
    return res;
  }, [rows, surnameQ, cardNumberQ]);

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

  const searchApi = async () => {
    if (!surnameQ.trim()) return;
    setError('');
    try {
      const { data } = await customerCardsApi.getBySurname(surnameQ.trim());
      setRows((data as ClientCardRow[]) || []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    }
  };

  const searchCardApi = async () => {
    if (!cardNumberQ.trim()) return;
    setError('');
    try {
      const { data } = await customerCardsApi.getByNumber(cardNumberQ.trim());
      setRows(data ? [data as ClientCardRow] : []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    }
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
      modal === 'edit' && editingNumber
        ? editingNumber
        : form.cardNumber.trim();
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
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

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
        <button
          type="button"
          className="btn secondary small"
          onClick={() => openEdit(r)}
        >
          Змінити
        </button>
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
        <SearchBar
          value={surnameQ}
          onChange={setSurnameQ}
          placeholder="Прізвище (локально)"
        />
        <button type="button" className="btn secondary" onClick={searchApi}>
          Пошук API за прізвищем
        </button>
      </div>
      <div className="toolbar">
        <SearchBar
          value={cardNumberQ}
          onChange={setCardNumberQ}
          placeholder="Номер картки (локально)"
        />
        <button type="button" className="btn secondary" onClick={searchCardApi}>
          Пошук API за номером
        </button>
        <button type="button" className="btn secondary" onClick={load}>
          Усі картки
        </button>
      </div>
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
          title={modal === 'create' ? 'Нова картка' : 'Редагування'}
          onClose={() => !saving && setModal(null)}
          wide
        >
          <div className="form-grid">
            {modal === 'create' && (
              <label>
                Номер картки
                <input
                  value={form.cardNumber}
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
                value={form.custPhoneNumber}
                maxLength={13}
                onChange={(e) =>
                  setForm((f) => ({ ...f, custPhoneNumber: e.target.value }))
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
    </div>
  );
}
