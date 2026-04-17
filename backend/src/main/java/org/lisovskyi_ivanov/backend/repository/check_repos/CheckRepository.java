package org.lisovskyi_ivanov.backend.repository.check_repos;

import org.lisovskyi_ivanov.backend.entity.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckRepository {
    List<Check> findAll();
    Optional<Check> findByCheckNumber(String checkNumber);
    List<Check> findByEmployeeId(Long employeeId);
    List<Check> findByCustomerCardNumber(String cardNumber);
    List<Check> findByPrintDate(LocalDateTime date);
    List<Check> findBySumTotalGreaterThan(BigDecimal sumTotal);
    List<Check> findByVatGreaterThan(BigDecimal vat);
    boolean existsByCheckNumber(String checkNumber);
    Check save(Check check);
    int update(Check check);
    int delete(Check check);
    int deleteByCheckNumber(String checkNumber);
    List<Check> findByEmployeeIdAndPrintDateBetween(Long employeeId, LocalDateTime from, LocalDateTime to);
    List<Check> findByPrintDateBetween(LocalDateTime from, LocalDateTime to);
    BigDecimal calculateTotalSumByEmployeeAndPeriod(Long employeeId, LocalDateTime from, LocalDateTime to);
    BigDecimal calculateTotalSumByPeriod(LocalDateTime from, LocalDateTime to);
}
