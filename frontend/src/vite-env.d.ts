/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly DEV: boolean;
  readonly MODE: string;
  readonly PROD: boolean;
  readonly SSR: boolean;
  readonly VITE_API_BASE?: string;
  readonly VITE_DEV_MANAGER_LOGIN?: string;
  readonly VITE_DEV_MANAGER_PASSWORD?: string;
  readonly VITE_DEV_MANAGER_ID?: string;
  readonly VITE_DEV_CASHIER_LOGIN?: string;
  readonly VITE_DEV_CASHIER_PASSWORD?: string;
  readonly VITE_DEV_CASHIER_ID?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
