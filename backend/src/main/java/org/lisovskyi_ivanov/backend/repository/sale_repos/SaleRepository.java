package org.lisovskyi_ivanov.backend.repository.sale_repos;

import org.lisovskyi_ivanov.backend.entity.Sale;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SaleRepository {

    List<Sale> findAll();

    Optional<Sale> findById(String upc, String checkNumber);

    Sale save(Sale sale);

    int update(Sale sale);

    int deleteById(String upc, String checkNumber);

    int delete(Sale sale);

    boolean existsById(String upc, String checkNumber);

    List<Sale> findAllByCheckNumber(String checkNumber);

    List<Sale> findAllByUpc(String upc);

    List<Sale> findAllByCustSurname(String custSurname);

    List<Sale> findAllByEmployeeId(String employeeId);

    List<Sale> findSalesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Sale> findSalesByProductNumberGreaterThan(Integer amount);

    BigDecimal calculateTotalSumByCheckNumber(String checkNumber);

    Integer countTotalProductsSoldByUpc(String upc);

    int deleteAllByCheckNumber(String checkNumber);
}