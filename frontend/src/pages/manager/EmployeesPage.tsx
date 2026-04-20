import { useCallback, useEffect, useMemo, useState } from "react";
import { employeesApi } from "../../api/employees";
import { register } from "../../api/auth";
import { getApiErrorMessage } from "../../api/index";
import {
  DataTable,
  type DataColumn,
  type TableSortState,
} from "../../components/DataTable";
import { Modal } from "../../components/Modal";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import { SearchBar } from "../../components/SearchBar";
import { Spinner } from "../../components/Spinner";
import { formatDateInput, money, parseFullName } from "../../utils/formatters";
import { sortRows, type SortValueType } from "../../utils/sort";
import { isAdultBirthDate, validatePhone } from "../../utils/validators";

type EmployeeRow = {
  idEmployee: number;
  fullName: string;
  emplRole: string;
  salary: unknown;
  dateOfBirth?: string;
  dateOfStart: string;
  emplPhoneNumber: string;
  emplCity?: string;
  emplStreet?: string;
  emplZipCode?: string;
};

const emptyForm = {
  emplSurname: "",
  emplName: "",
  emplPatronymic: "",
  emplRole: "CASHIER",
  salary: "",
  dateOfBirth: "",
  dateOfStart: "",
  emplPhoneNumber: "",
  emplCity: "",
  emplStreet: "",
  emplZipCode: "",
  accountLogin: "",
  accountPassword: "",
};

type EmployeeForm = typeof emptyForm;

export function EmployeesPage() {
  const [rows, setRows] = useState<EmployeeRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cashiersOnly, setCashiersOnly] = useState(false);
  const [searchSurname, setSearchSurname] = useState("");
  const [searchHit, setSearchHit] = useState<EmployeeRow | null>(null);
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<EmployeeForm>(emptyForm);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [deleteTarget, setDeleteTarget] = useState<EmployeeRow | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);
  const [sortState, setSortState] = useState<TableSortState>({
    key: "fullName",
    dir: "asc",
    type: "string",
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = cashiersOnly
        ? await employeesApi.getCashiers()
        : await employeesApi.getOrderedBySurname();
      setRows((res.data as EmployeeRow[]) || []);
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, [cashiersOnly]);

  useEffect(() => {
    load();
  }, [load]);

  const sortedRows = useMemo(() => {
    return sortRows(
      rows,
      sortState.key,
      sortState.dir,
      (sortState.type || "string") as SortValueType,
    );
  }, [rows, sortState]);

  const onSort = (key: string, type?: SortValueType) => {
    setSortState((s) => ({
      key,
      type: type || "string",
      dir: s.key === key && s.dir === "asc" ? "desc" : "asc",
    }));
  };

  const validate = (f: EmployeeForm) => {
    const err: Record<string, string> = {};
    if (!f.emplSurname?.trim()) err.emplSurname = "Обов'язково";
    if (!f.emplName?.trim()) err.emplName = "Обов'язково";
    if (!f.dateOfBirth) err.dateOfBirth = "Обов'язково";
    else if (!isAdultBirthDate(f.dateOfBirth))
      err.dateOfBirth = "Вік має бути не менше 18 років";
    if (!f.dateOfStart) err.dateOfStart = "Обов'язково";
    const pe = validatePhone(f.emplPhoneNumber);
    if (pe) err.emplPhoneNumber = pe;
    if (!f.emplCity?.trim()) err.emplCity = "Обов'язково";
    if (!f.emplStreet?.trim()) err.emplStreet = "Обов'язково";
    if (!f.emplZipCode?.trim()) err.emplZipCode = "Обов'язково";
    if (f.emplZipCode?.length && f.emplZipCode.length > 9)
      err.emplZipCode = "До 9 символів";
    const sal = Number(f.salary);
    if (Number.isNaN(sal) || sal < 0) err.salary = "≥ 0";
    if (modal === "create") {
      if (!f.accountLogin?.trim()) err.accountLogin = "Обов'язково";
      if (!f.accountPassword || f.accountPassword.length < 6)
        err.accountPassword = "Мінімум 6 символів";
    }
    return err;
  };

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setFormErrors({});
    setModal("create");
  };

  const openEdit = async (row: EmployeeRow) => {
    setEditingId(row.idEmployee);
    setFormErrors({});
    setModal("edit");
    setSaving(true);
    try {
      const { data } = await employeesApi.getById(row.idEmployee);
      const d = data as {
        fullName?: string;
        emplRole?: string;
        salary?: unknown;
        dateOfBirth?: string;
        dateOfStart?: string;
        emplPhoneNumber?: string;
        emplCity?: string;
        emplStreet?: string;
        emplZipCode?: string;
      };
      const { surname, firstName, patronymic } = parseFullName(
        d.fullName || "",
      );
      setForm({
        emplSurname: surname,
        emplName: firstName,
        emplPatronymic: patronymic,
        emplRole: (d.emplRole || "CASHIER").toUpperCase().includes("MANAG")
          ? "MANAGER"
          : "CASHIER",
        salary: d.salary ?? "",
        dateOfBirth: formatDateInput(d.dateOfBirth || ""),
        dateOfStart: formatDateInput(d.dateOfStart || ""),
        emplPhoneNumber: d.emplPhoneNumber || "",
        emplCity: d.emplCity || "",
        emplStreet: d.emplStreet || "",
        emplZipCode: d.emplZipCode || "",
        accountLogin: "",
        accountPassword: "",
      });
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
      setModal(null);
    } finally {
      setSaving(false);
    }
  };

  const submit = async () => {
    const err = validate(form);
    setFormErrors(err);
    if (Object.keys(err).length) return;
    setSaving(true);
    setError("");
    const body = {
      emplSurname: form.emplSurname.trim(),
      emplName: form.emplName.trim(),
      emplPatronymic: form.emplPatronymic?.trim() || null,
      emplRole: form.emplRole,
      salary: Number(form.salary),
      dateOfBirth: form.dateOfBirth,
      dateOfStart: form.dateOfStart,
      emplPhoneNumber: form.emplPhoneNumber.trim(),
      emplCity: form.emplCity.trim(),
      emplStreet: form.emplStreet.trim(),
      emplZipCode: form.emplZipCode.trim(),
    };
    try {
      if (modal === "create") {
        const { data } = await employeesApi.create(body);
        const createdId = (data as EmployeeRow).idEmployee;
        if (createdId) {
          await register({
            idEmployee: createdId,
            login: form.accountLogin.trim(),
            password: form.accountPassword,
          });
        }
      } else if (modal === "edit") {
        if (!editingId) throw new Error("Не вдалося визначити ID");
        await employeesApi.update(editingId, body);
      }
      setModal(null);
      setEditingId(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const doDelete = async () => {
    if (!deleteTarget) return;
    setSaving(true);
    try {
      await employeesApi.delete(deleteTarget.idEmployee);
      setDeleteTarget(null);
      await load();
    } catch (e: unknown) {
      setError(getApiErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const runSearch = async () => {
    if (!searchSurname.trim()) return;
    setError("");
    try {
      const { data } = await employeesApi.searchBySurname(searchSurname.trim());
      setSearchHit((data as EmployeeRow) || null);
    } catch (e: unknown) {
      setSearchHit(null);
      setError(getApiErrorMessage(e));
    }
  };

  const columns: DataColumn<EmployeeRow>[] = [
    {
      key: "fullName",
      label: "ПІБ",
      sortable: true,
      render: (r) => r.fullName,
    },
    {
      key: "emplRole",
      label: "Роль",
      sortable: true,
      render: (r) => r.emplRole,
    },
    {
      key: "salary",
      label: "Зарплата",
      sortable: true,
      sortType: "number",
      render: (r) => money(r.salary),
    },
    {
      key: "dateOfStart",
      label: "Початок",
      sortable: true,
      render: (r) => formatDateInput(r.dateOfStart),
    },
    {
      key: "emplPhoneNumber",
      label: "Телефон",
      sortable: true,
      render: (r) => r.emplPhoneNumber,
    },
    {
      key: "_actions",
      label: "",
      render: (r) => (
        <div className="stack">
          <button
            type="button"
            className="btn secondary small"
            onClick={() => openEdit(r)}
          >
            Змінити
          </button>
          <button
            type="button"
            className="btn danger small"
            onClick={() => setDeleteTarget(r)}
          >
            Видалити
          </button>
        </div>
      ),
    },
  ];

  if (loading && !rows.length) return <Spinner />;

  return (
    <div>
      <div className="page-head">
        <h1>Працівники</h1>
        <button type="button" className="btn primary" onClick={openCreate}>
          Додати
        </button>
      </div>
      {error && <div className="alert error">{error}</div>}
      <div className="toolbar">
        <label
          className="stack"
          style={{ fontSize: "0.85rem", color: "var(--muted)" }}
        >
          <input
            type="checkbox"
            checked={cashiersOnly}
            onChange={(e) => setCashiersOnly(e.target.checked)}
          />
          Лише касири
        </label>
        <SearchBar
          value={searchSurname}
          onChange={setSearchSurname}
          placeholder="Пошук за прізвищем"
          onSubmit={runSearch}
        />
      </div>
      {searchHit && (
        <div className="alert info stack">
          <strong>Результат пошуку</strong>
          <span>ПІБ: {searchHit.fullName}</span>
          <span>Телефон: {searchHit.emplPhoneNumber}</span>
          <span>
            Адреса:{" "}
            {[searchHit.emplCity, searchHit.emplStreet, searchHit.emplZipCode]
              .filter(Boolean)
              .join(", ") || "—"}
          </span>
        </div>
      )}
      <DataTable
        columns={columns}
        rows={sortedRows}
        rowKey={(r) => r.idEmployee}
        sortState={sortState}
        onSort={onSort}
      />
      {saving && <Spinner label="Збереження…" />}
      {modal && (
        <Modal
          title={modal === "create" ? "Новий працівник" : "Редагування"}
          onClose={() => {
            if (!saving) {
              setModal(null);
              setEditingId(null);
            }
          }}
          wide
        >
          {modal === "edit" && (
            <p className="alert info" style={{ marginTop: 0 }}>
              API повертає не всі поля профілю. Заповніть дату народження та
              адресу перед збереженням.
            </p>
          )}
          <div className="form-grid">
            <label>
              Прізвище
              <input
                value={form.emplSurname}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplSurname: e.target.value }))
                }
              />
              {formErrors.emplSurname && (
                <span className="field-error">{formErrors.emplSurname}</span>
              )}
            </label>
            <label>
              Імʼя
              <input
                value={form.emplName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplName: e.target.value }))
                }
              />
              {formErrors.emplName && (
                <span className="field-error">{formErrors.emplName}</span>
              )}
            </label>
            <label>
              По батькові
              <input
                value={form.emplPatronymic}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplPatronymic: e.target.value }))
                }
              />
            </label>
            <label>
              Роль
              <select
                value={form.emplRole}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplRole: e.target.value }))
                }
              >
                <option value="CASHIER">Касир</option>
                <option value="MANAGER">Менеджер</option>
              </select>
            </label>
            <label>
              Зарплата
              <input
                type="number"
                step="0.01"
                min="0"
                value={form.salary}
                onChange={(e) =>
                  setForm((f) => ({ ...f, salary: e.target.value }))
                }
              />
              {formErrors.salary && (
                <span className="field-error">{formErrors.salary}</span>
              )}
            </label>
            <label>
              Дата народження
              <input
                type="date"
                value={form.dateOfBirth}
                onChange={(e) =>
                  setForm((f) => ({ ...f, dateOfBirth: e.target.value }))
                }
              />
              {formErrors.dateOfBirth && (
                <span className="field-error">{formErrors.dateOfBirth}</span>
              )}
            </label>
            <label>
              Початок роботи
              <input
                type="date"
                value={form.dateOfStart}
                onChange={(e) =>
                  setForm((f) => ({ ...f, dateOfStart: e.target.value }))
                }
              />
              {formErrors.dateOfStart && (
                <span className="field-error">{formErrors.dateOfStart}</span>
              )}
            </label>
            <label>
              Телефон
              <input
                value={form.emplPhoneNumber}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplPhoneNumber: e.target.value }))
                }
              />
              {formErrors.emplPhoneNumber && (
                <span className="field-error">
                  {formErrors.emplPhoneNumber}
                </span>
              )}
            </label>
            <label>
              Місто
              <input
                value={form.emplCity}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplCity: e.target.value }))
                }
              />
              {formErrors.emplCity && (
                <span className="field-error">{formErrors.emplCity}</span>
              )}
            </label>
            <label>
              Вулиця
              <input
                value={form.emplStreet}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplStreet: e.target.value }))
                }
              />
              {formErrors.emplStreet && (
                <span className="field-error">{formErrors.emplStreet}</span>
              )}
            </label>
            <label>
              Індекс
              <input
                value={form.emplZipCode}
                onChange={(e) =>
                  setForm((f) => ({ ...f, emplZipCode: e.target.value }))
                }
              />
              {formErrors.emplZipCode && (
                <span className="field-error">{formErrors.emplZipCode}</span>
              )}
            </label>
            {modal === "create" && (
              <>
                <label>
                  Логін акаунта
                  <input
                    value={form.accountLogin}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, accountLogin: e.target.value }))
                    }
                  />
                  {formErrors.accountLogin && (
                    <span className="field-error">
                      {formErrors.accountLogin}
                    </span>
                  )}
                </label>
                <label>
                  Пароль акаунта
                  <input
                    type="password"
                    value={form.accountPassword}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        accountPassword: e.target.value,
                      }))
                    }
                  />
                  {formErrors.accountPassword && (
                    <span className="field-error">
                      {formErrors.accountPassword}
                    </span>
                  )}
                </label>
              </>
            )}
          </div>
          <div className="form-actions">
            <button
              type="button"
              className="btn secondary"
              onClick={() => setModal(null)}
              disabled={saving}
            >
              Скасувати
            </button>
            <button
              type="button"
              className="btn primary"
              onClick={submit}
              disabled={saving}
            >
              Зберегти
            </button>
          </div>
        </Modal>
      )}
      {deleteTarget && (
        <ConfirmDialog
          message={`Видалити працівника ${deleteTarget.fullName}?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={doDelete}
          danger
        />
      )}
    </div>
  );
}
