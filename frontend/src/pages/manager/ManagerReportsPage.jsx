import { useState } from 'react';
import { employeesApi } from '../../api/employees.js';
import { customerCardsApi } from '../../api/customerCards.js';
import { categoriesApi } from '../../api/categories.js';
import { productsApi } from '../../api/products.js';
import { storeProductsApi } from '../../api/storeProducts.js';
import { checksApi } from '../../api/checks.js';
import { getApiErrorMessage } from '../../api/index.js';
import { downloadCsv, printHtml } from '../../utils/exportCsv.js';
import { formatDateTime, money } from '../../utils/formatters.js';
import { Spinner } from '../../components/Spinner.jsx';

export function ManagerReportsPage() {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const run = async (name, fn) => {
    setBusy(true);
    setError('');
    try {
      await fn();
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <h1>Звіти та експорт</h1>
      {error && <div className="alert error">{error}</div>}
      {busy && <Spinner label="Формування…" />}
      <p style={{ color: 'var(--muted)', maxWidth: 640 }}>
        Експорт у CSV або друк HTML. Дані завантажуються з API у момент натискання.
      </p>
      <div className="stack" style={{ gap: '0.75rem', maxWidth: 400 }}>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('employees', async () => {
              const { data } = await employeesApi.getOrderedBySurname();
              downloadCsv(
                'employees.csv',
                (data || []).map((e) => ({
                  id: e.idEmployee,
                  fullName: e.fullName,
                  role: e.emplRole,
                  salary: e.salary,
                  dateOfStart: e.dateOfStart,
                  phone: e.emplPhoneNumber,
                }))
              );
            })
          }
        >
          Працівники (CSV)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('clients', async () => {
              const { data } = await customerCardsApi.getAll();
              downloadCsv(
                'client-cards.csv',
                (data || []).map((c) => ({
                  card: c.cardNumber,
                  surname: c.custSurname,
                  firstName: c.custName,
                  patronymic: c.custPatronymic,
                  phone: c.custPhoneNumber,
                  discount: c.percent,
                }))
              );
            })
          }
        >
          Клієнти (CSV)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('categories', async () => {
              const { data } = await categoriesApi.getAll();
              downloadCsv(
                'categories.csv',
                (data || []).map((c) => ({
                  id: c.categoryNumber,
                  name: c.categoryName,
                }))
              );
            })
          }
        >
          Категорії (CSV)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('products', async () => {
              const { data } = await productsApi.getOrderedByName();
              downloadCsv(
                'products.csv',
                (data || []).map((p) => ({
                  id: p.idProduct,
                  name: p.productName,
                  manufacturer: p.manufacturer,
                  characteristics: p.characteristics,
                  category: p.categoryName,
                }))
              );
            })
          }
        >
          Товари (CSV)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('store', async () => {
              const { data } = await storeProductsApi.getAll();
              downloadCsv(
                'store-products.csv',
                (data || []).map((s) => ({
                  upc: s.upc,
                  product: s.product?.productName,
                  price: s.sellingPrice,
                  qty: s.productsNumber,
                  promotional: s.promotionalProduct,
                }))
              );
            })
          }
        >
          Товар у магазині (CSV)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('receipts-print', async () => {
              const { data } = await checksApi.getAll();
              const rows = (data || [])
                .map(
                  (c) =>
                    `<tr><td>${c.checkNumber}</td><td>${c.employeeName}</td><td>${formatDateTime(c.printDate)}</td><td>${money(c.sumTotal)}</td><td>${money(c.vat)}</td></tr>`
                )
                .join('');
              printHtml(
                'receipts',
                `<h1>Чеки</h1><table border="1" cellspacing="0" cellpadding="4"><thead><tr><th>№</th><th>Касир</th><th>Дата</th><th>Сума</th><th>ПДВ</th></tr></thead><tbody>${rows}</tbody></table>`
              );
            })
          }
        >
          Чеки (друк HTML)
        </button>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run('receipts-csv', async () => {
              const { data } = await checksApi.getAll();
              downloadCsv(
                'receipts.csv',
                (data || []).map((c) => ({
                  check: c.checkNumber,
                  cashier: c.employeeName,
                  employeeId: c.employeeId,
                  date: c.printDate,
                  sum: c.sumTotal,
                  vat: c.vat,
                }))
              );
            })
          }
        >
          Чеки (CSV)
        </button>
      </div>
    </div>
  );
}
