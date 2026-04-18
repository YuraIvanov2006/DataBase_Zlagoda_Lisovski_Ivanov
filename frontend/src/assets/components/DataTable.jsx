import styles from './DataTable.module.css';

export function DataTable({
  columns,
  rows,
  rowKey,
  sortState,
  onSort,
  emptyText = 'Немає даних',
}) {
  return (
    <div className={styles.scroll}>
      <table className={styles.table}>
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.key} className={styles.th}>
                {col.sortable ? (
                  <button
                    type="button"
                    className={styles.sortBtn}
                    onClick={() => onSort?.(col.key, col.sortType || 'string')}
                  >
                    {col.label}
                    {sortState?.key === col.key
                      ? sortState.dir === 'asc'
                        ? ' ▲'
                        : ' ▼'
                      : ''}
                  </button>
                ) : (
                  col.label
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className={styles.empty}>
                {emptyText}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={rowKey(row)}>
                {columns.map((col) => (
                  <td key={col.key} className={styles.td}>
                    {col.render ? col.render(row) : row[col.key]}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
