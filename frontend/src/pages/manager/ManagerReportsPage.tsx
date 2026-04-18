import { useState } from 'react';
import { employeesApi } from '../../api/employees';
import { customerCardsApi } from '../../api/customerCards';
import { categoriesApi } from '../../api/categories';
import { productsApi } from '../../api/products';
import { storeProductsApi } from '../../api/storeProducts';
import { checksApi } from '../../api/checks';
import { getApiErrorMessage } from '../../api/index';
import { downloadCsv, printHtml } from '../../utils/exportCsv';
import { formatDateTime, money } from '../../utils/formatters';
import { Spinner } from '../../components/Spinner';

export function ManagerReportsPage() {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const run = async (fn: () => Promise<void>) => {
    setBusy(true);
    setError('');
    try {
      await fn();
    } catch (e: unknown) {
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
        Експорт у CSV або друк HTML. Дані завантажуються з API у момент
        натискання.
      </p>
      <div className="stack" style={{ gap: '0.75rem', maxWidth: 400 }}>
        <button
          type="button"
          className="btn secondary"
          disabled={busy}
          onClick={() =>
            run(async () => {
              const { data } = await employeesApi.getOrderedBySurname();
              downloadCsv(
                'employees.csv',
                ((data as Record<string, unknown>[]) || []).map((e) => ({
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
            run(async () => {
              const { data } = await customerCardsApi.getAll();
              downloadCsv(
                'client-cards.csv',
                ((data as Record<string, unknown>[]) || []).map((c) => ({
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
            run(async () => {
              const { data } = await categoriesApi.getAll();
              downloadCsv(
                'categories.csv',
                ((data as Record<string, unknown>[]) || []).map((c) => ({
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
            run(async () => {
              const { data } = await productsApi.getOrderedByName();
              downloadCsv(
                'products.csv',
                ((data as Record<string, unknown>[]) || []).map((p) => ({
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
            run(async () => {
              const { data } = await storeProductsApi.getAll();
              downloadCsv(
                'store-products.csv',
                ((data as Record<string, unknown>[]) || []).map((s) => {
                  const product = s.product as
                    | { productName?: string }
                    | undefined;
                  return {
                    upc: s.upc,
                    product: product?.productName,
                    price: s.sellingPrice,
                    qty: s.productsNumber,
                    promotional: s.promotionalProduct,
                  };
                })
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
            run(async () => {
              const { data } = await checksApi.getAll();
              const rows = ((data as Record<string, unknown>[]) || [])
                .map(
                  (c) =>
                    `<tr><td>${c.checkNumber}</td><td>${c.employeeName}</td><td>${formatDateTime(String(c.printDate))}</td><td>${money(c.sumTotal)}</td><td>${money(c.vat)}</td></tr>`
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
            run(async () => {
              const { data } = await checksApi.getAll();
              downloadCsv(
                'receipts.csv',
                ((data as Record<string, unknown>[]) || []).map((c) => ({
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
