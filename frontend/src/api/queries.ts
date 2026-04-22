import { api } from './index';

export interface CategorySale {
  categoryName: string;
  totalAmount: number;
  totalSum: number;
}

export interface ProductSoldByAll {
  idProduct: number;
  productName: string;
}

export interface CustomerCategoryPurchases {
  cardNumber: string;
  custSurname: string;
  custName: string;
  totalItems: number;
  totalSpent: number;
}

export interface CategoryBoughtByAll {
  categoryNumber: number;
  categoryName: string;
}

export const queriesApi = {
  getCategorySales: async (startDate: string, endDate: string) => {
    const response = await api.get<CategorySale[]>('/complex-queries/category-sales', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  getProductsSoldByAllCashiers: async () => {
    const response = await api.get<ProductSoldByAll[]>('/complex-queries/products-sold-by-all');
    return response.data;
  },

  getCustomerPurchasesByCategory: async (categoryId: number, startDate: string, endDate: string) => {
    const response = await api.get<CustomerCategoryPurchases[]>('/complex-queries/yura/customer-category-purchases', {
      params: { categoryId, startDate, endDate },
    });
    return response.data;
  },

  getCategoriesBoughtByAllCustomers: async () => {
    const response = await api.get<CategoryBoughtByAll[]>('/complex-queries/yura/categories-bought-by-all');
    return response.data;
  },
};
