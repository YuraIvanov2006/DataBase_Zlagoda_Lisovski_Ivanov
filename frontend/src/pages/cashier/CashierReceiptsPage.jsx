import { useCallback, useEffect, useMemo, useState } from 'react';
import { checksApi } from '../../api/checks.js';
import { salesApi } from '../../api/sales.js';
import { getApiErrorMessage } from '../../api/index.js';
import { useAuth } from '../../context/AuthContext.jsx';
import { DataTable } from '../../components/DataTable.jsx';
import { Modal } from '../../components/Modal.jsx';
import { Spinner } from '../../components/Spinner.jsx';
import { formatDateTime, money } from '../../utils/formatters.js';
import { sortRows } from '../../utils/sort.js';

function parseDayStart(isoDate) {
  return new Date(isoDate + 'T00:00:00');
}

function parseDayEnd(isoDate) {
  return new Date(isoDate + 'T23:59:59.999');
}

function inRange(isoDateTime, from, to) {
  const t = new Date(isoDateTime).getTime();
  return t >= from.getTime() && t <= to.getTime();
}

function isToday(isoDateTime) {
  const d = new Date(isoDateTime);
  const n = new Date();
  return (
    d.getFullYear() === n.getFullYear() &&
    d.getMonth() === n.getMonth() &&
    d.getDate() === n.getDate()
  );
}

export function CashierReceiptsPage() {
  const { employeeId } = useAuth();
  const [checks, setChecks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [mode, setMode] = useState('today');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [detail, setDetail] = useState(null);
  const [detailItems, setDetailItems] = useState([]);
  const [sortState, setSortState] = useState({
    key: 'printDate',
    dir: 'desc',
    type: 'string',
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await checksApi.getAll();
      setChecks(data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const mine = useMemo(
    () => checks.filter((c) => String(c.employeeId) === String(employeeId)),
    [checks, employeeId]
  );

  const filtered = useMemo(() => {
    if (mode === 'today') return mine.filter((c) => isToday(c.printDate));
    if (mode === 'range' && dateFrom && dateTo) {
      const from = parseDayStart(dateFrom);
      const to = parseDayEnd(dateTo);
      return mine.filter((c) => inRange(c.printDate, from, to));
    }
    return mine;
  }, [mine, mode, dateFrom, dateTo]);

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

  const openDetail = async (row) => {
    setDetail(row);
    setDetailItems([]);
    try {
      const { data } = await salesApi.getByCheck(row.checkNumber);
      setDetailItems(data || []);
    } catch (e) {
      setError(getApiErrorMessage(e));
    }
  };

  const columns = [
    { key: 'checkNumber', label: 'Чек', sortable: true },
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
      key: '_a',
      label: '',
      render: (r) => (
        <button
          type="button"
          className="btn secondary small"
          onClick={() => openDetail(r)}
        >
          Деталі
        </button>
      ),
    },
  ];

  if (loading && !checks.length) return <Spinner />;

  return (
    <div>
      <h1>Мої чеки</h1>
      {error && <div className="alert error">{error}</div>}
      <div className="toolbar">
        <button
          type="button"
          className={`btn ${mode === 'today' ? 'primary' : 'secondary'}`}
          onClick={() => setMode('today')}
        >
          Сьогодні
        </button>
        <button
          type="button"
          className={`btn ${mode === 'range' ? 'primary' : 'secondary'}`}
          onClick={() => setMode('range')}
        >
          Діапазон дат
        </button>
        {mode === 'range' && (
          <>
            <input
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
            />
            <input
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
            />
          </>
        )}
      </div>
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.checkNumber}
        sortState={sortState}
        onSort={onSort}
      />
      {detail && (
        <Modal
          title={`Чек ${detail.checkNumber}`}
          onClose={() => setDetail(null)}
          wide
        >
          <p>
            {formatDateTime(detail.printDate)} · Сума:{' '}
            {money(detail.sumTotal)} · ПДВ: {money(detail.vat)}
          </p>
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
              {detailItems.map((s) => (
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
          <style>{`
            .inner-table { width: 100%; border-collapse: collapse; margin-top: 0.5rem; font-size: 0.9rem; }
            .inner-table th, .inner-table td { border: 1px solid var(--border); padding: 0.35rem 0.5rem; }
          `}</style>
        </Modal>
      )}
    </div>
  );
}
