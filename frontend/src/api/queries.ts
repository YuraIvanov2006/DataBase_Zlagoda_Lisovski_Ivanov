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
};
