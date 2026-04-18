import type { ReactNode } from 'react';
import type { SortValueType } from '../utils/sort';
import styles from './DataTable.module.css';

export type TableSortState = {
  key: string;
  dir: 'asc' | 'desc';
  type?: SortValueType;
};

export type DataColumn<T> = {
  key: string;
  label: string;
  sortable?: boolean;
  sortType?: SortValueType;
  render?: (row: T) => ReactNode;
};

type DataTableProps<T> = {
  columns: DataColumn<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  sortState?: TableSortState;
  onSort?: (key: string, type?: SortValueType) => void;
  emptyText?: string;
};

export function DataTable<T>({
  columns,
  rows,
  rowKey,
  sortState,
  onSort,
  emptyText = 'Немає даних',
}: DataTableProps<T>) {
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
                    {col.render
                      ? col.render(row)
                      : String(
                          (row as Record<string, unknown>)[col.key] ?? ''
                        )}
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
