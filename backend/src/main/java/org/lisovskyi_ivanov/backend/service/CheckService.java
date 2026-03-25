package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.repository.check_repos.CheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final CheckRepository repository;


    @Transactional(readOnly = true)
    public List<Check> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Check> findByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.findByCheckNumber(checkNumber);
    }

    @Transactional(readOnly = true)
    public List<Check> findAllByEmployeeId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Employee id must not be null");
        }
        return repository.findByEmployeeId(id);
    }

    @Transactional(readOnly = true)
    public List<Check> findByCustomerCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number must not be null or blank");
        }
        return repository.findByCustomerCardNumber(cardNumber);
    }

    @Transactional(readOnly = true)
    public List<Check> findByPrintDate(LocalDateTime date) {
        return repository.findByPrintDate(date);
    }

    @Transactional(readOnly = true)
    public List<Check> findBySumTotalGreaterThan(BigDecimal sumTotal) {
        return repository.findBySumTotalGreaterThan(sumTotal);
    }

    @Transactional(readOnly = true)
    public List<Check> findByVatGreaterThan(BigDecimal vat) {
        return repository.findByVatGreaterThan(vat);
    }

    @Transactional(readOnly = true)
    public boolean existsByCheckNumber(String checkNumber) {
        if (checkNumber == null || checkNumber.isBlank()) {
            throw new IllegalArgumentException("Check number must not be null or blank");
        }
        return repository.existsByCheckNumber(checkNumber);
    }


    @Transactional
    public Check save(Check check) {
        if (check == null) {
            throw new IllegalArgumentException("Check must not be null");
        }
        if (check.getEmployee() == null || check.getEmployee().getIdEmployee() == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        if (check.getSumTotal() == null || check.getSumTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sum total must not be null or negative");
        }
        if (check.getCheckNumber() == null || check.getCheckNumber().isBlank()) {
            String checkNumber = generateUniqueString(10);
            check.setCheckNumber(checkNumber);
        }
        if (check.getPrintDate() == null) {
            check.setPrintDate(LocalDateTime.now());
        }
        return repository.save(check);
    }

    @Transactional
    public int update(Check check) {
        if (check == null) {
            throw new IllegalArgumentException("Check must not be null");
        }
        return repository.update(check);
    }

    @Transactional
    public int delete(Check check) {
        return repository.delete(check);
    }

    @Transactional
    public int deleteByCheckNumber(String checkNumber) {
        return repository.deleteByCheckNumber(checkNumber);
    }

}
