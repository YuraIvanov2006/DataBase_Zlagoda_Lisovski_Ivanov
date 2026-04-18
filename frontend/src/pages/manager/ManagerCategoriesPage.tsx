import { useCallback, useEffect, useMemo, useState } from 'react';
import { categoriesApi } from '../../api/categories';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { Modal } from '../../components/Modal';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { Spinner } from '../../components/Spinner';
import { sortRows, type SortValueType } from '../../utils/sort';

type CategoryRow = {
  categoryNumber: number;
  categoryName: string;
};

const emptyForm = { categoryName: '' };

export function ManagerCategoriesPage() {
  const [rows, setRows] = useState<CategoryRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modal, setModal] = useState<'create' | 'edit' | null>(null);
  const [form, setForm] = useState(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [deleteTarget, setDeleteTarget] = useState<CategoryRow | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState<TableSortState>({
    key: 'categoryName',
    dir: 'asc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await categoriesApi.getAll();
      setRows((data as CategoryRow[]) || []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const sortedRows = useMemo(
    () =>
      sortRows(
        rows,
        sortState.key,
        sortState.dir,
        (sortState.type || 'string') as SortValueType
      ),
    [rows, sortState]
  );

  const onSort = (key: string, type?: SortValueType) => {
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

  const openEdit = (row: CategoryRow) => {
    setEditingId(row.categoryNumber);
    setForm({ categoryName: row.categoryName || '' });
    setFormErrors({});
    setModal('edit');
  };

  const submit = async () => {
    const err: Record<string, string> = {};
    if (!form.categoryName?.trim()) err.categoryName = "Обов'язково";
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    try {
      const body = { categoryName: form.categoryName.trim() };
      if (modal === 'create') await categoriesApi.create(body);
      else if (editingId != null) await categoriesApi.update(editingId, body);
      setModal(null);
      setEditingId(null);
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
      await categoriesApi.delete(deleteTarget.categoryNumber);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const columns: DataColumn<CategoryRow>[] = [
    {
      key: 'categoryNumber',
      label: 'ID',
      sortable: true,
      sortType: 'number',
    },
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
