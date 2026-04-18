import { useCallback, useEffect, useMemo, useState } from 'react';
import { categoriesApi } from '../../api/categories.js';
import { getApiErrorMessage } from '../../api/index.js';
import { DataTable } from '../../components/DataTable.jsx';
import { Modal } from '../../components/Modal.jsx';
import { ConfirmDialog } from '../../components/ConfirmDialog.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { sortRows } from '../../utils/sort.js';

const emptyForm = { categoryName: '' };

export function ManagerCategoriesPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formErrors, setFormErrors] = useState({});
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState({
    key: 'categoryName',
    dir: 'asc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await categoriesApi.getAll();
      setRows(data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const sortedRows = useMemo(
    () => sortRows(rows, sortState.key, sortState.dir, sortState.type),
    [rows, sortState]
  );

  const onSort = (key, type) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setFormErrors({});
    setModal('create');
  };

  const openEdit = (row) => {
    setEditingId(row.categoryNumber);
    setForm({ categoryName: row.categoryName || '' });
    setFormErrors({});
    setModal('edit');
  };

  const submit = async () => {
    const err = {};
    if (!form.categoryName?.trim()) err.categoryName = "Обов'язково";
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    try {
      const body = { categoryName: form.categoryName.trim() };
      if (modal === 'create') await categoriesApi.create(body);
      else await categoriesApi.update(editingId, body);
      setModal(null);
      setEditingId(null);
      await load();
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const doDelete = async () => {
    if (!deleteTarget) return;
    setSaving(true);
    try {
      await categoriesApi.delete(deleteTarget.categoryNumber);
      setDeleteTarget(null);
      await load();
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const columns = [
    { key: 'categoryNumber', label: 'ID', sortable: true, sortType: 'number' },
    { key: 'categoryName', label: 'Назва', sortable: true },
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
        <h1>Категорії</h1>
        <button type="button" className="btn primary" onClick={openCreate}>
          Додати
        </button>
      </div>
      {error && <div className="alert error">{error}</div>}
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.categoryNumber}
        sortState={sortState}
        onSort={onSort}
      />
      {saving && <Spinner label="Збереження…" />}
      {modal && (
        <Modal
          title={modal === 'create' ? 'Нова категорія' : 'Редагування'}
          onClose={() => {
            if (!saving) {
              setModal(null);
              setEditingId(null);
            }
          }}
        >
          <label className="stack" style={{ width: '100%' }}>
            Назва
            <input
              value={form.categoryName}
              onChange={(e) =>
                setForm((f) => ({ ...f, categoryName: e.target.value }))
              }
            />
            {formErrors.categoryName && (
              <span className="field-error">{formErrors.categoryName}</span>
            )}
          </label>
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
          message={`Видалити категорію «${deleteTarget.categoryName}»?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
    </div>
  );
}
