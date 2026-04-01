package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.sale_repos.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository repository;

    @Transactional(readOnly = true)
    public List<Sale> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Sale findById(String upc, String checkNumber) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.findById(upc, checkNumber)
                .orElseThrow(() -> new NotFoundException(Sale.class,
                        List.of("upc", "checkNumber"),
                        List.of(upc, checkNumber))
                );
    }

    @Transactional
    public Sale save(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale must not be null");
        }
        return repository.save(sale);
    }

    @Transactional
    public Sale update(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale must not be null");
        }
        int rows = repository.update(sale);
        if (rows == 0) {
            throw new NotFoundException(Sale.class,
                    List.of("upc", "checkNumber"),
                    List.of(sale.getStoreProduct().getUpc(), sale.getCheck().getCheckNumber()));
        }
        return sale;
    }

    @Transactional
    public void deleteById(String upc, String checkNumber) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        int rows = repository.deleteById(upc, checkNumber);
        if (rows == 0) {
            throw new NotFoundException(Sale.class,
                    List.of("upc", "checkNumber"),
                    List.of(upc, checkNumber));
        }
    }

    @Transactional
    public void delete(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale must not be null");
        }
        int rows = repository.delete(sale);
        if (rows == 0) {
            throw new NotFoundException(Sale.class,
                    List.of("upc", "checkNumber"),
                    List.of(sale.getStoreProduct().getUpc(), sale.getCheck().getCheckNumber()));
        }
    }

    @Transactional(readOnly = true)
    public boolean existsById(String upc, String checkNumber) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.existsById(upc, checkNumber);
    }

    @Transactional(readOnly = true)
    public List<Sale> findAllByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.findAllByCheckNumber(checkNumber);
    }

    @Transactional(readOnly = true)
    public List<Sale> findAllByUpc(String upc) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        return repository.findAllByUpc(upc);
    }

    @Transactional(readOnly = true)
    public List<Sale> findAllByCustSurname(String custSurname) {
        if (custSurname == null || custSurname.isBlank()) {
            throw new IllegalArgumentException("Customer surname must not be null or blank");
        }
        return repository.findAllByCustSurname(custSurname);
    }

    @Transactional(readOnly = true)
    public List<Sale> findAllByEmployeeId(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID must not be null or blank");
        }
        return repository.findAllByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<Sale> findSalesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Prices must not be null");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price must not be greater than maximum price");
        }
        return repository.findSalesByPriceRange(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Sale> findSalesByProductNumberGreaterThan(Integer amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must not be null or negative");
        }
        return repository.findSalesByProductNumberGreaterThan(amount);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSumByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.calculateTotalSumByCheckNumber(checkNumber);
    }

    @Transactional(readOnly = true)
    public Integer countTotalProductsSoldByUpc(String upc) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        return repository.countTotalProductsSoldByUpc(upc);
    }

    @Transactional
    public void deleteAllByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        repository.deleteAllByCheckNumber(checkNumber);
    }
}