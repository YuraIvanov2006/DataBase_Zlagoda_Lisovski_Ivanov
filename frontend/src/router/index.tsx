import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Layout } from '../components/Layout';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { LoginPage } from '../pages/shared/LoginPage';
import { EmployeesPage } from '../pages/manager/EmployeesPage';
import { ManagerProductsPage } from '../pages/manager/ManagerProductsPage';
import { ManagerStoreItemsPage } from '../pages/manager/ManagerStoreItemsPage';
import { ManagerCategoriesPage } from '../pages/manager/ManagerCategoriesPage';
import { ManagerClientCardsPage } from '../pages/manager/ManagerClientCardsPage';
import { ManagerReceiptsPage } from '../pages/manager/ManagerReceiptsPage';
import { ManagerReportsPage } from '../pages/manager/ManagerReportsPage';
import { CashierProductsPage } from '../pages/cashier/CashierProductsPage';
import { CashierStoreItemsPage } from '../pages/cashier/CashierStoreItemsPage';
import { CashierClientCardsPage } from '../pages/cashier/CashierClientCardsPage';
import { CreateReceiptPage } from '../pages/cashier/CreateReceiptPage';
import { CashierReceiptsPage } from '../pages/cashier/CashierReceiptsPage';
import { CashierProfilePage } from '../pages/cashier/CashierProfilePage';

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route index element={<RoleRedirect />} />
          <Route
            path="manager/employees"
            element={
              <ProtectedRoute roles={['manager']}>
                <EmployeesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/products"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerProductsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/store-items"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerStoreItemsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/categories"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerCategoriesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/client-cards"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerClientCardsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/receipts"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerReceiptsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="manager/reports"
            element={
              <ProtectedRoute roles={['manager']}>
                <ManagerReportsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/products"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CashierProductsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/store-items"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CashierStoreItemsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/client-cards"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CashierClientCardsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/sale"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CreateReceiptPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/receipts"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CashierReceiptsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="cashier/profile"
            element={
              <ProtectedRoute roles={['cashier']}>
                <CashierProfilePage />
              </ProtectedRoute>
            }
          />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function RoleRedirect() {
  const { role } = useAuth();
  if (role === 'manager')
    return <Navigate to="/manager/employees" replace />;
  return <Navigate to="/cashier/products" replace />;
}
