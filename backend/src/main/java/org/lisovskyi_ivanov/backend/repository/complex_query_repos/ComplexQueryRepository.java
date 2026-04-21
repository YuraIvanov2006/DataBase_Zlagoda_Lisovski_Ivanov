package org.lisovskyi_ivanov.backend.repository.complex_query_repos;

import org.lisovskyi_ivanov.backend.dto.response.CategorySaleDto;
import org.lisovskyi_ivanov.backend.dto.response.ProductSoldByAllDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplexQueryRepository {
    List<CategorySaleDto> getCategorySales(LocalDateTime startDate, LocalDateTime endDate);
    List<ProductSoldByAllDto> getProductsSoldByAllCashiers();
}
