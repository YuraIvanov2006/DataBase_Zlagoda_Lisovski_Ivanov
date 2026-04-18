export function isAdultBirthDate(isoDateStr: string): boolean {
  if (!isoDateStr) return false;
  const d = new Date(isoDateStr);
  if (Number.isNaN(d.getTime())) return false;
  const now = new Date();
  let age = now.getFullYear() - d.getFullYear();
  const m = now.getMonth() - d.getMonth();
  if (m < 0 || (m === 0 && now.getDate() < d.getDate())) age -= 1;
  return age >= 18;
}

export function phoneMax13(value: unknown): boolean {
  if (value == null) return true;
  const s = String(value);
  return s.length <= 13;
}

export function nonNegativeNumber(n: unknown): boolean {
  const x = Number(n);
  return !Number.isNaN(x) && x >= 0;
}

export function validatePhone(value: string): string {
  if (!value || !value.trim()) return 'Телефон обовʼязковий';
  if (value.length > 13) return 'Телефон не довший за 13 символів';
  if (!/^\+?[0-9]{1,12}$/.test(value))
    return 'Дозволені лише цифри та опційний + на початку';
  return '';
}

export function generateCheckNumber(len = 10): string {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
  let s = '';
  for (let i = 0; i < len; i += 1) {
    s += chars[Math.floor(Math.random() * chars.length)];
  }
  return s;
}
