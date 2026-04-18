export function money(n) {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (Number.isNaN(x)) return String(n);
  return x.toFixed(2);
}

export function formatDateTime(iso) {
  if (!iso) return '—';
  try {
    const d = new Date(iso);
    return d.toLocaleString('uk-UA');
  } catch {
    return iso;
  }
}

export function formatDateInput(iso) {
  if (!iso) return '';
  return String(iso).slice(0, 10);
}

export function parseFullName(fullName) {
  if (!fullName || !fullName.trim()) {
    return { surname: '', firstName: '', patronymic: '' };
  }
  const parts = fullName.trim().split(/\s+/);
  return {
    surname: parts[0] || '',
    firstName: parts[1] || '',
    patronymic: parts.slice(2).join(' ') || '',
  };
}

export function promotionalUnitPrice(sellingPrice, promotional) {
  const p = Number(sellingPrice);
  if (Number.isNaN(p)) return 0;
  return promotional ? p * 0.8 : p;
}
