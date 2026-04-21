import { useState } from 'react';
import { queriesApi, CategorySale, ProductSoldByAll } from '../../api/queries';
import { getApiErrorMessage } from '../../api/index';
import { Spinner } from '../../components/Spinner';

export function ManagerComplexQueriesPage() {
  const [activeTab, setActiveTab] = useState<'q1' | 'q2'>('q1');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  // Tab 1 state
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [sales, setSales] = useState<CategorySale[] | null>(null);

  // Tab 2 state
  const [products, setProducts] = useState<ProductSoldByAll[] | null>(null);

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

  return (
    <div>
      <h1>Складні запити</h1>
      <p style={{ color: 'var(--muted)', maxWidth: 800 }}>
        Виконання та візуалізація складних аналітичних SQL-запитів до бази даних ZLAGODA.
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
          Продажі за категоріями (Групування)
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
          Популярні товари (Подвійне заперечення)
        </button>
      </div>

      {error && <div className="alert error">{error}</div>}

      {activeTab === 'q1' && (
        <div style={{ background: 'var(--bg-elevated)', padding: '1.5rem', borderRadius: '8px', border: '1px solid var(--border)' }}>
          <h3>Запит №1: Продажі товарів за категоріями</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            <strong>Умова:</strong> Отримати загальну кількість проданих одиниць та загальну суму продажів для кожної категорії товарів за вибраний період часу.
          </p>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap', marginBottom: '2rem' }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата початку</label>
              <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Дата завершення</label>
              <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
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
          <h3>Запит №2: Товари, продані всіма касирами</h3>
          <p style={{ color: 'var(--muted)', marginBottom: '1.5rem' }}>
            <strong>Умова:</strong> Знайти товари, які були продані хоча б один раз КОЖНИМ касиром (використання подвійного заперечення).
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
    </div>
  );
}
