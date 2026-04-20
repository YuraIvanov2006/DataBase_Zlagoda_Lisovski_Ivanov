import { useCallback, useEffect, useMemo, useState } from 'react';
import { checksApi } from '../../api/checks';
import { salesApi } from '../../api/sales';
import { employeesApi } from '../../api/employees';
import { getApiErrorMessage } from '../../api/index';
import { DataTable, type DataColumn, type TableSortState } from '../../components/DataTable';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { Spinner } from '../../components/Spinner';
import { formatDateTime, money } from '../../utils/formatters';
import { sortRows, type SortValueType } from '../../utils/sort';

type CheckRow = {
  checkNumber: string;
  employeeId: number;
  employeeName: string;
  printDate: string;
  sumTotal: unknown;
  vat: unknown;
};

type SaleLine = {
  upc: string;
  checkNumber: string;
  productName?: string;
  productNumber: unknown;
  sellingPrice: unknown;
  totalRowPrice: unknown;
};

type CashierOpt = { idEmployee: number; fullName: string };

function parseDayStart(isoDate: string) {
  return new Date(isoDate + 'T00:00:00');
}

function parseDayEnd(isoDate: string) {
  return new Date(isoDate + 'T23:59:59.999');
}

function inRange(isoDateTime: string, from: Date, to: Date) {
  const t = new Date(isoDateTime).getTime();
  return t >= from.getTime() && t <= to.getTime();
}

export function ManagerReceiptsPage() {
  const [checks, setChecks] = useState<CheckRow[]>([]);
  const [cashiers, setCashiers] = useState<CashierOpt[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cashierId, setCashierId] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [upcProduct, setUpcProduct] = useState('');
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [itemsCache, setItemsCache] = useState<Record<string, SaleLine[]>>({});
  const [deleteTarget, setDeleteTarget] = useState<CheckRow | null>(null);
  const [sortState, setSortState] = useState<TableSortState>({
    key: 'printDate',
    dir: 'desc',
    type: 'string',
  });

  const [totalSumCashier, setTotalSumCashier] = useState<number | null>(null);
  const [totalSumAllInPeriod, setTotalSumAllInPeriod] = useState<number | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const fromD = dateFrom ? parseDayStart(dateFrom).toISOString() : undefined;
      const toD = dateTo ? parseDayEnd(dateTo).toISOString() : undefined;
      const params: any = {};
      if (cashierId) params.employeeId = Number(cashierId);
      if (fromD && toD) {
        params.from = fromD;
        params.to = toD;
      }

      const chReq = checksApi.getFiltered(params);
      let sumReq = null;
      let sumAllReq = null;
      if (fromD && toD) {
        sumReq = checksApi.getSum(params as any);
        sumAllReq = checksApi.getSum({ from: fromD, to: toD });
      }

      const [ch, em, sumRes, sumAllRes] = await Promise.all([
        chReq,
        employeesApi.getCashiers(),
        sumReq || Promise.resolve(null),
        sumAllReq || Promise.resolve(null)
      ]);
      setChecks((ch.data as CheckRow[]) || []);
      setCashiers((em.data as CashierOpt[]) || []);
      
      // Якщо немає діапазону, рахуємо локально з отриманих чеків
      if (!sumRes) {
        setTotalSumCashier((ch.data as CheckRow[]).reduce((s, c) => s + Number(c.sumTotal || 0), 0));
      } else {
        setTotalSumCashier(Number(sumRes.data) || 0);
      }
      
      if (sumAllRes) {
        setTotalSumAllInPeriod(Number(sumAllRes.data) || 0);
      } else {
        setTotalSumAllInPeriod(null);
      }
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, [cashierId, dateFrom, dateTo]);

  useEffect(() => {
    load();
  }, [load]);

  const fromD = dateFrom ? parseDayStart(dateFrom) : null;
  const toD = dateTo ? parseDayEnd(dateTo) : null;

  const sortedRows = useMemo(
    () =>
      sortRows(
        checks,
        sortState.key,
        sortState.dir,
        (sortState.type || 'string') as SortValueType
      ),
    [checks, sortState]
  );

  const [unitsSold, setUnitsSold] = useState<number | null>(null);
  const [unitsLoading, setUnitsLoading] = useState(false);

  const computeUnitsSold = async () => {
    if (!upcProduct.trim() || !dateFrom || !dateTo) {
      setError('Вкажіть UPC та діапазон дат');
      return;
    }
    setUnitsLoading(true);
    setError('');
    try {
      const from = parseDayStart(dateFrom).toISOString();
      const to = parseDayEnd(dateTo).toISOString();
      const res = await salesApi.totalSoldByUpcAndPeriod(upcProduct.trim(), { from, to });
      setUnitsSold(Number(res.data) || 0);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
      setUnitsSold(null);
    } finally {
      setUnitsLoading(false);
    }
  };

  const toggleExpand = async (checkNumber: string) => {
    setExpanded((e) => ({ ...e, [checkNumber]: !e[checkNumber] }));
    if (!itemsCache[checkNumber]) {
      try {
        const { data } = await salesApi.getByCheck(checkNumber);
        setItemsCache((c) => ({ ...c, [checkNumber]: (data as SaleLine[]) || [] }));
      } catch (err: unknown) {
        setError(getApiErrorMessage(err));
      }
    }
  };

  const onSort = (key: string, type?: SortValueType) => {
    setSortState((s) => ({
      key,
      type: type || 'string',
      dir: s.key === key && s.dir === 'asc' ? 'desc' : 'asc',
    }));
  };

  const doDelete = async () => {
    if (!deleteTarget) return;
    try {
      await checksApi.delete(deleteTarget.checkNumber);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    }
  };

  const columns: DataColumn<CheckRow>[] = [
    {
      key: '_exp',
      label: '',
      render: (r) => (
        <button
          type="button"
          className="btn secondary small"
          onClick={() => toggleExpand(r.checkNumber)}
        >
          {expanded[r.checkNumber] ? '▼' : '▶'}
        </button>
      ),
    },
    { key: 'checkNumber', label: 'Чек', sortable: true },
    { key: 'employeeName', label: 'Касир', sortable: true },
    {
      key: 'employeeId',
      label: 'ID касира',
      sortable: true,
      sortType: 'number',
    },
    {
      key: 'printDate',
      label: 'Дата',
      sortable: true,
      render: (r) => formatDateTime(r.printDate),
    },
    {
      key: 'sumTotal',
      label: 'Сума',
      sortable: true,
      sortType: 'number',
      render: (r) => money(r.sumTotal),
    },
    {
      key: 'vat',
      label: 'ПДВ',
      sortable: true,
      sortType: 'number',
      render: (r) => money(r.vat),
    },
    {
      key: '_a',
      label: '',
      render: (r) => (
        <button
          type="button"
          className="btn danger small"
          onClick={() => setDeleteTarget(r)}
        >
          Видалити
        </button>
      ),
    },
  ];

  if (loading && !checks.length) return <Spinner />;

  return (
    <div>
      <h1>Чеки</h1>
      {error && <div className="alert error">{error}</div>}
      <div className="toolbar">
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          Касир
          <select
            value={cashierId}
            onChange={(e) => setCashierId(e.target.value)}
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
              minWidth: 200,
            }}
          >
            <option value="">Усі касири</option>
            {cashiers.map((c) => (
              <option key={c.idEmployee} value={c.idEmployee}>
                {c.fullName} (ID {c.idEmployee})
              </option>
            ))}
          </select>
        </label>
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          Від
          <input
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
          />
        </label>
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          До
          <input
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
          />
        </label>
      </div>
      <div className="alert info stack" style={{ marginBottom: '1rem' }}>
        <span>
          <strong>Сума за фільтром (касир + діапазон):</strong>{' '}
          {money(totalSumCashier)}
        </span>
        {totalSumAllInPeriod != null && (
          <span>
            <strong>Сума всіх касирів за період:</strong>{' '}
            {money(totalSumAllInPeriod)}
          </span>
        )}
      </div>
      <div className="toolbar">
        <label
          className="stack"
          style={{ fontSize: '0.85rem', color: 'var(--muted)' }}
        >
          UPC для підрахунку одиниць
          <input
            value={upcProduct}
            onChange={(e) => setUpcProduct(e.target.value)}
            placeholder="UPC"
            style={{
              padding: '0.45rem',
              borderRadius: 8,
              border: '1px solid var(--border)',
            }}
          />
        </label>
        <button
          type="button"
          className="btn secondary"
          onClick={computeUnitsSold}
          disabled={unitsLoading}
        >
          Підрахувати одиниці за період
        </button>
        {unitsSold != null && (
          <span style={{ alignSelf: 'center' }}>
            Продано шт.: <strong>{unitsSold}</strong>
          </span>
        )}
      </div>
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.checkNumber}
        sortState={sortState}
        onSort={onSort}
      />
      {sortedRows.map((r) =>
        expanded[r.checkNumber] ? (
          <div
            key={`d-${r.checkNumber}`}
            className="alert info"
            style={{ marginTop: '0.5rem' }}
          >
            <strong>Позиції чеку {r.checkNumber}</strong>
            <table className="inner-table">
              <thead>
                <tr>
                  <th>UPC</th>
                  <th>Товар</th>
                  <th>К-сть</th>
                  <th>Ціна</th>
                  <th>Рядок</th>
                </tr>
              </thead>
              <tbody>
                {(itemsCache[r.checkNumber] || []).map((s) => (
                  <tr key={`${s.upc}-${s.checkNumber}`}>
                    <td>{s.upc}</td>
                    <td>{s.productName}</td>
                    <td>{s.productNumber}</td>
                    <td>{money(s.sellingPrice)}</td>
                    <td>{money(s.totalRowPrice)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null
      )}
      {deleteTarget && (
        <ConfirmDialog
          message={`Видалити чек ${deleteTarget.checkNumber}?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
      <style>{`
        .inner-table { width: 100%; border-collapse: collapse; margin-top: 0.5rem; font-size: 0.85rem; }
        .inner-table th, .inner-table td { border: 1px solid var(--border); padding: 0.35rem 0.5rem; text-align: left; }
      `}</style>
    </div>
  );
}
