package org.lisovskyi_ivanov.backend.repository.product_repos;

import org.lisovskyi_ivanov.backend.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    List<Product> findByCategoryNumber(Long categoryNumber);
    List<Product> findAllOrderByProductName();
    Optional<Product> findById(Long id);
    Optional<Product> findByName(String name);
    Product save(Product product);
    int update(Product product);
    boolean existsById(Long id);
    int deleteById(Long id);
    int delete(Product product);
}