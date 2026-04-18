import { useCallback, useEffect, useMemo, useState } from 'react';
import { productsApi } from '../../api/products.js';
import { categoriesApi } from '../../api/categories.js';
import { getApiErrorMessage } from '../../api/index.js';
import { DataTable } from '../../components/DataTable.jsx';
import { SearchBar } from '../../components/SearchBar.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { sortRows } from '../../utils/sort.js';

export function CashierProductsPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [nameSearch, setNameSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [sortState, setSortState] = useState({
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
      setProducts(p.data || []);
      setCategories(c.data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const filtered = useMemo(() => {
    let list = products;
    if (categoryFilter) {
      const id = Number(categoryFilter);
      list = list.filter((p) => p.categoryNumber === id);
    }
    if (nameSearch.trim()) {
      const q = nameSearch.trim().toLowerCase();
      list = list.filter((p) =>
        (p.productName || '').toLowerCase().includes(q)
      );
    }
    return list;
  }, [products, categoryFilter, nameSearch]);

  const sortedRows = useMemo(
    () => sortRows(filtered, sortState.key, sortState.dir, sortState.type),
    [filtered, sortState]
  );

  const onSort = (key, type) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const columns = [
    { key: 'idProduct', label: 'ID', sortable: true, sortType: 'number' },
    { key: 'productName', label: 'Назва', sortable: true },
    { key: 'manufacturer', label: 'Виробник', sortable: true },
    { key: 'characteristics', label: 'Характеристики', sortable: true },
    { key: 'categoryName', label: 'Категорія', sortable: true },
  ];

  if (loading && !products.length) return <Spinner />;

  return (
    <div>
      <h1>Товари (перегляд)</h1>
      {error && <div className="alert error">{error}</div>}
      <div className="toolbar">
        <SearchBar
          value={nameSearch}
          onChange={setNameSearch}
          placeholder="Пошук за назвою"
        />
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          Категорія
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            style={{ padding: '0.45rem', borderRadius: 8, border: '1px solid var(--border)' }}
          >
            <option value="">Усі</option>
            {categories
              .slice()
              .sort((a, b) =>
                (a.categoryName || '').localeCompare(b.categoryName || '', 'uk')
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
    </div>
  );
}
