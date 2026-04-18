import { useCallback, useEffect, useMemo, useState } from 'react';
import { checksApi } from '../../api/checks.js';
import { salesApi } from '../../api/sales.js';
import { employeesApi } from '../../api/employees.js';
import { getApiErrorMessage } from '../../api/index.js';
import { DataTable } from '../../components/DataTable.jsx';
import { ConfirmDialog } from '../../components/ConfirmDialog.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { formatDateTime, money } from '../../utils/formatters.js';
import { sortRows } from '../../utils/sort.js';

function parseDayStart(isoDate) {
  const d = new Date(isoDate + 'T00:00:00');
  return d;
}

function parseDayEnd(isoDate) {
  const d = new Date(isoDate + 'T23:59:59.999');
  return d;
}

function inRange(isoDateTime, from, to) {
  const t = new Date(isoDateTime).getTime();
  return t >= from.getTime() && t <= to.getTime();
}

export function ManagerReceiptsPage() {
  const [checks, setChecks] = useState([]);
  const [cashiers, setCashiers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cashierId, setCashierId] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [upcProduct, setUpcProduct] = useState('');
  const [expanded, setExpanded] = useState({});
  const [itemsCache, setItemsCache] = useState({});
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [sortState, setSortState] = useState({
    key: 'printDate',
    dir: 'desc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [ch, em] = await Promise.all([
        checksApi.getAll(),
        employeesApi.getCashiers(),
      ]);
      setChecks(ch.data || []);
      setCashiers(em.data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const fromD = dateFrom ? parseDayStart(dateFrom) : null;
  const toD = dateTo ? parseDayEnd(dateTo) : null;

  const filtered = useMemo(() => {
    return checks.filter((c) => {
      if (cashierId && String(c.employeeId) !== String(cashierId))
        return false;
      if (fromD && toD && !inRange(c.printDate, fromD, toD)) return false;
      return true;
    });
  }, [checks, cashierId, fromD, toD, dateFrom, dateTo]);

  const sortedRows = useMemo(
    () => sortRows(filtered, sortState.key, sortState.dir, sortState.type),
    [filtered, sortState]
  );

  const totalSumCashier = useMemo(() => {
    return filtered.reduce(
      (s, c) => s + Number(c.sumTotal || 0),
      0
    );
  }, [filtered]);

  const totalSumAllInPeriod = useMemo(() => {
    if (!fromD || !toD) return null;
    return checks
      .filter((c) => inRange(c.printDate, fromD, toD))
      .reduce((s, c) => s + Number(c.sumTotal || 0), 0);
  }, [checks, fromD, toD, dateFrom, dateTo]);

  const [unitsSold, setUnitsSold] = useState(null);
  const [unitsLoading, setUnitsLoading] = useState(false);

  const computeUnitsSold = async () => {
    if (!upcProduct.trim() || !fromD || !toD) {
      setError('Вкажіть UPC та діапазон дат');
      return;
    }
    setUnitsLoading(true);
    setError('');
    try {
      const [salesRes] = await Promise.all([
        salesApi.getByUpc(upcProduct.trim()),
      ]);
      const sales = salesRes.data || [];
      const checkSet = new Set(
        checks
          .filter((c) => inRange(c.printDate, fromD, toD))
          .map((c) => c.checkNumber)
      );
      const n = sales
        .filter((s) => checkSet.has(s.checkNumber))
        .reduce((a, s) => a + Number(s.productNumber || 0), 0);
      setUnitsSold(n);
    } catch (e) {
      setError(getApiErrorMessage(e));
      setUnitsSold(null);
    } finally {
      setUnitsLoading(false);
    }
  };

  const toggleExpand = async (checkNumber) => {
    setExpanded((e) => ({ ...e, [checkNumber]: !e[checkNumber] }));
    if (!itemsCache[checkNumber]) {
      try {
        const { data } = await salesApi.getByCheck(checkNumber);
        setItemsCache((c) => ({ ...c, [checkNumber]: data || [] }));
      } catch (err) {
        setError(getApiErrorMessage(err));
      }
    }
  };

  const onSort = (key, type) => {
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
    } catch (e) {
      setError(getApiErrorMessage(e));
    }
  };

  const columns = [
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
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          Касир
          <select
            value={cashierId}
            onChange={(e) => setCashierId(e.target.value)}
            style={{ padding: '0.45rem', borderRadius: 8, border: '1px solid var(--border)', minWidth: 200 }}
          >
            <option value="">Усі касири</option>
            {cashiers.map((c) => (
              <option key={c.idEmployee} value={c.idEmployee}>
                {c.fullName} (ID {c.idEmployee})
              </option>
            ))}
          </select>
        </label>
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          Від
          <input
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
          />
        </label>
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
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
        <label className="stack" style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>
          UPC для підрахунку одиниць
          <input
            value={upcProduct}
            onChange={(e) => setUpcProduct(e.target.value)}
            placeholder="UPC"
            style={{ padding: '0.45rem', borderRadius: 8, border: '1px solid var(--border)' }}
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
