package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.response.CategorySaleDto;
import org.lisovskyi_ivanov.backend.dto.response.ProductSoldByAllDto;
import org.lisovskyi_ivanov.backend.repository.complex_query_repos.ComplexQueryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplexQueryService {
    
    private final ComplexQueryRepository complexQueryRepository;

    public List<CategorySaleDto> getCategorySales(LocalDateTime startDate, LocalDateTime endDate) {
        return complexQueryRepository.getCategorySales(startDate, endDate);
    }

    public List<ProductSoldByAllDto> getProductsSoldByAllCashiers() {
        return complexQueryRepository.getProductsSoldByAllCashiers();
    }

    public List<org.lisovskyi_ivanov.backend.dto.response.CustomerCategoryPurchasesDto> getCustomerPurchasesByCategory(Long categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        return complexQueryRepository.getCustomerPurchasesByCategory(categoryId, startDate, endDate);
    }

    public List<org.lisovskyi_ivanov.backend.dto.response.CategoryBoughtByAllDto> getCategoriesBoughtByAllCustomers() {
        return complexQueryRepository.getCategoriesBoughtByAllCustomers();
    }
}
