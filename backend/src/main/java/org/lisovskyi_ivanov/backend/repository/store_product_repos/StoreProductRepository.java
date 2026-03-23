package org.lisovskyi_ivanov.backend.repository.store_product_repos;

import org.lisovskyi_ivanov.backend.entity.StoreProduct;

import java.util.List;
import java.util.Optional;

public interface StoreProductRepository {
    List<StoreProduct> findAll();
    List<StoreProduct> findAllPromotional();
    List<StoreProduct> findAllNotPromotional();
    List<StoreProduct> findAllByProductId(Long productId);
    Optional<StoreProduct> findByUPC(String upc);
    StoreProduct save(StoreProduct storeProduct);
    int update(StoreProduct storeProduct);
    int delete(StoreProduct storeProduct);
    int deleteByUPC(String upc);
}
