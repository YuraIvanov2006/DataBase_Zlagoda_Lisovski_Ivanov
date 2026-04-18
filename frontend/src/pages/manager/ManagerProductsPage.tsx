import { useCallback, useEffect, useMemo, useState } from 'react';
import { productsApi } from '../../api/products';
import { categoriesApi } from '../../api/categories';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { Modal } from '../../components/Modal';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { Spinner } from '../../components/Spinner';
import { sortRows, type SortValueType } from '../../utils/sort';

type ProductRow = {
  idProduct: number;
  categoryNumber: number;
  productName: string;
  manufacturer: string;
  characteristics: string;
  categoryName: string;
};

type CategoryRow = {
  categoryNumber: number;
  categoryName: string;
};

const emptyForm = {
  categoryNumber: '',
  productName: '',
  manufacturer: '',
  characteristics: '',
};

type ProductForm = typeof emptyForm;

export function ManagerProductsPage() {
  const [products, setProducts] = useState<ProductRow[]>([]);
  const [categories, setCategories] = useState<CategoryRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [modal, setModal] = useState<'create' | 'edit' | null>(null);
  const [form, setForm] = useState<ProductForm>(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [deleteTarget, setDeleteTarget] = useState<ProductRow | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState<TableSortState>({
    key: 'productName',
    dir: 'asc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [p, c] = await Promise.all([
        productsApi.getOrderedByName(),
        categoriesApi.getAll(),
      ]);
      setProducts((p.data as ProductRow[]) || []);
      setCategories((c.data as CategoryRow[]) || []);
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
    if (!categoryFilter) return products;
    const id = Number(categoryFilter);
    return products.filter((p) => p.categoryNumber === id);
  }, [products, categoryFilter]);

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

  const validate = (f: ProductForm) => {
    const err: Record<string, string> = {};
    if (!f.categoryNumber) err.categoryNumber = "Обов'язково";
    if (!f.productName?.trim()) err.productName = "Обов'язково";
    if (!f.manufacturer?.trim()) err.manufacturer = "Обов'язково";
    if (!f.characteristics?.trim()) err.characteristics = "Обов'язково";
    return err;
  };

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setFormErrors({});
    setModal('create');
  };

  const openEdit = (row: ProductRow) => {
    setEditingId(row.idProduct);
    setForm({
      categoryNumber: String(row.categoryNumber ?? ''),
      productName: row.productName || '',
      manufacturer: row.manufacturer || '',
      characteristics: row.characteristics || '',
    });
    setFormErrors({});
    setModal('edit');
  };

  const submit = async () => {
    const err = validate(form);
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    const body = {
      categoryNumber: Number(form.categoryNumber),
      productName: form.productName.trim(),
      manufacturer: form.manufacturer.trim(),
      characteristics: form.characteristics.trim(),
    };
    try {
      if (modal === 'create') await productsApi.create(body);
      else if (editingId != null) await productsApi.update(editingId, body);
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
      await productsApi.delete(deleteTarget.idProduct);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const columns: DataColumn<ProductRow>[] = [
    {
      key: 'idProduct',
      label: 'ID',
      sortable: true,
      sortType: 'number',
    },
    {
      key: 'productName',
      label: 'Назва',
      sortable: true,
    },
    {
      key: 'manufacturer',
      label: 'Виробник',
      sortable: true,
    },
    {
      key: 'characteristics',
      label: 'Характеристики',
      sortable: true,
    },
    {
      key: 'categoryName',
      label: 'Категорія',
      sortable: true,
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

  if (loading && !products.length) return <Spinner />;

  return (
    <div>
      <div className="page-head">
        <h1>Товари</h1>
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
          Категорія
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
            }}
          >
            <option value="">Усі</option>
            {categories
              .slice()
              .sort((a, b) =>
                (a.categoryName || '').localeCompare(
                  b.categoryName || '',
                  'uk'
                )
              )
              .map((c) => (
                <option key={c.categoryNumber} value={c.categoryNumber}>
                  {c.categoryName}
                </option>
              ))}
          </select>
        </label>
      </div>
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.idProduct}
        sortState={sortState}
        onSort={onSort}
      />
      {saving && <Spinner label="Збереження…" />}
      {modal && (
        <Modal
          title={modal === 'create' ? 'Новий товар' : 'Редагування товару'}
          onClose={() => {
            if (!saving) {
              setModal(null);
              setEditingId(null);
            }
          }}
          wide
        >
          <div className="form-grid">
            <label>
              Категорія
              <select
                value={form.categoryNumber}
                onChange={(e) =>
                  setForm((f) => ({ ...f, categoryNumber: e.target.value }))
                }
              >
                <option value="">—</option>
                {categories.map((c) => (
                  <option key={c.categoryNumber} value={c.categoryNumber}>
                    {c.categoryName}
                  </option>
                ))}
              </select>
              {formErrors.categoryNumber && (
                <span className="field-error">{formErrors.categoryNumber}</span>
              )}
            </label>
            <label>
              Назва
              <input
                value={form.productName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, productName: e.target.value }))
                }
              />
              {formErrors.productName && (
                <span className="field-error">{formErrors.productName}</span>
              )}
            </label>
            <label style={{ gridColumn: '1 / -1' }}>
              Виробник
              <input
                value={form.manufacturer}
                onChange={(e) =>
                  setForm((f) => ({ ...f, manufacturer: e.target.value }))
                }
              />
              {formErrors.manufacturer && (
                <span className="field-error">{formErrors.manufacturer}</span>
              )}
            </label>
            <label style={{ gridColumn: '1 / -1' }}>
              Характеристики
              <textarea
                rows={3}
                value={form.characteristics}
                onChange={(e) =>
                  setForm((f) => ({ ...f, characteristics: e.target.value }))
                }
              />
              {formErrors.characteristics && (
                <span className="field-error">
                  {formErrors.characteristics}
                </span>
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
          message={`Видалити товар «${deleteTarget.productName}»?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
    </div>
  );
}
