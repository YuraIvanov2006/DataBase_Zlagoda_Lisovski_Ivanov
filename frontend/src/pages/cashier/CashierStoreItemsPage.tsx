import { useCallback, useEffect, useMemo, useState } from 'react';
import { storeProductsApi } from '../../api/storeProducts';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { SearchBar } from '../../components/SearchBar';
import { Spinner } from '../../components/Spinner';
import { money, promotionalUnitPrice } from '../../utils/formatters';
import { sortRows, type SortValueType } from '../../utils/sort';

type StoreRow = {
  upc: string;
  product?: { productName?: string; characteristics?: string };
  sellingPrice: number;
  productsNumber: number;
  promotionalProduct: boolean;
  productName?: string;
};

export function CashierStoreItemsPage() {
  const [rows, setRows] = useState<StoreRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [promoFilter, setPromoFilter] = useState<'all' | 'promo' | 'regular'>(
    'all'
  );
  const [sortMode, setSortMode] = useState<'quantity' | 'name'>('quantity');
  const [upcSearch, setUpcSearch] = useState('');
  const [upcHit, setUpcHit] = useState<StoreRow | null>(null);
  const [sortState, setSortState] = useState<TableSortState>({
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
      setRows((res.data as StoreRow[]) || []);
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

  const sortedRows = useMemo(
    () =>
      sortRows(
        tableRows,
        sortState.key,
        sortState.dir,
        (sortState.type || 'string') as SortValueType
      ),
    [tableRows, sortState]
  );

  const onSort = (key: string, type?: SortValueType) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const runUpcSearch = async () => {
    if (!upcSearch.trim()) return;
    setError('');
    try {
      const { data } = await storeProductsApi.getByUpc(upcSearch.trim());
      setUpcHit((data as StoreRow) || null);
    } catch (e: unknown) {
      setUpcHit(null);
      setError(getApiErrorMessage(e));
    }
  };

  const columns: DataColumn<StoreRow & { productName?: string }>[] = [
    { key: 'upc', label: 'UPC', sortable: true },
    { key: 'productName', label: 'Назва', sortable: true },
    {
      key: 'sellingPrice',
      label: 'Ціна',
      sortable: true,
      sortType: 'number',
      render: (r) => money(r.sellingPrice),
    },
    {
      key: 'customerPrice',
      label: 'Ціна для клієнта',
      render: (r) =>
        money(promotionalUnitPrice(r.sellingPrice, r.promotionalProduct)),
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
  ];

  if (loading && !rows.length) return <Spinner />;

  return (
    <div>
      <h1>Товар у магазині (перегляд)</h1>
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
            <option value="promo">Акційні</option>
            <option value="regular">Звичайні</option>
          </select>
        </label>
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          Сортування
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
            <option value="name">За назвою</option>
          </select>
        </label>
        <SearchBar
          value={upcSearch}
          onChange={setUpcSearch}
          placeholder="UPC"
          onSubmit={runUpcSearch}
        />
      </div>
      {upcHit && (
        <div className="alert info stack">
          <span>Назва: {upcHit.product?.productName || '—'}</span>
          <span>Характеристики: {upcHit.product?.characteristics || '—'}</span>
          <span>Ціна: {money(upcHit.sellingPrice)}</span>
          <span>Кількість на складі: {upcHit.productsNumber}</span>
        </div>
      )}
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.upc}
        sortState={sortState}
        onSort={onSort}
      />
    </div>
  );
}
