package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.exception.InsufficientStockException;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.check_repos.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final CheckRepository repository;
    private final SaleService saleService;
    private final StoreProductService storeProductService;

    @Lazy
    @Autowired
    private CheckService self;

    @Transactional(readOnly = true)
    public List<Check> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Check findByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank())
            throw new IllegalArgumentException("Check number must not be null or blank");
        return repository.findByCheckNumber(checkNumber)
                .orElseThrow(() -> new NotFoundException(Check.class, "checkNumber", checkNumber));
    }

    @Transactional(readOnly = true)
    public List<Check> findAllByEmployeeId(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Employee id must not be null");
        return repository.findByEmployeeId(id);
    }

    @Transactional(readOnly = true)
    public List<Check> findByCustomerCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank())
            throw new IllegalArgumentException("Card number must not be null or blank");
        return repository.findByCustomerCardNumber(cardNumber);
    }

    @Transactional(readOnly = true)
    public List<Check> findByPrintDate(LocalDateTime date) {
        if (date == null)
            throw new IllegalArgumentException("Print date must not be null");
        return repository.findByPrintDate(date);
    }

    @Transactional(readOnly = true)
    public List<Check> findBySumTotalGreaterThan(BigDecimal sumTotal) {
        if (sumTotal == null || sumTotal.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Sum total must not be null or negative");
        return repository.findBySumTotalGreaterThan(sumTotal);
    }

    @Transactional(readOnly = true)
    public List<Check> findByVatGreaterThan(BigDecimal vat) {
        if (vat == null || vat.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("VAT must not be null or negative");
        return repository.findByVatGreaterThan(vat);
    }

    @Transactional(readOnly = true)
    public boolean existsByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank())
            throw new IllegalArgumentException("Check number must not be null or blank");
        return repository.existsByCheckNumber(checkNumber);
    }

    @Transactional
    public Check save(Check check, List<Sale> sales) {
        if (check == null)
            throw new IllegalArgumentException("Check must not be null");
        if (check.getEmployee() == null || check.getEmployee().getIdEmployee() == null)
            throw new IllegalArgumentException("Employee must not be null");
        if (sales == null || sales.isEmpty())
            throw new IllegalArgumentException("Sales must not be null or empty");

        BigDecimal sumTotal = BigDecimal.ZERO;

        for (Sale sale : sales) {
            // Отримуємо актуальні дані товару з БД
            StoreProduct sp = storeProductService.findByUPC(
                    sale.getStoreProduct().getUpc());

            // Перевірка залишків
            if (sp.getProductsNumber() < sale.getProductNumber()) {
                throw new InsufficientStockException(
                        "Недостатньо товару на складі: " + sp.getUpc() +
                                ". Наявно: " + sp.getProductsNumber() +
                                ", запитано: " + sale.getProductNumber());
            }

            // Списання залишків
            sp.setProductsNumber(sp.getProductsNumber() - sale.getProductNumber());
            storeProductService.update(sp);

            // Фіксація ціни на момент продажу (захист від майбутніх переоцінок)
            sale.setSellingPrice(sp.getSellingPrice());

            BigDecimal lineTotal = sp.getSellingPrice()
                    .multiply(BigDecimal.valueOf(sale.getProductNumber()));
            sumTotal = sumTotal.add(lineTotal);
        }

        // Знижка по карті клієнта
        if (check.getCustomerCard() != null && check.getCustomerCard().getPercent() != null) {
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    BigDecimal.valueOf(check.getCustomerCard().getPercent())
                            .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            sumTotal = sumTotal.multiply(discountFactor)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        check.setSumTotal(sumTotal.setScale(2, RoundingMode.HALF_UP));
        check.setVat(sumTotal.multiply(BigDecimal.valueOf(0.2))
                .setScale(2, RoundingMode.HALF_UP));

        if (check.getCheckNumber() == null || check.getCheckNumber().isBlank())
            check.setCheckNumber(generateUniqueString(10));
        if (check.getPrintDate() == null)
            check.setPrintDate(LocalDateTime.now());

        Check saved = repository.save(check);

        // Зберігаємо позиції з зафіксованою ціною та прив'язкою до чека
        for (Sale sale : sales) {
            sale.setCheck(saved);
            saleService.save(sale);
        }

        return saved;
    }

    @Transactional
    public void delete(Check check) {
        if (check == null)
            throw new IllegalArgumentException("Check must not be null");
        if (check.getCheckNumber() == null || check.getCheckNumber().isBlank())
            throw new IllegalArgumentException("Check number must not be null or blank");
        int rows = repository.delete(check);
        if (rows == 0)
            throw new NotFoundException(Check.class, "checkNumber", check.getCheckNumber());
    }

    @Transactional
    public void deleteByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank())
            throw new IllegalArgumentException("Check number must not be null or blank");
        int rows = repository.deleteByCheckNumber(checkNumber);
        if (rows == 0)
            throw new NotFoundException(Check.class, "checkNumber", checkNumber);
    }
}