package org.lisovskyi_ivanov.backend.controller;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.response.CategorySaleDto;
import org.lisovskyi_ivanov.backend.dto.response.ProductSoldByAllDto;
import org.lisovskyi_ivanov.backend.service.ComplexQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/complex-queries")
@RequiredArgsConstructor
public class ComplexQueryController {

    private final ComplexQueryService complexQueryService;

    @GetMapping("/category-sales")
    public ResponseEntity<List<CategorySaleDto>> getCategorySales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<CategorySaleDto> result = complexQueryService.getCategorySales(
                startDate.atStartOfDay(), 
                endDate.atTime(LocalTime.MAX)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products-sold-by-all")
    public ResponseEntity<List<ProductSoldByAllDto>> getProductsSoldByAllCashiers() {
        List<ProductSoldByAllDto> result = complexQueryService.getProductsSoldByAllCashiers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/yura/customer-category-purchases")
    public ResponseEntity<List<org.lisovskyi_ivanov.backend.dto.response.CustomerCategoryPurchasesDto>> getCustomerPurchasesByCategory(
            @RequestParam Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<org.lisovskyi_ivanov.backend.dto.response.CustomerCategoryPurchasesDto> result = complexQueryService.getCustomerPurchasesByCategory(
                categoryId,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/yura/categories-bought-by-all")
    public ResponseEntity<List<org.lisovskyi_ivanov.backend.dto.response.CategoryBoughtByAllDto>> getCategoriesBoughtByAllCustomers() {
        List<org.lisovskyi_ivanov.backend.dto.response.CategoryBoughtByAllDto> result = complexQueryService.getCategoriesBoughtByAllCustomers();
        return ResponseEntity.ok(result);
    }
}
