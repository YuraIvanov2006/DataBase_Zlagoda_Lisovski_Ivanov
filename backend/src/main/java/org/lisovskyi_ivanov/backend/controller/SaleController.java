package org.lisovskyi_ivanov.backend.controller;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.response.SaleDto;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.mapping.dto.SaleMapper;
import org.lisovskyi_ivanov.backend.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final SaleMapper saleMapper;

    @GetMapping
    public ResponseEntity<List<SaleDto>> getAllSales() {
        List<SaleDto> sales = saleService.findAll().stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{checkNumber}/{upc}")
    public ResponseEntity<SaleDto> getSaleById(@PathVariable String checkNumber, @PathVariable String upc) {
        Sale sale = saleService.findById(upc, checkNumber);
        return ResponseEntity.ok(saleMapper.toDto(sale));
    }

    @GetMapping("/check/{checkNumber}")
    public ResponseEntity<List<SaleDto>> getSalesByCheckNumber(@PathVariable String checkNumber) {
        List<SaleDto> sales = saleService.findAllByCheckNumber(checkNumber).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/upc/{upc}")
    public ResponseEntity<List<SaleDto>> getSalesByUpc(@PathVariable String upc) {
        List<SaleDto> sales = saleService.findAllByUpc(upc).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/customer-surname/{surname}")
    public ResponseEntity<List<SaleDto>> getSalesByCustomerSurname(@PathVariable String surname) {
        List<SaleDto> sales = saleService.findAllByCustSurname(surname).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SaleDto>> getSalesByEmployeeId(@PathVariable Long employeeId) {
        List<SaleDto> sales = saleService.findAllByEmployeeId(employeeId).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<SaleDto>> getSalesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        List<SaleDto> sales = saleService.findSalesByPriceRange(minPrice, maxPrice).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/quantity-greater-than/{amount}")
    public ResponseEntity<List<SaleDto>> getSalesByProductNumberGreaterThan(@PathVariable Integer amount) {
        List<SaleDto> sales = saleService.findSalesByProductNumberGreaterThan(amount).stream()
                .map(saleMapper::toDto)
                .toList();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/check/{checkNumber}/total-sum")
    public ResponseEntity<BigDecimal> calculateTotalSumByCheckNumber(@PathVariable String checkNumber) {
        return ResponseEntity.ok(saleService.calculateTotalSumByCheckNumber(checkNumber));
    }

    @GetMapping("/upc/{upc}/total-sold")
    public ResponseEntity<Integer> countTotalProductsSoldByUpc(@PathVariable String upc) {
        return ResponseEntity.ok(saleService.countTotalProductsSoldByUpc(upc));
    }

    @DeleteMapping("/{checkNumber}/{upc}")
    public ResponseEntity<Void> deleteSale(@PathVariable String checkNumber, @PathVariable String upc) {
        saleService.deleteById(upc, checkNumber);
        return ResponseEntity.noContent().build();
    }
}