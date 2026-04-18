export function compareValues(a, b, type = 'string') {
  if (a == null && b == null) return 0;
  if (a == null) return 1;
  if (b == null) return -1;
  if (type === 'number') {
    return Number(a) - Number(b);
  }
  return String(a).localeCompare(String(b), 'uk');
}

export function sortRows(rows, key, dir, type = 'string') {
  const mul = dir === 'desc' ? -1 : 1;
  return [...rows].sort(
    (x, y) => mul * compareValues(x[key], y[key], type)
  );
}
