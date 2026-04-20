import { useCallback, useEffect, useMemo, useState } from 'react';
import { storeProductsApi } from '../../api/storeProducts';
import { productsApi } from '../../api/products';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { Modal } from '../../components/Modal';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { SearchBar } from '../../components/SearchBar';
import { Spinner } from '../../components/Spinner';
import { money, promotionalUnitPrice } from '../../utils/formatters';
import { sortRows, type SortValueType } from '../../utils/sort';

type StoreProductRow = {
  upc: string;
  product?: {
    productName?: string;
    characteristics?: string;
    idProduct?: number;
  };
  baseProductUpc?: string | null;
  sellingPrice: unknown;
  productsNumber: number;
  promotionalProduct: boolean;
  productName?: string;
};

const emptyForm = {
  upc: '',
  idProduct: '',
  baseProductUpc: '',
  sellingPrice: '',
  productsNumber: '',
  promotionalProduct: false,
};

type StoreForm = typeof emptyForm;

export function ManagerStoreItemsPage() {
  const [rows, setRows] = useState<StoreProductRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [promoFilter, setPromoFilter] = useState<'all' | 'promo' | 'regular'>(
    'all'
  );
  const [sortMode, setSortMode] = useState<'quantity' | 'name'>('quantity');
  const [upcSearch, setUpcSearch] = useState('');
  const [upcHit, setUpcHit] = useState<StoreProductRow | null>(null);
  const [modal, setModal] = useState<'create' | 'edit' | null>(null);
  const [form, setForm] = useState<StoreForm>(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [deleteTarget, setDeleteTarget] = useState<StoreProductRow | null>(
    null
  );
  const [editingUpc, setEditingUpc] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState<TableSortState>({
    key: 'productsNumber',
    dir: 'desc',
    type: 'number',
  });
  const [productsList, setProductsList] = useState<{idProduct: number; productName: string}[]>([]);
  const [regularStoreProducts, setRegularStoreProducts] = useState<StoreProductRow[]>([]);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      let res;
      if (promoFilter === 'promo') res = await storeProductsApi.getPromotional();
      else if (promoFilter === 'regular')
        res = await storeProductsApi.getNotPromotional();
      else res = await storeProductsApi.getAll();
      setRows((res.data as StoreProductRow[]) || []);

      const regRes = await storeProductsApi.getNotPromotional();
      setRegularStoreProducts((regRes.data as StoreProductRow[]) || []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, [promoFilter]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    productsApi.getAll()
      .then((res) => setProductsList(res.data as {idProduct: number; productName: string}[]))
      .catch((e) => console.error('Failed to load products list', e));
  }, []);

  useEffect(() => {
    if (sortMode === 'quantity') {
      setSortState({
        key: 'productsNumber',
        dir: 'desc',
        type: 'number',
      });
    } else {
      setSortState({ key: 'productName', dir: 'asc', type: 'string' });
    }
  }, [sortMode]);

  const tableRows = useMemo(
    () =>
      rows.map((r) => ({
        ...r,
        productName: r.product?.productName || '',
      })),
    [rows]
  );

  const sortedRows = useMemo(() => {
    return sortRows(
      tableRows,
      sortState.key,
      sortState.dir,
      (sortState.type || 'string') as SortValueType
    );
  }, [tableRows, sortState]);

  const onSort = (key: string, type?: SortValueType) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const validate = (f: StoreForm, isEdit: boolean) => {
    const err: Record<string, string> = {};
    if (!isEdit) {
      const u = f.upc?.trim();
      if (!u) err.upc = "Обов'язково";
      else if (u.length !== 12) err.upc = "Має бути рівно 12 символів";
      else if (rows.some((r) => r.upc === u)) err.upc = "Такий UPC вже існує";
    }
    if (!f.idProduct) err.idProduct = "Обов'язково";
    if (f.promotionalProduct && !f.baseProductUpc) err.baseProductUpc = "Обов'язково";
    if (!f.promotionalProduct) {
      const pr = Number(f.sellingPrice);
      if (Number.isNaN(pr) || pr < 0) err.sellingPrice = '≥ 0';
    }
    const q = Number(f.productsNumber);
    if (Number.isNaN(q) || q < 0) err.productsNumber = '≥ 0';
    return err;
  };

  const openCreate = () => {
    setEditingUpc(null);
    setForm({ ...emptyForm, upc: '' });
    setFormErrors({});
    setModal('create');
  };

  const openEdit = (row: StoreProductRow) => {
    setEditingUpc(row.upc);
    setForm({
      upc: row.upc,
      idProduct: String(row.product?.idProduct ?? ''),
      baseProductUpc: row.baseProductUpc || '',
      sellingPrice: row.sellingPrice ?? '',
      productsNumber: row.productsNumber ?? '',
      promotionalProduct: !!row.promotionalProduct,
    });
    setFormErrors({});
    setModal('edit');
  };

  const submit = async () => {
    const err = validate(form, modal === 'edit');
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    const upcForBody =
      modal === 'edit' && editingUpc ? editingUpc : form.upc.trim();
    const body = {
      upc: upcForBody,
      idProduct: Number(form.idProduct),
      baseProductUpc: form.baseProductUpc?.trim() || null,
      sellingPrice: form.promotionalProduct ? 0 : Number(form.sellingPrice),
      productsNumber: Number(form.productsNumber),
      promotionalProduct: !!form.promotionalProduct,
    };
    try {
      if (modal === 'create') await storeProductsApi.create(body);
      else if (editingUpc) await storeProductsApi.update(editingUpc, body);
      setModal(null);
      setEditingUpc(null);
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
      await storeProductsApi.delete(deleteTarget.upc);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const runUpcSearch = async () => {
    if (!upcSearch.trim()) return;
    setError('');
    try {
      const { data } = await storeProductsApi.getByUpc(upcSearch.trim());
      setUpcHit((data as StoreProductRow) || null);
    } catch (e: unknown) {
      setUpcHit(null);
      setError(getApiErrorMessage(e));
    }
  };

  const columns: DataColumn<StoreProductRow & { productName?: string }>[] = [
    { key: 'upc', label: 'UPC', sortable: true },
    {
      key: 'productName',
      label: 'Товар',
      sortable: true,
      render: (r) => r.product?.productName || '—',
    },
    {
      key: 'sellingPrice',
      label: 'Ціна',
      sortable: true,
      sortType: 'number',
      render: (r) => money(r.sellingPrice),
    },
    {
      key: 'promoPrice',
      label: 'Акційна ціна (×0.8)',
      render: (r) =>
        r.promotionalProduct
          ? money(promotionalUnitPrice(r.sellingPrice, true))
          : '—',
    },
    {
      key: 'productsNumber',
      label: 'Кількість',
      sortable: true,
      sortType: 'number',
    },
    {
      key: 'promotionalProduct',
      label: 'Акція',
      sortable: true,
      render: (r) => (r.promotionalProduct ? 'Так' : 'Ні'),
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
        <h1>Товар у магазині</h1>
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
          Фільтр
          <select
            value={promoFilter}
            onChange={(e) =>
              setPromoFilter(e.target.value as 'all' | 'promo' | 'regular')
            }
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
            }}
          >
            <option value="all">Усі</option>
            <option value="promo">Лише акційні</option>
            <option value="regular">Без акції</option>
          </select>
        </label>
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          Початкове сортування
          <select
            value={sortMode}
            onChange={(e) =>
              setSortMode(e.target.value as 'quantity' | 'name')
            }
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
            }}
          >
            <option value="quantity">За кількістю</option>
            <option value="name">За назвою товару</option>
          </select>
        </label>
        <SearchBar
          value={upcSearch}
          onChange={setUpcSearch}
          placeholder="Пошук за UPC"
          onSubmit={runUpcSearch}
        />
      </div>
      {upcHit && (
        <div className="alert info stack">
          <strong>За UPC</strong>
          <span>Назва: {upcHit.product?.productName}</span>
          <span>Ціна: {money(upcHit.sellingPrice)}</span>
          <span>Кількість: {upcHit.productsNumber}</span>
          <span>Характеристики: {upcHit.product?.characteristics || '—'}</span>
        </div>
      )}
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.upc}
        sortState={sortState}
        onSort={onSort}
      />
      {saving && <Spinner label="Збереження…" />}
      {modal && (
        <Modal
          title={
            modal === 'create' ? 'Новий товар у магазині' : 'Редагування'
          }
          onClose={() => {
            if (!saving) {
              setModal(null);
              setEditingUpc(null);
            }
          }}
          wide
        >
          <div className="form-grid">
            {modal === 'create' && (
              <label>
                UPC
                <input
                  value={form.upc}
                  maxLength={12}
                  autoFocus
                  placeholder="Наприклад: 482001494200"
                  onChange={(e) =>
                    setForm((f) => ({ ...f, upc: e.target.value.replace(/\D/g, '') }))
                  }
                />
                {formErrors.upc && (
                  <span className="field-error">{formErrors.upc}</span>
                )}
              </label>
            )}
            <label>
              Продукт
              <select
                value={form.idProduct}
                onChange={(e) =>
                  setForm((f) => ({ ...f, idProduct: e.target.value }))
                }
              >
                <option value="" disabled>
                  — Оберіть продукт —
                </option>
                {productsList.map((p) => (
                  <option key={p.idProduct} value={String(p.idProduct)}>
                    {p.idProduct} — {p.productName}
                  </option>
                ))}
              </select>
              {formErrors.idProduct && (
                <span className="field-error">{formErrors.idProduct}</span>
              )}
            </label>
            {!form.promotionalProduct && (
              <label>
                Ціна
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={form.sellingPrice}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, sellingPrice: e.target.value }))
                  }
                />
                {formErrors.sellingPrice && (
                  <span className="field-error">{formErrors.sellingPrice}</span>
                )}
              </label>
            )}
            <label>
              Кількість
              <input
                type="number"
                min="0"
                value={form.productsNumber}
                onChange={(e) =>
                  setForm((f) => ({ ...f, productsNumber: e.target.value }))
                }
              />
              {formErrors.productsNumber && (
                <span className="field-error">
                  {formErrors.productsNumber}
                </span>
              )}
            </label>
            <label className="stack" style={{ justifyContent: 'flex-end' }}>
              <span>Акційний товар</span>
              <input
                type="checkbox"
                checked={form.promotionalProduct}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    promotionalProduct: e.target.checked,
                  }))
                }
              />
            </label>
            {form.promotionalProduct && (
              <label>
                Базовий товар (UPC)
                <select
                  value={form.baseProductUpc}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, baseProductUpc: e.target.value }))
                  }
                >
                  <option value="" disabled>
                    — Оберіть звичайний товар —
                  </option>
                  {regularStoreProducts.map((p) => (
                    <option key={p.upc} value={p.upc}>
                      {p.product?.productName} (UPC: {p.upc})
                    </option>
                  ))}
                </select>
                {formErrors.baseProductUpc && (
                  <span className="field-error">{formErrors.baseProductUpc}</span>
                )}
              </label>
            )}
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
          message={`Видалити позицію UPC ${deleteTarget.upc}?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
    </div>
  );
}
