import { useCallback, useEffect, useMemo, useState } from 'react';
import { storeProductsApi } from '../../api/storeProducts.js';
import { getApiErrorMessage } from '../../api/index.js';
import { DataTable } from '../../components/DataTable.jsx';
import { Modal } from '../../components/Modal.jsx';
import { ConfirmDialog } from '../../components/ConfirmDialog.jsx';
import { SearchBar } from '../../components/SearchBar.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { money, promotionalUnitPrice } from '../../utils/formatters.js';
import { sortRows } from '../../utils/sort.js';

const emptyForm = {
  upc: '',
  idProduct: '',
  baseProductUpc: '',
  sellingPrice: '',
  productsNumber: '',
  promotionalProduct: false,
};

export function ManagerStoreItemsPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [promoFilter, setPromoFilter] = useState('all');
  const [sortMode, setSortMode] = useState('quantity');
  const [upcSearch, setUpcSearch] = useState('');
  const [upcHit, setUpcHit] = useState(null);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formErrors, setFormErrors] = useState({});
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [editingUpc, setEditingUpc] = useState(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState({
    key: 'productsNumber',
    dir: 'desc',
    type: 'number',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      let res;
      if (promoFilter === 'promo') res = await storeProductsApi.getPromotional();
      else if (promoFilter === 'regular')
        res = await storeProductsApi.getNotPromotional();
      else res = await storeProductsApi.getAll();
      setRows(res.data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, [promoFilter]);

  useEffect(() => {
    load();
  }, [load]);

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
    return sortRows(tableRows, sortState.key, sortState.dir, sortState.type);
  }, [tableRows, sortState]);

  const onSort = (key, type) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const validate = (f, isEdit) => {
    const err = {};
    if (!isEdit && !f.upc?.trim()) err.upc = "Обов'язково";
    if (!f.idProduct) err.idProduct = "Обов'язково";
    const pr = Number(f.sellingPrice);
    if (Number.isNaN(pr) || pr < 0) err.sellingPrice = '≥ 0';
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

  const openEdit = (row) => {
    setEditingUpc(row.upc);
    setForm({
      upc: row.upc,
      idProduct: row.product?.idProduct ?? '',
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
    const body = {
      upc: (modal === 'edit' ? editingUpc : form.upc).trim(),
      idProduct: Number(form.idProduct),
      baseProductUpc: form.baseProductUpc?.trim() || null,
      sellingPrice: Number(form.sellingPrice),
      productsNumber: Number(form.productsNumber),
      promotionalProduct: !!form.promotionalProduct,
    };
    try {
      if (modal === 'create') await storeProductsApi.create(body);
      else await storeProductsApi.update(editingUpc, body);
      setModal(null);
      setEditingUpc(null);
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
      await storeProductsApi.delete(deleteTarget.upc);
      setDeleteTarget(null);
      await load();
    } catch (e) {
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
      setUpcHit(data);
    } catch (e) {
      setUpcHit(null);
      setError(getApiErrorMessage(e));
    }
  };

  const columns = [
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
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          Фільтр
          <select
            value={promoFilter}
            onChange={(e) => setPromoFilter(e.target.value)}
            style={{ padding: '0.45rem', borderRadius: 8, border: '1px solid var(--border)' }}
          >
            <option value="all">Усі</option>
            <option value="promo">Лише акційні</option>
            <option value="regular">Без акції</option>
          </select>
        </label>
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          Початкове сортування
          <select
            value={sortMode}
            onChange={(e) => setSortMode(e.target.value)}
            style={{ padding: '0.45rem', borderRadius: 8, border: '1px solid var(--border)' }}
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
                  onChange={(e) =>
                    setForm((f) => ({ ...f, upc: e.target.value }))
                  }
                />
                {formErrors.upc && (
                  <span className="field-error">{formErrors.upc}</span>
                )}
              </label>
            )}
            <label>
              ID продукту
              <input
                type="number"
                value={form.idProduct}
                onChange={(e) =>
                  setForm((f) => ({ ...f, idProduct: e.target.value }))
                }
              />
              {formErrors.idProduct && (
                <span className="field-error">{formErrors.idProduct}</span>
              )}
            </label>
            <label>
              Базовий UPC (опц.)
              <input
                value={form.baseProductUpc}
                onChange={(e) =>
                  setForm((f) => ({ ...f, baseProductUpc: e.target.value }))
                }
              />
            </label>
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
