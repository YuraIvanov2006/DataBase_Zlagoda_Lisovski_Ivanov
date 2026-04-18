import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { Spinner } from './Spinner.jsx';

export function ProtectedRoute({ children, roles }) {
  const { isAuthenticated, role, loading } = useAuth();
  const loc = useLocation();

  if (loading) return <Spinner />;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: loc }} />;
  }

  if (roles && roles.length && !roles.includes(role)) {
    const fallback =
      role === 'manager' ? '/manager/employees' : '/cashier/products';
    return <Navigate to={fallback} replace />;
  }

  return children;
}
