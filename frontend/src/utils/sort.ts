export type SortValueType = 'string' | 'number';

export function compareValues(
  a: unknown,
  b: unknown,
  type: SortValueType = 'string'
): number {
  if (a == null && b == null) return 0;
  if (a == null) return 1;
  if (b == null) return -1;
  if (type === 'number') {
    return Number(a) - Number(b);
  }
  return String(a).localeCompare(String(b), 'uk');
}

export function sortRows<T>(
  rows: readonly T[],
  key: string,
  dir: 'asc' | 'desc',
  type: SortValueType = 'string'
): T[] {
  const mul = dir === 'desc' ? -1 : 1;
  return [...rows].sort((x, y) => {
    const vx =
      x && typeof x === 'object'
        ? (x as Record<string, unknown>)[key]
        : undefined;
    const vy =
      y && typeof y === 'object'
        ? (y as Record<string, unknown>)[key]
        : undefined;
    return mul * compareValues(vx, vy, type);
  });
}
