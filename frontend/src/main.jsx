import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { AuthProvider } from './context/AuthContext.jsx';
import { AppRouter } from './router/index.jsx';
import './index.css';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  </StrictMode>
);
