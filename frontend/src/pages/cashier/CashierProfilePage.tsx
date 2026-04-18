import { useEffect, useState } from 'react';
import { employeesApi } from '../../api/employees';
import { getApiErrorMessage } from '../../api/index';
import { useAuth } from '../../context/AuthContext';
import { Spinner } from '../../components/Spinner';
import { formatDateInput, money } from '../../utils/formatters';

type EmployeeDto = {
  idEmployee: number;
  fullName: string;
  emplRole: string;
  salary: unknown;
  dateOfStart: string;
  emplPhoneNumber: string;
};

export function CashierProfilePage() {
  const { employeeId } = useAuth();
  const [emp, setEmp] = useState<EmployeeDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (employeeId == null) {
      setLoading(false);
      return;
    }
    let cancelled = false;
    (async () => {
      setLoading(true);
      setError('');
      try {
        const { data } = await employeesApi.getById(employeeId);
        if (!cancelled) setEmp(data as EmployeeDto);
      } catch (e: unknown) {
        if (!cancelled) setError(getApiErrorMessage(e));
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [employeeId]);

  if (employeeId == null)
    return <div className="alert error">Немає сесії</div>;
  if (loading) return <Spinner />;
  if (error) return <div className="alert error">{error}</div>;
  if (!emp) return null;

  return (
    <div>
      <h1>Мій профіль</h1>
      <div
        className="alert info stack"
        style={{ maxWidth: 480, lineHeight: 1.6 }}
      >
        <span>
          <strong>ID:</strong> {emp.idEmployee}
        </span>
        <span>
          <strong>ПІБ:</strong> {emp.fullName}
        </span>
        <span>
          <strong>Роль:</strong> {emp.emplRole}
        </span>
        <span>
          <strong>Зарплата:</strong> {money(emp.salary)}
        </span>
        <span>
          <strong>Початок роботи:</strong> {formatDateInput(emp.dateOfStart)}
        </span>
        <span>
          <strong>Телефон:</strong> {emp.emplPhoneNumber}
        </span>
      </div>
    </div>
  );
}
