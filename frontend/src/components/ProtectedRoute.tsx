import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth, type UserRole } from '../context/AuthContext';
import { Spinner } from './Spinner';

type ProtectedRouteProps = {
  children: ReactNode;
  roles?: UserRole[];
};

export function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
  const { isAuthenticated, role, loading } = useAuth();
  const loc = useLocation();

  if (loading) return <Spinner />;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: loc }} />;
  }

  if (roles && roles.length && role && !roles.includes(role)) {
    const fallback =
      role === 'manager' ? '/manager/employees' : '/cashier/products';
    return <Navigate to={fallback} replace />;
  }

  return children;
}
