import { useState, useEffect } from 'react';
import { queriesApi, CategorySale, ProductSoldByAll, CustomerCategoryPurchases, CategoryBoughtByAll } from '../../api/queries';
import { categoriesApi } from '../../api/categories';
import { getApiErrorMessage } from '../../api/index';
import { Spinner } from '../../components/Spinner';

export function ManagerComplexQueriesPage() {
  const [activeTab, setActiveTab] = useState<'q1' | 'q2' | 'y1' | 'y2'>('q1');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  // Tab 1 state
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [sales, setSales] = useState<CategorySale[] | null>(null);

  // Tab 2 state
  const [products, setProducts] = useState<ProductSoldByAll[] | null>(null);

  // Tab Yura 1 state
  const [categories, setCategories] = useState<{ categoryNumber: number; categoryName: string }[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<number | ''>('');
  const [yuraStartDate, setYuraStartDate] = useState('');
  const [yuraEndDate, setYuraEndDate] = useState('');
  const [customerPurchases, setCustomerPurchases] = useState<CustomerCategoryPurchases[] | null>(null);

  // Tab Yura 2 state
  const [categoriesBoughtByAll, setCategoriesBoughtByAll] = useState<CategoryBoughtByAll[] | null>(null);

  useEffect(() => {
    categoriesApi.getAll().then(res => setCategories(res.data)).catch(console.error);
  }, []);

  const fetchCategorySales = async () => {
    if (!startDate || !endDate) {
      setError('Будь ласка, оберіть обидві дати');
      return;
    }
    setBusy(true);
    setError('');
    try {
      const data = await queriesApi.getCategorySales(startDate, endDate);
      setSales(data);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  const fetchProductsSoldByAll = async () => {
    setBusy(true);
    setError('');
    try {
      const data = await queriesApi.getProductsSoldByAllCashiers();
      setProducts(data);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  const fetchCustomerCategoryPurchases = async () => {
    if (!yuraStartDate || !yuraEndDate || selectedCategory === '') {
      setError('Будь ласка, оберіть дати та категорію');
      return;
    }
    setBusy(true);
    setError('');
    try {
      const data = await queriesApi.getCustomerPurchasesByCategory(Number(selectedCategory), yuraStartDate, yuraEndDate);
      setCustomerPurchases(data);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  const fetchCategoriesBoughtByAll = async () => {
    setBusy(true);
    setError('');
    try {
      const data = await queriesApi.getCategoriesBoughtByAllCustomers();
      setCategoriesBoughtByAll(data);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <h1>Аналітика</h1>
      <p style={{ color: 'var(--muted)', maxWidth: 600 }}>
        Інформаційна панель для аналізу результатів діяльності магазину.
      </p>

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border)', marginBottom: '2rem' }}>
        <button
          type="button"
          style={{
            background: 'none', border: 'none', padding: '0.75rem 1rem', cursor: 'pointer',
            borderBottom: activeTab === 'q1' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'q1' ? 'var(--primary)' : 'var(--text)',
            fontWeight: activeTab === 'q1' ? 600 : 400
          }}
          onClick={() => { setActiveTab('q1'); setError(''); }}
        >
          Продажі за категоріями (А)
        </button>
        <button
          type="button"
          style={{
            background: 'none', border: 'none', padding: '0.75rem 1rem', cursor: 'pointer',
            borderBottom: activeTab === 'q2' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'q2' ? 'var(--primary)' : 'var(--text)',
            fontWeight: activeTab === 'q2' ? 600 : 400
          }}
          onClick={() => { setActiveTab('q2'); setError(''); }}
        >
          Популярні товари (А)
        </button>
        <button
          type="button"
          style={{
            background: 'none', border: 'none', padding: '0.75rem 1rem', cursor: 'pointer',
            borderBottom: activeTab === 'y1' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'y1' ? 'var(--primary)' : 'var(--text)',
            fontWeight: activeTab === 'y1' ? 600 : 400
          }}
          onClick={() => { setActiveTab('y1'); setError(''); }}
        >
          Покупки клієнтів (Ю)
        </button>
        <button
          type="button"
          style={{
            background: 'none', border: 'none', padding: '0.75rem 1rem', cursor: 'pointer',
            borderBottom: activeTab === 'y2' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'y2' ? 'var(--primary)' : 'var(--text)',
            fontWeight: activeTab === 'y2' ? 600 : 400
          }}
          onClick={() => { setActiveTab('y2'); setError(''); }}
        >
          Категорії-лідери (Ю)
        </button>
      </div>

      {error && <div className="alert error">{error}</div>}

      {activeTab === 'q1' && (
        <div style={{ background: 'var(--bg-elevated)', padding: '1.5rem', borderRadius: '8px', border: '1px solid var(--border)' }}>
          <h3>Продажі товарів за категоріями</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            Аналіз загальної кількості проданих одиниць та загальної суми продажів для кожної категорії товарів за вибраний період часу.
          </p>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap', marginBottom: '2rem' }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата початку</label>
              <input type="date" value={startDate} max={endDate || undefined} onChange={e => setStartDate(e.target.value)} />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата завершення</label>
              <input type="date" value={endDate} min={startDate || undefined} onChange={e => setEndDate(e.target.value)} />
            </div>
            <button className="btn primary" onClick={fetchCategorySales} disabled={busy}>
              {busy ? <Spinner /> : 'Виконати запит'}
            </button>
          </div>

          {sales && (
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>Категорія</th>
                    <th style={{ textAlign: 'right' }}>Продано одиниць</th>
                    <th style={{ textAlign: 'right' }}>Загальна сума (₴)</th>
                  </tr>
                </thead>
                <tbody>
                  {sales.length === 0 ? (
                    <tr>
                      <td colSpan={3} style={{ textAlign: 'center' }}>За вказаний період немає даних</td>
                    </tr>
                  ) : (
                    sales.map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.categoryName}</td>
                        <td style={{ textAlign: 'right' }}>{item.totalAmount}</td>
                        <td style={{ textAlign: 'right' }}>{item.totalSum.toFixed(2)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'q2' && (
        <div style={{ background: 'var(--bg-elevated)', padding: '1.5rem', borderRadius: '8px', border: '1px solid var(--border)' }}>
          <h3>Товари-лідери продажів</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            Перелік товарів, які користуються найвищим попитом та були продані хоча б один раз кожним касиром магазину.
          </p>

          <div style={{ marginBottom: '2rem' }}>
            <button className="btn primary" onClick={fetchProductsSoldByAll} disabled={busy}>
              {busy ? <Spinner /> : 'Знайти популярні товари'}
            </button>
          </div>

          {products && (
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID Товару</th>
                    <th>Назва товару</th>
                  </tr>
                </thead>
                <tbody>
                  {products.length === 0 ? (
                    <tr>
                      <td colSpan={2} style={{ textAlign: 'center' }}>Немає товарів, які б продали всі касири</td>
                    </tr>
                  ) : (
                    products.map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.idProduct}</td>
                        <td>{item.productName}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'y1' && (
        <div style={{ background: 'var(--bg-elevated)', padding: '1.5rem', borderRadius: '8px', border: '1px solid var(--border)' }}>
          <h3>Аналіз покупок клієнтів за категорією</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            Знайти загальну кількість куплених одиниць та загальну суму витрат для кожного клієнта (власника картки), які купували товари певної категорії за вибраний період часу.
          </p>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap', marginBottom: '2rem' }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Категорія</label>
              <select value={selectedCategory} onChange={e => setSelectedCategory(Number(e.target.value))}>
                <option value="">Оберіть категорію</option>
                {categories.map(c => (
                  <option key={c.categoryNumber} value={c.categoryNumber}>{c.categoryName}</option>
                ))}
              </select>
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата початку</label>
              <input type="date" value={yuraStartDate} max={yuraEndDate || undefined} onChange={e => setYuraStartDate(e.target.value)} />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата завершення</label>
              <input type="date" value={yuraEndDate} min={yuraStartDate || undefined} onChange={e => setYuraEndDate(e.target.value)} />
            </div>
            <button className="btn primary" onClick={fetchCustomerCategoryPurchases} disabled={busy}>
              {busy ? <Spinner /> : 'Виконати запит'}
            </button>
          </div>

          {customerPurchases && (
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>№ картки</th>
                    <th>Прізвище</th>
                    <th>Ім'я</th>
                    <th style={{ textAlign: 'right' }}>Куплено одиниць</th>
                    <th style={{ textAlign: 'right' }}>Загальна сума (₴)</th>
                  </tr>
                </thead>
                <tbody>
                  {customerPurchases.length === 0 ? (
                    <tr>
                      <td colSpan={5} style={{ textAlign: 'center' }}>За вказаний період немає даних</td>
                    </tr>
                  ) : (
                    customerPurchases.map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.cardNumber}</td>
                        <td>{item.custSurname}</td>
                        <td>{item.custName}</td>
                        <td style={{ textAlign: 'right' }}>{item.totalItems}</td>
                        <td style={{ textAlign: 'right' }}>{item.totalSpent.toFixed(2)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'y2' && (
        <div style={{ background: 'var(--bg-elevated)', padding: '1.5rem', borderRadius: '8px', border: '1px solid var(--border)' }}>
          <h3>Категорії-лідери (куплені всіма клієнтами)</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            Знайти категорії, з яких КОЖЕН клієнт (власник картки) купив хоча б один товар (подвійне заперечення).
          </p>

          <div style={{ marginBottom: '2rem' }}>
            <button className="btn primary" onClick={fetchCategoriesBoughtByAll} disabled={busy}>
              {busy ? <Spinner /> : 'Знайти категорії'}
            </button>
          </div>

          {categoriesBoughtByAll && (
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID Категорії</th>
                    <th>Назва категорії</th>
                  </tr>
                </thead>
                <tbody>
                  {categoriesBoughtByAll.length === 0 ? (
                    <tr>
                      <td colSpan={2} style={{ textAlign: 'center' }}>Немає категорій, які б купили всі клієнти</td>
                    </tr>
                  ) : (
                    categoriesBoughtByAll.map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.categoryNumber}</td>
                        <td>{item.categoryName}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
