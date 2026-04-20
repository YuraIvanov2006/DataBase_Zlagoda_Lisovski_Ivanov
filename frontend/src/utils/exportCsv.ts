export function downloadCsv(filename: string, rows: Record<string, unknown>[]) {
  if (!rows.length) return;
  const headers = Object.keys(rows[0]);
  const esc = (v: unknown) => {
    const s = v == null ? '' : String(v);
    if (/[",\n]/.test(s)) return `"${s.replace(/"/g, '""')}"`;
    return s;
  };
  const lines = [
    headers.join(','),
    ...rows.map((r) => headers.map((h) => esc(r[h])).join(',')),
  ];
  const blob = new Blob([lines.join('\n')], {
    type: 'text/csv;charset=utf-8;',
  });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

export function printHtml(title: string, innerHtml: string) {
  const w = window.open('', '_blank');
  if (!w) return;
  const safeTitle = title.replace(/[<>&"]/g, '');
  w.document.write(
    `<!DOCTYPE html><html><head><title>${safeTitle}</title><meta charset="utf-8"/>
    <style>
      body { font-family: Arial, sans-serif; margin: 24px; color: #111827; }
      .report-header, .report-footer { font-size: 12px; color: #6b7280; display: flex; justify-content: space-between; margin: 6px 0 14px; }
      .actions { margin-bottom: 12px; }
      .actions button { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 8px; background: #fff; cursor: pointer; }
      table { width: 100%; border-collapse: collapse; table-layout: fixed; }
      th, td { border: 1px solid #d1d5db; padding: 6px 8px; text-align: left; word-break: break-word; }
      th { background: #f3f4f6; }
      @media print {
        .actions { display: none; }
        @page { size: auto; margin: 12mm; }
      }
    </style>
    </head><body>
      <div class="actions"><button onclick="window.print()">Print</button></div>
      <div class="report-header"><span>ZLAGODA</span><span>${safeTitle}</span></div>
      ${innerHtml}
      <div class="report-footer"><span>Generated report preview</span><span></span></div>
    </body></html>`
  );
  w.document.close();
  w.focus();
}
