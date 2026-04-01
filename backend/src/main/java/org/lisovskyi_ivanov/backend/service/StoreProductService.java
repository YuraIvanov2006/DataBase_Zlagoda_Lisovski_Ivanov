package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final StoreProductRepository repository;

    @Transactional(readOnly = true)
    public List<StoreProduct> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllPromotional() {
        return repository.findAllPromotional();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllNotPromotional() {
        return repository.findAllNotPromotional();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllByProductId(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }
        return repository.findAllByProductId(productId);
    }

    @Transactional(readOnly = true)
    public StoreProduct findByUPC(String upc) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        return repository.findByUPC(upc)
                .orElseThrow(() -> new NotFoundException(StoreProduct.class, "upc", upc));
    }

    @Transactional
    public StoreProduct save(StoreProduct storeProduct) {
        if (storeProduct == null) {
            throw new IllegalArgumentException("Store product must not be null");
        }
        if (storeProduct.getUpc() == null || storeProduct.getUpc().isBlank()) {
            String upc = generateUniqueString(12);
            storeProduct.setUpc(upc);
        }
        return repository.save(storeProduct);
    }

    @Transactional
    public StoreProduct update(StoreProduct storeProduct) {
        if (storeProduct == null) {
            throw new IllegalArgumentException("Store product must not be null");
        }
        if (storeProduct.getUpc() == null || storeProduct.getUpc().isBlank()) {
            throw new IllegalArgumentException("Store product UPC must not be null or blank for update");
        }
        int rows = repository.update(storeProduct);
        if (rows == 0) {
            throw new NotFoundException(StoreProduct.class, "upc", storeProduct.getUpc());
        }
        return storeProduct;
    }

    @Transactional
    public void deleteByUPC(String upc) {
        if (upc == null || upc.isBlank()) {
            throw new IllegalArgumentException("UPC must not be null or blank");
        }
        int rows = repository.deleteByUPC(upc);
        if (rows == 0) {
            throw new NotFoundException(StoreProduct.class, "upc", upc);
        }
    }

    @Transactional
    public void delete(StoreProduct storeProduct) {
        if (storeProduct == null) {
            throw new IllegalArgumentException("Store product must not be null");
        }
        int deleted = repository.delete(storeProduct);
        if (deleted == 0) {
            throw new NotFoundException(StoreProduct.class, "upc", storeProduct.getUpc());
        }
    }
}