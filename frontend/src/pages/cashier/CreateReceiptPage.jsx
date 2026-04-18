import { useCallback, useEffect, useMemo, useState } from 'react';
import { customerCardsApi } from '../../api/customerCards.js';
import { storeProductsApi } from '../../api/storeProducts.js';
import { checksApi } from '../../api/checks.js';
import { getApiErrorMessage } from '../../api/index.js';
import { useAuth } from '../../context/AuthContext.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { money } from '../../utils/formatters.js';
import { promotionalUnitPrice } from '../../utils/formatters.js';
import { generateCheckNumber } from '../../utils/validators.js';

export function CreateReceiptPage() {
  const { employeeId } = useAuth();
  const [cards, setCards] = useState([]);
  const [storeItems, setStoreItems] = useState([]);
  const [stockMap, setStockMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [lines, setLines] = useState([]);
  const [upcInput, setUpcInput] = useState('');
  const [qtyInput, setQtyInput] = useState('1');
  const [submitting, setSubmitting] = useState(false);

  const refreshData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [c, s] = await Promise.all([
        customerCardsApi.getAll(),
        storeProductsApi.getAll(),
      ]);
      setCards(c.data || []);
      const items = s.data || [];
      setStoreItems(items);
      const m = {};
      items.forEach((i) => {
        m[i.upc] = i.productsNumber;
      });
      setStockMap(m);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshData();
  }, [refreshData]);

  const selectedCard = useMemo(() => {
    if (!cardNumber) return null;
    return cards.find((c) => c.cardNumber === cardNumber) || null;
  }, [cards, cardNumber]);

  const lineDetails = useMemo(() => {
    return lines.map((ln) => {
      const sp = storeItems.find((s) => s.upc === ln.upc);
      const unit = sp
        ? promotionalUnitPrice(sp.sellingPrice, sp.promotionalProduct)
        : 0;
      const row = unit * ln.qty;
      return { ...ln, productName: sp?.product?.productName, unit, row };
    });
  }, [lines, storeItems]);

  const subtotal = useMemo(
    () => lineDetails.reduce((a, l) => a + l.row, 0),
    [lineDetails]
  );

  const discountPct = selectedCard?.percent ?? 0;
  const discountAmt = useMemo(
    () => subtotal * (discountPct / 100),
    [subtotal, discountPct]
  );

  const afterDiscount = subtotal - discountAmt;
  const vat = afterDiscount * 0.2;
  const toPay = afterDiscount + vat;

  const projectedStock = useMemo(() => {
    const m = { ...stockMap };
    lines.forEach((l) => {
      m[l.upc] = (m[l.upc] ?? 0) - l.qty;
    });
    return m;
  }, [stockMap, lines]);

  const addLine = () => {
    setError('');
    const upc = upcInput.trim();
    const qty = Number(qtyInput);
    if (!upc) {
      setError('Вкажіть UPC');
      return;
    }
    if (!Number.isInteger(qty) || qty <= 0) {
      setError('Кількість — ціле число > 0');
      return;
    }
    const sp = storeItems.find((s) => s.upc === upc);
    if (!sp) {
      setError('Товар з таким UPC не знайдено');
      return;
    }
    const already = lines.find((l) => l.upc === upc)?.qty || 0;
    const nextTotal = already + qty;
    const stock = stockMap[upc] ?? 0;
    if (nextTotal > stock) {
      setError(`Недостатньо на складі для ${upc} (є ${stock})`);
      return;
    }
    setLines((prev) => {
      const ix = prev.findIndex((l) => l.upc === upc);
      if (ix >= 0) {
        const n = [...prev];
        n[ix] = { ...n[ix], qty: n[ix].qty + qty };
        return n;
      }
      return [...prev, { upc, qty }];
    });
    setUpcInput('');
    setQtyInput('1');
  };

  const removeLine = (upc) => {
    setLines((prev) => prev.filter((l) => l.upc !== upc));
  };

  const submit = async () => {
    if (!lines.length) {
      setError('Додайте позиції');
      return;
    }
    setSubmitting(true);
    setError('');
    const checkNumber = generateCheckNumber(10);
    const body = {
      checkNumber,
      idEmployee: employeeId,
      cardNumber: cardNumber || null,
      sales: lines.map((l) => ({
        upc: l.upc,
        productNumber: l.qty,
      })),
    };
    try {
      await checksApi.create(body);
      setLines([]);
      setCardNumber('');
      await refreshData();
      alert(`Чек ${checkNumber} створено (якщо бекенд прийняв запит).`);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading && !storeItems.length) return <Spinner />;

  return (
    <div>
      <h1>Продаж (новий чек)</h1>
      {error && <div className="alert error">{error}</div>}
      <p className="alert info">
        Одна картка клієнта на чек — знижка застосовується до підсумку рядків.
        ПДВ 20% від суми після знижки: ПДВ = сума × 0.2, до сплати = сума +
        ПДВ.
      </p>
      <div className="form-grid" style={{ maxWidth: 720 }}>
        <label className="stack" style={{ gridColumn: '1 / -1' }}>
          Картка клієнта (необовʼязково)
          <select
            value={cardNumber}
            onChange={(e) => setCardNumber(e.target.value)}
          >
            <option value="">Без картки</option>
            {cards.map((c) => (
              <option key={c.cardNumber} value={c.cardNumber}>
                {c.cardNumber} — {c.custSurname} {c.custName} ({c.percent}%)
              </option>
            ))}
          </select>
        </label>
        <label>
          UPC
          <input
            value={upcInput}
            onChange={(e) => setUpcInput(e.target.value)}
          />
        </label>
        <label>
          Кількість
          <input
            type="number"
            min="1"
            step="1"
            value={qtyInput}
            onChange={(e) => setQtyInput(e.target.value)}
          />
        </label>
        <div style={{ alignSelf: 'end' }}>
          <button type="button" className="btn primary" onClick={addLine}>
            Додати в чек
          </button>
        </div>
      </div>
      <h2 style={{ marginTop: '1.5rem', fontSize: '1.1rem' }}>Позиції</h2>
      <table className="inner-table">
        <thead>
          <tr>
            <th>UPC</th>
            <th>Назва</th>
            <th>К-сть</th>
            <th>Ціна</th>
            <th>Сума</th>
            <th>Залишок (прогноз)</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {lineDetails.map((l) => (
            <tr key={l.upc}>
              <td>{l.upc}</td>
              <td>{l.productName}</td>
              <td>{l.qty}</td>
              <td>{money(l.unit)}</td>
              <td>{money(l.row)}</td>
              <td>{projectedStock[l.upc] ?? '—'}</td>
              <td>
                <button
                  type="button"
                  className="btn danger small"
                  onClick={() => removeLine(l.upc)}
                >
                  ✕
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="alert info stack" style={{ marginTop: '1rem', maxWidth: 400 }}>
        <span>Підсумок рядків: {money(subtotal)}</span>
        <span>
          Знижка ({discountPct}%): −{money(discountAmt)}
        </span>
        <span>Після знижки: {money(afterDiscount)}</span>
        <span>ПДВ 20%: {money(vat)}</span>
        <strong>До сплати: {money(toPay)}</strong>
      </div>
      <button
        type="button"
        className="btn primary"
        onClick={submit}
        disabled={submitting || !lines.length}
      >
        Оформити чек
      </button>
      <style>{`
        .inner-table { width: 100%; border-collapse: collapse; margin-top: 0.5rem; font-size: 0.9rem; background: var(--surface); border: 1px solid var(--border); border-radius: 8px; overflow: hidden; }
        .inner-table th, .inner-table td { border-bottom: 1px solid var(--border); padding: 0.45rem 0.6rem; text-align: left; }
        .inner-table th { background: var(--surface-2); }
      `}</style>
    </div>
  );
}
